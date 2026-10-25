package rip.alpha.blocklogging.query.parse;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.query.model.PageRequest;
import rip.alpha.blocklogging.query.model.QueryInput;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class PageRequests {

    private final Pattern PAGE = Pattern.compile("^(\\d{1,6})(?::(\\d{1,3}))?$");

    @Nullable
    public PageRequest parse(QueryInput input) {

        if (input.tokens().size() != 1)
            return null;

        Matcher matcher = PAGE.matcher(input.tokens().getFirst());

        if (!matcher.matches())
            return null;

        int size = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));

        return new PageRequest(Integer.parseInt(matcher.group(1)), size);
    }
}
