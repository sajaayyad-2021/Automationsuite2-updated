package controller;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import POM.POMPIM;

public class PIMCtrl {

	private static WebDriverWait wait(WebDriver d, long sec) {
		return new WebDriverWait(d, Duration.ofSeconds(sec));
	}

	// ---------- open PIM ----------
	public static void openPIMpage(WebDriver driver) {
		By pimLink = By.xpath("//span[normalize-space()='PIM']/ancestor::a");
		wait(driver, 15).until(ExpectedConditions.elementToBeClickable(pimLink)).click();
	

		wait(driver, 15).until(ExpectedConditions.or(//if add appear
				ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='Add Employee']")),
				ExpectedConditions.presenceOfElementLocated(By.xpath("//h6[contains(.,'PIM')]"))));
	
	}

	// ----------  add employee ----------
	public static void clickAddEmployee(WebDriver driver) {
		By addBtn = By.xpath("//a[normalize-space()='Add Employee' or contains(@href,'addEmployee')]");
		wait(driver, 15).until(ExpectedConditions.elementToBeClickable(addBtn)).click();
	

		wait(driver, 15).until(ExpectedConditions.visibilityOfElementLocated(By.name("firstName")));
		wait(driver, 15).until(ExpectedConditions.visibilityOfElementLocated(By.name("lastName")));
		
	}

	// ---------- fill fields ----------
	public static void fillfirstname(WebDriver driver, String firstname) {
		WebElement el = POMPIM.firstnameFaild(driver);
		el.clear();
		el.sendKeys(firstname);
	
	}

	public static void fillmiddlename(WebDriver driver, String middlename) {
		WebElement el = POMPIM.middlenameFaild(driver);
		el.clear();
		el.sendKeys(middlename);
	
	}

	public static void filllastname(WebDriver driver, String lastname) {
		WebElement el = POMPIM.lastnameFaild(driver);
		el.clear();
		el.sendKeys(lastname);
	
	}

	public static void fillemplyeeId(WebDriver driver, String employeeId) {
		WebElement el = POMPIM.emplyeeIdFaild(driver);
		el.clear();
		el.sendKeys(employeeId);
		
	}

	public static void clickSave(WebDriver driver) throws InterruptedException {
		if (POMPIM.firstnameFaild(driver).getAttribute("value").isBlank()
				|| POMPIM.lastnameFaild(driver).getAttribute("value").isBlank()) {
			throw new IllegalStateException("[PIM] First/Last name are required before Save.");
		}

		By saveBtn = By.xpath("//button[normalize-space()='Save']");
		WebElement button = driver.findElement(saveBtn);
		button.click();

		Thread.sleep(6000);
		if (driver.getCurrentUrl().contains("/pim/viewPersonalDetails")
				|| !driver.findElements(By.xpath("//h6[contains(.,'Personal Details')]")).isEmpty()) {
			
		} else {
		
		}
	}

}