package rip.alpha.blocklogging.record.model;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public record PurgeRequest(long before, @Nullable String world, boolean optimize, Consumer<PurgeResult> callback) implements PendingWrite {
}
