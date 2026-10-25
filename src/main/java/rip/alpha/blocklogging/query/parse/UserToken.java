package rip.alpha.blocklogging.query.parse;

import rip.alpha.blocklogging.query.model.ParseContext;

import java.util.Set;

final class UserToken implements QueryToken {

    @Override
    public Set<String> prefixes() {
        return Set.of("u", "user", "users", "p", "player");
    }

    @Override
    public void apply(QueryDraft draft, String value, ParseContext context) {
        draft.getUsers().addAll(Values.split(value));
    }
}
