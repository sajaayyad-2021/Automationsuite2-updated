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
import org.testng.annotations.Test;

import controller.loginCtrl;
import utilites.Config;
import testbase.BaseTemplate;
import utilites.CustomFunction;
import utilites.MainFunctions;
import utilites.ResultChecker;

public class LoginTests extends BaseTemplate {

    MainFunctions mf;
    ResultChecker ResultCheck = new ResultChecker();

    // Array that contains test names to run
    String[] testsList = null;

    // Currently running test in the loop.
    String activeTest = null;

    @Test
    public void RunTests() throws IOException, InterruptedException {

        // Timestamp for this run
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // className: "LoginTests"
        String className = this.getClass().getSimpleName();

        System.out.println("[" + timestamp + "] Current class name: " + className);
        System.out.println("Starting Tests...");

        // --------------------------------------------------------------------
        // Normalize CLI test names from program arguments (-testNames)
        // --------------------------------------------------------------------
        if (testNmaes_login == null || testNmaes_login.trim().isEmpty()) {
            testNmaes_login = "ALL"; // Default behavior: run all test cases
        } else {
            testNmaes_login = testNmaes_login.trim();
        }

        if (!"ALL".equalsIgnoreCase(testNmaes_login)) {

            // === SPECIFIC TESTS MODE ===
            // Run only the tests provided in -testNames argument

            if (testNmaes_login.contains(",")) {
                // Multiple test names separated by commas
                String[] parts = testNmaes_login.split(",");
                List<String> cleaned = new ArrayList<>();

                for (String p : parts) {
                    String trimmed = p.trim();
                    if (!trimmed.isEmpty()) {
                        cleaned.add(trimmed); // Add valid test name
                    }
                }

                testsList = cleaned.toArray(new String[0]);

            } else {
                // Only one test name provided
                testsList = new String[1];
                testsList[0] = testNmaes_login;
                System.out.println("Single test from args: " + testNmaes_login);
            }

        } else {

            // === DISCOVER ALL TEST CASES FROM CLASS METHODS ===
            // "ALL" → Automatically detect test methods based on naming convention (TC_ prefix)
            System.out.println("Discovering test cases from class methods...");

            Method[] methods = this.getClass().getDeclaredMethods();
            List<String> tmp = new ArrayList<>();

            for (Method m : methods) {
                // Only methods that follow the Test Case naming pattern (ex: TC_LOG_001_validLogin)
                if (m.getName().startsWith("TC_")) {
                    tmp.add(m.getName());
                }
            }

            testsList = tmp.toArray(new String[0]);

            System.out.println("Discovered test cases: " + Arrays.toString(testsList));
        }

        System.out.println("Number of tests to run: " + testsList.length);

        // --------------------------------------------------------------------
        // Main loop → run each selected test case
        // --------------------------------------------------------------------
        for (int i = 0; i < testsList.length; i++) {

            activeTest = testsList[i];
            addCurrentTestMthod(activeTest);

            String testCaseName = activeTest;
            System.out.println("_______");
            System.out.println("Generated Test Case Name: " + testCaseName);

            String tcRoot = inputPath(className, testCaseName);
            System.out.println("TestCase root directory: " + tcRoot);

            // Clean only this test case artifacts
            MainFunctions.deleteFiles(actualPath(className, testCaseName));
            MainFunctions.deleteFiles(diffPath(className, testCaseName));
            System.out.println("Processing test: " + activeTest);

            try {
                // Load config for this specific test case
                Config testConfig = loadthisTestConfig(className, testCaseName);

                // Initialize reusable flows helper for this test case
                mf = new MainFunctions(driver, testConfig);

                switch (activeTest) {
                    case "TC_LOG_001_validLogin":
                        TC_LOG_001_validLogin(testConfig, className);
                        break;
                    case "TC_LOG_003_emptyFields":
                        TC_LOG_003_emptyFields(testConfig, className);
                        break;
                    case "TC_LOG_005_passwordHidden":
                        TC_LOG_005_passwordHidden(testConfig, className);
                        break;
                    case "TC_PIM_001_openList":
                        TC_PIM_001_openList(testConfig, className);
                        break;
                    default:
                        System.out.println("WARNING: Unknown test name: " + activeTest + " (skipped)");
                }

                System.out.println("Test finished: " + activeTest);

            } catch (Throwable e) {
                System.out.println("Error in test: " + activeTest);
                e.printStackTrace();
            }
        }

        System.out.println("All tests finished.");
    }

    // ========================================================================
    // Artifacts handling with baseline logic
    // ========================================================================
    private void saveDataArtifacts(String className, String testName,
            String actualData, boolean res) {

try {
String baseName     = "baseline.txt";
String actualFile   = actualPath(className, testName)   + baseName;
String expectedFile = expectedPath(className, testName) + baseName;
String diffFile     = diffPath(className, testName)     + "baseline_diff.txt";

// 1) Always write current actual data
CustomFunction.writeTextFile(actualFile, actualData);

File expected = new File(expectedFile);

if (!expected.exists() || expected.length() == 0) {

// --- FIRST RUN → baseline = actual ---
CustomFunction.writeTextFile(expectedFile, actualData);

CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
CustomFunction.appendToFile("Baseline created for test: " + testName, diffFile);
CustomFunction.appendToFile("Expected (baseline): " + actualData, diffFile);
CustomFunction.appendToFile("Actual: " + actualData, diffFile);
CustomFunction.appendToFile("Result  : BASELINE_CREATED_PASS", diffFile);

} else {

// --- NORMAL RUN ---
String baseline = Files.readString(Paths.get(expectedFile));
boolean contentMatches = baseline.trim().equals(actualData.trim());
boolean finalResult = res && contentMatches;

CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
CustomFunction.appendToFile("Expected: " + baseline, diffFile);
CustomFunction.appendToFile("Actual  : " + actualData, diffFile);
CustomFunction.appendToFile("Result  : " + (finalResult ? "PASS" : "FAIL"), diffFile);
}

} catch (Exception ex) {
System.err.println("Failed to write artifacts for " + testName);
ex.printStackTrace();
}
}

    // ========================================================================
    // Test cases
    // ========================================================================
    private void TC_LOG_001_validLogin(Config testConfig, String className) {

        System.out.println("[TC_LOG_001_validLogin] start");

        mf.performLogin(testConfig);

        // After successful login, we should be on dashboard
        String actualURL = driver.getCurrentUrl();
        String expectedPartialURL = "/dashboard";

        boolean ok = actualURL.contains(expectedPartialURL)
                || driver.getPageSource().toLowerCase().contains("dashboard");

        String actualData = actualURL;
       

        saveDataArtifacts(className, "TC_LOG_001_validLogin",
                actualData,
                  // empty → baseline = actual on first run
                ok);

        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_001_validLogin", SuitePath);
        System.out.println("[TC_LOG_001_validLogin] FINAL RESULT = " + finalResult);

        MainFunctions.performLogout(driver);
    }

    private void TC_LOG_003_emptyFields(Config cfg, String className) {

        System.out.println("[TC_LOG_003_emptyFields] start");

        // Run all empty-fields scenarios (all three cases) from MainFunctions
        boolean overallPass = mf.runEmptyFieldsScenarios(cfg);

        // For artifacts, we keep a high-level description
        String actualData   = overallPass ? "VALIDATION_TRIGGERED_OK" : "VALIDATION_TRIGGERED_MISMATCH";
     

        saveDataArtifacts(className, "TC_LOG_003_emptyFields",
                actualData, overallPass);

        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_003_emptyFields", SuitePath);
        System.out.println(" TC_LOG_003_emptyFields : FINAL RESULT = " + finalResult);
    }

    private void TC_LOG_005_passwordHidden(Config cfg, String className) {

        System.out.println("[TC_LOG_005_passwordHidden] start");

        // Check password masking behavior via MainFunctions
        boolean ok = mf.isPasswordFieldMasked(cfg, "my245");

        String actualData   = "MASKED=" + ok;
        

        saveDataArtifacts(className, "TC_LOG_005_passwordHidden", actualData, ok);

        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_005_passwordHidden", SuitePath);
        System.out.println(" TC_LOG_005_passwordHidden: FINAL RESULT = " + finalResult);
    }

    private void TC_PIM_001_openList(Config cfg, String className) {

        System.out.println("[TC_PIM_001_openList] start");

        // Ensure we are logged in (MainFunctions login flow can be used inside loginIfNeeded)
        loginIfNeeded(cfg);

        // Use high-level PIM flow from MainFunctions
        boolean ok = mf.openPimAndVerifyHeader();

        String actualData   = "PIM_HEADER_VISIBLE=" + ok;
       

        saveDataArtifacts(className, "TC_PIM_001_openList", actualData, ok);

        String finalResult = ResultCheck.checkTestResult(className, "TC_PIM_001_openList", SuitePath);
        System.out.println("TC_PIM_001_openList: FINAL RESULT = " + finalResult);
    }

    // ========================================================================
    // Helper methods
    // ========================================================================
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

    private void loginIfNeeded(Config testConfig) {
        try {
            String current = driver.getCurrentUrl();
            if (current != null && current.contains("/dashboard")) return;
        } catch (Exception ignored) {}

        openLoginPage(testConfig.getBaseURL());
        loginCtrl.fillUsername(driver, testConfig.getAuth().getUserName());
        loginCtrl.fillpassword(driver, testConfig.getAuth().getPassWord());
        loginCtrl.clicklogin(driver);

        waitForDashboard();
    }
}
