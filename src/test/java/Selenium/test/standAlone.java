package Selenium.test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import io.github.bonigarcia.wdm.WebDriverManager;

public class standAlone {

    public static void main(String[] args) {
        String item = "ZARA COAT 3";
        String country = "India";

        WebDriverManager.chromedriver().setup();
       // WebDriver driver = new ChromeDriver();
        //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.setExperimentalOption("prefs", Map.of(
            "credentials_enable_service", false,
            "profile.password_manager_enabled", false
        ));

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");
        
        //WebDriver driver = new ChromeDriver(options);

        
        driver.manage().window().maximize();
        driver.get("https://rahulshettyacademy.com/client");

        // Login
        driver.findElement(By.id("userEmail")).sendKeys("roushan@gmail.com");
        driver.findElement(By.id("userPassword")).sendKeys("Roushan@123");
        driver.findElement(By.id("login")).click();

        // Wait for product cards to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".mb-3")));

        // Find product and click Add To Cart
        List<WebElement> products = driver.findElements(By.cssSelector(".mb-3"));
        WebElement targetProduct = products.stream()
                .filter(p -> p.findElement(By.cssSelector("b")).getText().equalsIgnoreCase(item))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found: " + item));

        targetProduct.findElement(By.cssSelector("button.w-10")).click();

        // Wait for toast confirmation
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("toast-container")));
        
        // ✅ Safe check for fade-out
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".ng-animating")));

        // Go to cart
        driver.findElement(By.cssSelector("[routerlink*='cart']")).click();

        // Check if item is in the cart
        List<WebElement> cartItems = driver.findElements(By.cssSelector(".cartSection h3"));
        boolean itemInCart = cartItems.stream()
                .anyMatch(cartItem -> cartItem.getText().equalsIgnoreCase(item));
        //Assert.assertTrue(itemInCart, "Item not found in cart: " + item);
        System.out.println("Cart Items:");
        cartItems.forEach(c -> System.out.println(" - " + c.getText()));
        Assert.assertTrue(itemInCart, "❌ Item not found in cart: " + item);
        
        
        // Proceed to checkout
        driver.findElement(By.cssSelector(".totalRow button")).click();

        // Enter country
        Actions actions = new Actions(driver);
        WebElement countryInput = driver.findElement(By.xpath("//input[@placeholder='Select Country']"));
        actions.sendKeys(countryInput, country).build().perform();

        // Wait and select country from dropdown
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ta-results")));
        List<WebElement> suggestions = driver.findElements(By.cssSelector(".ta-results button"));
        suggestions.stream()
                .filter(s -> s.getText().equalsIgnoreCase(country))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Country not found in list"))
                .click();

        // Place the order
        driver.findElement(By.cssSelector(".action__submit")).click();

        // Verify confirmation message
        String confirmationMessage = driver.findElement(By.cssSelector(".hero-primary")).getText();
        System.out.println("Confirmation: " + confirmationMessage);
        Assert.assertTrue(confirmationMessage.equalsIgnoreCase("THANKYOU FOR THE ORDER."));

        // Get and print order number
        String orderNumber = driver.findElement(By.cssSelector(".em-spacer-1 .ng-star-inserted")).getText();
        System.out.println("Order Number: " + orderNumber);

        driver.quit();
    }
}
