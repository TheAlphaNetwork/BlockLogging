package rip.alpha.blocklogging.query.parse;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.Radius;
import rip.alpha.blocklogging.query.model.TypeFilter;
import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.blocklogging.query.type.QueryFlag;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
final class QueryDraft {

    private final List<String> users = new ArrayList<>();
    private final List<String> excludedUsers = new ArrayList<>();

    private final Set<Action> actions = EnumSet.noneOf(Action.class);
    private final Set<QueryFlag> flags = EnumSet.noneOf(QueryFlag.class);

    private final Set<Integer> includeBlocks = new HashSet<>();
    private final Set<Integer> includeEntities = new HashSet<>();
    private final Set<Integer> excludeBlocks = new HashSet<>();
    private final Set<Integer> excludeEntities = new HashSet<>();

    @Setter
    private long since;

    @Setter
    private long until;

    @Setter
    @Nullable
    private Radius radius;

    Query build(Radius fallback) {
        return Query.builder()
                .users(List.copyOf(this.users))
                .excludedUsers(List.copyOf(this.excludedUsers))

                .since(this.since)
                .until(this.until)
                .radius(this.radius == null ? fallback : this.radius)

                .actions(Set.copyOf(this.actions))

                .include(new TypeFilter(Set.copyOf(this.includeBlocks), Set.copyOf(this.includeEntities)))
                .exclude(new TypeFilter(Set.copyOf(this.excludeBlocks), Set.copyOf(this.excludeEntities)))

                .flags(Set.copyOf(this.flags))
                .build();
    }
}
