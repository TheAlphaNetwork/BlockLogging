package rip.alpha.blocklogging.rollback.menu.button;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.query.hook.WorldEditSelections;
import rip.alpha.blocklogging.query.model.AreaRadius;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.Radius;
import rip.alpha.blocklogging.query.type.QueryFlag;
import rip.alpha.blocklogging.rollback.state.RollbackDraft;
import rip.alpha.blocklogging.rollback.type.RollbackScope;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@UtilityClass
class DraftQueries {

    @Nullable
    Query of(RollbackDraft draft, Player player, Set<QueryFlag> flags) {
        Radius radius = draft.isSelection() ? WorldEditSelections.of(player) : area(draft, player.getLocation());

        if (radius == null)
            return null;

        long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        return Query.at(radius, RollbackScope.of(draft.getScope()).getActions())
                .toBuilder()
                .since(now - draft.seconds())
                .users(draft.getTarget() == null ? List.of() : List.of(draft.getTarget()))
                .flags(flags)
                .build();
    }

    private AreaRadius area(RollbackDraft draft, Location location) {
        return AreaRadius.builder()
                .world(location.getWorld().getName())
                .x(location.getBlockX())
                .z(location.getBlockZ())
                .size(draft.blocks())
                .build();
    }
}
