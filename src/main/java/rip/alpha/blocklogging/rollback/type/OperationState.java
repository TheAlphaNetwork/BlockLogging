package rip.alpha.blocklogging.rollback.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationState {

    RUNNING(0),
    DONE(1);

    private final int id;

    public static OperationState of(int id) {
        return id == RUNNING.id ? RUNNING : DONE;
    }
}
