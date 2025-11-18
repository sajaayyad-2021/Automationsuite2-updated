package utilites;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ResultChecker {

    public String checkTestResult(String className, String testName, String suitePath) {

        try {
            // New per-test diff file path using BaseTemplate helpers
            String diffFilePath = testbase.BaseTemplate.diffPath(className, testName)
                    + "baseline_diff.txt";

            String content = Files.readString(Paths.get(diffFilePath));

            String header = "___" + testName + " DIFF___";

            int start = content.indexOf(header);
            if (start == -1) {
                System.out.println("Header not found for test: " + testName);
                return "FAIL";
            }

            int resultIndex = content.indexOf("Result", start);
            if (resultIndex == -1) {
                System.out.println("Result line not found for test: " + testName);
                return "FAIL";
            }

            String resultLine = content.substring(resultIndex);

            // Treat any line containing "PASS" as PASS
            if (resultLine.contains("PASS") || resultLine.contains("IDENTICAL")) {
                return "PASS";
            }

            return "FAIL";

        } catch (Exception e) {
            System.out.println("Error reading diff file for test: " + testName);
            return "FAIL";
        }
    }
}

