package rip.alpha.blocklogging.rollback.model;

import rip.alpha.blocklogging.storage.type.LogTable;

import java.util.List;

public sealed interface Change permits BlockTarget, HangingChange, ContainerChange, ItemChange, EntityRespawn, EntityRemoval {

    LogTable table();

    List<String> ids();
}
