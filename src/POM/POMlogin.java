package POM;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

//return the element on each page 
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
}
