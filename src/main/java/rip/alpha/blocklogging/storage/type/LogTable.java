package rip.alpha.blocklogging.storage.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogTable {

    BLOCK("block", true, true),
    CONTAINER("container", true, true),
    ITEM("item", true, true),
    SIGN("sign", false, false),
    KILL("entity_kill", true, true),
    CHAT("chat", false, false),
    COMMAND("command", false, false),
    SESSION("session", false, false),
    USERNAME("username", false, false);

    private final String base;

    private final boolean typed;
    private final boolean reversible;
}
