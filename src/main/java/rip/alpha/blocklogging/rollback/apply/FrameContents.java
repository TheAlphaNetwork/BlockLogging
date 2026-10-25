package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.rollback.model.ContainerChange;

@UtilityClass
class FrameContents {

    boolean apply(ContainerChange change) {
        ItemFrame frame = HangingEntities.find(change.position(), ItemFrame.class);

        if (frame == null)
            return false;

        ItemStack current = frame.getItem();

        boolean empty = current == null || current.getType() == Material.AIR;

        if (change.add() && empty) {
            frame.setItem(change.item().clone());
            return true;
        }

        if (!change.add() && !empty && current.isSimilar(change.item())) {
            frame.setItem(null);
            return true;
        }

        return false;
    }
}
