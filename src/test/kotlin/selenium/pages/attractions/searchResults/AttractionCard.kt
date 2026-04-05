package selenium.pages.attractions.searchResults

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

/**
 * Модель карточки экскурсии
 */
data class AttractionCard(
    val title: String?,
    val duration: String?,
    val rating: String?,
    val reviewCount: String?,
    val price: String?,
    val category: String?
)

/**
 * Парсер карточек экскурсий
 */
object AttractionCardParser {
    fun parse(card: WebElement): AttractionCard {
        return AttractionCard(
            title = try {
                card.findElement(By.xpath(".//h3[contains(@class, 'title') or contains(@data-testid, 'title')] | .//span[contains(@class, 'title')]")).text
            } catch (e: Exception) {
                null
            },
            duration = try {
                card.findElement(By.xpath(".//span[contains(@class, 'duration')]")).text
            } catch (e: Exception) {
                null
            },
            rating = try {
                card.findElement(By.xpath(".//span[contains(@class, 'rating') or contains(@data-testid, 'rating')]")).text
            } catch (e: Exception) {
                null
            },
            reviewCount = try {
                card.findElement(By.xpath(".//span[contains(@class, 'reviews') or contains(@data-testid, 'reviews')]")).text
            } catch (e: Exception) {
                null
            },
            price = try {
                card.findElement(By.xpath(".//span[contains(@class, 'price')]")).text
            } catch (e: Exception) {
                null
            },
            category = try {
                card.findElement(By.xpath(".//span[contains(@class, 'category') or contains(@data-testid, 'category')]")).text
            } catch (e: Exception) {
                null
            }
        )
    }
}