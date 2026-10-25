package rip.alpha.blocklogging.rollback.model;

import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

public record Operation(Query query, RollbackKind kind) {
}
