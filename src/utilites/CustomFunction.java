package utilites;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class CustomFunction {

    public static Config loadConfig(String filePath) {
        try {
            // 
//read  config.json as 
            String json = new String(Files.readAllBytes(Paths.get(filePath)));

           
            String clean = json.replace("\n", "")
                               .replace("\r", "")
                               .replace("\t", "")
                               .trim();

            //  config object
            Config cfg = new Config();

            //  Parse baseURL
            String baseURL = extractValue(clean, "\"baseURL\"");
            cfg.setBaseURL(baseURL);

            //  auth values
            String userName = extractValue(clean, "\"userName\"");
            String passWord = extractValue(clean, "\"passWord\"");
            cfg.setAuth(userName, passWord);

            String firstName  = extractValue(clean, "\"firstName\"");
            String middleName = extractValue(clean, "\"middleName\"");
            String lastName   = extractValue(clean, "\"lastName\"");
            cfg.setDefaults(firstName, middleName, lastName);

            
            System.out.println("[CONFIG LOADED]");
            System.out.println(" baseURL    = " + cfg.getBaseURL());
            System.out.println(" userName   = " + cfg.getAuth().getUserName());
            System.out.println(" passWord   = " + cfg.getAuth().getPassWord());
            System.out.println(" firstName  = " + cfg.getDefaults().getFirstName());
            System.out.println(" middleName = " + cfg.getDefaults().getMiddleName());
            System.out.println(" lastName   = " + cfg.getDefaults().getLastName());

            return cfg;

        } catch (IOException e) {
            throw new RuntimeException("Error reading config file: " + filePath, e);
        }
    }

   
    
    private static String extractValue(String cleanJson, String key) {
        
        int keyIndex = cleanJson.indexOf(key);
        if (keyIndex == -1) {
            return "";
        }

        
        int colonIndex = cleanJson.indexOf(":", keyIndex);
        if (colonIndex == -1) {
            return "";
        }

        int firstQuote = cleanJson.indexOf("\"", colonIndex + 1);
        if (firstQuote == -1) {
            return "";
        }

      
        int secondQuote = cleanJson.indexOf("\"", firstQuote + 1);
        if (secondQuote == -1) {
            return "";
        }

        
        return cleanJson.substring(firstQuote + 1, secondQuote);
    }

    public static String generateRandomEmployeeId() {
        Random random = new Random();
        int id = 1000 + random.nextInt(9000); 
        return String.valueOf(id);
    }
}
