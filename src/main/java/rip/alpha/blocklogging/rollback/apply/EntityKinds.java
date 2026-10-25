package rip.alpha.blocklogging.rollback.apply;

import lombok.experimental.UtilityClass;
import org.bukkit.DyeColor;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import rip.alpha.blocklogging.record.capture.TraitKeys;

import java.util.Map;

@UtilityClass
class EntityKinds {

    void apply(LivingEntity entity, Map<String, String> traits) {

        switch (entity) {
            case Sheep sheep -> {
                sheep.setColor(DyeColor.valueOf(traits.getOrDefault(TraitKeys.COLOR, DyeColor.WHITE.name())));
                sheep.setSheared(Boolean.parseBoolean(traits.get(TraitKeys.SHEARED)));
            }

            case Wolf wolf -> wolf.setCollarColor(DyeColor.valueOf(traits.getOrDefault(TraitKeys.COLOR, DyeColor.RED.name())));

            case Horse horse -> {
                horse.setVariant(Horse.Variant.valueOf(traits.getOrDefault(TraitKeys.VARIANT, Horse.Variant.HORSE.name())));
                horse.setColor(Horse.Color.valueOf(traits.getOrDefault(TraitKeys.HORSE_COLOR, Horse.Color.BROWN.name())));
                horse.setStyle(Horse.Style.valueOf(traits.getOrDefault(TraitKeys.HORSE_STYLE, Horse.Style.NONE.name())));
            }

            case Villager villager -> villager.setProfession(Villager.Profession.valueOf(trait(traits, TraitKeys.PROFESSION, "FARMER")));
            case Skeleton skeleton -> skeleton.setSkeletonType(Skeleton.SkeletonType.valueOf(trait(traits, TraitKeys.SKELETON, "NORMAL")));
            case Zombie zombie -> zombie.setVillager(Boolean.parseBoolean(traits.get(TraitKeys.VILLAGER)));
            case Creeper creeper -> creeper.setPowered(Boolean.parseBoolean(traits.get(TraitKeys.POWERED)));
            case Slime slime -> slime.setSize(Integer.parseInt(traits.getOrDefault(TraitKeys.SIZE, "1")));
            case Ocelot ocelot -> ocelot.setCatType(Ocelot.Type.valueOf(traits.getOrDefault(TraitKeys.CAT, "WILD_OCELOT")));
            case Rabbit rabbit -> rabbit.setRabbitType(Rabbit.Type.valueOf(traits.getOrDefault(TraitKeys.RABBIT, "BROWN")));
            case Pig pig -> pig.setSaddle(Boolean.parseBoolean(traits.get(TraitKeys.SADDLE)));
            default -> {
            }
        }
    }

    private String trait(Map<String, String> traits, String key, String fallback) {
        return traits.getOrDefault(key, fallback);
    }
}
