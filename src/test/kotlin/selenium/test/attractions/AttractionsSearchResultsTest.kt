package selenium.test.attractions

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import selenium.SeleniumBaseTest
import selenium.pages.attractions.searchResults.AttractionSearchResultsPage
import java.time.Duration


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AttractionsSearchResultsTest: SeleniumBaseTest() {

    lateinit var page: AttractionSearchResultsPage

    @BeforeEach
    fun setUp() {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30))
        driver.manage().deleteAllCookies()
        driver.get("https://www.booking.com/attractions/searchresults.ru.html?dest_id=-2625660&dest_type=city")
        page = AttractionSearchResultsPage(driver, wait)
        page.handlePageLoadAndDialogs()
    }

    @Test
    @DisplayName("Проверка применения фильтра 'Бесплатная отмена'")
    fun categoryFilterTest() {
        page.clickFilter( "Бесплатная отмена")

        val attractions = page.getAttractions()

        attractions.forEach {
            assertTrue(it.freeCancelAvailable)
        }
    }


    @Test
    @DisplayName("Проверка сортировки")
    fun sortTest() {
        page.applySort("Самая низкая цена")

        val attractions = page.getAttractions()

        val prices = attractions.map { it.price }

        val sorted = prices.sorted()

        assertEquals(prices, sorted)
    }

}