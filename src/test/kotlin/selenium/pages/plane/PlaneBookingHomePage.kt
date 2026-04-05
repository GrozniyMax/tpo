package selenium.pages.plane

import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.BookingCookiePage
import selenium.pages.plane.searchResults.PlaneSearchResultsPage
import java.time.LocalDate

private val logger = LoggerFactory.getLogger(PlaneBookingHomePage::class.java)

class PlaneBookingHomePage(
    private val driver: WebDriver,
    private val wait: WebDriverWait
) : BookingCookiePage(driver, wait) {

    companion object {
        private const val FROM_BUTTON_XPATH =
            "//button[contains(., 'Откуда') or contains(., 'From')]"

        private const val TO_BUTTON_XPATH =
            "//button[contains(., 'Куда') or contains(., 'To')]"

        private const val DATE_BUTTON_XPATH =
            "//button[contains(., 'Даты') or contains(., 'Dates')]"

        private const val PASSENGERS_BUTTON_XPATH =
            "//button[contains(., 'Пассажиры') or contains(., 'Passengers')]"

        private const val SUBMIT_BUTTON_XPATH =
            "//button[contains(., 'Найти') or contains(., 'Search')]"

        private const val SUGGESTION_XPATH =
            "//div[contains(@role, 'listbox')]//li[contains(@role, 'option')]"
    }

    fun declineRandom() {
        val xpath = "//button[@data-ui-name='input_location_from_segment_0']"
        val button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)))

        button.click()

        val declineCompleted = "//button[@data-autocomplete-chip-idx='0']"
        val declineButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(declineCompleted)))

        declineButton.click()

        logger.info { "Отклонили автокомплит" }
    }


    /**
     * Ввод пункта отправления
     */
    private fun enterFrom(from: String) {
        selectFirstFromDropdown(from, "input_location_from_segment_0")
        logger.info { "Введено место отправления: $from" }
    }

    /**
     * Ввод пункта назначения
     */
    private fun enterTo(to: String) {
        selectFirstFromDropdown(to, "input_location_to_segment_0")
        logger.info { "Введено место назначения: $to" }
    }

    private fun selectFirstFromDropdown(keys: String, dataUiName: String) {
        val xpath = "//button[@data-ui-name='$dataUiName']"
        val button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)))

        button.click()
        logger.info { "Клик по кнопке" }

        // Используем refreshed для защиты от stale element
        val inputLocator = By.xpath("//input[@data-ui-name='input_text_autocomplete']")

        wait.until(
            ExpectedConditions.refreshed(
                ExpectedConditions.elementToBeClickable(inputLocator)
            )
        )?.sendKeys(keys)

        logger.info { "Ввод места" }

        val dropdown = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.id("flights-searchbox_suggestions")
            )
        )

        val suggestion = wait.until {
            dropdown.findElement(By.xpath("./li[@data-ui-name='locations_list_item']"))
        }

        suggestion.click()

        logger.info { "Клик по первому попавшемуся месту" }
    }

    /**
     * Клик по кнопке поиска
     */
    fun clickSearch(): PlaneSearchResultsPage {
        val searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SUBMIT_BUTTON_XPATH)))
        searchButton.click()
        logger.info { "Нажата кнопка поиска" }
        return PlaneSearchResultsPage(driver, wait)
    }

    /**
     * Полный сценарий поиска авиабилетов (туда и обратно)
     */
    fun searchFlights(
        from: String,
        to: String
    ): PlaneSearchResultsPage {
        enterFrom(from)

        enterTo(to)

        return clickSearch()
    }

}
