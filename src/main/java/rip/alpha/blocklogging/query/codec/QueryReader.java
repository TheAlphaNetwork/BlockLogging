package rip.alpha.blocklogging.query.codec;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.TypeFilter;
import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.blocklogging.query.type.QueryFlag;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class QueryReader {

    @Nullable
    public Query decode(byte @Nullable [] data) {

        if (data == null || data.length == 0)
            return null;

        try (DataInputStream input = new DataInputStream(new ByteArrayInputStream(data))) {

            if (input.readByte() != QueryCodec.VERSION)
                return null;

            return Query.builder()
                    .users(strings(input))
                    .excludedUsers(strings(input))
                    .since(input.readLong())
                    .until(input.readLong())
                    .radius(RadiusCodec.read(input))
                    .actions(actions(input))
                    .include(types(input))
                    .exclude(types(input))
                    .flags(flags(input))
                    .build();

        } catch (IOException | IllegalArgumentException _) {
            return null;
        }
    }

    private List<String> strings(DataInputStream input) throws IOException {
        int size = input.readInt();

        List<String> values = new ArrayList<>(size);

        for (int index = 0; index < size; index++)
            values.add(input.readUTF());

        return List.copyOf(values);
    }

    private Set<Action> actions(DataInputStream input) throws IOException {
        Set<Action> actions = EnumSet.noneOf(Action.class);

        for (String name : strings(input))
            actions.add(Action.valueOf(name));

        return Set.copyOf(actions);
    }

    private Set<QueryFlag> flags(DataInputStream input) throws IOException {
        Set<QueryFlag> flags = EnumSet.noneOf(QueryFlag.class);

        for (String name : strings(input))
            flags.add(QueryFlag.valueOf(name));

        return Set.copyOf(flags);
    }

    private TypeFilter types(DataInputStream input) throws IOException {
        return new TypeFilter(ints(input), ints(input));
    }

    private Set<Integer> ints(DataInputStream input) throws IOException {
        int size = input.readInt();

        Set<Integer> values = new HashSet<>(size);

        for (int index = 0; index < size; index++)
            values.add(input.readInt());

        return Set.copyOf(values);
    }
}
