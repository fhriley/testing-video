package band.full.testing.video.generate.hevc;

import static band.full.testing.video.encoder.EncoderParameters.UHD4K_MAIN10;
import static band.full.testing.video.generate.GeneratorFactory.HEVC;

import band.full.testing.video.executor.GenerateVideo;
import band.full.testing.video.generate.basic.BasicSetupBase;

/**
 * @author Igor Malinin
 */
@GenerateVideo
public class BasicSetup2160pBT2020_10 extends BasicSetupBase {
    public BasicSetup2160pBT2020_10() {
        super(HEVC, UHD4K_MAIN10, "UHD4K/BT2020_10/Calibrate/Basic");
    }
}
