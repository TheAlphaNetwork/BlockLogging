package rip.alpha.blocklogging.query.describe;

import lombok.RequiredArgsConstructor;
import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.model.LookupRow;

@RequiredArgsConstructor
final class TextDescriber implements RowDescriber {

    private static final String LINE_BREAK = "\n";
    private static final String SEPARATOR = " | ";

    private final String verb;

    @Override
    public String describe(LookupRow row) {
        String text = row.text() == null ? "" : row.text().replace(LINE_BREAK, SEPARATOR);

        return Chat.TEXT + this.verb + " " + Chat.MUTED + text;
    }
}
