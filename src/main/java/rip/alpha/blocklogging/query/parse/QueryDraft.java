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
        return new Query(
                List.copyOf(this.users),
                List.copyOf(this.excludedUsers),

                this.since,
                this.until,
                this.radius == null ? fallback : this.radius,

                Set.copyOf(this.actions),

                new TypeFilter(Set.copyOf(this.includeBlocks), Set.copyOf(this.includeEntities)),
                new TypeFilter(Set.copyOf(this.excludeBlocks), Set.copyOf(this.excludeEntities)),

                Set.copyOf(this.flags)
        );
    }
}
