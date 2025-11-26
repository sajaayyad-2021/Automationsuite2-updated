package controller;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import POM.POMPIM;

public class PIMCtrl {
    
    // ==================== HELPER ====================
    private static WebDriverWait wait(WebDriver d, long sec) {
        return new WebDriverWait(d, Duration.ofSeconds(sec));
    }
    
    // ==================== NAVIGATION ====================
    
    /**
     * Open PIM page from main menu
     */
    public static void openPIMpage(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            // Wait for the main menu to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//ul[contains(@class,'oxd-main-menu')]")
            ));
            
            // Wait for PIM menu item to be clickable
            WebElement pimMenu = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//ul[contains(@class,'oxd-main-menu')]//span[text()='PIM']")
            ));
            
            pimMenu.click();
            
            // Wait for PIM page to load
            wait.until(ExpectedConditions.urlContains("/pim/"));
            
            System.out.println("[PIM] Navigated to PIM page");
            
        } catch (Exception e) {
            System.err.println("[PIM] Error navigating to PIM page: " + e.getMessage());
            throw e;
        }
    }
    
    // ==================== ADD EMPLOYEE ACTIONS ====================
    
    /**
     * Click "Add Employee" button to open the form
     * @throws Exception 
     */
//    public static void clickAddEmployee(WebDriver driver) throws Exception {
//        try {
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
//            
//            // Wait for PIM Employee List page to fully load
//            wait.until(ExpectedConditions.urlContains("/pim/viewEmployeeList"));
//            
//            // Wait a bit for dynamic content
//            Thread.sleep(1500);
//            
//            // Find and click the Add button
//            WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//button[contains(text(),'Add') or contains(@class,'oxd-button')]")
//            ));
//            
//            addBtn.click();
//            
//            // Wait for Add Employee form to load
//            wait.until(ExpectedConditions.urlContains("/pim/addEmployee"));
//            
//            System.out.println("[PIM] Clicked Add Employee");
//            
//        } catch (Exception e) {
//            System.err.println("[PIM] Error clicking Add Employee: " + e.getMessage());
//            
//            // Debug: Print current URL and page source snippet
//            System.err.println("[PIM] Current URL: " + driver.getCurrentUrl());
//            System.err.println("[PIM] Page title: " + driver.getTitle());
//            
//            throw e;
//        }
//    }
    public static void clickAddEmployee(WebDriver driver) {
        try {
            // Direct navigation to Add Employee page
            String currentUrl = driver.getCurrentUrl();
            String baseUrl = currentUrl.substring(0, currentUrl.indexOf("/web/") + 5);
            String addEmployeeUrl = baseUrl + "index.php/pim/addEmployee";
            
            driver.get(addEmployeeUrl);
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlContains("/pim/addEmployee"));
            
            System.out.println("[PIM] Navigated to Add Employee page");
            
        } catch (Exception e) {
            System.err.println("[PIM] Error navigating to Add Employee: " + e.getMessage());
            throw e;
        }
    }
    /**
     * Fill first name field
     */
    public static void fillfirstname(WebDriver driver, String firstname) {
        if (firstname == null || firstname.isEmpty()) {
            System.out.println("[PIMCtrl] First name is empty - skipping");
            return;
        }
        WebElement el = POMPIM.firstnameFaild(driver);
        el.clear();
        el.sendKeys(firstname);
        System.out.println("[PIMCtrl] First name filled: " + firstname);
    }
    
    /**
     * Fill middle name field
     */
    public static void fillmiddlename(WebDriver driver, String middlename) {
        if (middlename == null || middlename.isEmpty()) {
            System.out.println("[PIMCtrl] Middle name is empty - skipping");
            return;
        }
        WebElement el = POMPIM.middlenameFaild(driver);
        el.clear();
        el.sendKeys(middlename);
        System.out.println("[PIMCtrl] Middle name filled: " + middlename);
    }
    
    /**
     * Fill last name field
     */
    public static void filllastname(WebDriver driver, String lastname) {
        if (lastname == null || lastname.isEmpty()) {
            System.out.println("[PIMCtrl] Last name is empty - skipping");
            return;
        }
        WebElement el = POMPIM.lastnameFaild(driver);
        el.clear();
        el.sendKeys(lastname);
        System.out.println("[PIMCtrl] Last name filled: " + lastname);
    }
    
    /**
     * Fill employee ID field
     */
    public static void fillemplyeeId(WebDriver driver, String employeeId) {
        if (employeeId == null || employeeId.isEmpty()) {
            System.out.println("[PIMCtrl] Employee ID is empty - skipping");
            return;
        }
        WebElement el = POMPIM.emplyeeIdFaild(driver);
        el.clear();
        el.sendKeys(employeeId);
        System.out.println("[PIMCtrl] Employee ID filled: " + employeeId);
    }
    
    /**
     * Click Save button (no validation check)
     */
    public static void clickSave(WebDriver driver) throws InterruptedException {
        WebElement saveBtn = POMPIM.saveButton(driver);
        wait(driver, 15).until(ExpectedConditions.elementToBeClickable(saveBtn));
        saveBtn.click();
        
        // Wait for action to complete
        Thread.sleep(2000);
        System.out.println("[PIMCtrl] Save button clicked");
    }
    
    // ==================== SEARCH ACTIONS ====================
    
    /**
     * Fill employee name in search field
     * Handles autocomplete dropdown if it appears
     */
    public static void fillEmployeeName(WebDriver driver, String name) {
        if (name == null || name.isEmpty()) {
            System.out.println("[PIMCtrl] Employee name is empty - skipping");
            return;
        }
        
        WebElement el = POMPIM.employeeNameSearchInput(driver);
        el.clear();
        el.sendKeys(name);
        System.out.println("[PIMCtrl] Employee name filled: " + name);
        
        // Wait a bit for autocomplete dropdown
        try {
            Thread.sleep(1000);
            
            // Try to click autocomplete option if it appears
            WebElement autoOpt = POMPIM.autocompleteOption(driver);
            if (autoOpt != null && autoOpt.isDisplayed()) {
                autoOpt.click();
                System.out.println("[PIMCtrl] Autocomplete option clicked");
            }
        } catch (Exception e) {
            System.out.println("[PIMCtrl] No autocomplete or not needed");
        }
    }
    
    /**
     * Fill employee ID in search field
     */
    public static void fillEmployeeId(WebDriver driver, String empId) {
        if (empId == null || empId.isEmpty()) {
            System.out.println("[PIMCtrl] Employee ID is empty - skipping");
            return;
        }
        
        WebElement el = POMPIM.employeeIdSearchInput(driver);
        el.clear();
        el.sendKeys(empId);
        System.out.println("[PIMCtrl] Employee ID search filled: " + empId);
    }
    
    /**
     * Click Search button and wait for results
     */
    public static void clickSearchButton(WebDriver driver) {
        WebElement btn = POMPIM.searchButton(driver);
        wait(driver, 15).until(ExpectedConditions.elementToBeClickable(btn));
        btn.click();
        
        // Wait for results to load (either results or "No Records")
        wait(driver, 10).until(d -> 
            POMPIM.getSearchResultCount(driver) > 0 || POMPIM.hasNoRecordsMessage(driver)
        );
        
        System.out.println("[PIMCtrl] Search button clicked");
    }
    
    /**
     * Click Reset button to clear search filters
     */
    public static void clickResetButton(WebDriver driver) {
        WebElement btn = POMPIM.resetButton(driver);
        wait(driver, 15).until(ExpectedConditions.elementToBeClickable(btn));
        btn.click();
        
        System.out.println("[PIMCtrl] Reset button clicked");
    }
    
    // ==================== VALIDATION CHECKS ====================
    
    /**
     * Check if required validation message appears
     */
    public static boolean hasRequiredValidation(WebDriver driver) {
        boolean result = POMPIM.hasRequiredValidation(driver);
        System.out.println("[PIMCtrl] Required validation present: " + result);
        return result;
    }
    
    /**
     * Check if redirected to Personal Details page (success)
     */
    public static boolean isOnPersonalDetailsPage(WebDriver driver) {
        boolean result = POMPIM.isPersonalDetailsPage(driver);
        System.out.println("[PIMCtrl] On Personal Details page: " + result);
        return result;
    }
    
    /**
     * Get search result count
     */
    public static int getSearchResultCount(WebDriver driver) {
        int count = POMPIM.getSearchResultCount(driver);
        System.out.println("[PIMCtrl] Search results count: " + count);
        return count;
    }
    
    /**
     * Check if "No Records Found" message appears
     */
    public static boolean hasNoRecordsMessage(WebDriver driver) {
        boolean result = POMPIM.hasNoRecordsMessage(driver);
        System.out.println("[PIMCtrl] No Records message: " + result);
        return result;
    }
}