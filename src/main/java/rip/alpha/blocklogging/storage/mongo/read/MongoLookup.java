package rip.alpha.blocklogging.storage.mongo.read;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import rip.alpha.blocklogging.query.filter.TableFilters;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.blocklogging.query.model.PageRequest;
import rip.alpha.blocklogging.query.model.TableFilter;
import rip.alpha.blocklogging.storage.mongo.codec.MongoFields;
import rip.alpha.blocklogging.storage.mongo.codec.RowDocuments;
import rip.alpha.blocklogging.storage.mongo.model.MongoQuery;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public final class MongoLookup {

    private static final Bson NEWEST = Sorts.descending(MongoFields.TIME, MongoFields.ID);

    private final Function<LogTable, MongoCollection<Document>> collections;

    public LookupPage page(MongoQuery query, PageRequest request) {
        int limit = request.page() * request.size();

        long total = 0;

        List<LookupRow> rows = new ArrayList<>();

        for (TableFilter filter : TableFilters.of(query.query().actions())) {
            MongoCollection<Document> collection = this.collections.apply(filter.table());

            Bson where = MongoFilters.of(query, filter);

            total += collection.countDocuments(where);
            collection.find(where).sort(NEWEST).limit(limit).forEach(document -> rows.add(RowDocuments.row(filter.table(), document)));
        }

        rows.sort(Comparator.comparingLong(LookupRow::time).reversed());

        int from = Math.min(rows.size(), (request.page() - 1) * request.size());
        int to = Math.min(rows.size(), limit);

        List<LookupRow> page = rows.subList(from, to);

        return LookupPage.builder()
                .rows(List.copyOf(page))
                .total((int) Math.min(Integer.MAX_VALUE, total))
                .page(request.page())
                .pageSize(request.size())
                .build();
    }
}
