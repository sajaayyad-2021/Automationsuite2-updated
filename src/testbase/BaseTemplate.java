//package testbase;
//
//import java.time.Duration;
//import java.lang.reflect.Method;
//import java.util.LinkedHashSet;
//import java.util.Set;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
//
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.testng.annotations.*;
//
//import com.aventstack.extentreports.ExtentReports;
//
//import reporting.ExtentManager;
//import utilites.Config;
//import utilites.CustomFunction;
//
//public class BaseTemplate {
//
//    // -------------------- WebDriver & Wait --------------------
//    protected static WebDriver driver;
//    protected static WebDriverWait wait;
//
//    // -------------------- Config / CLI properties --------------------
//    protected static String jsoninput;
//    protected static String browser;
//    protected static String Testargs;
//    protected static String url = "";
//    protected static String xml;
//
//    // -------------------- ROOT PATH  --------------------
//    public static String SuitePath;
//    protected static String XML_DIR;
//
//    // -------------------- ExtentReports (shared) --------------------
//    protected static ExtentReports extent;
//
//    // -------------------- Test tracking --------------------
//    public static String testNmaes_login;
//    protected static final Set<String> CURRENT_TEST_NAME = new LinkedHashSet<>();
//
//    // ========================================================================
//    // CLI args only
//    public static void Setargs(String[] args) {
//
//        jsoninput = getArgs(args, "-cfg",      "artifacts\\TestCases\\Tests\\Input\\input.json");
//        SuitePath = getArgs(args, "-out",      "artifacts");
//        browser   = getArgs(args, "-browser",  "chrome");
//        Testargs  = getArgs(args, "-testNames","ALL");
//        xml       = getArgs(args, "-xml",      "testng.xml");
//        url       = getArgs(args, "-url",      "");
//    }
//
//    private static String getArgs(String[] args, String key, String def) {
//        if (args == null)
//            return def;
//
//        for (int i = 0; i < args.length - 1; i++) {
//            if (args[i].equals(key)) {
//                return args[i + 1];
//            }
//        }
//        return def;
//    }
//
//    // ========================================================================
//    // Path helpers (per test class + test case)
//    public static String testCaseRoot(String className, String testName) {
//        return SuitePath + "\\TestCases\\" + className + "\\" + testName + "\\";
//    }
//
//    public static String inputPath(String className, String testName) {
//        return testCaseRoot(className, testName) + "Input\\";
//    }
//
//    public static String actualPath(String className, String testName) {
//        return testCaseRoot(className, testName) + "Actual\\";
//    }
//
//    public static String expectedPath(String className, String testName) {
//        return testCaseRoot(className, testName) + "Expected\\";
//    }
//
//    public static String diffPath(String className, String testName) {
//        return testCaseRoot(className, testName) + "Diff\\";
//    }
//
//    // ========================================================================
//    protected static void addCurrentTestMthod(String method) {
//        CURRENT_TEST_NAME.add(method);
//    }
//
//    protected static String currentTestMethod() {
//        String last = null;
//        for (String m : CURRENT_TEST_NAME)
//            last = m;
//        return last == null ? "unknownTest" : last;
//    }
//
//    // ========================================================================
//    // Config loaders
//    protected static Config loadthisTestConfig(String className) {
//
//        String configPath = SuitePath
//                          + "\\TestCases\\"
//                          + className
//                          + "\\Input\\input.json";
//
//        return CustomFunction.loadConfig(configPath);
//    }
//
//    protected static Config loadthisTestConfig(String className, String testName) {
//        String configPath = testCaseRoot(className, testName) + "Input\\input.json";
//        return CustomFunction.loadConfig(configPath);
//    }
//
//    // ========================================================================
//    // Screenshot helper (used by subclasses)
//    protected String takeScreenshot(String className, String testName) {
//        try {
//            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//            String screenshotDir = testCaseRoot(className, testName) + "Img\\";
//            new java.io.File(screenshotDir).mkdirs();
//
//            String path = screenshotDir + "SS_" + ts + ".png";
//
//            java.io.File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//            java.io.File dest = new java.io.File(path);
//            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
//
//            return path;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    // ========================================================================
//    // TestNG hooks
//    @BeforeSuite
//    public void beforeSuiteSetup() {
//
//        System.out.println(">>> beforeSuiteSetup START");
//        testNmaes_login = Testargs;
//
//        try {
//            // 1) WebDriver
//            System.out.println(">>> creating ChromeDriver");
//            driver = new ChromeDriver();
//            driver.manage().window().maximize();
//            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//            System.out.println(">>> ChromeDriver OK");
//
//            // 2) ExtentReports
//            System.out.println(">>> creating ExtentReports");
//            extent = ExtentManager.getInstance();
//            System.out.println(">>> ExtentReports OK");
//
//            // 3) Open URL if provided
//            if (!url.isEmpty()) {
//                System.out.println(">>> opening URL: " + url);
//                driver.get(url);
//            }
//
//            System.out.println(">>> beforeSuiteSetup FINISH");
//        } catch (Throwable t) {
//            System.out.println(">>> ERROR in beforeSuiteSetup:");
//            t.printStackTrace();
//            throw t;
//        }
//    }
//
//    @BeforeMethod
//    public void getMethodname(Method method) {
//        addCurrentTestMthod(method.getName());
//    }
//
//    @AfterSuite
//    public void afterSuiteCleanup() {
//        try {
//            if (driver != null) driver.quit();
//        } catch (Exception ignored) {}
//
//        try {
//            if (extent != null) {
//                extent.flush();
//                System.out.println(">>> Extent report flushed");
//            }
//        } catch (Exception ignored) {}
//    }
//}

package testbase;

import java.time.Duration;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import utilites.Config;
import utilites.CustomFunction;

public class BaseTemplate {

    // -------------------- WebDriver & Wait --------------------
    protected static WebDriver driver;
    protected static WebDriverWait wait;

    // -------------------- Config / CLI properties --------------------
    protected static String jsoninput;
    protected static String browser;
    protected static String Testargs;
    protected static String url = "";
    protected static String xml;

    // -------------------- ROOT PATH  --------------------
    public static String SuitePath;
    protected static String XML_DIR;

    // -------------------- Test tracking --------------------
    public static String testNmaes_login;
    protected static final Set<String> CURRENT_TEST_NAME = new LinkedHashSet<>();

    // ========================================================================
    // CLI args only
    public static void Setargs(String[] args) {

        jsoninput = getArgs(args, "-cfg",      "artifacts\\TestCases\\Tests\\Input\\input.json");
        SuitePath = getArgs(args, "-out",      "artifacts");
        browser   = getArgs(args, "-browser",  "chrome");
        Testargs  = getArgs(args, "-testNames","ALL");
        xml       = getArgs(args, "-xml",      "testng.xml");
        url       = getArgs(args, "-url",      "");
    }

    private static String getArgs(String[] args, String key, String def) {
        if (args == null)
            return def;

        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(key)) {
                return args[i + 1];
            }
        }
        return def;
    }

    // ========================================================================
    // Path helpers (per test class + test case)
    public static String testCaseRoot(String className, String testName) {
        return SuitePath + "\\TestCases\\" + className + "\\" + testName + "\\";
    }

    public static String inputPath(String className, String testName) {
        return testCaseRoot(className, testName) + "Input\\";
    }

    public static String actualPath(String className, String testName) {
        return testCaseRoot(className, testName) + "Actual\\";
    }

    public static String expectedPath(String className, String testName) {
        return testCaseRoot(className, testName) + "Expected\\";
    }

    public static String diffPath(String className, String testName) {
        return testCaseRoot(className, testName) + "Diff\\";
    }

    // ========================================================================
    protected static void addCurrentTestMthod(String method) {
        CURRENT_TEST_NAME.add(method);
    }

    protected static String currentTestMethod() {
        String last = null;
        for (String m : CURRENT_TEST_NAME)
            last = m;
        return last == null ? "unknownTest" : last;
    }

    // ========================================================================
    // Config loaders
    protected static Config loadthisTestConfig(String className) {

        String configPath = SuitePath
                          + "\\TestCases\\"
                          + className
                          + "\\Input\\input.json";

        return CustomFunction.loadConfig(configPath);
    }

    protected static Config loadthisTestConfig(String className, String testName) {
        String configPath = testCaseRoot(className, testName) + "Input\\input.json";
        return CustomFunction.loadConfig(configPath);
    }

    // ========================================================================
    // TestNG hooks
    @BeforeSuite
    public void beforeSuiteSetup() {

        testNmaes_login = Testargs;

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        if (!url.isEmpty()) {
            driver.get(url);
        }
    }

    @BeforeMethod
    public void getMethodname(Method method) {
        addCurrentTestMthod(method.getName());
    }

    @AfterSuite
    public void afterSuiteCleanup() {
        try {
            if (driver != null) driver.quit();
        } catch (Exception ignored) {}
    }
}

