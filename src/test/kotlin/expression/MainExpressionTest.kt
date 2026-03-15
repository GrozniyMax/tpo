package expression

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import tpo.maxim.*
import tpo.maxim.expression.addLog5
import tpo.maxim.expression.lnCubed
import tpo.maxim.expression.positiveExpression

class MainExpressionTest {

    private val epsilon = 1e-10

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    // ========================================================================
    // УРОВЕНЬ 1: Полные заглушки - мокаем negativeExpression и positiveExpression
    // ========================================================================

    @ParameterizedTest
    @CsvSource(
        "-1.0, 5.0",
        "0.0, 5.0"
    )
    fun testComputeExpression_MockNegativeBranch(x: Double, expectedResult: Double) {
        mockkStatic("expression.NegativeModulesKt")
        mockkStatic("tpo.maxim.expression.PositiveModulesKt")
        every { negativeExpression(any(), any()) } returns 5.0
        every { positiveExpression(any(), any()) } returns 10.0

        val result = computeExpression(x, epsilon)

        assertEquals(expectedResult, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0",
        "2.0, 10.0"
    )
    fun testComputeExpression_MockPositiveBranch(x: Double, expectedResult: Double) {
        mockkStatic("expression.NegativeModulesKt")
        mockkStatic("tpo.maxim.expression.PositiveModulesKt")
        every { negativeExpression(any(), any()) } returns 5.0
        every { positiveExpression(any(), any()) } returns 10.0

        val result = computeExpression(x, epsilon)

        assertEquals(expectedResult, result, 1e-9)
    }

    // ========================================================================
    // УРОВЕНЬ 2: Мокаем только positiveExpression, negativeExpression вычисляется
    // ========================================================================

    @ParameterizedTest
    @CsvSource(
        "-3.141592653589793, 0.0"
    )
    fun testComputeExpression_NegativeBranch_NoMocks(x: Double, expectedResult: Double) {
        mockkStatic("tpo.maxim.expression.PositiveModulesKt")
        every { positiveExpression(any(), any()) } returns 10.0

        val result = computeExpression(x, epsilon)

        assertEquals(expectedResult, result, 1e-3)
    }

    // ========================================================================
    // УРОВЕНЬ 3: Мокаем только negativeExpression, positiveExpression вычисляется
    // ========================================================================

    @ParameterizedTest
    @CsvSource(
        "1.0, 0.0"
    )
    fun testComputeExpression_PositiveBranch_NoMocks(x: Double, expectedResult: Double) {
        mockkStatic("expression.NegativeModulesKt")
        every { negativeExpression(any(), any()) } returns 5.0

        val result = computeExpression(x, epsilon)

        assertEquals(expectedResult, result, 1e-3)
    }

    // ========================================================================
    // УРОВЕНЬ 4: Мокаем тригонометрию - все функции возвращают 1
    // negativeExpression при csc=sec=sin=cos=cot=1 должен вернуть 36
    // ========================================================================

    @ParameterizedTest
    @CsvSource(
        "-1.0, 36.0",
        "0.0, 36.0"
    )
    fun testComputeExpression_NegativeBranch_MockTrig(x: Double, expectedResult: Double) {
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { csc(any(), any()) } returns 1.0
        every { sec(any(), any()) } returns 1.0
        every { sin(any(), any()) } returns 1.0
        every { cos(any(), any()) } returns 1.0
        every { cot(any(), any()) } returns 1.0
        mockkStatic("tpo.maxim.expression.PositiveModulesKt")
        every { positiveExpression(any(), any()) } returns 10.0

        val result = computeExpression(x, epsilon)

        assertEquals(expectedResult, result, 1e-9)
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
    fun testComputeExpression_PositiveBranch_MockLog(x: Double, expectedResult: Double) {
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(any(), any(), any()) } returns 1.0
        every { ln(any(), any()) } returns 1.0

        val result = computeExpression(x, epsilon)

        assertEquals(expectedResult, result, 1e-9)
    }

    // ========================================================================
    // УРОВЕНЬ 6: Интеграционный тест без заглушек - реальные вычисления
    // ========================================================================

    @ParameterizedTest
    @CsvSource(
        "1.0, 0.0"
    )
    fun testComputeExpression_NoStubs_Positive(x: Double, expected: Double) {
        val result = computeExpression(x, epsilon)
        assertEquals(expected, result, 1e-6)
    }

    @ParameterizedTest
    @CsvSource(
        "-3.141592653589793, 0.0"
    )
    fun testComputeExpression_NoStubs_Negative(x: Double, expected: Double) {
        val result = computeExpression(x, epsilon)
        assertEquals(expected, result, 1e-3)
    }
}
