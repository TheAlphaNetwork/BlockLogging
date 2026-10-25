package rip.alpha.blocklogging.record.capture;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.record.model.EntityMeta;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class EntityTraits {

    @Nullable
    public EntityMeta capture(LivingEntity entity) {

        if (entity instanceof Player)
            return null;

        Map<String, String> traits = new HashMap<>();

        boolean baby = entity instanceof Ageable ageable && !ageable.isAdult() || entity instanceof Zombie zombie && zombie.isBaby();

        colors(entity, traits);
        kinds(entity, traits);

        if (entity instanceof Tameable tameable && tameable.isTamed())
            traits.put(TraitKeys.TAMED, "true");

        return EntityMeta.builder()
                .name(entity.getCustomName())
                .baby(baby)
                .traits(Map.copyOf(traits))
                .equipment(equipment(entity.getEquipment()))
                .build();
    }

    private void colors(LivingEntity entity, Map<String, String> traits) {

        switch (entity) {
            case Sheep sheep -> {
                traits.put(TraitKeys.COLOR, sheep.getColor().name());
                traits.put(TraitKeys.SHEARED, String.valueOf(sheep.isSheared()));
            }

            case Wolf wolf -> traits.put(TraitKeys.COLOR, wolf.getCollarColor().name());

            case Horse horse -> {
                traits.put(TraitKeys.VARIANT, horse.getVariant().name());
                traits.put(TraitKeys.HORSE_COLOR, horse.getColor().name());
                traits.put(TraitKeys.HORSE_STYLE, horse.getStyle().name());
            }

            default -> {
            }
        }
    }

    private void kinds(LivingEntity entity, Map<String, String> traits) {

        switch (entity) {
            case Villager villager -> traits.put(TraitKeys.PROFESSION, villager.getProfession().name());
            case Skeleton skeleton -> traits.put(TraitKeys.SKELETON, skeleton.getSkeletonType().name());
            case Zombie zombie -> traits.put(TraitKeys.VILLAGER, String.valueOf(zombie.isVillager()));
            case Creeper creeper -> traits.put(TraitKeys.POWERED, String.valueOf(creeper.isPowered()));
            case Slime slime -> traits.put(TraitKeys.SIZE, String.valueOf(slime.getSize()));
            case Ocelot ocelot -> traits.put(TraitKeys.CAT, ocelot.getCatType().name());
            case Rabbit rabbit -> traits.put(TraitKeys.RABBIT, rabbit.getRabbitType().name());
            case Pig pig -> traits.put(TraitKeys.SADDLE, String.valueOf(pig.hasSaddle()));
            default -> {
            }
        }
    }

    private ItemStack[] equipment(@Nullable EntityEquipment equipment) {

        if (equipment == null)
            return new ItemStack[0];

        ItemStack[] armor = equipment.getArmorContents();
        ItemStack[] items = new ItemStack[armor.length + 1];

        for (int index = 0; index < armor.length; index++)
            items[index] = armor[index] == null ? null : armor[index].clone();

        items[armor.length] = equipment.getItemInHand() == null ? null : equipment.getItemInHand().clone();

        return items;
    }
}
