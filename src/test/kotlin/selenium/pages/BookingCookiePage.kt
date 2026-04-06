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

        // OneTrust cookie banner (используется на Booking.com)
        private const val ONETRUST_REJECT_XPATH =
            "//button[contains(@id, 'onetrust-reject') or contains(@class, 'ot-sdk-btn') and contains(text(), 'Отклонить') or contains(text(), 'Reject')]"

        // Альтернативные селекторы для cookie баннера
        private const val COOKIE_BANNER_ALTERNATIVES_XPATH =
            "//button[contains(text(), 'Отклонить все') or contains(text(), 'Reject all') or contains(@aria-label, 'cookie') or contains(@aria-label, 'cookies')]"

        // OneTrust banner container
        private const val ONETRUST_BANNER_XPATH =
            "//div[contains(@id, 'onetrust-banner-sdk')]"

        private const val CLOSE_LOGIN_DIALOG_XPATH =
            "//button[@aria-label='Скрыть меню входа в аккаунт.']"

    }


    fun declineCookie() {
        // Пробуем разные варианты закрытия cookie баннера
        val xpathVariants = listOf(
            DECLINE_COOKIE_XPATH,
            ONETRUST_REJECT_XPATH,
            COOKIE_BANNER_ALTERNATIVES_XPATH
        )

        for (xpath in xpathVariants) {
            try {
                val element = driver.findElement(By.xpath(xpath))
                if (element.isDisplayed) {
                    element.click()
                    logger.info { "Отклонили cookie (использован селектор: $xpath)" }
                    return
                }
            } catch (e: Exception) {
                // Пробуем следующий вариант
            }
        }

        logger.warn { "Не удалось закрыть диалог cookie: ни один из селекторов не сработал" }
    }

    /**
     * Проверяет наличие cookie баннера и закрывает его если есть
     */
    fun tryCloseCookieBanner(): Boolean {
        try {
            // Проверяем наличие OneTrust баннера
            val banner = driver.findElement(By.xpath(ONETRUST_BANNER_XPATH))
            if (banner.isDisplayed) {
                declineCookie()
                return true
            }
        } catch (e: Exception) {
            // Баннера нет
        }
        return false
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
