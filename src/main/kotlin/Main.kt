import java.io.File
import java.io.PrintWriter
import java.math.BigDecimal
import java.math.MathContext
import expression.*
import tpo.maxim.expression.*

fun main() {
    println("=== Консольное приложение для вычисления модулей ===\n")

    // Шаг 1: Вывод списка доступных модулей
    println("Доступные модули:")
    val allDescriptions = MainModuleHandler.getModuleDescription()
    allDescriptions.forEach { desc ->
        println("  ${desc.name} - ${desc.description}")
    }
    println()

    // Шаг 2: Ввод типа модуля
    val moduleType = readInputWithValidation(
        prompt = "Введите тип модуля (negative или positive): ",
        validator = { it in listOf("negative", "positive") },
        errorMessage = "Ошибка: тип модуля должен быть 'negative' или 'positive'. Попробуйте снова."
    )

    // Шаг 3: Ввод названия модуля
    val availableModules = MainModuleHandler.getModulesDescriptions(moduleType).toSet()
    println("\nДоступные модули для типа '$moduleType':")
    availableModules.forEach { desc ->
        println("  ${desc.name} - ${desc.description}")
    }

    val moduleName = readInputWithValidation(
        prompt = "\nВведите название модуля: ",
        validator = { name -> name in availableModules.map { it.name } },
        errorMessage = "Ошибка: неверное название модуля. Выберите из списка выше. Попробуйте снова."
    )

    // Шаг 4: Получение модуля
    val module = MainModuleHandler.getModule(moduleType, moduleName)
        ?: run {
            println("Ошибка: модуль '$moduleName' не найден.")
            return
        }

    // Шаг 5: Ввод начального значения x
    val startX = readBigDecimalInput("Введите начальное значение x: ")

    // Шаг 6: Ввод шага
    val step = readBigDecimalInput("Введите шаг: ")

    // Шаг 7: Ввод количества итераций
    val iterations = readIntInput("Введите количество итераций: ")

    // Шаг 8: Ввод имени выходного файла
    val outputFileName = readInputWithValidation(
        prompt = "Введите имя выходного CSV файла: ",
        validator = { fileName -> fileName.isNotBlank() && fileName.endsWith(".csv") },
        errorMessage = "Ошибка: имя файла должно быть не пустым и заканчиваться на '.csv'. Попробуйте снова."
    )

    // Шаг 9: Вычисление и запись в файл
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

/**
 * Читает ввод пользователя с валидацией
 */
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

/**
 * Читает BigDecimal значение с обработкой ошибок
 */
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

/**
 * Читает int значение с обработкой ошибок
 */
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
