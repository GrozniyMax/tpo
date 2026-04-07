package selenium.test.chrome.place

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import selenium.pages.place.searchResults.PlaceSearchResultsPage
import selenium.test.chrome.ChromeBaseTest
import java.time.Duration

class DetailsPageTest: ChromeBaseTest() {

    lateinit var page: PlaceSearchResultsPage

    @BeforeEach
    fun setUp() {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30))
        driver.manage().deleteAllCookies()
        driver.get("https://www.booking.com/searchresults.ru.html?ss=%D0%A2%D0%B0%D0%BB%D0%BB%D0%B8%D0%BD&ssne=%D0%A2%D0%B0%D0%BB%D0%BB%D0%B8%D0%BD&ssne_untouched=%D0%A2%D0%B0%D0%BB%D0%BB%D0%B8%D0%BD&efdco=1&label=gen173nr-10CAEoggI46AdIM1gEaEiIAQGYATO4AQfIAQzYAQPoAQH4AQGIAgGoAgG4ApP2hs4GwAIB0gIkNzJjYmFmZTctYjE2MC00YzJjLTg1MzgtN2UxODc4OTAxN2Vj2AIB4AIB&sid=295a2e964066843143f65568eddd840b&aid=304142&lang=ru&sb=1&src_elem=sb&src=searchresults&dest_id=-2625660&dest_type=city&checkin=2026-04-13&checkout=2026-04-18&group_adults=2&no_rooms=1&group_children=0")
        page = PlaceSearchResultsPage(driver, wait)
        page.handlePageLoadAndDialogs()
    }

    @Test
    @DisplayName("Проверка аттрибутов, которые не видно в карточке")
    fun hiddenAttributes() {

        page.clickOtherFilter("hotelfacility", "Парковка")

        val placePage = page.select(0)
        val properties = placePage.getProperties()

        assertTrue(properties.any { it.contains("Парковка") || it.contains("парковка") })
    }

}