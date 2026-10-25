package rip.alpha.blocklogging.storage.mongo.write;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import rip.alpha.blocklogging.record.model.PurgeRequest;
import rip.alpha.blocklogging.record.model.PurgeResult;
import rip.alpha.blocklogging.storage.mongo.codec.MongoFields;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.function.Function;

@RequiredArgsConstructor
public final class MongoPurge {

    private final Function<LogTable, MongoCollection<Document>> collections;

    public PurgeResult purge(PurgeRequest request) {
        long started = System.currentTimeMillis();

        Bson older = Filters.lt(MongoFields.TIME, request.before());
        Bson filter = request.world() == null ? older : Filters.and(older, Filters.eq(MongoFields.WORLD, request.world()));

        long rows = 0;

        for (LogTable table : LogTable.values())
            rows += this.collections.apply(table).deleteMany(filter).getDeletedCount();

        return new PurgeResult((int) Math.min(Integer.MAX_VALUE, rows), System.currentTimeMillis() - started, false);
    }
}
