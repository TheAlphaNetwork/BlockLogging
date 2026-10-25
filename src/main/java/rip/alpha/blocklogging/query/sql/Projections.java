package rip.alpha.blocklogging.query.sql;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.storage.type.LogTable;

@UtilityClass
class Projections {

    private final String COLUMNS = "SELECT %d AS log, id, time, user, world, x, y, z, ";
    private final String VALUES = "%s AS type, %s AS data, %s AS amount, %s AS action, %s AS rolled_back, %s AS text FROM %s";
    private final String TEMPLATE = COLUMNS + VALUES;

    String select(LogTable table, String name) {
        return switch (table) {
            case BLOCK -> TEMPLATE.formatted(table.ordinal(), "type", "data", "1", "action", "rolled_back", "NULL", name);
            case CONTAINER -> TEMPLATE.formatted(table.ordinal(), "type", "data", "amount", "action", "rolled_back", "NULL", name);
            case ITEM -> TEMPLATE.formatted(table.ordinal(), "type", "data", "amount", "action", "0", "NULL", name);
            case KILL -> TEMPLATE.formatted(table.ordinal(), "type", "0", "1", "0", "rolled_back", "NULL", name);
            case SESSION -> TEMPLATE.formatted(table.ordinal(), "0", "0", "1", "action", "0", "NULL", name);
            case SIGN, CHAT, COMMAND, USERNAME -> TEMPLATE.formatted(table.ordinal(), "0", "0", "1", "0", "0", "text", name);
        };
    }
}
