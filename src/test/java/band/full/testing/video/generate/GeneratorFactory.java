package band.full.testing.video.generate;

import band.full.testing.video.encoder.DecoderY4M;
import band.full.testing.video.encoder.EncoderAVC;
import band.full.testing.video.encoder.EncoderHEVC;
import band.full.testing.video.encoder.EncoderParameters;
import band.full.testing.video.encoder.EncoderY4M;

import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public enum GeneratorFactory {
    AVC(EncoderAVC::encode),
    HEVC(EncoderHEVC::encode);

    public final Encoder encoder;

    GeneratorFactory(Encoder encoder) {
        this.encoder = encoder;
    }

    public void generate(String name, EncoderParameters ep,
            Consumer<EncoderY4M> ec, Consumer<DecoderY4M> dc) {
        encoder.encode(name, ep, ec);
        DecoderY4M.decode(name, ep, dc);
    }

    public <A> void generate(String name, EncoderParameters ep, A args,
            ParametrizedConsumer<EncoderY4M, A> ec,
            ParametrizedConsumer<DecoderY4M, A> dc) {
        encoder.encode(name, ep, e -> ec.accept(e, args));
        DecoderY4M.decode(name, ep, d -> dc.accept(d, args));
    }

    @FunctionalInterface
    interface Encoder {
        void encode(String name, EncoderParameters ep,
                Consumer<EncoderY4M> consumer);
    }

    @FunctionalInterface
    interface ParametrizedConsumer<T, A> {
        void accept(T t, A args);
    }
}
