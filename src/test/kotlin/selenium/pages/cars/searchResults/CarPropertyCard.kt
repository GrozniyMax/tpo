import org.openqa.selenium.By
import org.openqa.selenium.WebElement

/**
 * Модель карточки автомобиля
 */
data class CarCard(
    val carName: String?,
    val transmission: String?,
    val seats: String?,
    val doors: String?,
    val airConditioning: Boolean?,
    val fuelType: String?,
    val price: String?,
    val supplier: String?
)

/**
 * Парсер карточек автомобилей
 */
object CarCardParser {
    fun parse(card: WebElement): CarCard {
        return CarCard(
            carName = try {
                card.findElement(By.xpath(".//span[contains(@class, 'car-name') or contains(@data-testid, 'car-name')]")).text
            } catch (e: Exception) {
                null
            },
            transmission = try {
                card.findElement(By.xpath(".//span[contains(@class, 'transmission')]")).text
            } catch (e: Exception) {
                null
            },
            seats = try {
                card.findElement(By.xpath(".//span[contains(@class, 'seats')]")).text
            } catch (e: Exception) {
                null
            },
            doors = try {
                card.findElement(By.xpath(".//span[contains(@class, 'doors')]")).text
            } catch (e: Exception) {
                null
            },
            airConditioning = try {
                card.findElement(By.xpath(".//span[contains(@class, 'air-conditioning')]")).isDisplayed
            } catch (e: Exception) {
                null
            },
            fuelType = try {
                card.findElement(By.xpath(".//span[contains(@class, 'fuel')]")).text
            } catch (e: Exception) {
                null
            },
            price = try {
                card.findElement(By.xpath(".//span[contains(@class, 'price')]")).text
            } catch (e: Exception) {
                null
            },
            supplier = try {
                card.findElement(By.xpath(".//span[contains(@class, 'supplier')]")).text
            } catch (e: Exception) {
                null
            }
        )
    }
}