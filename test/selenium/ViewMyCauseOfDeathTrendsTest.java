package edu.ncsu.csc.itrust.selenium;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Iterator;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.meterware.httpunit.HttpUnitOptions;

import edu.ncsu.csc.itrust.enums.TransactionType;

public class MessagingUseCaseTest extends iTrustSeleniumTest {

	/*
	 * The URL for iTrust, change as needed
	 */
	/**ADDRESS*/
	public static final String ADDRESS = "http://localhost:8080/iTrust/";
	private WebDriver driver;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		gen.clearAllTables();
		gen.standardData();
		HttpUnitOptions.setScriptingEnabled(false);
		// turn off htmlunit warnings
	    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
	}
	
	public void testIncorrectYearNeginteger() throws Exception {
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Cause of Death Reports")).click();
		driver.findElement(By.name("startYear")).clear();
		driver.findElement(By.name("startYear")).sendKeys("-100");
		driver.findElement(By.name("endYear")).clear();
		driver.findElement(By.name("endYear")).sendKeys("2030");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		assertTrue(driver.getPageSource().contains("Error: Please Enter Valid Dates"));
    }
    
    public void testIncorrectYearNonint() throws Exception {
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Cause of Death Reports")).click();
		driver.findElement(By.name("startYear")).clear();
		driver.findElement(By.name("startYear")).sendKeys("blah");
		driver.findElement(By.name("endYear")).clear();
		driver.findElement(By.name("endYear")).sendKeys("2030");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		assertTrue(driver.getPageSource().contains("Error: Please Enter Valid Dates"));
    }
    
    public void testIncorrectYearNonsequential() throws Exception {
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Cause of Death Reports")).click();
		driver.findElement(By.name("startYear")).clear();
		driver.findElement(By.name("startYear")).sendKeys("2030");
		driver.findElement(By.name("endYear")).clear();
		driver.findElement(By.name("endYear")).sendKeys("1970");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		assertTrue(driver.getPageSource().contains("Error: Please Enter Valid Dates"));
    }

    public void testValidYearNoDeaths() throws Exception {
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Cause of Death Reports")).click();
		driver.findElement(By.name("startYear")).clear();
		driver.findElement(By.name("startYear")).sendKeys("2030");
		driver.findElement(By.name("endYear")).clear();
		driver.findElement(By.name("endYear")).sendKeys("2031");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		assertTrue(driver.getPageSource().contains("No Deaths to Display"));
    }
    
    public void testValidYearsOldAll() throws Exception {
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Cause of Death Reports")).click();
		driver.findElement(By.name("startYear")).clear();
		driver.findElement(By.name("startYear")).sendKeys("1970");
		driver.findElement(By.name("endYear")).clear();
        driver.findElement(By.name("endYear")).sendKeys("2030");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
        assertTrue(driver.getPageSource().contains("Report Generated for All Patients from 1970 to 2030"));
        assertTrue(driver.getPageSource().contains("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 4"));
        assertTrue(driver.getPageSource().contains("Description of Death: Coxsackie | Diagnosis Code: 79.30 | Count of Deaths: 1"));
        assertTrue(driver.getPageSource().contains("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 5"));
        assertTrue(driver.getPageSource().contains("Description of Death: Echovirus | Diagnosis Code: 79.10 | Count of Deaths: 2"));
    }
    
    public void testValidYearsNewAll() throws Exception {
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Cause of Death Reports")).click();
		driver.findElement(By.name("startYear")).clear();
		driver.findElement(By.name("startYear")).sendKeys("2020");
		driver.findElement(By.name("endYear")).clear();
        driver.findElement(By.name("endYear")).sendKeys("2030");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
        assertTrue(driver.getPageSource().contains("Report Generated for All Patients from 1970 to 2030"));
        assertTrue(driver.getPageSource().contains("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 3"));
        assertTrue(driver.getPageSource().contains("Description of Death: Coxsackie | Diagnosis Code: 79.30 | Count of Deaths: 1"));
        assertTrue(driver.getPageSource().contains("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 4"));
        assertTrue(driver.getPageSource().contains("Description of Death: Echovirus | Diagnosis Code: 79.10 | Count of Deaths: 2"));
    }
    
    public void testValidYearsMale() throws Exception {
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Cause of Death Reports")).click();
		driver.findElement(By.name("startYear")).clear();
		driver.findElement(By.name("startYear")).sendKeys("1970");
		driver.findElement(By.name("endYear")).clear();
        driver.findElement(By.name("endYear")).sendKeys("2030");
        Select select1 = new Select(driver.findElement(By.name("population")));
		select1.selectByValue("Male");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
        assertTrue(driver.getPageSource().contains("Report Generated for Male Patients from 1970 to 2030"));
        assertTrue(driver.getPageSource().contains("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 4"));
        assertTrue(driver.getPageSource().contains("Description of Death: Coxsackie | Diagnosis Code: 79.30 | Count of Deaths: 1"));
        assertTrue(driver.getPageSource().contains("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 4"));
        assertTrue(driver.getPageSource().contains("Description of Death: Tuberculosis of vertebral column | Diagnosis Code: 15.00 | Count of Deaths: 1"));
    }

    public void testValidYearsFemale() throws Exception {
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Cause of Death Reports")).click();
		driver.findElement(By.name("startYear")).clear();
		driver.findElement(By.name("startYear")).sendKeys("1970");
		driver.findElement(By.name("endYear")).clear();
        driver.findElement(By.name("endYear")).sendKeys("2030");
        Select select1 = new Select(driver.findElement(By.name("population")));
		select1.selectByValue("Female");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
        assertTrue(driver.getPageSource().contains("Report Generated for Female Patients from 1970 to 2030"));
        assertTrue(driver.getPageSource().contains("Description of Death: Coxsackie | Diagnosis Code: 79.30 | Count of Deaths: 1"));
        assertTrue(driver.getPageSource().contains("No Further Deaths"));
        assertTrue(driver.getPageSource().contains("Description of Death: Coxsackie | Diagnosis Code: 79.30 | Count of Deaths: 1"));
        assertTrue(driver.getPageSource().contains("Description of Death: Diabetes with ketoacidosis | Diagnosis Code: 250.10 | Count of Deaths: 1"));
    }
	
	
}