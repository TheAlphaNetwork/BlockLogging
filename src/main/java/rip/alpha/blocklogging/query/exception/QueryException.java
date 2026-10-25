package rip.alpha.blocklogging.query.exception;

public final class QueryException extends RuntimeException {

    public QueryException(String message) {
        super(message, null, false, false);
    }
}
