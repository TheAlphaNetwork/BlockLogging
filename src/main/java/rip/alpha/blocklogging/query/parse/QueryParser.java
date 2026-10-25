package rip.alpha.blocklogging.query.parse;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.ParseContext;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.QueryInput;
import rip.alpha.blocklogging.query.model.Radius;
import rip.alpha.blocklogging.query.type.QueryFlag;

import java.util.List;
import java.util.Locale;

@UtilityClass
public class QueryParser {

    private final List<QueryToken> TOKENS = List.of(
            new UserToken(),
            new TimeToken(),
            new RadiusToken(),
            new ActionToken(),
            new IncludeToken(),
            new ExcludeToken()
    );

    public Query parse(QueryInput input, ParseContext context, Radius fallback) {
        QueryDraft draft = new QueryDraft();

        for (String token : input.tokens())
            apply(draft, token, context);

        return draft.build(fallback);
    }

    private void apply(QueryDraft draft, String token, ParseContext context) {
        QueryFlag flag = QueryFlag.of(token);

        if (flag != null) {
            draft.getFlags().add(flag);
            return;
        }

        int split = token.indexOf(':');

        if (split <= 0 || split == token.length() - 1)
            throw new QueryException("Unknown argument \"%s\".".formatted(token));

        String prefix = token.substring(0, split).toLowerCase(Locale.ROOT);
        String value = token.substring(split + 1);

        for (QueryToken handler : TOKENS) {

            if (handler.prefixes().contains(prefix)) {
                handler.apply(draft, value, context);
                return;
            }
        }

        throw new QueryException("Unknown argument \"%s\".".formatted(token));
    }
}
