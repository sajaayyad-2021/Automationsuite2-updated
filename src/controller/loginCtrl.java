package controller;

import org.openqa.selenium.WebDriver;

import POM.POMlogin;

public class loginCtrl {
	public static void fillUsername(WebDriver driver, String username) {
	    POMlogin.usernameFaild(driver).clear();
	    POMlogin.usernameFaild(driver).sendKeys(username); 
	}

	public static void fillpassword(WebDriver driver, String password) {
	    POMlogin.passwordFaild(driver).clear();
	    POMlogin.passwordFaild(driver).sendKeys(password); 
	}


	public static void clicklogin(WebDriver driver) {
		POMlogin.loginButton(driver).click();
	}
}
