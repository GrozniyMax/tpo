package selenium.pages.attractions.searchResults

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
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

    private fun getAttractionCards(topN: Int = 5): List<WebElement> {
        val cardsXPath = "(//div[@data-testid='card'])[position() <= $topN]"

        // Пробуем найти карточки с основным селектором
        try {
            val shortWait = WebDriverWait(driver, java.time.Duration.ofSeconds(5))
            shortWait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[@data-testid='card']")
                )
            )
            logger.info { "Дождались появления карточек экскурсий" }
        } catch (e: Exception) {
            logger.warn { "Карточки экскурсий не найдены" }
            return emptyList()
        }

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
        // Пробуем разные варианты селекторов для сортировки
        val sortXpaths = listOf(
            "//button[contains(., '$sort')]",
            "//span[contains(., '$sort')]",
            "//label[contains(., '$sort')]"
        )

        var sortOption: WebElement? = null
        for (xpath in sortXpaths) {
            try {
                val shortWait = WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                sortOption = shortWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)))
                logger.info { "Нашли опцию сортировки с селектором: $xpath" }
                break
            } catch (e: Exception) {
                // Пробуем следующий селектор
            }
        }

        if (sortOption != null) {
            clickElement(sortOption, "опцию сортировки")
            logger.info { "Кликнули по опции сортировки $sort" }
            waitForResultsToUpdate()
        } else {
            logger.warn { "Не удалось найти опцию сортировки: $sort" }
        }
    }

    /**
     * Применение фильтра
     */
    fun clickFilter(filterName: String) {
        // Пробуем разные варианты селекторов для фильтра
        val filterXpaths = listOf(
            "//label[contains(., '$filterName')]",
            "//span[contains(., '$filterName')]",
            "//div[contains(., '$filterName')]"
        )

        var filterElement: WebElement? = null
        for (xpath in filterXpaths) {
            try {
                val shortWait = WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                filterElement = shortWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)))
                logger.info { "Нашли фильтр с селектором: $xpath" }
                break
            } catch (e: Exception) {
                // Пробуем следующий селектор
            }
        }

        if (filterElement != null) {
            clickElement(filterElement, "фильтр")
            logger.info { "Кликнули по фильтру $filterName" }
            waitForResultsToUpdate()
        } else {
            logger.warn { "Не удалось найти фильтр: $filterName" }
        }
    }

    /**
     * Ожидает обновления результатов после применения фильтра
     */
    private fun waitForResultsToUpdate() {
        // Ждём небольшого времени для начала загрузки
        try {
            Thread.sleep(1000)
        } catch (e: Exception) {
            // Игнорируем
        }
        
        // Ждём пока карточки станут видимыми
        try {
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@data-testid='card']")
                )
            )
            logger.info { "Результаты обновились" }
        } catch (e: Exception) {
            logger.warn { "Таймаут ожидания обновления результатов" }
        }
    }
}
