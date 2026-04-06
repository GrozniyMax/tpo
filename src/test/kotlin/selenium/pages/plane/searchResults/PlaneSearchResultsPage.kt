package selenium.pages.plane.searchResults

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.BookingCookiePage

class PlaneSearchResultsPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait
) : BookingCookiePage(driver, wait) {

    companion object {
        private const val FLIGHT_CARD_XPATH = "//div[@data-testid='searchresults_card']"
        private const val NO_RESULTS_XPATH = "//*[contains(., 'нет рейсов') or contains(., 'No flights')]"
    }

    private fun clickElement(element: WebElement) {
        try {
            element.click()
        } catch (e: Exception) {
            (driver as? JavascriptExecutor)?.executeScript("arguments[0].click();", element)
        }
    }

    private fun getFlightCards(topN: Int = 5): List<WebElement> {
        return wait.until { driver.findElements(By.xpath(FLIGHT_CARD_XPATH)) }.take(topN)
    }

    fun selectOneway() {
        val oneway = wait.until {
            driver.findElement(By.xpath("//div[@data-ui-name='search_type_oneway']"))
        }
        clickElement(oneway)
    }

    fun selectBothWay() {
        val bothWay = wait.until {
            driver.findElement(By.xpath("//input[@data-ui-name='input_search_type_roundtrip']"))
        }
        clickElement(bothWay)
    }

    fun getFlights(topN: Int = 5): List<FlightCard> {
        return getFlightCards(topN).mapNotNull { card ->
            try {
                PlaneCardParser.parse(card)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun search() {
        val searchButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@data-ui-name='button_search_submit']")
        ))
        clickElement(searchButton)
        waitForPageToOpen()
    }

    fun waitForPageToOpen() {
        wait.until(
            ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath(FLIGHT_CARD_XPATH)),
                ExpectedConditions.visibilityOfElementLocated(By.xpath(NO_RESULTS_XPATH))
            )
        )
    }
}
