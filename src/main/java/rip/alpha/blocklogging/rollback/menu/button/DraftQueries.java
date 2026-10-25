package rip.alpha.blocklogging.rollback.menu.button;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.query.model.AreaRadius;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.type.QueryFlag;
import rip.alpha.blocklogging.rollback.state.RollbackDraft;
import rip.alpha.blocklogging.rollback.type.RollbackScope;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@UtilityClass
class DraftQueries {

    Query of(RollbackDraft draft, Player player, Set<QueryFlag> flags) {
        Location location = player.getLocation();

        long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        AreaRadius area = AreaRadius.builder()
                .world(location.getWorld().getName())
                .x(location.getBlockX())
                .z(location.getBlockZ())
                .size(draft.blocks())
                .build();

        return Query.at(area, RollbackScope.of(draft.getScope()).getActions())
                .toBuilder()
                .since(now - draft.seconds())
                .users(draft.getTarget() == null ? List.of() : List.of(draft.getTarget()))
                .flags(flags)
                .build();
    }
}
