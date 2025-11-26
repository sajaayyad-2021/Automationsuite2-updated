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
public class LoginTests extends BaseTemplate {

    MainFunctions mf;
    ResultChecker ResultCheck = new ResultChecker();
    private static ExtentReports extent;
    private ExtentTest currentTest;
    String[] testsList = null;

    @Test
    public void LoginSuite() throws IOException, InterruptedException {
        extent = ExtentManager.getInstance();
        String className = this.getClass().getSimpleName();

        if (testNmaes_login == null || testNmaes_login.trim().isEmpty()) {
            testNmaes_login = "ALL";
        } else {
            testNmaes_login = testNmaes_login.trim();
        }

        if (!"ALL".equalsIgnoreCase(testNmaes_login)) {
            if (testNmaes_login.contains(",")) {
                testsList = Arrays.stream(testNmaes_login.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);
            } else {
                testsList = new String[]{ testNmaes_login };
            }
        } else {
            Method[] methods = this.getClass().getDeclaredMethods();
            List<String> TESTS = new ArrayList<>();
            for (Method m : methods) {
                if (m.getName().startsWith("TC_")) {
                    TESTS.add(m.getName());
                }
            }
            testsList = TESTS.toArray(new String[0]);
        }

        for (String tc : testsList) {
            addCurrentTestMthod(tc);
            currentTest = extent.createTest(tc);
            currentTest.assignCategory("Regression");
            currentTest.assignCategory("Login");
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
                currentTest.fail("Exception occurred: " + e.getMessage());
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
    // TEST CASES
    // ============================================

    private void TC_LOG_001_validLogin(Config cfg, String className) {
        try {
            currentTest.info("Performing login with valid credentials");
            
            mf.performLogin(cfg);
            String actualURL = mf.getCurrentURL();
            
            validateTest("TC_LOG_001_validLogin", className, actualURL);
            
            MainFunctions.performLogout(driver);
            
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void TC_LOG_003_emptyFields(Config cfg, String className) {
        try {
           currentTest.info("Testing empty fields validation"); 
           mf.performLogin(cfg);
           boolean hasValidation = mf.hasRequiredValidation();
           String actualValue = hasValidation ? "VALIDATION_SHOWN" : "NO_VALIDATION";
           validateTest("TC_LOG_003_emptyFields", className, actualValue);
            
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void TC_LOG_004_emptyPasswordOnly(Config cfg, String className) {
        try {
            currentTest.info("Empty password only");
            
            mf.performLogin(cfg);
            boolean hasRequired = mf.hasRequiredValidation();
            boolean hasPasswordError = mf.hasPasswordValidationError();
            String actualValue;
            if (hasRequired) {
                actualValue = "REQUIRED_VALIDATION";
            } else if (hasPasswordError) {
                actualValue = "PASSWORD_ERROR";
            } else {
                actualValue = "NO_VALIDATION";
            }
            validateTest("TC_LOG_004_emptyPasswordOnly", className, actualValue);
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
  private void TC_LOG_009_logoutRedirect(Config cfg, String className) {
        try {
            currentTest.info("Logout redirect check");
            
            mf.performLogin(cfg);
            mf.waitForDashboard();
            MainFunctions.performLogout(driver);
            String actualURL = mf.getCurrentURL();
            
            validateTest("TC_LOG_009_logoutRedirect", className, actualURL);
            
        } catch (Exception e) {
            currentTest.fail("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}