package selenium.test.chrome.plane

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import selenium.pages.plane.searchResults.Flight
import selenium.pages.plane.searchResults.PlaneSearchResultsPage
import selenium.test.chrome.ChromeBaseTest
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SearchResultsTest: ChromeBaseTest() {

    lateinit var page: PlaneSearchResultsPage

    @BeforeEach
    fun setUp() {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60))
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))
        driver.manage().deleteAllCookies()
        driver.get("https://flights.booking.com/flights/STO.CITY-IST.CITY/?type=ROUNDTRIP&adults=2&cabinClass=ECONOMY&children=0&from=STO.CITY&to=IST.CITY&fromCountry=SE&toCountry=TR&fromLocationName=%D0%A1%D1%82%D0%BE%D0%BA%D0%B3%D0%BE%D0%BB%D1%8C%D0%BC&toLocationName=%D0%A1%D1%82%D0%B0%D0%BC%D0%B1%D1%83%D0%BB&depart=2026-05-09&return=2026-05-16&sort=BEST&travelPurpose=leisure&ca_source=flights_search_sb&aid=304142&label=gen173nr-10EgdmbGlnaHRzKIICQg1zZWFyY2hyZXN1bHRzSCFYBGhIiAEBmAEzuAEHyAEM2AED6AEB-AEBiAIBqAIBuAKftcrOBsACAdICJGI3NDMxNDEzLTE1YWYtNGI5My04ZjEwLTdiNTIxNmE4ZDZlN9gCAeACAQ")
        page = PlaneSearchResultsPage(driver, wait)
        page.handlePageLoadAndDialogs()
    }

    @Test
    @DisplayName("Поиск билетов 'Туда и обратно'")
    fun searchBothWay() {
        val searchResults = page.getFlights()

        searchResults.forEach{
            assertEquals(2, it.flights.size)
            checkFlightToIstanbul(it.flights[0])
            checkFlightToStockholm(it.flights[1])
        }
    }

    @Test
    @DisplayName("Поиск билетов 'В одну сторону'")
    fun searchOneWay() {
        page.selectOneway()
        page.search()
        val searchResults = page.getFlights()

        searchResults.forEach{
            assertEquals(1, it.flights.size)
            checkFlightToIstanbul(it.flights[0])
        }
    }

    private fun checkFlightToIstanbul(flight: Flight) {

        assertEquals("9 мая", flight.departure.day)
        assertEquals("9 мая", flight.arrival.day)

        assertEquals("ARN", flight.departure.code)
        assertTrue(flight.arrival.code == "SAW" || flight.arrival.code == "IST" )

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
