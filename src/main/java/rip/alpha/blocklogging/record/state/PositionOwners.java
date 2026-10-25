package rip.alpha.blocklogging.record.state;

import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public final class PositionOwners {

    private static final int CAPACITY = 50_000;

    private final Map<BlockPosition, Actor> owners = new HashMap<>();

    private final Deque<BlockPosition> order = new ArrayDeque<>();

    public void put(BlockPosition position, Actor owner) {

        if (this.owners.put(position, owner) == null)
            this.order.addLast(position);

        while (this.order.size() > CAPACITY)
            this.owners.remove(this.order.removeFirst());
    }

    @Nullable
    public Actor get(BlockPosition position) {
        return this.owners.get(position);
    }

    public void remove(BlockPosition position) {
        this.owners.remove(position);
    }
}
