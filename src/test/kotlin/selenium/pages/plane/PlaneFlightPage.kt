package selenium.pages.plane

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import selenium.pages.plane.searchResults.PlaneSearchResultsPage

class PlaneFlightPage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(PlaneFlightPage::class.java)
) {

    /**
     * Получение заголовка рейса (авиакомпания, номер рейса)
     */
    fun getTitle(): String {
        val xpath = "//h1[contains(@class, 'flight-title') or contains(@data-testid, 'flight-title')] | //h2[contains(@class, 'flight-title')]"
        val element = wait.until { driver ->
            try {
                driver.findElement(By.xpath(xpath))
            } catch (e: Exception) {
                null
            }
        }
        val title = element?.text ?: "Unknown"
        logger.info { "Получили заголовок рейса: $title" }
        return title
    }

    /**
     * Возврат на предыдущую страницу (страницу результатов поиска)
     */
    fun goBack(): PlaneSearchResultsPage {
        driver.navigate().back()
        logger.info { "Вернулись на предыдущую страницу" }
        return PlaneSearchResultsPage(driver, wait)
    }

    /**
     * Получение информации о рейсе
     */
    fun getFlightDetails(): FlightDetails {
        return FlightDetails(
            airline = getAirline(),
            flightNumber = getFlightNumber(),
            departureAirport = getDepartureAirport(),
            arrivalAirport = getArrivalAirport(),
            departureTime = getDepartureTime(),
            arrivalTime = getArrivalTime(),
            duration = getDuration(),
            price = getPrice()
        )
    }

    private fun getAirline(): String? {
        return try {
            val xpath = "//span[contains(@class, 'airline-name') or contains(@data-testid, 'airline')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getFlightNumber(): String? {
        return try {
            val xpath = "//span[contains(@class, 'flight-number')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getDepartureAirport(): String? {
        return try {
            val xpath = "//span[contains(@class, 'departure-airport') or contains(@data-testid, 'origin-airport')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getArrivalAirport(): String? {
        return try {
            val xpath = "//span[contains(@class, 'arrival-airport') or contains(@data-testid, 'destination-airport')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getDepartureTime(): String? {
        return try {
            val xpath = "//span[contains(@class, 'departure-time') or contains(@data-testid, 'departure-time')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getArrivalTime(): String? {
        return try {
            val xpath = "//span[contains(@class, 'arrival-time') or contains(@data-testid, 'arrival-time')]"
            driver.findElement(By.xpath(xpath)).text
        } catch (e: Exception) {
            null
        }
    }

    private fun getDuration(): String? {
        return try {
            val xpath = "//span[contains(@class, 'duration') or contains(@data-testid, 'flight-duration')]"
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

    /**
     * Получение характеристик рейса (пересадки, багаж и т.д.)
     */
    fun getProperties(): List<String> {
        val properties = mutableListOf<String>()

        // Ищем информацию о пересадках
        try {
            val stopsXpath = "//span[contains(@class, 'stops') or contains(@data-testid, 'stops')]"
            val stops = driver.findElement(By.xpath(stopsXpath)).text
            if (stops.isNotEmpty()) {
                properties.add("Stops: $stops")
            }
        } catch (e: Exception) {
            // Игнорируем
        }

        // Ищем информацию о багаже
        try {
            val baggageXpath = "//span[contains(@class, 'baggage') or contains(@data-testid, 'baggage')]"
            val baggage = driver.findElement(By.xpath(baggageXpath)).text
            if (baggage.isNotEmpty()) {
                properties.add("Baggage: $baggage")
            }
        } catch (e: Exception) {
            // Игнорируем
        }

        logger.info { "Получили ${properties.size} характеристик рейса" }

        return properties
    }
}

/**
 * Модель деталей рейса
 */
data class FlightDetails(
    val airline: String?,
    val flightNumber: String?,
    val departureAirport: String?,
    val arrivalAirport: String?,
    val departureTime: String?,
    val arrivalTime: String?,
    val duration: String?,
    val price: String?
)
