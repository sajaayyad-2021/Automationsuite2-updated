package Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.*;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import reporting.ExtentManager;
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
    public void LeaveSuite() throws IOException, InterruptedException {

        extent = ExtentManager.getInstance();
        String className = this.getClass().getSimpleName();

        // -------------------- Parse CLI Args --------------------
        if (testNmaes_leave == null || testNmaes_leave.trim().isEmpty()) {
            testNmaes_leave = "ALL";
        } else {
            testNmaes_leave = testNmaes_leave.trim();
        }

        if (!"ALL".equalsIgnoreCase(testNmaes_leave)) {
            if (testNmaes_leave.contains(",")) {
                testsList = Arrays.stream(testNmaes_leave.split(","))
                                 .map(String::trim)
                                 .filter(s -> !s.isEmpty())
                                 .toArray(String[]::new);
            } else {
                testsList = new String[]{ testNmaes_leave };
            }
        } else {
            Method[] methods = this.getClass().getDeclaredMethods();
            List<String> TC = new ArrayList<>();

            for (Method m : methods)
                if (m.getName().startsWith("TC_"))
                    TC.add(m.getName());

            testsList = TC.toArray(new String[0]);
        }

        // LOGIN ONCE BEFORE ALL LEAVE TESTS (without logout)
        Config loginCfg = loadthisTestConfig("LoginTests", "TC_LOG_001_validLogin");
        mf = new MainFunctions(driver, loginCfg);
        mf.performLoginWithoutLogout(loginCfg);  // Use new method without logout
        System.out.println("[LeaveSuite] Logged in successfully");

        // -------------------- Test Loop --------------------
        for (String tc : testsList) {

            activeTest = tc;
            addCurrentTestMthod(activeTest);

            currentTest = extent.createTest(activeTest);
            currentTest.assignCategory("Regression");
            currentTest.assignCategory("Leave");
            currentTest.info("Starting test: " + activeTest);

            MainFunctions.deleteFiles(actualPath(className, tc));
            MainFunctions.deleteFiles(diffPath(className, tc));

            try {
                Config cfg = loadthisTestConfig(className, tc);
                mf = new MainFunctions(driver, cfg);

                // general leave executor
                general(cfg, className, tc);

                currentTest.pass("Test completed");

            } catch (Throwable e) {
                currentTest.fail("Exception occurred: " + e.getMessage());
                currentTest.fail(e);
            }
        }

        extent.flush();
    }


    // ========================================================
    // GENERAL LEAVE ACTION HANDLER
    // ========================================================
    private void general(Config cfg, String className, String testCaseName) {

        try {
            currentTest.info("Executing test: " + testCaseName);

            // Perform Leave Search (all scenarios handled inside)
            mf.performLeaveSearch(cfg);

            // Capture actual result from page
            String actualResult = getActualLeaveResult();
            currentTest.info("Actual Result: " + actualResult);

            // Save artifacts and validate with baseline
            saveDataArtifacts(className, testCaseName, actualResult);

        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // ========================================================
    // CAPTURE ACTUAL LEAVE RESULT FROM UI
    // ========================================================
    private String getActualLeaveResult() {
        try {
            // Return current URL as actual result (no hardcoded strings)
            return mf.getCurrentURL();
            
        } catch (Exception e) {
            currentTest.fail("Error capturing result: " + e.getMessage());
            return e.getMessage();
        }
    }


    // ========================================================
    // ARTIFACT & BASELINE VALIDATION
    // ========================================================
    private void saveDataArtifacts(String className, String testName, String actualData) {
        try {
            String baseName     = "baseline.txt";
            String actualFile   = actualPath(className, testName)   + baseName;
            String expectedFile = expectedPath(className, testName) + baseName;
            String diffFile     = diffPath(className, testName)     + "baseline_diff.txt";

            CustomFunction.writeTextFile(actualFile, actualData);

            File expected = new File(expectedFile);

            if (!expected.exists() || expected.length() == 0) {
                currentTest.warning("Expected baseline file not found: " + expectedFile);
                currentTest.info("Please create expected baseline manually.");
                currentTest.info("Actual: " + actualData);

                CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
                CustomFunction.appendToFile("Expected missing!", diffFile);
                CustomFunction.appendToFile("Actual: " + actualData, diffFile);

                currentTest.fail("Expected baseline file not found");
                return;
            }

            String baseline = Files.readString(Paths.get(expectedFile)).trim();
            String actual = actualData.trim();
            boolean match = baseline.equals(actual);

            CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
            CustomFunction.appendToFile("Expected: " + baseline, diffFile);
            CustomFunction.appendToFile("Actual  : " + actual, diffFile);
            CustomFunction.appendToFile("Result  : " + (match ? "PASS" : "FAIL"), diffFile);

            String block = String.format(
                "EXPECTED:\n%s\n\nACTUAL:\n%s\n\nRESULT: %s",
                baseline, actual, match ? "PASS" : "FAIL"
            );

            currentTest.info(MarkupHelper.createCodeBlock(block));

            if (match) {
                currentTest.pass("✓ Actual matches Expected");
            } else {
                currentTest.fail("✗ Baseline mismatch - Expected: " + baseline + ", but got: " + actual);
            }

        } catch (Exception ex) {
            currentTest.fail("Artifact save failed: " + ex.getMessage());
        }
    }
}