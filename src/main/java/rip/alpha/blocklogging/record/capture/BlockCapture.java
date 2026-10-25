package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.model.BlockRecord;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.type.BlockAction;

@UtilityClass
@SuppressWarnings("deprecation")
public class BlockCapture {

    public BlockRecord broke(Actor actor, BlockState state) {
        LogBase base = LogBase.now(actor, BlockPosition.of(state.getBlock()));

        return BlockRecord.builder()
                .base(base)
                .type(state.getTypeId())
                .data(state.getRawData())
                .action(BlockAction.BREAK)
                .meta(BlockMetas.of(state))
                .build();
    }

    public BlockRecord broke(Actor actor, Block block) {
        return broke(actor, block.getState());
    }

    public BlockRecord placed(Actor actor, BlockState state) {
        LogBase base = LogBase.now(actor, BlockPosition.of(state.getBlock()));

        return BlockRecord.builder()
                .base(base)
                .type(state.getTypeId())
                .data(state.getRawData())
                .action(BlockAction.PLACE)
                .meta(null)
                .build();
    }

    public BlockRecord placed(Actor actor, Block block) {
        return placed(actor, block.getState());
    }

    public BlockRecord clicked(Actor actor, Block block) {
        LogBase base = LogBase.now(actor, BlockPosition.of(block));

        return BlockRecord.builder()
                .base(base)
                .type(block.getTypeId())
                .data(block.getData())
                .action(BlockAction.CLICK)
                .meta(null)
                .build();
    }

    public BlockRecord moved(Actor actor, Block source, Block destination) {
        LogBase base = LogBase.now(actor, BlockPosition.of(destination));

        return BlockRecord.builder()
                .base(base)
                .type(source.getTypeId())
                .data(source.getData())
                .action(BlockAction.PLACE)
                .meta(null)
                .build();
    }

    public BlockRecord raw(Actor actor, BlockPosition position, int type, BlockAction action) {
        return BlockRecord.builder()
                .base(LogBase.now(actor, position))
                .type(type)
                .data(0)
                .action(action)
                .meta(null)
                .build();
    }
}
