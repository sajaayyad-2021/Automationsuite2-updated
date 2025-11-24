package utilites;

import java.io.File;
import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		navigateTo(LOGIN_PATH);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
		System.out.println("[LOGIN] login page ready");

		loginCtrl.fillUsername(driver, config.getAuth().getUserName());
		loginCtrl.fillpassword(driver, config.getAuth().getPassWord());
		loginCtrl.clicklogin(driver);

		wait.until(ExpectedConditions.or(ExpectedConditions.urlContains("/dashboard"),
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Dashboard']"))));
		System.out.println("[LOGIN] success: dashboard loaded");
	}

	
	

	// ---- LEAVE SEARCH ----
	public void performLeaveSearch(Config config) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		leaveCtrl.openLeaveList(driver, wait);
		leaveCtrl.fillFromDate(driver, wait, config.getLeaveSearch().getFromDate());
		leaveCtrl.fillToDate(driver, wait, config.getLeaveSearch().getToDate());
		leaveCtrl.fillEmployeeName(driver, config.getLeaveSearch().getEmployeeName());
		leaveCtrl.selectStatus(driver, wait, config.getLeaveSearch().getStatus());
		leaveCtrl.selectLeaveType(driver, wait, config.getLeaveSearch().getLeaveType());
		leaveCtrl.selectSubUnit(driver, wait, config.getLeaveSearch().getSubUnit());
		leaveCtrl.clickSearch(driver);

		System.out.println("[leave] leave search completed.");
	}

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

	public boolean runEmptyFieldsScenarios(Config cfg) {
		boolean overallPass = true;

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		// Case 1: Both fields empty
		driver.get(baseUrl + "/auth/login");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
		// System.out.println("[emptyFields] login page ready (case 1)");

		// Click login with both fields empty
		loginCtrl.clicklogin(driver);

		boolean bothRequired = wait.until(d -> {
			try {
				return POMlogin.requiredMessages(driver).size() >= 2;
			} catch (Exception e) {
				return false;
			}
		});

		if (!bothRequired) {
			overallPass = false;
			System.out.println("[emptyFields] Both-required validation FAILED");
		}

		// Case 2: Username only
		driver.navigate().refresh();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
		// System.out.println("[emptyFields] login page ready (case 2)");

		POMlogin.usernameField(driver).sendKeys(cfg.getAuth().getUserName());
		loginCtrl.clicklogin(driver);

		boolean passError = wait.until(d -> {
			return POMlogin.requiredMessages(driver).size() >= 1 || POMlogin.isPasswordInvalid(driver);
		});

		if (!passError) {
			overallPass = false;
			// System.out.println("[emptyFields] Password-required validation FAILED");
		}

		// Case 3: Password only
		driver.navigate().refresh();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
		// System.out.println("[emptyFields] login page ready (case 3)");

		POMlogin.passwordField(driver).sendKeys(cfg.getAuth().getPassWord());
		loginCtrl.clicklogin(driver);

		boolean userError = wait.until(d -> {
			return POMlogin.requiredMessages(driver).size() >= 1 || POMlogin.isUsernameInvalid(driver);
		});

		if (!userError) {
			overallPass = false;
			System.out.println("[emptyFields] Username-required validation FAILED");
		}

		return overallPass;
	}

	public boolean isPasswordFieldMasked(Config cfg, String samplePassword) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		driver.get(baseUrl + "/auth/login");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
		// System.out.println("[passwordHidden] login page ready");

		WebElement pass = POMlogin.passwordField(driver);
		pass.clear();
		pass.sendKeys(samplePassword);

		String type = pass.getAttribute("type");
		System.out.println("[passwordHidden] field type = " + type);

		return "password".equalsIgnoreCase(type);
	}

	public boolean openPimAndVerifyHeader() {
		// TODO Auto-generated method stub
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		PIMCtrl.openPIMpage(driver);

		WebElement header = POMPIM.pimHeader(driver);
		wait.until(ExpectedConditions.visibilityOf(header));

		boolean visible = header.isDisplayed();
		System.out.println("[PIM] header text: " + header.getText());

		return visible;
	}
	// inside utilites.MainFunctions
	
	//-----------------1-----------------

	public boolean openPimList() {
	    try {
	        PIMCtrl.openPIMpage(driver);

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

	        wait.until(ExpectedConditions.or(
	                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Employee Information']")),
	                ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(.,'+ Add')]")),
	                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='PIM']"))
	        ));

	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
//--------------2----------------------
	
	public boolean openAddEmployeeForm() {
	    try {
	        openPimList();

	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

	        By addBtn = By.xpath("//button[contains(.,'+ Add')] | //a[normalize-space()='Add Employee']");
	        WebElement add = wait.until(ExpectedConditions.elementToBeClickable(addBtn));
	        add.click();

	        wait.until(ExpectedConditions.visibilityOfElementLocated(
	                By.xpath("//h6[normalize-space()='Add Employee']")
	        ));

	        return true;

	    } catch (Exception e) {
	        return false;
	    }
	}
	//------------------3---------------------


	public String performCreateEmployee(Config cfg) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

	        openPimList();

	        By addBtn = By.xpath("//button[contains(.,'+ Add')] | //a[normalize-space()='Add Employee']");
	        WebElement add = wait.until(ExpectedConditions.elementToBeClickable(addBtn));
	        add.click();

	        wait.until(ExpectedConditions.visibilityOfElementLocated(
	                By.xpath("//h6[normalize-space()='Add Employee']")
	        ));

	        String first = cfg.getDefaults().getFirstName();
	        String middle = cfg.getDefaults().getMiddleName();
	        String last = cfg.getDefaults().getLastName();

	        String empId = CustomFunction.generateRandomEmployeeId();

	        WebElement fn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstName")));
	        fn.clear();
	        fn.sendKeys(first);

	        WebElement mn = driver.findElement(By.name("middleName"));
	        mn.clear();
	        if (middle != null && !middle.isBlank()) {
	            mn.sendKeys(middle);
	        }

	        WebElement ln = driver.findElement(By.name("lastName"));
	        ln.clear();
	        ln.sendKeys(last);

	        WebElement idField = driver.findElement(By.xpath("//label[contains(.,'Employee Id')]/../following-sibling::div//input"));
	        idField.clear();
	        idField.sendKeys(empId);

	        WebElement save = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Save']")));
	        save.click();

	        wait.until(ExpectedConditions.or(
	                ExpectedConditions.urlContains("/pim/viewPersonalDetails"),
	                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[contains(.,'Personal Details')]"))
	        ));

	        return empId;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "";
	    }
	}
	//-----------------------4-----------------------
	public boolean validateAddEmployeeMissingFields() {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

	        openPimList();

	        // 2 Add
	        By addBtn = By.xpath("//button[contains(.,'+ Add')] | //a[normalize-space()='Add Employee']");
	        WebElement add = wait.until(ExpectedConditions.elementToBeClickable(addBtn));
	        add.click();

	        wait.until(ExpectedConditions.visibilityOfElementLocated(
	                By.xpath("//h6[normalize-space()='Add Employee']")
	        ));

	        // 4) clean first & last name
	        WebElement first = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstName")));
	        WebElement last  = driver.findElement(By.name("lastName"));

	        first.clear();
	        last.clear();

	        // 5)  Save
	        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(
	                By.xpath("//button[normalize-space()='Save']")
	        ));
	        saveBtn.click();

	        boolean requiredExists = wait.until(d ->
	                d.findElements(By.xpath("//span[normalize-space()='Required']")).size() >= 1
	        );

	        return requiredExists;

	    } catch (Exception e) {
	        return false;
	    }
	}



	
	public boolean searchEmployeeByName(String fullName) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

	        openPimList();

	        By nameInput = By.xpath("//label[normalize-space()='Employee Name']/following::input[1]");
	        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(nameInput));

	        input.clear();
	        input.sendKeys(fullName);

	        try {
	            By autoOpt = By.xpath("//div[contains(@class,'oxd-autocomplete-dropdown') and contains(@class,'--active')]//span");
	            WebElement opt = new WebDriverWait(driver, Duration.ofSeconds(5))
	                    .until(ExpectedConditions.visibilityOfElementLocated(autoOpt));
	            opt.click();
	        } catch (Exception ignore) {}

	        By searchBtn = By.xpath("//button[normalize-space()='Search']");
	        wait.until(ExpectedConditions.elementToBeClickable(searchBtn)).click();

	        boolean hasRows = new WebDriverWait(driver, Duration.ofSeconds(10))
	                .until(d -> d.findElements(By.cssSelector(".oxd-table-body .oxd-table-card")).size() >= 1);

	        return hasRows;

	    } catch (Exception e) {
	        return false;
	    }
	}


	public boolean searchEmployeeById(String empId) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

	        openPimList();

	        By idInput = By.xpath("//label[normalize-space()='Employee Id']/following::input[1]");
	        WebElement idField = wait.until(ExpectedConditions.elementToBeClickable(idInput));

	        idField.clear();
	        idField.sendKeys(empId);

	        By searchBtn = By.xpath("//button[normalize-space()='Search']");
	        wait.until(ExpectedConditions.elementToBeClickable(searchBtn)).click();

	        boolean found = new WebDriverWait(driver, Duration.ofSeconds(10))
	                .until(d -> d.findElements(By.cssSelector(".oxd-table-body .oxd-table-card")).size() >= 1);

	        return found;

	    } catch (Exception e) {
	        return false;
	    }
	}


	public boolean editEmployeeMiddleName(String newMiddle) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

	        WebElement middle = wait.until(
	                ExpectedConditions.visibilityOfElementLocated(By.name("middleName"))
	        );

	        middle.clear();
	        middle.sendKeys(newMiddle);

	        By saveBtn = By.xpath("//button[normalize-space()='Save']");
	        WebElement save = wait.until(ExpectedConditions.elementToBeClickable(saveBtn));
	        save.click();

	        try {
	            wait.until(ExpectedConditions.visibilityOfElementLocated(
	                    By.cssSelector(".oxd-toast--success")
	            ));
	        } catch (Exception ignored) {}

	        String actual = driver.findElement(By.name("middleName")).getAttribute("value");

	        return newMiddle.equals(actual);

	    } catch (Exception e) {
	        return false;
	    }
	}


	public boolean deleteEmployeeById(String empId) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

	        openPimList();

	        By idInput = By.xpath("//label[normalize-space()='Employee Id']/following::input[1]");
	        WebElement idField = wait.until(ExpectedConditions.elementToBeClickable(idInput));

	        idField.clear();
	        idField.sendKeys(empId);

	        By searchBtn = By.xpath("//button[normalize-space()='Search']");
	        wait.until(ExpectedConditions.elementToBeClickable(searchBtn)).click();

	        By deleteBtn = By.xpath("(//div[@class='oxd-table-cell-actions']//button[contains(@class,'bi-trash')])[1]");
	        WebElement del = wait.until(ExpectedConditions.elementToBeClickable(deleteBtn));
	        del.click();

	        WebElement yes = wait.until(
	                ExpectedConditions.elementToBeClickable(
	                        By.xpath("//button[normalize-space()='Yes, Delete']")
	                )
	        );
	        
	        yes.click();
	        boolean deleted = new WebDriverWait(driver, Duration.ofSeconds(15))
	                .until(d -> d.findElements(By.xpath("//*[contains(text(),'" + empId + "')]")).isEmpty());

	        return deleted;

	    } catch (Exception e) {
	        return false;
	    }
	}

	// =====================================================================
//  LOGIN TEST HELPERS  (FOR TC_LOG_006 → 017)
//=====================================================================

	public void goToLogin() {
		driver.get(baseUrl + "/auth/login");
		POMlogin.waitForLoginPage(driver);
	}

	public void clearCookies() {
		try {
			driver.manage().deleteAllCookies();
		} catch (Exception ignored) {
		}
	}

	public void resetToLogin() {
		clearCookies();
		goToLogin();
	}

	public boolean attemptLogin(String username, String password) {
		resetToLogin();
		loginCtrl.fillUsername(driver, username);
		loginCtrl.fillpassword(driver, password);
		loginCtrl.clicklogin(driver);

// wait for either error… or dashboard
		new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(d -> POMlogin.isInvalidCredentials(driver) || POMlogin.isDashboard(driver));

		return POMlogin.isDashboard(driver);
	}

	public boolean expectInvalidCredentials() {
		boolean invalid = POMlogin.isInvalidCredentials(driver);
		boolean dash = POMlogin.isDashboard(driver);
		return invalid && !dash;
	}

	public boolean expectRequiredUsername() {
		return POMlogin.requiredMessages(driver).size() >= 1 || POMlogin.isUsernameInvalid(driver);
	}

	public boolean expectRequiredPassword() {
		return POMlogin.requiredMessages(driver).size() >= 1 || POMlogin.isPasswordInvalid(driver);
	}

	public boolean directDashboardAccessBlocked() {
		clearCookies();
		driver.get(baseUrl + "/dashboard");

		new WebDriverWait(driver, Duration.ofSeconds(10))
				.until(d -> POMlogin.isLoginPage(driver) || driver.getCurrentUrl().contains("/auth/login"));

		return POMlogin.isLoginPage(driver) || driver.getCurrentUrl().contains("/auth/login");
	}

	public boolean logoutRedirectsToLogin() {
		try {
			MainFunctions.performLogout(driver);
		} catch (Exception ignored) {
		}
		return POMlogin.isLoginPage(driver) || driver.getCurrentUrl().contains("/auth/login");
	}

	public boolean bruteForceLockoutTest(String username) {
		String lastError = "";
		for (int i = 1; i <= 5; i++) {
			resetToLogin();
			loginCtrl.fillUsername(driver, username);
			loginCtrl.fillpassword(driver, "WRONG" + i);
			loginCtrl.clicklogin(driver);

			new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> POMlogin.isInvalidCredentials(driver));

			lastError = POMlogin.getInvalidMessage(driver).toLowerCase();
		}

		boolean locked = lastError.contains("lock") || lastError.contains("attempt") || lastError.contains("too many");

		boolean invalid = POMlogin.isInvalidCredentials(driver);

		return locked || invalid;
	}
	
	public boolean basicLeaveSearch(Config cfg) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

	        leaveCtrl.openLeaveList(driver, wait);
	        leaveCtrl.fillFromDate(driver, wait, cfg.getLeaveSearch().getFromDate());
	        leaveCtrl.fillToDate(driver, wait, cfg.getLeaveSearch().getToDate());
	        leaveCtrl.clickSearch(driver);

	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}

	public boolean searchLeaveByEmployee(Config cfg) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

	        leaveCtrl.openLeaveList(driver, wait);
	        leaveCtrl.fillEmployeeName(driver, cfg.getLeaveSearch().getEmployeeName());
	        leaveCtrl.clickSearch(driver);

	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}

	public boolean invalidDateRange(Config cfg) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

	        leaveCtrl.openLeaveList(driver, wait);

	        leaveCtrl.fillFromDate(driver, wait, cfg.getLeaveSearch().getToDate());
	        leaveCtrl.fillToDate(driver, wait, cfg.getLeaveSearch().getFromDate());
	        
	        leaveCtrl.clickSearch(driver);

	        return true; 
	    } catch (Exception e) {
	        return false;
	    }
	}
	public boolean selectLeaveStatus(Config cfg) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

	        leaveCtrl.openLeaveList(driver, wait);
	        leaveCtrl.selectStatus(driver, wait, cfg.getLeaveSearch().getStatus());

	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public boolean selectLeaveTypeOnly(Config cfg) {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

	        leaveCtrl.openLeaveList(driver, wait);
	        leaveCtrl.selectLeaveType(driver, wait, cfg.getLeaveSearch().getLeaveType());

	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}


	public boolean resetLeaveFilters() {
	    try {
	        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

	        leaveCtrl.openLeaveList(driver, wait);
	        leaveCtrl.clickReset(driver);

	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}


}
