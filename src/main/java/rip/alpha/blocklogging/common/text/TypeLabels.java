package rip.alpha.blocklogging.common.text;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import rip.alpha.libraries.item.name.ItemNames;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@UtilityClass
@SuppressWarnings("deprecation")
public class TypeLabels {

    private final int MAX_VARIANT = 15;

    public String block(int id, int data) {
        Material material = Material.getMaterial(id);

        if (material == null)
            return "#" + id;

        if (data <= 0 || data > MAX_VARIANT || material.getMaxDurability() > 0)
            return ItemNames.pretty(material);

        try {
            return ItemNames.of(new ItemStack(material, 1, (short) data));

        } catch (RuntimeException _) {
            return ItemNames.pretty(material);
        }
    }

    public String entity(int id) {
        EntityType type = EntityType.fromId(id);

        return type == null ? "#" + id : words(type.name());
    }

    private String words(String constant) {
        return Arrays.stream(constant.split("_"))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(" "));
    }
}
