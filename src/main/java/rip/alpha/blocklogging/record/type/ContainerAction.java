package rip.alpha.blocklogging.record.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContainerAction {

    REMOVE(0),
    ADD(1);

    private final int id;
}
