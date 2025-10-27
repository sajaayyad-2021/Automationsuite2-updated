package Test;

import org.openqa.selenium.WebDriver;

import utilites.Config;
import utilites.CustomFunction;
import utilites.MainFunctions;
import Driver.RegressionDriver;


public class Main {
	public static void main(String[] args) {

        //  Load config.json
        
        Config cfg = CustomFunction.loadConfig("data/config.json");

        //  Start browser driver
        WebDriver driver = RegressionDriver.initDriver();

        // 3) create  flow 
        MainFunctions flow = new MainFunctions(driver);

        try {
          // -----------1----------
            flow.performLogin(cfg);

            // ----------2---------
            flow.performCreateEmployee(cfg);

            // ----------3---------
            flow.performLogout(cfg);

            System.out.println("Test scenario completed successfully");

        } catch (Exception e) {
            System.out.println("Test failed with exception:");
            e.printStackTrace();

        } finally {
            // 7) Always close the browser
            driver.quit();
            System.out.println("Driver closed.");
        }
    }
}

