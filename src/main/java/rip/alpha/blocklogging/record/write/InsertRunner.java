package rip.alpha.blocklogging.record.write;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.model.LogRecord;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@UtilityClass
class InsertRunner {

    private final List<String> BASE = List.of("time", "user", "world", "x", "y", "z");

    <T extends LogRecord> int run(WriteContext context, RecordInsert<T> insert, List<T> records) throws SQLException, IOException {

        if (records.isEmpty())
            return 0;

        int[] users = new int[records.size()];
        int[] worlds = new int[records.size()];

        for (int index = 0; index < records.size(); index++) {
            LogBase base = records.get(index).base();

            users[index] = context.user(base.actor(), base.time());
            worlds[index] = context.world(base.position().world());
        }

        try (PreparedStatement statement = context.getConnection().prepareStatement(sql(context, insert))) {

            for (int index = 0; index < records.size(); index++) {
                T record = records.get(index);

                LogBase base = record.base();

                statement.setLong(1, base.time());
                statement.setInt(2, users[index]);
                statement.setInt(3, worlds[index]);
                statement.setInt(4, base.position().x());
                statement.setInt(5, base.position().y());
                statement.setInt(6, base.position().z());

                insert.bind(statement, BASE.size() + 1, record);
                statement.addBatch();
            }

            statement.executeBatch();
        }

        return records.size();
    }

    private String sql(WriteContext context, RecordInsert<?> insert) {
        List<String> columns = new ArrayList<>(BASE);

        columns.addAll(insert.columns());

        String marks = String.join(", ", Collections.nCopies(columns.size(), "?"));

        return "INSERT INTO %s (%s) VALUES (%s)".formatted(context.getStorage().table(insert.table()), String.join(", ", columns), marks);
    }
}
