package utilites;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import POM.POMlogin;
import controller.loginCtrl;
import controller.PIMCtrl;
import controller.leaveCtrl;
import controller.LogoutCtrl;

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

    // ---- LOGIN ----
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

    public void performCreateEmployee(Config config) throws InterruptedException {
        PIMCtrl.openPIMpage(driver);
        PIMCtrl.clickAddEmployee(driver);

        String firstName  = config.getDefaults().getFirstName();
        String middleName = config.getDefaults().getMiddleName();
        String lastName   = config.getDefaults().getLastName();
        String empId      = CustomFunction.generateRandomEmployeeId(); // 4 digits

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

    // ---- LOGOUT ----
    public void performLogout() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            LogoutCtrl.openUserMenu(driver, wait);
            LogoutCtrl.clickLogout(driver, wait);

            wait.until(drv -> {
                try { return POMlogin.usernameFaild(drv).isDisplayed(); }
                catch (Exception e) { return false; }
            });
            System.out.println("[LOGOUT] success: back to login page");
        } catch (Exception e) {
            System.out.println("[LOGOUT] warning: " + e.getMessage());
        }
    }
}
