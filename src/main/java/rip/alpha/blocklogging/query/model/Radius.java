package rip.alpha.blocklogging.query.model;

import org.jetbrains.annotations.Nullable;

public sealed interface Radius permits GlobalRadius, WorldRadius, AreaRadius, ExactRadius {

    @Nullable
    String world();
}
