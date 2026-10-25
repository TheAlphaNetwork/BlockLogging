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

        return new BlockRecord(base, state.getTypeId(), state.getRawData(), BlockAction.BREAK, BlockMetas.of(state));
    }

    public BlockRecord broke(Actor actor, Block block) {
        return broke(actor, block.getState());
    }

    public BlockRecord placed(Actor actor, BlockState state) {
        LogBase base = LogBase.now(actor, BlockPosition.of(state.getBlock()));

        return new BlockRecord(base, state.getTypeId(), state.getRawData(), BlockAction.PLACE, null);
    }

    public BlockRecord placed(Actor actor, Block block) {
        return placed(actor, block.getState());
    }

    public BlockRecord clicked(Actor actor, Block block) {
        LogBase base = LogBase.now(actor, BlockPosition.of(block));

        return new BlockRecord(base, block.getTypeId(), block.getData(), BlockAction.CLICK, null);
    }

    public BlockRecord moved(Actor actor, Block source, Block destination) {
        LogBase base = LogBase.now(actor, BlockPosition.of(destination));

        return new BlockRecord(base, source.getTypeId(), source.getData(), BlockAction.PLACE, null);
    }

    public BlockRecord raw(Actor actor, BlockPosition position, int type, BlockAction action) {
        return new BlockRecord(LogBase.now(actor, position), type, 0, action, null);
    }
}
