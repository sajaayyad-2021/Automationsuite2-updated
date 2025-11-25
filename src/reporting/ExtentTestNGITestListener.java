
package reporting;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.*;

public class ExtentTestNGITestListener implements ITestListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> parentTest = new ThreadLocal<>();
    private static ThreadLocal<ExtentTest> childTest = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        extent = ExtentManager.getInstance();

        String module = context.getName(); // Regression / LoginTests / PIMTests
        ExtentTest parent = extent.createTest("Module: " + module);
        parentTest.set(parent);
    }

    @Override
    public synchronized void onTestStart(ITestResult result) {
        ExtentTest child = parentTest.get().createNode(result.getMethod().getMethodName());
        childTest.set(child);
    }

    @Override
    public synchronized void onTestSuccess(ITestResult result) {
        childTest.get().pass("Test Passed");
    }

    @Override
    
    public synchronized void onTestFailure(ITestResult result) {
        ExtentTest test = childTest.get();
        Throwable throwable = result.getThrowable();
        
        if (throwable != null) {
            String errorMessage = throwable.getMessage();
            if (errorMessage.contains("Exception occurred")) {
                test.fail("Test Failed: " + errorMessage.substring(0, 100));  
            } else {
                test.fail(throwable);
            }

           
        }
    }


    @Override
    public synchronized void onTestSkipped(ITestResult result) {
        childTest.get().skip("Test Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}
