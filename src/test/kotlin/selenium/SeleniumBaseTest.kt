package selenium

import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.jupiter.api.*
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.safari.SafariOptions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

enum class Mode {
    CHROME,
    FIREFOX,
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class SeleniumBaseTest(
    private val useHeadless: Boolean = false,
    private val mode: Mode = Mode.CHROME
) {

    protected lateinit var driver: WebDriver
    protected lateinit var wait: WebDriverWait

    @BeforeEach
    fun setUpAll() {
        driver = when(mode) {
            Mode.CHROME -> setupChromeDriver()
            Mode.FIREFOX -> setupFirefoxDriver()
        }

        // Увеличиваем таймауты для Firefox
        val implicitWait = when(mode) {
            Mode.CHROME -> 10L
            Mode.FIREFOX -> 20L
        }
        
        val pageLoadTimeout = when(mode) {
            Mode.CHROME -> 30L
            Mode.FIREFOX -> 60L
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait))
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout))
        wait = WebDriverWait(driver, Duration.ofSeconds(when(mode) {
            Mode.CHROME -> 30L
            Mode.FIREFOX -> 60L
        }))
    }

    private fun setupChromeDriver(): WebDriver {
        WebDriverManager.chromedriver().setup()

        val options = ChromeOptions().apply {
            if (useHeadless) {
                addArguments("--headless=new")
            }

            addArguments("--no-sandbox")
            addArguments("--disable-dev-shm-usage")
            addArguments("--disable-blink-features=AutomationControlled")
            addArguments("--disable-gpu")
            addArguments("--window-size=1920,1080")
            addArguments("--user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            addArguments("--disable-features=IsolateOrigins,site-per-process")
            addArguments("--disable-site-isolation-trials")

            setExperimentalOption("excludeSwitches", arrayOf("enable-automation"))
            setExperimentalOption("useAutomationExtension", false)
        }

        val chromeDriver = ChromeDriver(options)

        // Подмена navigator.webdriver через CDP
        val cdpDriver = chromeDriver as org.openqa.selenium.chromium.ChromiumDriver
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

        return chromeDriver
    }

    private fun setupFirefoxDriver(): WebDriver {
        WebDriverManager.firefoxdriver().setup()

        val options = FirefoxOptions().apply {
            if (useHeadless) {
                addArguments("--headless")
            }
            addArguments("--width=1920")
            addArguments("--height=1080")
            
            // Подмена navigator.webdriver для Firefox
            addPreference("dom.webdriver.enabled", false)
            addPreference("useAutomationExtension", false)
            
            // Отключаем telemetry и сбор данных
            addPreference("datareporting.healthreport.uploadEnabled", false)
            addPreference("datareporting.policy.dataSubmissionEnabled", false)
            
            // Устанавливаем тот же user-agent что и в Chrome
            addPreference("general.useragent.override", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        }

        val firefoxDriver = FirefoxDriver(options)
        
        // Подмена navigator.webdriver через JavaScript
        firefoxDriver.executeScript("""
            Object.defineProperty(navigator, 'webdriver', { get: () => undefined })
            Object.defineProperty(navigator, 'plugins', { get: () => [1, 2, 3, 4, 5] })
            Object.defineProperty(navigator, 'languages', { get: () => ['en-US', 'en'] })
            Object.defineProperty(document, 'hidden', { get: () => false })
            Object.defineProperty(document, 'visibilityState', { get: () => 'visible' })
        """.trimIndent())
        
        return firefoxDriver
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