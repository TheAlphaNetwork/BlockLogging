package rip.alpha.blocklogging.rollback.model;

import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

public record RunKey(String owner, RollbackKind kind, Query query) {

    public static RunKey of(String owner, Operation operation) {
        return new RunKey(owner, operation.kind(), operation.query());
    }
}
