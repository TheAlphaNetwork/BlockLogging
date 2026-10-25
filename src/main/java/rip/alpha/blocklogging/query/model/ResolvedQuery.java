package rip.alpha.blocklogging.query.model;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record ResolvedQuery(Query query, @Nullable Set<Integer> users, Set<Integer> excludedUsers, @Nullable Integer world) {
}
