package selenium.test

import org.junit.jupiter.api.*
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import selenium.SeleniumBaseTest
import selenium.pages.BookingCookiePage

/**
 * Тест для проверки работы PageObject обработки cookie и регистрации
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CookieAndRegistrationTest : SeleniumBaseTest(useHeadless = false) {

    private lateinit var cookiePage: BookingCookiePage

    @BeforeEach
    fun setUp() {
        cookiePage = BookingCookiePage(driver, wait)
    }

    @Test
    fun testPageLoadWithDialogsHandling() {
        // Навигация и полная обработка диалогов
        cookiePage.navigateToBooking()

        // Ждем пока страница полностью загрузится
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//main")))

        // Проверяем наличие основных элементов страницы
        val searchInput = driver.findElement(By.xpath("//input[@name='ss']"))
        Assertions.assertTrue(searchInput.isDisplayed, "Поле поиска должно быть доступно")
    }

    @Test
    fun testCookieDialogPresence() {
        // Переход на страницу без обработки
        cookiePage.loadPage()
        cookiePage.isPageLoaded()

        val cookiePresent = cookiePage.isCookieDialogPresent()

        Assertions.assertTrue(cookiePresent, "Диалог cookie должен быть отображен")
    }
}
