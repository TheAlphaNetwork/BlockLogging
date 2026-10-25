package rip.alpha.blocklogging.query.model;

import java.util.List;

public record QueryInput(List<String> tokens) {

    public boolean isEmpty() {
        return this.tokens.isEmpty();
    }
}
