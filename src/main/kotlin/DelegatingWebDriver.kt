package selenium

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * WebDriver-делегат, который выполняет операции на нескольких драйверах одновременно
 * и проверяет совпадение результатов.
 * Если результаты разных драйверов не совпадают, бросает IllegalStateException.
 */
class DelegatingWebDriver(
    vararg drivers: Pair<String, WebDriver>
) : WebDriver {

    private val drivers = drivers.toMap()

    override fun get(url: String) {
        drivers.values.forEach { it.get(url) }
    }

    override fun getCurrentUrl(): String {
        val results = drivers.mapValues { it.value.currentUrl }
        return requireAllEqual(results, "currentUrl")!!
    }

    override fun getTitle(): String {
        val results = drivers.mapValues { it.value.title }
        return requireAllEqual(results, "title")!!
    }

    override fun findElements(by: By): List<WebElement> {
        val results = drivers.mapValues { it.value.findElements(by) }
        val sizes = results.mapValues { it.value.size }
        requireAllEqual(sizes, "findElements('$by') count")
        
        val elementLists = results.values.toList()
        return elementLists.first().mapIndexed { index, _ ->
            DelegatingWebElement(
                *drivers.keys.map { key ->
                    key to elementLists[drivers.keys.indexOf(key)][index]
                }.toTypedArray()
            )
        }
    }

    override fun findElement(by: By): WebElement {
        val results = drivers.mapValues { it.value.findElement(by) }
        return DelegatingWebElement(
            *drivers.keys.map { key -> key to results[key]!! }.toTypedArray()
        )
    }

    override fun getPageSource(): String {
        val results = drivers.mapValues { it.value.pageSource }
        return requireAllEqual(results, "pageSource")!!
    }

    override fun close() {
        drivers.values.forEach { it.close() }
    }

    override fun quit() {
        drivers.values.forEach { it.quit() }
    }

    override fun getWindowHandles(): Set<String> {
        val results = drivers.mapValues { it.value.windowHandles }
        return requireAllEqual(results, "windowHandles")
    }

    override fun getWindowHandle(): String {
        val results = drivers.mapValues { it.value.windowHandle }
        return requireAllEqual(results, "windowHandle")
    }

    override fun switchTo(): WebDriver.TargetLocator = drivers.values.first().switchTo()

    override fun navigate(): WebDriver.Navigation = drivers.values.first().navigate()

    override fun manage(): WebDriver.Options = drivers.values.first().manage()

    /**
     * Проверяет, что все значения в карте одинаковы.
     * Если значения не совпадают, бросает IllegalStateException с подробным сообщением.
     */
    private fun <T> requireAllEqual(results: Map<String, T>, operationName: String): T {
        val distinctValues = results.entries.groupBy { it.value }
        require(distinctValues.size == 1) {
            val mismatchDetails = results.entries.joinToString(", ") { (key, value) ->
                "$key='$value'"
            }
            "$operationName mismatch: $mismatchDetails"
        }
        return distinctValues.keys.first()
    }
}
