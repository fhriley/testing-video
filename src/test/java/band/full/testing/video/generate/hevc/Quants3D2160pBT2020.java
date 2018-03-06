package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.UHD4K_MAIN10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.base.Quants3DBase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants3D2160pBT2020 extends Quants3DBase {
    public Quants3D2160pBT2020() {
        super(HEVC, UHD4K_MAIN10, "UHD4K/BT2020/Quants3D",
                "Quants3D1080p4K2020");
    }

    @ParameterizedTest
    @MethodSource("params")
    public void quants(Args args) {
        generate(args);
    }
}
