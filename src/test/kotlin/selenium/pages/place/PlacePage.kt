package selenium.pages.place

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.place.searchResults.PlaceSearchResultsPage

class PlacePage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val originalWindow: String,
    private val logger: Logger = LoggerFactory.getLogger(PlacePage::class.java)
) {


    fun getTitle(): String {
        val xpath = "//div[@id='hp_hotel_name']//h2"

        val element = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath))
        )

        val title = element.text
        logger.info { "Получили заголовок: $title" }
        return title
    }

    /**
     * Возвращает на предыдущую страницу (страницу результатов поиска)
     */
    fun goBack() {
        driver.close() // закрыть текущую вкладку
        driver.switchTo().window(originalWindow)
    }

    fun getProperties(): List<String> {
    val containerXpath = "//div[@data-testid='property-section--content']"

    // Ждём, пока контейнер станет видимым
    wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.xpath(containerXpath))
    )

    logger.info { "Контейнер с атрибутами загружен" }

    // Собираем тексты напрямую через XPath, без промежуточного сохранения контейнера
    val spanTexts = driver.findElements(By.xpath("$containerXpath//span"))
        .mapNotNull {
            it.text.trim().takeIf { text -> text.isNotEmpty() }
        }

    logger.info { "Получили ${spanTexts.size} элементов в span" }

    val divTexts = driver.findElements(By.xpath("$containerXpath//div"))
        .mapNotNull {
             it.text.trim().takeIf { text -> text.isNotEmpty() }
        }

    logger.info { "Получили ${divTexts.size} элементов в div" }

    return spanTexts + divTexts
}


}