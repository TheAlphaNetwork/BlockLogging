package rip.alpha.blocklogging.query.parameter;

import org.bukkit.command.CommandSender;
import rip.alpha.blocklogging.query.model.QueryInput;
import rip.alpha.libraries.command.parameter.ParameterType;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class QueryInputType implements ParameterType<QueryInput> {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    @Override
    public QueryInput parse(CommandSender sender, String input) {
        List<String> tokens = Arrays.stream(WHITESPACE.split(input.trim()))
                .filter(token -> !token.isEmpty())
                .toList();

        return new QueryInput(tokens);
    }

    @Override
    public List<String> complete(CommandSender sender, String prefix) {
        return QueryCompletions.complete(prefix);
    }
}
