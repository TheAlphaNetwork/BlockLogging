package rip.alpha.blocklogging.query.describe;

import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.query.model.LookupRow;

final class SessionDescriber implements RowDescriber {

    @Override
    public String describe(LookupRow row) {
        return Chat.TEXT + (row.action() == 1 ? "logged in" : "logged out");
    }
}
