package selenium.test

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import selenium.SeleniumBaseTest
import selenium.pages.BookingCookiePage
import selenium.pages.place.BookingHomePage
import java.time.Duration
import java.time.LocalDate

class SearchInputTest : SeleniumBaseTest(useHeadless = false) {

    lateinit var homePage: BookingHomePage

    val expectedSuggestions = listOf(
        "Hestia Hotel Seaport Tallinn",
        "Harbour Cabins Near Old Town"
    )

    @BeforeEach
    fun setUp() {
        driver.manage().deleteAllCookies()
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30))

        homePage = BookingCookiePage(driver, wait).navigateToBooking()
    }


    @Test
    @DisplayName("UC-GUEST-01: Поиск жилья без регистрации")
    fun testGuestSearchAccommodation() {
        val start = LocalDate.now().plusDays(1)
        val end = start.plusDays(5)

        val searchResult = homePage.searchAccommodation("Таллин", start, end, 2)

        val actualSuggestions = searchResult.getSuggestions(topN = 2)
        val totalSuggestionsCount = searchResult.getTotalSuggestionsCount()

        assertEquals(318, totalSuggestionsCount)
        assertEquals(expectedSuggestions, actualSuggestions)
    }

    @Test
    @DisplayName("Поиск без указания даты")
    fun testGuestSearchAccommodationWithoutDates() {

        val searchResult = homePage.searchAccommodation("Таллин", 2)

        val actualSuggestions = searchResult.getSuggestions(topN = 2)
        val totalSuggestionsCount = searchResult.getTotalSuggestionsCount()

        assertEquals(318, totalSuggestionsCount)
        assertEquals(expectedSuggestions, actualSuggestions)
    }

    @Test
    @DisplayName("Поиск без указания места")
    fun testGuestSearchAccommodationWithoutPlace() {
        val alertText = "Чтобы начать поиск, введите направление."

        homePage.clickSearch()

        val alert = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"main\"]/div[1]/div/div/div/div/div/div/div/div[2]/div/div/form/div/div[1]/div/div[2]")))

        assertTrue(alert.isDisplayed)
        assertEquals(alertText, alert.text)
    }


}
