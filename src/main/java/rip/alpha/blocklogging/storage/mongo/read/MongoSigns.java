package rip.alpha.blocklogging.storage.mongo.read;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import lombok.experimental.UtilityClass;
import org.bson.Document;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.storage.mongo.codec.MongoFields;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class MongoSigns {

    public Map<BlockPosition, String> latest(MongoCollection<Document> signs, Collection<BlockPosition> positions) {
        Map<BlockPosition, String> texts = new HashMap<>();

        for (BlockPosition position : positions) {
            Document document = signs.find(Filters.and(
                    Filters.eq(MongoFields.WORLD, position.world()),
                    Filters.eq(MongoFields.X, position.x()),
                    Filters.eq(MongoFields.Y, position.y()),
                    Filters.eq(MongoFields.Z, position.z())
            )).sort(Sorts.descending(MongoFields.TIME, MongoFields.ID)).first();

            if (document != null)
                texts.put(position, document.getString(MongoFields.TEXT));
        }

        return texts;
    }
}
