package rip.alpha.blocklogging.inspect;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.inspect.state.Inspectors;
import rip.alpha.blocklogging.query.QueryService;
import rip.alpha.blocklogging.query.model.ExactRadius;
import rip.alpha.blocklogging.query.model.LookupView;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.blocklogging.record.capture.ContainerLocations;
import rip.alpha.blocklogging.record.capture.Interactables;

import java.util.Set;

@RequiredArgsConstructor
public final class InspectService {

    private final Inspectors inspectors = new Inspectors();

    private final QueryService queries;

    public boolean toggle(Player player) {
        return this.inspectors.toggle(player.getUniqueId());
    }

    public boolean isInspecting(Player player) {
        return this.inspectors.isInspecting(player.getUniqueId());
    }

    public void forget(Player player) {
        this.inspectors.remove(player.getUniqueId());
    }

    public void inspectBlock(Player player, Block block) {
        inspect(player, BlockPosition.of(block), Set.of(Action.BLOCK));
    }

    public void inspectClicked(Player player, Block block, BlockFace face) {
        BlockPosition container = ContainerLocations.of(block);

        Material type = block.getType();

        if (container != null)
            inspect(player, container, Set.of(Action.CONTAINER));

        else if (type == Material.WALL_SIGN || type == Material.SIGN_POST)
            inspect(player, BlockPosition.of(block), Set.of(Action.BLOCK, Action.SIGN));

        else if (Interactables.is(type))
            inspect(player, BlockPosition.of(block), Set.of(Action.BLOCK, Action.CLICK));

        else
            inspect(player, BlockPosition.of(block.getRelative(face)), Set.of(Action.BLOCK));
    }

    private void inspect(Player player, BlockPosition position, Set<Action> actions) {

        if (!this.inspectors.tryUse(player.getUniqueId()))
            return;

        LookupView view = new LookupView("Inspect " + position.describe(), false);

        this.queries.lookup(player, Query.at(new ExactRadius(position), actions), view);
    }
}
