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

        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/dashboard"),
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Dashboard']"))
        ));
        System.out.println("[LOGIN] success: dashboard loaded");
    }

    // ---- CREATE EMPLOYEE (PIM) ----
    public void performCreateEmployee(Config config) throws InterruptedException {
        PIMCtrl.openPIMpage(driver);
        PIMCtrl.clickAddEmployee(driver);

        String firstName  = config.getDefaults().getFirstName();
        String middleName = config.getDefaults().getMiddleName();
        String lastName   = config.getDefaults().getLastName();
        String empId      = CustomFunction.generateRandomEmployeeId();

        PIMCtrl.fillfirstname(driver, firstName);
        PIMCtrl.fillmiddlename(driver, middleName);
        PIMCtrl.filllastname(driver, lastName);
        PIMCtrl.fillemplyeeId(driver, empId);

        PIMCtrl.clickSave(driver);
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
        RecruitmentCtrl.fillFullName(
            driver,
            config.getRecruitment().getCandidateFirstName(),
            config.getRecruitment().getCandidateMiddleName(),
            config.getRecruitment().getCandidateLastName()
        );

        // Vacancy dropdown (exact visible text must exist)
        RecruitmentCtrl.selectVacancy(driver, wait, config.getRecruitment().getVacancy());

        // Contact info
        RecruitmentCtrl.fillContact(
            driver,
            config.getRecruitment().getEmail(),
            config.getRecruitment().getContactNumber()
        );

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
                    return POMlogin.usernameFaild(drv).isDisplayed(); 
                } catch (Exception e) { 
                    return false; 
                }
            });

          //  System.out.println("[LOGOUT] success: back to login page");

        } catch (Exception e) {
           // System.out.println("[LOGOUT] warning: " + e.getMessage());
        }
    }

    public static void deleteFiles(String folderPath) {
        try {
            File folder = new File(folderPath);

            if (!folder.exists() || !folder.isDirectory()) {
            //    System.out.println("Folder does not exist: " + folderPath);
                return;
            }

            File[] files = folder.listFiles();

            if (files == null) {
              //  System.out.println("No files found in: " + folderPath);
                return;
            }

            for (File f : files) {
                if (f.isFile()) {
                    if (f.delete()) {
              //          System.out.println("Deleted: " + f.getName());
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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Case 1: Both fields empty
        driver.get(baseUrl + "/auth/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
     //   System.out.println("[emptyFields] login page ready (case 1)");

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
     //   System.out.println("[emptyFields] login page ready (case 2)");

        POMlogin.usernameFaild(driver).sendKeys(cfg.getAuth().getUserName());
        loginCtrl.clicklogin(driver);

        boolean passError = wait.until(d -> {
            return POMlogin.requiredMessages(driver).size() >= 1
                    || POMlogin.isPasswordInvalid(driver);
        });

        if (!passError) {
            overallPass = false;
      //      System.out.println("[emptyFields] Password-required validation FAILED");
        }

        // Case 3: Password only
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
      //  System.out.println("[emptyFields] login page ready (case 3)");

        POMlogin.passwordFaild(driver).sendKeys(cfg.getAuth().getPassWord());
        loginCtrl.clicklogin(driver);

        boolean userError = wait.until(d -> {
            return POMlogin.requiredMessages(driver).size() >= 1
                    || POMlogin.isUsernameInvalid(driver);
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
      //  System.out.println("[passwordHidden] login page ready");

        WebElement pass = POMlogin.passwordFaild(driver);
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
	 

}
