package rip.alpha.blocklogging.query.codec;

import lombok.experimental.UtilityClass;
import rip.alpha.blocklogging.common.model.BlockPosition;
import rip.alpha.blocklogging.query.model.AreaRadius;
import rip.alpha.blocklogging.query.model.CuboidRadius;
import rip.alpha.blocklogging.query.model.ExactRadius;
import rip.alpha.blocklogging.query.model.GlobalRadius;
import rip.alpha.blocklogging.query.model.Radius;
import rip.alpha.blocklogging.query.model.WorldRadius;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@UtilityClass
class RadiusCodec {

    private final int GLOBAL = 0;
    private final int WORLD = 1;
    private final int AREA = 2;
    private final int CUBOID = 3;
    private final int EXACT = 4;

    void write(DataOutputStream output, Radius radius) throws IOException {

        switch (radius) {
            case GlobalRadius _ -> output.writeByte(GLOBAL);

            case WorldRadius world -> {
                output.writeByte(WORLD);
                output.writeUTF(world.world());
            }

            case AreaRadius area -> {
                output.writeByte(AREA);
                output.writeUTF(area.world());
                ints(output, area.x(), area.z(), area.size());
            }

            case CuboidRadius cuboid -> {
                output.writeByte(CUBOID);
                output.writeUTF(cuboid.world());
                ints(output, cuboid.minX(), cuboid.minY(), cuboid.minZ(), cuboid.maxX(), cuboid.maxY(), cuboid.maxZ());
            }

            case ExactRadius exact -> {
                output.writeByte(EXACT);
                output.writeUTF(exact.position().world());
                ints(output, exact.position().x(), exact.position().y(), exact.position().z());
            }
        }
    }

    Radius read(DataInputStream input) throws IOException {
        return switch (input.readByte()) {
            case WORLD -> new WorldRadius(input.readUTF());
            case AREA -> area(input);
            case CUBOID -> cuboid(input);
            case EXACT -> new ExactRadius(position(input));
            default -> new GlobalRadius();
        };
    }

    private AreaRadius area(DataInputStream input) throws IOException {
        return AreaRadius.builder()
                .world(input.readUTF())
                .x(input.readInt())
                .z(input.readInt())
                .size(input.readInt())
                .build();
    }

    private BlockPosition position(DataInputStream input) throws IOException {
        return BlockPosition.builder()
                .world(input.readUTF())
                .x(input.readInt())
                .y(input.readInt())
                .z(input.readInt())
                .build();
    }

    private CuboidRadius cuboid(DataInputStream input) throws IOException {
        return CuboidRadius.builder()
                .world(input.readUTF())
                .minX(input.readInt())
                .minY(input.readInt())
                .minZ(input.readInt())
                .maxX(input.readInt())
                .maxY(input.readInt())
                .maxZ(input.readInt())
                .build();
    }

    private void ints(DataOutputStream output, int... values) throws IOException {

        for (int value : values)
            output.writeInt(value);
    }
}
