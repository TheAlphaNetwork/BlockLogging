package rip.alpha.blocklogging.query.filter;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.model.Ranks;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.List;

@UtilityClass
public class StaffCommands {

    private final String HIDDEN = "[hidden]";

    public LookupPage redact(CommandSender viewer, LookupPage page, Ranks ranks) {

        if (viewer.isOp() || !(viewer instanceof Player player))
            return page;

        int weight = ranks.weight(player.getUniqueId());

        List<LookupRow> rows = page.rows().stream().map(row -> hidden(row, player, weight, ranks) ? hide(row) : row).toList();

        return page.toBuilder().rows(rows).build();
    }

    private LookupRow hide(LookupRow row) {
        return row.toBuilder().text(HIDDEN).build();
    }

    private boolean hidden(LookupRow row, Player viewer, int weight, Ranks ranks) {

        if (row.table() != LogTable.COMMAND || row.uuid() == null || row.uuid().equals(viewer.getUniqueId()))
            return false;

        return ranks.weight(row.uuid()) >= weight;
    }
}
