package band.full.test.video.generator;

import static java.lang.Boolean.getBoolean;
import static java.lang.System.getProperty;
import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;

import band.full.video.encoder.EncoderAVC;
import band.full.video.encoder.EncoderHEVC;
import band.full.video.encoder.EncoderParameters;
import band.full.video.encoder.EncoderY4M;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Igor Malinin
 */
public enum GeneratorFactory {
    AVC(EncoderAVC::encode, "isom" /* avc1 */, "H.264-AVC") {
        @Override
        EncoderParameters lossless(EncoderParameters template) {
            return template.withEncoderOptions(prepend(template.encoderOptions,
                    "--qp", "0"));
        }

        @Override
        EncoderParameters bluray(EncoderParameters template) {
            return template.withEncoderOptions(prepend(template.encoderOptions,
                    // "--tune", "film", "--slices", "4",
                    "--bluray-compat", "--level", "4.1",
                    "--vbv-maxrate", "40000", "--vbv-bufsize", "30000",
                    "--crf", "1", "--qpmax", "4", "--psnr", "--ssim"));
        }
    },

    HEVC(EncoderHEVC::encode, "hvc1", "H.265-HEVC") {
        @Override
        EncoderParameters lossless(EncoderParameters template) {
            return template.withEncoderOptions(prepend(template.encoderOptions,
                    "--lossless"));
        }

        @Override
        EncoderParameters bluray(EncoderParameters template) {
            return template.withEncoderOptions(prepend(template.encoderOptions,
                    // "--uhd-bd",
                    "--level-idc", "5.1", "--high-tier", "--hrd",
                    "--vbv-maxrate", "160000", "--vbv-bufsize", "160000",
                    "--crf", "0", "--qpmax", "4", "--cu-lossless",
                    "--no-rskip", "--psnr", "--ssim"));
        }
    };

    abstract EncoderParameters lossless(EncoderParameters template);

    abstract EncoderParameters bluray(EncoderParameters template);

    static String[] prepend(List<String> options, String... args) {
        return concat(stream(args), options.stream()).toArray(String[]::new);
    }

    enum IO {
        PIPE, TEMP_FILE, KEEP_FILE;

        private static IO get(String property) {
            switch (getProperty(property, "pipe")) {
                case "temp":
                    return TEMP_FILE;
                case "keep":
                    return KEEP_FILE;
            }
            return PIPE;
        }

        boolean isPipe() {
            return this == PIPE;
        }

        boolean isKeepFile() {
            return this == IO.KEEP_FILE;
        }
    }

    final IO OUT = IO.get("encoder.file.annexb");

    public final Encoder encoder;
    public final String brand;
    public final String folder;

    public static final boolean LOSSLESS = getBoolean("encoder.lossless");

    GeneratorFactory(Encoder encoder, String brand, String folder) {
        this.encoder = encoder;
        this.brand = brand;
        this.folder = folder;
    }

    public File greet(String folder, String name) {
        System.out.println(LOSSLESS
                ? "Generating lossless encode..."
                : "Generating normal encode...");

        return new File("target/video-"
                + (LOSSLESS ? "lossless/" : "main/")
                + folder);
    }

    private EncoderParameters enrich(EncoderParameters ep) {
        return LOSSLESS ? lossless(ep) : bluray(ep);
    }

    public String encode(File dir, String name,
            EncoderParameters ep, Consumer<EncoderY4M> ec)
            throws IOException, InterruptedException {
        return encoder.encode(dir, name, enrich(ep), ec);
    }

    @FunctionalInterface
    interface Encoder {
        String encode(File dir, String name, EncoderParameters ep,
                Consumer<EncoderY4M> consumer)
                throws IOException, InterruptedException;
    }
}
