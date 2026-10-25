package rip.alpha.blocklogging.storage.mongo.model;

import java.util.Set;

public record MongoActors(Set<String> uuids, Set<String> systemNames) {

    public boolean isEmpty() {
        return this.uuids.isEmpty() && this.systemNames.isEmpty();
    }
}
