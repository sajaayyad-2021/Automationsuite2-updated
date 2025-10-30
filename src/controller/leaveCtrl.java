package controller;



import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import POM.POMLeave;

public class leaveCtrl {

    // 1) open page
	public static void openLeaveList(WebDriver driver, WebDriverWait wait) {

	    POMLeave.leaveMenu(driver).click();
	    System.out.println("[leave] clicked Leave menu");

	    wait.until(ExpectedConditions.visibilityOfElementLocated(
	        By.xpath("//h6[contains(@class,'oxd-topbar-header-breadcrumb-module') and normalize-space()='Leave']")
	    ));

	    WebElement leaveListTab= wait.until(ExpectedConditions.elementToBeClickable(
	        By.xpath(
	            "//a[normalize-space()='Leave List' or contains(@href,'viewLeaveList')] | " +
	            "//button[normalize-space()='Leave List']"
	        )
	    ));
	    leaveListTab.click();
	    System.out.println("[leave] opened Leave List tab");


	    wait.until(ExpectedConditions.visibilityOfElementLocated(//wait
	        By.xpath("//label[normalize-space()='From Date']")
	    ));

	    wait.until(ExpectedConditions.visibilityOfElementLocated(
	        By.xpath("//label[normalize-space()='From Date']/ancestor::div[contains(@class,'oxd-input-group')]//input")
	    ));

	    System.out.println("[leave] Leave  page loaded");
	}

    // 2) from date
	public static void fillFromDate(WebDriver driver, WebDriverWait wait, String fromDate) {

	    By fromIcon = By.xpath(
	        "//label[text()='From Date']" +
	        "/ancestor::div[contains(@class,'oxd-input-group')]" +
	        "//i[contains(@class,'bi-calendar')]"
	    );

	    safeClickWithScroll(driver, wait, fromIcon);

	    selectDateWithPicker(driver, wait, fromDate);

	    System.out.println("[leave] from date picked: " + fromDate);

	    wait.until(ExpectedConditions.invisibilityOfElementLocated(
	        By.xpath("//div[contains(@class,'oxd-date-input-calendar')]")
	    ));
	}


    // 3) to date
	public static void fillToDate(WebDriver driver, WebDriverWait wait, String toDate) {

	    By toIcon = By.xpath(
	        "//label[text()='To Date']" +
	        "/ancestor::div[contains(@class,'oxd-input-group')]" +
	        "//i[contains(@class,'bi-calendar')]"
	    );

	    safeClickWithScroll(driver, wait, toIcon);

	    selectDateWithPicker(driver, wait, toDate);

	    System.out.println("[leave] To Date picked: " + toDate);

	    wait.until(ExpectedConditions.invisibilityOfElementLocated(
	        By.xpath("//div[contains(@class,'oxd-date-input-calendar')]")
	    ));
	}

    private static void selectDateWithPicker(WebDriver driver, WebDriverWait wait, String Date) {

        String[] parts = Date.split("-");
        String year  = parts[0];
        String month = parts[1];
        String day   = parts[2];

        String monthName = monthNumberToName(month);
        String dayZero = String.valueOf(Integer.parseInt(day));

        WebElement datePicker = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class,'oxd-date-input-calendar')]")
        ));

        try {
            WebElement yearSelector = datePicker.findElement(
                By.xpath(".//div[contains(@class,'oxd-calendar-selector-year')]")
            );
            yearSelector.click();

            WebElement targetYear = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(
                    "//li[contains(@class,'oxd-calendar-dropdown--option') and normalize-space()='" + year + "']"
                )
            ));
            targetYear.click();
        } catch (WebDriverException e) {
            System.out.println("[leave] Year dropdown not found, skipped year selection.");
        }

        try {
            WebElement monthSelector = datePicker.findElement(
                By.xpath(".//div[contains(@class,'oxd-calendar-selector-month')]")
            );
            monthSelector.click();
//target month
            WebElement Month  = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(
                    "//li[contains(@class,'oxd-calendar-dropdown--option') and normalize-space()='" + monthName + "']"
                )
            ));
            Month.click();
        } catch (WebDriverException e) {
            System.out.println("[leave] month not found");
        }

        WebElement dayButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(
                "//div[contains(@class,'oxd-calendar-date') or contains(@class,'oxd-calendar-day')]" +
                "[normalize-space()='" + dayZero + "']"
            )
        ));
        dayButton.click();
    }

    private static String monthNumberToName(String mm) {
        switch (mm) {
            case "01": return "January";
            case "02": return "February";
            case "03": return "March";
            case "04": return "April";
            case "05": return "May";
            case "06": return "June";
            case "07": return "July";
            case "08": return "August";
            case "09": return "September";
            case "10": return "October";
            case "11": return "November";
            case "12": return "December";
            default:    return "January";
        }
    }

    // 4) employee
    public static void fillEmployeeName(WebDriver driver, String empname) {
        WebElement empInput = POMLeave.employeeNameInput(driver);
        empInput.clear();
        empInput.sendKeys(empname);
        System.out.println("[leave] Employee name entered: " + empname);
    }

    // 5) status dropdown
    public static void selectStatus(WebDriver driver, WebDriverWait wait, String statusText) {

        By statusCaretLocator = By.xpath(
            "//label[text()='Show Leave with Status']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//i[contains(@class,'oxd-icon')]"
        );

        safeClickWithScroll(driver, wait, statusCaretLocator);
        System.out.println("[leave] opened Status dropdown");

        selectDropdownOptionByText(driver, wait, statusText);
        System.out.println("[leave] Status selected: " + statusText);
    }

    // 6) leave type dropdown
    public static void selectLeaveType(WebDriver driver, WebDriverWait wait, String leaveTypeText) {

        By leaveTypeCaretLocator = By.xpath(
            "//label[text()='Leave Type']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//i[contains(@class,'oxd-icon')]"
        );

        safeClickWithScroll(driver, wait, leaveTypeCaretLocator);
        System.out.println("[leave] opened Leave Type dropdown");

        selectDropdownOptionByText(driver, wait, leaveTypeText);
        System.out.println("[leave] Leave Type selected: " + leaveTypeText);
    }

    // 7) sub unit dropdown
    public static void selectSubUnit(WebDriver driver, WebDriverWait wait, String subUnitText) {

        By subUnitCaretLocator = By.xpath(
            "//label[text()='Sub Unit']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//i[contains(@class,'oxd-icon')]"
        );

        safeClickWithScroll(driver, wait, subUnitCaretLocator);
        System.out.println("[leave] opened Sub Unit dropdown");

        selectDropdownOptionByText(driver, wait, subUnitText);
        System.out.println("[leave] Sub Unit selected: " + subUnitText);
    }
  
    private static void selectDropdownOptionByText(WebDriver driver, WebDriverWait wait, String optionText) {

        String optionXPath = String.format(
            "//div[contains(@class,'oxd-select-option')]//span[normalize-space()='%s']",
            optionText
        );

        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(optionXPath)));
        option.click();
    }

    // 8) actions
    public static void clickSearch(WebDriver driver) {
        POMLeave.searchButton(driver).click();
        System.out.println("[leave] search button");
    }

    public static void clickReset(WebDriver driver) {
        POMLeave.resetButton(driver).click();
        System.out.println("[leave]Reset button");
    }

private static void safeClickWithScroll(WebDriver driver, WebDriverWait wait, By locator) {

    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));

   
    ((org.openqa.selenium.JavascriptExecutor) driver)
        .executeScript("arguments[0].scrollIntoView({block: 'center'});", el);

   
    try {
        el.click();
        return;
    } catch (WebDriverException e) {
        System.out.println("[leave][safeClickWithScroll] normal click failed, trying JS click: " + e.getClass().getSimpleName());
    }

    ((org.openqa.selenium.JavascriptExecutor) driver)
        .executeScript("arguments[0].click();", el);
}
}
