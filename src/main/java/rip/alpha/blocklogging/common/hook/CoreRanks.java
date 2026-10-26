package rip.alpha.blocklogging.common.hook;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.model.RankSnapshot;
import rip.alpha.core.api.Core;
import rip.alpha.core.api.profile.PlayerProfile;
import rip.alpha.core.api.profile.ProfileService;
import rip.alpha.core.api.rank.Rank;
import rip.alpha.libraries.text.Colors;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@UtilityClass
class CoreRanks {

    private final long TIMEOUT_MILLIS = 2_000;

    CompletableFuture<RankSnapshot> load(UUID uuid, long expires) {
        ProfileService profiles = Core.api().profiles();
        Optional<PlayerProfile> online = profiles.online(uuid);

        if (online.isPresent())
            return CompletableFuture.completedFuture(snapshot(online.get().rank(), expires));

        return profiles.load(uuid)
                .orTimeout(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .thenApply(profile -> profile.map(found -> snapshot(found.rank(), expires)).orElse(null));
    }

    private RankSnapshot snapshot(Rank rank, long expires) {
        String color = rank.color() == null || rank.color().isEmpty() ? RankCache.PLAYER : Colors.translate(rank.color());

        return new RankSnapshot(color, rank.weight(), expires);
    }
}
