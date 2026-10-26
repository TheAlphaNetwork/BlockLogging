package rip.alpha.blocklogging.query.render;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.model.Ranks;
import rip.alpha.blocklogging.query.model.LookupPage;
import rip.alpha.blocklogging.query.model.LookupRow;
import rip.alpha.blocklogging.query.model.NameColors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class NameColoring {

    public Set<UUID> subjects(LookupPage page) {
        Set<UUID> subjects = new HashSet<>();

        for (LookupRow row : page.rows()) {

            if (row.uuid() != null)
                subjects.add(row.uuid());
        }

        return subjects;
    }

    public NameColors of(LookupPage page, Ranks ranks) {
        Map<UUID, String> colors = new HashMap<>();

        for (LookupRow row : page.rows()) {

            if (row.uuid() != null)
                colors.computeIfAbsent(row.uuid(), uuid -> ranks.of(uuid).color());
        }

        return new NameColors(Map.copyOf(colors));
    }
}
