package rip.alpha.blocklogging.storage.mongo.state;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.storage.model.KnownName;
import rip.alpha.blocklogging.storage.mongo.codec.MongoFields;
import rip.alpha.blocklogging.storage.mongo.model.MongoActors;
import rip.alpha.blocklogging.storage.mongo.model.MongoQuery;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public final class MongoUsers {

    private final Map<String, KnownName> names = new ConcurrentHashMap<>();

    private final MongoCollection<Document> users;

    public boolean observe(Actor actor, long time) {

        if (!actor.isPlayer())
            return false;

        String uuid = actor.uuid().toString();

        KnownName known = this.names.computeIfAbsent(uuid, this::load);

        if (known.name().equals(actor.name()) || time < known.time())
            return false;

        String lower = actor.name().toLowerCase(Locale.ROOT);
        Document update = new Document("$set", new Document(MongoFields.NAME, actor.name()).append(MongoFields.NAME_TIME, time))
                .append("$addToSet", new Document(MongoFields.NAMES, lower));

        this.users.updateOne(Filters.eq(MongoFields.ID, uuid), update, new UpdateOptions().upsert(true));
        this.names.put(uuid, new KnownName(actor.name(), time));

        return true;
    }

    public MongoActors resolve(Collection<String> names, boolean required) {
        Set<String> uuids = new HashSet<>();
        Set<String> system = new HashSet<>();

        for (String name : names) {
            String lower = name.toLowerCase(Locale.ROOT);

            if (lower.startsWith(Actor.SYSTEM_PREFIX)) {
                system.add(lower);
                continue;
            }

            int before = uuids.size();

            this.users.find(Filters.eq(MongoFields.NAMES, lower)).forEach(document -> uuids.add(document.getString(MongoFields.ID)));

            if (required && uuids.size() == before)
                throw new QueryException("No logs found for \"%s\".".formatted(name));
        }

        return new MongoActors(Set.copyOf(uuids), Set.copyOf(system));
    }

    public MongoQuery query(Query query) {
        MongoActors included = query.users().isEmpty() ? null : resolve(query.users(), true);

        return new MongoQuery(query, included, resolve(query.excludedUsers(), false));
    }

    public void clear() {
        this.names.clear();
    }

    private KnownName load(String uuid) {
        Document document = this.users.find(Filters.eq(MongoFields.ID, uuid)).first();

        if (document == null)
            return new KnownName("", Long.MIN_VALUE);

        Long time = document.getLong(MongoFields.NAME_TIME);

        return new KnownName(document.getString(MongoFields.NAME), time == null ? 0L : time);
    }
}
