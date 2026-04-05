package selenium.pages.attractions.searchResults

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.attractions.AttractionDetailsPage

class AttractionSearchResultsPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(AttractionSearchResultsPage::class.java)
) {

    private fun getAttractionCards(topN: Int = 5): List<WebElement> {
        val cardsXPath = "(//div[contains(@data-testid, 'attraction-card') or contains(@class, 'attraction-card') or contains(@class, 'tour-card')])[position() <= $topN]"

        wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@data-testid, 'attraction-card') or contains(@class, 'attraction-card') or contains(@class, 'tour-card')]")
            )
        )
        logger.info { "Дождались появления карточек экскурсий" }

        val attractionCards = driver.findElements(By.xpath(cardsXPath))
        logger.info { "Нашли ${attractionCards.size} карточек экскурсий" }

        return attractionCards
    }

    /**
     * Проверка, что результатов нет
     */
    fun hasNoResults(): Boolean {
        try {
            driver.findElement(By.xpath("//div[contains(@data-testid, 'empty-state') or contains(@class, 'no-results')]"))
            return true
        } catch (e: NoSuchElementException) {
            return false
        }
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
        val sortButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@data-testid, 'sort') or contains(@aria-label, 'sort')]")
            )
        )
        sortButton.click()
        logger.info { "Кликнули по кнопке сортировки" }

        val sortContainer = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@data-testid, 'sorters-dropdown') or contains(@class, 'sort-dropdown')]")
            )
        )

        logger.info { "Нашли контейнер с сортировкой" }

        val sortOption = sortContainer.findElement(
            By.xpath(".//div[contains(., '$sort')]")
        )

        logger.info { "Нашли опцию сортировки $sort" }

        sortOption.click()
        logger.info { "Кликнули по опции сортировки $sort" }
    }

    /**
     * Выбор экскурсии по индексу
     */
    fun select(index: Int): AttractionDetailsPage {
        val attractionCards = getAttractionCards(index + 1)

        val card = attractionCards[index]
        logger.info { "Выбрали карточку экскурсии с индексом $index" }

        card.click()
        logger.info { "Кликнули по карточке экскурсии" }

        waitForResultsToUpdate()
        logger.info { "Дождались обновления результатов" }

        return AttractionDetailsPage(driver, wait)
    }

    /**
     * Применение фильтра
     */
    fun clickFilter(filterGroup: String, filterName: String) {
        val filtersContainerXPath = "//div[contains(@data-testid, 'filters-group') or contains(@data-filters-group, '$filterGroup')]"
        val filterXPath = ".//label[contains(., '$filterName') and .//input[@type='checkbox']]"

        val container = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath(filtersContainerXPath)
            )
        )
        logger.info { "Нашли группу фильтров $filterGroup" }

        val filterElement = wait.until(
            ExpectedConditions.elementToBeClickable(
                container.findElement(By.xpath(filterXPath))
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
                By.xpath("//div[contains(@data-testid, 'attraction-card') or contains(@class, 'attraction-card') or contains(@class, 'tour-card')]")
            )
        )
        logger.info { "Результаты обновились" }
    }
}
