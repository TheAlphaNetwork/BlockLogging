package rip.alpha.blocklogging.query.describe;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.text.Chat;

@UtilityClass
class Replacements {

    String describe(String before, String after) {
        return "%sreplaced %s%s %swith %s%s".formatted(Chat.SWAP, Chat.ACCENT, before, Chat.MUTED, Chat.ACCENT, after);
    }
}
