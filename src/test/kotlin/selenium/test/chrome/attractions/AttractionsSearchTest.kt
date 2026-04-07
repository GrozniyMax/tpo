package selenium.test.chrome.attractions

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import selenium.pages.attractions.AttractionBookingHomePage
import selenium.test.chrome.ChromeBaseTest
import java.time.Duration
import java.time.LocalDate

class AttractionsSearchTest: ChromeBaseTest() {

    lateinit var homePage: AttractionBookingHomePage

    @BeforeEach
    fun setUp() {
        homePage = AttractionBookingHomePage(driver, wait)
        homePage.load()

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30))
    }

    @Test
    @DisplayName("Поиск экскурсий по направлению")
    fun testAttractionSearch() {
        val date = LocalDate.now().plusDays(1)

        val searchResult = homePage.searchAttractions("Таллин", date)

        val actualAttractions = searchResult.getAttractions(topN = 2)

        if (actualAttractions.isNotEmpty()) {
            actualAttractions.forEach {
                assertTrue(!it.title.isNullOrEmpty(), "У экскурсии должен быть заголовок")
            }
        }
    }

}
