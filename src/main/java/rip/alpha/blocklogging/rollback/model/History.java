package rip.alpha.blocklogging.rollback.model;

import lombok.Builder;

import java.util.List;

@Builder
public record History(List<BlockHistory> blocks, List<ContainerHistory> containers, List<KillHistory> kills, List<ItemHistory> items) {

    public static History empty() {
        return History.builder()
                .blocks(List.of())
                .containers(List.of())
                .kills(List.of())
                .items(List.of())
                .build();
    }

    public int size() {
        return this.blocks.size() + this.containers.size() + this.kills.size() + this.items.size();
    }
}
