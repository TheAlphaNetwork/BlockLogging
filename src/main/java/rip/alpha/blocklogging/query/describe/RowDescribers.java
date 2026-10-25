package rip.alpha.blocklogging.query.describe;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public class RowDescribers {

    private final Map<LogTable, RowDescriber> DESCRIBERS = new EnumMap<>(Map.of(
            LogTable.BLOCK, new BlockDescriber(),
            LogTable.CONTAINER, new AmountDescriber("removed", "added"),
            LogTable.ITEM, new AmountDescriber("dropped", "picked up"),
            LogTable.KILL, new KillDescriber(),
            LogTable.SIGN, new TextDescriber("wrote"),
            LogTable.CHAT, new TextDescriber("said"),
            LogTable.COMMAND, new TextDescriber("ran"),
            LogTable.SESSION, new SessionDescriber(),
            LogTable.USERNAME, new TextDescriber("used the name")
    ));

    public RowDescriber of(LogTable table) {
        return DESCRIBERS.get(table);
    }
}
