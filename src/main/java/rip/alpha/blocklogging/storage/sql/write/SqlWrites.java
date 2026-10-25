package rip.alpha.blocklogging.storage.sql.write;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.record.model.PendingWrite;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@UtilityClass
public class SqlWrites {

    public int write(WriterSession session, List<PendingWrite> writes) throws SQLException, IOException {
        WriteContext context = session.context();

        Connection connection = context.getConnection();

        WriteBatch batch = new WriteBatch(writes);

        connection.setAutoCommit(false);

        int rows = batch.insert(context);

        RolledBackWriter.apply(context, batch.getUpdates());
        RespawnWriter.apply(context, batch.getRespawns());
        OperationWriter.apply(context, batch.getOperations());

        connection.commit();
        connection.setAutoCommit(true);

        return rows;
    }
}
