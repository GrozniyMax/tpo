package selenium.pages.attractions.searchResults

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.BookingCookiePage

class AttractionSearchResultsPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(AttractionSearchResultsPage::class.java)
): BookingCookiePage(driver, wait) {

    private fun getAttractionCards(topN: Int = 5): List<WebElement> {
        val cardsXPath = "(//div[@data-testid='card'])[position() <= $topN]"

        wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[@data-testid='card']")
            )
        )
        logger.info { "Дождались появления карточек экскурсий" }

        val attractionCards = driver.findElements(By.xpath(cardsXPath))
        logger.info { "Нашли ${attractionCards.size} карточек экскурсий" }

        return attractionCards
    }

    /**
     * Получение списка экскурсий
     */
    fun getAttractions(topN: Int = 5): List<AttractionCard> {
        val attractionCards = getAttractionCards(topN)

        val attractions = attractionCards.map { card -> AttractionCardParser.parse(card) }
        logger.info { "Распарсили карточки экскурсий" }

        return attractions
    }

    /**
     * Применение сортировки
     */
    fun applySort(sort: String) {
        // The sort bar uses radio buttons with labels
        val sortOptionXPath = "//div[@data-testid='sort-bar']//label[contains(., '$sort')]"
        
        val sortOption = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath(sortOptionXPath)
            )
        )
        sortOption.click()
        logger.info { "Кликнули по опции сортировки $sort" }

        waitForResultsToUpdate()
    }

    /**
     * Применение фильтра
     */
    fun clickFilter(filterName: String) {
        val filterXPath = "//fieldset//label[contains(., 'Бесплатная отмена')]"

        val filterElement = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath(filterXPath)
            )
        )
        logger.info { "Нашли фильтр $filterName" }

        filterElement.click()
        logger.info { "Кликнули по фильтру $filterName" }

        waitForResultsToUpdate()
    }

    /**
     * Ожидает обновления результатов после применения фильтра
     */
    private fun waitForResultsToUpdate() {
        wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[@data-testid='card']")
            )
        )
        logger.info { "Результаты обновились" }
    }
}
