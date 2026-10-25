package rip.alpha.blocklogging.record.model;

import rip.alpha.blocklogging.common.model.Actor;
import rip.alpha.blocklogging.common.model.BlockPosition;

import java.util.concurrent.TimeUnit;

public record LogBase(long time, Actor actor, BlockPosition position) {

    public static LogBase now(Actor actor, BlockPosition position) {
        return new LogBase(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()), actor, position);
    }
}
