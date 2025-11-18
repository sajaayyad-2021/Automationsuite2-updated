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
    // Path helpers
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
    // Config
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
    // TestNG
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
