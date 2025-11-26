package POM;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class POMPIM {
    
    // ==================== HELPER ====================
    private static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(12));
    }
    
    // ==================== NAVIGATION ====================
    
    public static WebElement pimMenu(WebDriver driver) {
        try {
            return driver.findElement(By.xpath("//span[normalize-space()='PIM']/ancestor::a"));
        } catch (Exception e) {
            // Fallback 1
        }
        try {
            return driver.findElement(By.xpath("//a[contains(@href,'pim')]"));
        } catch (Exception e) {
            // Fallback 2
        }
        return driver.findElement(By.xpath("(//ul[contains(@class,'oxd-main-menu')]/li//a)[2]"));
    }
    
    public static WebElement pimHeader(WebDriver driver) {
        return wait(driver).until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h6[contains(normalize-space(),'Employee') or normalize-space()='PIM']"))
        );
    }
    
    // ==================== ADD EMPLOYEE FORM ====================
    
    public static WebElement addEmployeeButton(WebDriver driver) {
        try {
            return driver.findElement(By.xpath("//a[normalize-space()='Add Employee']"));
        } catch (Exception e) {
            // Fallback
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
        return driver.findElement(
            By.xpath("//label[contains(.,'Employee Id')]/../following-sibling::div//input")
        );
    }
    
    public static WebElement saveButton(WebDriver driver) {
        try {
            return driver.findElement(By.xpath("//button[normalize-space()='Save']"));
        } catch (Exception e) {
            // Fallback
        }
        return driver.findElement(By.cssSelector("button[type='submit']"));
    }
    
    // ==================== SEARCH FIELDS ====================
    
    public static WebElement employeeNameSearchInput(WebDriver driver) {
        return driver.findElement(
            By.xpath("//label[normalize-space()='Employee Name']/following::input[1]")
        );
    }
    
    public static WebElement autocompleteOption(WebDriver driver) {
        try {
            return driver.findElement(
                By.xpath("//div[contains(@class,'oxd-autocomplete-dropdown') and contains(@class,'--active')]//span")
            );
        } catch (Exception e) {
            return null; // No autocomplete dropdown present
        }
    }
    
    public static WebElement employeeIdSearchInput(WebDriver driver) {
        return driver.findElement(
            By.xpath("//label[normalize-space()='Employee Id']/following::input[1]")
        );
    }
    
    public static WebElement searchButton(WebDriver driver) {
        return driver.findElement(
            By.xpath("//button[normalize-space()='Search']")
        );
    }
    
    public static WebElement resetButton(WebDriver driver) {
        return driver.findElement(
            By.xpath("//button[normalize-space()='Reset']")
        );
    }
    
    // ==================== VALIDATION & STATUS ====================
    
    public static WebElement personalDetalisHeader(WebDriver driver) {
        return driver.findElement(
            By.xpath("//h6[contains(.,'Personal Details')]")
        );
    }
    
    public static WebElement errorMessage(WebDriver driver) {
        return driver.findElement(
            By.cssSelector(".oxd-input-field-error-message")
        );
    }
    
    // ==================== HELPER METHODS (BOOLEAN CHECKS) ====================
    
    /**
     * Check if "Required" validation message appears
     */
    public static boolean hasRequiredValidation(WebDriver driver) {
        try {
            return driver.findElements(
                By.xpath("//span[normalize-space()='Required']")
            ).size() >= 1;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if on Personal Details page (after successful employee creation)
     */
    public static boolean isPersonalDetailsPage(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            // Wait for URL to contain personalDetails
            wait.until(ExpectedConditions.urlContains("/viewPersonalDetails/empNumber/"));
            
            // Also check for the "Personal Details" heading
            WebElement heading = driver.findElement(
                By.xpath("//h6[text()='Personal Details'] | //h6[contains(text(),'Personal Details')]")
            );
            
            return heading.isDisplayed();
            
        } catch (Exception e) {
            System.err.println("[PIM] Not on Personal Details page: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get count of search result rows
     */
    public static int getSearchResultCount(WebDriver driver) {
        try {
            return driver.findElements(
                By.cssSelector(".oxd-table-body .oxd-table-card")
            ).size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Check if "No Records Found" message appears
     */
    public static boolean hasNoRecordsMessage(WebDriver driver) {
        try {
            return driver.findElements(
                By.xpath("//*[contains(text(),'No Records Found')]")
            ).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if on Employee List page
     */
    public static boolean isEmployeeListPage(WebDriver driver) {
        try {
            return driver.findElements(
                By.xpath("//h6[normalize-space()='Employee Information']")
            ).size() > 0
            || driver.findElements(
                By.xpath("//button[contains(.,'+ Add')]")
            ).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if Add Employee form is displayed
     */
    public static boolean isAddEmployeeForm(WebDriver driver) {
        try {
            return driver.findElements(
                By.xpath("//h6[normalize-space()='Add Employee']")
            ).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}