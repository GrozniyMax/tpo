package selenium.pages.attractions

import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.attractions.searchResults.AttractionSearchResultsPage
import java.time.LocalDate

private val logger = LoggerFactory.getLogger(AttractionBookingHomePage::class.java)

class AttractionBookingHomePage(private val driver: WebDriver, private val wait: WebDriverWait) {

    companion object {
        private const val DESTINATION_INPUT_XPATH =
            "//input[@name='destination' or contains(@placeholder, 'Направление') or contains(@placeholder, 'Destination')]"

        private const val DATE_BUTTON_XPATH =
            "//button[contains(., 'Выберите даты') or contains(., 'Select dates')]"

        private const val SEARCH_BUTTON_XPATH =
            "//button[contains(., 'Проверить цены') or contains(., 'Check availability')]"

        private const val DESTINATION_SUGGESTION_XPATH =
            "//div[contains(@role, 'listbox')]//li[contains(@role, 'option')]"
    }

    /**
     * Проверка загрузки страницы
     */
    fun isLoaded(): Boolean {
        return try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(DESTINATION_INPUT_XPATH))).isDisplayed
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Загрузка страницы
     */
    fun load() {
        driver.get("https://www.booking.com/attractions/index.ru.html")
        isLoaded()
        logger.info { "Страница поиска экскурсий и развлечений загружена" }
    }

    /**
     * Ввод направления
     */
    private fun enterDestination(destination: String) {
        val destinationInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(DESTINATION_INPUT_XPATH)))
        destinationInput.clear()
        destinationInput.sendKeys(destination)
        logger.info { "Введено направление: $destination" }
    }

    /**
     * Выбор первой подсказки из автокомплита
     */
    private fun selectFirstSuggestion(): Boolean {
        return try {
            val suggestion = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DESTINATION_SUGGESTION_XPATH)))
            suggestion.click()
            logger.info { "Выбрана первая подсказка" }
            true
        } catch (e: Exception) {
            logger.info { "Подсказки не появились" }
            false
        }
    }

    /**
     * Клик по кнопке дат для открытия календаря
     */
    private fun openCalendar() {
        val dateButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DATE_BUTTON_XPATH)))
        dateButton.click()
        logger.info { "Календарь открыт" }
    }

    /**
     * Выбор дня из открытого календаря
     */
    private fun selectDayFromCalendar(date: LocalDate) {
        val xpath = "//span[@data-date='${date}']"
        val suggestedDay = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)))
        suggestedDay.click()
        logger.info { "Выбрана дата: $date" }
    }

    /**
     * Клик по кнопке поиска
     */
    fun clickSearch(): AttractionSearchResultsPage {
        val searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SEARCH_BUTTON_XPATH)))
        searchButton.click()
        logger.info { "Нажата кнопка поиска" }
        return AttractionSearchResultsPage(driver, wait)
    }

    /**
     * Полный сценарий поиска экскурсий и развлечений
     */
    fun searchAttractions(
        destination: String,
        date: LocalDate
    ): AttractionSearchResultsPage {
        enterDestination(destination)
        selectFirstSuggestion()
        logger.info { "Введено направление: $destination" }

        openCalendar()
        selectDayFromCalendar(date)
        logger.info { "Выбрана дата: $date" }

        return clickSearch()
    }

    /**
     * Поиск только по направлению (без даты)
     */
    fun searchAttractionsByDestination(destination: String): AttractionSearchResultsPage {
        enterDestination(destination)
        selectFirstSuggestion()
        logger.info { "Введено направление: $destination" }

        return clickSearch()
    }
}
