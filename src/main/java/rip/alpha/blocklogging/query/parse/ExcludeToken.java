package rip.alpha.blocklogging.query.parse;

import rip.alpha.blocklogging.query.model.ParseContext;
import rip.alpha.blocklogging.query.model.TypeRef;

import java.util.Set;

final class ExcludeToken implements QueryToken {

    @Override
    public Set<String> prefixes() {
        return Set.of("e", "exclude");
    }

    @Override
    public void apply(QueryDraft draft, String value, ParseContext context) {

        for (String part : Values.split(value)) {
            TypeRef type = TypeNames.resolve(part);

            if (type != null)
                TypeNames.add(type, draft.getExcludeBlocks(), draft.getExcludeEntities());
            else
                draft.getExcludedUsers().add(part);
        }
    }
}
