package rip.alpha.blocklogging.record.model;

public sealed interface BlockMeta permits ContainerMeta, SignMeta, SpawnerMeta, SkullData, BannerData, HangingData {
}
