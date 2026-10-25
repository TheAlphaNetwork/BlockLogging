package rip.alpha.blocklogging.query.model;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.storage.type.LogTable;

@Builder
public record LookupRow(
        LogTable table,
        long time,
        String user,
        BlockPosition position,
        int type,
        int data,
        int amount,
        int action,
        boolean rolledBack,
        @Nullable String text
) {
}
