package reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class ReportLogger {

    public static void step(ExtentTest test, String title) {
        test.info("STEP: " + title);
    }

    public static void expected(ExtentTest test, String expected) {
        test.info("EXPECTED: " + expected);
    }

    public static void actual(ExtentTest test, String actual) {
        test.info("ACTUAL: " + actual);
    }

    public static void result(ExtentTest test, boolean pass) {
        String status = pass ? "PASS" : "FAIL";
        test.info("Validation result: " + status);
    }

    // FULL block (exact style like screenshot)
    public static void fullBlock(ExtentTest test, String step, String expected, String actual, boolean pass) {

        String details =
                "STEP:\n    " + step + "\n\n" +
                "EXPECTED:\n    " + expected + "\n\n" +
                "ACTUAL:\n    " + actual + "\n\n" +
                "RESULT:\n    " + (pass ? "PASS" : "FAIL");

        test.info(MarkupHelper.createCodeBlock(details));
    }
}
