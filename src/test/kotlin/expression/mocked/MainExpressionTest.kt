package expression.mocked

import expression.negativeExpression
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import expression.positiveExpression
import tpo.maxim.expression.computeExpression
import java.math.BigDecimal
import java.math.MathContext

class MainExpressionTest {

    private val mc = MathContext.DECIMAL128
    private val epsilon = BigDecimal("1E-20", mc)
    private val testEpsilon = BigDecimal("1E-9", mc)

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @ParameterizedTest
    @CsvSource(
        "-1.0, 5.0"
    )
    fun `Negative branch, мокаем всю ветку`(xStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val expectedResult = BigDecimal(expectedResultStr, mc)

        mockkStatic("expression.NegativeModulesKt")
        mockkStatic("expression.PositiveModulesKt")
        every { negativeExpression(any(), any(), any()) } returns BigDecimal(5, mc)
        every { positiveExpression(any(), any(), any()) } returns BigDecimal(10, mc)

        val result = computeExpression(x, epsilon, mc)

        assertEquals(expectedResult, result)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0",
        "2.0, 10.0"
    )
    fun `Positive branch, мокаем всю ветку`(xStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val expectedResult = BigDecimal(expectedResultStr, mc)

        mockkStatic("expression.NegativeModulesKt")
        mockkStatic("expression.PositiveModulesKt")
        every { negativeExpression(any(), any(), any()) } returns BigDecimal(5, mc)
        every { positiveExpression(any(), any(), any()) } returns BigDecimal(10, mc)

        val result = computeExpression(x, epsilon, mc)

        assertEquals(expectedResult, result)
    }


    @ParameterizedTest
    @CsvSource(
        "0.1, -19.056579481010992",
        "0.3, -4.064083218661359",
        "0.5, -1.4852500219232365",
        "1, 0",
        "3.2, 3.7833060107176113",
        "100.1, 131.37559989856018",

        )
    fun `Positive branch, без моков`(xStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)

        val result = computeExpression(x, epsilon, mc)
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "-1.3, -80860.44755328876",
        "-1.2, -3237.420375950311",
        "-0.7, -0.03257213586684889",
        "-0.2, -0.02908162888594679",
    )
    fun `Negative branch, без моков`(xStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)

        val result = computeExpression(x, epsilon, mc)
        assertEquals(expected, result)
    }

    private fun assertEquals(expected: BigDecimal, actual: BigDecimal) {
        assertTrue(
            expected.subtract(actual, mc).abs() <= testEpsilon,
            "Expected $expected, got $actual difference: ${expected.subtract(actual, mc)}"
        )
    }
}
