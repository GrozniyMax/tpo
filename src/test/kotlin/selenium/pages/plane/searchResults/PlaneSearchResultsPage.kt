package selenium.pages.plane.searchResults

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.BookingCookiePage

class PlaneSearchResultsPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(PlaneSearchResultsPage::class.java)
) : BookingCookiePage(driver, wait) {

    companion object {
        // Упрощенные XPath выражения
        private const val FLIGHT_CARD_XPATH = "//div[@data-testid='searchresults_card']"
        private const val NO_RESULTS_XPATH =
            "//*[contains(., 'нет рейсов') or contains(., 'No flights') or contains(., 'No results')]"
    }

    private fun getFlightCards(topN: Int = 5): List<WebElement> {
        val xPath = "//div[@data-testid='searchresults_card'][position() <= $topN]"

        val elements = wait.until {
            driver.findElements(By.xpath(xPath))
        }
        logger.info { "Дождались появления карточек рейсов" }
        logger.info { "Найдено ${elements.size} карточек рейсов" }
        return elements.toList().subList(0, topN)
    }

    fun selectOneway() {
        val oneway = wait.until {
            driver.findElement(By.xpath("//div[@data-ui-name='search_type_oneway']"))
        }
        logger.info { "Нашли кнопку 'В одну сторону'" }

        oneway.click()

        logger.info { "Нажали на кнопку 'В одну сторону'" }
    }

    fun selectBothWay() {
        val bothWay = wait.until {
            driver.findElement(By.xpath("//input[@data-ui-name='input_search_type_roundtrip']"))
        }
        logger.info { "Нашли кнопку 'В обе стороны'" }
        bothWay.click()
        logger.info { "Нажали на кнопку 'В обе стороны'" }
    }


    fun getFlights(topN: Int = 5): List<FlightCard> {
        val flightCards = getFlightCards(topN)

        if (flightCards.isEmpty()) {
            logger.info { "Нет карточек для парсинга" }
            return emptyList()
        }

        val cards = flightCards.mapNotNull { card ->
            try {
                PlaneCardParser.parse(card)
            } catch (e: Exception) {
                logger.info { "Ошибка парсинга карточки: ${e.message}" }
                null
            }
        }
        logger.info { "Распарсили ${cards.size} карточек рейсов" }

        return cards
    }

    fun search() {
        val xpath = "//button[@data-ui-name='button_search_submit']"

        val searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)))

        searchButton.click()
        logger.info { "Нашли кнопку поиска" }

        waitForPageToOpen()
    }

    fun waitForPageToOpen() {
        wait.until {
            driver.findElement(By.xpath(FLIGHT_CARD_XPATH))
        }
    }

}
