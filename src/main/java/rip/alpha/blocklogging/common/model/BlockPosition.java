package rip.alpha.blocklogging.common.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public record BlockPosition(String world, int x, int y, int z) {

    public static BlockPosition of(Block block) {
        return new BlockPosition(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }

    public static BlockPosition of(Location location) {
        return new BlockPosition(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Nullable
    public Block block() {
        World handle = Bukkit.getWorld(this.world);

        return handle == null ? null : handle.getBlockAt(this.x, this.y, this.z);
    }

    @Nullable
    public Location center() {
        World handle = Bukkit.getWorld(this.world);

        return handle == null ? null : new Location(handle, this.x + 0.5, this.y, this.z + 0.5);
    }

    public BlockPosition offset(int dx, int dy, int dz) {
        return new BlockPosition(this.world, this.x + dx, this.y + dy, this.z + dz);
    }

    public String describe() {
        return "%d/%d/%d/%s".formatted(this.x, this.y, this.z, this.world);
    }
}
