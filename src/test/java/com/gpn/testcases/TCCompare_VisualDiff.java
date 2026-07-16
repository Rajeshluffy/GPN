package com.gpn.testcases;

import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.framework.utils.ScreenshotStore;
import com.gpn.utils.VisualComparisonUtils;
import com.gpn.utils.VisualComparisonUtils.ComparisonResult;

/**
 * Standalone visual comparison test — runs after QA and DEV test blocks complete.
 *
 * <p>Does not open a browser, does not login.  Reads the screenshots saved by
 * the QA and DEV runs from the filesystem and compares them pixel-by-pixel.
 *
 * <h3>Folder layout expected</h3>
 * <pre>
 *   Screenshots/QA/&lt;TestCase&gt;/&lt;account&gt;_&lt;element&gt;.jpg
 *   Screenshots/DEV/&lt;TestCase&gt;/&lt;account&gt;_&lt;element&gt;.jpg
 * </pre>
 *
 * <h3>Configuring the threshold</h3>
 * Pass {@code -Dcompare.threshold=1.0} to allow up to 1% pixel difference.
 * Default is {@code 0.0} (exact match required).
 */
public class TCCompare_VisualDiff {

    @Test
    public void compareEnvironmentScreenshots(ITestContext ctx) {
        double threshold = resolveThreshold(ctx);

        System.out.println("[TCCompare] Starting visual comparison.");
        System.out.println("[TCCompare]   QA root  : " + ScreenshotStore.qaRoot());
        System.out.println("[TCCompare]   DEV root : " + ScreenshotStore.devRoot());
        System.out.println("[TCCompare]   Diff root: " + ScreenshotStore.diffRoot());
        System.out.printf ("[TCCompare]   Threshold: %.2f%%%n", threshold);

        List<ComparisonResult> results = VisualComparisonUtils.compareAll(
                ScreenshotStore.qaRoot(),
                ScreenshotStore.devRoot(),
                ScreenshotStore.diffRoot(),
                threshold);

        if (results.isEmpty()) {
            System.out.println("[TCCompare] WARNING: No screenshot pairs found. "
                    + "Ensure QA and DEV blocks ran before this test.");
            return;
        }

        int pass = 0, fail = 0;
        StringBuilder report = new StringBuilder();
        report.append("\n═══════════════════════════════════════════════════════\n");
        report.append("  VISUAL COMPARISON REPORT\n");
        report.append("═══════════════════════════════════════════════════════\n");

        for (ComparisonResult r : results) {
            if (r.getError() != null) {
                report.append(String.format("  ✗ ERROR  — %s%n    %s%n", r.getQaPath(), r.getError()));
                fail++;
            } else if (r.isPassed()) {
                report.append(String.format("  ✓ PASS   — %s (diff=%.2f%%)%n",
                        shortName(r.getQaPath()), r.getDiffPercent()));
                pass++;
            } else {
                report.append(String.format("  ✗ FAIL   — %s (diff=%.2f%% > threshold=%.2f%%)%n    diff: %s%n",
                        shortName(r.getQaPath()), r.getDiffPercent(), threshold, r.getDiffPath()));
                fail++;
            }
        }

        report.append("═══════════════════════════════════════════════════════\n");
        report.append(String.format("  TOTAL: %d  |  PASS: %d  |  FAIL: %d%n", results.size(), pass, fail));
        report.append("═══════════════════════════════════════════════════════\n");
        System.out.println(report);

        if (fail > 0) {
            throw new AssertionError(fail + " of " + results.size()
                    + " screenshot comparison(s) failed. See diff images in: "
                    + ScreenshotStore.diffRoot());
        }
    }

    // -------------------------------------------------------------------------

    private double resolveThreshold(ITestContext ctx) {
        // 1. System property  (-Dcompare.threshold=1.0)
        String sysProp = System.getProperty("compare.threshold");
        if (sysProp != null) {
            try { return Double.parseDouble(sysProp.trim()); } catch (NumberFormatException ignored) {}
        }
        // 2. TestNG XML parameter  <parameter name="compareThreshold" value="1.0"/>
        if (ctx != null && ctx.getCurrentXmlTest() != null) {
            String xmlParam = ctx.getCurrentXmlTest().getAllParameters().get("compareThreshold");
            if (xmlParam != null) {
                try { return Double.parseDouble(xmlParam.trim()); } catch (NumberFormatException ignored) {}
            }
        }
        // 3. Default — exact match
        return 0.0;
    }

    private String shortName(String path) {
        // Return last two path segments: <TestCase>/<filename>
        java.io.File f = new java.io.File(path);
        String name   = f.getName();
        String parent = f.getParentFile() != null ? f.getParentFile().getName() : "";
        return parent.isEmpty() ? name : parent + "/" + name;
    }
}
