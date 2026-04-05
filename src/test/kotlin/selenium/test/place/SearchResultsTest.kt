package selenium.test.place

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory
import selenium.Mode
import selenium.SeleniumBaseTest
import selenium.pages.place.searchResults.PlaceSearchResultsPage
import java.time.Duration


class SearchResultsTest: SeleniumBaseTest(mode = Mode.CHROME) {

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
    @DisplayName("Проверка 'популярных' фильтров")
    fun popularFiltersTest() {

        page.clickPopularFilter("Завтрак включен")

        val suggestions = page.getSuggestions()

        suggestions.forEach {
            assertTrue(it.attributes.any { attribute -> attribute.contains("Завтрак включен") })
        }
    }

    @Test
    @DisplayName("Проверка фильтра 'Оценка по отзывам'")
    fun ratingFilterTest() {

        page.clickOtherFilter("review_score", "Превосходно: 9+")

        val suggestions = page.getSuggestions()

        suggestions.forEach {
            val rating = it.rating.replace(",", ".").toDouble()
            assertTrue(rating >= 9.0)
        }

    }

    @Test
    @DisplayName("Переход на страницу с деталями")
    fun goToDetailsPageTest() {

        val suggestions = page.getSuggestions(1)

        val details = page.select(0)

        val title = details.getTitle()

        assertEquals(title, suggestions[0].title)
    }

    @Test
    @DisplayName("Проверка аттрибутов, которые не видно в карточке")
    fun hiddenAttributes() {

        page.clickOtherFilter("hotelfacility", "Парковка")

        val placePage = page.select(0)
        val properties = placePage.getProperties()

        assertTrue(properties.any { it.contains("Парковка") || it.contains("парковка") })
    }

    @Test
    @DisplayName("Проверка возможности вернуться назад")
    fun goBackTest() {

        val suggestions = page.getSuggestions()

        val details = page.select(0)

        details.goBack()

        val suggestions2 = page.getSuggestions()

        assertEquals(suggestions, suggestions2)

    }

    @Test
    @DisplayName("Проверка сотрирования")
    fun sortTest() {

        page.applySort("Цена (сначала самая низкая)")

        val suggestions = page.getSuggestions()

        val sorted = suggestions.sortedByDescending { it.price }

        assertEquals(suggestions, sorted)
    }

}