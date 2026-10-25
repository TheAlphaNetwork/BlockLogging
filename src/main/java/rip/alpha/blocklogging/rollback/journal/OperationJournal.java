package rip.alpha.blocklogging.rollback.journal;

import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.record.RecordService;
import rip.alpha.blocklogging.record.model.OperationWrite;
import rip.alpha.blocklogging.rollback.model.Operation;
import rip.alpha.blocklogging.rollback.model.OperationRecord;
import rip.alpha.blocklogging.rollback.type.OperationState;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class OperationJournal {

    private final RecordService records;

    public void started(String owner, Operation operation) {
        write(owner, operation, OperationState.RUNNING);
    }

    public void finished(String owner, Operation operation) {
        write(owner, operation, OperationState.DONE);
    }

    private void write(String owner, Operation operation, OperationState state) {
        OperationRecord record = OperationRecord.builder()
                .operation(operation)
                .sender(owner)
                .state(state)
                .time(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()))
                .build();

        this.records.submit(new OperationWrite(record));
    }
}
