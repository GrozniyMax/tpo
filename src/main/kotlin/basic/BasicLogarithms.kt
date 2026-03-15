package tpo.maxim

import kotlin.math.abs

fun ln(x: Double, epsilon: Double = 1e-10): Double {
    if (x <= 0) {
        return Double.NaN
    }

    val t = (x - 1.0) / (x + 1.0)
    val tSquared = t * t
    
    var result = 0.0
    var term = t
    var n = 1
    
    while (abs(term) >= epsilon) {
        result += term / (2 * n - 1)
        term *= tSquared
        n++
    }
    
    return 2.0 * result
}

fun log(x: Double, base: Double, epsilon: Double = 1e-10): Double {
    if (x <= 0 || base <= 0 || base == 1.0) {
        return Double.NaN
    }
    return ln(x, epsilon) / ln(base, epsilon)
}
