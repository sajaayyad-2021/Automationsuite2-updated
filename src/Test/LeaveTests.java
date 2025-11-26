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
import com.aventstack.extentreports.markuputils.ExtentColor;
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
    public void LeaveSuite() throws IOException, InterruptedException {

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
                currentTest.info("Please create the expected baseline manually with the correct value.");
                currentTest.info("Actual value: " + actualData);
                
                CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
                CustomFunction.appendToFile("Expected file missing!", diffFile);
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
                currentTest.log(Status.PASS,
                    MarkupHelper.createLabel("✓ Actual matches Expected", ExtentColor.GREEN));
            } else {
                currentTest.log(Status.FAIL,
                    MarkupHelper.createLabel("✗ Baseline mismatch", ExtentColor.RED));
                currentTest.log(Status.WARNING, "Diff file: " + diffFile);
            }

        } catch (Exception ex) {
            if (currentTest != null) {
                currentTest.fail("Artifact save failed: " + ex.getMessage());
            }
        }
    }

    private void validateTest1(String testName, String className, String actualData) {
        saveDataArtifacts(className, testName, actualData);
        
        String finalResult = resultCheck.checkTestResult(className, testName, SuitePath);
        
        if ("PASS".equalsIgnoreCase(finalResult)) {
            currentTest.pass(testName + ": " + finalResult);
        } else {
            currentTest.fail(testName + ": " + finalResult);
        }
    }

    // ======================================================
    // ---------------  TEST CASES ---------------------------
    // ======================================================


 // 1) BASIC SEARCH
 private void TC_LEAVE_001_basicSearch(Config cfg, String cn) {
     try {
         currentTest.info("Leave Basic Search");
         
         String result = mf.performLeaveSearch(cfg);
         
         validateTest1("TC_LEAVE_001_basicSearch", cn, result);
         
     } catch (Exception e) {
         currentTest.fail("Exception: " + e.getMessage());
         e.printStackTrace();
     }
 }

 // 2) SEARCH BY EMPLOYEE
 private void TC_LEAVE_002_searchWithEmployeeName(Config cfg, String cn) {
     try {
         currentTest.info("Search using Employee Name");
         
         String result = mf.performLeaveSearch(cfg);
         
         validateTest1("TC_LEAVE_002_searchWithEmployeeName", cn, result);
         
     } catch (Exception e) {
         currentTest.fail("Exception: " + e.getMessage());
         e.printStackTrace();
     }
 }

 // 3) INVALID DATE RANGE
 private void TC_LEAVE_003_invalidDateRange(Config cfg, String cn) {
     try {
         currentTest.info("Invalid Date Range Test");
         
         String result = mf.performLeaveSearch(cfg);
         
         validateTest1("TC_LEAVE_003_invalidDateRange", cn, result);
         
     } catch (Exception e) {
         currentTest.fail("Exception: " + e.getMessage());
         e.printStackTrace();
     }
 }

 // 4) SELECT STATUS
 private void TC_LEAVE_004_selectLeaveStatus(Config cfg, String cn) {
     try {
         currentTest.info("Leave Status Selection");
         
         String result = mf.performLeaveSearch(cfg);
         
         validateTest1("TC_LEAVE_004_selectLeaveStatus", cn, result);
         
     } catch (Exception e) {
         currentTest.fail("Exception: " + e.getMessage());
         e.printStackTrace();
     }
 }

 // 5) SELECT LEAVE TYPE
 private void TC_LEAVE_005_selectLeaveType(Config cfg, String cn) {
     try {
         currentTest.info("Leave Type Selection");
         
         String result = mf.performLeaveSearch(cfg);
         
         validateTest1("TC_LEAVE_005_selectLeaveType", cn, result);
         
     } catch (Exception e) {
         currentTest.fail("Exception: " + e.getMessage());
         e.printStackTrace();
     }
 }

 // 6) RESET BUTTON
 private void TC_LEAVE_006_resetButton(Config cfg, String cn) {
     try {
         currentTest.info("Reset Button Test");
         
         String result = mf.performLeaveReset(cfg);
         
         validateTest1("TC_LEAVE_006_resetButton", cn, result);
         
     } catch (Exception e) {
         currentTest.fail("Exception: " + e.getMessage());
         e.printStackTrace();
     }
 }

 // Add validateTest method (same as PIMTests)


    }