package selenium.test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import selenium.SeleniumBaseTest
import selenium.pages.BookingCookiePage
import selenium.pages.place.searchResults.PlaceSearchResultsPage
import java.time.Duration
import java.time.LocalDate

class FiltersTest: SeleniumBaseTest(useHeadless = false) {

    lateinit var placeSearchResultsPage: PlaceSearchResultsPage

    @BeforeEach
    fun setUp() {
        driver.manage().deleteAllCookies()
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30))

        val homePage = BookingCookiePage(driver, wait).navigateToBooking()

        val start = LocalDate.now()
        val end = start.plusDays(5)

        placeSearchResultsPage = homePage.searchAccommodation("Таллин", start, end, 2)
    }

//    @Test
//    @DisplayName("Фильтрация по 'Популярные фильтры'")
//    fun testPopularFilters() {
//        val expectedSuggestions = listOf(
//            "Hestia Hotel Seaport Tallinn",
//            "Center Hotel"
//        )
//
//        val expectedTotal = placeSearchResultsPage.addFilter("Завтрак включен")
//
//        val actualSuggestions = placeSearchResultsPage.getSuggestions(topN = 2)
//        val totalSuggestionsCount = placeSearchResultsPage.getTotalSuggestionsCount()
//
//        assertEquals(expectedSuggestions, actualSuggestions)
//        assertEquals(expectedTotal, totalSuggestionsCount)
//    }
//
//    @Test
//    @DisplayName("Фильтрация по 'Тип размещения'")
//    fun testPlacementFilter() {
//        val expectedSuggestions = listOf(
//            "Hestia Hotel Seaport Tallinn",
//            "Citybox Tallinn City Center"
//        )
//
//        placeSearchResultsPage.addFilter("Отели")
//
//        val actualSuggestions = placeSearchResultsPage.getSuggestions(topN = 2)
//        val totalSuggestionsCount = placeSearchResultsPage.getTotalSuggestionsCount()
//
//        assertEquals(expectedSuggestions, actualSuggestions)
//        assertEquals(72, totalSuggestionsCount)
//    }
}