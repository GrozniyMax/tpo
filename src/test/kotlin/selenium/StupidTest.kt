package selenium

import org.junit.jupiter.api.*
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import selenium.SeleniumBaseTest
import selenium.Mode

/**
 * Глупый тест, который проверяет тестовый функционал
 */
class StupidTest : SeleniumBaseTest(
    useHeadless = false,
) {

    @Test
    fun testLogoExists() {
        driver.get("https://www.booking.com")
        waitForPageLoad()

        val logo = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a[aria-label*='Booking'], img[alt*='Booking'], .logo")
            )
        )

        Assertions.assertTrue(logo.isDisplayed) {
            "Логотип должен быть виден"
        }
    }

}
