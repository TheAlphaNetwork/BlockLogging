package rip.alpha.blocklogging.record.capture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.model.ContainerRecord;
import rip.alpha.blocklogging.record.model.ItemDelta;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.state.ContainerViews;
import rip.alpha.blocklogging.record.state.SharedContainer;
import rip.alpha.blocklogging.record.type.ContainerAction;

import java.util.UUID;

@RequiredArgsConstructor
public final class ContainerTracker {

    private final Plugin plugin;

    private final RecordService records;

    @Getter
    private final ContainerViews views = new ContainerViews();

    public void touched(SharedContainer container, @Nullable Actor actor) {
        settle(container);

        container.setDirty(true);
        container.setChangedBy(actor);

        if (container.isScheduled())
            return;

        container.setScheduled(true);
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {

            container.setScheduled(false);
            settle(container);
        });
    }

    public void close(UUID player) {
        SharedContainer container = this.views.viewed(player);

        if (container != null)
            settle(container);

        this.views.close(player);
    }

    public void log(Actor actor, BlockPosition position, ItemStack item, int change) {
        ContainerAction action = change > 0 ? ContainerAction.ADD : ContainerAction.REMOVE;

        this.records.submit(ContainerRecord.builder()
                .base(LogBase.now(actor, position))
                .item(item)
                .amount(Math.abs(change))
                .action(action)
                .build());
    }

    private void settle(SharedContainer container) {

        if (!container.isDirty())
            return;

        ItemStack[] now = ItemCopies.copy(container.getInventory().getContents());

        Actor actor = container.getChangedBy();

        if (actor != null) {

            for (ItemDelta delta : ItemDiff.between(container.getSnapshot(), now))
                log(actor, container.getPosition(), delta.item(), delta.change());
        }

        container.setSnapshot(now);
        container.setDirty(false);
        container.setChangedBy(null);
    }
}
