package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import rip.alpha.blocklogging.record.capture.TraitKeys;
import rip.alpha.blocklogging.record.model.EntityMeta;

import java.util.Arrays;
import java.util.Map;

@UtilityClass
class EntityTraitApplier {

    void apply(LivingEntity entity, EntityMeta meta) {

        if (meta.name() != null) {
            entity.setCustomName(meta.name());
            entity.setCustomNameVisible(true);
        }

        if (meta.baby() && entity instanceof Ageable ageable)
            ageable.setBaby();

        if (meta.baby() && entity instanceof Zombie zombie)
            zombie.setBaby(true);

        Map<String, String> traits = meta.traits();

        if (Boolean.parseBoolean(traits.get(TraitKeys.TAMED)) && entity instanceof Tameable tameable)
            tameable.setTamed(true);

        EntityKinds.apply(entity, traits);
        equip(entity.getEquipment(), meta.equipment());
    }

    private void equip(EntityEquipment equipment, ItemStack[] items) {

        if (equipment == null || items.length == 0)
            return;

        equipment.setArmorContents(Arrays.copyOf(items, items.length - 1));
        equipment.setItemInHand(items[items.length - 1]);
    }
}
