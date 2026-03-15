package tpo.maxim

import kotlin.math.E
import kotlin.math.abs

fun ln(x: Double, epsilon: Double = 1e-10): Double {
    require(x > 0) { "ln определён только для x > 0" }

    // Для ускорения сходимости при больших x используем свойство:
    // ln(x) = ln(x / e^k) + k, где k подбираем так, чтобы x / e^k было близко к 1
    val e = E
    var adjustedX = x
    var adjustment = 0

    // Приводим x к диапазону [0.5, 2] для лучшей сходимости
    while (adjustedX > 2) {
        adjustedX /= e
        adjustment++
    }
    while (adjustedX < 0.5) {
        adjustedX *= e
        adjustment--
    }

    val t = (adjustedX - 1) / (adjustedX + 1)
    val tSquared = t * t

    var result = 0.0
    var term = t
    var n = 1

    while (abs(term) >= epsilon) {
        result += term
        term *= tSquared * (2 * n - 1) / (2 * n + 1)
        n++
    }

    return 2 * result + adjustment
}

fun log(x: Double, base: Double, epsilon: Double = 1e-10): Double {
    require(base > 0 && base != 1.0) { "Основание должно быть > 0 и != 1" }
    return ln(x, epsilon) / ln(base, epsilon)
}