package band.full.test.video.generate.hevc.fhd;

import static band.full.test.video.generator.GeneratorFactory.HEVC;
import static band.full.video.encoder.EncoderParameters.FULLHD_MAIN8;

import band.full.test.video.executor.GenerateVideo;
import band.full.test.video.generator.Quants2DBase8;

/**
 * Testing color bands separation / quantization step uniformity.
 *
 * @author Igor Malinin
 */
@GenerateVideo
public class Quants2D1080pHEVC extends Quants2DBase8 {
    public Quants2D1080pHEVC() {
        super(HEVC, FULLHD_MAIN8, "FullHD/BT709/Quants2D", "U4K");
    }
}
