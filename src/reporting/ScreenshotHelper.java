package reporting;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import testbase.BaseTemplate;

import java.io.File;
import java.nio.file.Files;

public class ScreenshotHelper {

	public static String takeScreenshot(String className, String testName) throws Exception {

	    WebDriver driver = BaseTemplate.driver;

	    String folder = BaseTemplate.actualPath(className, testName);
	    new File(folder).mkdirs();

	    String path = folder + "screenshot_" + System.currentTimeMillis() + ".png";

	    File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	    Files.copy(src.toPath(), new File(path).toPath());

	    return path;
	}

}
