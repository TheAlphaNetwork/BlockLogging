package rip.alpha.blocklogging.rollback.message;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.text.Chat;

import java.util.StringJoiner;

@UtilityClass
class ChangeSummary {

    String of(long blocks, long hangings, long containers, long items, long entities) {
        StringJoiner joiner = new StringJoiner(Chat.MUTED + ", ");

        add(joiner, blocks, "blocks");
        add(joiner, hangings, "frames and paintings");
        add(joiner, containers, "container stacks");
        add(joiner, items, "player items");
        add(joiner, entities, "entities");

        return joiner.length() == 0 ? Chat.MUTED + "nothing" : joiner.toString();
    }

    private void add(StringJoiner joiner, long amount, String label) {

        if (amount > 0)
            joiner.add("%s%,d %s%s".formatted(Chat.ACCENT, amount, Chat.MUTED, label));
    }
}
