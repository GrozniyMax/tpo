package selenium.test.plane

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import selenium.Mode
import selenium.SeleniumBaseTest
import selenium.pages.plane.PlaneBookingHomePage
import selenium.pages.plane.searchResults.Flight
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SearchTest: SeleniumBaseTest() {

    lateinit var homePage: PlaneBookingHomePage

    @BeforeEach
    fun setUp() {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30))
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))
        driver.manage().deleteAllCookies()
        driver.get("https://www.booking.com/flights/index.ru.html")
        homePage = PlaneBookingHomePage(driver, wait)
        homePage.handlePageLoadAndDialogs()
        
        // Дополнительная проверка и закрытие cookie баннера
        try {
            homePage.tryCloseCookieBanner()
        } catch (e: Exception) {
            // Игнорируем ошибки
        }
    }

    @Test
    @DisplayName("Проверка поиска рейсов для РФ")
    fun testForRussia() {
        homePage.declineRandom()

        val searchResultsPage = homePage.searchFlights("Москва", "Санкт-Петербург")

        val results = searchResultsPage.getFlights()

        assertTrue(results.isEmpty())
    }

    @Test
    @DisplayName("Проверка поиска рейсов")
    fun testSearchFlights() {
        homePage.declineRandom()

        val searchResultsPage = homePage.searchFlights("Стокгольм", "Стамбул")

        val results = searchResultsPage.getFlights(3)

        assertTrue(results.isNotEmpty())

        results.forEach{
            assertEquals(2, it.flights.size)
            checkFlightToIstanbul(it.flights[0])
            checkFlightToStockholm(it.flights[1])
        }
    }

    private fun checkFlightToIstanbul(flight: Flight) {

        assertEquals("9 мая", flight.departure.day)
        assertEquals("9 мая", flight.arrival.day)

        assertEquals("ARN", flight.departure.code)
        assertEquals("SAW", flight.arrival.code)

        assertTrue(parseTime(flight.departure.time) < parseTime(flight.arrival.time))

    }

    private fun checkFlightToStockholm(flight: Flight) {
        assertEquals("16 мая", flight.departure.day)
        assertEquals("16 мая", flight.arrival.day)

        assertEquals("SAW", flight.departure.code)
        assertEquals("ARN", flight.arrival.code)

        assertTrue(parseTime(flight.departure.time) < parseTime(flight.arrival.time))
    }

    private fun parseTime(time: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.parse(time, formatter)
    }

}
