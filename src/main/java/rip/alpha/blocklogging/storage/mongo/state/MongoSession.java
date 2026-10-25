package rip.alpha.blocklogging.storage.mongo.state;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.config.StorageConfig;
import rip.alpha.blocklogging.storage.mongo.schema.MongoIndexes;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.EnumMap;
import java.util.Map;

public final class MongoSession {

    public static final String USERS = "user";
    public static final String OPERATIONS = "operation";

    private final Map<LogTable, MongoCollection<Document>> collections = new EnumMap<>(LogTable.class);

    private final StorageConfig config;

    @Nullable
    private MongoClient client;

    @Nullable
    private MongoDatabase database;

    @Nullable
    private MongoUsers users;

    public MongoSession(StorageConfig config) {
        this.config = config;
    }

    public void open() {
        this.client = MongoClients.create(this.config.getMongoUri());
        this.database = this.client.getDatabase(this.config.getDatabase());

        for (LogTable table : LogTable.values())
            this.collections.put(table, collection(table.getBase()));

        this.users = new MongoUsers(collection(USERS));

        MongoIndexes.create(this::collection, collection(USERS));
    }

    public void close() {

        if (this.client != null)
            this.client.close();
    }

    public MongoCollection<Document> collection(LogTable table) {
        return this.collections.get(table);
    }

    public MongoCollection<Document> collection(String base) {

        if (this.database == null)
            throw new IllegalStateException("The MongoDB store is not open");

        return this.database.getCollection(this.config.getTablePrefix() + base);
    }

    public MongoUsers users() {

        if (this.users == null)
            throw new IllegalStateException("The MongoDB store is not open");

        return this.users;
    }
}
