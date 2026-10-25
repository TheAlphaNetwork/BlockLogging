package rip.alpha.blocklogging.record.write;

import lombok.Getter;
import rip.alpha.blocklogging.record.model.BlockRecord;
import rip.alpha.blocklogging.record.model.ContainerRecord;
import rip.alpha.blocklogging.record.model.ItemRecord;
import rip.alpha.blocklogging.record.model.KillRecord;
import rip.alpha.blocklogging.record.model.MessageRecord;
import rip.alpha.blocklogging.record.model.PendingWrite;
import rip.alpha.blocklogging.record.model.PurgeRequest;
import rip.alpha.blocklogging.record.model.RolledBackUpdate;
import rip.alpha.blocklogging.record.model.SessionRecord;
import rip.alpha.blocklogging.record.model.SignRecord;
import rip.alpha.blocklogging.record.type.MessageChannel;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

final class WriteBatch {

    private static final BlockInsert BLOCKS = new BlockInsert();

    private static final ContainerInsert CONTAINERS = new ContainerInsert();

    private static final ItemInsert ITEMS = new ItemInsert();

    private static final KillInsert KILLS = new KillInsert();

    private static final SessionInsert SESSIONS = new SessionInsert();

    private static final TextInsert<SignRecord> SIGNS = new TextInsert<>(LogTable.SIGN, SignRecord::text);
    private static final TextInsert<MessageRecord> CHATS = new TextInsert<>(LogTable.CHAT, MessageRecord::text);
    private static final TextInsert<MessageRecord> COMMANDS = new TextInsert<>(LogTable.COMMAND, MessageRecord::text);

    private final List<BlockRecord> blocks = new ArrayList<>();
    private final List<ContainerRecord> containers = new ArrayList<>();
    private final List<ItemRecord> items = new ArrayList<>();
    private final List<KillRecord> kills = new ArrayList<>();
    private final List<SessionRecord> sessions = new ArrayList<>();
    private final List<SignRecord> signs = new ArrayList<>();
    private final List<MessageRecord> chats = new ArrayList<>();
    private final List<MessageRecord> commands = new ArrayList<>();

    @Getter
    private final List<RolledBackUpdate> updates = new ArrayList<>();

    @Getter
    private final List<PurgeRequest> purges = new ArrayList<>();

    WriteBatch(List<PendingWrite> writes) {
        writes.forEach(this::add);
    }

    int insert(WriteContext context) throws SQLException, IOException {
        int written = InsertRunner.run(context, BLOCKS, this.blocks);

        written += InsertRunner.run(context, CONTAINERS, this.containers);
        written += InsertRunner.run(context, ITEMS, this.items);
        written += InsertRunner.run(context, KILLS, this.kills);
        written += InsertRunner.run(context, SESSIONS, this.sessions);
        written += InsertRunner.run(context, SIGNS, this.signs);
        written += InsertRunner.run(context, CHATS, this.chats);
        written += InsertRunner.run(context, COMMANDS, this.commands);
        return written;
    }

    private void add(PendingWrite write) {

        switch (write) {
            case BlockRecord record -> this.blocks.add(record);
            case ContainerRecord record -> this.containers.add(record);
            case ItemRecord record -> this.items.add(record);
            case KillRecord record -> this.kills.add(record);
            case SessionRecord record -> this.sessions.add(record);
            case SignRecord record -> this.signs.add(record);
            case MessageRecord record -> (record.channel() == MessageChannel.CHAT ? this.chats : this.commands).add(record);
            case RolledBackUpdate update -> this.updates.add(update);
            case PurgeRequest purge -> this.purges.add(purge);
        }
    }
}
