package controller;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class loginCtrl {

	public static void fillUsername(WebDriver driver, String username) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
		WebElement userField = wait.until(ExpectedConditions
				.elementToBeClickable(By.cssSelector("input[name='username'], input[placeholder='Username']")));
		try {
			userField.clear();
		} catch (InvalidElementStateException ignored) {}
		userField.sendKeys(username);
		// System.out.println("[login] username typed: " + username);
	}

	public static void fillpassword(WebDriver driver, String password) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
		WebElement passField = wait.until(ExpectedConditions
				.elementToBeClickable(By.cssSelector("input[name='password'], input[placeholder='Password']")));
		try {
			passField.clear();
		} catch (InvalidElementStateException ignored) {}
		passField.sendKeys(password);
		// System.out.println("[login]  password typed: " + password);
	}

	public static void clicklogin(WebDriver driver) {
		WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
		btn.click();
		// System.out.println("[login] clicked login button");
		// System.out.println("[login]  credentials submitted");
	}
}
