package rip.alpha.blocklogging.storage.sql.read;

import rip.alpha.blocklogging.query.filter.TableFilters;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.blocklogging.query.model.ResolvedQuery;
import rip.alpha.blocklogging.query.model.TableFilter;
import rip.alpha.blocklogging.storage.sql.SqlStore;
import rip.alpha.blocklogging.storage.sql.schema.Tables;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class LookupSql {

    private static final String UNION = " UNION ALL ";
    private static final String MEMBER = "SELECT * FROM (%s ORDER BY time DESC, id DESC LIMIT %d) m%d";
    private static final String JOINED = "SELECT r.*, u.name AS user_name, w.name AS world_name FROM (%s) r JOIN %s u ON u.id = r.user ";
    private static final String ORDERED = "LEFT JOIN %s w ON w.id = r.world ORDER BY r.time DESC, r.id DESC LIMIT %d";

    private final Connection connection;

    private final SqlStore storage;

    private final List<TableFilter> filters;
    private final List<SqlWhere> wheres;

    public LookupSql(Connection connection, SqlStore storage, ResolvedQuery resolved) {
        this.connection = connection;
        this.storage = storage;
        this.filters = TableFilters.of(resolved.query().actions());
        this.wheres = this.filters.stream().map(filter -> FilterSql.where(resolved, filter)).toList();
    }

    public LookupPage page(int page, int size) throws SQLException {

        if (this.filters.isEmpty())
            return LookupPage.builder().rows(List.of()).total(0).page(page).pageSize(size).build();

        int total = count();

        List<LookupRow> rows = total == 0 ? List.of() : rows(page * size);

        int from = Math.min(rows.size(), (page - 1) * size);

        return LookupPage.builder().rows(rows.subList(from, rows.size())).total(total).page(page).pageSize(size).build();
    }

    private int count() throws SQLException {
        List<String> parts = new ArrayList<>();

        for (int index = 0; index < this.filters.size(); index++)
            parts.add("SELECT id FROM %s%s".formatted(this.storage.table(this.filters.get(index).table()), this.wheres.get(index).sql()));

        String sql = "SELECT COUNT(*) FROM (%s) c".formatted(String.join(UNION, parts));

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            bind(statement);

            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt(1) : 0;
            }
        }
    }

    private List<LookupRow> rows(int limit) throws SQLException {
        List<LookupRow> rows = new ArrayList<>();

        try (PreparedStatement statement = this.connection.prepareStatement(select(limit))) {
            bind(statement);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next())
                    rows.add(LookupRows.read(result));
            }
        }

        return rows;
    }

    private String select(int limit) {
        List<String> parts = new ArrayList<>();

        for (int index = 0; index < this.filters.size(); index++) {
            LogTable table = this.filters.get(index).table();

            String inner = Projections.select(table, this.storage.table(table)) + this.wheres.get(index).sql();

            parts.add(MEMBER.formatted(inner, limit, index));
        }

        String joined = JOINED.formatted(String.join(UNION, parts), this.storage.table(Tables.USER));

        return joined + ORDERED.formatted(this.storage.table(Tables.WORLD), limit);
    }

    private void bind(PreparedStatement statement) throws SQLException {
        int index = 1;

        for (SqlWhere where : this.wheres)
            index = where.bind(statement, index);
    }
}
