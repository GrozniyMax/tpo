package tpo.maxim

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal
import java.math.MathContext

class BasicLogarithmsTest {

    private val mc = MathContext.DECIMAL128
    private val epsilon = BigDecimal("1E-10", mc)
    private val testEpsilon = BigDecimal("1E-9", mc)

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
    fun `проверка значений ln`(xStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        val result = ln(x, epsilon, mc)
        assertTrue(expected.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "ln($x) = $result, expected $expected")
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
    fun `проверка значений log`(xStr: String, baseStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val base = BigDecimal(baseStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        val result = log(x, base, epsilon, mc)
        assertTrue(expected.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "log($x, $base) = $result, expected $expected")
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 10.0",
        "-1.0, 10.0",
        "-100.0, 2.0"
    )
    fun `ln должен выбрасывать исключение для недопустимых значений`(xStr: String, baseStr: String) {
        val x = BigDecimal(xStr, mc)
        assertThrows(IllegalArgumentException::class.java) {
            ln(x, epsilon, mc)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "10.0, 0.0",
        "10.0, -1.0",
        "10.0, 1.0"
    )
    fun `log должен выбрасывать исключение для недопустимых значений`(xStr: String, baseStr: String) {
        val x = BigDecimal(xStr, mc)
        val base = BigDecimal(baseStr, mc)
        assertThrows(IllegalArgumentException::class.java) {
            log(x, base, epsilon, mc)
        }
    }
}
