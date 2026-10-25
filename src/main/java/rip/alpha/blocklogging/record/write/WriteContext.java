package rip.alpha.blocklogging.record.write;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.storage.StorageService;
import rip.alpha.blocklogging.storage.state.UserIds;
import rip.alpha.blocklogging.storage.state.WorldIds;

import java.sql.Connection;
import java.sql.SQLException;

@Getter
@RequiredArgsConstructor
final class WriteContext {

    private final Connection connection;

    private final StorageService storage;

    private final UserIds users;

    private final WorldIds worlds;

    int user(Actor actor, long time) throws SQLException {
        return this.users.resolve(this.connection, actor, time);
    }

    int world(String name) throws SQLException {
        return this.worlds.resolve(this.connection, name);
    }
}
