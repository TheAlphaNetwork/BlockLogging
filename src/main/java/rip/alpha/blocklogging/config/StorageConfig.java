package rip.alpha.blocklogging.config;

import lombok.Data;

@Data
public final class StorageConfig {

    private String type = "sqlite";
    private String file = "blocklogging.db";
    private String host = "127.0.0.1";

    private int port = 3306;

    private String database = "blocklogging";
    private String username = "root";
    private String password = "";
    private String properties = "useSSL=false&rewriteBatchedStatements=true";
    private String tablePrefix = "bl_";

    public boolean isMysql() {
        return this.type.equalsIgnoreCase("mysql");
    }
}
