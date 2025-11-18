package reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance("test-output/ExtentReport.html");
        }
        return extent;
    }

    public static ExtentReports createInstance(String fileName) {

        ExtentSparkReporter spark = new ExtentSparkReporter(fileName);

        spark.config().setTheme(Theme.STANDARD);
        spark.config().setDocumentTitle("Automation Report");
        spark.config().setReportName("Regression Report");
        

        extent = new ExtentReports();
        extent.attachReporter(spark);

        return extent;
    }
}
