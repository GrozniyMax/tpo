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
                card.findElement(By.xpath(".//h3")).text
            },

            price = tryParse("") {
                // Пробуем разные варианты для цены
                val priceXpath = ".//div[@data-testid='price']"
                val priceElement = card.findElement(By.xpath(priceXpath))
                val text = priceElement.text.trim()
                if (text.isNotEmpty()) {
                    return@tryParse text
                } else {
                    return@tryParse ""
                }
            },

            freeCancelAvailable = tryParse(false) {
                val xpath = ".//*[contains(text(), 'бесплатная отмена')]"
                val elements = card.findElement(By.xpath(xpath))
                return@tryParse true
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