package rip.alpha.blocklogging.query.model;

import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.blocklogging.query.type.QueryFlag;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

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
        return new Query(List.of(), List.of(), 0L, 0L, radius, actions, TypeFilter.none(), TypeFilter.none(), Set.of());
    }

    public boolean has(QueryFlag flag) {
        return this.flags.contains(flag);
    }

    public Query withActions(Set<Action> actions) {
        return new Query(this.users, this.excludedUsers, this.since, this.until, this.radius, actions, this.include, this.exclude,
                this.flags);
    }

    public Query withRadius(Radius radius) {
        return new Query(this.users, this.excludedUsers, this.since, this.until, radius, this.actions, this.include, this.exclude,
                this.flags);
    }

    public Query without(QueryFlag flag) {
        Set<QueryFlag> remaining = this.flags.isEmpty() ? EnumSet.noneOf(QueryFlag.class) : EnumSet.copyOf(this.flags);

        remaining.remove(flag);

        return new Query(this.users, this.excludedUsers, this.since, this.until, this.radius, this.actions, this.include, this.exclude,
                Set.copyOf(remaining));
    }
}
