package rip.alpha.blocklogging.admin.menu;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.write.WriterStats;
import rip.alpha.blocklogging.rollback.RollbackService;
import rip.alpha.libraries.item.ItemBuilder;
import rip.alpha.libraries.menu.editor.Lore;

@UtilityClass
class StatusIcon {

    ItemStack of(RecordService records, RollbackService rollbacks) {
        WriterStats stats = records.stats();

        return ItemBuilder.of(Material.REDSTONE_COMPARATOR)
                .name(Chat.PRIMARY + "Status")
                .lore(
                        Lore.value("Queued", String.valueOf(records.queued())),
                        Lore.value("Written", String.valueOf(stats.written())),
                        Lore.value("Last batch", stats.lastBatchMillis() + "ms"),
                        Lore.value("Failed / dropped", stats.failedRows() + " / " + stats.droppedRows()),
                        Lore.value("Consumer", records.isPaused() ? "paused" : "running"),
                        Lore.value("Running rollbacks", String.valueOf(rollbacks.active()))
                )
                .build();
    }
}
