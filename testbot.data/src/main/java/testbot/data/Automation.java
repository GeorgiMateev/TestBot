package testbot.data;

import java.io.File;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Automation {
	public void start() {
		File pathToBinary = new File("D:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
		FirefoxBinary ffBinary = new FirefoxBinary(pathToBinary);
		FirefoxProfile firefoxProfile = new FirefoxProfile();
		FirefoxDriver driver = new FirefoxDriver(ffBinary,firefoxProfile);
		
        //WebDriver driver = new FirefoxDriver();
        driver.get("http://localhost:8080");
        
        
        WebElement element = driver.findElement(By.cssSelector("ng-view.ng-scope>section#todoapp>header#header>form#todo-form>input#new-todo"));

        element.click();
        element.sendKeys("sdsd");
        element.sendKeys(Keys.ENTER);

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());
        
        
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.cssSelector("ng-view.ng-scope>section#todoapp>section#main>ul#todo-list>li.ng-scope")) != null;
            }
        });

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        
        //Close the browser
        driver.quit();
	}
}
