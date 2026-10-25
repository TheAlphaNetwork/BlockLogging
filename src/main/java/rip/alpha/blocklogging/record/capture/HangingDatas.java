package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.record.model.HangingData;

@UtilityClass
public class HangingDatas {

    public HangingData of(Hanging hanging) {
        HangingData.HangingDataBuilder data = HangingData.builder()
                .type(hanging instanceof ItemFrame ? EntityType.ITEM_FRAME : EntityType.PAINTING)
                .facing(hanging.getFacing());

        if (hanging instanceof ItemFrame frame)
            data.item(copy(frame.getItem())).rotation(frame.getRotation());

        if (hanging instanceof Painting painting)
            data.art(painting.getArt());

        return data.build();
    }

    @Nullable
    private ItemStack copy(@Nullable ItemStack item) {
        return item == null || item.getType() == Material.AIR ? null : item.clone();
    }
}
