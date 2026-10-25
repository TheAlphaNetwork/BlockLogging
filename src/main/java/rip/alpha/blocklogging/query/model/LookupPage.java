package rip.alpha.blocklogging.query.model;

import lombok.Builder;

import java.util.List;

@Builder
public record LookupPage(List<LookupRow> rows, int total, int page, int pageSize) {

    public int pages() {
        return Math.max(1, (this.total + this.pageSize - 1) / this.pageSize);
    }
}
