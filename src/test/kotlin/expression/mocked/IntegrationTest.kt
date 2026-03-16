package expression.mocked

import basic.*
import expression.negativeExpression
import expression.positiveExpression
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal
import java.math.MathContext

class IntegrationTest {

    private val mc = MathContext.DECIMAL128
    private val epsilon = BigDecimal("1E-20", mc)
    private val testEpsilon = BigDecimal("1E-5", mc)

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @ParameterizedTest
    @CsvSource(
        "-0.9, -1.0484",
        "-0.78539816339, 0", // Корень
        "-0.75, -0.0114897",
        "-0.65, -0.0394723",// Перегиб
        "-0.5, -0.0198088",
        "-0.38107, -0.012414", // Перегиб
        "-0.3, -0.015755",
        "-0.102293, -0.041179", // Перегиб
        "-0.1, -0.0411668",
        "0.0, 0.0", // Корень
        "0.5, 4.78228",
        "1.0265, 25.16401", // Перегиб
        "1.1, 14.0991",
        "1.145107, 0", // Корень,
        "1.15, 0.479141",
        "1.925, -0.0437806",
        "1.925476, 0", // Корень
        "1.926, -0.0516775",
        "1.9, -248.8682731",
        "1.95, -60.4606740",
        "2.009365, -163.99001", // Перегиб
        "2.2, -18.5296303",
        "2.35619449019, 0.0", // Корень
        "2.4, -0.347299",
        "2.59021, -2.11154", // Перегиб
        "2.8, -1.11771",
        "3.14159265359, 0.0",
        "3.35301, 0.086265", // Перегиб
        "3.5, 0.0613175",
        "3.70317, 0.0", //Корень + Перегиб
        "3.8, 0.050085",
        "4.00047, 0.493097", // Перегиб
        "4.005, 0.492161",
        "4.10865, 0.0", //Корень + Перегиб
        "4.2, 14.348472"
    )
    fun `Negative Branch без моков`(xStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedResultStr, mc)

        val result = negativeExpression(x, epsilon, mc)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "-0.9, 16331156536454710.122161, 16331239623605928.618784, -1.0484",
        "-0.78539816339, 16331156536454710.122161, 16331239623605928.618784, 0",
        "-0.75, 16331156536454710.122161, 16331239623605928.618784, -0.0114897",
        "-0.65, 16331156536454710.122161, 16331239623605928.618784, -0.0394723",
        "-0.5, 16331156536454710.122161, 16331239623605928.618784, -0.0198088",
        "-0.38107, 16331156536454710.122161, 16331239623605928.618784, -0.012414",
        "-0.3, 16331156536454710.122161, 16331239623605928.618784, -0.015755",
        "-0.102293, 16331156536454710.122161, 16331239623605928.618784, -0.041179",
        "-0.1, 16331156536454710.122161, 16331239623605928.618784, -0.0411668",
        "0.0, 16331156536454710.122161, 16331239623605928.618784, 0.0",
        "0.5, 16331156536454710.122161, 16331239623605928.618784, 4.78228",
        "1.0265, 16331156536454710.122161, 16331239623605928.618784, 25.16401",
        "1.1, 16331156536454710.122161, 16331239623605928.618784, 14.0991",
        "1.145107, 16331156536454710.122161, 16331239623605928.618784, 0",
        "1.15, 16331156536454710.122161, 16331239623605928.618784, 0.479141",
        "1.925, 16331156536454710.122161, 16331239623605928.618784, -0.0437806",
        "1.925476, 16331156536454710.122161, 16331239623605928.618784, 0",
        "1.926, 16331156536454710.122161, 16331239623605928.618784, -0.0516775",
        "1.9, 16331156536454710.122161, 16331239623605928.618784, -248.8682731",
        "1.95, 16331156536454710.122161, 16331239623605928.618784, -60.4606740",
        "2.009365, 16331156536454710.122161, 16331239623605928.618784, -163.99001",
        "2.2, 16331156536454710.122161, 16331239623605928.618784, -18.5296303",
        "2.35619449019, 16331156536454710.122161, 16331239623605928.618784, 0.0",
        "2.4, 16331156536454710.122161, 16331239623605928.618784, -0.347299",
        "2.59021, 16331156536454710.122161, 16331239623605928.618784, -2.11154",
        "2.8, 16331156536454710.122161, 16331239623605928.618784, -1.11771",
        "3.14159265359, 16331156536454710.122161, 16331239623605928.618784, 0.0",
        "3.35301, 16331156536454710.122161, 16331239623605928.618784, 0.086265",
        "3.5, 16331156536454710.122161, 16331239623605928.618784, 0.0613175",
        "3.70317, 16331156536454710.122161, 16331239623605928.618784, 0.0",
        "3.8, 16331156536454710.122161, 16331239623605928.618784, 0.050085",
        "4.00047, 16331156536454710.122161, 16331239623605928.618784, 0.493097",
        "4.005, 16331156536454710.122161, 16331239623605928.618784, 0.492161",
        "4.10865, 16331156536454710.122161, 16331239623605928.618784, 0.0",
        "4.2, 16331156536454710.122161, 16331239623605928.618784, 14.348472"
    )
    fun `Negative Branch level1`(xStr: String, cscStr: String, cotStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val csc = BigDecimal(cscStr, mc)
        val cot = BigDecimal(cotStr, mc)
        val expected = BigDecimal(expectedResultStr, mc)

        mockkStatic("basic.BasicTrigonometryKt")
        every { csc(any()) } returns csc
        every { cot(any()) } returns cot

        val result = negativeExpression(x, epsilon, mc)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "-0.9, 6.123265E-17, 1, -1.0484",
        "-0.78539816339, 6.123265E-17, 1, 0",
        "-0.75, 6.123265E-17, 1, -0.0114897",
        "-0.65, 6.123265E-17, 1, -0.0394723",
        "-0.5, 6.123265E-17, 1, -0.0198088",
        "-0.38107, 6.123265E-17, 1, -0.012414",
        "-0.3, 6.123265E-17, 1, -0.015755",
        "-0.102293, 6.123265E-17, 1, -0.041179",
        "-0.1, 6.123265E-17, 1, -0.0411668",
        "0.0, 6.123265E-17, 1, 0.0",
        "0.5, 6.123265E-17, 1, 4.78228",
        "1.0265, 6.123265E-17, 1, 25.16401",
        "1.1, 6.123265E-17, 1, 14.0991",
        "1.145107, 6.123265E-17, 1, 0",
        "1.15, 6.123265E-17, 1, 0.479141",
        "1.925, 6.123265E-17, 1, -0.0437806",
        "1.925476, 6.123265E-17, 1, 0",
        "1.926, 6.123265E-17, 1, -0.0516775",
        "1.9, 6.123265E-17, 1, -248.8682731",
        "1.95, 6.123265E-17, 1, -60.4606740",
        "2.009365, 6.123265E-17, 1, -163.99001",
        "2.2, 6.123265E-17, 1, -18.5296303",
        "2.35619449019, 6.123265E-17, 1, 0.0",
        "2.4, 6.123265E-17, 1, -0.347299",
        "2.59021, 6.123265E-17, 1, -2.11154",
        "2.8, 6.123265E-17, 1, -1.11771",
        "3.14159265359, 6.123265E-17, 1, 0.0",
        "3.35301, 6.123265E-17, 1, 0.086265",
        "3.5, 6.123265E-17, 1, 0.0613175",
        "3.70317, 6.123265E-17, 1, 0.0",
        "3.8, 6.123265E-17, 1, 0.050085",
        "4.00047, 6.123265E-17, 1, 0.493097",
        "4.005, 6.123265E-17, 1, 0.492161",
        "4.10865, 6.123265E-17, 1, 0.0",
        "4.2, 6.123265E-17, 1, 14.348472"
    )
    fun `Negative Branch level2`(xStr: String, sinStr: String, secStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val sin = BigDecimal(sinStr, mc)
        val sec = BigDecimal(secStr, mc)
        val expected = BigDecimal(expectedResultStr, mc)

        mockkStatic("basic.BasicTrigonometryKt")
        every { sin(any()) } returns sin
        every { sec(any()) } returns sec

        val result = negativeExpression(x, epsilon, mc)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "-0.9, 1, -1.0484",
        "-0.78539816339, 1, 0",
        "-0.75, 1, -0.0114897",
        "-0.65, 1, -0.0394723",
        "-0.5, 1, -0.0198088",
        "-0.38107, 1, -0.012414",
        "-0.3, 1, -0.015755",
        "-0.102293, 1, -0.041179",
        "-0.1, 1, -0.0411668",
        "0.0, 1, 0.0",
        "0.5, 1, 4.78228",
        "1.0265, 1, 25.16401",
        "1.1, 1, 14.0991",
        "1.145107, 1, 0",
        "1.15, 1, 0.479141",
        "1.925, 1, -0.0437806",
        "1.925476, 1, 0",
        "1.926, 1, -0.0516775",
        "1.9, 1, -248.8682731",
        "1.95, 1, -60.4606740",
        "2.009365, 1, -163.99001",
        "2.2, 1, -18.5296303",
        "2.35619449019, 1, 0.0",
        "2.4, 1, -0.347299",
        "2.59021, 1, -2.11154",
        "2.8, 1, -1.11771",
        "3.14159265359, 1, 0.0",
        "3.35301, 1, 0.086265",
        "3.5, 1, 0.0613175",
        "3.70317, 1, 0.0",
        "3.8, 1, 0.050085",
        "4.00047, 1, 0.493097",
        "4.005, 1, 0.492161",
        "4.10865, 1, 0.0",
        "4.2, 1, 14.348472"
    )
    fun `Negative Branch level2`(xStr: String, cosStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val cos = BigDecimal(cosStr, mc)
        val expected = BigDecimal(expectedResultStr, mc)

        mockkStatic("basic.BasicTrigonometryKt")
        every { cos(any()) } returns cos

        val result = negativeExpression(x, epsilon, mc)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "0.5, -1.48525",
        "1.0, 0.0",
        "4.1, 5.73453"
    )
    fun `Positive Branch без моков`(xStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedResultStr, mc)

        val result = positiveExpression(x, epsilon, mc)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "0.5, -1, -0.63092975, -0.43067, -0.301029, -1.48525",
        "1.0, 0E+34, 0E+33, 0E+33, 0E+33, 0.0",
        "4.1, 2.035623, 1.2843356, 0.876695, 0.612783, 5.73453"
    )
    fun `Positive Branch level1`(
        xStr: String,
        log2Str: String,
        log3Str: String,
        log5Str: String,
        log10Str: String,
        expectedResultStr: String
    ) {
        val x = BigDecimal(xStr, mc)
        val log2 = BigDecimal(log2Str, mc)
        val log3 = BigDecimal(log3Str, mc)
        val log5 = BigDecimal(log5Str, mc)
        val log10 = BigDecimal(log10Str, mc)
        val expected = BigDecimal(expectedResultStr, mc)

        mockkStatic("basic.BasicLogarithmsKt")
        every { log(any(), BigDecimal(2), any(), any()) } returns log2
        every { log(any(), BigDecimal(3), any(), any()) } returns log3
        every { log(any(), BigDecimal(5), any(), any()) } returns log5
        every { log(any(), BigDecimal(10), any(), any()) } returns log10

        val result = positiveExpression(x, epsilon, mc)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "0.5, -0.6931471, -1.48525",
        "1.0, 0, 0.0",
        "4.1, 1.4109869, 5.73453",
    )
    fun `Positive Branch level2`(xStr: String, lnStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val ln = BigDecimal(lnStr, mc)
        val expected = BigDecimal(expectedResultStr, mc)

        mockkStatic("basic.BasicLogarithmsKt")
        every { ln(any()) } returns ln

        val result = positiveExpression(x, epsilon, mc)
        assertEquals(expected, result)
    }

    private fun assertEquals(expected: BigDecimal, actual: BigDecimal) {
        assertTrue(
            expected.subtract(actual, mc).abs() <= testEpsilon,
            "Expected $expected, got $actual difference: ${expected.subtract(actual, mc)}"
        )
    }

}
