package Driver; 

import java.awt.AWTException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.testng.TestNG;
import com.google.common.collect.Lists;

import testbase.BaseTemplate; 

public class RegressionDriver extends BaseTemplate {
	
	public static void main(String[] args) throws AWTException, InterruptedException, IOException {

		System.out.println("args are:" + Arrays.toString(args));
		
		
		BaseTemplate.Setargs(args); 
        
		TestNG testng = new TestNG();
		List<String> suite = Lists.newArrayList();
        
		suite.add(xml); 
		
		testng.setTestSuites(suite);
		
		testng.run(); 
        
        System.out.println("=== RegressionDriver finished ===");

	}
}
/*Regression driver class:
package drivers;

import java.awt.AWTException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.testng.TestNG;

import com.google.common.collect.Lists;

import utilities.BaseTemplate;

public class RegressionDriver extends BaseTemplate {
	public static void main(String[] args) throws AWTException, InterruptedException, IOException {

		System.out.println("args are:" + Arrays.toString(args));
		setArgs(args);
		TestNG testng = new TestNG();
		List<String> suite = Lists.newArrayList();
		suite.add(testngXMLPath);
		testng.setTestSuites(suite);
		testng.run();

	}
}*/
/**/