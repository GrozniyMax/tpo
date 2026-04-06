package selenium.pages.place.searchResults

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.BookingCookiePage
import selenium.pages.place.PlacePage

class PlaceSearchResultsPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(PlaceSearchResultsPage::class.java)
): BookingCookiePage(driver, wait) {

    private fun getProductCards(topN: Int = 5): List<WebElement> {
        val cardsXPath = "(//div[@role='list']//div[@data-testid='property-card'])[position() <= $topN]"

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
        
        // Скроллим к кнопке сортировки
        scrollIntoView(sortButton)

        clickElement(sortButton)

        logger.info { "Кликнули по кнопке сортировки" }

        // Ждём появления контейнера с опциями сортировки
        val sortContainer = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-testid='sorters-dropdown']")
            )
        )

        logger.info { "Нашли контейнер с сортировкой" }

        // Ждём пока опция станет видимой и кликабельной
        val sortOption = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='$sort']")
            )
        )

        logger.info { "Нашли опцию сортировки $sort" }

        // Скроллим к опции сортировки
        scrollIntoView(sortOption)
        clickElement(sortOption)

        logger.info { "Кликнули по опции сортировки $sort" }
        
        // Ждём обновления результатов
        waitForResultsToUpdate()
    }

    fun select(index: Int): PlacePage {
        val productCards = getProductCards(index + 1)

        val link = productCards[index].findElement(By.xpath(".//a[@data-testid='title-link']"))
        logger.info { "Выбрали карточку с индексом $index" }

        // Скроллим к элементу перед кликом (важно для Firefox)
        scrollIntoView(link)

        val originalWindow = driver.windowHandle

        clickElement(link)

        logger.info { "Кликнули по карточке" }

        wait.until { driver.windowHandles.size > 1 }
        val newWindow = driver.windowHandles.first { it != originalWindow }
        driver.switchTo().window(newWindow)

        logger.info { "Переключились на новую вкладку" }

        return PlacePage(driver, wait, originalWindow)
    }

    fun clickPopularFilter(filterName: String) {
        val filtersContainerXPath = "//div[@data-testid='filters-group'][@data-filters-group='popular']"
        val filterXPath = ".//label[contains(., '$filterName')]"

        // Сначала пробуем закрыть cookie баннер если он есть
        handlePageLoadAndDialogs()

        val container = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath(filtersContainerXPath)
            )
        )
        logger.info { "Нашли популярные фильтры" }

        val filterElement = wait.until(
            ExpectedConditions.elementToBeClickable(
                container.findElement(By.xpath(filterXPath))
            )
        )
        logger.info { "Нашли фильтр $filterName" }

        // Скроллим к элементу перед кликом (важно для Firefox)
        scrollIntoView(filterElement)
        filterElement.click()

        logger.info { "Кликнули по фильтру $filterName" }

        waitForResultsToUpdate()
    }

    fun clickOtherFilter(filterGroup: String, filterName: String) {
        val filterXPath = "//div[@data-testid='filters-group-label-content'][text()='$filterName']"

        val filterElement = wait.until (
            ExpectedConditions.elementToBeClickable(By.xpath(filterXPath))
        )
        logger.info { "Нашли фильтр $filterName" }

        // Скроллим к элементу перед кликом (важно для Firefox)
        scrollIntoView(filterElement)

        // Проверяем и закрываем cookie баннер прямо перед кликом
        tryCloseCookieBanner()

        // Повторно находим элемент после закрытия баннера
        val filterElementFinal = wait.until (
            ExpectedConditions.elementToBeClickable(By.xpath(filterXPath))
        )

        // Пробуем обычный клик, если не получается - используем JavaScript
        try {
            filterElementFinal.click()
            logger.info { "Кликнули по фильтру $filterName (обычный клик)" }
        } catch (e: ElementClickInterceptedException) {
            logger.warn { "Не удалось кликнуть по фильтру обычным способом, используем JavaScript: ${e.message}" }
            // Используем JavaScript для клика по input внутри label если есть
            try {
                val inputElement = filterElementFinal.findElement(By.xpath(".//preceding::input[1]"))
                (driver as? JavascriptExecutor)?.executeScript(
                    "arguments[0].click();", inputElement
                )
            } catch (e2: Exception) {
                (driver as? JavascriptExecutor)?.executeScript(
                    "arguments[0].click();", filterElementFinal
                )
            }
            logger.info { "Кликнули по фильтру $filterName (JavaScript)" }
        }

        waitForResultsToUpdate()
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

    /**
     * Кликает по элементу с fallback на JavaScript
     */
    private fun clickElement(element: WebElement, description: String = "элемент") {
        try {
            scrollIntoView(element)
            element.click()
            logger.info { "Кликнули по $description (обычный клик)" }
        } catch (e: ElementClickInterceptedException) {
            logger.warn { "Не удалось кликнуть по $description обычным способом, используем JavaScript: ${e.message}" }
            (driver as? JavascriptExecutor)?.executeScript(
                "arguments[0].click();", element
            )
        } catch (e: ElementNotInteractableException) {
            logger.warn { "$description не интерактивен, используем JavaScript: ${e.message}" }
            (driver as? JavascriptExecutor)?.executeScript(
                "arguments[0].click();", element
            )
        }
    }
}