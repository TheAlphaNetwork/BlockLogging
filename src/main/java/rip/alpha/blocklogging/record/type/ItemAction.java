package rip.alpha.blocklogging.record.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemAction {

    DROP(0),
    PICKUP(1);

    private final int id;
}
