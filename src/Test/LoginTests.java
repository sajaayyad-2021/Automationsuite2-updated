package Test;

import java.time.Duration;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import controller.loginCtrl;
import utilites.Config;
import testbase.BaseTemplate;
import utilites.CustomFunction;
import utilites.MainFunctions;
import utilites.ResultChecker;
import reporting.ExtentManager;
import reporting.ScreenshotHelper;


public class LoginTests extends BaseTemplate {

    MainFunctions mf;
    ResultChecker ResultCheck = new ResultChecker();

    private static ExtentReports extent;
    private ExtentTest currentTest;

    String[] testsList = null;
    String activeTest = null;

    @Test
    public void RunTests() throws IOException, InterruptedException {

        extent = ExtentManager.getInstance();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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

            activeTest = tc;
            addCurrentTestMthod(activeTest);

            String tcRoot = inputPath(className, tc);

            currentTest = extent.createTest(activeTest);
            currentTest.assignCategory("Regression");
            if (activeTest.contains("LOG")) currentTest.assignCategory("Login");
            if (activeTest.contains("PIM")) currentTest.assignCategory("PIM");

            currentTest.info("Starting test: " + activeTest);

            MainFunctions.deleteFiles(actualPath(className, tc));
            MainFunctions.deleteFiles(diffPath(className, tc));

            try {
                Config cfg = loadthisTestConfig(className, tc);
                mf = new MainFunctions(driver, cfg);

                invokeTestMethod(tc, cfg, className);

                currentTest.pass("Test finished successfully");

            } catch (NoSuchMethodException e) {
                currentTest.skip("No matching test method found for: " + activeTest);

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

                CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
                CustomFunction.appendToFile("Baseline created for test: " + testName, diffFile);
                CustomFunction.appendToFile("Expected (baseline): " + actualData, diffFile);
                CustomFunction.appendToFile("Actual: " + actualData, diffFile);
                CustomFunction.appendToFile("Result  : BASELINE_CREATED_PASS", diffFile);

                currentTest.pass("Baseline created - First run");
                currentTest.info(MarkupHelper.createCodeBlock(
                        "Expected (baseline NEW):\n" + actualData
                ));

            } else {

                String baseline = Files.readString(Paths.get(expectedFile));
                boolean contentMatches = baseline.trim().equals(actualData.trim());
                boolean finalResult = passed && contentMatches;

                CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
                CustomFunction.appendToFile("Expected: " + baseline, diffFile);
                CustomFunction.appendToFile("Actual  : " + actualData, diffFile);
                CustomFunction.appendToFile("Result  : " + (finalResult ? "PASS" : "FAIL"), diffFile);

                String block =
                        "EXPECTED:\n" + baseline + "\n\n" +
                        "ACTUAL:\n"   + actualData + "\n\n" +
                        "RESULT: " + (finalResult ? "PASS" : "FAIL");

                currentTest.info(MarkupHelper.createCodeBlock(block));

                if (contentMatches) {
                    currentTest.log(Status.INFO,
                            "Actual == Expected.");
                } else {
                    currentTest.log(Status.WARNING,
                            "Actual != Expected. Differences stored in: " + diffFile);
                }

                if (finalResult) {
                    currentTest.log(Status.PASS,
                            MarkupHelper.createLabel("Test validation passed", ExtentColor.GREEN));
                } else {
                    currentTest.log(Status.FAIL,
                            MarkupHelper.createLabel("Baseline mismatch", ExtentColor.RED));
                }
            }

        } catch (Exception ex) {
            if (currentTest != null) {
                currentTest.fail("Artifact save failed: " + ex.getMessage());
            }
        }
    }

    private void TC_LOG_001_validLogin(Config cfg, String className) {

        currentTest.info("Performing login with valid credentials");

        mf.performLogin(cfg);

        String actualURL = driver.getCurrentUrl();
        String expectedPartialURL = "/dashboard";

        boolean ok = actualURL.contains(expectedPartialURL)
                || driver.getPageSource().toLowerCase().contains("dashboard");

        currentTest.info("Current URL: " + actualURL);
        currentTest.info("Expected URL to contain: " + expectedPartialURL);
        currentTest.info("Validation result: " + (ok ? "PASS" : "FAIL"));

        saveDataArtifacts(className, "TC_LOG_001_validLogin", actualURL, ok);

        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_001_validLogin", SuitePath);

        if ("PASS".equalsIgnoreCase(finalResult)) {
            currentTest.pass("TC_LOG_001_validLogin: " + finalResult);
        } else {
            currentTest.fail("TC_LOG_001_validLogin: " + finalResult);
        }

        MainFunctions.performLogout(driver);
    }

    private void TC_LOG_003_emptyFields(Config cfg, String className) {

        currentTest.info("Testing empty fields validation");

        boolean ok = mf.runEmptyFieldsScenarios(cfg);
        String data = ok ? "VALIDATION_TRIGGERED_OK" : "VALIDATION_TRIGGERED_MISMATCH";

        currentTest.info("Scenario overall result: " + data);

        saveDataArtifacts(className, "TC_LOG_003_emptyFields", data, ok);

        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_003_emptyFields", SuitePath);

        if ("PASS".equalsIgnoreCase(finalResult)) currentTest.pass(finalResult);
        else currentTest.fail(finalResult);
    }

   
    private void TC_LOG_004_emptyPasswordOnly(Config cfg, String className) {

        currentTest.info("Empty password only");

        mf.resetToLogin();

        loginCtrl.fillUsername(driver, cfg.getAuth().getUserName());
        loginCtrl.clicklogin(driver);

        boolean required = mf.expectRequiredPassword();

        saveDataArtifacts(className,
                "TC_LOG_004_emptyPasswordOnly",
                "RequiredPass=" + required, required);

        String result = ResultCheck.checkTestResult(className,
                "TC_LOG_004_emptyPasswordOnly", SuitePath);

        if (result.equalsIgnoreCase("PASS")) currentTest.pass(result);
        else currentTest.fail(result);
    }
    
    private void TC_LOG_005_passwordHidden(Config cfg, String className) {

        currentTest.info("Checking password field masking");

        boolean ok = mf.isPasswordFieldMasked(cfg, "my245");
        String data = "MASKED=" + ok;

        currentTest.info("Password field masked? " + ok);

        saveDataArtifacts(className, "TC_LOG_005_passwordHidden", data, ok);

        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_005_passwordHidden", SuitePath);

        if ("PASS".equalsIgnoreCase(finalResult)) currentTest.pass(finalResult);
        else currentTest.fail(finalResult);
    }

    private void TC_LOG_006_invalidUsername(Config cfg, String className) {

        currentTest.info("Invalid username");

        mf.resetToLogin();

        boolean dashboard = mf.attemptLogin("WRONG_USER_123", cfg.getAuth().getPassWord());
        boolean invalid = mf.expectInvalidCredentials();

        boolean ok = (!dashboard && invalid);

        String data = "Dashboard=" + dashboard + ", InvalidCred=" + invalid;

        saveDataArtifacts(className, "TC_LOG_006_invalidUsername", data, ok);

        String result = ResultCheck.checkTestResult(className, "TC_LOG_006_invalidUsername", SuitePath);

        if (result.equalsIgnoreCase("PASS")) currentTest.pass(result);
        else currentTest.fail(result);
    }
    
    private void TC_LOG_009_logoutRedirect(Config cfg, String className) {

        currentTest.info("Logout redirect check");

        loginIfNeeded(cfg);

        boolean redirect = mf.logoutRedirectsToLogin();

        saveDataArtifacts(className,
                "TC_LOG_009_logoutRedirect",
                "Redirect=" + redirect, redirect);

        String result = ResultCheck.checkTestResult(className, "TC_LOG_009_logoutRedirect", SuitePath);

        if (result.equalsIgnoreCase("PASS")) currentTest.pass(result);
        else currentTest.fail(result);
    }


    private void openLoginPage(String baseUrl) {
        driver.get(baseUrl + "/auth/login");
    }

    private void waitForDashboard() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("/dashboard"),
                        d -> d.getPageSource().toLowerCase().contains("dashboard")
                ));
    }

    private void loginIfNeeded(Config cfg) {
        try {
            String current = driver.getCurrentUrl();
            if (current != null && current.contains("/dashboard")) return;
        } catch (Exception ignored) {}

        openLoginPage(cfg.getBaseURL());
        loginCtrl.fillUsername(driver, cfg.getAuth().getUserName());
        loginCtrl.fillpassword(driver, cfg.getAuth().getPassWord());
        loginCtrl.clicklogin(driver);
        waitForDashboard();
    }
}
