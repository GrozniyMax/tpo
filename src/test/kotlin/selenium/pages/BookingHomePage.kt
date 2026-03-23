package selenium.pages

import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.sql.Date
import java.time.LocalDate

private val logger = LoggerFactory.getLogger(BookingHomePage::class.java)

class BookingHomePage(private val driver: WebDriver, private val wait: WebDriverWait) {

    companion object {
        private const val PLACE_INPUT_XPATH =
            "//input[@name='ss' and contains(@placeholder, 'Куда') or contains(@placeholder, 'Where')]"

        // ==================== ДАТЫ ====================
        // Кнопка открытия календаря (контейнер с датами)
        private const val DATE_BUTTON_XPATH = "//button[@data-testid='searchbox-dates-container']"

        private const val SUBMIT_BUTTON_XPATH =
            "/html/body/div[1]/div/div[2]/div/main/div[1]/div/div/div/div/div/div/div/div[2]/div/div/form/div/div[4]/button"

        private const val GUESTS_BUTTON_XPATH = "//button[@data-testid='occupancy-config']"

        private const val LOG_IN_XPATH = "//button[contains(@aria-label, 'Войти') or contains(@aria-label, 'Sign in')]"

        private const val DECLINE_COOKIE_XPATH =
            "//button[contains(@class, 'reject') or contains(@class, 'deny') or .//span[contains(text(), 'Отклонить') or contains(text(), 'Reject') or contains(text(), 'Decline')]]"

        private const val SUGGESTION_XPATH =
            "//div[contains(@class, 'autocomplete')]//li[contains(@class, 'result') or contains(@role, 'option')]"
    }

    /**
     * Проверка загрузки страницы
     */
    fun isLoaded(): Boolean {
        return try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(PLACE_INPUT_XPATH))).isDisplayed
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Закрытие модального окна входа
     */
    private fun declineLogIn() {
        try {
            val declineButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(LOG_IN_XPATH)))
            declineButton.click()
        } catch (e: Exception) {
            // Кнопка может отсутствовать
        }
    }

    /**
     * Отказ от cookies
     */
    private fun declineCookie() {
        try {
            val declineButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DECLINE_COOKIE_XPATH)))
            declineButton.click()
        } catch (e: Exception) {
            // Кнопка может отсутствовать
        }
    }

    /**
     * Загрузка страницы и обработка модальных окон
     */
    fun load() {
        driver.get("https://www.booking.com")
        isLoaded()
        declineLogIn()
        declineCookie()
        logger.info { "Страница загружена" }
    }

    /**
     * Ввод направления
     */
    fun enterPlace(place: String) {
        val placeInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(PLACE_INPUT_XPATH)))
        placeInput.clear()
        placeInput.sendKeys(place)
    }

    /**
     * Выбор первой подсказки из автокомплита
     */
    fun selectFirstSuggestion() {
        try {
            val suggestion = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SUGGESTION_XPATH)))
            suggestion.click()
        } catch (e: Exception) {
            // Подсказки могут не появиться
        }
    }

    /**
     * Клик по кнопке поиска
     */
    fun clickSearch(): SearchResultsPage {
        val searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SUBMIT_BUTTON_XPATH)))
        searchButton.click()
        return SearchResultsPage(driver, wait)
    }

    /**
     * Клик по кнопке дат для открытия календаря
     */
    private fun openCalendar() {
        val dateButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DATE_BUTTON_XPATH)))
        dateButton.click()
    }

    /**
     * Выбор дня из открытого календаря
     */
    private fun selectDayFromCalendar(date: LocalDate) {
        val xpath = "//span[@data-date='${date}']"
        val suggestedDay = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)))
        suggestedDay.click()
    }

    /**
     * Заполнение диапазона дат (заезд и выезд)
     * @param checkInDay день заезда (число месяца)
     * @param checkOutDay день выезда (число месяца)
     */
    private fun fillDateRange(checkInDay: LocalDate, checkOutDay: LocalDate) {
        // Открываем календарь
        openCalendar()

        // Выбираем дату заезда
        selectDayFromCalendar(checkInDay)

        // Выбираем дату выезда (календарь остаётся открытым после выбора заезда)
        selectDayFromCalendar(checkOutDay)

    }


    /**
     * Открытие селектора гостей
     */
    private fun openGuestsSelector() {
        try {
            val guestsButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(GUESTS_BUTTON_XPATH)))
            guestsButton.click()
        } catch (e: Exception) {
            // Селектор может быть уже открыт
        }
    }

    /**
     * Установка количества взрослых
     */
    private fun setAdults(count: Int) {
        adjustGuestCount(count, "adult")
    }

    /**
     * Установка количества детей
     */
    fun setChildren(count: Int) {
        adjustGuestCount(count, "child")
    }

    /**
     * Регулировка счётчика гостей
     */
    private fun adjustGuestCount(targetCount: Int, type: String) {
        try {
            val incrementXpath =
                "//button[contains(@aria-label, 'increase') or contains(@aria-label, 'add')][contains(@aria-label, '$type')]//span[text()='+']"
            val buttons = driver.findElements(By.xpath(incrementXpath))
            if (buttons.isNotEmpty()) {
                repeat(kotlin.math.min(targetCount, 3)) {
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
            // Игнорируем, если не нашли кнопки
        }
    }

    /**
     * Закрытие селектора гостей кликом вне
     */
    private fun closeGuestsSelector() {
        driver.findElement(By.xpath("//body")).click()
    }

    /**
     * Полный сценарий поиска жилья
     * @param place направление (город, отель)
     * @param checkInDay день заезда
     * @param checkOutDay день выезда
     * @param adults количество взрослых
     */
    fun searchAccommodation(place: String, checkInDay: LocalDate, checkOutDay: LocalDate, adults: Int = 2): SearchResultsPage {
        enterPlace(place)
        logger.info { "Введено направление: $place" }
        selectFirstSuggestion()
        logger.info { "Выбрана первая подсказка" }

        // Выбираем даты
        fillDateRange(checkInDay, checkOutDay)
        logger.info { "Выбраны даты: $checkInDay - $checkOutDay" }

        // Выбираем количество гостей
        if (adults > 0) {
            openGuestsSelector()
            setAdults(adults)
            closeGuestsSelector()
        }
        logger.info { "Выбрано количество взрослых: $adults" }

        val page = clickSearch()
        logger.info { "Нажата кнопка поиска" }
        return page
    }


    /**
     * Полный сценарий поиска жилья
     * @param place направление (город, отель)
     * @param checkInDay день заезда
     * @param checkOutDay день выезда
     * @param adults количество взрослых
     */
    fun searchAccommodation(place: String, adults: Int = 2):SearchResultsPage {
        enterPlace(place)
        logger.info { "Введено направление: $place" }
        selectFirstSuggestion()
        logger.info { "Выбрана первая подсказка" }

        if (adults > 0) {
            openGuestsSelector()
            setAdults(adults)
            closeGuestsSelector()
        }
        logger.info { "Выбрано количество взрослых: $adults" }

        val page = clickSearch()
        logger.info { "Нажата кнопка поиска" }
        return page
    }

}
