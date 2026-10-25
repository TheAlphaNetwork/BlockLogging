package rip.alpha.blocklogging.query.parse;

import rip.alpha.blocklogging.query.exception.QueryException;
import rip.alpha.blocklogging.query.model.ParseContext;
import rip.alpha.blocklogging.query.model.TypeRef;

import java.util.Set;

final class IncludeToken implements QueryToken {

    @Override
    public Set<String> prefixes() {
        return Set.of("i", "include", "b", "block", "blocks");
    }

    @Override
    public void apply(QueryDraft draft, String value, ParseContext context) {

        for (String part : Values.split(value)) {
            TypeRef type = TypeNames.resolve(part);

            if (type == null)
                throw new QueryException("Unknown block or entity \"%s\".".formatted(part));

            TypeNames.add(type, draft.getIncludeBlocks(), draft.getIncludeEntities());
        }
    }
}
