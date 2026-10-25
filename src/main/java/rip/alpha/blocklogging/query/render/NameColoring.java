package rip.alpha.blocklogging.query.render;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.hook.RankCache;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.blocklogging.query.model.NameColors;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class NameColoring {

    public NameColors of(LookupPage page) {
        Map<UUID, String> colors = new HashMap<>();

        for (LookupRow row : page.rows()) {

            if (row.uuid() != null)
                colors.computeIfAbsent(row.uuid(), RankCache::color);
        }

        return new NameColors(Map.copyOf(colors));
    }
}
