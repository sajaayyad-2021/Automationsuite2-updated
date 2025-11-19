# 🚀 Selenium Test Automation Framework  
### Java | TestNG | POM | Baseline Validation | Extent Reports

This repository contains a fully-structured, scalable, and maintainable **Test Automation Framework** built using **Selenium WebDriver**, **TestNG**, **POM architecture**, and **Extent Reports**.  
The framework is designed to support **dynamic test discovery**, **config-driven execution**, and **baseline-comparison validation** for automated end-to-end testing.

---

##  Project Structure
src/
├── controller/ → Reusable action flows (Login, PIM, Leave, Recruitment…)
├── Driver/
│ └── RegressionDriver.java → Entry point for running the suite with CLI arguments
├── POM/ → Page Objects for all application modules
├── reporting/ → Extent Report manager + TestNG listener
├── Test/
│ └── LoginTests.java → Test execution controller 
├── testbase/
│ └── BaseTemplate.java → WebDriver, paths, CLI args, config loading
└── utilities/
├── Config.java
├── CustomFunction.java
├── MainFunctions.java → High-level reusable business flows
└── ResultChecker.java
artifacts/
├── TestCases/
│ └── LoginTests/
│ ├── TC_LOG_001_validLogin/
│ │ ├── Input/input.json
│ │ ├── Actual/
│ │ ├── Expected/
│ │ └── Diff/
│ └── ... (other test cases)
├── XML/

