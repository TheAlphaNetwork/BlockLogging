package rip.alpha.blocklogging.query.parse;

import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.ParseContext;
import rip.alpha.libraries.time.Durations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

final class TimeToken implements QueryToken {

    @Override
    public Set<String> prefixes() {
        return Set.of("t", "time");
    }

    @Override
    public void apply(QueryDraft draft, String value, ParseContext context) {
        long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        int dash = value.indexOf('-');

        if (dash < 0) {
            draft.setSince(now - seconds(value));
            return;
        }

        long first = seconds(value.substring(0, dash));
        long second = seconds(value.substring(dash + 1));

        draft.setSince(now - Math.max(first, second));
        draft.setUntil(now - Math.min(first, second));
    }

    private long seconds(String value) {
        long millis = Durations.parse(value);

        if (millis <= 0)
            throw new QueryException("Invalid time \"%s\", use something like t:2w5d7h.".formatted(value));

        return TimeUnit.MILLISECONDS.toSeconds(millis);
    }
}
