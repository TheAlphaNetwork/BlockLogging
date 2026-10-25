package rip.alpha.blocklogging.query.model;

import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.Set;

public record TypeFilter(Set<Integer> blocks, Set<Integer> entities) {

    public static TypeFilter none() {
        return new TypeFilter(Set.of(), Set.of());
    }

    public Set<Integer> forTable(LogTable table) {
        return table == LogTable.KILL ? this.entities : this.blocks;
    }

    public boolean isEmpty() {
        return this.blocks.isEmpty() && this.entities.isEmpty();
    }
}
