import java.io.File
import java.io.PrintWriter
import java.math.BigDecimal
import java.math.MathContext
import expression.*
import tpo.maxim.expression.*

fun main() {
    println("=== Консольное приложение для вычисления модулей ===\n")

    println("Доступные модуля:")
    val allDescriptions = UnifiedModuleHandler.getModulesDescriptions()
    allDescriptions.forEach { desc ->
        println("  ${desc.name} - ${desc.description}")
    }
    println()

    // Choose a module
    val moduleName = readInputWithValidation(
        prompt = "\nВведите название модуля: ",
        validator = { name -> name in allDescriptions.map { it.name } },
        errorMessage = "Ошибка: неверное название модуля. Выберите из списка выше. Попробуйте снова."
    )

    val module = UnifiedModuleHandler.getModule(moduleName)
        ?: run {
            println("Ошибка: модуль '$moduleName' не найден.")
            return
        }

    val startX = readBigDecimalInput("Введите начальное значение x: ")

    val step = readBigDecimalInput("Введите шаг: ")

    val iterations = readIntInput("Введите количество итераций: ")

    val outputFileName = readInputWithValidation(
        prompt = "Введите имя выходного CSV файла: ",
        validator = { fileName -> fileName.isNotBlank() && fileName.endsWith(".csv") },
        errorMessage = "Ошибка: имя файла должно быть не пустым и заканчиваться на '.csv'. Попробуйте снова."
    )

    val mc = MathContext.DECIMAL128
    try {
        PrintWriter(File(outputFileName)).use { writer ->
            writer.println("x,result")

            var x = startX
            for (i in 0 until iterations) {
                try {
                    val result = module.function(x, mc)
                    writer.println("$x,$result")
                    println("x = $x, result = $result")
                } catch (e: Exception) {
                    println("Ошибка вычисления при x = $x: ${e.message}")
                    writer.println("$x,ERROR: ${e.message}")
                }
                x = x.add(step, mc)
            }
        }
        println("\nРезультаты успешно записаны в файл: $outputFileName")
    } catch (e: Exception) {
        println("Ошибка записи в файл: ${e.message}")
    }
}

fun readInputWithValidation(
    prompt: String,
    validator: (String) -> Boolean,
    errorMessage: String
): String {
    while (true) {
        print(prompt)
        val input = readlnOrNull()?.trim()
        if (input != null && validator(input)) {
            return input
        }
        println(errorMessage)
    }
}

fun readBigDecimalInput(prompt: String): BigDecimal {
    while (true) {
        print(prompt)
        val input = readlnOrNull()?.trim()
        if (input != null) {
            try {
                return BigDecimal(input)
            } catch (e: NumberFormatException) {
                println("Ошибка: введите корректное числовое значение. Попробуйте снова.")
            }
        }
    }
}

fun readIntInput(prompt: String): Int {
    while (true) {
        print(prompt)
        val input = readlnOrNull()?.trim()
        if (input != null) {
            try {
                val value = input.toInt()
                if (value > 0) {
                    return value
                }
                println("Ошибка: количество итераций должно быть положительным. Попробуйте снова.")
            } catch (e: NumberFormatException) {
                println("Ошибка: введите корректное целое число. Попробуйте снова.")
            }
        }
    }
}
