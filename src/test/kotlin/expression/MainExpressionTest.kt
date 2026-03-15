package expression

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import tpo.maxim.csc
import tpo.maxim.sec
import tpo.maxim.sin
import tpo.maxim.cos
import tpo.maxim.cot
import tpo.maxim.expression.computeExpression
import tpo.maxim.expression.negativeExpression
import tpo.maxim.expression.positiveExpression
import tpo.maxim.log
import tpo.maxim.ln
import java.math.BigDecimal
import java.math.MathContext

class MainExpressionTest {

    private val mc = MathContext.DECIMAL128
    private val epsilon = BigDecimal("1E-10", mc)
    private val testEpsilon = BigDecimal("1E-9", mc)

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    // ========================================================================
    // УРОВЕНЬ 1: Полные заглушки - мокаем negativeExpression и positiveExpression
    // ========================================================================

    @ParameterizedTest
    @CsvSource(
        "-1.0, 5.0"
    )
    fun testComputeExpression_MockNegativeBranch(xStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val expectedResult = BigDecimal(expectedResultStr, mc)
        
        mockkStatic("tpo.maxim.expression.NegativeModulesKt")
        mockkStatic("tpo.maxim.expression.PositiveModulesKt")
        every { negativeExpression(any(), any(), any()) } returns BigDecimal(5, mc)
        every { positiveExpression(any(), any(), any()) } returns BigDecimal(10, mc)

        val result = computeExpression(x, epsilon, mc)

        assertTrue(expectedResult.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "Expected $expectedResult, got $result")
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0",
        "2.0, 10.0"
    )
    fun testComputeExpression_MockPositiveBranch(xStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val expectedResult = BigDecimal(expectedResultStr, mc)
        
        mockkStatic("tpo.maxim.expression.NegativeModulesKt")
        mockkStatic("tpo.maxim.expression.PositiveModulesKt")
        every { negativeExpression(any(), any(), any()) } returns BigDecimal(5, mc)
        every { positiveExpression(any(), any(), any()) } returns BigDecimal(10, mc)

        val result = computeExpression(x, epsilon, mc)

        assertTrue(expectedResult.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "Expected $expectedResult, got $result")
    }

    // ========================================================================
    // УРОВЕНЬ 2: Мокаем только positiveExpression, negativeExpression вычисляется
    // ========================================================================

    @ParameterizedTest
    @CsvSource(
        "-3.141592653589793, 0.0"
    )
    fun testComputeExpression_NegativeBranch_NoMocks(xStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val expectedResult = BigDecimal(expectedResultStr, mc)
        
        mockkStatic("tpo.maxim.expression.PositiveModulesKt")
        every { positiveExpression(any(), any(), any()) } returns BigDecimal(10, mc)

        val result = computeExpression(x, epsilon, mc)

        assertTrue(expectedResult.subtract(result, mc).abs().compareTo(BigDecimal("1E-3", mc)) <= 0, "Expected $expectedResult, got $result")
    }

    // ========================================================================
    // УРОВЕНЬ 3: Мокаем только negativeExpression, positiveExpression вычисляется
    // ========================================================================

    @ParameterizedTest
    @CsvSource(
        "1.0, 0.0"
    )
    fun testComputeExpression_PositiveBranch_NoMocks(xStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val expectedResult = BigDecimal(expectedResultStr, mc)
        
        mockkStatic("tpo.maxim.expression.NegativeModulesKt")
        every { negativeExpression(any(), any(), any()) } returns BigDecimal(5, mc)

        val result = computeExpression(x, epsilon, mc)

        assertTrue(expectedResult.subtract(result, mc).abs().compareTo(BigDecimal("1E-3", mc)) <= 0, "Expected $expectedResult, got $result")
    }

    // ========================================================================
    // УРОВЕНЬ 4: Мокаем тригонометрию - все функции возвращают 1
    // negativeExpression при csc=sec=sin=cos=cot=1 должен вернуть 36
    // ========================================================================

    @ParameterizedTest
    @CsvSource(
        "-1.0, 36.0"
    )
    fun testComputeExpression_NegativeBranch_MockTrig(xStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val expectedResult = BigDecimal(expectedResultStr, mc)
        
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { csc(any(), any(), any()) } returns BigDecimal(1, mc)
        every { sec(any(), any(), any()) } returns BigDecimal(1, mc)
        every { sin(any(), any(), any()) } returns BigDecimal(1, mc)
        every { cos(any(), any(), any()) } returns BigDecimal(1, mc)
        every { cot(any(), any(), any()) } returns BigDecimal(1, mc)
        mockkStatic("tpo.maxim.expression.PositiveModulesKt")
        every { positiveExpression(any(), any(), any()) } returns BigDecimal(10, mc)

        val result = computeExpression(x, epsilon, mc)

        assertTrue(expectedResult.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "Expected $expectedResult, got $result")
    }

    // ========================================================================
    // УРОВЕНЬ 5: Мокаем логарифмы - все функции возвращают 1
    // positiveExpression при log=ln=1 должен вернуть 4
    // ========================================================================

    @ParameterizedTest
    @CsvSource(
        "1.0, 4.0",
        "2.0, 4.0"
    )
    fun testComputeExpression_PositiveBranch_MockLog(xStr: String, expectedResultStr: String) {
        val x = BigDecimal(xStr, mc)
        val expectedResult = BigDecimal(expectedResultStr, mc)
        
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(any(), any(), any(), any()) } returns BigDecimal(1, mc)
        every { ln(any(), any(), any()) } returns BigDecimal(1, mc)

        val result = computeExpression(x, epsilon, mc)

        assertTrue(expectedResult.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "Expected $expectedResult, got $result")
    }

    // ========================================================================
    // УРОВЕНЬ 6: Интеграционный тест без заглушек - реальные вычисления
    // ========================================================================

    @ParameterizedTest
    @CsvSource(
        "1.0, 0.0"
    )
    fun testComputeExpression_NoStubs_Positive(xStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        
        val result = computeExpression(x, epsilon, mc)
        assertTrue(expected.subtract(result, mc).abs().compareTo(BigDecimal("1E-6", mc)) <= 0, "Expected $expected, got $result")
    }

    @ParameterizedTest
    @CsvSource(
        "-3.141592653589793, 0.0"
    )
    fun testComputeExpression_NoStubs_Negative(xStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        
        val result = computeExpression(x, epsilon, mc)
        assertTrue(expected.subtract(result, mc).abs().compareTo(BigDecimal("1E-3", mc)) <= 0, "Expected $expected, got $result")
    }
}
