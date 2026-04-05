package selenium

import org.openqa.selenium.By
import org.openqa.selenium.OutputType
import org.openqa.selenium.Point
import org.openqa.selenium.Rectangle
import org.openqa.selenium.WebElement

/**
 * WebElement-делегат, который выполняет операции на нескольких WebElement одновременно
 * и проверяет совпадение результатов.
 * Если результаты разных элементов не совпадают, бросает IllegalStateException.
 */
class DelegatingWebElement(
    vararg elements: Pair<String, WebElement>
) : WebElement {

    private val elements = elements.toMap()

    override fun click() {
        elements.values.forEach { it.click() }
    }

    override fun submit() {
        elements.values.forEach { it.submit() }
    }

    override fun sendKeys(vararg keys: CharSequence) {
        elements.values.forEach { it.sendKeys(*keys) }
    }

    override fun clear() {
        elements.values.forEach { it.clear() }
    }

    override fun getTagName(): String {
        val results = elements.mapValues { it.value.tagName }
        return requireAllEqual(results, "tagName")
    }

    override fun getAttribute(name: String): String? {
        val results = elements.mapValues { it.value.getAttribute(name) }
        return requireAllEqual(results, "attribute '$name'")
    }

    override fun isSelected(): Boolean {
        val results = elements.mapValues { it.value.isSelected() }
        return requireAllEqual(results, "isSelected")
    }

    override fun isEnabled(): Boolean {
        val results = elements.mapValues { it.value.isEnabled() }
        return requireAllEqual(results, "isEnabled")
    }

    override fun getText(): String {
        val results = elements.mapValues { it.value.text }
        return requireAllEqual(results, "text")
    }

    override fun findElements(by: By): List<WebElement> {
        val results = elements.mapValues { it.value.findElements(by) }
        val sizes = results.mapValues { it.value.size }
        requireAllEqual(sizes, "findElements('$by') count")
        return results.values.first()
    }

    override fun findElement(by: By): WebElement {
        val results = elements.mapValues { it.value.findElement(by) }
        // Проверяем, что найденные элементы имеют одинаковый текст
        val texts = results.mapValues { it.value.text }
        requireAllEqual(texts, "findElement('$by') text")
        return results.values.first()
    }

    override fun isDisplayed(): Boolean {
        val results = elements.mapValues { it.value.isDisplayed() }
        return requireAllEqual(results, "isDisplayed")
    }

    override fun getLocation(): Point {
        val results = elements.mapValues { it.value.location }
        return requireAllEqual(results, "location")
    }

    override fun getSize(): org.openqa.selenium.Dimension {
        val results = elements.mapValues { it.value.size }
        return requireAllEqual(results, "size")
    }

    override fun getRect(): Rectangle {
        val results = elements.mapValues { it.value.rect }
        return requireAllEqual(results, "rect")
    }

    override fun getCssValue(propertyName: String): String {
        val results = elements.mapValues { it.value.getCssValue(propertyName) }
        return requireAllEqual(results, "cssValue '$propertyName'")
    }

    override fun <X : Any> getScreenshotAs(target: OutputType<X>): X {
        // Screenshot from first element only (side effect operation)
        return elements.values.first().getScreenshotAs(target)
    }

    /**
     * Проверяет, что все значения в карте одинаковы.
     * Если значения не совпадают, бросает IllegalStateException с подробным сообщением.
     */
    private fun <T> requireAllEqual(results: Map<String, T>, operationName: String = "Value"): T {
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