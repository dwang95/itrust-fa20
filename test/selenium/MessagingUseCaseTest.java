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
	
	public void testHCPSendMessage() throws Exception {
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Message Outbox")).click();
		assertLogged(TransactionType.OUTBOX_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Compose a Message")).click();
		driver.findElement(By.name("UID_PATIENTID")).sendKeys("2");
		driver.findElement(By.xpath("//input[@value='2']")).submit();
		driver.findElement(By.name("subject")).clear();
		driver.findElement(By.name("subject")).sendKeys("Visit Request");
		driver.findElement(By.name("messageBody")).clear();
		driver.findElement(By.name("messageBody")).sendKeys("We really need to have a visit.");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		assertLogged(TransactionType.MESSAGE_SEND, 9000000000L, 2L, "");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date();
		String stamp = dateFormat.format(date);
		assertTrue(driver.getPageSource().contains("My Sent Messages"));
		driver.findElement(By.linkText("Message Outbox")).click();
		assertTrue(driver.getPageSource().contains("Visit Request"));
		assertTrue(driver.getPageSource().contains("Andy Programmer"));
		assertTrue(driver.getPageSource().contains(stamp));
		assertLogged(TransactionType.OUTBOX_VIEW, 9000000000L, 0L, "");
		
		driver = login("2", "pw");
		assertLogged(TransactionType.HOME_VIEW, 2L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 2L, 0L, "");
		assertTrue(driver.getPageSource().contains("Kelly Doctor"));
		assertTrue(driver.getPageSource().contains("Visit Request"));
		assertTrue(driver.getPageSource().contains(stamp));
	}
	
	public void testPatientSendReply() throws Exception {
		driver = login("2", "pw");
		assertLogged(TransactionType.HOME_VIEW, 2L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 2L, 0L, "");
		driver.findElement(By.linkText("Read")).click();
		assertLogged(TransactionType.MESSAGE_VIEW, 2L, 9000000000L, "");
		driver.findElement(By.linkText("Reply")).click();
		driver.findElement(By.name("messageBody")).clear();
		driver.findElement(By.name("messageBody")).sendKeys("Which office visit did you update?");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		assertLogged(TransactionType.MESSAGE_SEND, 2L, 9000000000L, "");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date();
		String stamp = dateFormat.format(date);
		driver.findElement(By.linkText("Message Outbox")).click();
		assertTrue(driver.getPageSource().contains("RE: Office Visit Updated"));
		assertTrue(driver.getPageSource().contains("Kelly Doctor"));
		assertTrue(driver.getPageSource().contains(stamp));
		assertLogged(TransactionType.OUTBOX_VIEW, 2L, 0L, "");
		
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 9000000000L, 0L, "");
		assertTrue(driver.getPageSource().contains("Andy Programmer"));
		assertTrue(driver.getPageSource().contains("RE: Office Visit Updated"));
		assertTrue(driver.getPageSource().contains(stamp));
	}
	
	public void testPatientSendMessageMultiRecipients() throws Exception {
		gen.messagingCcs();
		driver = login("1", "pw");
		assertLogged(TransactionType.HOME_VIEW, 1L, 0L, "");
		driver.findElement(By.linkText("Compose a Message")).click();
		final Select selectBox = new Select(driver.findElement(By.name("dlhcp")));
		selectBox.selectByValue("9000000003");
		//selectComboValue("dlhcp", "9000000003", driver);
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		driver.findElement(By.name("cc")).clear();
		driver.findElement(By.name("cc")).sendKeys("9000000000");
		driver.findElement(By.name("subject")).clear();
		driver.findElement(By.name("subject")).sendKeys("This is a message to multiple recipients");
		driver.findElement(By.name("messageBody")).clear();
		driver.findElement(By.name("messageBody")).sendKeys("We really need to have a visit!");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		assertTrue(driver.getPageSource().contains("Gandalf Stormcrow"));
		assertTrue(driver.getPageSource().contains("Kelly Doctor"));
		assertTrue(driver.getPageSource().contains("This is a message to multiple recipients"));
	}
	
	public void testPatientSendReplyMultipleRecipients() throws Exception {
		driver = login("2", "pw");
		assertLogged(TransactionType.HOME_VIEW, 2L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 2L, 0L, "");
		driver.findElement(By.linkText("Read")).click();
		assertLogged(TransactionType.MESSAGE_VIEW, 2L, 9000000000L, "");
		driver.findElement(By.linkText("Reply")).click();
		driver.findElement(By.name("cc")).click();
		driver.findElement(By.name("messageBody")).clear();
		driver.findElement(By.name("messageBody")).sendKeys("Which office visit did you update?");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date();
		String stamp = dateFormat.format(date);
		driver.findElement(By.linkText("Message Outbox")).click();
		assertTrue(driver.getPageSource().contains("RE: Office Visit Updated"));
		assertTrue(driver.getPageSource().contains("Kelly Doctor"));
		assertTrue(driver.getPageSource().contains("Gandalf Stormcrow"));
		assertTrue(driver.getPageSource().contains(stamp));
		assertLogged(TransactionType.OUTBOX_VIEW, 2L, 0L, "");
		
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 9000000000L, 0L, "");
		assertTrue(driver.getPageSource().contains("Andy Programmer"));
		assertTrue(driver.getPageSource().contains("RE: Office Visit Updated"));
		assertTrue(driver.getPageSource().contains(stamp));
		
		driver = login("9000000003", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000003L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 9000000003L, 0L, "");
		assertTrue(driver.getPageSource().contains("Andy Programmer"));
		assertTrue(driver.getPageSource().contains("RE: Office Visit Updated"));
		assertTrue(driver.getPageSource().contains(stamp));
	}
	
	public void testHCPSendReplySingleCCRecipient() throws Exception {
		gen.clearMessages();
		gen.messages6();
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Read")).click();
		assertLogged(TransactionType.MESSAGE_VIEW, 9000000000L, 22L, "Viewed Message: 3");
		driver.findElement(By.linkText("Reply")).click();
		driver.findElement(By.name("cc")).click();
		driver.findElement(By.name("messageBody")).clear();
		driver.findElement(By.name("messageBody")).sendKeys("I will not be able to make my next schedulded appointment.  Is there anyone who can book another time?");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		assertLogged(TransactionType.MESSAGE_SEND, 9000000000L, 22L, "9000000007");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date();
		String stamp = dateFormat.format(date);
		driver.findElement(By.linkText("Message Outbox")).click();
		assertTrue(driver.getPageSource().contains("RE: Appointment rescheduling"));
		assertTrue(driver.getPageSource().contains("Fozzie Bear"));
		assertTrue(driver.getPageSource().contains("Beaker Beaker"));
		assertTrue(driver.getPageSource().contains(stamp));
		assertLogged(TransactionType.OUTBOX_VIEW, 9000000000L, 0L, "");
		
		driver = login("22", "pw");
		assertLogged(TransactionType.HOME_VIEW, 22L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 22L, 0L, "");
		assertTrue(driver.getPageSource().contains("Kelly Doctor"));
		assertTrue(driver.getPageSource().contains("RE: Appointment rescheduling"));
		assertTrue(driver.getPageSource().contains(stamp));
		
		driver = login("9000000007", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000007L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 9000000007L, 0L, "");
		assertTrue(driver.getPageSource().contains("Kelly Doctor"));
		assertTrue(driver.getPageSource().contains("RE: Appointment rescheduling"));
		assertTrue(driver.getPageSource().contains(stamp));
	}

	public void testSortPatientOutboxBynameAsce() throws Exception {
		driver = login("1", "pw");
		assertLogged(TransactionType.HOME_VIEW, 1L, 0L, "");
		driver.findElement(By.linkText("Message Outbox")).click();
		Select select1 = new Select(driver.findElement(By.name("sortby")));
		select1.selectByValue("name");
		Select select2 = new Select(driver.findElement(By.name("sorthow")));
		select2.selectByValue("asce");
		driver.findElement(By.cssSelector("input[value=\"Sort\"]")).click();
		WebElement table = driver.findElement(By.className("fancyTable"));
		WebElement tableBody = table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
		rows.remove(0);
		for (int i = 1; i<rows.size();i++) {
			List<WebElement> td = rows.get(i).findElements(By.tagName("td"));
			List<WebElement> tdprev = rows.get(i-1).findElements(By.tagName("td"));
			String name1 = td.get(0).getText().split(" ")[1];
			String name2 = tdprev.get(0).getText().split(" ")[1];
			int result = name1.compareTo(name2);
			assertTrue(result>=0);

		}
	}

	public void testSortPatientOutboxBynameDesc() throws Exception {
		driver = login("1", "pw");
		assertLogged(TransactionType.HOME_VIEW, 1L, 0L, "");
		driver.findElement(By.linkText("Message Outbox")).click();
		Select select1 = new Select(driver.findElement(By.name("sortby")));
		select1.selectByValue("name");
		Select select2 = new Select(driver.findElement(By.name("sorthow")));
		select2.selectByValue("desc");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		WebElement table = driver.findElement(By.className("fancyTable"));
		WebElement tableBody = table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
		rows.remove(0);
		for (int i = 1; i<rows.size();i++) {
			List<WebElement> td = rows.get(i).findElements(By.tagName("td"));
			List<WebElement> tdprev = rows.get(i-1).findElements(By.tagName("td"));
			String[] name1 = td.get(0).getText().split(" ");
			String[] name2 = tdprev.get(0).getText().split(" ");
			assertTrue(name1[1].compareTo(name2[1])<=0);
		}
	}

	public void testSortPatientOutboxBytimeAsce() throws Exception {
		driver = login("1", "pw");
		assertLogged(TransactionType.HOME_VIEW, 1L, 0L, "");
		driver.findElement(By.linkText("Message Outbox")).click();
		Select select1 = new Select(driver.findElement(By.name("sortby")));
		select1.selectByValue("time");
		Select select2 = new Select(driver.findElement(By.name("sorthow")));
		select2.selectByValue("asce");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		WebElement table = driver.findElement(By.className("fancyTable"));
		WebElement tableBody = table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
		rows.remove(0);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		for (int i = 1; i<rows.size();i++) {
			List<WebElement> td = rows.get(i).findElements(By.tagName("td"));
			List<WebElement> tdprev = rows.get(i-1).findElements(By.tagName("td"));
			Date date1 = sdf.parse(td.get(2).getText());
			Date date2 = sdf.parse(tdprev.get(2).getText());
			//System.out.println("Date1: " + date1);
			//System.out.println("Date2: " + date2);    
			assertEquals(true, date1.compareTo(date2)>=0);
		}
	}

	public void testSortPatientOutboxBytimeDesc() throws Exception {
		driver = login("1", "pw");
		assertLogged(TransactionType.HOME_VIEW, 1L, 0L, "");
		driver.findElement(By.linkText("Message Outbox")).click();
		Select select1 = new Select(driver.findElement(By.name("sortby")));
		select1.selectByValue("time");
		Select select2 = new Select(driver.findElement(By.name("sorthow")));
		select2.selectByValue("desc");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		WebElement table = driver.findElement(By.className("fancyTable"));
		WebElement tableBody = table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
		rows.remove(0);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		for (int i = 1; i<rows.size();i++) {
			List<WebElement> td = rows.get(i).findElements(By.tagName("td"));
			List<WebElement> tdprev = rows.get(i-1).findElements(By.tagName("td"));
			String date1 = td.get(2).getText();
			String date2 = tdprev.get(2).getText();
			assertEquals(true, sdf.parse(date1).compareTo(sdf.parse(date2))<=0);
		}
	}

	public void testFilterPatientInboxNameMissing() throws Exception{
		driver = login("1", "pw");
		assertLogged(TransactionType.HOME_VIEW, 1L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		driver.findElement(By.linkText("Edit Filter")).click();
		driver.findElement(By.name("sender")).clear();
		driver.findElement(By.name("subject")).clear();
		driver.findElement(By.name("hasWords")).clear();
		driver.findElement(By.name("notWords")).clear();
		driver.findElement(By.name("startDate")).clear();
		driver.findElement(By.name("endDate")).clear();
		driver.findElement(By.name("sender")).sendKeys("Andy Programmer");
		driver.findElement(By.name("test")).click();
		assertTrue(driver.getPageSource().contains("You have no messages"));
	}

	public void testFilterPatientInboxName() throws Exception{
		driver = login("1", "pw");
		driver.findElement(By.linkText("Message Inbox")).click();
		driver.findElement(By.linkText("Edit Filter")).click();
		driver.findElement(By.name("sender")).clear();
		driver.findElement(By.name("subject")).clear();
		driver.findElement(By.name("hasWords")).clear();
		driver.findElement(By.name("notWords")).clear();
		driver.findElement(By.name("startDate")).clear();
		driver.findElement(By.name("endDate")).clear();
		driver.findElement(By.name("sender")).sendKeys("Kelly Doctor");
		driver.findElement(By.name("test")).click();
		WebElement table = driver.findElement(By.className("fancyTable"));
		WebElement tableBody = table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
		rows.remove(0);
		for (WebElement row : rows){
			List<WebElement> td = row.findElements(By.tagName("td"));
			assertEquals("Kelly Doctor",td.get(0).getText());
		}
	}

	public void testFilterHCPInboxName() throws Exception{
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Edit Filter")).click();
		driver.findElement(By.name("sender")).clear();
		driver.findElement(By.name("subject")).clear();
		driver.findElement(By.name("hasWords")).clear();
		driver.findElement(By.name("notWords")).clear();
		driver.findElement(By.name("startDate")).clear();
		driver.findElement(By.name("endDate")).clear();
		driver.findElement(By.name("sender")).sendKeys("Andy Programmer");
		driver.findElement(By.name("test")).click();
		WebElement table = driver.findElement(By.className("fancyTable"));
		WebElement tableBody = table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
		rows.remove(0);
		for (WebElement row : rows){
			List<WebElement> td = row.findElements(By.tagName("td"));
			assertEquals("Andy Programmer",td.get(0).getText());
		}
	}

	public void testFilterHCPInboxSubject() throws Exception{
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Edit Filter")).click();
		driver.findElement(By.name("sender")).clear();
		driver.findElement(By.name("subject")).clear();
		driver.findElement(By.name("hasWords")).clear();
		driver.findElement(By.name("notWords")).clear();
		driver.findElement(By.name("startDate")).clear();
		driver.findElement(By.name("endDate")).clear();
		driver.findElement(By.name("subject")).sendKeys("Appointment");
		driver.findElement(By.name("test")).click();
		WebElement table = driver.findElement(By.className("fancyTable"));
		WebElement tableBody = table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
		rows.remove(0);
		for (WebElement row : rows){
			List<WebElement> td = row.findElements(By.tagName("td"));
			assertEquals("Appointment",td.get(1).getText());
		}
	}

	public void testFilterHCPInboxHasWords() throws Exception{
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Edit Filter")).click();
		driver.findElement(By.name("sender")).clear();
		driver.findElement(By.name("subject")).clear();
		driver.findElement(By.name("hasWords")).clear();
		driver.findElement(By.name("notWords")).clear();
		driver.findElement(By.name("startDate")).clear();
		driver.findElement(By.name("endDate")).clear();
		driver.findElement(By.name("hasWords")).sendKeys("My bologna has a first name");
		driver.findElement(By.name("test")).click();
		assertTrue(driver.getPageSource().contains("You have no messages"));
		
	}

	public void testFilterHCPInboxHasWordsBetter() throws Exception{
		gen.clearMessages();
        gen.messages6();
        driver = login("9000000000", "pw");
        driver.findElement(By.linkText("Message Inbox")).click();
        driver.findElement(By.linkText("Edit Filter")).click();
        driver.findElement(By.name("sender")).clear();
        driver.findElement(By.name("subject")).clear();
        driver.findElement(By.name("hasWords")).clear();
        driver.findElement(By.name("notWords")).clear();
        driver.findElement(By.name("startDate")).clear();
        driver.findElement(By.name("endDate")).clear();
        driver.findElement(By.name("notWords")).sendKeys("visit");
        driver.findElement(By.name("test")).click();
        driver.findElement(By.linkText("Read")).click();
        assertFalse(driver.findElement(By.id("iTrustContent")).getText().contains("visit"));
	}

	public void testFilterHCPInboxNotHasWords() throws Exception{
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Edit Filter")).click();
		driver.findElement(By.name("sender")).clear();
		driver.findElement(By.name("subject")).clear();
		driver.findElement(By.name("hasWords")).clear();
		driver.findElement(By.name("notWords")).clear();
		driver.findElement(By.name("startDate")).clear();
		driver.findElement(By.name("endDate")).clear();
		driver.findElement(By.name("notWords")).sendKeys("appointment");
		driver.findElement(By.name("test")).click();
		WebElement table = driver.findElement(By.className("fancyTable"));
		WebElement tableBody = table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
		rows.remove(0);
		assertEquals(9, rows.size());
	}

	public void testFilterHCPInboxDate() throws Exception{
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Edit Filter")).click();
		driver.findElement(By.name("sender")).clear();
		driver.findElement(By.name("subject")).clear();
		driver.findElement(By.name("hasWords")).clear();
		driver.findElement(By.name("notWords")).clear();
		driver.findElement(By.name("startDate")).clear();
		driver.findElement(By.name("endDate")).clear();
		driver.findElement(By.name("startDate")).sendKeys("01/01/2010");
		driver.findElement(By.name("endDate")).sendKeys("01/31/2010");
		driver.findElement(By.name("test")).click();
		WebElement table = driver.findElement(By.className("fancyTable"));
		WebElement tableBody = table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
		Date date1 = sdf.parse("01/01/2010");
		Date date2 = sdf.parse("01/31/2010");
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		rows.remove(0);
		for (WebElement row : rows){
			List<WebElement> td = row.findElements(By.tagName("td"));
			Date date = sdf2.parse(td.get(2).getText());
			assertTrue(date.after(date1) && date.before(date2));
		}
	}

	public void testFilterHCPInboxSaveFilter() throws Exception{
		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		assertLogged(TransactionType.INBOX_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Edit Filter")).click();
		driver.findElement(By.name("sender")).clear();
		driver.findElement(By.name("subject")).clear();
		driver.findElement(By.name("hasWords")).clear();
		driver.findElement(By.name("notWords")).clear();
		driver.findElement(By.name("startDate")).clear();
		driver.findElement(By.name("endDate")).clear();
		driver.findElement(By.name("sender")).sendKeys("Andy Programmer");
		driver.findElement(By.name("subject")).sendKeys("Prescription");
		driver.findElement(By.name("save")).click();
		WebElement table = driver.findElement(By.className("fancyTable"));
		WebElement tableBody = table.findElement(By.tagName("tbody"));
		List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
		rows.remove(0);
		List<WebElement> td = rows.get(0).findElements(By.tagName("td"));
		assertEquals("Andy Programmer",td.get(0).getText());
		assertEquals("Prescription",td.get(1).getText());

		driver = login("9000000000", "pw");
		assertLogged(TransactionType.HOME_VIEW, 9000000000L, 0L, "");
		driver.findElement(By.linkText("Message Inbox")).click();
		driver.findElement(By.linkText("Apply Filter")).click();
		WebElement table1 = driver.findElement(By.className("fancyTable"));
		WebElement tableBody1 = table1.findElement(By.tagName("tbody"));
		List<WebElement> rows1 = tableBody1.findElements(By.tagName("tr"));
		rows1.remove(0);
		for (WebElement row : rows1){
			List<WebElement> td1 = row.findElements(By.tagName("td"));
			assertEquals("Andy Programmer",td1.get(0).getText());
			assertEquals("Prescription",td1.get(1).getText());
		}
		driver.findElement(By.linkText("Edit Filter")).click();
		driver.findElement(By.name("sender")).clear();
		driver.findElement(By.name("subject")).clear();
		driver.findElement(By.name("hasWords")).clear();
		driver.findElement(By.name("notWords")).clear();
		driver.findElement(By.name("startDate")).clear();
		driver.findElement(By.name("endDate")).clear();
		driver.findElement(By.name("save")).click();
		WebElement table2 = driver.findElement(By.className("fancyTable"));
		WebElement tableBody2 = table2.findElement(By.tagName("tbody"));
		List<WebElement> rows2 = tableBody2.findElements(By.tagName("tr"));
		rows2.remove(0);
		assertTrue(rows2.size()>1);
	}
}