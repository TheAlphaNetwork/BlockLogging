package rip.alpha.blocklogging.query.model;

import lombok.Builder;
import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.blocklogging.query.type.QueryFlag;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Builder(toBuilder = true)
public record Query(
        List<String> users,
        List<String> excludedUsers,
        long since,
        long until,
        Radius radius,
        Set<Action> actions,
        TypeFilter include,
        TypeFilter exclude,
        Set<QueryFlag> flags
) {

    public static Query at(Radius radius, Set<Action> actions) {
        return Query.builder()
                .users(List.of())
                .excludedUsers(List.of())
                .radius(radius)
                .actions(actions)
                .include(TypeFilter.none())
                .exclude(TypeFilter.none())
                .flags(Set.of())
                .build();
    }

    public boolean has(QueryFlag flag) {
        return this.flags.contains(flag);
    }

    public Query withActions(Set<Action> actions) {
        return toBuilder().actions(actions).build();
    }

    public Query withRadius(Radius radius) {
        return toBuilder().radius(radius).build();
    }

    public Query without(QueryFlag flag) {
        Set<QueryFlag> remaining = this.flags.isEmpty() ? EnumSet.noneOf(QueryFlag.class) : EnumSet.copyOf(this.flags);

        remaining.remove(flag);

        return toBuilder().flags(Set.copyOf(remaining)).build();
    }
}
