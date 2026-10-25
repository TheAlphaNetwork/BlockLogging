package rip.alpha.blocklogging.query.parse;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.query.model.TypeRef;

import java.util.Locale;
import java.util.Set;

@UtilityClass
@SuppressWarnings("deprecation")
public class TypeNames {

    private final String NAMESPACE = "minecraft:";

    @Nullable
    public TypeRef resolve(String name) {
        String clean = name.toLowerCase(Locale.ROOT).replace(NAMESPACE, "");

        Material material = Material.matchMaterial(clean);

        if (material != null)
            return new TypeRef(material.getId(), false);

        for (EntityType type : EntityType.values()) {

            if (type.getTypeId() > 0 && (type.name().equalsIgnoreCase(clean) || clean.equalsIgnoreCase(type.getName())))
                return new TypeRef(type.getTypeId(), true);
        }

        return null;
    }

    public void add(TypeRef type, Set<Integer> blocks, Set<Integer> entities) {
        (type.entity() ? entities : blocks).add(type.id());
    }
}
