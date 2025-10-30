package POM;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class POMPIM {
	
	public static WebElement pimMenu(WebDriver driver) {
		try {
			return driver.findElement(By.xpath("//span[normalize-space()='PIM']/ancestor::a"));
		} catch (Exception e) {
		}

		try {
			return driver.findElement(By.xpath("//a[contains(@href,'pim')]"));
		} catch (Exception e) {
		}

		return driver.findElement(By.xpath("(//ul[contains(@class,'oxd-main-menu')]/li//a)[2]"));
	}

	public static WebElement addEmployeeButton(WebDriver driver) {
		try {
			return driver.findElement(By.xpath("//a[normalize-space()='Add Employee']"));
		} catch (Exception e) {
		}

		return driver.findElement(By.xpath("//button[.//text()[normalize-space()='Add Employee']]"));
	}

	public static WebElement firstnameFaild(WebDriver driver) {
		return driver.findElement(By.name("firstName"));
	}

	public static WebElement middlenameFaild(WebDriver driver) {
		return driver.findElement(By.name("middleName"));
	}

	public static WebElement lastnameFaild(WebDriver driver) {
		return driver.findElement(By.name("lastName"));
	}

	public static WebElement emplyeeIdFaild(WebDriver driver) {
		return driver.findElement(By.xpath("//label[contains(.,'Employee Id')]/../following-sibling::div//input"));
	}

	public static WebElement saveButton(WebDriver driver) {
		try {
			return driver.findElement(By.xpath("//button[normalize-space()='Save']"));
		} catch (Exception e) {
		}

		return driver.findElement(By.cssSelector("button[type='submit']"));
	}

	public static WebElement personalDetalisHeader(WebDriver driver) {
		return driver.findElement(By.xpath("//h6[contains(.,'Personal Details')]"));
	}

	public static WebElement errorMessage(WebDriver driver) {
		return driver.findElement(By.cssSelector(".oxd-input-field-error-message"));
	}
}
