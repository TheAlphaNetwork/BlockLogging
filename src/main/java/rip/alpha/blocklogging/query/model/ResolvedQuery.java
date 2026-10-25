package rip.alpha.blocklogging.query.model;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Builder
public record ResolvedQuery(Query query, @Nullable Set<Integer> users, Set<Integer> excludedUsers, @Nullable Integer world) {
}
