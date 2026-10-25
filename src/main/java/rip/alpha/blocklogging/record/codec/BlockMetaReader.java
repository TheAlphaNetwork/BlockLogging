package rip.alpha.blocklogging.record.codec;

import lombok.experimental.UtilityClass;
import org.bukkit.Art;
import org.bukkit.DyeColor;
import org.bukkit.Rotation;
import org.bukkit.SkullType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.record.model.BannerData;
import rip.alpha.blocklogging.record.model.BlockMeta;
import rip.alpha.blocklogging.record.model.ContainerMeta;
import rip.alpha.blocklogging.record.model.HangingData;
import rip.alpha.blocklogging.record.model.SignMeta;
import rip.alpha.blocklogging.record.model.SkullData;
import rip.alpha.blocklogging.record.model.SpawnerMeta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@UtilityClass
class BlockMetaReader {

    @Nullable
    BlockMeta read(byte @Nullable [] data) {

        if (data == null || data.length == 0)
            return null;

        try (BukkitObjectInputStream input = new BukkitObjectInputStream(new ByteArrayInputStream(data))) {
            return read(input);

        } catch (IOException | ClassNotFoundException | IllegalArgumentException _) {
            return null;
        }
    }

    @Nullable
    private BlockMeta read(BukkitObjectInputStream input) throws IOException, ClassNotFoundException {
        return switch (input.readByte()) {
            case BlockMetaCodec.CONTAINER -> new ContainerMeta((ItemStack[]) input.readObject());
            case BlockMetaCodec.SIGN -> new SignMeta((String[]) input.readObject());
            case BlockMetaCodec.SPAWNER -> new SpawnerMeta(EntityType.valueOf(input.readUTF()));
            case BlockMetaCodec.SKULL -> skull(input);
            case BlockMetaCodec.HANGING -> hanging(input);
            case BlockMetaCodec.BANNER -> new BannerData(DyeColor.valueOf(input.readUTF()), List.of((Pattern[]) input.readObject()));
            default -> null;
        };
    }

    private HangingData hanging(BukkitObjectInputStream input) throws IOException, ClassNotFoundException {
        EntityType type = EntityType.valueOf(input.readUTF());

        BlockFace facing = BlockFace.valueOf(input.readUTF());

        ItemStack item = (ItemStack) input.readObject();

        String rotation = input.readUTF();
        String art = input.readUTF();

        return HangingData.builder()
                .type(type)
                .facing(facing)
                .item(item)
                .rotation(rotation.isEmpty() ? null : Rotation.valueOf(rotation))
                .art(art.isEmpty() ? null : Art.valueOf(art))
                .build();
    }

    private SkullData skull(BukkitObjectInputStream input) throws IOException {
        SkullType type = SkullType.valueOf(input.readUTF());

        String owner = input.readUTF();

        BlockFace rotation = BlockFace.valueOf(input.readUTF());

        return new SkullData(type, owner.isEmpty() ? null : owner, rotation);
    }
}
