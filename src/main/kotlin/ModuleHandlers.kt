import expression.*
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

object MainModuleHandler {
    fun getModule(prefix: String, key: String) = when (prefix) {
        "negative" -> NegativeModuleHandler.getModule(key)
        "positive" -> PositiveModuleHandler.getModule(key)
        else -> null
    }

    fun getModulesDescriptions(prefix: String) = when (prefix) {
        "negative" -> NegativeModuleHandler.getModulesDescriptions()
        "positive" -> PositiveModuleHandler.getModulesDescriptions()
        else -> emptyList()
    }

    fun getModuleDescription(): List<ModuleDescription> {
        val negative = NegativeModuleHandler.getModulesDescriptions().map { it.copy(name = "negative: " + it.name) }
        val positive = PositiveModuleHandler.getModulesDescriptions().map { it.copy(name = "positive: " + it.name) }
        return negative + positive
    }
}

/**
 * Handler for negative expression modules (x <= 0)
 */
private object NegativeModuleHandler : ModuleHandler {
    private val modules: Map<String, ModuleInstance> = mapOf(
        "module1" to ModuleInstance(
            description = "Вычисляет ((((csc(x) / csc(x)) / sec(x)) - sin(x)) + cot(x)) ^ 3",
            function = { x, mc -> module1(x, BigDecimal("1E-50", mc), mc) }
        ),
        "module2" to ModuleInstance(
            description = "Вычисляет cos(x) + csc(x)",
            function = { x, mc -> module2(x, BigDecimal("1E-50", mc), mc) }
        ),
        "module3" to ModuleInstance(
            description = "Вычисляет sec(x) - sin(x)",
            function = { x, mc -> module3(x, BigDecimal("1E-50", mc), mc) }
        ),
        "module4Numerator" to ModuleInstance(
            description = "Вычисляет ((...^3) + (cos(x) + csc(x))) - (sec(x) - sin(x))",
            function = { x, mc -> module4Numerator(x, BigDecimal("1E-50", mc), mc) }
        ),
        "module4Denominator" to ModuleInstance(
            description = "Вычисляет знаменатель (cot(x) ^ 2) / (csc(x) + sec(x))",
            function = { x, mc -> module4Denominator(x, BigDecimal("1E-50", mc), mc) }
        ),
        "module5" to ModuleInstance(
            description = "Вычисляет дробь: (...)/[(cot(x) ^ 2) / (csc(x) + sec(x))]",
            function = { x, mc -> module5(x, BigDecimal("1E-50", mc), mc) }
        ),
        "module6Numerator" to ModuleInstance(
            description = "Вычисляет знаменатель всей большой дроби",
            function = { x, mc -> module6Numerator(x, BigDecimal("1E-50", mc), mc) }
        ),
        "negativeExpression" to ModuleInstance(
            description = "Полное выражение для x <= 0",
            function = { x, mc -> negativeExpression(x, BigDecimal("1E-50", mc), mc) }
        )
    )

    override fun getModule(key: String): ModuleInstance? =
        modules[key]


    override fun getModulesDescriptions(): List<ModuleDescription> =
        modules.map { (name, instance) ->
            ModuleDescription(name, instance.description)
        }

}

/**
 * Handler for positive expression modules (x > 0)
 */
private object PositiveModuleHandler: ModuleHandler {
    private val modules: Map<String, ModuleInstance> = mapOf(
        "log10Squared" to ModuleInstance(
            description = "Вычисляет: log_10(x) * log_10(x)",
            function = { x, mc -> log10Squared(x, BigDecimal("1E-50", mc), mc) }
        ),
        "multiplyByLog2" to ModuleInstance(
            description = "Вычисляет: (log_10(x) * log_10(x)) * log_2(x)",
            function = { x, mc -> multiplyByLog2(x, BigDecimal("1E-50", mc), mc) }
        ),
        "addLog3" to ModuleInstance(
            description = "Вычисляет: ((log_10(x) * log_10(x)) * log_2(x)) + log_3(x)",
            function = { x, mc -> addLog3(x, BigDecimal("1E-50", mc), mc) }
        ),
        "addLog5" to ModuleInstance(
            description = "Вычисляет: (((log_10(x) * log_10(x)) * log_10(x)) + log_3(x)) + log_5(x)",
            function = { x, mc -> addLog5(x, BigDecimal("1E-50", mc), mc) }
        ),
        "lnCubed" to ModuleInstance(
            description = "Вычисляет: ln(x) ^ 3",
            function = { x, mc -> lnCubed(x, BigDecimal("1E-50", mc), mc) }
        ),
        "positiveExpression" to ModuleInstance(
            description = "Вычисляет полное выражение для x > 0",
            function = { x, mc -> positiveExpression(x, BigDecimal("1E-50", mc), mc) }
        )
    )

    override fun getModule(key: String): ModuleInstance? {
        return modules[key]
    }

    override fun getModulesDescriptions(): List<ModuleDescription> {
        return modules.map { (name, instance) ->
            ModuleDescription(name, instance.description)
        }
    }
}
