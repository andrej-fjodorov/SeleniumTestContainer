import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import static java.time.Duration.ofSeconds;
import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

@ExtendWith(SeleniumJupiter.class)
public class SeleniumTestContainer
{
    private WebDriver driver;

    @BeforeEach
    public void setUp() throws MalformedURLException {
         //docker run -d -p 5555:4444 -p 7900:7900 --shm-size="2g" selenium/standalone-firefox:latest
        driver = new RemoteWebDriver(new URL("http://localhost:4444"), new ChromeOptions());
        //driver = new ChromeDriver();

    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void searchTest() {
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(ofSeconds(5));
        driver.get("https://labirint.ru");
        driver.manage().addCookie(new Cookie("cookie_policy", "1"));

        driver.findElement(By.cssSelector("#search-field")).sendKeys("Java", Keys.RETURN);

        driver.findElement(By.cssSelector("span.sorting-items")).click();
        driver.findElement(By.cssSelector("[data-event-content='высокий рейтинг']")).click();

        WebElement loader = driver.findElement(By.cssSelector("div.loading-panel"));
        new WebDriverWait(driver, ofSeconds(10)).until(
                or(
                        stalenessOf(loader),
                        invisibilityOf(loader)
                )
        );

        List<WebElement> buttons = driver.findElements(By.cssSelector(".btn-tocart.buy-link"));

        for (WebElement button : buttons) {
            button.click();
        }

        String sizeToBe = Integer.toString(buttons.size());

        new WebDriverWait(driver, ofSeconds(5)).until(textToBe(By.cssSelector(".basket-in-cart-a"), sizeToBe));

        String sizeAsIs = driver.findElement(By.cssSelector(".basket-in-cart-a")).getText();

        assertEquals(sizeToBe, sizeAsIs);
    }
}
