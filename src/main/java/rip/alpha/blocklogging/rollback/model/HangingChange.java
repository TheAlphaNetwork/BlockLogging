package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.model.HangingData;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.List;

@Builder
public record HangingChange(BlockPosition position, HangingData data, boolean spawn, List<String> ids) implements Change {

    @Override
    public LogTable table() {
        return LogTable.BLOCK;
    }
}
