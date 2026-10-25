package rip.alpha.blocklogging.record.codec;

import lombok.experimental.UtilityClass;
import org.bukkit.block.banner.Pattern;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.record.model.BannerData;
import rip.alpha.blocklogging.record.model.BlockMeta;
import rip.alpha.blocklogging.record.model.ContainerMeta;
import rip.alpha.blocklogging.record.model.SignMeta;
import rip.alpha.blocklogging.record.model.SkullData;
import rip.alpha.blocklogging.record.model.SpawnerMeta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UtilityClass
public class BlockMetaCodec {

    public final int CONTAINER = 0;
    public final int SIGN = 1;
    public final int SPAWNER = 2;
    public final int SKULL = 3;
    public final int BANNER = 4;

    public byte @Nullable [] encode(@Nullable BlockMeta meta) throws IOException {

        if (meta == null)
            return null;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try (BukkitObjectOutputStream output = new BukkitObjectOutputStream(bytes)) {
            write(output, meta);
        }

        return bytes.toByteArray();
    }

    @Nullable
    public BlockMeta decode(byte @Nullable [] data) {
        return BlockMetaReader.read(data);
    }

    private void write(BukkitObjectOutputStream output, BlockMeta meta) throws IOException {

        switch (meta) {
            case ContainerMeta container -> {
                output.writeByte(CONTAINER);
                output.writeObject(container.contents());
            }

            case SignMeta sign -> {
                output.writeByte(SIGN);
                output.writeObject(sign.lines());
            }

            case SpawnerMeta spawner -> {
                output.writeByte(SPAWNER);
                output.writeUTF(spawner.type().name());
            }

            case SkullData skull -> {
                output.writeByte(SKULL);
                output.writeUTF(skull.type().name());
                output.writeUTF(skull.owner() == null ? "" : skull.owner());
                output.writeUTF(skull.rotation().name());
            }

            case BannerData banner -> {
                output.writeByte(BANNER);
                output.writeUTF(banner.base().name());
                output.writeObject(banner.patterns().toArray(new Pattern[0]));
            }
        }
    }
}
