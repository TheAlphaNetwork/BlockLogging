package rip.alpha.blocklogging.query.model;

import org.jetbrains.annotations.Nullable;

public record GlobalRadius() implements Radius {

    @Override
    @Nullable
    public String world() {
        return null;
    }
}
