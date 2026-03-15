package tpo.maxim

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BasicLogarithmsTest {

    private val epsilon = 1e-9

    @ParameterizedTest
    @CsvSource(
        "1.0, 0.0",
        "2.718281828459045, 1.0",
        "7.38905609893065, 2.0",
        "20.085536923187668, 3.0",
        "0.36787944117144233, -1.0",
        "0.1353352832366127, -2.0",
        "0.049787068367863944, -3.0",
        "1.5, 0.4054651081081644",
        "2.0, 0.6931471805599453",
        "2.5, 0.9162907318741551",
        "3.0, 1.0986122886681098",
        "4.0, 1.3862943611198906",
        "5.0, 1.6094379124341003",
        "10.0, 2.302585092994046"
    )
    fun `проверка значений ln`(x: Double, expected: Double) {
        val result = ln(x, 1e-10)
        assertEquals(expected, result, epsilon)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0, 0.0",
        "10.0, 10.0, 1.0",
        "100.0, 10.0, 2.0",
        "1000.0, 10.0, 3.0",
        "1.0, 2.0, 0.0",
        "2.0, 2.0, 1.0",
        "4.0, 2.0, 2.0",
        "8.0, 2.0, 3.0",
        "16.0, 2.0, 4.0",
        "27.0, 3.0, 3.0",
        "81.0, 3.0, 4.0",
        "100.0, 100.0, 1.0",
        "2.718281828459045, 2.718281828459045, 1.0"
    )
    fun `проверка значений log`(x: Double, base: Double, expected: Double) {
        val result = log(x, base, 1e-10)
        assertEquals(expected, result, epsilon)
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 10.0",
        "-1.0, 10.0",
        "-100.0, 2.0"
    )
    fun `ln должен возвращать NaN для недопустимых значений`(x: Double, base: Double) {
        val actual = ln(x, 1e-10)
        assertEquals(Double.NaN, actual)
    }

    @ParameterizedTest
    @CsvSource(
        "10.0, 0.0",
        "10.0, -1.0",
        "10.0, 1.0"
    )
    fun `log должен возвращать NaN для недопустимых значений`(x: Double, base: Double) {
        val actual = log(x, base, 1e-10)
        assertEquals(Double.NaN, actual)
    }
}
