package rip.alpha.blocklogging.storage.mongo;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.config.StorageConfig;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.PageRequest;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.record.model.PendingWrite;
import rip.alpha.blocklogging.record.model.PurgeRequest;
import rip.alpha.blocklogging.record.model.PurgeResult;
import rip.alpha.blocklogging.rollback.model.History;
import rip.alpha.blocklogging.rollback.model.OperationRecord;
import rip.alpha.blocklogging.rollback.type.RollbackKind;
import rip.alpha.blocklogging.storage.exception.StoreException;
import rip.alpha.blocklogging.storage.mongo.model.MongoQuery;
import rip.alpha.blocklogging.storage.mongo.read.MongoCall;
import rip.alpha.blocklogging.storage.mongo.read.MongoHistory;
import rip.alpha.blocklogging.storage.mongo.read.MongoLookup;
import rip.alpha.blocklogging.storage.mongo.read.MongoOperations;
import rip.alpha.blocklogging.storage.mongo.read.MongoSigns;
import rip.alpha.blocklogging.storage.mongo.state.MongoSession;
import rip.alpha.blocklogging.storage.mongo.write.MongoPurge;
import rip.alpha.blocklogging.storage.mongo.write.MongoWrites;
import rip.alpha.blocklogging.storage.store.LogStore;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class MongoStore implements LogStore {

    private final MongoSession session;

    public MongoStore(StorageConfig config) {
        this.session = new MongoSession(config);
    }

    @Override
    public String name() {
        return "MongoDB";
    }

    @Override
    public void open() throws StoreException {

        try {
            this.session.open();
        } catch (MongoException exception) {
            throw new StoreException("Could not open the MongoDB database", exception);
        }
    }

    @Override
    public void close() {
        this.session.close();
    }

    @Override
    public int write(List<PendingWrite> writes) throws StoreException {
        MongoWrites writer = new MongoWrites(this.session::collection, operationCollection(), this.session.users());

        try {
            return MongoCall.run("Could not write a batch of logs", () -> writer.write(writes));

        } catch (StoreException exception) {
            this.session.users().clear();

            throw exception;
        }
    }

    @Override
    public PurgeResult purge(PurgeRequest request) throws StoreException {
        return MongoCall.run("Could not purge logs", () -> new MongoPurge(this.session::collection).purge(request));
    }

    @Override
    public LookupPage lookup(Query query, PageRequest page) throws StoreException {
        return MongoCall.run("Lookup failed", () -> new MongoLookup(this.session::collection).page(resolve(query), page));
    }

    @Override
    public History history(Query query, RollbackKind kind) throws StoreException {
        return MongoCall.run("Could not load the history", () -> new MongoHistory(this.session::collection).load(resolve(query), kind));
    }

    @Override
    public Map<BlockPosition, String> signs(Collection<BlockPosition> positions) throws StoreException {
        return MongoCall.run("Could not load sign text", () -> MongoSigns.latest(this.session.collection(LogTable.SIGN), positions));
    }

    @Override
    public List<OperationRecord> operations() throws StoreException {
        return MongoCall.run("Could not load rollback operations", () -> MongoOperations.read(operationCollection()));
    }

    private MongoCollection<Document> operationCollection() {
        return this.session.collection(MongoSession.OPERATIONS);
    }

    private MongoQuery resolve(Query query) {
        return this.session.users().query(query);
    }
}
