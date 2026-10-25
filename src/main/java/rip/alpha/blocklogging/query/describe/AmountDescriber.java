package rip.alpha.blocklogging.query.describe;

import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.common.text.TypeLabels;
import rip.alpha.blocklogging.query.model.LookupRow;

@RequiredArgsConstructor
final class AmountDescriber implements RowDescriber {

    private final String negative;
    private final String positive;

    @Override
    public String describe(LookupRow row) {
        LookupRow replaced = row.replaced();

        if (replaced != null)
            return Replacements.describe(label(replaced), label(row));

        String verb = row.action() == 1 ? Chat.GOOD + this.positive : Chat.BAD + this.negative;

        return "%s %s%s".formatted(verb, Chat.ACCENT, label(row));
    }

    private String label(LookupRow row) {
        return "%dx %s".formatted(row.amount(), TypeLabels.block(row.type(), row.data()));
    }
}
