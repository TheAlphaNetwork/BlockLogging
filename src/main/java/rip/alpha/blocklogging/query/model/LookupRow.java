package rip.alpha.blocklogging.query.model;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.UUID;

@Builder(toBuilder = true)
public record LookupRow(
        LogTable table,
        long time,
        String user,
        @Nullable UUID uuid,
        BlockPosition position,
        int type,
        int data,
        int amount,
        int action,
        boolean rolledBack,
        @Nullable String text,
        @Nullable LookupRow replaced
) {
}
