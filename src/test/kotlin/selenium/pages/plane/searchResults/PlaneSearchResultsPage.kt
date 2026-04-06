package selenium.pages.plane.searchResults

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
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

    /**
     * Скроллит элемент в видимую область
     */
    private fun scrollIntoView(element: WebElement) {
        try {
            (driver as? JavascriptExecutor)?.executeScript(
                "arguments[0].scrollIntoView(true);", element
            )
        } catch (e: Exception) {
            logger.warn { "Не удалось проскроллить к элементу: ${e.message}" }
        }
    }

    /**
     * Кликает по элементу с fallback на JavaScript
     */
    private fun clickElement(element: WebElement, description: String = "элемент") {
        try {
            scrollIntoView(element)
            element.click()
            logger.info { "Кликнули по $description (обычный клик)" }
        } catch (e: org.openqa.selenium.ElementClickInterceptedException) {
            logger.warn { "Не удалось кликнуть по $description обычным способом, используем JavaScript: ${e.message}" }
            (driver as? JavascriptExecutor)?.executeScript(
                "arguments[0].click();", element
            )
        } catch (e: org.openqa.selenium.ElementNotInteractableException) {
            logger.warn { "$description не интерактивен, используем JavaScript: ${e.message}" }
            (driver as? JavascriptExecutor)?.executeScript(
                "arguments[0].click();", element
            )
        }
    }

    private fun getFlightCards(topN: Int = 5): List<WebElement> {
        val xPath = "//div[@data-testid='searchresults_card'][position() <= $topN]"
        
        // Пробуем разные селекторы для карточек рейсов
        val cardLocators = listOf(
            By.xpath(xPath),
            By.xpath("//div[contains(@data-testid, 'searchresults')]"),
            By.xpath("//article[contains(@class, 'flight')]"),
            By.xpath("//div[contains(@class, 'flight-card')]")
        )
        
        var elements: List<WebElement> = emptyList()
        for (locator in cardLocators) {
            try {
                elements = wait.until {
                    driver.findElements(locator)
                }
                if (elements.isNotEmpty()) {
                    logger.info { "Нашли карточки рейсов с селектором: $locator" }
                    break
                }
            } catch (e: Exception) {
                // Пробуем следующий локатор
            }
        }
        
        logger.info { "Дождались появления карточек рейсов" }
        logger.info { "Найдено ${elements.size} карточек рейсов" }
        return elements.take(topN)
    }

    fun selectOneway() {
        val oneway = wait.until {
            driver.findElement(By.xpath("//div[@data-ui-name='search_type_oneway']"))
        }
        logger.info { "Нашли кнопку 'В одну сторону'" }

        clickElement(oneway, "кнопку 'В одну сторону'")
    }

    fun selectBothWay() {
        val bothWay = wait.until {
            driver.findElement(By.xpath("//input[@data-ui-name='input_search_type_roundtrip']"))
        }
        logger.info { "Нашли кнопку 'В обе стороны'" }
        clickElement(bothWay, "кнопку 'В обе стороны'")
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

        clickElement(searchButton, "кнопку поиска")

        waitForPageToOpen()
    }

    fun waitForPageToOpen() {
        // Ждём появления карточек рейсов или сообщения об отсутствии результатов
        val flightCardLocator = By.xpath(FLIGHT_CARD_XPATH)
        val noResultsLocator = By.xpath(NO_RESULTS_XPATH)
        
        // Дополнительные локаторы для разных вариантов загрузки
        val additionalCardLocators = listOf(
            By.xpath("//div[contains(@data-testid, 'searchresults')]"),
            By.xpath("//article[contains(@class, 'flight')]"),
            By.xpath("//div[contains(@class, 'flight-card')]"),
            By.xpath("//div[contains(@class, 'results')]")
        )

        // Сначала ждём основные карточки
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(flightCardLocator))
            logger.info { "Карточки рейсов загружены" }
            return
        } catch (e: Exception) {
            logger.warn { "Основные карточки не найдены, пробуем альтернативные селекторы" }
        }
        
        // Пробуем альтернативные селекторы
        for (locator in additionalCardLocators) {
            try {
                val tempWait = WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                tempWait.until(ExpectedConditions.presenceOfElementLocated(locator))
                logger.info { "Карточки рейсов найдены с селектором: $locator" }
                return
            } catch (e: Exception) {
                // Пробуем следующий локатор
            }
        }

        // Проверяем сообщение об отсутствии результатов
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(noResultsLocator))
            logger.info { "Получено сообщение об отсутствии рейсов" }
        } catch (e2: Exception) {
            logger.warn { "Не удалось дождаться загрузки страницы результатов" }
        }
    }

}
