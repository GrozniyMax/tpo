package selenium.pages.plane.searchResults

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.plane.PlaneFlightPage

class PlaneSearchResultsPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(PlaneSearchResultsPage::class.java)
) {

    private fun getFlightCards(topN: Int = 5): List<WebElement> {
        val cardsXPath = "(//div[contains(@data-testid, 'card') or contains(@class, 'flight-card')])[position() <= $topN]"

        wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@data-testid, 'card') or contains(@class, 'flight-card')]")
            )
        )
        logger.info { "Дождались появления карточек рейсов" }

        val flightCards = driver.findElements(By.xpath(cardsXPath))
        logger.info { "Нашли ${flightCards.size} карточек рейсов" }

        return flightCards
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
     * Получение списка рейсов
     */
    fun getFlights(topN: Int = 5): List<FlightCard> {
        val flightCards = getFlightCards(topN)

        val cards = flightCards.map { card -> FlightCardParser.parse(card) }
        logger.info { "Распарсили карточки рейсов" }

        return cards
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
     * Выбор рейса по индексу
     */
    fun select(index: Int): PlaneFlightPage {
        val flightCards = getFlightCards(index + 1)

        val card = flightCards[index]
        logger.info { "Выбрали карточку рейса с индексом $index" }

        card.click()
        logger.info { "Кликнули по карточке рейса" }

        waitForResultsToUpdate()
        logger.info { "Дождались обновления результатов" }

        return PlaneFlightPage(driver, wait)
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
                By.xpath("//div[contains(@data-testid, 'card') or contains(@class, 'flight-card')]")
            )
        )
        logger.info { "Результаты обновились" }
    }
}

/**
 * Модель карточки рейса
 */
data class FlightCard(
    val airline: String?,
    val departureTime: String?,
    val arrivalTime: String?,
    val duration: String?,
    val price: String?
)

/**
 * Парсер карточек рейсов
 */
object FlightCardParser {
    fun parse(card: WebElement): FlightCard {
        return FlightCard(
            airline = try {
                card.findElement(By.xpath(".//span[contains(@class, 'airline') or contains(@class, 'carrier')]")).text
            } catch (e: Exception) {
                null
            },
            departureTime = try {
                card.findElement(By.xpath(".//span[contains(@class, 'departure-time')]")).text
            } catch (e: Exception) {
                null
            },
            arrivalTime = try {
                card.findElement(By.xpath(".//span[contains(@class, 'arrival-time')]")).text
            } catch (e: Exception) {
                null
            },
            duration = try {
                card.findElement(By.xpath(".//span[contains(@class, 'duration')]")).text
            } catch (e: Exception) {
                null
            },
            price = try {
                card.findElement(By.xpath(".//span[contains(@class, 'price')]")).text
            } catch (e: Exception) {
                null
            }
        )
    }
}
