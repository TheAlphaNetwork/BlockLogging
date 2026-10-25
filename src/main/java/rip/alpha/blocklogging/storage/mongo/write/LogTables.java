package rip.alpha.blocklogging.storage.mongo.write;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.record.model.BlockRecord;
import rip.alpha.blocklogging.record.model.ContainerRecord;
import rip.alpha.blocklogging.record.model.ItemRecord;
import rip.alpha.blocklogging.record.model.KillRecord;
import rip.alpha.blocklogging.record.model.LogRecord;
import rip.alpha.blocklogging.record.model.MessageRecord;
import rip.alpha.blocklogging.record.model.SessionRecord;
import rip.alpha.blocklogging.record.model.SignRecord;
import rip.alpha.blocklogging.storage.type.LogTable;

@UtilityClass
class LogTables {

    LogTable of(LogRecord record) {
        return switch (record) {
            case BlockRecord _ -> LogTable.BLOCK;
            case ContainerRecord _ -> LogTable.CONTAINER;
            case ItemRecord _ -> LogTable.ITEM;
            case KillRecord _ -> LogTable.KILL;
            case SessionRecord _ -> LogTable.SESSION;
            case SignRecord _ -> LogTable.SIGN;
            case MessageRecord message -> message.channel().getTable();
        };
    }
}
