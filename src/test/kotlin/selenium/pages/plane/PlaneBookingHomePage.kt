package selenium.pages.plane

import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.plane.searchResults.PlaneSearchResultsPage
import java.time.LocalDate

private val logger = LoggerFactory.getLogger(PlaneBookingHomePage::class.java)

class PlaneBookingHomePage(private val driver: WebDriver, private val wait: WebDriverWait) {

    companion object {
        private const val FROM_BUTTON_XPATH =
            "//button[contains(., 'Откуда') or contains(., 'From')]"

        private const val TO_BUTTON_XPATH =
            "//button[contains(., 'Куда') or contains(., 'To')]"

        private const val DATE_BUTTON_XPATH =
            "//button[contains(., 'Даты поездки') or contains(., 'Dates')]"

        private const val PASSENGERS_BUTTON_XPATH =
            "//button[contains(., 'Пассажиры') or contains(., 'Passengers')]"

        private const val SUBMIT_BUTTON_XPATH =
            "//button[contains(., 'Найти') or contains(., 'Search')]"

        private const val SUGGESTION_XPATH =
            "//div[contains(@role, 'listbox')]//li[contains(@role, 'option')]"

        private const val ROUND_TRIP_RADIO_XPATH =
            "//radio[contains(., 'Туда и обратно') or contains(., 'Round trip')]"

        private const val ONE_WAY_RADIO_XPATH =
            "//radio[contains(., 'В одну сторону') or contains(., 'One way')]"

        private const val CABIN_CLASS_SELECT_XPATH =
            "//div[contains(@role, 'menu') and @aria-label='Салон'] | //select[contains(@aria-label, 'Салон')]"
    }

    /**
     * Проверка загрузки страницы
     */
    fun isLoaded(): Boolean {
        return try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(FROM_BUTTON_XPATH))).isDisplayed
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Загрузка страницы
     */
    fun load() {
        driver.get("https://www.booking.com/flights/index.ru.html")
        isLoaded()
        logger.info { "Страница поиска авиабилетов загружена" }
    }

    /**
     * Ввод пункта отправления
     */
    private fun enterFrom(from: String) {
        val fromButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(FROM_BUTTON_XPATH)))
        fromButton.click()
        logger.info { "Открыто поле ввода места отправления" }
        
        // Вводим текст в активный элемент
        driver.switchTo().activeElement().sendKeys(from)
        logger.info { "Введено место отправления: $from" }
    }

    /**
     * Ввод пункта назначения
     */
    private fun enterTo(to: String) {
        val toButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(TO_BUTTON_XPATH)))
        toButton.click()
        logger.info { "Открыто поле ввода места назначения" }
        
        // Вводим текст в активный элемент
        driver.switchTo().activeElement().sendKeys(to)
        logger.info { "Введено место назначения: $to" }
    }

    /**
     * Выбор первой подсказки из автокомплита
     */
    private fun selectFirstSuggestion(): Boolean {
        return try {
            val suggestion = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SUGGESTION_XPATH)))
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
     * Заполнение диапазона дат (вылет и возврат)
     */
    private fun fillDateRange(departureDate: LocalDate, returnDate: LocalDate) {
        openCalendar()
        selectDayFromCalendar(departureDate)
        logger.info { "Выбрана дата вылета: $departureDate" }

        selectDayFromCalendar(returnDate)
        logger.info { "Выбрана дата возврата: $returnDate" }
    }

    /**
     * Заполнение даты только в одну сторону
     */
    private fun fillOneWayDate(departureDate: LocalDate) {
        openCalendar()
        selectDayFromCalendar(departureDate)
        logger.info { "Выбрана дата вылета: $departureDate" }
    }

    /**
     * Открытие селектора пассажиров
     */
    private fun openPassengersSelector() {
        try {
            val passengersButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(PASSENGERS_BUTTON_XPATH)))
            passengersButton.click()
            logger.info { "Открыт селектор пассажиров" }
        } catch (e: Exception) {
            logger.info { "Селектор пассажиров уже открыт или не найден" }
        }
    }

    /**
     * Установка количества взрослых
     */
    private fun setAdults(count: Int) {
        adjustPassengerCount(count, "adult")
    }

    /**
     * Установка количества детей
     */
    fun setChildren(count: Int) {
        adjustPassengerCount(count, "child")
    }

    /**
     * Регулировка счётчика пассажиров
     */
    private fun adjustPassengerCount(targetCount: Int, type: String) {
        try {
            val incrementXpath =
                "//button[contains(@aria-label, 'increase') or contains(@aria-label, 'add')][contains(@aria-label, '$type')]//span[text()='+']"
            val buttons = driver.findElements(By.xpath(incrementXpath))
            if (buttons.isNotEmpty()) {
                repeat(kotlin.math.min(targetCount, 9)) {
                    try {
                        if (buttons.first().isDisplayed) {
                            buttons.first().click()
                        }
                    } catch (e: Exception) {
                        // Игнорируем
                    }
                }
            }
        } catch (e: Exception) {
            logger.info { "Не удалось настроить количество пассажиров типа $type" }
        }
    }

    /**
     * Закрытие селектора пассажиров кликом вне
     */
    private fun closePassengersSelector() {
        try {
            driver.findElement(By.xpath("//body")).click()
            logger.info { "Селектор пассажиров закрыт" }
        } catch (e: Exception) {
            // Игнорируем
        }
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
        to: String,
        departureDate: LocalDate,
        returnDate: LocalDate,
        adults: Int = 2
    ): PlaneSearchResultsPage {
        enterFrom(from)
        selectFirstSuggestion()

        enterTo(to)
        selectFirstSuggestion()

        fillDateRange(departureDate, returnDate)

        if (adults > 0) {
            openPassengersSelector()
            setAdults(adults)
            closePassengersSelector()
        }
        logger.info { "Выбрано количество взрослых: $adults" }

        return clickSearch()
    }

    /**
     * Полный сценарий поиска авиабилетов (в одну сторону)
     */
    fun searchOneWayFlight(
        from: String,
        to: String,
        departureDate: LocalDate,
        adults: Int = 2
    ): PlaneSearchResultsPage {
        enterFrom(from)
        selectFirstSuggestion()

        enterTo(to)
        selectFirstSuggestion()

        fillOneWayDate(departureDate)

        if (adults > 0) {
            openPassengersSelector()
            setAdults(adults)
            closePassengersSelector()
        }
        logger.info { "Выбрано количество взрослых: $adults" }

        return clickSearch()
    }
}
