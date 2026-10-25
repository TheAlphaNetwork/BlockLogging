package rip.alpha.blocklogging.storage.mongo.read;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import lombok.experimental.UtilityClass;
import org.bson.Document;
import org.bson.types.Binary;
import rip.alpha.blocklogging.query.codec.QueryReader;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.model.OperationRecord;
import rip.alpha.blocklogging.rollback.type.OperationState;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.blocklogging.storage.mongo.codec.MongoFields;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class MongoOperations {

    public List<OperationRecord> read(MongoCollection<Document> operations) {
        List<OperationRecord> records = new ArrayList<>();

        for (Document document : operations.find().sort(Sorts.ascending(MongoFields.TIME))) {
            Query query = document.get(MongoFields.QUERY) instanceof Binary binary ? QueryReader.decode(binary.getData()) : null;

            if (query != null)
                records.add(record(document, query));
        }

        return records;
    }

    private OperationRecord record(Document document, Query query) {
        RollbackKind kind = RollbackKind.values()[document.getInteger(MongoFields.KIND)];

        return OperationRecord.builder()
                .operation(new Operation(document.getString(MongoFields.ID), query, kind))
                .sender(document.getString(MongoFields.SENDER))
                .state(OperationState.of(document.getInteger(MongoFields.STATE)))
                .time(document.getLong(MongoFields.TIME))
                .build();
    }
}
