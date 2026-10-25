package rip.alpha.blocklogging.record.codec;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.record.model.EntityMeta;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class EntityMetaCodec {

    public byte @Nullable [] encode(@Nullable EntityMeta meta) throws IOException {

        if (meta == null)
            return null;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try (BukkitObjectOutputStream output = new BukkitObjectOutputStream(bytes)) {
            output.writeUTF(meta.name() == null ? "" : meta.name());
            output.writeBoolean(meta.baby());
            output.writeObject(new HashMap<>(meta.traits()));
            output.writeObject(meta.equipment());
        }

        return bytes.toByteArray();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public EntityMeta decode(byte @Nullable [] data) {

        if (data == null || data.length == 0)
            return null;

        try (BukkitObjectInputStream input = new BukkitObjectInputStream(new ByteArrayInputStream(data))) {
            String name = input.readUTF();

            boolean baby = input.readBoolean();

            Map<String, String> traits = (Map<String, String>) input.readObject();

            ItemStack[] equipment = (ItemStack[]) input.readObject();

            return EntityMeta.builder()
                    .name(name.isEmpty() ? null : name)
                    .baby(baby)
                    .traits(Map.copyOf(traits))
                    .equipment(equipment)
                    .build();

        } catch (IOException | ClassNotFoundException | ClassCastException _) {
            return null;
        }
    }
}
