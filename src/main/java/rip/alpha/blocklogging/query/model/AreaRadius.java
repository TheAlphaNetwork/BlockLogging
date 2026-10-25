package rip.alpha.blocklogging.query.model;

import lombok.Builder;
@Builder
public record AreaRadius(String world, int x, int z, int size) implements Radius {
}
