package Driver;

import org.openqa.selenium.chrome.ChromeDriver;


public class RegressionDriver {

    public static ChromeDriver initDriver() {
      
   

       
        ChromeDriver driver = new ChromeDriver();
driver.manage().window().maximize();
        return driver; 
    }
}
