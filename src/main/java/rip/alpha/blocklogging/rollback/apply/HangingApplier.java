package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import rip.alpha.blocklogging.record.model.HangingData;
import rip.alpha.blocklogging.rollback.model.HangingChange;

@UtilityClass
public class HangingApplier {

    public boolean apply(HangingChange change) {
        HangingData data = change.data();

        Class<? extends Hanging> type = data.type() == EntityType.ITEM_FRAME ? ItemFrame.class : Painting.class;

        Hanging existing = HangingEntities.find(change.position(), type);

        if (!change.spawn()) {

            if (existing == null)
                return false;

            existing.remove();
            return true;
        }

        if (existing != null)
            return false;

        Block block = change.position().block();

        if (block == null)
            return false;

        Hanging hanging = block.getWorld().spawn(block.getLocation(), type);

        hanging.setFacingDirection(data.facing(), true);
        decorate(hanging, data);

        return true;
    }

    private void decorate(Hanging hanging, HangingData data) {

        if (hanging instanceof ItemFrame frame) {
            frame.setItem(data.item());

            if (data.rotation() != null)
                frame.setRotation(data.rotation());
        }

        if (hanging instanceof Painting painting && data.art() != null)
            painting.setArt(data.art(), true);
    }
}
