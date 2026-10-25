package rip.alpha.blocklogging.rollback.model;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.List;
import java.util.UUID;

public record EntityRemoval(BlockPosition position, @Nullable UUID entity, String id) implements Change {

    @Override
    public LogTable table() {
        return LogTable.KILL;
    }

    @Override
    public List<String> ids() {
        return List.of(this.id);
    }
}
