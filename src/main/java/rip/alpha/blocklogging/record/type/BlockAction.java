package rip.alpha.blocklogging.record.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BlockAction {

    BREAK(0),
    PLACE(1),
    CLICK(2);

    private final int id;
}
