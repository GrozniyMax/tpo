package selenium.test.firefox.place

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import selenium.Mode
import selenium.SeleniumBaseTest
import selenium.pages.BookingCookiePage
import selenium.pages.place.BookingHomePage
import java.time.Duration
import java.time.LocalDate

class SearchTest: SeleniumBaseTest() {

    lateinit var homePage: BookingHomePage

    @BeforeEach
    fun setUp() {
        homePage = BookingCookiePage(driver, wait).navigateToBooking()

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30))

        homePage = BookingCookiePage(driver, wait).navigateToBooking()
    }

    @Test
    @DisplayName("Поиск жилья")
    fun testGuestSearchAccommodation() {
        val start = LocalDate.now().plusDays(1)
        val end = start.plusDays(5)

        val searchResult = homePage.searchAccommodation("Таллин", start, end, 2)

        val actualSuggestions = searchResult.getSuggestions(topN = 2)

        actualSuggestions.forEach {
            assertTrue(it.attributes.any{ attribute -> attribute.contains("Таллин") || attribute.contains("Talinn") })
        }
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