package rip.alpha.blocklogging.query.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class SqlWhere {

    private final List<String> conditions = new ArrayList<>();
    private final List<Object> params = new ArrayList<>();

    public SqlWhere add(String condition, Object... values) {
        this.conditions.add(condition);
        this.params.addAll(List.of(values));

        return this;
    }

    public SqlWhere in(String column, Collection<?> values, boolean negate) {

        if (values.isEmpty())
            return this;

        String marks = String.join(", ", Collections.nCopies(values.size(), "?"));

        this.conditions.add("%s %sIN (%s)".formatted(column, negate ? "NOT " : "", marks));
        this.params.addAll(values);

        return this;
    }

    public String sql() {
        return this.conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", this.conditions);
    }

    public int bind(PreparedStatement statement, int index) throws SQLException {
        int cursor = index;

        for (Object param : this.params)
            statement.setObject(cursor++, param);

        return cursor;
    }
}
