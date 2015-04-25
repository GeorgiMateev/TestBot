package hello;

import java.io.File;
import java.util.Iterator;

import org.openqa.selenium.By;
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
		
        WebDriver driver = new FirefoxDriver();
        driver.get("http://localhost:8081");
        
        for (DBObject event : events) {
        	BasicDBList userEvents = (BasicDBList)event.get("events");
        	for (int i = 0; i < userEvents.size(); i++) {
        		DBObject userEvent = (DBObject) userEvents.get(i);
        		
        		WebElement element = driver.findElement(By.cssSelector((String)userEvent.get("targetSelector")));
        		String tagName =  element.getTagName();
        		String type = element.getAttribute("type");
        		if (tagName == "input" && type == "text") {
        			element.click();
        	        element.sendKeys("Test data");
        	        element.sendKeys(Keys.ENTER);
				}
        		else {
        			element.click();
        		}
        	}
        	
        	BasicDBList mutations = (BasicDBList)event.get("mutations");
        	for (int i = 0; i < mutations.size(); i++) {
        		DBObject mutation = (DBObject) mutations.get(i);
        		
        		String type = (String)mutation.get("type");
        		if(type != "added") {
        			continue;
        		}
        		
        		String childSelector = (String)mutation.get("childSelector");
        		
        		WebElement child;
        		try {
        			child = (new WebDriverWait(driver, 5)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(childSelector)));
        		}
        		catch(TimeoutException e){
        			String targetSelector = (String)mutation.get("targetSelector");
        			WebElement targetElement = driver.findElement(By.cssSelector(targetSelector));
        			String html = targetElement.getAttribute("outerHTML");
        			db.saveErrorReport(childSelector, html, type);
        		}
        	}
		}
        
        //Close the browser
        driver.quit();
	}
}
