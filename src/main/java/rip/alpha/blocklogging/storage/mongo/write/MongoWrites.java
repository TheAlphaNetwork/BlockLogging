package rip.alpha.blocklogging.storage.mongo.write;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Updates;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import rip.alpha.blocklogging.query.codec.QueryCodec;
import rip.alpha.blocklogging.record.model.LogRecord;
import rip.alpha.blocklogging.record.model.OperationWrite;
import rip.alpha.blocklogging.record.model.PendingWrite;
import rip.alpha.blocklogging.record.model.PurgeRequest;
import rip.alpha.blocklogging.record.model.RespawnUpdate;
import rip.alpha.blocklogging.record.model.RolledBackUpdate;
import rip.alpha.blocklogging.rollback.model.OperationRecord;
import rip.alpha.blocklogging.storage.mongo.codec.LogDocuments;
import rip.alpha.blocklogging.storage.mongo.codec.MongoFields;
import rip.alpha.blocklogging.storage.mongo.state.MongoUsers;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public final class MongoWrites {

    private final Function<LogTable, MongoCollection<Document>> collections;

    private final MongoCollection<Document> operations;

    private final MongoUsers users;

    public int write(List<PendingWrite> writes) throws IOException {
        Map<LogTable, List<Document>> documents = new EnumMap<>(LogTable.class);

        for (PendingWrite write : writes)
            add(documents, write);

        int rows = 0;

        for (Map.Entry<LogTable, List<Document>> entry : documents.entrySet()) {
            this.collections.apply(entry.getKey()).insertMany(entry.getValue());

            rows += entry.getValue().size();
        }

        return rows;
    }

    private void add(Map<LogTable, List<Document>> documents, PendingWrite write) throws IOException {

        switch (write) {
            case LogRecord record -> {
                long time = record.base().time();

                if (this.users.observe(record.base().actor(), time))
                    bucket(documents, LogTable.USERNAME).add(LogDocuments.username(record.base().actor(), time));

                bucket(documents, LogTables.of(record)).add(LogDocuments.of(record));
            }

            case RolledBackUpdate update -> flag(update);
            case RespawnUpdate update -> respawn(update);
            case OperationWrite operation -> operation(operation.record());

            case PurgeRequest _ -> {
            }
        }
    }

    private void flag(RolledBackUpdate update) {
        List<ObjectId> ids = update.ids().stream().map(ObjectId::new).toList();

        Bson value = Updates.set(MongoFields.ROLLED_BACK, update.rolledBack() ? 1 : 0);

        if (!ids.isEmpty())
            this.collections.apply(update.table()).updateMany(Filters.in(MongoFields.ID, ids), value);
    }

    private void respawn(RespawnUpdate update) {
        String entity = update.entity() == null ? null : update.entity().toString();

        Bson filter = Filters.eq(MongoFields.ID, new ObjectId(update.id()));

        this.collections.apply(LogTable.KILL).updateOne(filter, Updates.set(MongoFields.RESPAWNED, entity));
    }

    private void operation(OperationRecord record) throws IOException {
        Document document = new Document(MongoFields.ID, record.operation().id())
                .append(MongoFields.SENDER, record.sender())
                .append(MongoFields.KIND, record.operation().kind().ordinal())
                .append(MongoFields.STATE, record.state().getId())
                .append(MongoFields.TIME, record.time())
                .append(MongoFields.QUERY, new Binary(QueryCodec.encode(record.operation().query())));

        this.operations.replaceOne(Filters.eq(MongoFields.ID, record.operation().id()), document, new ReplaceOptions().upsert(true));
    }

    private List<Document> bucket(Map<LogTable, List<Document>> documents, LogTable table) {
        return documents.computeIfAbsent(table, _ -> new ArrayList<>());
    }
}
