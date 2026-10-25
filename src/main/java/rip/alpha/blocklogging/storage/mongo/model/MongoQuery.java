package rip.alpha.blocklogging.storage.mongo.model;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.query.model.Query;

public record MongoQuery(Query query, @Nullable MongoActors users, MongoActors excluded) {
}
