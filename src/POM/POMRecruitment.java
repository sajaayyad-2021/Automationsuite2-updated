package POM;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class POMRecruitment {

    public static WebElement recruitmentMenu(WebDriver driver) {
        return driver.findElement(By.xpath("//span[normalize-space()='Recruitment']"));
    }
//candidates tab
    public static WebElement candidatesTab(WebDriver driver) {
        return driver.findElement(By.xpath(
            "//a[contains(@class,'oxd-topbar-body-nav-tab-item') and normalize-space()='Candidates']"
        ));
    }
//addButton 
    public static WebElement addButton(WebDriver driver) {
        return driver.findElement(By.xpath(
            "//div[contains(@class,'orangehrm-header-container')]//button[contains(@class,'oxd-button') and " +
            "(contains(normalize-space(),'Add') or .//i[contains(@class,'bi-plus')])]"
        ));
    }
//addCandidateTitle
    public static WebElement addCandidateTitle(WebDriver driver) {
        return driver.findElement(By.xpath("//h6[contains(@class,'orangehrm-main-title') and normalize-space()='Add Candidate']"));
    }
//firsname
    public static WebElement firstName(WebDriver driver) {
        return driver.findElement(By.cssSelector("input[name='firstName']"));
    }
//middlename
    public static WebElement middleName(WebDriver driver) {
        return driver.findElement(By.cssSelector("input[name='middleName']"));
    }
//lastname
    public static WebElement lastName(WebDriver driver) {
        return driver.findElement(By.cssSelector("input[name='lastName']"));
    }
//vacancySelect
    public static WebElement vacancySelect(WebDriver driver) {
        return driver.findElement(By.xpath(
            "//label[normalize-space()='Vacancy']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text')]"
        ));
    }
//dropdownOption
    public static By dropdownOption(String visibleText) {
        return By.xpath("//div[contains(@class,'oxd-select-dropdown')]//div[contains(@class,'oxd-select-option')]//span[normalize-space()='" + visibleText + "']");
    }

    public static WebElement email(WebDriver driver) {
        return driver.findElement(By.xpath("//label[normalize-space()='Email']/ancestor::div[contains(@class,'oxd-input-group')]//input"));
    }

    public static WebElement contactNumber(WebDriver driver) {
        return driver.findElement(By.xpath("//label[normalize-space()='Contact Number']/ancestor::div[contains(@class,'oxd-input-group')]//input"));
    }

    public static WebElement resumeUpload(WebDriver driver) {
        return driver.findElement(By.cssSelector("input[type='file'].oxd-file-input"));
    }

    public static WebElement keywords(WebDriver driver) {
        return driver.findElement(By.xpath("//label[normalize-space()='Keywords']/ancestor::div[contains(@class,'oxd-input-group')]//input"));
    }

    public static WebElement dateInput(WebDriver driver) {
        return driver.findElement(By.xpath("//label[normalize-space()='Date of Application']/ancestor::div[contains(@class,'oxd-input-group')]//input"));
    }

    public static WebElement dateIcon(WebDriver driver) {
        return driver.findElement(By.xpath("//label[normalize-space()='Date of Application']/ancestor::div[contains(@class,'oxd-input-group')]//i[contains(@class,'bi-calendar')]"));
    }

    public static WebElement notes(WebDriver driver) {
        return driver.findElement(By.cssSelector("textarea.oxd-textarea"));
    }

    public static WebElement consentVisual(WebDriver driver) {
        return driver.findElement(By.xpath(
            "//label[normalize-space()='Consent to keep data']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//span[contains(@class,'oxd-checkbox-input')]"
        ));
    }

    public static WebElement consentCheckbox(WebDriver driver) {
        return driver.findElement(By.xpath(
            "//label[normalize-space()='Consent to keep data']" +
            "/ancestor::div[contains(@class,'oxd-input-group')]" +
            "//input[@type='checkbox']"
        ));
    }

    public static WebElement saveButton(WebDriver driver) {
        return driver.findElement(By.xpath("//button[@type='submit' and contains(@class,'oxd-button') and contains(normalize-space(),'Save')]"));
    }

    public static WebElement cancelButton(WebDriver driver) {
        return driver.findElement(By.xpath("//button[contains(@class,'oxd-button') and normalize-space()='Cancel']"));
    }
}
