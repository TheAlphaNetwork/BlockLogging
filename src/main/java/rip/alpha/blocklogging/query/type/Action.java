package rip.alpha.blocklogging.query.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum Action {

    BLOCK("block", LogTable.BLOCK, Set.of(0, 1)),
    PLACE("+block", LogTable.BLOCK, Set.of(1)),
    BREAK("-block", LogTable.BLOCK, Set.of(0)),
    CLICK("click", LogTable.BLOCK, Set.of(2)),
    CONTAINER("container", LogTable.CONTAINER, Set.of()),
    DEPOSIT("+container", LogTable.CONTAINER, Set.of(1)),
    WITHDRAW("-container", LogTable.CONTAINER, Set.of(0)),
    ITEM("item", LogTable.ITEM, Set.of()),
    PICKUP("+item", LogTable.ITEM, Set.of(1)),
    DROP("-item", LogTable.ITEM, Set.of(0)),
    KILL("kill", LogTable.KILL, Set.of()),
    SIGN("sign", LogTable.SIGN, Set.of()),
    CHAT("chat", LogTable.CHAT, Set.of()),
    COMMAND("command", LogTable.COMMAND, Set.of()),
    SESSION("session", LogTable.SESSION, Set.of()),
    LOGIN("+session", LogTable.SESSION, Set.of(1)),
    LOGOUT("-session", LogTable.SESSION, Set.of(0)),
    USERNAME("username", LogTable.USERNAME, Set.of());

    public static final Set<Action> LOOKUP_DEFAULTS = Set.of(BLOCK, CONTAINER, KILL);
    public static final Set<Action> PRIVATE = Set.of(CHAT, COMMAND);

    private final String token;

    private final LogTable table;

    private final Set<Integer> values;

    @Nullable
    public static Action of(String token) {

        for (Action action : values()) {

            if (action.token.equalsIgnoreCase(token))
                return action;
        }

        return null;
    }
}
