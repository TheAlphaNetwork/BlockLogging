package rip.alpha.blocklogging.storage.store;

import rip.alpha.blocklogging.common.model.BlockPosition;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface LogStore {

    String name();

    void open() throws StoreException;

    void close();

    int write(List<PendingWrite> writes) throws StoreException;

    PurgeResult purge(PurgeRequest request) throws StoreException;

    LookupPage lookup(Query query, PageRequest page) throws StoreException;

    History history(Query query, RollbackKind kind) throws StoreException;

    Map<BlockPosition, String> signs(Collection<BlockPosition> positions) throws StoreException;

    List<OperationRecord> operations() throws StoreException;
}
