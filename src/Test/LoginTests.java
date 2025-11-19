////package Test;
////
////import java.time.Duration;
////import java.text.SimpleDateFormat;
////import java.util.ArrayList;
////import java.util.Arrays;
////import java.util.Date;
////import java.util.List;
////import java.io.File;
////import java.io.IOException;
////import java.lang.reflect.Method;
////import java.nio.file.Files;
////import java.nio.file.Paths;
////
////import org.openqa.selenium.support.ui.ExpectedConditions;
////import org.openqa.selenium.support.ui.WebDriverWait;
////import org.testng.annotations.Test;
////
////import com.aventstack.extentreports.ExtentTest;
////
////import controller.loginCtrl;
////import utilites.Config;
////import testbase.BaseTemplate;
////import utilites.CustomFunction;
////import utilites.MainFunctions;
////import utilites.ResultChecker;
////
////public class LoginTests extends BaseTemplate {
////
////    MainFunctions mf;
////    ResultChecker ResultCheck = new ResultChecker();
////
////    private ExtentTest currentTest;
////
////    // Array that contains test names to run
////    String[] testsList = null;
////
////    // Currently running test in the loop.
////    String activeTest = null;
////
////    @Test
////    public void RunTests() throws IOException, InterruptedException {
////
////        // Timestamp for this run
////        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
////
////        // className: "LoginTests"
////        String className = this.getClass().getSimpleName();
////
////        System.out.println("[" + timestamp + "] Current class name: " + className);
////        System.out.println("Starting Tests...");
////
////        // --------------------------------------------------------------------
////        // Normalize CLI test names from program arguments (-testNames)
////        // --------------------------------------------------------------------
////        if (testNmaes_login == null || testNmaes_login.trim().isEmpty()) {
////            testNmaes_login = "ALL"; // Default behavior: run all test cases
////        } else {
////            testNmaes_login = testNmaes_login.trim();
////        }
////
////        if (!"ALL".equalsIgnoreCase(testNmaes_login)) {
////
////            if (testNmaes_login.contains(",")) {
////                String[] parts = testNmaes_login.split(",");
////                List<String> cleaned = new ArrayList<>();
////
////                for (String p : parts) {
////                    String trimmed = p.trim();
////                    if (!trimmed.isEmpty()) {
////                        cleaned.add(trimmed);
////                    }
////                }
////
////                testsList = cleaned.toArray(new String[0]);
////
////            } else {
////                testsList = new String[1];
////                testsList[0] = testNmaes_login;
////                System.out.println("Single test from args: " + testNmaes_login);
////            }
////
////        } else {
////
////            System.out.println("Discovering test cases from class methods...");
////
////            Method[] methods = this.getClass().getDeclaredMethods();
////            List<String> tmp = new ArrayList<>();
////
////            for (Method m : methods) {
////                if (m.getName().startsWith("TC_")) {
////                    tmp.add(m.getName());
////                }
////            }
////
////            testsList = tmp.toArray(new String[0]);
////
////            System.out.println("Discovered test cases: " + Arrays.toString(testsList));
////        }
////
////        System.out.println("Number of tests to run: " + testsList.length);
////
////        // --------------------------------------------------------------------
////        // Main loop → run each selected test case
////        // --------------------------------------------------------------------
////        for (int i = 0; i < testsList.length; i++) {
////
////            activeTest = testsList[i];
////            addCurrentTestMthod(activeTest);
////
////            // CREATE EXTENT TEST FOR THIS TEST CASE
////            currentTest = extent.createTest(activeTest);
////            currentTest.info("Starting test: " + activeTest);
////
////            String testCaseName = activeTest;
////            System.out.println("_______");
////            System.out.println("Generated Test Case Name: " + testCaseName);
////
////            String tcRoot = inputPath(className, testCaseName);
////            System.out.println("TestCase root directory: " + tcRoot);
////
////            // Clean only this test case artifacts
////            MainFunctions.deleteFiles(actualPath(className, testCaseName));
////            MainFunctions.deleteFiles(diffPath(className, testCaseName));
////            System.out.println("Processing test: " + activeTest);
////
////            try {
////                // Load config for this specific test case
////                Config testConfig = loadthisTestConfig(className, testCaseName);
////
////                // Initialize reusable flows helper for this test case
////                mf = new MainFunctions(driver, testConfig);
////
////                switch (activeTest) {
////                    case "TC_LOG_001_validLogin":
////                        TC_LOG_001_validLogin(testConfig, className);
////                        break;
////                    case "TC_LOG_003_emptyFields":
////                        TC_LOG_003_emptyFields(testConfig, className);
////                        break;
////                    case "TC_LOG_005_passwordHidden":
////                        TC_LOG_005_passwordHidden(testConfig, className);
////                        break;
////                    case "TC_PIM_001_openList":
////                        TC_PIM_001_openList(testConfig, className);
////                        break;
////                    default:
////                        System.out.println("WARNING: Unknown test name: " + activeTest + " (skipped)");
////                        currentTest.warning("Test case not found in switch statement");
////                }
////
////                System.out.println("Test finished: " + activeTest);
////
////            } catch (Throwable e) {
////                System.out.println("Error in test: " + activeTest);
////                e.printStackTrace();
////                currentTest.fail("Exception occurred: " + e.getMessage());
////                currentTest.fail(e);
////            }
////        }
////
////        System.out.println("All tests finished.");
////        // ما في داعي نعمل extent.flush هنا، لأنه بيصير في @AfterSuite
////    }
////
////    // ========================================================================
////    // Artifacts handling with baseline logic
////    // ========================================================================
////    private void saveDataArtifacts(String className, String testName,
////                                  String actualData, boolean res) {
////
////        try {
////            String baseName     = "baseline.txt";
////            String actualFile   = actualPath(className, testName)   + baseName;
////            String expectedFile = expectedPath(className, testName) + baseName;
////            String diffFile     = diffPath(className, testName)     + "baseline_diff.txt";
////
////            // 1) Always write current actual data
////            CustomFunction.writeTextFile(actualFile, actualData);
////
////            File expected = new File(expectedFile);
////
////            if (!expected.exists() || expected.length() == 0) {
////
////                // --- FIRST RUN → baseline = actual ---
////                CustomFunction.writeTextFile(expectedFile, actualData);
////
////                CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
////                CustomFunction.appendToFile("Baseline created for test: " + testName, diffFile);
////                CustomFunction.appendToFile("Expected (baseline): " + actualData, diffFile);
////                CustomFunction.appendToFile("Actual: " + actualData, diffFile);
////                CustomFunction.appendToFile("Result  : BASELINE_CREATED_PASS", diffFile);
////
////                currentTest.pass("Baseline created - First run");
////
////            } else {
////
////                // --- NORMAL RUN ---
////                String baseline = Files.readString(Paths.get(expectedFile));
////                boolean contentMatches = baseline.trim().equals(actualData.trim());
////                boolean finalResult = res && contentMatches;
////
////                CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
////                CustomFunction.appendToFile("Expected: " + baseline, diffFile);
////                CustomFunction.appendToFile("Actual  : " + actualData, diffFile);
////                CustomFunction.appendToFile("Result  : " + (finalResult ? "PASS" : "FAIL"), diffFile);
////
////                if (finalResult) {
////                    currentTest.pass("Test validation passed");
////                } else {
////                    currentTest.fail("Expected: " + baseline + " | Actual: " + actualData);
////                }
////            }
////
////        } catch (Exception ex) {
////            System.err.println("Failed to write artifacts for " + testName);
////            ex.printStackTrace();
////            currentTest.fail("Artifact save failed: " + ex.getMessage());
////        }
////    }
////
////    // ========================================================================
////    // Test cases
////    // ========================================================================
////    private void TC_LOG_001_validLogin(Config testConfig, String className) {
////
////        System.out.println("[TC_LOG_001_validLogin] start");
////        currentTest.info("Performing login with valid credentials");
////
////        mf.performLogin(testConfig);
////
////        String actualURL = driver.getCurrentUrl();
////        String expectedPartialURL = "/dashboard";
////
////        boolean ok = actualURL.contains(expectedPartialURL)
////                || driver.getPageSource().toLowerCase().contains("dashboard");
////
////        String actualData = actualURL;
////
////        currentTest.info("Current URL: " + actualURL);
////        currentTest.info("Validation result: " + (ok ? "PASS" : "FAIL"));
////
////        saveDataArtifacts(className, "TC_LOG_001_validLogin", actualData, ok);
////
////        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_001_validLogin", SuitePath);
////        System.out.println("[TC_LOG_001_validLogin] FINAL RESULT = " + finalResult);
////
////        if ("PASS".equalsIgnoreCase(finalResult)) {
////            currentTest.pass("TC_LOG_001_validLogin: " + finalResult);
////        } else {
////            currentTest.fail("TC_LOG_001_validLogin: " + finalResult);
////        }
////
////        MainFunctions.performLogout(driver);
////    }
////
////    private void TC_LOG_003_emptyFields(Config cfg, String className) {
////
////        System.out.println("[TC_LOG_003_emptyFields] start");
////        currentTest.info("Testing empty fields validation");
////
////        boolean overallPass = mf.runEmptyFieldsScenarios(cfg);
////
////        String actualData = overallPass ? "VALIDATION_TRIGGERED_OK" : "VALIDATION_TRIGGERED_MISMATCH";
////
////        saveDataArtifacts(className, "TC_LOG_003_emptyFields", actualData, overallPass);
////
////        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_003_emptyFields", SuitePath);
////        System.out.println("TC_LOG_003_emptyFields: FINAL RESULT = " + finalResult);
////
////        if ("PASS".equalsIgnoreCase(finalResult)) {
////            currentTest.pass("TC_LOG_003_emptyFields: " + finalResult);
////        } else {
////            currentTest.fail("TC_LOG_003_emptyFields: " + finalResult);
////        }
////    }
////
////    private void TC_LOG_005_passwordHidden(Config cfg, String className) {
////
////        System.out.println("[TC_LOG_005_passwordHidden] start");
////        currentTest.info("Checking password field masking");
////
////        boolean ok = mf.isPasswordFieldMasked(cfg, "my245");
////
////        String actualData = "MASKED=" + ok;
////
////        saveDataArtifacts(className, "TC_LOG_005_passwordHidden", actualData, ok);
////
////        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_005_passwordHidden", SuitePath);
////        System.out.println("TC_LOG_005_passwordHidden: FINAL RESULT = " + finalResult);
////
////        if ("PASS".equalsIgnoreCase(finalResult)) {
////            currentTest.pass("TC_LOG_005_passwordHidden: " + finalResult);
////        } else {
////            currentTest.fail("TC_LOG_005_passwordHidden: " + finalResult);
////        }
////    }
////
////    private void TC_PIM_001_openList(Config cfg, String className) {
////
////        System.out.println("[TC_PIM_001_openList] start");
////        currentTest.info("Opening PIM module and verifying header");
////
////        loginIfNeeded(cfg);
////
////        boolean ok = mf.openPimAndVerifyHeader();
////
////        String actualData = "PIM_HEADER_VISIBLE=" + ok;
////
////        saveDataArtifacts(className, "TC_PIM_001_openList", actualData, ok);
////
////        String finalResult = ResultCheck.checkTestResult(className, "TC_PIM_001_openList", SuitePath);
////        System.out.println("TC_PIM_001_openList: FINAL RESULT = " + finalResult);
////
////        if ("PASS".equalsIgnoreCase(finalResult)) {
////            currentTest.pass("TC_PIM_001_openList: " + finalResult);
////        } else {
////            currentTest.fail("TC_PIM_001_openList: " + finalResult);
////        }
////    }
////
////    // ========================================================================
////    // Helper methods
////    // ========================================================================
////    private void openLoginPage(String baseUrl) {
////        driver.get(baseUrl + "/auth/login");
////    }
////
////    private void waitForDashboard() {
////        new WebDriverWait(driver, Duration.ofSeconds(20))
////                .until(ExpectedConditions.or(
////                        ExpectedConditions.urlContains("/dashboard"),
////                        d -> d.getPageSource().toLowerCase().contains("dashboard")
////                ));
////    }
////
////    private void loginIfNeeded(Config testConfig) {
////        try {
////            String current = driver.getCurrentUrl();
////            if (current != null && current.contains("/dashboard")) return;
////        } catch (Exception ignored) {}
////
////        openLoginPage(testConfig.getBaseURL());
////        loginCtrl.fillUsername(driver, testConfig.getAuth().getUserName());
////        loginCtrl.fillpassword(driver, testConfig.getAuth().getPassWord());
////        loginCtrl.clicklogin(driver);
////
////        waitForDashboard();
////    }
////}
//
//package Test;
//
//import java.time.Duration;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.Method;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.testng.annotations.Test;
//
//import com.aventstack.extentreports.ExtentTest;
//import com.aventstack.extentreports.Status;
//import com.aventstack.extentreports.markuputils.MarkupHelper;
//import com.aventstack.extentreports.markuputils.ExtentColor;
//
//import controller.loginCtrl;
//import utilites.Config;
//import testbase.BaseTemplate;
//import utilites.CustomFunction;
//import utilites.MainFunctions;
//import utilites.ResultChecker;
//
//public class LoginTests extends BaseTemplate {
//
//    MainFunctions mf;
//    ResultChecker ResultCheck = new ResultChecker();
//
//    private ExtentTest currentTest;
//
//    // Array that contains test names to run
//    String[] testsList = null;
//
//    // Currently running test in the loop.
//    String activeTest = null;
//
//    // Helper to log a step
//    private void step(String message) {
//        System.out.println("[STEP] " + message);
//        currentTest.info(message);
//    }
//
//    @Test
//    public void RunTests() throws IOException, InterruptedException {
//
//        // Timestamp for this run
//        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//
//        // className: "LoginTests"
//        String className = this.getClass().getSimpleName();
//
//        System.out.println("[" + timestamp + "] Current class name: " + className);
//        System.out.println("Starting Tests...");
//
//        // --------------------------------------------------------------------
//        // Normalize CLI test names from program arguments (-testNames)
//        // --------------------------------------------------------------------
//        if (testNmaes_login == null || testNmaes_login.trim().isEmpty()) {
//            testNmaes_login = "ALL"; // Default behavior: run all test cases
//        } else {
//            testNmaes_login = testNmaes_login.trim();
//        }
//
//        if (!"ALL".equalsIgnoreCase(testNmaes_login)) {
//
//            if (testNmaes_login.contains(",")) {
//                String[] parts = testNmaes_login.split(",");
//                List<String> cleaned = new ArrayList<>();
//
//                for (String p : parts) {
//                    String trimmed = p.trim();
//                    if (!trimmed.isEmpty()) {
//                        cleaned.add(trimmed);
//                    }
//                }
//
//                testsList = cleaned.toArray(new String[0]);
//
//            } else {
//                testsList = new String[1];
//                testsList[0] = testNmaes_login;
//                System.out.println("Single test from args: " + testNmaes_login);
//            }
//
//        } else {
//
//            System.out.println("Discovering test cases from class methods...");
//
//            Method[] methods = this.getClass().getDeclaredMethods();
//            List<String> tmp = new ArrayList<>();
//
//            for (Method m : methods) {
//                if (m.getName().startsWith("TC_")) {
//                    tmp.add(m.getName());
//                }
//            }
//
//            testsList = tmp.toArray(new String[0]);
//
//            System.out.println("Discovered test cases: " + Arrays.toString(testsList));
//        }
//
//        System.out.println("Number of tests to run: " + testsList.length);
//
//        // --------------------------------------------------------------------
//        // Main loop → run each selected test case
//        // --------------------------------------------------------------------
//        for (int i = 0; i < testsList.length; i++) {
//
//            activeTest = testsList[i];
//            addCurrentTestMthod(activeTest);
//
//            // CREATE EXTENT TEST FOR THIS TEST CASE
//            currentTest = extent.createTest(activeTest);
//            currentTest.info("Starting test: " + activeTest);
//
//            // Categories / Author / Device
//            if (activeTest.contains("LOG")) {
//                currentTest.assignCategory("Login");
//            } else if (activeTest.contains("PIM")) {
//                currentTest.assignCategory("PIM");
//            }
//            currentTest.assignCategory("Regression");
//            currentTest.assignAuthor("Saja");
//            currentTest.assignDevice(browser != null ? browser.toUpperCase() : "CHROME");
//
//            String testCaseName = activeTest;
//            System.out.println("_______");
//            System.out.println("Generated Test Case Name: " + testCaseName);
//
//            String tcRoot = inputPath(className, testCaseName);
//            System.out.println("TestCase root directory: " + tcRoot);
//
//            // Clean only this test case artifacts
//            MainFunctions.deleteFiles(actualPath(className, testCaseName));
//            MainFunctions.deleteFiles(diffPath(className, testCaseName));
//            System.out.println("Processing test: " + activeTest);
//
//            try {
//                // Load config for this specific test case
//                Config testConfig = loadthisTestConfig(className, testCaseName);
//
//                // Initialize reusable flows helper for this test case
//                mf = new MainFunctions(driver, testConfig);
//
//                switch (activeTest) {
//                    case "TC_LOG_001_validLogin":
//                        TC_LOG_001_validLogin(testConfig, className);
//                        break;
//                    case "TC_LOG_003_emptyFields":
//                        TC_LOG_003_emptyFields(testConfig, className);
//                        break;
//                    case "TC_LOG_005_passwordHidden":
//                        TC_LOG_005_passwordHidden(testConfig, className);
//                        break;
//                    case "TC_PIM_001_openList":
//                        TC_PIM_001_openList(testConfig, className);
//                        break;
//                    default:
//                        System.out.println("WARNING: Unknown test name: " + activeTest + " (skipped)");
//                        currentTest.warning("Test case not found in switch statement");
//                }
//
//                System.out.println("Test finished: " + activeTest);
//
//            } catch (Throwable e) {
//                System.out.println("Error in test: " + activeTest);
//                e.printStackTrace();
//                currentTest.fail("Exception occurred: " + e.getMessage());
//                currentTest.fail(e);
//
//                // Screenshot for any unexpected exception
//                String ssPath = takeScreenshot(className, activeTest);
//                if (ssPath != null) {
//                    currentTest.addScreenCaptureFromPath(ssPath);
//                }
//            }
//        }
//
//        System.out.println("All tests finished.");
//        // flush is handled in @AfterSuite
//    }
//
//    // ========================================================================
//    // Artifacts handling with baseline logic
//    // ========================================================================
//    private void saveDataArtifacts(String className, String testName,
//                                  String actualData, boolean res) {
//
//        try {
//            String baseName     = "baseline.txt";
//            String actualFile   = actualPath(className, testName)   + baseName;
//            String expectedFile = expectedPath(className, testName) + baseName;
//            String diffFile     = diffPath(className, testName)     + "baseline_diff.txt";
//
//            // 1) Always write current actual data
//            CustomFunction.writeTextFile(actualFile, actualData);
//
//            File expected = new File(expectedFile);
//
//            if (!expected.exists() || expected.length() == 0) {
//
//                // --- FIRST RUN → baseline = actual ---
//                CustomFunction.writeTextFile(expectedFile, actualData);
//
//                CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
//                CustomFunction.appendToFile("Baseline created for test: " + testName, diffFile);
//                CustomFunction.appendToFile("Expected (baseline): " + actualData, diffFile);
//                CustomFunction.appendToFile("Actual: " + actualData, diffFile);
//                CustomFunction.appendToFile("Result  : BASELINE_CREATED_PASS", diffFile);
//
//                currentTest.pass("Baseline created - First run");
//
//            } else {
//
//                // --- NORMAL RUN ---
//                String baseline = Files.readString(Paths.get(expectedFile));
//                boolean contentMatches = baseline.trim().equals(actualData.trim());
//                boolean finalResult = res && contentMatches;
//
//                CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
//                CustomFunction.appendToFile("Expected: " + baseline, diffFile);
//                CustomFunction.appendToFile("Actual  : " + actualData, diffFile);
//                CustomFunction.appendToFile("Result  : " + (finalResult ? "PASS" : "FAIL"), diffFile);
//
//                // Expected vs Actual block inside report
//                String diffSummary =
//                        "Expected:\n" + baseline + "\n\n" +
//                        "Actual:\n"   + actualData  + "\n";
//
//                currentTest.info(
//                    MarkupHelper.createCodeBlock(diffSummary)
//                );
//
//                if (finalResult) {
//                    currentTest.log(Status.PASS,
//                            MarkupHelper.createLabel("Test validation passed (baseline match)", ExtentColor.GREEN));
//                } else {
//                    currentTest.log(Status.FAIL,
//                            MarkupHelper.createLabel("Baseline mismatch", ExtentColor.RED));
//                }
//            }
//
//        } catch (Exception ex) {
//            System.err.println("Failed to write artifacts for " + testName);
//            ex.printStackTrace();
//            currentTest.fail("Artifact save failed: " + ex.getMessage());
//        }
//    }
//
//    // ========================================================================
//    // Test cases
//    // ========================================================================
//    private void TC_LOG_001_validLogin(Config testConfig, String className) {
//
//        System.out.println("[TC_LOG_001_validLogin] start");
//        step("Precondition: Navigate to login page & open login form");
//        step("Action: Perform login with valid credentials");
//
//        mf.performLogin(testConfig);
//
//        String actualURL = driver.getCurrentUrl();
//        String expectedPartialURL = "/dashboard";
//
//        boolean ok = actualURL.contains(expectedPartialURL)
//                || driver.getPageSource().toLowerCase().contains("dashboard");
//
//        String actualData = actualURL;
//
//        step("Verification: User redirected to dashboard");
//        step("Current URL: " + actualURL);
//        step("Validation result: " + (ok ? "PASS" : "FAIL"));
//
//        saveDataArtifacts(className, "TC_LOG_001_validLogin", actualData, ok);
//
//        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_001_validLogin", SuitePath);
//        step("Final baseline result: " + finalResult);
//
//        if ("PASS".equalsIgnoreCase(finalResult)) {
//            currentTest.pass("TC_LOG_001_validLogin: " + finalResult);
//        } else {
//            currentTest.fail("TC_LOG_001_validLogin: " + finalResult);
//            String ssPath = takeScreenshot(className, "TC_LOG_001_validLogin");
//            if (ssPath != null) {
//                currentTest.addScreenCaptureFromPath(ssPath);
//            }
//        }
//
//        MainFunctions.performLogout(driver);
//    }
//
//    private void TC_LOG_003_emptyFields(Config cfg, String className) {
//
//        System.out.println("[TC_LOG_003_emptyFields] start");
//        step("Precondition: Ensure login page is opened");
//
//        openLoginPage(cfg.getBaseURL());
//
//        step("Action: Run empty-fields validation scenarios");
//
//        boolean overallPass = mf.runEmptyFieldsScenarios(cfg);
//
//        String actualData = overallPass ? "VALIDATION_TRIGGERED_OK" : "VALIDATION_TRIGGERED_MISMATCH";
//
//        saveDataArtifacts(className, "TC_LOG_003_emptyFields", actualData, overallPass);
//
//        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_003_emptyFields", SuitePath);
//        step("Final baseline result: " + finalResult);
//
//        if ("PASS".equalsIgnoreCase(finalResult)) {
//            currentTest.pass("TC_LOG_003_emptyFields: " + finalResult);
//        } else {
//            currentTest.fail("TC_LOG_003_emptyFields: " + finalResult);
//            String ssPath = takeScreenshot(className, "TC_LOG_003_emptyFields");
//            if (ssPath != null) {
//                currentTest.addScreenCaptureFromPath(ssPath);
//            }
//        }
//    }
//
//    private void TC_LOG_005_passwordHidden(Config cfg, String className) {
//
//        System.out.println("[TC_LOG_005_passwordHidden] start");
//        step("Precondition: Ensure login page is opened");
//
//        openLoginPage(cfg.getBaseURL());
//
//        step("Action: Type password & check masking");
//
//        boolean ok = mf.isPasswordFieldMasked(cfg, "my245");
//
//        String actualData = "MASKED=" + ok;
//
//        saveDataArtifacts(className, "TC_LOG_005_passwordHidden", actualData, ok);
//
//        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_005_passwordHidden", SuitePath);
//        step("Final baseline result: " + finalResult);
//
//        if ("PASS".equalsIgnoreCase(finalResult)) {
//            currentTest.pass("TC_LOG_005_passwordHidden: " + finalResult);
//        } else {
//            currentTest.fail("TC_LOG_005_passwordHidden: " + finalResult);
//            String ssPath = takeScreenshot(className, "TC_LOG_005_passwordHidden");
//            if (ssPath != null) {
//                currentTest.addScreenCaptureFromPath(ssPath);
//            }
//        }
//    }
//
//   
//
//    private void TC_PIM_001_openList(Config cfg, String className) {
//
//        System.out.println("[TC_PIM_001_openList] start");
//        step("Precondition: User is logged in");
//        step("Action: Open PIM module and navigate to employee list");
//
//        loginIfNeeded(cfg);
//
//        boolean ok = mf.openPimAndVerifyHeader();
//
//        String actualData = "PIM_HEADER_VISIBLE=" + ok;
//
//        saveDataArtifacts(className, "TC_PIM_001_openList", actualData, ok);
//
//        String finalResult = ResultCheck.checkTestResult(className, "TC_PIM_001_openList", SuitePath);
//        step("Final baseline result: " + finalResult);
//
//        if ("PASS".equalsIgnoreCase(finalResult)) {
//            currentTest.pass("TC_PIM_001_openList: " + finalResult);
//        } else {
//            currentTest.fail("TC_PIM_001_openList: " + finalResult);
//            String ssPath = takeScreenshot(className, "TC_PIM_001_openList");
//            if (ssPath != null) {
//                currentTest.addScreenCaptureFromPath(ssPath);
//            }
//            
//        }
//        try {
//            step("Cleanup: Logout after PIM test");
//            MainFunctions.performLogout(driver);
//        } catch (Exception e) {
//            System.out.println("[TC_PIM_001_openList] logout failed (ignored)");
//        }
//    }
//
//    // ========================================================================
//    // Helper methods
//    // ========================================================================
//    private void openLoginPage(String baseUrl) {
//        driver.get(baseUrl + "/auth/login");
//    }
//
//    private void waitForDashboard() {
//        new WebDriverWait(driver, Duration.ofSeconds(20))
//                .until(ExpectedConditions.or(
//                        ExpectedConditions.urlContains("/dashboard"),
//                        d -> d.getPageSource().toLowerCase().contains("dashboard")
//                ));
//    }
//
//    private void loginIfNeeded(Config testConfig) {
//        try {
//            String current = driver.getCurrentUrl();
//            if (current != null && current.contains("/dashboard")) return;
//        } catch (Exception ignored) {}
//
//        openLoginPage(testConfig.getBaseURL());
//        loginCtrl.fillUsername(driver, testConfig.getAuth().getUserName());
//        loginCtrl.fillpassword(driver, testConfig.getAuth().getPassWord());
//        loginCtrl.clicklogin(driver);
//
//        waitForDashboard();
//        
//    }
//    
//}
//


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

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import controller.loginCtrl;
import utilites.Config;
import testbase.BaseTemplate;
import utilites.CustomFunction;
import utilites.MainFunctions;
import utilites.ResultChecker;
import reporting.ExtentManager;

public class LoginTests extends BaseTemplate {

    MainFunctions mf;
    ResultChecker ResultCheck = new ResultChecker();

    private static ExtentReports extent;
    private ExtentTest currentTest;

    // Array that contains test names to run
    String[] testsList = null;

    String activeTest = null;

    @Test
    public void RunTests() throws IOException, InterruptedException {

        // Initialize Extent Reports
        extent = ExtentManager.getInstance();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // className LoginTests
        String className = this.getClass().getSimpleName();

        System.out.println("[" + timestamp + "] Current class name: " + className);
        System.out.println("Starting Tests...");

     
        //  CLI  arg (-testNames)
   
        if (testNmaes_login == null || testNmaes_login.trim().isEmpty()) {
            testNmaes_login = "ALL"; // Default behavior: run all test cases
        } else {
            testNmaes_login = testNmaes_login.trim();
        }

        if (!"ALL".equalsIgnoreCase(testNmaes_login)) {

            if (testNmaes_login.contains(",")) {
                String[] parts = testNmaes_login.split(",");
                List<String> cleaned = new ArrayList<>();

                for (String p : parts) {
                    String trimmed = p.trim();
                    if (!trimmed.isEmpty()) {
                        cleaned.add(trimmed);
                    }
                }

                testsList = cleaned.toArray(new String[0]);

            } else {
                testsList = new String[1];
                testsList[0] = testNmaes_login;
                System.out.println("Single test from args: " + testNmaes_login);
            }

        } else {

            System.out.println("Discovering test cases from class methods...");

            Method[] methods = this.getClass().getDeclaredMethods();
            List<String> TESTS = new ArrayList<>();

            for (Method m : methods) {
                if (m.getName().startsWith("TC_")) {
                   TESTS.add(m.getName());
                }
            }

            testsList = TESTS.toArray(new String[0]);

            System.out.println("Discovered test cases: " + Arrays.toString(testsList));
        }

        System.out.println("Number of tests to run: " + testsList.length);

        
        // Main loop → run each selected test case
        
        for (int i = 0; i < testsList.length; i++) {

            activeTest = testsList[i];
            addCurrentTestMthod(activeTest);

            // CREATE EXTENT TEST FOR THIS TEST CASE
            currentTest = extent.createTest(activeTest);
            currentTest.info("Starting test: " + activeTest);

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
                // Load config for this  test case
                Config testConfig = loadthisTestConfig(className, testCaseName);

                
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
                        currentTest.warning("Test case not found in switch statement");
                }

                System.out.println("Test finished: " + activeTest);

            } catch (Throwable e) {
                System.out.println("Error in test: " + activeTest);
                e.printStackTrace();
                currentTest.fail("Exception occurred: " + e.getMessage());
                currentTest.fail(e);
            }
        }

        System.out.println("All tests finished.");

       
        extent.flush();
    }

    // ========================================================================
    // FOLDERS handling baseline logic
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

                currentTest.pass("Baseline created - First run");

            } else {

                // --- NORMAL RUN ---
                String baseline = Files.readString(Paths.get(expectedFile));
                boolean contentMatches = baseline.trim().equals(actualData.trim());
                boolean finalResult = res && contentMatches;

                CustomFunction.appendToFile("___" + testName + " DIFF___", diffFile);
                CustomFunction.appendToFile("Expected: " + baseline, diffFile);
                CustomFunction.appendToFile("Actual  : " + actualData, diffFile);
                CustomFunction.appendToFile("Result  : " + (finalResult ? "PASS" : "FAIL"), diffFile);

                if (finalResult) {
                    currentTest.pass("Test validation passed");
                } else {
                    currentTest.fail("Expected: " + baseline + " | Actual: " + actualData);
                }
            }

        } catch (Exception ex) {
            System.err.println("Failed to write artifacts for " + testName);
            ex.printStackTrace();
            currentTest.fail("Artifact save failed: " + ex.getMessage());
        }
    }


    // Test cases

    private void TC_LOG_001_validLogin(Config testConfig, String className) {

        System.out.println("[TC_LOG_001_validLogin] start");
        currentTest.info("Performing login with valid credentials");

        mf.performLogin(testConfig);

        String actualURL = driver.getCurrentUrl();
        String expectedPartialURL = "/dashboard";

        boolean ok = actualURL.contains(expectedPartialURL)
                || driver.getPageSource().toLowerCase().contains("dashboard");

        String actualData = actualURL;

        currentTest.info("Current URL: " + actualURL);
        currentTest.info("Validation result: " + (ok ? "PASS" : "FAIL"));

        saveDataArtifacts(className, "TC_LOG_001_validLogin", actualData, ok);

        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_001_validLogin", SuitePath);
        System.out.println("[TC_LOG_001_validLogin] FINAL RESULT = " + finalResult);

        if ("PASS".equalsIgnoreCase(finalResult)) {
            currentTest.pass("TC_LOG_001_validLogin: " + finalResult);
        } else {
            currentTest.fail("TC_LOG_001_validLogin: " + finalResult);
        }

        MainFunctions.performLogout(driver);
    }

    private void TC_LOG_003_emptyFields(Config cfg, String className) {

        System.out.println("[TC_LOG_003_emptyFields] start");
        currentTest.info("Testing empty fields validation");

        boolean overallPass = mf.runEmptyFieldsScenarios(cfg);

        String actualData = overallPass ? "VALIDATION_TRIGGERED_OK" : "VALIDATION_TRIGGERED_MISMATCH";

        saveDataArtifacts(className, "TC_LOG_003_emptyFields", actualData, overallPass);

        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_003_emptyFields", SuitePath);
        System.out.println("TC_LOG_003_emptyFields: FINAL RESULT = " + finalResult);

        if ("PASS".equalsIgnoreCase(finalResult)) {
            currentTest.pass("TC_LOG_003_emptyFields: " + finalResult);
        } else {
            currentTest.fail("TC_LOG_003_emptyFields: " + finalResult);
        }
    }

    private void TC_LOG_005_passwordHidden(Config cfg, String className) {

        System.out.println("[TC_LOG_005_passwordHidden] start");
        currentTest.info("Checking password field masking");

        boolean ok = mf.isPasswordFieldMasked(cfg, "my245");

        String actualData = "MASKED=" + ok;

        saveDataArtifacts(className, "TC_LOG_005_passwordHidden", actualData, ok);

        String finalResult = ResultCheck.checkTestResult(className, "TC_LOG_005_passwordHidden", SuitePath);
        System.out.println("TC_LOG_005_passwordHidden: FINAL RESULT = " + finalResult);

        if ("PASS".equalsIgnoreCase(finalResult)) {
            currentTest.pass("TC_LOG_005_passwordHidden: " + finalResult);
        } else {
            currentTest.fail("TC_LOG_005_passwordHidden: " + finalResult);
        }
    }

    private void TC_PIM_001_openList(Config cfg, String className) {

        System.out.println("[TC_PIM_001_openList] start");
        currentTest.info("Opening PIM module and verifying header");

        loginIfNeeded(cfg);

        boolean ok = mf.openPimAndVerifyHeader();

        String actualData = "PIM_HEADER_VISIBLE=" + ok;

        saveDataArtifacts(className, "TC_PIM_001_openList", actualData, ok);

        String finalResult = ResultCheck.checkTestResult(className, "TC_PIM_001_openList", SuitePath);
        System.out.println("TC_PIM_001_openList: FINAL RESULT = " + finalResult);

        if ("PASS".equalsIgnoreCase(finalResult)) {
            currentTest.pass("TC_PIM_001_openList: " + finalResult);
        } else {
            currentTest.fail("TC_PIM_001_openList: " + finalResult);
        }
    }

    // Helper methods

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

