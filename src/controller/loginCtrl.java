package controller;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import POM.POMlogin;  // Import the POM for login page elements

import java.time.Duration;

public class loginCtrl {

    // Fill the username field using the POM method (no need to re-fetch the element)
    public static void fillUsername(WebDriver driver, String username) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        WebElement userField = wait.until(ExpectedConditions
                .elementToBeClickable(POMlogin.usernameField(driver))); // Using POM to get the locator
        try {
            userField.clear();
        } catch (InvalidElementStateException ignored) {}
        userField.sendKeys(username);
    }

    // Fill the password field using the POM method (no need to re-fetch the element)
    public static void fillPassword(WebDriver driver, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        WebElement passField = wait.until(ExpectedConditions
                .elementToBeClickable(POMlogin.passwordField(driver))); // Using POM to get the locator
        try {
            passField.clear();
        } catch (InvalidElementStateException ignored) {}
        passField.sendKeys(password);
    }

    // Click the login button using the POM method (no need to re-fetch the element)
    public static void clickLogin(WebDriver driver) {
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(POMlogin.loginButton(driver))); // Using POM here
        btn.click();
    }
    

    public static boolean waitForDashboard(WebDriver driver, int timeoutSeconds) {
        try {
            POMlogin.waitForDashboard(driver, timeoutSeconds);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}
