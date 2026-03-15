package expression.mocked

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import tpo.maxim.expression.*
import tpo.maxim.ln
import tpo.maxim.log
import java.math.BigDecimal
import java.math.MathContext

class PositiveModulesTest {

    private val mc = MathContext.DECIMAL128
    private val epsilon = BigDecimal("1E-10", mc)
    private val testEpsilon = BigDecimal("1E-9", mc)

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0, 0.0, 0.0",
        "10.0, 10.0, 1.0, 1.0",
        "100.0, 10.0, 2.0, 4.0",
        "1000.0, 10.0, 3.0, 9.0",
        "2.0, 2.0, 1.0, 1.0",
        "4.0, 2.0, 2.0, 4.0",
        "8.0, 2.0, 3.0, 9.0"
    )
    fun `Проверка вычисления log10Squared`(xStr: String, baseStr: String, logResultStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        val logResult = BigDecimal(logResultStr, mc)
        
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(x, BigDecimal(10), epsilon, mc) } returns logResult

        val result = log10Squared(x, epsilon, mc)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0, 0.0, 2.0, 0.0, 0.0, 0.0",
        "10.0, 10.0, 1.0, 2.0, 1.0, 1.0, 1.0",
        "100.0, 10.0, 2.0, 2.0, 2.0, 8.0, 8.0",
        "1000.0, 10.0, 3.0, 2.0, 3.0, 27.0, 27.0",
        "2.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0",
        "4.0, 2.0, 2.0, 1.0, 2.0, 8.0, 8.0",
        "8.0, 2.0, 3.0, 1.0, 3.0, 27.0, 27.0"
    )
    fun `Проверка вычисления multiplyByLog2`(xStr: String, base10Str: String, log10ResultStr: String, base2Str: String, log2ResultStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        val log10Result = BigDecimal(log10ResultStr, mc)
        val log2Result = BigDecimal(log2ResultStr, mc)
        
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(x, BigDecimal(10), epsilon, mc) } returns log10Result
        every { log(x, BigDecimal(2), epsilon, mc) } returns log2Result

        val result = multiplyByLog2(x, epsilon, mc)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0, 0.0, 2.0, 0.0, 3.0, 0.0, 0.0",
        "10.0, 10.0, 1.0, 2.0, 1.0, 3.0, 1.0, 2.0",
        "100.0, 10.0, 2.0, 2.0, 2.0, 3.0, 2.0, 10.0",
        "1000.0, 10.0, 3.0, 2.0, 3.0, 3.0, 3.0, 30.0"
    )
    fun `Проверка вычисления addLog3`(xStr: String, base10Str: String, log10ResultStr: String, base2Str: String, log2ResultStr: String, base3Str: String, log3ResultStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        val log10Result = BigDecimal(log10ResultStr, mc)
        val log2Result = BigDecimal(log2ResultStr, mc)
        val log3Result = BigDecimal(log3ResultStr, mc)
        
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(x, BigDecimal(10), epsilon, mc) } returns log10Result
        every { log(x, BigDecimal(2), epsilon, mc) } returns log2Result
        every { log(x, BigDecimal(3), epsilon, mc) } returns log3Result

        val result = addLog3(x, epsilon, mc)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0, 0.0, 2.0, 0.0, 3.0, 0.0, 5.0, 0.0, 0.0",
        "10.0, 10.0, 1.0, 2.0, 1.0, 3.0, 1.0, 5.0, 1.0, 3.0",
        "100.0, 10.0, 2.0, 2.0, 2.0, 3.0, 2.0, 5.0, 2.0, 12.0"
    )
    fun `Проверка вычисления addLog5`(xStr: String, base10Str: String, log10ResultStr: String, base2Str: String, log2ResultStr: String, base3Str: String, log3ResultStr: String, base5Str: String, log5ResultStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        val log10Result = BigDecimal(log10ResultStr, mc)
        val log2Result = BigDecimal(log2ResultStr, mc)
        val log3Result = BigDecimal(log3ResultStr, mc)
        val log5Result = BigDecimal(log5ResultStr, mc)
        
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(x, BigDecimal(10), epsilon, mc) } returns log10Result
        every { log(x, BigDecimal(2), epsilon, mc) } returns log2Result
        every { log(x, BigDecimal(3), epsilon, mc) } returns log3Result
        every { log(x, BigDecimal(5), epsilon, mc) } returns log5Result

        val result = addLog5(x, epsilon, mc)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 0.0, 0.0",
        "2.718281828459045, 1.0, 1.0",
        "7.38905609893065, 2.0, 8.0",
        "20.085536923187668, 3.0, 27.0"
    )
    fun `Проверка вычисления lnCubed`(xStr: String, lnResultStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        val lnResult = BigDecimal(lnResultStr, mc)
        
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { ln(x, epsilon, mc) } returns lnResult

        val result = lnCubed(x, epsilon, mc)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0, 0.0, 2.0, 0.0, 3.0, 0.0, 5.0, 0.0, 0.0, 0.0, 0.0",
        "10.0, 10.0, 1.0, 2.0, 1.0, 3.0, 1.0, 5.0, 1.0, 1.0, 1.0, 4.0"
    )
    fun `Проверка вычисления positiveExpression`(
        xStr: String,
        base10Str: String, log10ResultStr: String,
        base2Str: String, log2ResultStr: String,
        base3Str: String, log3ResultStr: String,
        base5Str: String, log5ResultStr: String,
        lnBaseStr: String, lnResultStr: String,
        expectedStr: String
    ) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        val log10Result = BigDecimal(log10ResultStr, mc)
        val log2Result = BigDecimal(log2ResultStr, mc)
        val log3Result = BigDecimal(log3ResultStr, mc)
        val log5Result = BigDecimal(log5ResultStr, mc)
        val lnResult = BigDecimal(lnResultStr, mc)
        
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(x, BigDecimal(10), epsilon, mc) } returns log10Result
        every { log(x, BigDecimal(2), epsilon, mc) } returns log2Result
        every { log(x, BigDecimal(3), epsilon, mc) } returns log3Result
        every { log(x, BigDecimal(5), epsilon, mc) } returns log5Result
        every { ln(x, epsilon, mc) } returns lnResult

        val result = positiveExpression(x, epsilon, mc)

        assertEquals(expected, result)
    }
    
    private fun assertEquals(expected: BigDecimal, actual: BigDecimal) {
        assertTrue(expected.subtract(actual, mc).abs() <= testEpsilon, "Expected $expected, got $actual")
    }
}
