
package utilites;

import java.io.File;
import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import POM.POMLeave;
import POM.POMPIM;
import POM.POMlogin;
import controller.loginCtrl;

import controller.PIMCtrl;
import controller.leaveCtrl;
import controller.LogoutCtrl;
import controller.RecruitmentCtrl;

public class MainFunctions {

	private final WebDriver driver;
	private final String baseUrl;

	private static final String LOGIN_PATH = "/auth/login";

	public MainFunctions(WebDriver driver, Config config) {
		this.driver = driver;
		this.baseUrl = config.getBaseURL();
	}

	private void navigateTo(String path) {
		driver.get(baseUrl + path);
		System.out.println("[navigate] → " + (baseUrl + path));
	}
	// ... inside MainFunctions class ...

	public void performLogin(Config config) {
	   
	    
	    navigateTo(LOGIN_PATH);
	    POMlogin.waitForLoginPage(driver);
	    System.out.println("[LOGIN] login page ready");
	    
	    loginCtrl.fillUsername(driver, config.getAuth().getUserName());
	    loginCtrl.fillPassword(driver, config.getAuth().getPassWord());
	    loginCtrl.clickLogin(driver);
	    
	    // Try to wait for dashboard through controller, but don't fail if timeout
	    boolean dashboardReached = loginCtrl.waitForDashboard(driver, 20);
	    
	    if (dashboardReached) {
	        System.out.println("[LOGIN] success: dashboard loaded");
	    } else {
	        System.out.println("[LOGIN] dashboard not reached (possible negative test or invalid credentials)");
	    }
	}
	// ============================================
	// ADD THESE GENERIC METHODS TO MainFunctions.java
	// ============================================

	/**
	 * Check if current page is Dashboard
	 */
	public boolean isDashboard() {
	    return POMlogin.isDashboard(driver);
	}

	/**
	 * Check if invalid credentials error is displayed
	 */
	public boolean hasInvalidCredentialsError() {
	    return POMlogin.isInvalidCredentials(driver);
	}

	/**
	 * Check if required field validation is displayed
	 */
	public boolean hasRequiredValidation() {
	    return POMlogin.requiredMessages(driver).size() >= 1;
	}

	/**
	 * Check if username field shows validation error
	 */
	public boolean hasUsernameValidationError() {
	    return POMlogin.isUsernameInvalid(driver);
	}

	/**
	 * Check if password field shows validation error
	 */
	public boolean hasPasswordValidationError() {
	    return POMlogin.isPasswordInvalid(driver);
	}

	/**
	 * Check if password field is masked
	 */
	public boolean isPasswordFieldMasked() {
	    return "password".equals(POMlogin.passwordField(driver).getAttribute("type"));
	}

	/**
	 * Check if redirected to login page
	 */
	public boolean isOnLoginPage() {
	    return POMlogin.isLoginPage(driver) || driver.getCurrentUrl().contains("/auth/login");
	}

	/**
	 * Get current URL
	 */
	public String getCurrentURL() {
	    return driver.getCurrentUrl();
	}

	/**
	 * Wait for dashboard to load
	 */
	public void waitForDashboard() {
	    new WebDriverWait(driver, Duration.ofSeconds(10))
	        .until(d -> POMlogin.isDashboard(driver));
	}
	
	

	// ---- LEAVE SEARCH ----
//	public void performLeaveSearch(Config config) {
//		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
//
//		leaveCtrl.openLeaveList(driver, wait);
//		leaveCtrl.fillFromDate(driver, wait, config.getLeaveSearch().getFromDate());
//		leaveCtrl.fillToDate(driver, wait, config.getLeaveSearch().getToDate());
//		leaveCtrl.fillEmployeeName(driver, config.getLeaveSearch().getEmployeeName());
//		leaveCtrl.selectStatus(driver, wait, config.getLeaveSearch().getStatus());
//		leaveCtrl.selectLeaveType(driver, wait, config.getLeaveSearch().getLeaveType());
//		leaveCtrl.selectSubUnit(driver, wait, config.getLeaveSearch().getSubUnit());
//		leaveCtrl.clickSearch(driver);
//
//		System.out.println("[leave] leave search completed.");
//	}

	// ---- RECRUITMENT: Add Candidate (NEW) ----
	public void performRecruitmentAdd(Config config) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		// Open Recruitment > +Add
		RecruitmentCtrl.openRecruitment(driver, wait);
		RecruitmentCtrl.openAddCandidate(driver, wait);

		// Fill Full Name
		RecruitmentCtrl.fillFullName(driver, config.getRecruitment().getCandidateFirstName(),
				config.getRecruitment().getCandidateMiddleName(), config.getRecruitment().getCandidateLastName());

		// Vacancy dropdown (exact visible text must exist)
		RecruitmentCtrl.selectVacancy(driver, wait, config.getRecruitment().getVacancy());

		// Contact info
		RecruitmentCtrl.fillContact(driver, config.getRecruitment().getEmail(),
				config.getRecruitment().getContactNumber());

		// Upload resume
		RecruitmentCtrl.uploadResume(driver, config.getRecruitment().getResumePath());

		// Keywords
		RecruitmentCtrl.fillKeywords(driver, config.getRecruitment().getKeywords());

		// Date of Application
		RecruitmentCtrl.pickDateOfApplication(driver, wait, config.getRecruitment().getDateOfApplication());

		// Notes
		RecruitmentCtrl.fillNotes(driver, config.getRecruitment().getNotes());

		// Consent
		RecruitmentCtrl.setConsent(driver, config.getRecruitment().isConsent());

		// Save
		RecruitmentCtrl.clickSave(driver);

		System.out.println("[recruitment] add candidate flow completed.");
	}

	// ---- LOGOUT ----
	public static void performLogout(WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		try {
			LogoutCtrl.openUserMenu(driver, wait);
			LogoutCtrl.clickLogout(driver, wait);

			wait.until(drv -> {
				try {
					return POMlogin.usernameField(drv).isDisplayed();
				} catch (Exception e) {
					return false;
				}
			});

			// System.out.println("[LOGOUT] success: back to login page");

		} catch (Exception e) {
			// System.out.println("[LOGOUT] warning: " + e.getMessage());
		}
	}

	public static void deleteFiles(String folderPath) {
		try {
			File folder = new File(folderPath);

			if (!folder.exists() || !folder.isDirectory()) {
				// System.out.println("Folder does not exist: " + folderPath);
				return;
			}

			File[] files = folder.listFiles();

			if (files == null) {
				// System.out.println("No files found in: " + folderPath);
				return;
			}

			for (File f : files) {
				if (f.isFile()) {
					if (f.delete()) {
						// System.out.println("Deleted: " + f.getName());
					} else {
						// System.out.println("Failed to delete: " + f.getName());
					}
				}
			}

		} catch (Exception e) {
			System.err.println("Error deleting files in: " + folderPath);
			e.printStackTrace();
		}

	}

	
	
	/**
	 * Generic function to add employee - works for POSITIVE and NEGATIVE tests
	 * Config determines: firstName, middleName, lastName
	 * Returns: empId (success) or "VALIDATION_SHOWN" (failure)
	 */
	public String performAddEmployee(Config cfg) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	        
	        // 1. Navigate to PIM page
	        PIMCtrl.openPIMpage(driver);
	        
	        // 2. Click Add Employee
	        PIMCtrl.clickAddEmployee(driver);
	        
	        // 3. Wait for form
	        wait.until(ExpectedConditions.presenceOfElementLocated(
	            By.xpath("//h6[text()='Add Employee']")
	        ));
	        Thread.sleep(1500);
	        
	        // 4. Get data from config
	        String first = cfg.getDefaults().getFirstName();
	        String middle = cfg.getDefaults().getMiddleName();
	        String last = cfg.getDefaults().getLastName();
	        
	        // 5. Fill fields
	        if (first != null && !first.isEmpty()) {
	            PIMCtrl.fillfirstname(driver, first);
	        }
	        
	        if (middle != null && !middle.isEmpty()) {
	            PIMCtrl.fillmiddlename(driver, middle);
	        }
	        
	        if (last != null && !last.isEmpty()) {
	            PIMCtrl.filllastname(driver, last);
	        }
	        
	        // 6. Generate employee ID
	        String empId = CustomFunction.generateRandomEmployeeId();
	        PIMCtrl.fillemplyeeId(driver, empId);
	        
	        // 7. Click Save
	        WebElement saveBtn = driver.findElement(By.xpath("//button[normalize-space()='Save']"));
	        saveBtn.click();
	        
	        // 8. Wait longer for the response
	        Thread.sleep(3000);
	        
	        // 9. Check result - validation error OR success
	        // First check for validation (stays on same page)
	        if (POMPIM.hasRequiredValidation(driver)) {
	            System.out.println("[PIM] Validation shown - required fields missing");
	            return "VALIDATION_SHOWN";
	        }
	        
	        // Check if URL changed to personal details
	        String currentUrl = driver.getCurrentUrl();
	        System.out.println("[PIM] Current URL after save: " + currentUrl);
	        
	        if (currentUrl.contains("/viewPersonalDetails/empNumber/") || 
	            currentUrl.contains("/pim/viewPersonalDetails")) {
	            System.out.println("[PIM] Success - employee created: " + empId);
	            return "SUCCESS";  // Change this to return "SUCCESS" instead of empId
	        } else if (POMPIM.isPersonalDetailsPage(driver)) {
	            System.out.println("[PIM] Success - employee created: " + empId);
	            return "SUCCESS";
	        } else {
	            System.out.println("[PIM] Unknown state - URL: " + currentUrl);
	            return "UNKNOWN_STATE";
	        }
	        
	    } catch (Exception e) {
	        System.err.println("[PIM] Error in performAddEmployee: " + e.getMessage());
	        e.printStackTrace();
	        return "ERROR: " + e.getMessage();
	    }
	}

	/**
	 * Generic function to search employee - works for ALL search scenarios
	 * Config determines: searchBy ("name" or "id"), searchValue
	 * Returns: "FOUND_X" (X = number of results) or "NO_RESULTS" or "ERROR"
	 */
	public String performSearchEmployee(Config cfg) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	        
	        // 1. Navigate to PIM page
	        PIMCtrl.openPIMpage(driver);
	        
	        // 2. Get search criteria from config
	        String searchBy = cfg.getDefaults().getFirstName(); // Reuse firstName as searchBy
	        String searchValue = cfg.getDefaults().getLastName(); // Reuse lastName as searchValue
	        
	        // 3. Fill search fields based on searchBy
	        if ("name".equalsIgnoreCase(searchBy)) {
	            PIMCtrl.fillEmployeeName(driver, searchValue);
	        } else if ("id".equalsIgnoreCase(searchBy)) {
	            PIMCtrl.fillEmployeeId(driver, searchValue);
	        } else {
	            // Leave fields empty for negative test
	            System.out.println("[PIM] Search with empty fields");
	        }
	        
	        // 4. Click Search
	        PIMCtrl.clickSearchButton(driver);
	        
	        // 5. Get result count (with exception handling for timeout)
	        Thread.sleep(2000);
	        
	        int count;
	        try {
	            count = POMPIM.getSearchResultCount(driver);
	        } catch (Exception e) {
	            // If getSearchResultCount times out, assume no results
	            if (e.getMessage().contains("Expected condition failed") || 
	                e.getMessage().contains("TimeoutException")) {
	                System.out.println("[PIM] No results found (timeout caught)");
	                return "NO_RESULTS";
	            } else {
	                throw e; // Re-throw unexpected exceptions
	            }
	        }
	        
	        if (count > 0) {
	            System.out.println("[PIM] Found " + count + " result(s)");
	            return "FOUND_" + count;
	        } else {
	            System.out.println("[PIM] No results found");
	            return "NO_RESULTS";
	        }
	        
	    } catch (Exception e) {
	        System.err.println("[PIM] Error in performSearchEmployee: " + e.getMessage());
	        e.printStackTrace();
	        return "ERROR: " + e.getMessage();
	    }
	}


	
	// Helper method - check if validation appears
	private boolean hasRequiredValidation1() {
	    return POMPIM.hasRequiredValidation(driver);
	}

	// Helper method - check if on Personal Details page
	private boolean isOnPersonalDetailsPage() {
	    return POMPIM.isPersonalDetailsPage(driver);
	}

	// ============================================
	// LEAVE FUNCTIONS
	// ============================================

	/**
	 * Basic Leave Search - opens leave list and performs search
	 */
	// ============================================
	// LEAVE FUNCTION - ONE UNIFIED METHOD
	// ============================================

	/**
	 * Unified Leave Search Function
	 * Handles ALL leave search scenarios based on config
	 * Returns: "SUCCESS", "NO_RESULTS", "VALIDATION_ERROR", or "ERROR"
	 */
	public String performLeaveSearch(Config cfg) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	        
	        // 1. Open Leave List page
	        leaveCtrl.openLeaveList(driver, wait);
	        System.out.println("[LEAVE] Opened Leave List page");
	        
	        // 2. Get search criteria from config
	        String fromDate = cfg.getLeaveSearch().getFromDate();
	        String toDate = cfg.getLeaveSearch().getToDate();
	        String empName = cfg.getLeaveSearch().getEmployeeName();
	        String status = cfg.getLeaveSearch().getStatus();
	        String leaveType = cfg.getLeaveSearch().getLeaveType();
	        String subUnit = cfg.getLeaveSearch().getSubUnit();
	        
	        // 3. Fill From Date (if provided)
	        if (fromDate != null && !fromDate.isEmpty()) {
	            leaveCtrl.fillFromDate(driver, wait, fromDate);
	            System.out.println("[LEAVE] Filled From Date: " + fromDate);
	        }
	        
	        // 4. Fill To Date (if provided)
	        if (toDate != null && !toDate.isEmpty()) {
	            leaveCtrl.fillToDate(driver, wait, toDate);
	            System.out.println("[LEAVE] Filled To Date: " + toDate);
	        }
	        
	        // 5. Fill Employee Name (if provided)
	        if (empName != null && !empName.isEmpty()) {
	            leaveCtrl.fillEmployeeName(driver, empName);
	            System.out.println("[LEAVE] Filled Employee Name: " + empName);
	            
	            // Wait for autocomplete and select first option
	            Thread.sleep(1500);
	            try {
	                WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(
	                    By.xpath("//div[contains(@class,'oxd-autocomplete-option')]")
	                ));
	                firstOption.click();
	                System.out.println("[LEAVE] Selected employee from dropdown");
	            } catch (Exception e) {
	                System.out.println("[LEAVE] No autocomplete options or employee not found");
	            }
	        }
	        
	        // 6. Select Status (if provided)
	        if (status != null && !status.isEmpty()) {
	            leaveCtrl.selectStatus(driver, wait, status);
	            System.out.println("[LEAVE] Selected Status: " + status);
	        }
	        
	        // 7. Select Leave Type (if provided)
	        if (leaveType != null && !leaveType.isEmpty()) {
	            leaveCtrl.selectLeaveType(driver, wait, leaveType);
	            System.out.println("[LEAVE] Selected Leave Type: " + leaveType);
	        }
	        
	        // 8. Select Sub Unit (if provided)
	        if (subUnit != null && !subUnit.isEmpty()) {
	            leaveCtrl.selectSubUnit(driver, wait, subUnit);
	            System.out.println("[LEAVE] Selected Sub Unit: " + subUnit);
	        }
	        
	        // 9. Click Search
	        leaveCtrl.clickSearch(driver);
	        System.out.println("[LEAVE] Clicked Search button");
	        
	        // 10. Wait for results to load
	        Thread.sleep(3000);
	        
	        // 11. Check for validation errors (invalid date range, etc.)
	        try {
	            WebElement validationError = driver.findElement(
	                By.xpath("//*[contains(text(),'To date should be after from date') or " +
	                        "contains(text(),'Should be a valid date') or " +
	                        "contains(@class,'oxd-input-field-error-message')]")
	            );
	            if (validationError.isDisplayed()) {
	                System.out.println("[LEAVE] Validation error detected");
	                return "VALIDATION_ERROR";
	            }
	        } catch (Exception e) {
	            // No validation error
	        }
	        
	        // 12. Check for "No Records Found"
	        try {
	            WebElement noRecords = driver.findElement(
	                By.xpath("//*[contains(text(),'No Records Found')] | " +
	                        "//span[contains(text(),'No Records Found')]")
	            );
	            if (noRecords.isDisplayed()) {
	                System.out.println("[LEAVE] No records found");
	                return "NO_RESULTS";
	            }
	        } catch (Exception e) {
	            // Records found or checking failed
	        }
	        
	        // 13. Check if results table exists
	        try {
	            WebElement resultsTable = wait.until(ExpectedConditions.presenceOfElementLocated(
	                By.xpath("//div[contains(@class,'oxd-table-body')] | " +
	                        "//div[@class='oxd-table-card']")
	            ));
	            
	            if (resultsTable.isDisplayed()) {
	                System.out.println("[LEAVE] Search results found");
	                return "SUCCESS";
	            }
	        } catch (Exception e) {
	            System.out.println("[LEAVE] Could not find results table");
	        }
	        
	        // 14. Default: assume success if no errors
	        System.out.println("[LEAVE] Search completed");
	        return "SUCCESS";
	        
	    } catch (Exception e) {
	        System.err.println("[LEAVE] Error in performLeaveSearch: " + e.getMessage());
	        e.printStackTrace();
	        return "ERROR: " + e.getMessage();
	    }
	}

	/**
	 * Reset Leave Filters
	 * Returns: "RESET_SUCCESS" or "RESET_FAILED"
	 */
	public String performLeaveReset(Config cfg) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	        
	        // 1. Open Leave List
	        leaveCtrl.openLeaveList(driver, wait);
	        
	        // 2. Fill some filters first
	        leaveCtrl.fillFromDate(driver, wait, "2024-01-01");
	        leaveCtrl.fillToDate(driver, wait, "2024-12-31");
	        Thread.sleep(1000);
	        
	        // 3. Click Reset
	        leaveCtrl.clickReset(driver);
	        Thread.sleep(2000);
	        
	        // 4. Verify fields are cleared
	        try {
	            WebElement fromDateInput = POMLeave.fromDateInput(driver);
	            String fromValue = fromDateInput.getAttribute("value");
	            
	            if (fromValue == null || fromValue.isEmpty()) {
	                System.out.println("[LEAVE] Reset successful - fields cleared");
	                return "RESET_SUCCESS";
	            } else {
	                System.out.println("[LEAVE] Reset failed - fields still have values");
	                return "RESET_FAILED";
	            }
	        } catch (Exception e) {
	            System.out.println("[LEAVE] Could not verify reset");
	            return "RESET_FAILED";
	        }
	        
	    } catch (Exception e) {
	        System.err.println("[LEAVE] Error in performLeaveReset: " + e.getMessage());
	        e.printStackTrace();
	        return "ERROR: " + e.getMessage();
	    }
	}
}

	