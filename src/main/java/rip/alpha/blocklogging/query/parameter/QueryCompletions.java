package rip.alpha.blocklogging.query.parameter;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.blocklogging.query.type.QueryFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@UtilityClass
class QueryCompletions {

    private final List<String> KEYS = List.of("u:", "t:", "r:", "a:", "i:", "e:");
    private final List<String> SYSTEM_USERS = List.of("#tnt", "#creeper", "#fire", "#lava", "#water", "#piston", "#hopper", "#decay");
    private final List<String> TIMES = List.of("30m", "1h", "6h", "12h", "1d", "3d", "1w", "2w");

    List<String> complete(String prefix) {
        int split = prefix.indexOf(':');

        if (split < 0)
            return Stream.concat(KEYS.stream(), Arrays.stream(QueryFlag.values()).map(QueryFlag::getToken)).toList();

        String key = prefix.substring(0, split + 1);
        String values = prefix.substring(split + 1);

        int comma = values.lastIndexOf(',');

        String head = key + values.substring(0, comma + 1);
        String current = values.substring(comma + 1).toLowerCase(Locale.ROOT);

        List<String> results = new ArrayList<>();

        for (String candidate : candidates(key.toLowerCase(Locale.ROOT))) {

            if (candidate.toLowerCase(Locale.ROOT).startsWith(current))
                results.add(head + candidate);
        }

        return results;
    }

    private List<String> candidates(String key) {
        return switch (key) {
            case "u:", "user:" -> Stream.concat(Bukkit.getOnlinePlayers().stream().map(Player::getName), SYSTEM_USERS.stream()).toList();
            case "a:", "action:" -> Arrays.stream(Action.values()).map(Action::getToken).toList();
            case "t:", "time:" -> TIMES;
            case "r:", "radius:" -> radii();
            case "i:", "e:", "include:", "exclude:" -> materials();
            default -> List.of();
        };
    }

    private List<String> radii() {
        return Stream.concat(Stream.of("#global", "5", "10", "25"), Bukkit.getWorlds().stream().map(QueryCompletions::world)).toList();
    }

    private List<String> materials() {
        return Arrays.stream(Material.values())
                .map(material -> material.name().toLowerCase(Locale.ROOT))
                .toList();
    }

    private String world(World world) {
        return "#" + world.getName();
    }
}
