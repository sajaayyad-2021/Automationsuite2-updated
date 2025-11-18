package controller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import POM.POMRecruitment;

public class RecruitmentCtrl {

	public static void openRecruitment(WebDriver driver, WebDriverWait wait) {
		POMRecruitment.recruitmentMenu(driver).click();
		System.out.println("[recruitment] clicked Recruitment menu");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
				"//h6[contains(@class,'oxd-topbar-header-breadcrumb-module') and normalize-space()='Recruitment']")));

		WebElement candTab = wait.until(ExpectedConditions.elementToBeClickable(POMRecruitment.candidatesTab(driver)));
		candTab.click();
		System.out.println("[recruitment] Candidates tab clicked");

		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//div[contains(@class,'orangehrm-header-container')]")));
		wait.until(ExpectedConditions.elementToBeClickable(POMRecruitment.addButton(driver)));
		System.out.println("[recruitment] list loaded (Add visible)");
	}

	public static void openAddCandidate(WebDriver driver, WebDriverWait wait) {
		try {
			POMRecruitment.addButton(driver).click();
		} catch (WebDriverException e) {
			wait.until(ExpectedConditions.elementToBeClickable(POMRecruitment.addButton(driver))).click();
		}
		System.out.println("[recruitment]Add clicked");

		wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.xpath("//h6[contains(@class,'orangehrm-main-title') and normalize-space()='Add Candidate']")));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='firstName']")));
		System.out.println("[recruitment] Add Candidate form loaded");
	}

	public static void fillFullName(WebDriver driver, String first, String middle, String last) {
		WebElement f = POMRecruitment.firstName(driver);
		f.clear();
		f.sendKeys(first);

		WebElement m = POMRecruitment.middleName(driver);
		m.clear();
		//if (middle != null)
			m.sendKeys(middle);

		WebElement l = POMRecruitment.lastName(driver);
		l.clear();
		l.sendKeys(last);

		System.out.println("[recruitment] Full name: " + first + " " + (middle == null ? "" : middle) + " " + last);
	}
//safeClickWithScroll
	public static void selectVacancy(WebDriver driver, WebDriverWait wait, String visibleText) {
		safeClickWithScroll(driver, wait, By.xpath(
				"//label[normalize-space()='Vacancy']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]"));
		WebElement option = wait.until(ExpectedConditions.elementToBeClickable(POMRecruitment.dropdownOption(visibleText)));
		option.click();
		System.out.println("[recruitment] Vacancy selected: " + visibleText);
	}

	public static void fillContact(WebDriver driver, String email, String phone) {
		try {
			POMRecruitment.email(driver).clear();
			POMRecruitment.email(driver).sendKeys(email);
		} catch (Exception ignored) {
		}
		WebElement c = POMRecruitment.contactNumber(driver);
		c.clear();
		if (phone != null)
			c.sendKeys(phone);
		System.out.println("[recruitment] Contact filled");
	}

	public static void uploadResume(WebDriver driver, String absoluteFilePath) {
		POMRecruitment.resumeUpload(driver).sendKeys(absoluteFilePath);
		System.out.println("[recruitment] Resume uploaded: " + absoluteFilePath);
	}

	public static void fillKeywords(WebDriver driver, String kws) {
		WebElement k = POMRecruitment.keywords(driver);
		k.clear();
		if (kws != null)
			k.sendKeys(kws);
		System.out.println("[recruitment] Keywords filled");
	}

	public static void pickDateOfApplication(WebDriver driver, WebDriverWait wait, String yyyy_mm_dd) {
		safeClickWithScroll(driver, wait, By.xpath(
				"//label[normalize-space()='Date of Application']/ancestor::div[contains(@class,'oxd-input-group')]//i[contains(@class,'bi-calendar')]"));
		selectDateWithPicker(driver, wait, yyyy_mm_dd);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@class,'oxd-date-input-calendar')]")));
		System.out.println("[recruitment] Date picked: " + yyyy_mm_dd);
	}

	public static void fillNotes(WebDriver driver, String text) {
		WebElement n = POMRecruitment.notes(driver);
		n.clear();
		if (text != null)
			n.sendKeys(text);
		System.out.println("[recruitment] Notes filled");
	}

	public static void setConsent(WebDriver driver, boolean value) {
		WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));

		WebElement input = POMRecruitment.consentCheckbox(driver);
		WebElement visual = POMRecruitment.consentVisual(driver);
		WebElement label = driver.findElement(By.xpath("//label[normalize-space()='Consent to keep data']"));

		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", visual);

		if (input.isSelected() == value) {
			System.out.println("[recruitment] Consent already " + value);
			return;
		}

		try {
			wait.until(ExpectedConditions.elementToBeClickable(visual)).click();
		} catch (ElementClickInterceptedException e1) {
			System.out.println("[recruitment] visual span click intercepted → try label");
			try {
				wait.until(ExpectedConditions.elementToBeClickable(label)).click();
			} catch (ElementClickInterceptedException e2) {
				System.out.println("[recruitment] label click intercepted → try JS on span");
				try {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", visual);
				} catch (WebDriverException e3) {
					System.out.println("[recruitment] JS click on span failed → try JS on input");
					((JavascriptExecutor) driver).executeScript(
							"var el=arguments[0], want=arguments[1];" + "if(el.checked!==want){el.click();}"
									+ "var evt=new Event('change',{bubbles:true}); el.dispatchEvent(evt);",
							input, value);
				}
			}
		}

		wait.until(drv -> POMRecruitment.consentCheckbox(drv).isSelected() == value);
		System.out.println("[recruitment] Consent set to: " + value);
	}

	public static void clickSave(WebDriver driver) {
		POMRecruitment.saveButton(driver).click();
		System.out.println("[recruitment] Save clicked");
	}

	public static void clickCancel(WebDriver driver) {
		POMRecruitment.cancelButton(driver).click();
		System.out.println("[recruitment] Cancel clicked");
	}
//safeClickWithScroll
	private static void safeClickWithScroll(WebDriver driver, WebDriverWait wait, By locator) {
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", el);
		try {
			el.click();
		} catch (WebDriverException e) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
		}
	}

	private static void selectDateWithPicker(WebDriver driver, WebDriverWait wait, String date) {
		String[] p = date.split("-");
		String year = p[0];
		String month = p[1];
		String day = p[2];
		String monthName = monthNumberToName(month);
		String dayNum = String.valueOf(Integer.parseInt(day));

		WebElement picker = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//div[contains(@class,'oxd-date-input-calendar')]")));

		try {
			picker.findElement(By.xpath(".//div[contains(@class,'oxd-calendar-selector-year')]")).click();
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
					"//li[contains(@class,'oxd-calendar-dropdown--option') and normalize-space()='" + year + "']")))
					.click();
		} catch (WebDriverException ignored) {
		}

		try {
			picker.findElement(By.xpath(".//div[contains(@class,'oxd-calendar-selector-month')]")).click();
			wait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//li[contains(@class,'oxd-calendar-dropdown--option') and normalize-space()='" + monthName
							+ "']")))
					.click();
		} catch (WebDriverException ignored) {
		}

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
				"//div[contains(@class,'oxd-calendar-date') or contains(@class,'oxd-calendar-day')][normalize-space()='"
						+ dayNum + "']")))
				.click();
	}

	private static String monthNumberToName(String mm) {
		switch (mm) {
		case "01":
			return "January";
		case "02":
			return "February";
		case "03":
			return "March";
		case "04":
			return "April";
		case "05":
			return "May";
		case "06":
			return "June";
		case "07":
			return "July";
		case "08":
			return "August";
		case "09":
			return "September";
		case "10":
			return "October";
		case "11":
			return "November";
		case "12":
			return "December";
		default:
			return "January";
		}
	}
}
