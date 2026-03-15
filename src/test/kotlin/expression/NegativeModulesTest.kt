package expression

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import tpo.maxim.*

class NegativeModulesTest {

    private val epsilon = 1e-10

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 1.0, 1.0, 0.0, 1.0, 8.0",
        "-1.0, 2.0, 2.0, 1.0, 1.0, 0.125"
    )
    fun testModule1(x: Double, cscResult: Double, secResult: Double, sinResult: Double, cotResult: Double, expected: Double) {
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { csc(x, epsilon) } returns cscResult
        every { sec(x, epsilon) } returns secResult
        every { sin(x, epsilon) } returns sinResult
        every { cot(x, epsilon) } returns cotResult

        val result = module1(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 1.0, 1.0, 2.0",
        "-1.0, 0.5, 2.0, 2.5"
    )
    fun testModule2(x: Double, cosResult: Double, cscResult: Double, expected: Double) {
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { cos(x, epsilon) } returns cosResult
        every { csc(x, epsilon) } returns cscResult

        val result = module2(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 1.0, 0.0, 1.0",
        "-1.0, 2.0, 0.5, 1.5"
    )
    fun testModule3(x: Double, secResult: Double, sinResult: Double, expected: Double) {
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { sec(x, epsilon) } returns secResult
        every { sin(x, epsilon) } returns sinResult

        val result = module3(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 8.0, 2.0, 1.0, 9.0",
        "-1.0, 0.125, 2.5, 1.5, 1.125"
    )
    fun testModule4Numerator(x: Double, module1Result: Double, module2Result: Double, module3Result: Double, expected: Double) {
        mockkStatic("expression.NegativeModulesKt")
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { module1(x, epsilon) } returns module1Result
        every { module2(x, epsilon) } returns module2Result
        every { module3(x, epsilon) } returns module3Result

        val result = module4Numerator(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 1.0, 2.0, 2.0, 0.25",
        "-1.0, 2.0, 3.0, 3.0, 0.6666666666666666"
    )
    fun testModule4Denominator(x: Double, cotResult: Double, cscResult: Double, secResult: Double, expected: Double) {
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { cot(x, epsilon) } returns cotResult
        every { csc(x, epsilon) } returns cscResult
        every { sec(x, epsilon) } returns secResult

        val result = module4Denominator(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 9.0, 0.25, 36.0",
        "-1.0, 1.125, 0.6666666666666666, 1.6875"
    )
    fun testModule5(x: Double, numeratorResult: Double, denominatorResult: Double, expected: Double) {
        mockkStatic("expression.NegativeModulesKt")
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { module4Numerator(x, epsilon) } returns numeratorResult
        every { module4Denominator(x, epsilon) } returns denominatorResult

        val result = module5(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 36.0, 1.0, 1296.0",
        "-1.0, 1.6875, 0.5, 0.177978515625"
    )
    fun testModule6Numerator(x: Double, module5Result: Double, sinResult: Double, expected: Double) {
        mockkStatic("expression.NegativeModulesKt")
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { sin(x, epsilon) } returns sinResult
        every { module5(x, epsilon) } returns module5Result

        val result = module6Numerator(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 1296.0, 1.0, 1296.0",
        "-1.0, 0.177978515625, 1.0, 0.177978515625"
    )
    fun testNegativeExpression(x: Double, module6NumeratorResult: Double, cotResult: Double, expected: Double) {
        mockkStatic("expression.NegativeModulesKt")
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { csc(x, epsilon) } returns 1.0
        every { sec(x, epsilon) } returns 1.0
        every { sin(x, epsilon) } returns 1.0
        every { cot(x, epsilon) } returns cotResult
        every { cos(x, epsilon) } returns 1.0
        every { module6Numerator(x, epsilon) } returns module6NumeratorResult

        val result = negativeExpression(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }
}
