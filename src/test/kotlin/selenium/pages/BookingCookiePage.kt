package selenium.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

/**
 * PageObject для обработки диалогов cookie и регистрации на Booking.com
 * Использует только XPATH локаторы
 */
class BookingCookiePage(private val driver: WebDriver, private val wait: WebDriverWait) {

    companion object {
        // Кнопка "Отклонить" в диалоге cookie
        private const val DECLINE_COOKIE_XPATH =
            "//button[contains(@class, 'bui-btn') and .//span[normalize-space(text())='Отклонить']] | //button[contains(text(), 'Отклонить')]"

        // Кнопка "Принять" в диалоге cookie
        private const val ACCEPT_COOKIE_XPATH =
            "//button[contains(@class, 'bui-btn') and .//span[normalize-space(text())='Принять']]"

        // Кнопка "Изменить настройки" в диалоге cookie
        private const val SETTINGS_COOKIE_XPATH =
            "//button[contains(@class, 'bui-btn') and .//span[normalize-space(text())='Изменить настройки']]"

        // Контейнер диалога cookie (регион с фокусом)
        private const val COOKIE_DIALOG_XPATH =
            "//div[contains(@role, 'dialog') or contains(@aria-labelledby)] | //region[contains(@role, 'region') and contains(., 'cookie')]"

        // ==================== REGISTRATION DIALOG ====================
        // Кнопка закрытия диалога регистрации/входа
        private const val CLOSE_LOGIN_DIALOG_XPATH =
            "//button[@aria-label='Скрыть меню входа в аккаунт.']"

        // Кнопка входа в хедере
        private const val HEADER_LOGIN_BUTTON_XPATH =
            "//a[contains(@href, 'login') or contains(@href, 'auth')]//span[contains(text(), 'Войти') or contains(text(), 'Зарегистрироваться')]"

        // ==================== PAGE LOAD INDICATORS ====================
        // Основной элемент для проверки загрузки страницы
        private const val PAGE_LOADED_INDICATOR_XPATH =
            "//main"

        // Заголовок страницы
        private const val PAGE_TITLE_XPATH =
            "//h1[contains(text(), 'Найдите жилье') or contains(text(), 'Find')]"

        // Логотип Booking.com
        private const val LOGO_XPATH =
            "//a[contains(@href, 'booking.com') and contains(@aria-label, 'Booking') or @class*='logo']"
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

    fun loadPage() {
        driver.get("https://www.booking.com")
    }
}
