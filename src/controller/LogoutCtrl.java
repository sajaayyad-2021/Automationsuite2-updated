package controller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LogoutCtrl {

    // locators
    private static By userMenu = By.cssSelector(".oxd-userdropdown"); 
    private static By logoutOption = By.xpath("//a[normalize-space()='Logout']");

   
    public static void openUserMenu(WebDriver driver, WebDriverWait wait) {
        wait.until(ExpectedConditions.elementToBeClickable(userMenu)).click();
        System.out.println("[LOGOUT] opened user menu");
    }

  
    public static void clickLogout(WebDriver driver, WebDriverWait wait) {
        wait.until(ExpectedConditions.elementToBeClickable(logoutOption)).click();
        System.out.println("[LOGOUT] clicked logout");
    }
}
