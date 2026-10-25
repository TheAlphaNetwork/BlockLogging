package rip.alpha.blocklogging.query.parse;

import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.ParseContext;
import rip.alpha.blocklogging.query.type.Action;

import java.util.Set;

final class ActionToken implements QueryToken {

    @Override
    public Set<String> prefixes() {
        return Set.of("a", "action", "actions");
    }

    @Override
    public void apply(QueryDraft draft, String value, ParseContext context) {

        for (String part : Values.split(value)) {
            Action action = Action.of(part);

            if (action == null)
                throw new QueryException("Unknown action \"%s\".".formatted(part));

            draft.getActions().add(action);
        }
    }
}
