package reporting;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = "test-output/ExtentReport_" + timestamp + ".html";
            createInstance(reportPath);
        }
        return extent;
    }

    public static ExtentReports createInstance(String fileName) {

        ExtentSparkReporter spark = new ExtentSparkReporter(fileName);

        spark.config().setTheme(Theme.STANDARD);
        spark.config().setDocumentTitle("Automation Report");
        spark.config().setReportName("Regression Suite");
        spark.config().setEncoding("utf-8");

        extent = new ExtentReports();
        extent.attachReporter(spark);

      
        extent.setSystemInfo("Project", "OrangeHRM Automation");
        extent.setSystemInfo("Environment", "Test");
     

        return extent;
    }
}







//package reporting;
//
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import com.aventstack.extentreports.ExtentReports;
//import com.aventstack.extentreports.reporter.ExtentSparkReporter;
//import com.aventstack.extentreports.reporter.configuration.Theme;
//2nd way
//
//public class ExtentManager {
//
//    private static ExtentReports extent;
//
//    public static ExtentReports getInstance() {
//        if (extent == null) {
//            createInstance();
//        }
//        return extent;
//    }
//
//    private static void createInstance() {
//
//        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//
//        String reportDir = "test-output/ExtentReports/";
//        new File(reportDir).mkdirs();
//
//        String reportPath = reportDir + "RegressionSuite_" + timestamp + ".html";
//
//        // 👈 Debug مهم
//        System.out.println(">>> ExtentManager: creating report at: " + reportPath);
//
//        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
//
//        spark.config().setTheme(Theme.STANDARD); // Light mode
//        spark.config().setDocumentTitle("Automation Report");
//        spark.config().setReportName("Regression Suite");
//        spark.config().setEncoding("utf-8");
//
//        spark.config().setTimelineEnabled(true);
//
//        extent = new ExtentReports();
//        extent.attachReporter(spark);
//
//        extent.setSystemInfo("Project", "OrangeHRM Automation");
//        extent.setSystemInfo("Environment", "Test");
//        extent.setSystemInfo("Tester", "Saja");
//    }
//}
