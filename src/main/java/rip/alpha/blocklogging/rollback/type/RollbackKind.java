package rip.alpha.blocklogging.rollback.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RollbackKind {

    ROLLBACK("Rollback", "rolled back", false),
    RESTORE("Restore", "restored", true);

    private final String title;
    private final String verb;

    private final boolean source;

    public RollbackKind inverse() {
        return this == ROLLBACK ? RESTORE : ROLLBACK;
    }

    public int matchedFlag() {
        return this.source ? 1 : 0;
    }
}
