package rip.alpha.blocklogging.storage.mongo.schema;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import lombok.experimental.UtilityClass;
import org.bson.Document;
import rip.alpha.blocklogging.storage.mongo.codec.MongoFields;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.function.Function;

@UtilityClass
public class MongoIndexes {

    public void create(Function<LogTable, MongoCollection<Document>> collections, MongoCollection<Document> users) {

        for (LogTable table : LogTable.values()) {
            MongoCollection<Document> collection = collections.apply(table);

            collection.createIndex(Indexes.compoundIndex(
                    Indexes.ascending(MongoFields.WORLD, MongoFields.X, MongoFields.Z),
                    Indexes.descending(MongoFields.TIME)
            ));

            collection.createIndex(Indexes.compoundIndex(Indexes.ascending(MongoFields.UUID), Indexes.descending(MongoFields.TIME)));
            collection.createIndex(Indexes.compoundIndex(Indexes.ascending(MongoFields.USER_LOWER), Indexes.descending(MongoFields.TIME)));
            collection.createIndex(Indexes.descending(MongoFields.TIME));
        }

        users.createIndex(Indexes.ascending(MongoFields.NAMES));
    }
}
