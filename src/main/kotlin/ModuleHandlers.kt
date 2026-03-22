import basic.*
import tpo.maxim.expression.*
import java.math.BigDecimal
import java.math.MathContext

data class ModuleInstance(
    val description: String,
    val function: (BigDecimal, MathContext) -> BigDecimal
)

data class ModuleDescription(
    val name: String,
    val description: String
)

interface ModuleHandler {
    fun getModule(key: String): ModuleInstance?
    fun getModulesDescriptions(): List<ModuleDescription>
}

/**
 * Unified module handler that combines negative, positive and additional expression modules.
 */
object UnifiedModuleHandler : ModuleHandler {
    private val modules: Map<String, ModuleInstance> = mapOf(
        // Basic functions
        "ln" to ModuleInstance(
            description = "Вычисляет натуральный логарифм ln(x)",
            function = { x, mc -> ln(x, BigDecimal("1E-50", mc), mc) }
        ),
        "log10" to ModuleInstance(
            description = "Вычисляет логарифм base 10",
            function = { x, mc -> log(x, BigDecimal(10), BigDecimal("1E-50", mc), mc) }
        ),
        "log2" to ModuleInstance(
            description = "Вычисляет логарифм base 2",
            function = { x, mc -> log(x, BigDecimal(2), BigDecimal("1E-50", mc), mc) }
        ),
        "log3" to ModuleInstance(
            description = "Вычисляет логарифм base 3",
            function = { x, mc -> log(x, BigDecimal(3), BigDecimal("1E-50", mc), mc) }
        ),
        "log5" to ModuleInstance(
            description = "Вычисляет логарифм base 5",
            function = { x, mc -> log(x, BigDecimal(5), BigDecimal("1E-50", mc), mc) }
        ),
        "cos" to ModuleInstance(
            description = "Вычисляет cos(x)",
            function = { x, mc -> cos(x, BigDecimal("1E-50", mc), mc) }
        ),
        "sin" to ModuleInstance(
            description = "Вычисляет sin(x)",
            function = { x, mc -> sin(x, BigDecimal("1E-50", mc), mc) }
        ),
        "sec" to ModuleInstance(
            description = "Вычисляет sec(x)",
            function = { x, mc -> sec(x, BigDecimal("1E-50", mc), mc) }
        ),
        "csc" to ModuleInstance(
            description = "Вычисляет csc(x)",
            function = { x, mc -> csc(x, BigDecimal("1E-50", mc), mc) }
        ),
        "cot" to ModuleInstance(
            description = "Вычисляет cot(x)",
            function = { x, mc -> cot(x, BigDecimal("1E-50", mc), mc) }
        ),
        // Main combined expression
        "main" to ModuleInstance(
            description = "Вычисляет основное выражение (positive/negative)",
            function = { x, mc -> computeExpression(x, BigDecimal("1E-50", mc), mc) }
        )
    )

    override fun getModule(key: String): ModuleInstance? = modules[key]

    override fun getModulesDescriptions(): List<ModuleDescription> =
        modules.map { (name, instance) -> ModuleDescription(name, instance.description) }
}
