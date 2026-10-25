package rip.alpha.blocklogging.query.codec;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.query.model.Query;
import rip.alpha.blocklogging.query.model.TypeFilter;
import rip.alpha.blocklogging.query.type.Action;
import rip.alpha.blocklogging.query.type.QueryFlag;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

@UtilityClass
public class QueryCodec {

    public final int VERSION = 1;

    public byte[] encode(Query query) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try (DataOutputStream output = new DataOutputStream(bytes)) {
            output.writeByte(VERSION);

            strings(output, query.users());
            strings(output, query.excludedUsers());

            output.writeLong(query.since());
            output.writeLong(query.until());

            RadiusCodec.write(output, query.radius());

            strings(output, query.actions().stream().map(Action::name).toList());

            types(output, query.include());
            types(output, query.exclude());

            strings(output, query.flags().stream().map(QueryFlag::name).toList());
        }

        return bytes.toByteArray();
    }

    private void strings(DataOutputStream output, Collection<String> values) throws IOException {
        output.writeInt(values.size());

        for (String value : values)
            output.writeUTF(value);
    }

    private void types(DataOutputStream output, TypeFilter filter) throws IOException {
        ints(output, filter.blocks());
        ints(output, filter.entities());
    }

    private void ints(DataOutputStream output, Collection<Integer> values) throws IOException {
        output.writeInt(values.size());

        for (int value : values)
            output.writeInt(value);
    }
}
