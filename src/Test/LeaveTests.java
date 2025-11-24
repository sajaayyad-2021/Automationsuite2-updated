package Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import reporting.ExtentManager;
import reporting.ScreenshotHelper;
import testbase.BaseTemplate;
import utilites.Config;
import utilites.CustomFunction;
import utilites.MainFunctions;
import utilites.ResultChecker;

@Listeners(reporting.ExtentTestNGITestListener.class)
public class LeaveTests extends BaseTemplate {

    MainFunctions mf;
    ResultChecker resultCheck = new ResultChecker();

    private static ExtentReports extent;
    private ExtentTest currentTest;

    String[] testsList = null;
    String activeTest = null;

    @Test
    public void RunTests() throws IOException, InterruptedException {

        extent = ExtentManager.getInstance();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String className = this.getClass().getSimpleName();

        // ----------------- Parse CLI Args -----------------
        if (testNmaes_pim == null || testNmaes_pim.trim().isEmpty()) {
            testNmaes_pim = "ALL";
        } else {
            testNmaes_pim = testNmaes_pim.trim();
        }

        if (!"ALL".equalsIgnoreCase(testNmaes_pim)) {
            if (testNmaes_pim.contains(",")) {
                testsList = Arrays.stream(testNmaes_pim.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);
            } else {
                testsList = new String[]{ testNmaes_pim };
            }
        } else {
            Method[] methods = this.getClass().getDeclaredMethods();
            List<String> TC = new ArrayList<>();

            for (Method m : methods)
                if (m.getName().startsWith("TC_"))
                    TC.add(m.getName());

            testsList = TC.toArray(new String[0]);
        }

        // ----------------- Execute Each TC -----------------
        for (String tc : testsList) {

            activeTest = tc;
            addCurrentTestMthod(activeTest);

            String tcRoot = inputPath(className, tc);

            currentTest = extent.createTest(activeTest);
            currentTest.assignCategory("Regression");
            currentTest.assignCategory("Leave");

            currentTest.info("Starting test: " + activeTest);

            MainFunctions.deleteFiles(actualPath(className, tc));
            MainFunctions.deleteFiles(diffPath(className, tc));

            try {

                Config cfg = loadthisTestConfig(className, tc);
                mf = new MainFunctions(driver, cfg);

                invokeTestMethod(tc, cfg, className);

                currentTest.pass("Test completed");

            } catch (NoSuchMethodException e) {

                currentTest.skip("No test method found for: " + activeTest);

            } catch (Throwable e) {

                currentTest.fail("Exception occurred: " + e.getMessage());
                currentTest.fail(e);

              
            }
        }

        extent.flush();
    }

    private void invokeTestMethod(String methodName, Config cfg, String className) throws Exception {
        Method m = this.getClass().getDeclaredMethod(methodName, Config.class, String.class);
        m.setAccessible(true);
        m.invoke(this, cfg, className);
    }

    // Save Baseline/Actual/Diff
    private void saveDataArtifacts(String className, String testName,
                                   String actualData, boolean passed) {

        try {
            String baseName     = "baseline.txt";
            String actualFile   = actualPath(className, testName)   + baseName;
            String expectedFile = expectedPath(className, testName) + baseName;
            String diffFile     = diffPath(className, testName)     + "baseline_diff.txt";

            CustomFunction.writeTextFile(actualFile, actualData);

            File expected = new File(expectedFile);

            if (!expected.exists() || expected.length() == 0) {

                CustomFunction.writeTextFile(expectedFile, actualData);
                CustomFunction.appendToFile("Baseline created.", diffFile);

                currentTest.pass("Baseline CREATED for first run.");
                currentTest.info(MarkupHelper.createCodeBlock(actualData));

                return;
            }

            String baseline = Files.readString(Paths.get(expectedFile));
            boolean match = baseline.trim().equals(actualData.trim());
            boolean finalResult = passed && match;

            CustomFunction.appendToFile("EXPECTED:\n" + baseline, diffFile);
            CustomFunction.appendToFile("ACTUAL:\n"   + actualData, diffFile);
            CustomFunction.appendToFile("RESULT: " + (finalResult ? "PASS" : "FAIL"), diffFile);

            currentTest.info(
                    MarkupHelper.createCodeBlock(
                            "EXPECTED:\n" + baseline + "\n\nACTUAL:\n" + actualData
                    )
            );

            if (finalResult)
                currentTest.pass("Validation PASSED");
            else
                currentTest.fail("Baseline mismatch");

        } catch (Exception e) {
            currentTest.fail("Failed saving artifacts: " + e.getMessage());
        }
    }

    // ======================================================
    // ---------------  TEST CASES ---------------------------
    // ======================================================


    // 1) BASIC SEARCH
    private void TC_LEAVE_001_basicSearch(Config cfg, String cn) {

        currentTest.info("Leave Basic Search");

        boolean ok = mf.basicLeaveSearch(cfg);
        String data = ok ? "SEARCH_OK" : "SEARCH_FAIL";

        saveDataArtifacts(cn, "TC_LEAVE_001_basicSearch", data, ok);

        String finalResult =
                resultCheck.checkTestResult(cn, "TC_LEAVE_001_basicSearch", SuitePath);

        if (finalResult.equalsIgnoreCase("PASS"))
            currentTest.pass(finalResult);
        else
            currentTest.fail(finalResult);
    }


    // 2) SEARCH BY EMPLOYEE
    private void TC_LEAVE_002_searchWithEmployeeName(Config cfg, String cn) {

        currentTest.info("Search using Employee Name");

        boolean ok = mf.searchLeaveByEmployee(cfg);
        String data = ok ? "FOUND" : "NOT_FOUND";

        saveDataArtifacts(cn, "TC_LEAVE_002_searchWithEmployeeName", data, ok);

        String finalResult =
                resultCheck.checkTestResult(cn, "TC_LEAVE_002_searchWithEmployeeName", SuitePath);

        if (finalResult.equalsIgnoreCase("PASS"))
            currentTest.pass(finalResult);
        else
            currentTest.fail(finalResult);
    }


    // 3) INVALID DATE RANGE
    private void TC_LEAVE_003_invalidDateRange(Config cfg, String cn) {

        currentTest.info("Invalid Date Range Test");

        boolean ok = mf.invalidDateRange(cfg);
        String data = ok ? "INVALID_RANGE_HANDLED" : "CRASH";

        saveDataArtifacts(cn, "TC_LEAVE_003_invalidDateRange", data, ok);

        String finalResult =
                resultCheck.checkTestResult(cn, "TC_LEAVE_003_invalidDateRange", SuitePath);

        if (finalResult.equalsIgnoreCase("PASS"))
            currentTest.pass(finalResult);
        else
            currentTest.fail(finalResult);
    }


    // 4) SELECT STATUS
    private void TC_LEAVE_004_selectLeaveStatus(Config cfg, String cn) {

        currentTest.info("Leave Status Selection");

        boolean ok = mf.selectLeaveStatus(cfg);
        String data = ok ? "STATUS_OK" : "STATUS_FAIL";

        saveDataArtifacts(cn, "TC_LEAVE_004_selectLeaveStatus", data, ok);

        String finalResult =
                resultCheck.checkTestResult(cn, "TC_LEAVE_004_selectLeaveStatus", SuitePath);

        if (finalResult.equalsIgnoreCase("PASS"))
            currentTest.pass(finalResult);
        else
            currentTest.fail(finalResult);
    }


    // 5) SELECT LEAVE TYPE
    private void TC_LEAVE_005_selectLeaveType(Config cfg, String cn) {

        currentTest.info("Leave Type Selection");

        boolean ok = mf.selectLeaveTypeOnly(cfg);
        String data = ok ? "TYPE_OK" : "TYPE_FAIL";

        saveDataArtifacts(cn, "TC_LEAVE_005_selectLeaveType", data, ok);

        String finalResult =
                resultCheck.checkTestResult(cn, "TC_LEAVE_005_selectLeaveType", SuitePath);

        if (finalResult.equalsIgnoreCase("PASS"))
            currentTest.pass(finalResult);
        else
            currentTest.fail(finalResult);
    }


    // 6) RESET BUTTON
    private void TC_LEAVE_006_resetButton(Config cfg, String cn) {

        currentTest.info("Reset Button Test");

        boolean ok = mf.resetLeaveFilters();
        String data = ok ? "RESET_OK" : "RESET_FAIL";

        saveDataArtifacts(cn, "TC_LEAVE_006_resetButton", data, ok);

        String finalResult =
                resultCheck.checkTestResult(cn, "TC_LEAVE_006_resetButton", SuitePath);

        if (finalResult.equalsIgnoreCase("PASS"))
            currentTest.pass(finalResult);
        else
            currentTest.fail(finalResult);
    }

}
