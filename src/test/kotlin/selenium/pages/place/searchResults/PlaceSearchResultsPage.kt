package selenium.pages.place.searchResults

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.place.PlacePage

class PlaceSearchResultsPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(PlaceSearchResultsPage::class.java)
) {

    private fun getProductCards(topN: Int = 5): List<WebElement> {
        val cardsXPath = "(//div[@role=\"list\"]//div[@data-testid=\"property-card\"])[position() <= $topN]"

        wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[@data-testid='property-card']")
            )
        )
        logger.info { "Дождались появления карточек" }

        val productCards = driver.findElements(By.xpath(cardsXPath))
        logger.info { "Нашли ${productCards.size} карточек" }

        return productCards
    }

    fun hasNoResults(): Boolean {
        try {
            driver.findElement(By.xpath("//div[@data-testid='properties-list-empty-state']"))
            return true
        } catch (e: NoSuchElementException) {
            return false
        }
    }

    fun getSuggestions(topN: Int = 5): List<PropertyCard> {
        val productCards = getProductCards(topN)

        val cards = productCards.map { card -> PropertyCardParser.parse(card) }
        logger.info { "Распарсили карточки" }

        return cards
    }

    fun applySort(sort: String) {
        val sortButton = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@data-testid='sorters-dropdown-trigger']")
            )
        )
        sortButton.click()
        logger.info { "Кликнули по кнопке сортировки" }

        val sortContainer = driver.findElement(
            By.xpath("//div[@data-testid='sorters-dropdown']")
        )

        logger.info { "Нашли контейнер с сортировкой" }

        val sortOption = sortContainer.findElement(
            By.xpath(".//div[contains(., '$sort')]")
        )

        logger.info { "Нашли опцию сортировки $sort" }

        sortOption.click()
        logger.info { "Кликнули по опции сортировки $sort" }
    }

    fun select(index: Int): PlacePage {
        val productCards = getProductCards(index + 1)

        val card = productCards[index]
        logger.info { "Выбрали карточку с индексом $index" }

        card.click()
        logger.info { "Кликнули по карточке" }

        waitForResultsToUpdate()
        logger.info { "Дождались обновления результатов" }

        return PlacePage(driver, wait)
    }

    fun clickFilter(filterGroup: String, filterName: String) {
        val filtersContainerXPath = "//div[@data-testid='filters-group'][@data-filters-group='$filterGroup']"
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
                By.xpath("//div[@data-testid='property-card']")
            )
        )
        logger.info { "Результаты обновились" }
    }
}