package expression

import tpo.maxim.*

/**
 * Вычисляет ((((csc(x) / csc(x)) / sec(x)) - sin(x)) + cot(x)) ^ 3
 */
fun module1(x: Double, epsilon: Double = 1e-10): Double {
    val cscx = csc(x, epsilon)
    val secx = sec(x, epsilon)
    val sinx = sin(x, epsilon)
    val cotx = cot(x, epsilon)

    // (csc(x) / csc(x)) / sec(x) - sin(x) + cot(x)
    val step1 = cscx / cscx           // = 1
    val step2 = step1 / secx          // = cos(x)
    val step3 = step2 - sinx
    val step4 = step3 + cotx

    // Возведение в куб
    return step4 * step4 * step4
}

/**
 * Вычисляет cos(x) + csc(x)
 */
fun module2(x: Double, epsilon: Double = 1e-10): Double {
    val cosx = cos(x, epsilon)
    val cscx = csc(x, epsilon)
    return cosx + cscx
}

/**
 * Вычисляет sec(x) - sin(x)
 */
fun module3(x: Double, epsilon: Double = 1e-10): Double {
    val secx = sec(x, epsilon)
    val sinx = sin(x, epsilon)
    return secx - sinx
}

/**
 * Вычисляет ((...^3) + (cos(x) + csc(x))) - (sec(x) - sin(x))
 */
fun module4Numerator(x: Double, epsilon: Double = 1e-10): Double {
    val module1 = module1(x, epsilon)
    val module2 = module2(x, epsilon)
    val module3 = module3(x, epsilon)

    return (module1 + module2) - module3
}

/**
 * Вычисляет знаменатель (cot(x) ^ 2) / (csc(x) + sec(x))
 */
fun module4Denominator(x: Double, epsilon: Double = 1e-10): Double {
    val cotx = cot(x, epsilon)
    val cscx = csc(x, epsilon)
    val secx = sec(x, epsilon)

    val cotSquared = cotx * cotx
    val cscSecSum = cscx + secx

    return cotSquared / cscSecSum
}

/**
 * Вычисляет дробь: (...)/[(cot(x) ^ 2) / (csc(x) + sec(x))]
 */
fun module5(x: Double, epsilon: Double = 1e-10): Double {
    val numerator = module4Numerator(x, epsilon)
    val denominator = module4Denominator(x, epsilon)
    return numerator / denominator
}


/**
 * Вычисляет знаменатель всей большой дроби
 */
fun module6Numerator(x: Double, epsilon: Double = 1e-10): Double {
    val mainFraction = module5(x, epsilon)
    val sinx = sin(x, epsilon)
    val sinSquared = sinx * sinx

    val product = mainFraction * sinSquared
    return product * product
}

/**
 * Полное выражение для x <= 0
 */
fun negativeExpression(x: Double, epsilon: Double = 1e-10): Double {
    require(x <= 0) { "x должен быть <= 0" }

    val module8 = module6Numerator(x, epsilon)
    val cotx = cot(x, epsilon)

    return module8 / cotx
}