package selenium.pages.cars.searchResults

import CarCard
import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.cars.CarDetailsPage

class CarSearchResultsPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(CarSearchResultsPage::class.java)
) {

    private fun getCarCards(topN: Int = 5): List<WebElement> {
        val cardsXPath = "(//div[contains(@data-testid, 'car-card') or contains(@class, 'car-card')])[position() <= $topN]"

        wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@data-testid, 'car-card') or contains(@class, 'car-card')]")
            )
        )
        logger.info { "Дождались появления карточек автомобилей" }

        val carCards = driver.findElements(By.xpath(cardsXPath))
        logger.info { "Нашли ${carCards.size} карточек автомобилей" }

        return carCards
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
     * Получение списка автомобилей
     */
    fun getCars(topN: Int = 5): List<CarCard> {
        val carCards = getCarCards(topN)

        val cars = carCards.map { card -> CarCardParser.parse(card) }
        logger.info { "Распарсили карточки автомобилей" }

        return cars
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
     * Выбор автомобиля по индексу
     */
    fun select(index: Int): CarDetailsPage {
        val carCards = getCarCards(index + 1)

        val card = carCards[index]
        logger.info { "Выбрали карточку автомобиля с индексом $index" }

        card.click()
        logger.info { "Кликнули по карточке автомобиля" }

        waitForResultsToUpdate()
        logger.info { "Дождались обновления результатов" }

        return CarDetailsPage(driver, wait)
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
                By.xpath("//div[contains(@data-testid, 'car-card') or contains(@class, 'car-card')]")
            )
        )
        logger.info { "Результаты обновились" }
    }
}
