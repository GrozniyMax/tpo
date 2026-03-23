package selenium

import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.jupiter.api.*
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class SeleniumBaseTest(private val useHeadless: Boolean = false) {

    protected lateinit var driver: WebDriver
    protected lateinit var wait: WebDriverWait

    @BeforeAll
    fun setUpAll() {
        WebDriverManager.chromedriver().setup()

        val options = ChromeOptions().apply {
            if (useHeadless) {
                addArguments("--headless=new") // Невидимый браузер
            }

            // Аргументы для скрытия автоматизации
            addArguments("--no-sandbox")
            addArguments("--disable-dev-shm-usage")
            addArguments("--disable-blink-features=AutomationControlled")
            addArguments("--disable-gpu")
            addArguments("--window-size=1920,1080")
            addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            addArguments("--disable-features=IsolateOrigins,site-per-process")
            addArguments("--disable-site-isolation-trials")

            // Исключаем переключатель автоматизации
            setExperimentalOption("excludeSwitches", arrayOf("enable-automation"))
            setExperimentalOption("useAutomationExtension", false)
        }

        driver = ChromeDriver(options)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30))
        wait = WebDriverWait(driver, Duration.ofSeconds(3))

        // Подмена navigator.webdriver через CDP
        val cdpDriver = driver as org.openqa.selenium.chromium.ChromiumDriver
        cdpDriver.executeCdpCommand(
            "Page.addScriptToEvaluateOnNewDocument", mapOf(
                "source" to """
                    Object.defineProperty(navigator, 'webdriver', { get: () => undefined })
                    Object.defineProperty(navigator, 'plugins', { get: () => [1, 2, 3, 4, 5] })
                    Object.defineProperty(navigator, 'languages', { get: () => ['en-US', 'en'] })
                    Object.defineProperty(document, 'hidden', { get: () => false })
                    Object.defineProperty(document, 'visibilityState', { get: () => 'visible' })
                """
            )
        )
    }

    protected fun waitForPageLoad() {
        wait.until { driver ->
            driver.title?.isNotEmpty() == true
        }
    }


    @AfterAll
    fun tearDownAll() {
        if (::driver.isInitialized) {
            driver.quit()
        }
    }
}