package rip.alpha.blocklogging.storage.mongo.codec;

import lombok.experimental.UtilityClass;
import org.bson.Document;
import org.bson.types.Binary;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.blocklogging.record.codec.ItemCodec;
import rip.alpha.blocklogging.rollback.model.BlockHistory;
import rip.alpha.blocklogging.rollback.model.ContainerHistory;
import rip.alpha.blocklogging.rollback.model.ItemHistory;
import rip.alpha.blocklogging.rollback.model.KillHistory;
import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.UUID;

@UtilityClass
public class RowDocuments {

    public LookupRow row(LogTable table, Document document) {
        return LookupRow.builder()
                .table(table)
                .time(document.getLong(MongoFields.TIME))
                .user(document.getString(MongoFields.USER))
                .position(position(document))
                .type(integer(document, MongoFields.TYPE))
                .data(integer(document, MongoFields.DATA))
                .amount(Math.max(1, integer(document, MongoFields.AMOUNT)))
                .action(integer(document, MongoFields.ACTION))
                .rolledBack(integer(document, MongoFields.ROLLED_BACK) == 1)
                .text(document.getString(MongoFields.TEXT))
                .build();
    }

    public BlockHistory block(Document document) {
        return BlockHistory.builder()
                .id(id(document))
                .position(position(document))
                .type(integer(document, MongoFields.TYPE))
                .data(integer(document, MongoFields.DATA))
                .action(integer(document, MongoFields.ACTION))
                .meta(bytes(document, MongoFields.META))
                .build();
    }

    public ContainerHistory container(Document document) {
        return ContainerHistory.builder()
                .id(id(document))
                .position(position(document))
                .item(ItemCodec.decode(bytes(document, MongoFields.ITEM)))
                .amount(integer(document, MongoFields.AMOUNT))
                .action(integer(document, MongoFields.ACTION))
                .build();
    }

    public KillHistory kill(Document document) {
        return KillHistory.builder()
                .id(id(document))
                .position(position(document))
                .type(integer(document, MongoFields.TYPE))
                .meta(bytes(document, MongoFields.META))
                .respawned(uuid(document.getString(MongoFields.RESPAWNED)))
                .build();
    }

    public ItemHistory item(Document document) {
        return ItemHistory.builder()
                .id(id(document))
                .player(uuid(document.getString(MongoFields.UUID)))
                .item(ItemCodec.decode(bytes(document, MongoFields.ITEM)))
                .amount(integer(document, MongoFields.AMOUNT))
                .action(integer(document, MongoFields.ACTION))
                .build();
    }

    public BlockPosition position(Document document) {
        return BlockPosition.builder()
                .world(document.getString(MongoFields.WORLD))
                .x(integer(document, MongoFields.X))
                .y(integer(document, MongoFields.Y))
                .z(integer(document, MongoFields.Z))
                .build();
    }

    private String id(Document document) {
        return document.getObjectId(MongoFields.ID).toHexString();
    }

    private int integer(Document document, String field) {
        return document.get(field) instanceof Number number ? number.intValue() : 0;
    }

    private byte @Nullable [] bytes(Document document, String field) {
        return document.get(field) instanceof Binary binary ? binary.getData() : null;
    }

    @Nullable
    private UUID uuid(@Nullable String value) {
        return value == null ? null : UUID.fromString(value);
    }
}
