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
) : BookingCookiePage(driver, wait) {

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
        val sortXpaths = "//span[contains(., '$sort')]"

        val sortOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(sortXpaths)))

        clickElement(sortOption, "опцию сортировки")
        logger.info { "Кликнули по опции сортировки $sort" }
        waitForResultsToUpdate()
    }

    /**
     * Применение фильтра
     */
    fun clickFilter(filterName: String) {
        // Пробуем разные варианты селекторов для фильтра
        val filterXpaths =
            "//span[contains(., '$filterName')]"

        val filterElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(filterXpaths)))


        clickElement(filterElement, "фильтр")
        logger.info { "Кликнули по фильтру $filterName" }
        waitForResultsToUpdate()
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
