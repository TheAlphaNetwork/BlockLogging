package rip.alpha.blocklogging.query.model;

import lombok.Builder;

@Builder
public record CuboidRadius(String world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) implements Radius {
}
