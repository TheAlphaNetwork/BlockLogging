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
        String verb = row.action() == 1 ? Chat.GOOD + this.positive : Chat.BAD + this.negative;

        return "%s %s%dx %s".formatted(verb, Chat.ACCENT, row.amount(), TypeLabels.block(row.type(), row.data()));
    }
}
