package selenium

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import selenium.pages.BookingHomePage
import java.time.Duration
import java.time.LocalDate

class BookingGuestUseCaseTest : SeleniumBaseTest(useHeadless = false) {

    lateinit var homePage: BookingHomePage

    @BeforeEach
    fun setUp() {
        driver.manage().deleteAllCookies()
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30))
        homePage = BookingHomePage(driver, wait)
    }

    @Test
    @DisplayName("Проверка загрузки базовой страницы")
    fun loadDoesNotThrowException() {
        homePage.load()
    }

    @Test
    @DisplayName("UC-GUEST-01: Поиск жилья без регистрации")
    fun testGuestSearchAccommodation() {
        homePage.load()

        val start = LocalDate.now()
        val end = start.plusDays(5)

        homePage.searchAccommodation("Таллин", start, end, 2)
    }
}
