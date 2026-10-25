package rip.alpha.blocklogging.storage.state;

import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.storage.StorageService;
import rip.alpha.blocklogging.storage.model.KnownName;
import rip.alpha.blocklogging.storage.model.UserRow;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class UserIds {

    private final Map<String, Integer> ids = new ConcurrentHashMap<>();
    private final Map<Integer, KnownName> names = new ConcurrentHashMap<>();

    private final UserRows rows;

    public UserIds(StorageService storage) {
        this.rows = new UserRows(storage);
    }

    public int resolve(Connection connection, Actor actor, long time) throws SQLException {
        String key = actor.isPlayer() ? String.valueOf(actor.uuid()) : actor.name();

        Integer cached = this.ids.get(key);

        if (cached != null) {
            rename(connection, cached, actor, time);
            return cached;
        }

        UserRow row = this.rows.find(connection, actor);

        long nameTime = row == null ? time : 0L;

        if (row == null) {
            this.rows.insert(connection, actor);

            row = Objects.requireNonNull(this.rows.find(connection, actor));

            if (actor.isPlayer())
                this.rows.recordName(connection, row.id(), actor.name(), time);
        }

        this.ids.put(key, row.id());
        this.names.put(row.id(), new KnownName(row.name(), nameTime));

        rename(connection, row.id(), actor, time);
        return row.id();
    }

    public void clear() {
        this.ids.clear();
        this.names.clear();
    }

    private void rename(Connection connection, int id, Actor actor, long time) throws SQLException {

        KnownName known = this.names.get(id);

        if (!actor.isPlayer() || known != null && (known.name().equals(actor.name()) || time < known.time()))
            return;

        this.rows.rename(connection, id, actor.name());
        this.rows.recordName(connection, id, actor.name(), time);
        this.names.put(id, new KnownName(actor.name(), time));
    }
}
