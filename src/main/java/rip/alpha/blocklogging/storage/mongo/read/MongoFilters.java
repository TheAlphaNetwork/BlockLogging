package rip.alpha.blocklogging.storage.mongo.read;

import com.mongodb.client.model.Filters;
import lombok.experimental.UtilityClass;
import org.bson.conversions.Bson;
import rip.alpha.blocklogging.query.model.AreaRadius;
import rip.alpha.blocklogging.query.model.CuboidRadius;
import rip.alpha.blocklogging.query.model.ExactRadius;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.Radius;
import rip.alpha.blocklogging.query.model.TableFilter;
import rip.alpha.blocklogging.storage.mongo.codec.MongoFields;
import rip.alpha.blocklogging.storage.mongo.model.MongoActors;
import rip.alpha.blocklogging.storage.mongo.model.MongoQuery;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@UtilityClass
public class MongoFilters {

    private final Set<LogTable> ACTION_TABLES = Set.of(LogTable.BLOCK, LogTable.CONTAINER, LogTable.ITEM, LogTable.SESSION);

    public Bson of(MongoQuery resolved, TableFilter filter) {
        Query query = resolved.query();

        List<Bson> filters = new ArrayList<>();

        if (query.since() > 0)
            filters.add(Filters.gte(MongoFields.TIME, query.since()));

        if (query.until() > 0)
            filters.add(Filters.lte(MongoFields.TIME, query.until()));

        if (resolved.users() != null)
            filters.add(actors(resolved.users()));

        if (!resolved.excluded().isEmpty())
            filters.add(Filters.nor(actors(resolved.excluded())));

        radius(filters, query.radius());

        if (filter.table().isTyped()) {
            in(filters, MongoFields.TYPE, query.include().forTable(filter.table()));
            nin(filters, MongoFields.TYPE, query.exclude().forTable(filter.table()));
        }

        if (ACTION_TABLES.contains(filter.table()))
            in(filters, MongoFields.ACTION, filter.actions());

        return filters.isEmpty() ? Filters.empty() : Filters.and(filters);
    }

    private Bson actors(MongoActors actors) {
        return Filters.or(Filters.in(MongoFields.UUID, actors.uuids()), Filters.in(MongoFields.USER_LOWER, actors.systemNames()));
    }

    private void radius(List<Bson> filters, Radius radius) {

        if (radius.world() != null)
            filters.add(Filters.eq(MongoFields.WORLD, radius.world()));

        switch (radius) {
            case AreaRadius area -> {
                between(filters, MongoFields.X, area.x() - area.size(), area.x() + area.size());
                between(filters, MongoFields.Z, area.z() - area.size(), area.z() + area.size());
            }

            case CuboidRadius cuboid -> {
                between(filters, MongoFields.X, cuboid.minX(), cuboid.maxX());
                between(filters, MongoFields.Y, cuboid.minY(), cuboid.maxY());
                between(filters, MongoFields.Z, cuboid.minZ(), cuboid.maxZ());
            }

            case ExactRadius exact -> {
                filters.add(Filters.eq(MongoFields.X, exact.position().x()));
                filters.add(Filters.eq(MongoFields.Y, exact.position().y()));
                filters.add(Filters.eq(MongoFields.Z, exact.position().z()));
            }

            default -> {
            }
        }
    }

    private void between(List<Bson> filters, String field, int min, int max) {
        filters.add(Filters.gte(field, min));
        filters.add(Filters.lte(field, max));
    }

    private void in(List<Bson> filters, String field, Set<Integer> values) {

        if (!values.isEmpty())
            filters.add(Filters.in(field, values));
    }

    private void nin(List<Bson> filters, String field, Set<Integer> values) {

        if (!values.isEmpty())
            filters.add(Filters.nin(field, values));
    }
}
