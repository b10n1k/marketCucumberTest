import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import cucumber.api.PendingException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
//import org.openqa.selenium.phantomjsdriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Cookie;
import java.util.List;

public class Stepdefs {

    WebDriver driver;
    double expectedPrice1 = 0.0;
    double expectedPrice2 = 0.0;
    
    @Given("^Visit www\\.sportsdirect\\.com$")
    public void visit_www_sportsdirect_com() throws Throwable {
        FirefoxProfile profile = new FirefoxProfile();
        // Try to disable popup
        //profile.setPreference("dom.disable_beforeunload", true);
        profile.setPreference("dom.popup_maximum", 0);
        profile.setPreference("privacy.popups.showBrowserMessage", false);

        // use geckodriver 0.17
        System.setProperty("webdriver.gecko.driver", "geckodriver");
        DesiredCapabilities capabilities=DesiredCapabilities.firefox();
        capabilities.setCapability("marionette", false);
        // Firefox/53.0
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);
        
        driver = new FirefoxDriver(capabilities);
        //driver = getPhantomJSDriver();

        // Attempt to set AdvertCookie to true
        Cookie ck = new Cookie("AdvertCookie", "true");
        driver.manage().addCookie(ck);

        driver.get("http://www.sportsdirect.com");
        
    }

    @When("^User chooses two products and puts them in the bag$")
    public void user_chooses_two_products_and_puts_them_in_the_bag() throws Throwable {
 
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        
        WebDriverWait wait = new WebDriverWait(driver, 2);
        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("advertPopup")));
        
        if (popup.isDisplayed()) {
            popup.findElement(By.xpath("//*[@id='advertPopup']//button")).click();
        }
        // Select Mens catalogue
        WebElement elemMens = driver.findElement(By.xpath("//a[@href='/mens']"));
        elemMens.click();
        // Select first item from shoes
        WebElement elemShoes = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/mens/mens-shoes']")));
        elemShoes.click();
        popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("advertPopup")));
        if (popup.isDisplayed()) {
            popup.findElement(By.xpath("//*[@id='advertPopup']//button")).click();
        }
        // To add an item to the basket choose its picture and its size
        List<WebElement> pic1 = driver.findElements(By.xpath("//a[@class='ProductImageList']"));
        pic1.get(2).click();
        
        List<WebElement> size1 = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//ul[@class='sizeButtons hidden-xs']/li")));
        size1.get(2).click();
        
        popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("advertPopup")));
        if (popup.isDisplayed()) {
            popup.findElement(By.xpath("//*[@id='advertPopup']//button")).click();
        }
        // Get the prize
        WebElement expectedPrize1 = driver.findElement(By.xpath("//span[@class='productHasRef']"));

        expectedPrice1 = Double.parseDouble(expectedPrize1.getText().replace(",",".").split(" ")[0]);
        System.out.println("expectedPrize1 = " + expectedPrice1);

        WebElement elemToBag = driver.findElement(By.xpath("//a[@class='addToBag']"));
        elemToBag.click();

        //Ensure item has been added
        wait.until(ExpectedConditions.textToBe(By.xpath("//span[@id='bagQuantity']"), "1"));

        // Select second item
        elemMens = driver.findElement(By.xpath("//a[@href='/mens']"));
        elemMens.click();
        // Select from trainers subcatalogue
        WebElement elemTrainers =  driver.findElement(By.xpath("//a[@href='/mens/mens-trainers']"));
        elemTrainers.click();
        // Select item and its size
        List<WebElement> pic2 = driver.findElements(By.xpath("//a[@class='ProductImageList']"));
        pic2.get(2).click();
        
        List<WebElement> size2 = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//ul[@class='sizeButtons hidden-xs']/li")));
        size2.get(2).click();

        // Save price for second item
        WebElement expectedPrize2 = driver.findElement(By.xpath("//span[@class='productHasRef']"));

        expectedPrice2 = Double.parseDouble(expectedPrize2.getText().replace(",",".").split(" ")[0]);
        System.out.println("expectedPrize1 = " + expectedPrice2);
        // add to the bag
        elemToBag = driver.findElement(By.xpath("//a[@class='addToBag']"));
        elemToBag.click();
    }

    @When("^User goes to the bag$")
    public void user_goes_to_the_bag() throws Throwable {
        WebDriverWait wait = new WebDriverWait(driver, 2);
        WebElement bag = driver.findElement(By.xpath("//span[@id='bagQuantity']"));
        bag.click();
        
    }

    @When("^User adds one more instance of shoes product$")
    public void user_adds_one_more_instance_of_shoes_product() throws Throwable {
        // increase first item's quantity
        List<WebElement> elemPlus = driver.findElements(By.xpath("//a[@class='BasketQuantBut s-basket-plus-button']"));
        elemPlus.get(0).click();
        // Bag need to be updated
        WebElement elemUpdateBag = driver.findElement(By.xpath("//a[contains(.,'Update bag')]"));
        elemUpdateBag.click();
    }
    
    @Then("^Ensure that the total sum is calculated correctly$")
    public void ensure_that_the_total_sum_is_calculated_correctly() throws Throwable {

        WebElement elemTotal = driver.findElement(By.xpath("//span[@id='TotalValue']"));
        Double total = Double.parseDouble(elemTotal.getText().replace(",",".").split(" ")[0]);
        System.out.println((expectedPrice1*2)+expectedPrice2+"");
        
        assert(total == ((expectedPrice1*2)+expectedPrice2));
    }   
 
}
