package rip.alpha.blocklogging.common.model;

import lombok.Builder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

@Builder(toBuilder = true)
public record BlockPosition(String world, int x, int y, int z) {

    public static BlockPosition of(Block block) {
        return BlockPosition.builder()
                .world(block.getWorld().getName())
                .x(block.getX())
                .y(block.getY())
                .z(block.getZ())
                .build();
    }

    public static BlockPosition of(Location location) {
        return BlockPosition.builder()
                .world(location.getWorld().getName())
                .x(location.getBlockX())
                .y(location.getBlockY())
                .z(location.getBlockZ())
                .build();
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
        return toBuilder().x(this.x + dx).y(this.y + dy).z(this.z + dz).build();
    }

    public String pretty() {
        return "%d, %d, %d".formatted(this.x, this.y, this.z);
    }

    public String describe() {
        return "%d/%d/%d/%s".formatted(this.x, this.y, this.z, this.world);
    }
}
