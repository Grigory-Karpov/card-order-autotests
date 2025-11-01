import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DebitCardTest {

    private WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // --- Позитивный сценарий ---
    @Test
    void shouldSubmitRequestSuccessfully() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Анна-Мария Петрова-Сидорова");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79998887766");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button")).click();

        WebElement successMessage = driver.findElement(By.cssSelector("[data-test-id='order-success']"));
        String actualText = successMessage.getText().trim();
        Assertions.assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", actualText);
    }

    // --- Тесты на валидацию поля "Имя и Фамилия" ---
    @Test
    void shouldFailWithInvalidName() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Vasilisa");
        driver.findElement(By.cssSelector("button")).click();

        WebElement nameValidation = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        String validationText = nameValidation.getText().trim();
        Assertions.assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", validationText);
    }

    @Test
    void shouldFailWithEmptyName() {
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79998887766");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button")).click();

        WebElement nameValidation = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        String validationText = nameValidation.getText().trim();
        Assertions.assertEquals("Поле обязательно для заполнения", validationText);
    }

    // --- Тесты на валидацию поля "Телефон" ---
    @Test
    void shouldFailWithInvalidPhone() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Мария Иванова");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+7999");
        driver.findElement(By.cssSelector("button")).click();

        WebElement phoneValidation = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        String validationText = phoneValidation.getText().trim();
        Assertions.assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", validationText);
    }

    @Test
    void shouldFailWithEmptyPhone() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Мария Иванова");
        driver.findElement(By.cssSelector("button")).click();

        WebElement phoneValidation = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        String validationText = phoneValidation.getText().trim();
        Assertions.assertEquals("Поле обязательно для заполнения", validationText);
    }

    // --- Тест на валидацию чекбокса ---
    @Test
    void shouldFailWithoutAgreement() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Мария Иванова");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79998887766");
        driver.findElement(By.cssSelector("button")).click();

        WebElement agreementValidation = driver.findElement(By.cssSelector("[data-test-id='agreement'].input_invalid"));
        Assertions.assertTrue(agreementValidation.isDisplayed());
    }
}