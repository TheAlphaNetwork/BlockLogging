package rip.alpha.blocklogging.storage.sql.write;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.storage.sql.SqlStore;
import rip.alpha.blocklogging.storage.sql.state.UserIds;
import rip.alpha.blocklogging.storage.sql.state.WorldIds;

import java.sql.Connection;
import java.sql.SQLException;

@Getter
@RequiredArgsConstructor
final class WriteContext {

    private final Connection connection;

    private final SqlStore storage;

    private final UserIds users;

    private final WorldIds worlds;

    int user(Actor actor, long time) throws SQLException {
        return this.users.resolve(this.connection, actor, time);
    }

    int world(String name) throws SQLException {
        return this.worlds.resolve(this.connection, name);
    }
}
