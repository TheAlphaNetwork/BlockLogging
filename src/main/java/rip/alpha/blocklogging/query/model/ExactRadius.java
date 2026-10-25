package rip.alpha.blocklogging.query.model;

import rip.alpha.blocklogging.common.model.BlockPosition;

public record ExactRadius(BlockPosition position) implements Radius {

    @Override
    public String world() {
        return this.position.world();
    }
}
