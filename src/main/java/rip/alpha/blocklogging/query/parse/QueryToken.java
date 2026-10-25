package rip.alpha.blocklogging.query.parse;

import rip.alpha.blocklogging.query.model.ParseContext;

import java.util.Set;

interface QueryToken {

    Set<String> prefixes();

    void apply(QueryDraft draft, String value, ParseContext context);
}
