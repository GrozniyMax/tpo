package selenium

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import selenium.pages.BookingHomePage
import java.time.Duration
import java.time.LocalDate

class BookingGuestUseCaseTest : SeleniumBaseTest(useHeadless = false) {

    lateinit var homePage: BookingHomePage

    val expectedSuggestions = listOf(
        "Hestia Hotel Seaport Tallinn",
        "Harbour Cabins Near Old Town"
    )

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

        val actualSuggestions = homePage.getSuggestions(firstN = 2)

        assertEquals(expectedSuggestions, actualSuggestions)
    }

    @Test
    @DisplayName("Поиск без указания даты")
    fun testGuestSearchAccommodationWithoutDates() {
        homePage.load()

        homePage.searchAccommodation("Таллин", 2)

        val actualSuggestions = homePage.getSuggestions(firstN = 2)

        assertEquals(expectedSuggestions, actualSuggestions)
    }

    @Test
    @DisplayName("Поиск без указания места")
    fun testGuestSearchAccommodationWithoutPlace() {
        val alertText = "Чтобы начать поиск, введите направление."

        homePage.load()

        homePage.clickSearch()

        val alert = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"main\"]/div[1]/div/div/div/div/div/div/div/div[2]/div/div/form/div/div[1]/div/div[2]")))

        assertTrue(alert.isDisplayed)
        assertEquals(alertText, alert.text)

    }


}
