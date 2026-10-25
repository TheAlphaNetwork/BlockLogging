package rip.alpha.blocklogging.storage.sql.read;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.ResolvedQuery;
import rip.alpha.blocklogging.storage.sql.SqlStore;
import rip.alpha.blocklogging.storage.sql.state.WorldIds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class QueryResolver {

    private final int MISSING_WORLD = -1;

    public ResolvedQuery resolve(Connection connection, SqlStore storage, Query query) throws SQLException {
        Set<Integer> users = query.users().isEmpty() ? null : new HashSet<>();
        Set<Integer> excluded = new HashSet<>();

        for (String name : query.users()) {
            Set<Integer> ids = UserLookup.ids(connection, storage, name);

            if (ids.isEmpty())
                throw new QueryException("No logs found for \"%s\".".formatted(name));

            users.addAll(ids);
        }

        for (String name : query.excludedUsers())
            excluded.addAll(UserLookup.ids(connection, storage, name));

        Integer world = null;

        if (query.radius().world() != null) {
            Integer found = WorldIds.find(connection, storage, query.radius().world());

            world = found == null ? MISSING_WORLD : found;
        }

        return ResolvedQuery.builder()
                .query(query)
                .users(users)
                .excludedUsers(excluded)
                .world(world)
                .build();
    }
}
