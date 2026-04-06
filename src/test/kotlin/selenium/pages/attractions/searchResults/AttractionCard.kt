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
                card.findElement(By.xpath(".//h3//a")).text
            },

            price = tryParse("") {
                card.findElement(By.xpath(".//div[@data-testid='price']//div//div[2]")).text
            },

            freeCancelAvailable = tryParse(false) {
                card.findElements(By.xpath(".//*[contains(text(), 'Бесплатная отмена')]")).isNotEmpty()
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