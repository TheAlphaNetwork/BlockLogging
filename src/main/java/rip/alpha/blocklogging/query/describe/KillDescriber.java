package rip.alpha.blocklogging.query.describe;

import rip.alpha.blocklogging.common.text.Chat;
import rip.alpha.blocklogging.common.text.TypeLabels;
import rip.alpha.blocklogging.query.model.LookupRow;

final class KillDescriber implements RowDescriber {

    @Override
    public String describe(LookupRow row) {
        return Chat.TEXT + "killed " + Chat.ACCENT + TypeLabels.entity(row.type());
    }
}
