package POM;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class POMPIM {

	// Menu
	public static WebElement pimMenu(WebDriver driver) {
		return driver.findElement(By.xpath("//span[text()='PIM']"));
	}

	public static WebElement addEmployeeButton(WebDriver driver) {
		return driver.findElement(By.xpath("//a[contains(@href, 'pim/addEmployee')]"));
	}

	// - Add Employee Form Fields
	public static WebElement firstnameFaild(WebDriver driver) {
		return driver.findElement(By.name("firstname"));

	}

	public static WebElement middlenameFaild(WebDriver driver) {
		return driver.findElement(By.name("middlename"));

	}

	public static WebElement lastnameFaild(WebDriver driver) {
		return driver.findElement(By.name("lastname"));

	}

	public static WebElement emplyeeIdFaild(WebDriver driver) {
		return driver.findElement(By.xpath("//label[text()='Employee Id']/../following-sibling::div/input"));

	}

	// Save Button
	public static WebElement savaButton(WebDriver driver) {
		return driver.findElement(By.xpath("//button[@type='submit']"));

	}

	// confirmation
	public static WebElement personalDetalisHeader(WebDriver driver) {
		return driver.findElement(By.xpath("//h6[text()='Personal Details']"));

	}
}
