package band.full.test.video.executor;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertFalse;

import band.full.core.Window;
import band.full.video.buffer.FrameBuffer;
import band.full.video.buffer.Plane;

import java.util.Arrays;

public class FrameVerifier {
    // Verifiers for FrameBuffer

    public static void verify(FrameBuffer expected, FrameBuffer actual) {
        verify(expected.Y, actual.Y);
        verify(expected.U, actual.U);
        verify(expected.V, actual.V);
    }

    public static void verify(FrameBuffer expected, FrameBuffer actual,
            int deviation,
            double maxMisses) {
        verify(expected.Y, actual.Y, deviation, maxMisses);
        verify(expected.U, actual.U, deviation, maxMisses);
        verify(expected.V, actual.V, deviation, maxMisses);
    }

    public static void verify(FrameBuffer expected, FrameBuffer actual,
            int deviation,
            int maxMisses) {
        verify(expected.Y, actual.Y, deviation, maxMisses);
        verify(expected.U, actual.U, deviation, maxMisses);
        verify(expected.V, actual.V, deviation, maxMisses);
    }

    public static void verifyRect(int[] yuvExpected, FrameBuffer actual,
            int x, int y, int w, int h) {
        verifyRect(yuvExpected[0], yuvExpected[1], yuvExpected[2],
                actual, x, y, w, h);
    }

    public static void verifyRect(int[] yuvExpected, FrameBuffer actual,
            int x, int y, int w, int h,
            int deviation, double maxMisses) {
        verifyRect(yuvExpected[0], yuvExpected[1], yuvExpected[2],
                actual, x, y, w, h, deviation, maxMisses);
    }

    public static void verifyRect(int[] yuvExpected,
            FrameBuffer actual, Window window,
            int deviation, int maxMisses) {
        verifyRect(yuvExpected[0], yuvExpected[1], yuvExpected[2],
                actual, window.x, window.y, window.width, window.height,
                deviation, maxMisses);
    }

    public static void verifyRect(int[] yuvExpected,
            FrameBuffer actual, int x, int y, int w, int h,
            int deviation, int maxMisses) {
        verifyRect(yuvExpected[0], yuvExpected[1], yuvExpected[2],
                actual, x, y, w, h, deviation, maxMisses);
    }

    public static void verifyRect(int yExpected, int uExpected, int vExpected,
            FrameBuffer actual, int x, int y, int w, int h) {
        verifyRect(yExpected, actual.Y, x, y, w, h);

        // FIXME: precise cw, ch
        int cx = x >> 1, cy = y >> 1, cw = (w + 1) >> 1, ch = (h + 1) >> 1;

        verifyRect(uExpected, actual.U, cx, cy, cw, ch);
        verifyRect(vExpected, actual.V, cx, cy, cw, ch);
    }

    public static void verifyRect(int yExpected, int uExpected, int vExpected,
            FrameBuffer actual, int x, int y, int w, int h,
            int deviation, double maxMisses) {
        verifyRect(yExpected, actual.Y, x, y, w, h, deviation, maxMisses);

        // FIXME: precise cw, ch
        int cx = x >> 1, cy = y >> 1, cw = (w + 1) >> 1, ch = (h + 1) >> 1;

        verifyRect(uExpected, actual.U, cx, cy, cw, ch, deviation, maxMisses);
        verifyRect(vExpected, actual.V, cx, cy, cw, ch, deviation, maxMisses);
    }

    public static void verifyRect(int yExpected, int uExpected, int vExpected,
            FrameBuffer actual, int x, int y, int w, int h,
            int deviation, int maxMisses) {
        verifyRect(yExpected, actual.Y, x, y, w, h, deviation, maxMisses);

        // FIXME: precise cw, ch
        int cx = x >> 1, cy = y >> 1, cw = (w + 1) >> 1, ch = (h + 1) >> 1;

        verifyRect(uExpected, actual.U, cx, cy, cw, ch, deviation, maxMisses);
        verifyRect(vExpected, actual.V, cx, cy, cw, ch, deviation, maxMisses);
    }

    // Verifiers for Plane

    public static void verify(Plane expected, Plane actual) {
        verify(expected, actual, 0, 0);
    }

    public static void verify(Plane expected, Plane actual,
            int deviation, double maxMisses) {
        int intMisses = (int) (actual.width * actual.height * maxMisses);

        verify(expected, actual, deviation, intMisses);
    }

    public static void verify(Plane expected, Plane actual,
            int deviation, int maxMisses) {
        int count = range(0, actual.height).map(iy -> {
            int base = iy * actual.width;
            return verify(expected.pixels, actual.pixels,
                    base, base + actual.width, deviation);
        }).sum();

        assertTotal(actual.width * actual.height, count, maxMisses);
    }

    private static int verify(short[] expected, short[] actual,
            int from, int to, int deviation) {
        int count = 0;

        for (int i = from; i < to; i++) {
            int delta = actual[i] - expected[i];

            if (delta == 0) {
                ++count;
            } else {
                assertDelta(delta, deviation);
            }
        }

        return count;
    }

    private static void assertTotal(int total, int count, int maxMisses) {
        assertFalse(count + maxMisses < total, () -> format(
                "Encountered %d misses, allowed maximum is %d of %d!",
                total - count, maxMisses, total));
    }

    private static void assertDelta(int delta, int deviation) {
        assertFalse(abs(delta) > deviation, () -> format(
                "Encountered deviation %+d,"
                        + " allowed maximum is ±%d!",
                delta, deviation));
    }

    /** Lossless target, verify 100% matching with intent. */
    public static void verifyRect(int expected, Plane actual,
            int x, int y, int w, int h) {
        verifyRect(expected, actual, x, y, w, h, 0, 0);
    }

    public static void verifyRect(int expected, Plane actual,
            int x, int y, int w, int h,
            int deviation, double maxMisses) {
        int x1 = limit(x, actual.width);
        int y1 = limit(y, actual.height);
        int x2 = limit(x + w, actual.width);
        int y2 = limit(y + h, actual.height);

        int intMisses = (int) ((x2 - x1) * (y2 - y1) * maxMisses);

        verifyRect(expected, actual, x, y, w, h, deviation, intMisses);
    }

    public static void verifyRect(int expected, Plane actual,
            int x, int y, int w, int h,
            int deviation, int maxMisses) {
        int x1 = limit(x, actual.width);
        int y1 = limit(y, actual.height);
        int x2 = limit(x + w, actual.width);
        int y2 = limit(y + h, actual.height);

        int count = range(y1, y2).map(iy -> {
            int base = iy * actual.width;
            return verify(expected, actual, base + x1, base + x2, deviation);
        }).sum();

        assertTotal((y2 - y1) * (x2 - x1), count, maxMisses);
    }

    /** @return amount of matched subpixels */
    private static int verify(int expected, Plane actual,
            int from, int to, int deviation) {
        int count = 0;

        for (int i = from; i < to; i++) {
            var value = actual.pixels[i];
            int delta = value - expected;

            if (delta == 0) {
                ++count;
            } else {
                assertDelta(delta, deviation);
            }
        }

        return count;
    }

    private static int limit(int value, int limit) {
        return min(max(value, 0), limit);
    }

    public void histogramm(Plane plane, int[] buf) {
        Arrays.fill(buf, 0);
        for (int p : plane.pixels) {
            ++buf[p];
        }
    }

    public static void histogram(Plane plane, int x, int y, int w, int h,
            int[] buf) {
        int x1 = limit(x, plane.width);
        int y1 = limit(y, plane.height);
        int x2 = limit(x + w, plane.width);
        int y2 = limit(y + h, plane.height);

        Arrays.fill(buf, 0);

        for (int iy = y1; iy < y2; ++iy) {
            int base = iy * plane.width;
            histogram(plane.pixels, base + x1, base + x2, buf);
        }
    }

    private static void histogram(short[] pixels, int from, int to,
            int[] buf) {
        for (int p = from; p < to; ++p) {
            ++buf[p];
        }
    }
}
