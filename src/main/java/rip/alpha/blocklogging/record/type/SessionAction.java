package rip.alpha.blocklogging.record.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SessionAction {

    QUIT(0),
    JOIN(1);

    private final int id;
}
