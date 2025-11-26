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
public class PIMTests extends BaseTemplate {

    MainFunctions mf;
    ResultChecker resultCheck = new ResultChecker();
    private static ExtentReports extent;
    private ExtentTest currentTest;

    String[] testsList = null;
    String activeTest = null;

    @Test
    public void PIMSuite() throws IOException, InterruptedException {

        extent = ExtentManager.getInstance();
        String className = this.getClass().getSimpleName();

        // -------------------- Parse CLI Args --------------------
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
                if (m.getName().startsWith("TC_PIM_"))
                    TC.add(m.getName());
            testsList = TC.toArray(new String[0]);
        }

        // LOGIN ONCE BEFORE ALL PIM TESTS (without logout)
        Config loginCfg = loadthisTestConfig("LoginTests", "TC_LOG_001_validLogin");
        mf = new MainFunctions(driver, loginCfg);
        mf.performLoginWithoutLogout(loginCfg);  // Use new method without logout
        System.out.println("[PIMSuite] Logged in successfully");

        // -------------------- Test Loop --------------------
        for (String tc : testsList) {

            activeTest = tc;
            addCurrentTestMthod(activeTest);

            currentTest = extent.createTest(activeTest);
            currentTest.assignCategory("Regression");
            currentTest.assignCategory("PIM");
            currentTest.info("Starting test: " + activeTest);

            MainFunctions.deleteFiles(actualPath(className, tc));
            MainFunctions.deleteFiles(diffPath(className, tc));

            try {
                Config cfg = loadthisTestConfig(className, tc);
                mf = new MainFunctions(driver, cfg);

                // general pim executor
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
    // GENERAL PIM ACTION HANDLER
    // ========================================================
    private void general(Config cfg, String className, String testCaseName) {

        try {
            currentTest.info("Executing test: " + testCaseName);

            // Determine action type from test name
            String actionType = determineActionType(testCaseName);
            currentTest.info("Action type: " + actionType);

            // Perform action based on type
            if (actionType.equals("addEmployee")) {
                mf.performAddEmployee(cfg);
            } else if (actionType.equals("searchEmployee")) {
                mf.performSearchEmployee(cfg);
            }

            // Capture actual result from page
            String actualResult = getActualPIMResult();
            currentTest.info("Actual Result: " + actualResult);

            // Save artifacts and validate with baseline
            saveDataArtifacts(className, testCaseName, actualResult);

        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // ========================================================
    // DETERMINE ACTION TYPE FROM TEST NAME
    // ========================================================
    private String determineActionType(String testName) {
        // TC_PIM_001_addEmployeeValid -> addEmployee
        // TC_PIM_006_searchByValidName -> searchEmployee
        
        if (testName.toLowerCase().contains("addemployee")) {
            return "addEmployee";
        } else if (testName.toLowerCase().contains("search")) {
            return "searchEmployee";
        }
        
        return "unknown";
    }


    // ========================================================
    // CAPTURE ACTUAL PIM RESULT FROM UI
    // ========================================================
    private String getActualPIMResult() {
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