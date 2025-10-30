package POM;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class POMlogin {
	//---1
	public static WebElement usernameFaild(WebDriver driver) {
		try {
			return driver.findElement(By.cssSelector("input[placeholder='Username']"));
		} catch (Exception e) {
		}

		try {
			return driver.findElement(By.xpath("(//form//input)[1]"));
		} catch (Exception e) {
		}

		return driver.findElement(By.xpath("//label[contains(.,'Username')]/../following-sibling::div//input"));
	}
	//---2

	public static WebElement passwordFaild(WebDriver driver) {
		try {
			return driver.findElement(By.cssSelector("input[placeholder='Password']"));
		} catch (Exception e) {
		}

		try {
			return driver.findElement(By.xpath("(//form//input)[2]"));
		} catch (Exception e) {
		}

		return driver.findElement(By.xpath("//label[contains(.,'Password')]/../following-sibling::div//input"));
	}
	//---3
	public static WebElement loginButton(WebDriver driver) {
		try {
			return driver.findElement(By.cssSelector("button[type='submit']"));
		} catch (Exception e) {
		}

		return driver.findElement(By.xpath("//button[normalize-space()='Login']"));
	}
}
