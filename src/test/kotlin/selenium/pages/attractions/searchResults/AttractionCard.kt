package selenium.pages.attractions.searchResults

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

/**
 * Модель карточки экскурсии
 */
data class AttractionCard(
    val title: String,
    val price: String,
    val freeCancelAvailable: Boolean,
)

/**
 * Парсер карточек экскурсий
 */
object AttractionCardParser {
    fun parse(card: WebElement): AttractionCard {
        return AttractionCard(
            title = tryParse("") {
                // Пробуем разные варианты для заголовка
                try {
                    card.findElement(By.xpath(".//h3//a")).text
                } catch (e: Exception) {
                    card.findElement(By.xpath(".//h3")).text
                }
            },

            price = tryParse("") {
                // Пробуем разные варианты для цены
                val priceXpaths = listOf(
                    ".//div[@data-testid='price']//div//div[2]",
                    ".//div[@data-testid='price']",
                    ".//*[contains(@class, 'price')]",
                    ".//span[contains(text(), '€') or contains(text(), 'RUB')]"
                )
                
                for (xpath in priceXpaths) {
                    try {
                        val priceElement = card.findElement(By.xpath(xpath))
                        val text = priceElement.text.trim()
                        if (text.isNotEmpty()) {
                            return@tryParse text
                        }
                    } catch (e: Exception) {
                        // Пробуем следующий селектор
                    }
                }
                ""
            },

            freeCancelAvailable = tryParse(false) {
                // Пробуем разные варианты для проверки бесплатной отмены
                val cancelXpaths = listOf(
                    ".//*[contains(text(), 'Бесплатная отмена')]",
                    ".//*[contains(text(), 'free cancellation')]",
                    ".//*[contains(@class, 'free-cancel')]",
                    ".//*[contains(@data-testid, 'free-cancellation')]"
                )
                
                for (xpath in cancelXpaths) {
                    try {
                        val elements = card.findElements(By.xpath(xpath))
                        if (elements.isNotEmpty()) {
                            return@tryParse true
                        }
                    } catch (e: Exception) {
                        // Пробуем следующий селектор
                    }
                }
                false
            }
        )
    }

    private fun <T> tryParse(default: T, action: () -> T): T {
        return try {
            action()
        } catch (e: Exception) {
            default
        }
    }
}