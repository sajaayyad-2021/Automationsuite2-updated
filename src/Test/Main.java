package Test;

import org.openqa.selenium.WebDriver;
import utilites.Config;
import utilites.CustomFunction;
import utilites.MainFunctions;
import Driver.RegressionDriver;

public class Main {

    public static void main(String[] args) {
        // If no external driver passed, create a default one here
        WebDriver driver = RegressionDriver.initDriver();
        runTest(driver);
    }

    public static void runTest(WebDriver driver) {
        Config cfg = CustomFunction.loadConfig("data/config.json");
        MainFunctions flow = new MainFunctions(driver, cfg);

        try {
            flow.performLogin(cfg);
            flow.performCreateEmployee(cfg);
            flow.performLeaveSearch(cfg);
            flow.performLogout();
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
