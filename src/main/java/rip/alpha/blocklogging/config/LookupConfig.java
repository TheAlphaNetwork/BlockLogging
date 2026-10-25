package rip.alpha.blocklogging.config;

import lombok.Data;

@Data
public final class LookupConfig {

    private int pageSize = 8;
    private int maxPageSize = 50;
    private int nearRadius = 5;
}
