package rip.alpha.blocklogging.query.describe;

import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.common.text.TypeLabels;
import rip.alpha.blocklogging.query.model.LookupRow;

final class BlockDescriber implements RowDescriber {

    private static final String[] VERBS = {"broke", "placed", "clicked"};

    @Override
    public String describe(LookupRow row) {
        String verb = row.action() >= 0 && row.action() < VERBS.length ? VERBS[row.action()] : "changed";

        return Chat.TEXT + verb + " " + Chat.ACCENT + TypeLabels.block(row.type(), row.data());
    }
}
