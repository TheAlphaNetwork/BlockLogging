package rip.alpha.blocklogging.query.describe;

import rip.alpha.blocklogging.query.model.LookupRow;

public interface RowDescriber {

    String describe(LookupRow row);
}
