package rip.alpha.blocklogging.storage.mongo.read;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import rip.alpha.blocklogging.query.filter.RollbackFilters;
import rip.alpha.blocklogging.query.model.TableFilter;
import rip.alpha.blocklogging.rollback.model.History;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.blocklogging.storage.mongo.codec.MongoFields;
import rip.alpha.blocklogging.storage.mongo.codec.RowDocuments;
import rip.alpha.blocklogging.storage.mongo.model.MongoQuery;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public final class MongoHistory {

    private static final Bson OLDEST = Sorts.ascending(MongoFields.TIME, MongoFields.ID);

    private final Function<LogTable, MongoCollection<Document>> collections;

    public History load(MongoQuery query, RollbackKind kind) {
        Map<LogTable, TableFilter> filters = RollbackFilters.of(query.query().actions());

        return History.builder()
                .blocks(rows(query, kind, filters.get(LogTable.BLOCK), RowDocuments::block))
                .containers(rows(query, kind, filters.get(LogTable.CONTAINER), RowDocuments::container))
                .kills(rows(query, kind, filters.get(LogTable.KILL), RowDocuments::kill))
                .items(rows(query, kind, filters.get(LogTable.ITEM), RowDocuments::item))
                .build();
    }

    private <T> List<T> rows(MongoQuery query, RollbackKind kind, TableFilter filter, Function<Document, T> mapper) {

        if (filter == null)
            return List.of();

        Bson where = Filters.and(MongoFilters.of(query, filter), Filters.eq(MongoFields.ROLLED_BACK, kind.matchedFlag()));

        List<T> rows = new ArrayList<>();

        this.collections.apply(filter.table()).find(where).sort(OLDEST).forEach(document -> rows.add(mapper.apply(document)));

        return rows;
    }
}
