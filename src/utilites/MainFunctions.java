package utilites;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import POM.POMlogin;
import POM.POMPIM;
import controller.loginCtrl;
import controller.PIMCtrl;

public class MainFunctions {

	private final WebDriver driver;
	private final WebDriverWait wait;

	// constructor
	public MainFunctions(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	// -------------------1-----------------

	public void performLogin(Config config) {

		String loginUrl = config.getBaseURL() + "/auth/login";

		driver.get(loginUrl);

		System.out.println("[NAVIGATE] Login page: " + loginUrl);

		wait.until(ExpectedConditions.visibilityOf(POMlogin.usernameFaild(driver)));

		// config.json
		String username = config.getAuth().getUserName();
		String password = config.getAuth().getPassWord();

		loginCtrl.fillUsername(driver, username);
		loginCtrl.fillpassword(driver, password);
		loginCtrl.clicklogin(driver);

		System.out.println(" submitted credentials");

		// to ensure that the dashboard appare
		boolean dashboardOk = wait.until(drv -> drv.getCurrentUrl().contains("/dashboard"));
		if (dashboardOk) {
			System.out.println(" success  dashboard loaded");
		} else {
			System.out.println(" warning  dashboard not detected");
		}
	}

	// -------------------2-----------------
	public void performCreateEmployee(Config config) {

		String addEmpUrl = config.getBaseURL() + "/pim/addEmployee";
		driver.get(addEmpUrl);
		System.out.println(" add Employee page: " + addEmpUrl);

		wait.until(ExpectedConditions.visibilityOf(POMPIM.firstnameFaild(driver)));

		String firstName = config.getDefaults().getFirstName();
		String middleName = config.getDefaults().getMiddleName();
		String lastName = config.getDefaults().getLastName();

		String empId = CustomFunction.generateRandomEmployeeId();
		System.out.println(" generated ID: " + empId);

		// Use controller
		PIMCtrl.fillfirstname(driver, firstName);
		PIMCtrl.fillmiddlename(driver, middleName);
		PIMCtrl.filllastname(driver, lastName);
		PIMCtrl.fillemplyeeId(driver, empId);

		// -----------saving----------------
		PIMCtrl.clickAddEmployee(driver);
		PIMCtrl.clickSave(driver);

		boolean personalShown = wait.until(drv -> {
			try {
				return PIMCtrl.isdetailspagedisplayed(drv);
			} catch (Exception e) {
				return false;
			}
		});

		if (personalShown) {
			System.out.println(" employee created details page visible");
		} else {
			System.out.println(" warning could not confirm personal details page");
		}
	}

	// -------------------3-----------------
	public void performLogout(Config config) {
		String logoutUrl = config.getBaseURL() + "/auth/logout";
		driver.get(logoutUrl);
		System.out.println("logout navigating to: " + logoutUrl);

		try {
			wait.until(ExpectedConditions.visibilityOf(POMlogin.usernameFaild(driver)));
			System.out.println("logout success  back to login page");
		} catch (Exception e) {
			System.out.println("logout warning  login page didnt appear");
		}
	}
}
