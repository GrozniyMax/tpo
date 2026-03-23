package selenium.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait

class SearchResultsPage(private val driver: WebDriver, private val wait: WebDriverWait) {


    fun getSuggestions(firstN: Int = 5): List<String> {
        // XPath на первые N карточек
        val cardsXPath = "(//div[@role=\"list\"]//div[@data-testid=\"property-card\"])[position() <= $firstN]"

        // Находим карточки
        val productCards = driver.findElements(By.xpath(cardsXPath))

        return productCards.map { card ->
            try {
                // Пытаемся найти внутри карточки название
                val titleElement = card.findElement(By.xpath(".//*[@data-testid='title']"))
                titleElement.text.trim()
            } catch (e: Exception) {
                // Если название не найдено, возвращаем пустую строку
                ""
            }
        }.filter { it.isNotEmpty() } // убираем пустые строки
    }

    fun getTotalSuggestionsCount(): Int {
        val xpath = "//h1[@aria-live='assertive']"
        val element = wait.until { driver ->
            driver.findElement(By.xpath(xpath))
        }

        return parseNumber(element.text)
    }

    /**
     * Добавляет фильтр по названию
     * Возвращает количество предложений после применения фильтра
     */
    fun addFilter(filterStartsWith: String): Int {
        val xpath = "//div[@data-testid='filters-sidebar']//input[@type='checkbox' and contains(@aria-label,'$filterStartsWith')]"

        val filterCheckbox = wait.until { driver ->
            driver.findElement(By.xpath(xpath))
        }

        filterCheckbox.click()

        return parseNumber(filterCheckbox.text)
    }

    private fun parseNumber(text: String):Int {
        val regex = "\\d+".toRegex()
        return regex.find(text)?.value?.toInt() ?: 0
    }


}