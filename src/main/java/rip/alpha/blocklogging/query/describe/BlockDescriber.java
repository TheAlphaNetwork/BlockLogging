package rip.alpha.blocklogging.query.describe;

import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.common.text.TypeLabels;
import rip.alpha.blocklogging.query.model.LookupRow;

final class BlockDescriber implements RowDescriber {

    private static final String[] VERBS = {Chat.BAD + "broke", Chat.GOOD + "placed", Chat.MUTED + "clicked"};

    @Override
    public String describe(LookupRow row) {
        LookupRow replaced = row.replaced();

        if (replaced != null)
            return Replacements.describe(label(replaced), label(row));

        String verb = row.action() >= 0 && row.action() < VERBS.length ? VERBS[row.action()] : Chat.MUTED + "changed";

        return verb + " " + Chat.ACCENT + label(row);
    }

    private String label(LookupRow row) {
        return TypeLabels.block(row.type(), row.data());
    }
}
