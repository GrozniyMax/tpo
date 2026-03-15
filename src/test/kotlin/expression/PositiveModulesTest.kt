package tpo.maxim.expression

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import tpo.maxim.ln
import tpo.maxim.log

class PositiveModulesTest {

    private val epsilon = 1e-10

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
    fun testLog10Squared(x: Double, base: Double, logResult: Double, expected: Double) {
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(x, 10.0, epsilon) } returns logResult

        val result = log10Squared(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0, 0.0, 2.0, 0.0, 0.0",
        "10.0, 10.0, 1.0, 2.0, 1.0, 1.0",
        "100.0, 10.0, 2.0, 2.0, 2.0, 8.0",
        "1000.0, 10.0, 3.0, 2.0, 3.0, 27.0",
        "2.0, 2.0, 1.0, 1.0, 1.0, 1.0",
        "4.0, 2.0, 2.0, 1.0, 2.0, 8.0",
        "8.0, 2.0, 3.0, 1.0, 3.0, 27.0"
    )
    fun testMultiplyByLog2(x: Double, base10: Double, log10Result: Double, base2: Double, log2Result: Double, expected: Double) {
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(x, 10.0, epsilon) } returns log10Result
        every { log(x, 2.0, epsilon) } returns log2Result

        val result = multiplyByLog2(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0, 0.0, 2.0, 0.0, 3.0, 0.0, 0.0",
        "10.0, 10.0, 1.0, 2.0, 1.0, 3.0, 1.0, 2.0",
        "100.0, 10.0, 2.0, 2.0, 2.0, 3.0, 2.0, 10.0",
        "1000.0, 10.0, 3.0, 2.0, 3.0, 3.0, 3.0, 30.0"
    )
    fun testAddLog3(x: Double, base10: Double, log10Result: Double, base2: Double, log2Result: Double, base3: Double, log3Result: Double, expected: Double) {
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(x, 10.0, epsilon) } returns log10Result
        every { log(x, 2.0, epsilon) } returns log2Result
        every { log(x, 3.0, epsilon) } returns log3Result

        val result = addLog3(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0, 0.0, 2.0, 0.0, 3.0, 0.0, 5.0, 0.0, 0.0",
        "10.0, 10.0, 1.0, 2.0, 1.0, 3.0, 1.0, 5.0, 1.0, 3.0",
        "100.0, 10.0, 2.0, 2.0, 2.0, 3.0, 2.0, 5.0, 2.0, 12.0"
    )
    fun testAddLog5(x: Double, base10: Double, log10Result: Double, base2: Double, log2Result: Double, base3: Double, log3Result: Double, base5: Double, log5Result: Double, expected: Double) {
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(x, 10.0, epsilon) } returns log10Result
        every { log(x, 2.0, epsilon) } returns log2Result
        every { log(x, 3.0, epsilon) } returns log3Result
        every { log(x, 5.0, epsilon) } returns log5Result

        val result = addLog5(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 0.0, 0.0",
        "2.718281828459045, 1.0, 1.0",
        "7.38905609893065, 2.0, 8.0",
        "20.085536923187668, 3.0, 27.0"
    )
    fun testLnCubed(x: Double, lnResult: Double, expected: Double) {
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { ln(x, epsilon) } returns lnResult

        val result = lnCubed(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }

    @ParameterizedTest
    @CsvSource(
        "1.0, 10.0, 0.0, 2.0, 0.0, 3.0, 0.0, 5.0, 0.0, 0.0, 0.0, 0.0",
        "10.0, 10.0, 1.0, 2.0, 1.0, 3.0, 1.0, 5.0, 1.0, 1.0, 1.0, 4.0"
    )
    fun testPositiveExpression(
        x: Double,
        base10: Double, log10Result: Double,
        base2: Double, log2Result: Double,
        base3: Double, log3Result: Double,
        base5: Double, log5Result: Double,
        lnBase: Double, lnResult: Double,
        expected: Double
    ) {
        mockkStatic("tpo.maxim.BasicLogarithmsKt")
        every { log(x, 10.0, epsilon) } returns log10Result
        every { log(x, 2.0, epsilon) } returns log2Result
        every { log(x, 3.0, epsilon) } returns log3Result
        every { log(x, 5.0, epsilon) } returns log5Result
        every { ln(x, epsilon) } returns lnResult

        val result = positiveExpression(x, epsilon)

        assertEquals(expected, result, 1e-9)
    }
}
