package POM;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class POMlogin {

    public static WebElement usernameFaild(WebDriver driver) {
        return driver.findElement(By.name("username"));
    }

    public static WebElement passwordFaild(WebDriver driver) {
        return driver.findElement(By.name("password"));
    }

    public static WebElement loginButton(WebDriver driver) {
        return driver.findElement(By.cssSelector("button[type='submit']"));
    }

    public static List<WebElement> requiredMessages(WebDriver driver) {
        return driver.findElements(By.cssSelector("span.oxd-input-field-error-message"));
    }

    public static boolean isPasswordInvalid(WebDriver driver) {
        try {
            return driver.findElement(By.xpath("//*[contains(text(),'Password')]")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isUsernameInvalid(WebDriver driver) {
        try {
            return driver.findElement(By.xpath("//*[contains(text(),'Username')]")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
