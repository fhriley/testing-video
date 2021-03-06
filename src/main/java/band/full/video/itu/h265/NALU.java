package band.full.video.itu.h265;

import static band.full.core.ArrayMath.toHexString;

import band.full.video.itu.nal.RbspReader;
import band.full.video.itu.nal.RbspWriter;

import java.io.PrintStream;

/**
 * Generic NAL Unit for all unknow types, stores RBSP bytes.
 *
 * @author Igor Malinin
 */
public class NALU extends NALUnit {
    public byte[] bytes;

    public NALU(NALUnitType type) {
        super(type);
    }

    public NALU(NALUnitType type, byte[] bytes) {
        super(type);
        this.bytes = bytes;
    }

    @Override
    public void read(H265Context context, RbspReader reader) {
        bytes = reader.readTrailingBits();
    }

    @Override
    public void write(H265Context context, RbspWriter writer) {
        writer.writeTrailingBits(bytes);
    }

    @Override
    public void print(H265Context context, PrintStream ps) {
        ps.println("    size: " + bytes.length);
        if (bytes.length <= 256) {
            ps.print("    bytes: 0x");
            ps.println(toHexString(bytes));
        }
    }
}
