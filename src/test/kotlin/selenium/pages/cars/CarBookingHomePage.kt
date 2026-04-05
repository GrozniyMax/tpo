package selenium.pages.cars

import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.cars.searchResults.CarSearchResultsPage
import java.time.LocalDate

private val logger = LoggerFactory.getLogger(CarBookingHomePage::class.java)

class CarBookingHomePage(private val driver: WebDriver, private val wait: WebDriverWait) {

    companion object {
        private const val PICKUP_LOCATION_INPUT_XPATH =
            "//input[@id='pickup-location']"

        private const val PICKUP_DATE_BUTTON_XPATH =
            "//button[contains(., 'Выберите дату получения') or contains(., 'Pick-up date')]"

        private const val DROP_OFF_DATE_BUTTON_XPATH =
            "//button[contains(., 'Выберите дату возврата') or contains(., 'Drop-off date')]"

        private const val SEARCH_BUTTON_XPATH =
            "//button[contains(@class, 'submit-button') or contains(@type, 'submit') or .//span[contains(text(), 'Поиск') or contains(text(), 'Search')]]"

        private const val LOCATION_SUGGESTION_XPATH =
            "//div[contains(@class, 'autocomplete') or contains(@role, 'listbox')]//li[contains(@role, 'option')]"

        private const val PICKUP_TIME_SELECT_XPATH =
            "//select[@name='pickup-time'] | //div[contains(@class, 'pickup-time')]//select"

        private const val DROP_OFF_TIME_SELECT_XPATH =
            "//select[@name='drop-off-time'] | //div[contains(@class, 'drop-off-time')]//select"
    }

    /**
     * Проверка загрузки страницы
     */
    fun isLoaded(): Boolean {
        return try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(PICKUP_LOCATION_INPUT_XPATH))).isDisplayed
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Загрузка страницы
     */
    fun load() {
        driver.get("https://www.booking.com/cars/index.ru.html")
        isLoaded()
        logger.info { "Страница поиска автомобилей загружена" }
    }

    /**
     * Ввод места получения автомобиля
     */
    private fun enterPickupLocation(location: String) {
        val locationInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(PICKUP_LOCATION_INPUT_XPATH)))
        locationInput.clear()
        locationInput.sendKeys(location)
        logger.info { "Введено место получения: $location" }
    }

    /**
     * Выбор первой подсказки из автокомплита
     */
    private fun selectFirstSuggestion(): Boolean {
        return try {
            val suggestion = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(LOCATION_SUGGESTION_XPATH)))
            suggestion.click()
            logger.info { "Выбрана первая подсказка" }
            true
        } catch (e: Exception) {
            logger.info { "Подсказки не появились" }
            false
        }
    }

    /**
     * Клик по кнопке дат для открытия календаря (дата получения)
     */
    private fun openPickupDateCalendar() {
        val dateButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(PICKUP_DATE_BUTTON_XPATH)))
        dateButton.click()
        logger.info { "Календарь даты получения открыт" }
    }

    /**
     * Клик по кнопке даты возврата
     */
    private fun openDropOffDateCalendar() {
        val dateButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DROP_OFF_DATE_BUTTON_XPATH)))
        dateButton.click()
        logger.info { "Календарь даты возврата открыт" }
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
     * Заполнение диапазона дат (получение и возврат)
     */
    private fun fillDateRange(pickupDate: LocalDate, dropOffDate: LocalDate) {
        openPickupDateCalendar()
        selectDayFromCalendar(pickupDate)
        logger.info { "Выбрана дата получения: $pickupDate" }

        openDropOffDateCalendar()
        selectDayFromCalendar(dropOffDate)
        logger.info { "Выбрана дата возврата: $dropOffDate" }
    }

    /**
     * Клик по кнопке поиска
     */
    fun clickSearch(): CarSearchResultsPage {
        val searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(SEARCH_BUTTON_XPATH)))
        searchButton.click()
        logger.info { "Нажата кнопка поиска" }
        return CarSearchResultsPage(driver, wait)
    }

    /**
     * Полный сценарий поиска автомобиля
     */
    fun searchCar(
        pickupLocation: String,
        pickupDate: LocalDate,
        dropOffDate: LocalDate,
        driverAge: Int = 30
    ): CarSearchResultsPage {
        enterPickupLocation(pickupLocation)
        selectFirstSuggestion()
        logger.info { "Введено место получения: $pickupLocation" }

        fillDateRange(pickupDate, dropOffDate)
        logger.info { "Выбраны даты: $pickupDate - $dropOffDate" }

        if (driverAge > 0) {
            setDriverAge(driverAge)
            logger.info { "Выбран возраст водителя: $driverAge" }
        }

        return clickSearch()
    }

    /**
     * Установка возраста водителя
     */
    private fun setDriverAge(age: Int) {
        try {
            val ageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Возраст') or contains(., 'Driver age')]")))
            ageButton.click()
            logger.info { "Открыт селектор возраста" }

            // Находим и выбираем нужный возраст
            val ageOption = driver.findElement(By.xpath("//div[contains(@class, 'age-option') and text()='$age']"))
            ageOption.click()
            logger.info { "Выбран возраст: $age" }
        } catch (e: Exception) {
            logger.info { "Не удалось установить возраст водителя" }
        }
    }
}
