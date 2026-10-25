package rip.alpha.blocklogging.query.model;

import java.util.List;

public record LookupPage(List<LookupRow> rows, int total, int page, int pageSize) {

    public int pages() {
        return Math.max(1, (this.total + this.pageSize - 1) / this.pageSize);
    }
}
