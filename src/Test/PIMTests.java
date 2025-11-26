package Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import utilites.Config;
import testbase.BaseTemplate;
import utilites.CustomFunction;
import utilites.MainFunctions;
import utilites.ResultChecker;
import reporting.ExtentManager;

@Listeners(reporting.ExtentTestNGITestListener.class)
public class PIMTests extends BaseTemplate {

    MainFunctions mf;
    ResultChecker ResultCheck = new ResultChecker();
    private static ExtentReports extent;
    private ExtentTest currentTest;
    String[] testsList = null;

    @Test
    public void PIMSuite() throws IOException, InterruptedException {
        extent = ExtentManager.getInstance();
        String className = this.getClass().getSimpleName();

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
            List<String> TESTS = new ArrayList<>();
            for (Method m : methods) {
                if (m.getName().startsWith("TC_PIM_")) {
                    TESTS.add(m.getName());
                }
            }
            testsList = TESTS.toArray(new String[0]);
        }

        // Login once before running PIM tests
        Config loginCfg = loadthisTestConfig("LoginTests", "TC_LOG_001_validLogin");
        mf = new MainFunctions(driver, loginCfg);
        mf.performLogin(loginCfg);
        System.out.println("[PIMSuite] Logged in successfully");

        for (String tc : testsList) {
            addCurrentTestMthod(tc);
            currentTest = extent.createTest(tc);
            currentTest.assignCategory("Regression");
            currentTest.assignCategory("PIM");
            currentTest.info("Starting test: " + tc);

            MainFunctions.deleteFiles(actualPath(className, tc));
            MainFunctions.deleteFiles(diffPath(className, tc));

            try {
                Config cfg = loadthisTestConfig(className, tc);
                mf = new MainFunctions(driver, cfg);

                Method m = this.getClass().getDeclaredMethod(tc, Config.class, String.class);
                m.setAccessible(true);
                m.invoke(this, cfg, className);

                currentTest.pass("Test finished successfully");

            } catch (NoSuchMethodException e) {
                currentTest.skip("No matching test method found for: " + tc);
            } catch (Throwable e) {
//                currentTest.fail("Exception occurred: " + e.getMessage());
            }
        }
        extent.flush();
    }

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

    private void validateTest(String testName, String className, String actualData) {
        saveDataArtifacts(className, testName, actualData);
        
        String finalResult = ResultCheck.checkTestResult(className, testName, SuitePath);
        
        if ("PASS".equalsIgnoreCase(finalResult)) {
            currentTest.pass(testName + ": " + finalResult);
        } else {
            currentTest.fail(testName + ": " + finalResult);
        }
    }

    // ============================================
    // TEST CASES - ADD EMPLOYEE
    // ============================================

    private void TC_PIM_001_addEmployeeValid(Config cfg, String className) {
        try {
            currentTest.info("Adding employee with valid data (all fields)");
            
            String result = mf.performAddEmployee(cfg);
            
            validateTest("TC_PIM_001_addEmployeeValid", className, result);
            
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void TC_PIM_002_addEmployeeNoMiddleName(Config cfg, String className) {
        try {
            currentTest.info("Adding employee without middle name");
            
            String result = mf.performAddEmployee(cfg);
            
            validateTest("TC_PIM_002_addEmployeeNoMiddleName", className, result);
            
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void TC_PIM_003_addEmployeeMissingFirstName(Config cfg, String className) {
        try {
            currentTest.info("Testing missing first name validation");
            
            String result = mf.performAddEmployee(cfg);
            
            validateTest("TC_PIM_003_addEmployeeMissingFirstName", className, result);
            
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void TC_PIM_004_addEmployeeMissingLastName(Config cfg, String className) {
        try {
            currentTest.info("Testing missing last name validation");
            
            String result = mf.performAddEmployee(cfg);
            
            validateTest("TC_PIM_004_addEmployeeMissingLastName", className, result);
            
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void TC_PIM_005_addEmployeeMissingBothNames(Config cfg, String className) {
        try {
            currentTest.info("Testing missing both first and last name");
            
            String result = mf.performAddEmployee(cfg);
            
            validateTest("TC_PIM_005_addEmployeeMissingBothNames", className, result);
            
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================
    // TEST CASES - SEARCH EMPLOYEE
    // ============================================

    private void TC_PIM_006_searchByValidName(Config cfg, String className) {
        try {
            currentTest.info("Searching employee by valid name");
            
            // First add an employee to search for
            String empId = mf.performAddEmployee(cfg);
            currentTest.info("Employee created with ID: " + empId);
            
            // Now search by name
            String result = mf.performSearchEmployee(cfg);
            
            validateTest("TC_PIM_006_searchByValidName", className, result);
            
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private void TC_PIM_008_searchByInvalidName(Config cfg, String className) {
        try {
            currentTest.info("Searching with non-existent employee name");
            
            String result = mf.performSearchEmployee(cfg);
            
            validateTest("TC_PIM_008_searchByInvalidName", className, result);
            
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void TC_PIM_009_searchByInvalidId(Config cfg, String className) {
        try {
            currentTest.info("Searching with non-existent employee ID");
            String result = mf.performSearchEmployee(cfg);
            validateTest("TC_PIM_009_searchByInvalidId", className, result);
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
}