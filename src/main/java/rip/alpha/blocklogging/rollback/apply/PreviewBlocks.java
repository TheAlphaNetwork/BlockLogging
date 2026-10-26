package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import rip.alpha.aspigot.block.BlockPositions;
import rip.alpha.aspigot.packet.Packets;
import rip.alpha.aspigot.packet.play.clientbound.world.block.BlockChangePacket;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.rollback.model.BlockTarget;

import java.util.List;

@UtilityClass
@SuppressWarnings("deprecation")
public class PreviewBlocks {

    private final int DATA_MASK = 15;

    public void show(Player player, List<BlockTarget> targets) {
        String world = player.getWorld().getName();

        for (BlockTarget target : targets) {
            Material material = Material.getMaterial(target.type());

            if (material != null && material.isBlock() && target.position().world().equals(world))
                send(player, target.position(), target.type(), target.data());
        }
    }

    public void revert(Player player, List<BlockPosition> positions) {

        for (BlockPosition position : positions) {
            Block block = position.block();

            if (block != null && block.getWorld().equals(player.getWorld()))
                send(player, position, block.getTypeId(), block.getData());
        }
    }

    private void send(Player player, BlockPosition position, int type, int data) {
        BlockChangePacket packet = BlockChangePacket.create()
                .position(BlockPositions.of(position.x(), position.y(), position.z()))
                .block(new MaterialData(type, (byte) (data & DATA_MASK)));

        Packets.send(player, packet);
    }
}
