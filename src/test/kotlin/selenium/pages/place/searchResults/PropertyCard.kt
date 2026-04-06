package selenium.pages.place.searchResults

import org.openqa.selenium.By
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement
import kotlin.math.log

data class PropertyCard(
    val title: String,
    val price: String,
    val rating: String,
    val attributes: List<String>
)

object PropertyCardParser {

    fun parse(card: WebElement): PropertyCard {
        return PropertyCard(
            title = parseTitle(card),
            price = parsePrice(card),
            rating = parseRating(card),
            attributes = parseAttributes(card)
        )
    }

    private fun parseTitle(card: WebElement): String {
        return try {
            card.findElement(
                By.xpath(".//div[@data-testid='title']")
            ).text
        } catch (e: StaleElementReferenceException) {
            ""
        }
    }

    private fun parsePrice(card: WebElement): String {
        return try {
            card.findElement(
                By.xpath(".//span[@data-testid='price-and-discounted-price']")
            ).text
        } catch (e: StaleElementReferenceException) {
            ""
        }
    }

    private fun parseRating(card: WebElement): String {
        return try {
            card.findElement(
                By.xpath(".//div[@data-testid='review-score']")
            ).text
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Собирает все span элементы с текстом внутри карточки
     */
    private fun parseAttributes(card: WebElement): List<String> {
        return try {
            card.findElements(By.xpath(".//span"))
                .mapNotNull { span ->
                    try {
                        span.text.trim().takeIf { it.isNotEmpty() }
                    } catch (e: StaleElementReferenceException) {
                        null
                    }
                }
                .distinct()
        } catch (e: StaleElementReferenceException) {
            emptyList()
        }
    }
}