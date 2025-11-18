//package POM;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//
//public class POMLeave {
//
//    public static WebElement leaveMenu(WebDriver driver) {
//        return driver.findElement(
//            By.xpath("//span[normalize-space()='Leave' or contains(@class,'oxd-topbar-body-nav-tab-item')]")
//        );
//    }
//
//   
//    public static WebElement fromDateInput(WebDriver driver) {
//        return driver.findElement(
//            By.xpath(
//                "//label[text()='From Date']" +
//                "/ancestor::div[contains(@class,'oxd-input-group')]" +
//                "//input"
//            )
//        );
//    }
//
//    public static WebElement toDateInput(WebDriver driver) {
//        return driver.findElement(
//            By.xpath(
//                "//label[text()='To Date']" +
//                "/ancestor::div[contains(@class,'oxd-input-group')]" +
//                "//input"
//            )
//        );
//    }
//
//    public static WebElement employeeNameInput(WebDriver driver) {
//        return driver.findElement(
//            By.xpath(
//                "//label[normalize-space()='Employee Name']" +
//                "/ancestor::div[contains(@class,'oxd-input-group')]" +
//                "//input"
//            )
//        );
//    }
//
//  
//    public static WebElement searchButton(WebDriver driver) {
//        return driver.findElement(
//            By.xpath("//button[normalize-space()='Search' and contains(@class,'oxd-button')]")
//        );
//    }
//
//    public static WebElement resetButton(WebDriver driver) {
//        return driver.findElement(
//            By.xpath("//button[normalize-space()='Reset' and contains(@class,'oxd-button')]")
//        );
//    }
//}

package POM;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class POMLeave {

    public static WebElement leaveMenu(WebDriver driver) {
        return driver.findElement(
            By.xpath("//span[normalize-space()='Leave' or contains(@class,'oxd-topbar-body-nav-tab-item')]")
        );
    }

   
    public static WebElement fromDateInput(WebDriver driver) {
        return driver.findElement(
            By.xpath(
                "//label[text()='From Date']" +
                "/ancestor::div[contains(@class,'oxd-input-group')]" +
                "//input"
            )
        );
    }

    public static WebElement toDateInput(WebDriver driver) {
        return driver.findElement(
            By.xpath(
                "//label[text()='To Date']" +
                "/ancestor::div[contains(@class,'oxd-input-group')]" +
                "//input"
            )
        );
    }

    public static WebElement employeeNameInput(WebDriver driver) {
        return driver.findElement(
            By.xpath(
                "//label[normalize-space()='Employee Name']" +
                "/ancestor::div[contains(@class,'oxd-input-group')]" +
                "//input"
            )
        );
    }

  
    public static WebElement searchButton(WebDriver driver) {
        return driver.findElement(
            By.xpath("//button[normalize-space()='Search' and contains(@class,'oxd-button')]")
        );
    }

    public static WebElement resetButton(WebDriver driver) {
        return driver.findElement(
            By.xpath("//button[normalize-space()='Reset' and contains(@class,'oxd-button')]")
        );
    }
}

