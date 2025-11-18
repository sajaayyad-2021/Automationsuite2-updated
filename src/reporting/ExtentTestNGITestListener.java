package reporting;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentTestNGITestListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    // ====== Optional helper to use inside test classes ======
    public static ExtentTest getTest() {
        return test.get();
    }

    // ================= TestNG callbacks =================

    @Override
    public synchronized void onTestStart(ITestResult result) {
        // create a new test in the report for each @Test method
        ExtentTest t = extent.createTest(result.getMethod().getMethodName());
        test.set(t);
    }

    @Override
    public synchronized void onTestSuccess(ITestResult result) {
        test.get().pass("Test passed");
    }

    @Override
    public synchronized void onTestFailure(ITestResult result) {
        test.get().fail(result.getThrowable());
    }

    @Override
    public synchronized void onTestSkipped(ITestResult result) {
        test.get().skip(result.getThrowable());
    }

    @Override
    public synchronized void onStart(ITestContext context) {
        // nothing extra needed here
    }

    @Override
    public synchronized void onFinish(ITestContext context) {
        extent.flush();  // write everything to HTML when suite finishes
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // not used
    }
}
