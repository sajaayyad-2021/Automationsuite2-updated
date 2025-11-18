package Test;

import org.openqa.selenium.WebDriver;
import utilites.Config;
import utilites.CustomFunction;
import utilites.MainFunctions;

public class Main {

    public static void main(String[] args) {
      //  WebDriver driver = RegressionDriver.initDriver();
     //   runTest(driver);
    }

    public static void runTest(WebDriver driver) {
        Config cfg = CustomFunction.loadConfig("data/config.json");
        MainFunctions flow = new MainFunctions(driver, cfg);

        try {
            flow.performLogin(cfg);
            flow.performCreateEmployee(cfg);     // PIM: Add Employee (using Defaults)
            flow.performLeaveSearch(cfg);        // Leave Search
            flow.performRecruitmentAdd(cfg);     // NEW: Recruitment Add Candidate
            MainFunctions.performLogout(driver);

            System.out.println("test scenario completed successfully");
        } catch (Exception e) {
            System.out.println("test failed with exception:");
            e.printStackTrace();
        } finally {
            driver.quit();
            System.out.println("Driver closed.");
        }
    }
}
