package Test;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;

import reporting.ReportLogger;
import utilites.Config;
import testbase.BaseTemplate;
import utilites.CustomFunction;
import utilites.MainFunctions;
import utilites.ResultChecker;

@Listeners(reporting.ExtentTestNGITestListener.class)
public class PIMTests extends BaseTemplate {

    MainFunctions mf;
    ResultChecker resultCheck = new ResultChecker();

    String[] testsList = null;
    String activeTest = null;

    private ExtentTest currentTest;

    @Test
    public void RunTests() throws Exception {

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String className = this.getClass().getSimpleName();

        System.out.println("[" + timestamp + "] Current class name: " + className);
        System.out.println("Starting PIM Tests...");

        if (testNmaes_pim == null || testNmaes_pim.trim().isEmpty()) {
            testNmaes_pim = "ALL";
        } else {
            testNmaes_pim = testNmaes_pim.trim();
        }

        if (!"ALL".equalsIgnoreCase(testNmaes_pim)) {
            if (testNmaes_pim.contains(",")) {
                testsList = Arrays.stream(testNmaes_pim.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);
            } else {
                testsList = new String[]{ testNmaes_pim };
            }
        } else {
            Method[] methods = this.getClass().getDeclaredMethods();
            List<String> tmp = new ArrayList<>();
            for (Method m : methods) {
                if (m.getName().startsWith("TC_PIM_")) {
                    tmp.add(m.getName());
                }
            }
            testsList = tmp.toArray(new String[0]);
        }

        Config loginCfg = loadthisTestConfig("LoginTests", "TC_LOG_001_validLogin");
        mf = new MainFunctions(driver, loginCfg);

        mf.performLogin(loginCfg);

        for (String tc : testsList) {

            activeTest = tc;
            addCurrentTestMthod(activeTest);

            currentTest = extent.createTest(activeTest);
            currentTest.assignCategory("PIM");
            currentTest.info("Starting test: " + activeTest);

            MainFunctions.deleteFiles(actualPath(className, activeTest));
            MainFunctions.deleteFiles(diffPath(className, activeTest));

            try {
                Config cfg = loadthisTestConfig(className, activeTest);
                mf = new MainFunctions(driver, cfg);

                invokeTestMethod(activeTest, cfg, className);

                currentTest.pass("Test finished successfully");

            } catch (NoSuchMethodException e) {
                currentTest.skip("Test method NOT FOUND: " + activeTest);

            } catch (Throwable e) {
                currentTest.fail("Exception occurred: " + e.getMessage());
                currentTest.fail(e);
            }
        }

        extent.flush();
    }

    private void invokeTestMethod(String methodName, Config cfg, String className) throws Exception {
        Method m = this.getClass().getDeclaredMethod(methodName, Config.class, String.class);
        m.setAccessible(true);
        m.invoke(this, cfg, className);
    }

    private void saveDataArtifacts(String className, String testName,
                                   String actualData, String expectedData, boolean res) {

        try {
            String baseName = "baseline.txt";

            String actualFile = actualPath(className, testName) + baseName;
            String expectedFile = expectedPath(className, testName) + baseName;
            String diffFile = diffPath(className, testName) + "baseline_diff.txt";

            CustomFunction.writeTextFile(actualFile, actualData);

            File expected = new File(expectedFile);

            if (!expected.exists() || expected.length() == 0) {
                String baseline = (expectedData != null && !expectedData.isEmpty())
                        ? expectedData
                        : actualData;

                CustomFunction.writeTextFile(expectedFile, baseline);

                CustomFunction.appendToFile("Baseline created for test: " + testName, diffFile);
                CustomFunction.appendToFile("Expected (baseline): " + baseline, diffFile);
                CustomFunction.appendToFile("Actual: " + actualData, diffFile);

                return;
            }

            String baseline = Files.readString(Paths.get(expectedFile));
            boolean match = baseline.trim().equals(actualData.trim());

            CustomFunction.appendToFile("Expected: " + baseline, diffFile);
            CustomFunction.appendToFile("Actual  : " + actualData, diffFile);
            CustomFunction.appendToFile("Result  : " + (match ? "PASS" : "FAIL"), diffFile);

        } catch (Exception ex) {
            currentTest.fail("Artifact save failed: " + ex.getMessage());
        }
    }

    // ------------------ PIM TEST CASES  ------------------

    private void TC_PIM_001_openList(Config cfg, String className) {

        boolean ok = mf.openPimList();

        ReportLogger.fullBlock(
                currentTest,
                "Open PIM → Employee List",
                "Employee list page should load",
                "List loaded = " + ok,
                ok
        );

        saveDataArtifacts(className, "TC_PIM_001_openList",
                "PIM_LIST_OPEN=" + ok,
                "PIM_LIST_OPEN=true",
                ok);
    }
    private void TC_PIM_002_openAddEmployeeForm(Config cfg, String className) {

        boolean ok = mf.openAddEmployeeForm();

        ReportLogger.fullBlock(
                currentTest,
                "Open 'Add Employee' Form",
                "Form should appear and display input fields",
                "Form opened = " + ok,
                ok
        );

        saveDataArtifacts(className, "TC_PIM_002_openAddEmployeeForm",
                "ADD_EMPLOYEE_FORM=" + ok,
                "ADD_EMPLOYEE_FORM=true",
                ok);
    }
    
    private void TC_PIM_003_addEmployee(Config cfg, String className) {

        String empId = mf.performCreateEmployee(cfg);
        boolean ok = empId != null && !empId.isBlank();

        ReportLogger.fullBlock(
                currentTest,
                "Create Employee",
                "Employee should be added successfully",
                "Generated Employee ID = " + empId,
                ok
        );

        saveDataArtifacts(className, "TC_PIM_003_addEmployee",
                "EMP_ID=" + empId,
                "EMP_CREATED=true",
                ok
        );
    }
    private void TC_PIM_004_MissingFields(Config cfg, String className) {

        boolean ok = mf.validateAddEmployeeMissingFields();

        ReportLogger.fullBlock(
                currentTest,
                "Add Employee With Missing Required Fields",
                "System must block saving and show 'Required' validation",
                "ValidationShown = " + ok,
                ok
        );

        saveDataArtifacts(
                className,
                "TC_PIM_004_MissingFields",
                "REQUIRED_VALIDATION=" + ok,
                "REQUIRED_VALIDATION=true",
                ok
        );
    }
    
    private void TC_PIM_005_SearchEmployee_ByName(Config cfg, String className) {

        String empId = mf.performCreateEmployee(cfg);
        String fullName = cfg.getDefaults().getFirstName() + " " +
                          cfg.getDefaults().getMiddleName() + " " +
                          cfg.getDefaults().getLastName();

        fullName = fullName.replaceAll(" +", " ").trim();

        // 2) Search
        boolean ok = mf.searchEmployeeByName(fullName);

        ReportLogger.fullBlock(
                currentTest,
                "Search Employee By Name",
                "Employee should appear in results",
                "Name=" + fullName + " | Found=" + ok,
                ok
        );

        saveDataArtifacts(
                className,
                "TC_PIM_005_SearchEmployee_ByName",
                "FOUND=" + ok,
                "FOUND_BY_NAME=true",
                ok
        );
    }
    
    private void TC_PIM_006_SearchEmployee_ByID(Config cfg, String className) {

        String empId = mf.performCreateEmployee(cfg);
        boolean ok = false;

        if (empId != null && !empId.isBlank()) {
            ok = mf.searchEmployeeById(empId);
        }

        ReportLogger.fullBlock(
                currentTest,
                "Search Employee By ID",
                "System should locate the employee using Employee ID",
                "ID = " + empId + " | Found = " + ok,
                ok
        );

        saveDataArtifacts(
                className,
                "TC_PIM_006_SearchEmployee_ByID",
                "FOUND=" + ok,
                "FOUND_BY_ID=true",
                ok
        );
    }
    
    private void TC_PIM_007_EditEmployeeDetails(Config cfg, String className) {

        mf.performCreateEmployee(cfg);

        String newMiddle = "M" + (System.currentTimeMillis() % 1000);

        boolean ok = mf.editEmployeeMiddleName(newMiddle);

        ReportLogger.fullBlock(
                currentTest,
                "Edit Employee Middle Name",
                "Middle name should be updated successfully",
                "NewMiddle = " + newMiddle + " | Updated = " + ok,
                ok
        );

        saveDataArtifacts(
                className,
                "TC_PIM_007_EditEmployeeDetails",
                "UPDATED=" + ok,
                "MIDDLE_UPDATED=true",
                ok
        );
    }

 





}
