package com.gpn.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filesystem-based pixel-wise visual comparison utility.
 *
 * <h3>How matching works</h3>
 * Files are matched by <em>relative path within the environment root</em>.
 * If {@code Screenshots/QA/TC004_VerifyMODisplay/okabe_canvas.jpg} exists, this
 * utility looks for {@code Screenshots/DEV/TC004_VerifyMODisplay/okabe_canvas.jpg}.
 * The comparison key is the relative path — no in-memory registry is needed.
 *
 * <h3>Threshold</h3>
 * {@code thresholdPercent} controls how many pixels may differ before the
 * comparison is marked as FAILED.  {@code 0.0} requires a pixel-perfect match;
 * {@code 1.0} allows up to 1% of pixels to differ (useful for anti-aliasing or
 * sub-pixel rendering differences between environments).
 *
 * <h3>Diff image</h3>
 * Changed pixels are highlighted in red; matching pixels are dimmed to 50% white
 * so differences stand out clearly against a muted background.
 */
public final class VisualComparisonUtils {

    private static final Logger logger = LoggerFactory.getLogger(VisualComparisonUtils.class);

    private VisualComparisonUtils() {}

    // =========================================================================
    // COMPARE ALL — filesystem scan
    // =========================================================================

    /**
     * Walks every image file under {@code qaRoot}, finds its counterpart in
     * {@code devRoot} (matched by relative path), runs a pixel comparison, and
     * writes a diff image to {@code diffRoot}.
     *
     * @param qaRoot           root folder for QA screenshots,  e.g. {@code "Screenshots/QA"}
     * @param devRoot          root folder for DEV screenshots, e.g. {@code "Screenshots/DEV"}
     * @param diffRoot         root folder for diff images,     e.g. {@code "Screenshots/Diff"}
     * @param thresholdPercent maximum allowed pixel-difference percentage (0.0 = exact match)
     * @return list of {@link ComparisonResult} — one entry per matched file pair
     */
    public static List<ComparisonResult> compareAll(String qaRoot, String devRoot,
                                                    String diffRoot, double thresholdPercent) {
        List<ComparisonResult> results = new ArrayList<>();
        Path qaPath = Paths.get(qaRoot);

        if (!qaPath.toFile().exists()) {
            logger.warn("[VisualComparison] QA root does not exist: {}", qaRoot);
            return results;
        }

        try (Stream<Path> files = Files.walk(qaPath)) {
            files.filter(Files::isRegularFile)
                 .filter(p -> isImage(p.toString()))
                 .forEach(qaFile -> {
                     Path relative = qaPath.relativize(qaFile);
                     Path devFile  = Paths.get(devRoot).resolve(relative);
                     Path diffFile = Paths.get(diffRoot).resolve(relative);

                     if (!devFile.toFile().exists()) {
                         logger.warn("[VisualComparison] No DEV counterpart for: {}", relative);
                         results.add(ComparisonResult.missing(qaFile.toString(), devFile.toString()));
                         return;
                     }

                     ComparisonResult result = compare(
                             qaFile.toFile(), devFile.toFile(),
                             diffFile.toFile(), thresholdPercent);
                     results.add(result);
                 });
        } catch (IOException e) {
            logger.error("[VisualComparison] Error walking QA directory: {}", e.getMessage());
        }

        logSummary(results);
        return results;
    }

    // =========================================================================
    // COMPARE SINGLE PAIR
    // =========================================================================

    /**
     * Compares two image files pixel-by-pixel and writes a diff image.
     *
     * @param qaFile           QA screenshot file
     * @param devFile          DEV screenshot file
     * @param diffOutputFile   output file for the diff image
     * @param thresholdPercent maximum allowed pixel-difference percentage
     * @return {@link ComparisonResult} with diff percentage and pass/fail status
     */
    public static ComparisonResult compare(File qaFile, File devFile,
                                           File diffOutputFile, double thresholdPercent) {
        try {
            BufferedImage imgA = ImageIO.read(qaFile);
            BufferedImage imgB = ImageIO.read(devFile);

            if (imgA == null || imgB == null) {
                return ComparisonResult.error(qaFile.getPath(), devFile.getPath(),
                        "ImageIO.read returned null — file may not be a valid image");
            }

            int width  = Math.min(imgA.getWidth(),  imgB.getWidth());
            int height = Math.min(imgA.getHeight(), imgB.getHeight());
            long total = (long) width * height;
            long diff  = 0;

            BufferedImage diffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgbA = imgA.getRGB(x, y);
                    int rgbB = imgB.getRGB(x, y);
                    if (rgbA != rgbB) {
                        diff++;
                        diffImg.setRGB(x, y, Color.RED.getRGB());
                    } else {
                        diffImg.setRGB(x, y, dim(rgbA));
                    }
                }
            }

            double diffPct = total == 0 ? 0.0 : (100.0 * diff / total);
            boolean passed = diffPct <= thresholdPercent;

            // Save diff image
            if (diffOutputFile.getParentFile() != null) {
                diffOutputFile.getParentFile().mkdirs();
            }
            ImageIO.write(diffImg, "jpg", diffOutputFile);

            logger.info("[VisualComparison] {} | {}/{} px differ ({}) | {} (threshold={}%)",
                    qaFile.getName(), diff, total,
                    String.format("%.2f%%", diffPct),
                    passed ? "PASS" : "FAIL",
                    thresholdPercent);

            return new ComparisonResult(
                    qaFile.getPath(), devFile.getPath(), diffOutputFile.getPath(),
                    diffPct, thresholdPercent, passed, null);

        } catch (IOException e) {
            logger.error("[VisualComparison] IO error comparing {}: {}", qaFile.getName(), e.getMessage());
            return ComparisonResult.error(qaFile.getPath(), devFile.getPath(), e.getMessage());
        }
    }

    // =========================================================================
    // INTERNAL HELPERS
    // =========================================================================

    /** Returns true for common raster image extensions. */
    private static boolean isImage(String path) {
        String lower = path.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
    }

    /** Blends an RGB pixel 50% toward white — dimmed background in the diff image. */
    private static int dim(int rgb) {
        Color c = new Color(rgb);
        return new Color(
                (c.getRed()   + 255) / 2,
                (c.getGreen() + 255) / 2,
                (c.getBlue()  + 255) / 2).getRGB();
    }

    private static void logSummary(List<ComparisonResult> results) {
        long passed  = results.stream().filter(r -> r.isPassed()).count();
        long failed  = results.stream().filter(r -> !r.isPassed() && r.getError() == null).count();
        long missing = results.stream().filter(r -> r.isMissing()).count();
        long errors  = results.stream().filter(r -> r.getError() != null).count();
        logger.info("[VisualComparison] Summary — PASS:{} FAIL:{} MISSING:{} ERROR:{} / TOTAL:{}",
                passed, failed, missing, errors, results.size());
    }

    // =========================================================================
    // RESULT VALUE OBJECT
    // =========================================================================

    public static final class ComparisonResult {

        private final String  qaPath;
        private final String  devPath;
        private final String  diffPath;
        private final double  diffPercent;
        private final double  threshold;
        private final boolean passed;
        private final String  error;   // non-null means comparison could not run

        ComparisonResult(String qaPath, String devPath, String diffPath,
                         double diffPercent, double threshold, boolean passed, String error) {
            this.qaPath      = qaPath;
            this.devPath     = devPath;
            this.diffPath    = diffPath;
            this.diffPercent = diffPercent;
            this.threshold   = threshold;
            this.passed      = passed;
            this.error       = error;
        }

        static ComparisonResult missing(String qaPath, String devPath) {
            return new ComparisonResult(qaPath, devPath, null, -1.0, 0.0, false,
                    "DEV counterpart not found: " + devPath);
        }

        static ComparisonResult error(String qaPath, String devPath, String message) {
            return new ComparisonResult(qaPath, devPath, null, -1.0, 0.0, false, message);
        }

        /** True when diff% ≤ threshold (comparison ran and was within tolerance). */
        public boolean isPassed()     { return passed; }

        /** True when the DEV counterpart file was not found. */
        public boolean isMissing()    { return error != null && error.startsWith("DEV counterpart"); }

        /** Percentage of pixels that differ (0.0–100.0). -1.0 if comparison could not run. */
        public double  getDiffPercent() { return diffPercent; }

        /** Path of the QA screenshot. */
        public String  getQaPath()    { return qaPath; }

        /** Path of the DEV screenshot. */
        public String  getDevPath()   { return devPath; }

        /** Path of the diff image, or {@code null} if comparison did not run. */
        public String  getDiffPath()  { return diffPath; }

        /** Error message when the comparison could not be performed, otherwise {@code null}. */
        public String  getError()     { return error; }

        @Override
        public String toString() {
            if (error != null) return "ComparisonResult[ERROR: " + error + "]";
            return String.format("ComparisonResult[%s | diff=%.2f%% | %s]",
                    new File(qaPath).getName(), diffPercent, passed ? "PASS" : "FAIL");
        }
    }
}
