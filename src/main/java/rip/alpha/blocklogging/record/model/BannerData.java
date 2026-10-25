package rip.alpha.blocklogging.record.model;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;

import java.util.List;

public record BannerData(DyeColor base, List<Pattern> patterns) implements BlockMeta {
}
