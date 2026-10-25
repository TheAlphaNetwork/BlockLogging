package rip.alpha.blocklogging.rollback.model;

import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

import java.util.UUID;

public record Operation(String id, Query query, RollbackKind kind) {

    public static Operation of(Query query, RollbackKind kind) {
        return new Operation(UUID.randomUUID().toString(), query, kind);
    }

    public Operation inverse() {
        return of(this.query, this.kind.inverse());
    }
}
