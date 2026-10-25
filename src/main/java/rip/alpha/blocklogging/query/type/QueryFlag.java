package rip.alpha.blocklogging.query.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum QueryFlag {

    PREVIEW("#preview"),
    COUNT("#count"),
    VERBOSE("#verbose"),
    SILENT("#silent"),
    OPTIMIZE("#optimize");

    private final String token;

    @Nullable
    public static QueryFlag of(String token) {

        for (QueryFlag flag : values()) {

            if (flag.token.equalsIgnoreCase(token))
                return flag;
        }

        return null;
    }
}
