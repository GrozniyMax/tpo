package selenium.pages.searchResults

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait

class SearchResultsPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(SearchResultsPage::class.java)
) {

    fun getSuggestions(topN: Int = 5): List<PropertyCard> {

        val cardsXPath = "(//div[@role=\"list\"]//div[@data-testid=\"property-card\"])[position() <= $topN]"

        val productCards = driver.findElements(By.xpath(cardsXPath))
        logger.info { "Нашли карточки" }

        val cards = productCards.map { card -> PropertyCardParser.parse(card) }
        logger.info { "Парсили карточки" }

        return cards
    }

    fun clickFilter(filterGroup: String, filterName: String) {
        val filtersContainer = "//div[@data-testid='filters-group'][@data-filters-group='$filterGroup']"
        val filter = ".//label[contains(., '$filterName') and .//input[@type='checkbox']]"

        val container = driver.findElement(By.xpath(filtersContainer))
        logger.info { "Нашли группу фильтров $filterGroup" }

        val filterElement = container.findElement(By.xpath(filter))
        logger.info { "Нашли фильтр $filterName" }

        filterElement.click()
        logger.info { "Кликнули по фильтру $filterName" }
    }

}