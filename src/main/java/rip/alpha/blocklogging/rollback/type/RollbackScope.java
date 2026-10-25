package rip.alpha.blocklogging.rollback.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import rip.alpha.blocklogging.query.type.Action;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum RollbackScope {

    EVERYTHING("Blocks, containers and kills", Material.CHEST, Set.of(Action.BLOCK, Action.CONTAINER, Action.KILL)),
    BLOCKS("Blocks only", Material.GRASS, Set.of(Action.BLOCK)),
    CONTAINERS("Containers only", Material.TRAPPED_CHEST, Set.of(Action.CONTAINER)),
    ITEMS("Player items", Material.DIAMOND, Set.of(Action.ITEM));

    private final String label;

    private final Material icon;

    private final Set<Action> actions;

    public static RollbackScope of(int index) {
        RollbackScope[] scopes = values();

        return scopes[Math.floorMod(index, scopes.length)];
    }
}
