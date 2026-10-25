package rip.alpha.blocklogging.rollback.plan;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.EntityType;
import rip.alpha.blocklogging.record.codec.EntityMetaCodec;
import rip.alpha.blocklogging.rollback.model.Change;
import rip.alpha.blocklogging.rollback.model.EntityRemoval;
import rip.alpha.blocklogging.rollback.model.EntityRespawn;
import rip.alpha.blocklogging.rollback.model.KillHistory;
import rip.alpha.blocklogging.rollback.type.RollbackKind;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
@SuppressWarnings("deprecation")
class EntityPlanner {

    List<Change> plan(List<KillHistory> kills, RollbackKind kind) {
        List<Change> changes = new ArrayList<>(kills.size());

        for (KillHistory kill : kills) {

            if (kind == RollbackKind.RESTORE) {
                changes.add(new EntityRemoval(kill.position(), kill.respawned(), kill.id()));
                continue;
            }

            EntityType type = EntityType.fromId(kill.type());

            if (type != null && type.isAlive() && type.isSpawnable() && type != EntityType.PLAYER)
                changes.add(EntityRespawn.builder()
                        .position(kill.position())
                        .type(type)
                        .meta(EntityMetaCodec.decode(kill.meta()))
                        .id(kill.id())
                        .build());
        }

        return changes;
    }
}
