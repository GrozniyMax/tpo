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
class BookingCookiePage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(BookingCookiePage::class.java)) {

    companion object {
        // Кнопка "Отклонить" в диалоге cookie
        private const val DECLINE_COOKIE_XPATH =
            "//button[contains(@class, 'bui-btn') and .//span[normalize-space(text())='Отклонить']] | //button[contains(text(), 'Отклонить')]"

        private const val CLOSE_LOGIN_DIALOG_XPATH =
            "//button[@aria-label='Скрыть меню входа в аккаунт.']"

        private const val PAGE_LOADED_INDICATOR_XPATH =
            "//main"
    }

    /**
     * Проверка загрузки основной страницы
     */
    fun isPageLoaded(): Boolean {
        return try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(PAGE_LOADED_INDICATOR_XPATH))).isDisplayed
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Проверка наличия диалога cookie
     */
    fun isCookieDialogPresent(): Boolean {
        return try {
            val cookieDialog = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(DECLINE_COOKIE_XPATH)))
            cookieDialog.isDisplayed
        } catch (e: Exception) {
            false
        }
    }

    fun declineCookie() {

        val declineButton = wait.until { driver ->
            driver.findElement(By.xpath(DECLINE_COOKIE_XPATH)).takeIf { it.isDisplayed }
        }!!
        declineButton.click()

    }

    fun closeLoginDialog() {
        val closeButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(CLOSE_LOGIN_DIALOG_XPATH)))
        closeButton.click()
    }

    /**
     * Полная обработка страницы: ожидание загрузки, отказ от cookie и закрытие диалога регистрации
     */
    fun handlePageLoadAndDialogs() {
        // Ждем загрузки страницы по основному элементу
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(PAGE_LOADED_INDICATOR_XPATH)))

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

    fun navigateToPlanesPage(): PlaneBookingHomePage {
        val navContainer = getNavContainer()

        val element = navContainer.findElement(By.id("flights"))

        logger.info { "Получаем элемент с id flights" }

        element.click()

        return PlaneBookingHomePage(driver, wait)
    }

    fun navigateToCarsPage(): CarBookingHomePage {
        val navContainer = getNavContainer()

        val element = navContainer.findElement(By.id("cars"))

        logger.info { "Получаем элемент с id cars" }

        element.click()

        return CarBookingHomePage(driver, wait)
    }

    private fun getNavContainer(): WebElement {
        driver.get("https://www.booking.com/planes")
        handlePageLoadAndDialogs()

        val navContainerXPath = "//nav[@data-testid='header-xpb']"

        val navContainer = wait.until { driver ->
            driver.findElement(By.xpath(navContainerXPath))
        }
        logger.info { "Получаем контейнер с навигацией" }
        return navContainer
    }

    fun loadPage() {
        driver.get("https://www.booking.com")
    }

}
