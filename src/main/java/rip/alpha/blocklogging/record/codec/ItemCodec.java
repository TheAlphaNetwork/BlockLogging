package rip.alpha.blocklogging.record.codec;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UtilityClass
public class ItemCodec {

    public byte[] encode(ItemStack item) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try (BukkitObjectOutputStream output = new BukkitObjectOutputStream(bytes)) {
            output.writeObject(item);
        }

        return bytes.toByteArray();
    }

    @Nullable
    public ItemStack decode(byte @Nullable [] data) {

        if (data == null || data.length == 0)
            return null;

        try (BukkitObjectInputStream input = new BukkitObjectInputStream(new ByteArrayInputStream(data))) {
            return input.readObject() instanceof ItemStack item ? item : null;

        } catch (IOException | ClassNotFoundException _) {
            return null;
        }
    }
}
