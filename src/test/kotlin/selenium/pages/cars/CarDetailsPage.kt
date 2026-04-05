package selenium.pages.cars

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.cars.searchResults.CarSearchResultsPage

class CarDetailsPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(CarDetailsPage::class.java)
) {

    /**
     * Получение заголовка (название автомобиля)
     */
    fun getTitle(): String {
        val xpath = "//h1[contains(@class, 'car-title') or contains(@data-testid, 'car-title')] | //h2[contains(@class, 'car-title')]"
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
    fun goBack(): CarSearchResultsPage {
        driver.navigate().back()
        logger.info { "Вернулись на предыдущую страницу" }
        return CarSearchResultsPage(driver, wait)
    }

    /**
     * Получение информации об автомобиле
     */
    fun getCarDetails(): CarDetails {
        return CarDetails(
            carName = getCarName(),
            category = getCategory(),
            transmission = getTransmission(),
            seats = getSeats(),
            doors = getDoors(),
            airConditioning = hasAirConditioning(),
            fuelType = getFuelType(),
            mileage = getMileage(),
            price = getPrice(),
            supplier = getSupplier()
        )
    }

    private fun getCarName(): String? {
        return try {
            val xpath = "//span[contains(@class, 'car-name') or contains(@data-testid, 'car-name')]"
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

    private fun getTransmission(): String? {
        return try {
            val xpath = "//span[contains(@class, 'transmission') or contains(@data-testid, 'transmission')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getSeats(): String? {
        return try {
            val xpath = "//span[contains(@class, 'seats') or contains(@data-testid, 'seats')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getDoors(): String? {
        return try {
            val xpath = "//span[contains(@class, 'doors') or contains(@data-testid, 'doors')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun hasAirConditioning(): Boolean? {
        return try {
            val xpath = "//span[contains(@class, 'air-conditioning') or contains(@data-testid, 'air-conditioning')]"
            driver.findElement(By.xpath(xpath)).isDisplayed
        } catch (e: Exception) {
            null
        }
    }

    private fun getFuelType(): String? {
        return try {
            val xpath = "//span[contains(@class, 'fuel') or contains(@data-testid, 'fuel-type')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getMileage(): String? {
        return try {
            val xpath = "//span[contains(@class, 'mileage') or contains(@data-testid, 'mileage')]"
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

    private fun getSupplier(): String? {
        return try {
            val xpath = "//span[contains(@class, 'supplier') or contains(@data-testid, 'supplier')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Получение характеристик автомобиля
     */
    fun getProperties(): List<String> {
        val properties = mutableListOf<String>()

        // Трансмиссия
        getTransmission()?.let { properties.add("Transmission: $it") }

        // Количество мест
        getSeats()?.let { properties.add("Seats: $it") }

        // Количество дверей
        getDoors()?.let { properties.add("Doors: $it") }

        // Кондиционер
        if (hasAirConditioning() == true) {
            properties.add("Air Conditioning: Yes")
        }

        // Тип топлива
        getFuelType()?.let { properties.add("Fuel Type: $it") }

        // Пробег
        getMileage()?.let { properties.add("Mileage: $it") }

        logger.info { "Получили ${properties.size} характеристик автомобиля" }

        return properties
    }
}

/**
 * Модель деталей автомобиля
 */
data class CarDetails(
    val carName: String?,
    val category: String?,
    val transmission: String?,
    val seats: String?,
    val doors: String?,
    val airConditioning: Boolean?,
    val fuelType: String?,
    val mileage: String?,
    val price: String?,
    val supplier: String?
)
