package rip.alpha.blocklogging.rollback.model;

public sealed interface Change permits BlockTarget, ContainerChange, EntityRespawn {
}
