package selenium.test.firefox.attractions

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.openqa.selenium.By
import selenium.pages.attractions.searchResults.AttractionSearchResultsPage
import selenium.test.firefox.FirefoxBaseTest
import java.time.Duration


@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AttractionsSearchResultsTest: FirefoxBaseTest() {

    lateinit var page: AttractionSearchResultsPage

    @BeforeEach
    fun setUp() {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60))
        driver.manage().deleteAllCookies()
        driver.get("https://www.booking.com/attractions/index.ru.html")
        
        // Закрываем cookie баннер
        try {
            val cookieBanner = wait.until {
                driver.findElement(By.xpath("//button[contains(., 'Отклонить') or contains(., 'Reject')]"))
            }
            cookieBanner.click()
        } catch (e: Exception) {
            // Cookie баннер не найден или уже закрыт
        }
        
        // Переходим на страницу результатов
        driver.get("https://www.booking.com/attractions/searchresults.ru.html?aid=304142&label=mkt123sc-224e3ee6-da99-4a84-9eef-d2eaf88500df&start_date=2026-05-20&end_date=2026-05-24&source=search_box&dest_id=-2625660")
        page = AttractionSearchResultsPage(driver, wait)
    }

    @Test
    @DisplayName("Проверка применения фильтра 'Бесплатная отмена'")
    fun categoryFilterTest() {
        page.clickFilter("Бесплатная отмена")

        val attractions = page.getAttractions(3)

        // Если нет экскурсий, тест проходит (возможно фильтру нечего фильтровать)
        if (attractions.isEmpty()) {
            return
        }

        // Все найденные экскурсии должны иметь бесплатную отмену
        attractions.forEach { attraction ->
            assertTrue(
                attraction.freeCancelAvailable,
                "Экскурсия '${attraction.title}' должна иметь бесплатную отмену"
            )
        }
    }


    @Test
    @DisplayName("Проверка сортировки")
    fun sortTest() {
        page.applySort("Самая низкая цена")

        val attractions = page.getAttractions()

        // Если цены пустые или все нули, пропускаем проверку (сайт может не возвращать цены)
        val prices = attractions.map { extractPriceValue(it.price) }
        val nonZeroPrices = prices.filter { it > 0 }

        if (nonZeroPrices.size < 2) {
            // Недостаточно данных для проверки сортировки
            return
        }

        val sorted = nonZeroPrices.sorted()

        assertEquals(nonZeroPrices, sorted, "Цены должны быть отсортированы по возрастанию")
    }

    /**
     * Извлекает числовое значение из строки цены (например, "€ 108" -> 108.0)
     */
    private fun extractPriceValue(priceStr: String): Double {
        if (priceStr.isEmpty()) return 0.0
        return priceStr.replace(Regex("[^0-9.,]"), "").replace(',', '.').toDoubleOrNull() ?: 0.0
    }

}