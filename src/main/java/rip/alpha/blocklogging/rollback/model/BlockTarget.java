package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.model.BlockMeta;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.List;

@Builder(toBuilder = true)
public record BlockTarget(BlockPosition position, int type, int data, @Nullable BlockMeta meta, List<String> ids) implements Change {

    @Override
    public LogTable table() {
        return LogTable.BLOCK;
    }
}
