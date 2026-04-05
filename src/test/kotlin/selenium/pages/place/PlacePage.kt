package selenium.pages.place

import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait

class PlacePage(
    private val driver: WebDriver,
    private val wait: WebDriverWait,
    private val logger: Logger = LoggerFactory.getLogger(PlacePage::class.java)
) {


    fun getTitle(): String {
        val xpath = "//*[@id=\"hp_hotel_name\"]/h2"
        val element = wait.until { driver ->
            driver.findElement(By.xpath(xpath))
        }
        logger.info { "Получили заголовок: ${element.text}" }
        return element.text
    }

    fun getProperties(): List<String> {
        return (getPropertiesMostPopularFacilities() + getPropertyHighlights()).toSet().toList()
    }

    private fun getPropertiesMostPopularFacilities(): List<String> {
        val propertiesContainer = wait.until { driver ->
            driver.findElement(By.xpath("//div[@id='property-most-popular-facilities-wrapper']"))
        }

        logger.info { "Получили контейнер с характеристиками" }

        val properties = propertiesContainer.findElements(By.xpath(".//li[@role='listitem']"))
            .map { it.text.trim() }
            .filter { it.isNotEmpty() }

        logger.info { "Получили ${properties.size} характеристик" }

        return properties
    }

    private fun getPropertyHighlights(): List<String> {
        val propertiesContainer = wait.until { driver ->
            driver.findElement(By.xpath("//div[@id='property-highlights']"))
        }

        logger.info { "Получили контейнер с характеристиками" }

        val properties = propertiesContainer.findElements(By.xpath(".//li[@role='listitem']"))
            .map { it.text.trim() }
            .filter { it.isNotEmpty() }

        logger.info { "Получили ${properties.size} характеристик" }

        return properties
    }

}