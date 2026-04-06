package selenium.pages.attractions

import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.BookingCookiePage
import selenium.pages.attractions.searchResults.AttractionSearchResultsPage
import java.time.Duration
import java.time.LocalDate

private val logger = LoggerFactory.getLogger(AttractionBookingHomePage::class.java)

class AttractionBookingHomePage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val cookiePage: BookingCookiePage = BookingCookiePage(driver, wait)
) {

    companion object {
        private const val DESTINATION_INPUT_XPATH =
            "//input[@data-testid='search-input-field']"

        private const val DATE_BUTTON_XPATH =
            "//button[contains(@data-testid, 'date') or contains(., 'Выберите даты')]"

        private const val SEARCH_BUTTON_XPATH =
            "//button[@data-testid='search-button']"

        private const val DESTINATION_SUGGESTION_XPATH =
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

    /**
     * Проверка и закрытие cookie баннера
     */
    private fun tryCloseCookieBanner() {
        try {
            cookiePage.tryCloseCookieBanner()
        } catch (e: Exception) {
            logger.warn { "Не удалось закрыть cookie баннер: ${e.message}" }
        }
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
        tryCloseCookieBanner()
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
        
        // Ждём появления подсказок (не блокирующе)
        try {
            val shortWait = WebDriverWait(driver, Duration.ofSeconds(3))
            shortWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(DESTINATION_SUGGESTION_XPATH)))
            logger.info { "Подсказки появились" }
        } catch (e: Exception) {
            logger.info { "Подсказки не появились, продолжаем без выбора" }
        }
    }

    /**
     * Выбор первой подсказки из автокомплита
     */
    private fun selectFirstSuggestion(): Boolean {
        return try {
            val suggestion = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DESTINATION_SUGGESTION_XPATH)))
            clickElement(suggestion, "подсказку")
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
        // Пробуем разные варианты селекторов для кнопки дат
        val dateXpaths = listOf(
            "//button[contains(@data-testid, 'date') or contains(., 'Выберите даты')]",
            "//button[contains(., 'Даты')]",
            "//input[@type='date']",
            "//*[contains(@class, 'date-picker')]"
        )
        
        var dateButton: WebElement? = null
        for (xpath in dateXpaths) {
            try {
                dateButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)))
                logger.info { "Нашли кнопку дат с селектором: $xpath" }
                break
            } catch (e: Exception) {
                // Пробуем следующий селектор
            }
        }
        
        if (dateButton != null) {
            clickElement(dateButton, "кнопку дат")
            logger.info { "Календарь открыт" }
        } else {
            logger.warn { "Не удалось найти кнопку дат" }
        }
    }

    /**
     * Выбор дня из открытого календаря
     */
    private fun selectDayFromCalendar(date: LocalDate) {
        // Закрываем cookie баннер если он есть перед выбором даты
        tryCloseCookieBanner()
        
        // Пробуем найти дату с несколькими вариантами селекторов
        val dateXpaths = listOf(
            "//span[@data-date='${date}']",
            "//button[@data-date='${date}']",
            "//div[@data-date='${date}']"
        )
        
        var dateElement: WebElement? = null
        for (xpath in dateXpaths) {
            try {
                dateElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)))
                logger.info { "Нашли дату с селектором: $xpath" }
                break
            } catch (e: Exception) {
                // Пробуем следующий селектор
            }
        }
        
        if (dateElement != null) {
            clickElement(dateElement, "дату в календаре")
            logger.info { "Выбрана дата: $date" }
        } else {
            logger.warn { "Не удалось найти дату: $date" }
        }
    }

    /**
     * Клик по кнопке поиска
     */
    fun clickSearch(): AttractionSearchResultsPage {
        val searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SEARCH_BUTTON_XPATH)))
        clickElement(searchButton, "кнопку поиска")
        logger.info { "Нажата кнопка поиска" }
        
        // Ждём загрузки страницы результатов
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@data-testid='card']")))
            logger.info { "Страница результатов загружена" }
        } catch (e: Exception) {
            logger.warn { "Карточки не появились, возможно ошибка валидации" }
        }
        
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

}
