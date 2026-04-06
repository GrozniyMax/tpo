package selenium.pages

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.place.BookingHomePage
import selenium.pages.plane.PlaneBookingHomePage

/**
 * PageObject для обработки диалогов cookie и регистрации на Booking.com
 * Использует только XPATH локаторы
 */
open class BookingCookiePage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(BookingCookiePage::class.java)
) {

    companion object {
        // Кнопка "Отклонить" в диалоге cookie
        private const val DECLINE_COOKIE_XPATH =
            "//button[contains(@class, 'bui-btn') and .//span[normalize-space(text())='Отклонить']] | //button[contains(text(), 'Отклонить')]"

        private const val CLOSE_LOGIN_DIALOG_XPATH =
            "//button[@aria-label='Скрыть меню входа в аккаунт.']"

    }


    fun declineCookie() {

        try {
            driver.findElement(By.xpath(DECLINE_COOKIE_XPATH)).click()
            logger.info { "Отклонили cookie" }
        } catch (e: Exception) {
            logger.warn { "Не удалось закрыть диалог cookie: ${e.message}" }
        }

    }

    fun closeLoginDialog() {
        try {
            driver.findElement(By.xpath(CLOSE_LOGIN_DIALOG_XPATH)).click()
            logger.info { "Закрыли диалог входа" }
        } catch (e: Exception) {
            logger.warn { "Не удалось закрыть диалог входа: ${e.message}" }
        }
    }

    /**
     * Полная обработка страницы: ожидание загрузки, отказ от cookie и закрытие диалога регистрации
     */
    fun handlePageLoadAndDialogs() {

        closeLoginDialog()
        declineCookie()
    }

    /**
     * Переход на главную страницу и обработка всех диалогов
     */
    fun navigateToBooking(): BookingHomePage {
        driver.get("https://www.booking.com")
        handlePageLoadAndDialogs()
        return BookingHomePage(driver, wait)
    }

}
