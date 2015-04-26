package hello;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Automation {
	public void start() {
//		File pathToBinary = new File("D:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
//		FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
//		FirefoxProfile firefoxProfile = new FirefoxProfile();
//		FirefoxDriver driver = new FirefoxDriver(ffBinary,firefoxProfile);
		
		Database db = new Database();
		
		DBCursor events = db.GetEvents();
		
		Object runId = db.createRun(System.currentTimeMillis() / 1000L);
		
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("general.useragent.override", "TestBot");
		
        WebDriver driver = new FirefoxDriver(profile);
        driver.get("http://localhost:8081");
        
        ((JavascriptExecutor)driver).executeScript("window.focus();");
        
        try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        for (DBObject event : events) {
        	BasicDBList userEvents = (BasicDBList)event.get("events");
        	for (int i = 0; i < userEvents.size(); i++) {
        		DBObject userEvent = (DBObject) userEvents.get(i);
        		
        		String targetSelector = (String)userEvent.get("targetSelector");
        		String eventType = (String)userEvent.get("type");
        		
        		WebElement element = null;
        		try {
        			element =(new WebDriverWait(driver, 2))
        					.until(ExpectedConditions.presenceOfElementLocated((By.cssSelector(targetSelector))));
        		}
        		catch(TimeoutException e){
        			int lastDelimeterIndex = targetSelector.lastIndexOf(">");
        			String parentSelector = targetSelector.substring(0, lastDelimeterIndex);
        			WebElement parentElement = driver.findElement(By.cssSelector(parentSelector));
        			String expectedHtml = (String)userEvent.get("surroundingHtml");
        			String html = parentElement.getAttribute("outerHTML");
        			db.saveErrorReport(runId, targetSelector, expectedHtml, html, eventType, (long)userEvent.get("timeStamp"));
        		}
        		
        		if(element == null) continue;        		
        		
        		String tagName =  element.getTagName();
        		String type = element.getAttribute("type");
        		
        		try {
	        		if ("enter".equals(eventType)) {
	        			if ("input".equals(tagName) && "text".equals(type)) {
	            			element.click();
	            	        element.sendKeys("Test data");
	            	        element.sendKeys(Keys.ENTER);
	    				}
	        		}
	        		
	        		if ("click".equals(eventType)) {
	        			element.click();
	        		}
        		}
        		catch(ElementNotVisibleException e) {
        			
        		}
        	}
        	
        	BasicDBList mutations = (BasicDBList)event.get("mutations");
        	for (int i = 0; i < mutations.size(); i++) {
        		DBObject mutation = (DBObject) mutations.get(i);
        		
        		String type = (String)mutation.get("type");
        		if(!"added".equals(type)) {
        			continue;
        		}
        		
        		String childSelector = (String)mutation.get("childSelector");
        		
        		WebElement child;
        		try {
        			child = (new WebDriverWait(driver, 2))
        					.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(childSelector)));
        		}
        		catch(TimeoutException e){
        			String targetSelector = (String)mutation.get("targetSelector");
        			WebElement targetElement = driver.findElement(By.cssSelector(targetSelector));
        			String expectedHtml = (String)mutation.get("surroundingHtml");
        			String html = targetElement.getAttribute("outerHTML");
        			db.saveErrorReport(runId, childSelector, expectedHtml, html, type, (long)mutation.get("timeStamp"));
        		}
        	}
		}
        
        //Close the browser
        driver.quit();
	}
}
