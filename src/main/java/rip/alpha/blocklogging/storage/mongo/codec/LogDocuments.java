package rip.alpha.blocklogging.storage.mongo.codec;

import lombok.experimental.UtilityClass;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.record.codec.BlockMetaCodec;
import rip.alpha.blocklogging.record.codec.EntityMetaCodec;
import rip.alpha.blocklogging.record.codec.ItemCodec;
import rip.alpha.blocklogging.record.model.BlockRecord;
import rip.alpha.blocklogging.record.model.ContainerRecord;
import rip.alpha.blocklogging.record.model.ItemRecord;
import rip.alpha.blocklogging.record.model.KillRecord;
import rip.alpha.blocklogging.record.model.LogBase;
import rip.alpha.blocklogging.record.model.LogRecord;
import rip.alpha.blocklogging.record.model.MessageRecord;
import rip.alpha.blocklogging.record.model.SessionRecord;
import rip.alpha.blocklogging.record.model.SignRecord;

import java.io.IOException;
import java.util.Locale;

@UtilityClass
@SuppressWarnings("deprecation")
public class LogDocuments {

    public Document of(LogRecord record) throws IOException {
        Document document = base(record.base());

        switch (record) {
            case BlockRecord block -> document
                    .append(MongoFields.TYPE, block.type())
                    .append(MongoFields.DATA, block.data())
                    .append(MongoFields.ACTION, block.action().getId())
                    .append(MongoFields.META, binary(BlockMetaCodec.encode(block.meta())));

            case ContainerRecord container -> document
                    .append(MongoFields.TYPE, container.item().getTypeId())
                    .append(MongoFields.DATA, (int) container.item().getDurability())
                    .append(MongoFields.AMOUNT, container.amount())
                    .append(MongoFields.ACTION, container.action().getId())
                    .append(MongoFields.ITEM, binary(ItemCodec.encode(container.item())));

            case ItemRecord item -> document
                    .append(MongoFields.TYPE, item.item().getTypeId())
                    .append(MongoFields.DATA, (int) item.item().getDurability())
                    .append(MongoFields.AMOUNT, item.amount())
                    .append(MongoFields.ACTION, item.action().getId())
                    .append(MongoFields.ITEM, binary(ItemCodec.encode(item.item())));

            case KillRecord kill -> document
                    .append(MongoFields.TYPE, (int) kill.type().getTypeId())
                    .append(MongoFields.META, binary(EntityMetaCodec.encode(kill.meta())));

            case SessionRecord session -> document.append(MongoFields.ACTION, session.action().getId());
            case SignRecord sign -> document.append(MongoFields.TEXT, sign.text());
            case MessageRecord message -> document.append(MongoFields.TEXT, message.text());
        }

        return document;
    }

    public Document username(Actor actor, long time) {
        BlockPosition nowhere = BlockPosition.builder()
                .world("")
                .x(0)
                .y(0)
                .z(0)
                .build();

        return base(new LogBase(time, actor, nowhere)).append(MongoFields.TEXT, actor.name());
    }

    private Document base(LogBase base) {
        Actor actor = base.actor();

        BlockPosition position = base.position();

        return new Document(MongoFields.ID, new ObjectId())
                .append(MongoFields.TIME, base.time())
                .append(MongoFields.USER, actor.name())
                .append(MongoFields.USER_LOWER, actor.name().toLowerCase(Locale.ROOT))
                .append(MongoFields.UUID, actor.isPlayer() ? actor.uuid().toString() : null)
                .append(MongoFields.WORLD, position.world())
                .append(MongoFields.X, position.x())
                .append(MongoFields.Y, position.y())
                .append(MongoFields.Z, position.z())
                .append(MongoFields.ROLLED_BACK, 0);
    }

    @Nullable
    private Binary binary(byte @Nullable [] bytes) {
        return bytes == null ? null : new Binary(bytes);
    }
}
