package rip.alpha.blocklogging.query.model;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public record ParseContext(@Nullable Location location, int maxRadius) {
}
