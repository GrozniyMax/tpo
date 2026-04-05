package selenium.test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import selenium.SeleniumBaseTest
import selenium.pages.BookingCookiePage
import selenium.pages.searchResults.SearchResultsPage
import java.time.Duration
import java.time.LocalDate

class FiltersTest: SeleniumBaseTest(useHeadless = false) {

    lateinit var searchResultsPage: SearchResultsPage

    @BeforeEach
    fun setUp() {
        driver.manage().deleteAllCookies()
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30))

        val homePage = BookingCookiePage(driver, wait).navigateToBooking()

        val start = LocalDate.now()
        val end = start.plusDays(5)

        searchResultsPage = homePage.searchAccommodation("Таллин", start, end, 2)
    }

    @Test
    @DisplayName("Фильтрация по 'Популярные фильтры'")
    fun testPopularFilters() {
        val expectedSuggestions = listOf(
            "Hestia Hotel Seaport Tallinn",
            "Center Hotel"
        )

        val expectedTotal = searchResultsPage.addFilter("Завтрак включен")

        val actualSuggestions = searchResultsPage.getSuggestions(topN = 2)
        val totalSuggestionsCount = searchResultsPage.getTotalSuggestionsCount()

        assertEquals(expectedSuggestions, actualSuggestions)
        assertEquals(expectedTotal, totalSuggestionsCount)
    }

    @Test
    @DisplayName("Фильтрация по 'Тип размещения'")
    fun testPlacementFilter() {
        val expectedSuggestions = listOf(
            "Hestia Hotel Seaport Tallinn",
            "Citybox Tallinn City Center"
        )

        searchResultsPage.addFilter("Отели")

        val actualSuggestions = searchResultsPage.getSuggestions(topN = 2)
        val totalSuggestionsCount = searchResultsPage.getTotalSuggestionsCount()

        assertEquals(expectedSuggestions, actualSuggestions)
        assertEquals(72, totalSuggestionsCount)
    }
}