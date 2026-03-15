import expression.*
import tpo.maxim.expression.*

data class ModuleInstance(
    val description: String,
    val function: (Double, Double) -> Double
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
            function = ::module1
        ),
        "module2" to ModuleInstance(
            description = "Вычисляет cos(x) + csc(x)",
            function = ::module2
        ),
        "module3" to ModuleInstance(
            description = "Вычисляет sec(x) - sin(x)",
            function = ::module3
        ),
        "module4Numerator" to ModuleInstance(
            description = "Вычисляет ((...^3) + (cos(x) + csc(x))) - (sec(x) - sin(x))",
            function = ::module4Numerator
        ),
        "module4Denominator" to ModuleInstance(
            description = "Вычисляет знаменатель (cot(x) ^ 2) / (csc(x) + sec(x))",
            function = ::module4Denominator
        ),
        "module5" to ModuleInstance(
            description = "Вычисляет дробь: (...)/[(cot(x) ^ 2) / (csc(x) + sec(x))]",
            function = ::module5
        ),
        "module8Numerator" to ModuleInstance(
            description = "Вычисляет знаменатель всей большой дроби",
            function = ::module8Numerator
        ),
        "negativeExpression" to ModuleInstance(
            description = "Полное выражение для x <= 0",
            function = ::negativeExpression
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
            function = ::log10Squared
        ),
        "multiplyByLog2" to ModuleInstance(
            description = "Вычисляет: (log_10(x) * log_10(x)) * log_2(x)",
            function = ::multiplyByLog2
        ),
        "addLog3" to ModuleInstance(
            description = "Вычисляет: ((log_10(x) * log_10(x)) * log_2(x)) + log_3(x)",
            function = ::addLog3
        ),
        "addLog5" to ModuleInstance(
            description = "Вычисляет: (((log_10(x) * log_10(x)) * log_2(x)) + log_3(x)) + log_5(x)",
            function = ::addLog5
        ),
        "lnCubed" to ModuleInstance(
            description = "Вычисляет: ln(x) ^ 3",
            function = ::lnCubed
        ),
        "positiveExpression" to ModuleInstance(
            description = "Вычисляет полное выражение для x > 0",
            function = ::positiveExpression
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
