package controller;

import org.openqa.selenium.WebDriver;

import POM.POMPIM;

public class PIMCtrl {

	public static void openPIMpage(WebDriver driver) {
		POMPIM.pimMenu(driver).click();
		System.out.println("navigated to PIM page");

	}

	public static void clickAddEmployee(WebDriver driver) {
		POMPIM.addEmployeeButton(driver).click();
		System.out.println("opened add form");
	}

	public static void fillfirstname(WebDriver driver, String firstname) {
	    POMPIM.firstnameFaild(driver).clear();
	    POMPIM.firstnameFaild(driver).sendKeys(firstname);
	}

	public static void fillmiddlename(WebDriver driver, String middlename) {
	    POMPIM.middlenameFaild(driver).clear();
	    POMPIM.middlenameFaild(driver).sendKeys(middlename);
	}

	public static void filllastname(WebDriver driver, String lastname) {
	    POMPIM.lastnameFaild(driver).clear();
	    POMPIM.lastnameFaild(driver).sendKeys(lastname);
	}

	public static void fillemplyeeId(WebDriver driver, String employeeId) {
	    POMPIM.emplyeeIdFaild(driver).clear();
	    POMPIM.emplyeeIdFaild(driver).sendKeys(employeeId);
	}
	public static void clickSave(WebDriver driver) {
	    POMPIM.savaButton(driver).click();

		System.out.println("Save button clicked.");
	}

	public static boolean isdetailspagedisplayed(WebDriver driver) {
		try {
			return POMPIM.personalDetalisHeader(driver).isDisplayed();

		} catch (Exception e) {
			return false;
		}
	}
}
