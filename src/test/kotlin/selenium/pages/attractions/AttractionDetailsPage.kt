package selenium.pages.attractions

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.attractions.searchResults.AttractionSearchResultsPage

class AttractionDetailsPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(AttractionDetailsPage::class.java)
) {

    /**
     * Получение заголовка экскурсии
     */
    fun getTitle(): String {
        val xpath = "//h1[contains(@class, 'title') or contains(@data-testid, 'title')] | //h2[contains(@class, 'title')]"
        val element = wait.until { driver ->
            try {
                driver.findElement(By.xpath(xpath))
            } catch (e: Exception) {
                null
            }
        }
        val title = element?.text ?: "Unknown"
        logger.info { "Получили заголовок: $title" }
        return title
    }

    /**
     * Возврат на предыдущую страницу (страницу результатов поиска)
     */
    fun goBack(): AttractionSearchResultsPage {
        driver.navigate().back()
        logger.info { "Вернулись на предыдущую страницу" }
        return AttractionSearchResultsPage(driver, wait)
    }

    /**
     * Получение информации об экскурсии
     */
    fun getAttractionDetails(): AttractionDetails {
        return AttractionDetails(
            title = getTitle(),
            duration = getDuration(),
            rating = getRating(),
            reviewCount = getReviewCount(),
            price = getPrice(),
            description = getDescription(),
            category = getCategory()
        )
    }

    private fun getDuration(): String? {
        return try {
            val xpath = "//span[contains(@class, 'duration') or contains(@data-testid, 'duration')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getRating(): String? {
        return try {
            val xpath = "//span[contains(@class, 'rating') or contains(@data-testid, 'rating')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getReviewCount(): String? {
        return try {
            val xpath = "//span[contains(@class, 'reviews') or contains(@data-testid, 'reviews')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getPrice(): String? {
        return try {
            val xpath = "//span[contains(@class, 'price') or contains(@data-testid, 'price')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getDescription(): String? {
        return try {
            val xpath = "//div[contains(@class, 'description') or contains(@data-testid, 'description')] | //p[contains(@class, 'description')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getCategory(): String? {
        return try {
            val xpath = "//span[contains(@class, 'category') or contains(@data-testid, 'category')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Получение характеристик экскурсии
     */
    fun getProperties(): List<String> {
        val properties = mutableListOf<String>()

        // Длительность
        getDuration()?.let { properties.add("Duration: $it") }

        // Рейтинг
        getRating()?.let { properties.add("Rating: $it") }

        // Количество отзывов
        getReviewCount()?.let { properties.add("Reviews: $it") }

        // Категория
        getCategory()?.let { properties.add("Category: $it") }

        // Языки гида
        try {
            val languagesXpath = "//span[contains(@class, 'language') or contains(@data-testid, 'language')]"
            val languages = driver.findElement(By.xpath(languagesXpath)).text
            if (languages.isNotEmpty()) {
                properties.add("Languages: $languages")
            }
        } catch (e: Exception) {
            // Игнорируем
        }

        logger.info { "Получили ${properties.size} характеристик экскурсии" }

        return properties
    }

    /**
     * Проверка возможности бесплатной отмены
     */
    fun hasFreeCancellation(): Boolean {
        return try {
            val xpath = "//span[contains(., 'Бесплатная отмена') or contains(., 'Free cancellation')]"
            driver.findElement(By.xpath(xpath)).isDisplayed
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Проверка возможности мгновенного подтверждения
     */
    fun hasInstantConfirmation(): Boolean {
        return try {
            val xpath = "//span[contains(., 'Мгновенное подтверждение') or contains(., 'Instant confirmation')]"
            driver.findElement(By.xpath(xpath)).isDisplayed
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Модель деталей экскурсии
 */
data class AttractionDetails(
    val title: String?,
    val duration: String?,
    val rating: String?,
    val reviewCount: String?,
    val price: String?,
    val description: String?,
    val category: String?
)
