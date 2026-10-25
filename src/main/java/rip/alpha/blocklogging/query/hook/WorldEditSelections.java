package rip.alpha.blocklogging.query.hook;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.query.model.CuboidRadius;

@UtilityClass
public class WorldEditSelections {

    @Nullable
    public CuboidRadius of(Player player) {

        if (!(Bukkit.getPluginManager().getPlugin("WorldEdit") instanceof WorldEditPlugin worldEdit))
            return null;

        Selection selection = worldEdit.getSelection(player);

        if (selection == null || selection.getWorld() == null)
            return null;

        Location min = selection.getMinimumPoint();
        Location max = selection.getMaximumPoint();

        return CuboidRadius.builder()
                .world(selection.getWorld().getName())
                .minX(min.getBlockX())
                .minY(min.getBlockY())
                .minZ(min.getBlockZ())
                .maxX(max.getBlockX())
                .maxY(max.getBlockY())
                .maxZ(max.getBlockZ())
                .build();
    }
}
