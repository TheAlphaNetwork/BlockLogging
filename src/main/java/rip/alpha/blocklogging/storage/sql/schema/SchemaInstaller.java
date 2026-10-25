package rip.alpha.blocklogging.storage.sql.schema;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.storage.sql.SqlStore;
import rip.alpha.blocklogging.storage.sql.model.TableSpec;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@UtilityClass
public class SchemaInstaller {

    public void install(SqlStore store, String prefix) throws SQLException {

        try (Connection connection = store.connect(); Statement statement = connection.createStatement()) {

            for (TableSpec spec : Tables.all()) {

                for (String sql : store.getDialect().createTable(spec, prefix))
                    statement.execute(sql);
            }
        }
    }
}
