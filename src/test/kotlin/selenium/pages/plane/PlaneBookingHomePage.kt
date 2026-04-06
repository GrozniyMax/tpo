package selenium.pages.plane

import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.BookingCookiePage
import selenium.pages.plane.searchResults.PlaneSearchResultsPage
import java.time.Duration
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

    /**
     * Скроллит элемент в видимую область
     */
    private fun scrollIntoView(element: WebElement) {
        try {
            (driver as? JavascriptExecutor)?.executeScript(
                "arguments[0].scrollIntoView(true);", element
            )
        } catch (e: Exception) {
            logger.warn { "Не удалось проскроллить к элементу: ${e.message}" }
        }
    }

    /**
     * Кликает по элементу с fallback на JavaScript
     */
    private fun clickElement(element: WebElement, description: String = "элемент") {
        try {
            scrollIntoView(element)
            element.click()
            logger.info { "Кликнули по $description (обычный клик)" }
        } catch (e: org.openqa.selenium.ElementClickInterceptedException) {
            logger.warn { "Не удалось кликнуть по $description обычным способом, используем JavaScript: ${e.message}" }
            (driver as? JavascriptExecutor)?.executeScript(
                "arguments[0].click();", element
            )
        } catch (e: org.openqa.selenium.ElementNotInteractableException) {
            logger.warn { "$description не интерактивен, используем JavaScript: ${e.message}" }
            (driver as? JavascriptExecutor)?.executeScript(
                "arguments[0].click();", element
            )
        }
    }

    fun declineRandom() {
        try {
            declineCookie()

            val xpath = "//button[@data-ui-name='input_location_from_segment_0']"
            val button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)))
            clickElement(button, "кнопку 'Откуда'")

            val declineCompleted = "//button[@data-autocomplete-chip-idx='0']"
            val declineButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(declineCompleted)))
            clickElement(declineButton, "кнопку отмены автокомплита")

            logger.info { "Отклонили автокомплит" }
        } catch (e: Exception) {
            logger.warn { "Не удалось отклонить автокомплит: ${e.message}" }
        }
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
        clickElement(button, "кнопку ввода места")

        // Ждём появления input поля
        val inputLocator = By.xpath("//input[@data-ui-name='input_text_autocomplete' or @placeholder]")
        val input = wait.until(ExpectedConditions.presenceOfElementLocated(inputLocator))

        // Очищаем и вводим текст
        input.sendKeys(keys)
        logger.info { "Ввод места: $keys" }
        
        // Небольшая пауза для появления подсказок
        try {
            Thread.sleep(500)
        } catch (e: Exception) {
            // Игнорируем
        }

        // Ждём появления dropdown - пробуем разные селекторы с увеличенным таймаутом
        val dropdownLocators = listOf(
            By.id("flights-searchbox_suggestions"),
            By.xpath("//ul[@role='listbox']"),
            By.xpath("//ul[contains(@role, 'listbox')]"),
            By.xpath("//div[contains(@class, 'autocomplete') or contains(@class, 'suggestion')]"),
            By.xpath("//div[contains(@id, 'autocomplete') or contains(@id, 'suggestion')]"),
            By.xpath("//ul[contains(@class, 'autocomplete')]"),
            By.xpath("//div[contains(@class, 'autocomplete-results')]")
        )

        var dropdown: WebElement? = null
        for (locator in dropdownLocators) {
            try {
                val tempWait = WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                dropdown = tempWait.until(ExpectedConditions.presenceOfElementLocated(locator))
                logger.info { "Нашли dropdown с селектором: $locator" }
                break
            } catch (e: Exception) {
                // Пробуем следующий локатор
            }
        }

        if (dropdown == null) {
            logger.warn { "Не удалось найти dropdown, пробуем найти подсказку напрямую" }
            // Пробуем найти подсказку напрямую
            val suggestionLocators = listOf(
                By.xpath("//li[@data-ui-name='locations_list_item']"),
                By.xpath("//li[contains(@role, 'option')]"),
                By.xpath("//div[contains(@class, 'autocomplete-item')]"),
                By.xpath("//span[contains(text(), '$keys')]"),
                By.xpath("//li[contains(., '$keys')]"),
                By.xpath("//div[contains(., '$keys')]")
            )

            for (locator in suggestionLocators) {
                try {
                    val tempWait = WebDriverWait(driver, java.time.Duration.ofSeconds(3))
                    val suggestion = tempWait.until(ExpectedConditions.elementToBeClickable(locator))
                    clickElement(suggestion, "подсказку (напрямую)")
                    logger.info { "Выбрана подсказка для: $keys" }
                    return
                } catch (e: Exception) {
                    // Пробуем следующий локатор
                }
            }
            logger.warn { "Не удалось найти подсказку для: $keys, продолжаем без выбора" }
            return
        }

        // Ищем подсказку внутри dropdown
        val suggestionLocators = listOf(
            By.xpath("./li[@data-ui-name='locations_list_item']"),
            By.xpath(".//li[contains(@role, 'option')]"),
            By.xpath(".//div[contains(@class, 'autocomplete-item')]"),
            By.xpath(".//li[contains(., '$keys')]"),
            By.xpath(".//span[contains(., '$keys')]")
        )

        var suggestion: WebElement? = null
        for (locator in suggestionLocators) {
            try {
                suggestion = dropdown.findElement(locator)
                wait.until(ExpectedConditions.elementToBeClickable(suggestion))
                logger.info { "Нашли подсказку с селектором: $locator" }
                break
            } catch (e: Exception) {
                // Пробуем следующий локатор
            }
        }

        if (suggestion != null) {
            clickElement(suggestion, "подсказку")
            logger.info { "Выбрана подсказка для: $keys" }
        } else {
            logger.warn { "Не удалось найти подсказку в dropdown для: $keys" }
        }
    }

    /**
     * Клик по кнопке поиска
     */
    fun clickSearch(): PlaneSearchResultsPage {
        val searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SUBMIT_BUTTON_XPATH)))
        clickElement(searchButton, "кнопку поиска")
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
