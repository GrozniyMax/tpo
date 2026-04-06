package selenium.test.attractions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import selenium.SeleniumBaseTest
import selenium.pages.attractions.AttractionBookingHomePage
import java.time.Duration
import java.time.LocalDate

class AttractionsSearchTest: SeleniumBaseTest() {

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

        actualAttractions.forEach {
            assertTrue(!it.title.isNullOrEmpty())
        }
    }

    @Test
    @DisplayName("Поиск без указания направления")
    fun testSearchWithoutDestination() {
        homePage.clickSearch()

        val errorElement = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@data-testid, 'error') or contains(@role, 'alert')]")
            )
        )

        assertTrue(errorElement.isDisplayed)
    }

}
