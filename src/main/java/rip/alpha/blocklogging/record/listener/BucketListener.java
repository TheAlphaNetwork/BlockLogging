package rip.alpha.blocklogging.record.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.capture.BlockCapture;
import rip.alpha.blocklogging.record.state.PositionOwners;
import rip.alpha.blocklogging.record.type.BlockAction;

@RequiredArgsConstructor
public final class BucketListener implements Listener {

    private final RecordService records;

    private final PositionOwners owners;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @SuppressWarnings("deprecation")
    public void onEmpty(PlayerBucketEmptyEvent event) {

        if (!this.records.logging().isBuckets())
            return;

        Actor actor = Actor.player(event.getPlayer());

        Block target = event.getBlockClicked().getRelative(event.getBlockFace());

        BlockPosition position = BlockPosition.of(target);

        Material liquid = event.getBucket() == Material.LAVA_BUCKET ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER;

        if (target.getType() != Material.AIR && !target.isLiquid())
            this.records.submit(BlockCapture.broke(actor, target));

        this.records.submit(BlockCapture.raw(actor, position, liquid.getId(), BlockAction.PLACE));
        this.owners.put(position, actor);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFill(PlayerBucketFillEvent event) {

        if (!this.records.logging().isBuckets())
            return;

        Block clicked = event.getBlockClicked();
        Block liquid = clicked.isLiquid() ? clicked : clicked.getRelative(event.getBlockFace());

        if (!liquid.isLiquid())
            return;

        this.records.submit(BlockCapture.broke(Actor.player(event.getPlayer()), liquid));
        this.owners.remove(BlockPosition.of(liquid));
    }
}
