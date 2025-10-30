package utilites;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class CustomFunction {

    public static Config loadConfig(String filePath) {
        try {
            String json = new String(Files.readAllBytes(Paths.get(filePath)));
            String clean = json.replace("\n", "").replace("\r", "").replace("\t", "").trim();

            Config cfg = new Config();

            String baseURL   = extractValue(clean, "\"baseURL\"");
            String userName  = extractValue(clean, "\"userName\"");
            String passWord  = extractValue(clean, "\"passWord\"");
            String firstName = extractValue(clean, "\"firstName\"");
            String middle    = extractValue(clean, "\"middleName\"");
            String lastName  = extractValue(clean, "\"lastName\"");

            String fromDate   = extractValue(clean, "\"fromDate\"");
            String toDate     = extractValue(clean, "\"toDate\"");
            String empName    = extractValue(clean, "\"employeeName\"");
            String status     = extractValue(clean, "\"status\"");
            String leaveType  = extractValue(clean, "\"leaveType\"");
            String subUnit    = extractValue(clean, "\"subUnit\"");

            cfg.setBaseURL(baseURL);
            cfg.setAuth(userName, passWord);
            cfg.setDefaults(firstName, middle, lastName);
            cfg.setLeaveSearch(fromDate, toDate, empName, status, leaveType, subUnit);

            System.out.println("[CONFIG LOADED]");
            System.out.println(" baseURL        = " + cfg.getBaseURL());
            System.out.println(" userName       = " + cfg.getAuth().getUserName());
            System.out.println(" passWord       = " + cfg.getAuth().getPassWord());
            System.out.println(" firstName      = " + cfg.getDefaults().getFirstName());
            System.out.println(" middleName     = " + cfg.getDefaults().getMiddleName());
            System.out.println(" lastName       = " + cfg.getDefaults().getLastName());
            System.out.println(" leave.from     = " + cfg.getLeaveSearch().getFromDate());
            System.out.println(" leave.to       = " + cfg.getLeaveSearch().getToDate());
            System.out.println(" leave.empName  = " + cfg.getLeaveSearch().getEmployeeName());
            System.out.println(" leave.status   = " + cfg.getLeaveSearch().getStatus());
            System.out.println(" leave.type     = " + cfg.getLeaveSearch().getLeaveType());
            System.out.println(" leave.subUnit  = " + cfg.getLeaveSearch().getSubUnit());

            return cfg;

        } catch (IOException e) {
            throw new RuntimeException("Error reading config file: " + filePath, e);
        }
    }

	private static String extractValue(String cleanJson, String key) {
		int keyIndex = cleanJson.indexOf(key);
		if (keyIndex == -1)
			return "";
		int colonIndex = cleanJson.indexOf(":", keyIndex);
		if (colonIndex == -1)
			return "";
		int firstQuote = cleanJson.indexOf("\"", colonIndex + 1);
		if (firstQuote == -1)
			return "";
		int secondQuote = cleanJson.indexOf("\"", firstQuote + 1);
		if (secondQuote == -1)
			return "";
		return cleanJson.substring(firstQuote + 1, secondQuote);
	}

    public static String generateRandomEmployeeId() {
     //4 digit
        int id = new Random().nextInt(9000) + 1000;
        return String.valueOf(id);
    }
}
