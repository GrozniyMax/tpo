package selenium.pages.plane

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.BookingCookiePage
import selenium.pages.plane.searchResults.PlaneSearchResultsPage

class PlaneBookingHomePage(
    private val driver: WebDriver,
    private val wait: WebDriverWait
) : BookingCookiePage(driver, wait) {

    companion object {
        private const val SUBMIT_BUTTON_XPATH = "//button[contains(., 'Найти') or contains(., 'Search')]"
        private const val INPUT_XPATH = "//input[@data-ui-name='input_text_autocomplete']"
        private const val SUGGESTION_XPATH = "//li[@data-ui-name='locations_list_item'][1]"
    }

    private fun clickElement(element: WebElement) {
        try {
            element.click()
        } catch (e: Exception) {
            (driver as? JavascriptExecutor)?.executeScript("arguments[0].click();", element)
        }
    }

    fun declineRandom() {
        try {
            declineCookie()

            val button = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@data-ui-name='input_location_from_segment_0']")
            ))
            clickElement(button)

            val declineButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@data-autocomplete-chip-idx='0']")
            ))
            clickElement(declineButton)
        } catch (e: Exception) {
            // Игнорируем ошибки автокомплита
        }
    }

    private fun enterFrom(from: String) {
        selectFirstFromDropdown(from, "input_location_from_segment_0")
    }

    private fun enterTo(to: String) {
        selectFirstFromDropdown(to, "input_location_to_segment_0")
    }

    private fun selectFirstFromDropdown(keys: String, dataUiName: String) {
        val button = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[@data-ui-name='$dataUiName']")
        ))
        clickElement(button)

        val input = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(INPUT_XPATH)
        ))

        try {
            input.sendKeys(keys)
        } catch (e: org.openqa.selenium.StaleElementReferenceException) {
            val newInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(INPUT_XPATH)))
            newInput.sendKeys(keys)
        }

        try {
            val suggestion = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(SUGGESTION_XPATH)
            ))
            clickElement(suggestion)
            wait.until(ExpectedConditions.stalenessOf(suggestion))
        } catch (e: Exception) {
            // Игнорируем, если подсказка не найдена
        }
    }

    fun clickSearch(): PlaneSearchResultsPage {
        val searchButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath(SUBMIT_BUTTON_XPATH)
        ))
        clickElement(searchButton)

        val resultsPage = PlaneSearchResultsPage(driver, wait)
        resultsPage.waitForPageToOpen()
        return resultsPage
    }

    fun searchFlights(from: String, to: String): PlaneSearchResultsPage {
        enterFrom(from)
        enterTo(to)
        return clickSearch()
    }
}
