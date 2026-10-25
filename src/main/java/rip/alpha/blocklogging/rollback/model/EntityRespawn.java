package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.model.EntityMeta;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.List;

@Builder
public record EntityRespawn(BlockPosition position, EntityType type, @Nullable EntityMeta meta, String id) implements Change {

    @Override
    public LogTable table() {
        return LogTable.KILL;
    }

    @Override
    public List<String> ids() {
        return List.of(this.id);
    }
}
