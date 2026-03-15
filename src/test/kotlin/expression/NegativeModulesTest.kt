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
import tpo.maxim.expression.*
import java.math.BigDecimal
import java.math.MathContext

class NegativeModulesTest {

    private val mc = MathContext.DECIMAL128
    private val epsilon = BigDecimal("1E-10", mc)
    private val testEpsilon = BigDecimal("1E-9", mc)

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 1.0, 1.0, 0.0, 1.0, 8.0",
        "-1.0, 2.0, 2.0, 1.0, 1.0, 0.125"
    )
    fun testModule1(xStr: String, cscResultStr: String, secResultStr: String, sinResultStr: String, cotResultStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val cscResult = BigDecimal(cscResultStr, mc)
        val secResult = BigDecimal(secResultStr, mc)
        val sinResult = BigDecimal(sinResultStr, mc)
        val cotResult = BigDecimal(cotResultStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { csc(x, epsilon, mc) } returns cscResult
        every { sec(x, epsilon, mc) } returns secResult
        every { sin(x, epsilon, mc) } returns sinResult
        every { cot(x, epsilon, mc) } returns cotResult

        val result = module1(x, epsilon, mc)

        assertTrue(expected.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "Expected $expected, got $result")
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 1.0, 1.0, 2.0",
        "-1.0, 0.5, 2.0, 2.5"
    )
    fun testModule2(xStr: String, cosResultStr: String, cscResultStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val cosResult = BigDecimal(cosResultStr, mc)
        val cscResult = BigDecimal(cscResultStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { cos(x, epsilon, mc) } returns cosResult
        every { csc(x, epsilon, mc) } returns cscResult

        val result = module2(x, epsilon, mc)

        assertTrue(expected.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "Expected $expected, got $result")
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 1.0, 0.0, 1.0",
        "-1.0, 2.0, 0.5, 1.5"
    )
    fun testModule3(xStr: String, secResultStr: String, sinResultStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val secResult = BigDecimal(secResultStr, mc)
        val sinResult = BigDecimal(sinResultStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { sec(x, epsilon, mc) } returns secResult
        every { sin(x, epsilon, mc) } returns sinResult

        val result = module3(x, epsilon, mc)

        assertTrue(expected.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "Expected $expected, got $result")
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 8.0, 2.0, 1.0, 9.0",
        "-1.0, 0.125, 2.5, 1.5, 1.125"
    )
    fun testModule4Numerator(xStr: String, module1ResultStr: String, module2ResultStr: String, module3ResultStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val module1Result = BigDecimal(module1ResultStr, mc)
        val module2Result = BigDecimal(module2ResultStr, mc)
        val module3Result = BigDecimal(module3ResultStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        
        mockkStatic("tpo.maxim.expression.NegativeModulesKt")
        every { module1(x, epsilon, mc) } returns module1Result
        every { module2(x, epsilon, mc) } returns module2Result
        every { module3(x, epsilon, mc) } returns module3Result

        val result = module4Numerator(x, epsilon, mc)

        assertTrue(expected.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "Expected $expected, got $result")
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 1.0, 2.0, 2.0, 0.25",
        "-1.0, 2.0, 3.0, 3.0, 0.6666666666666666"
    )
    fun testModule4Denominator(xStr: String, cotResultStr: String, cscResultStr: String, secResultStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val cotResult = BigDecimal(cotResultStr, mc)
        val cscResult = BigDecimal(cscResultStr, mc)
        val secResult = BigDecimal(secResultStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        
        mockkStatic("tpo.maxim.BasicTrigonometryKt")
        every { cot(x, epsilon, mc) } returns cotResult
        every { csc(x, epsilon, mc) } returns cscResult
        every { sec(x, epsilon, mc) } returns secResult

        val result = module4Denominator(x, epsilon, mc)

        assertTrue(expected.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "Expected $expected, got $result")
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 9.0, 0.25, 36.0",
        "-1.0, 1.125, 0.6666666666666666, 1.6875"
    )
    fun testModule5(xStr: String, numeratorResultStr: String, denominatorResultStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val numeratorResult = BigDecimal(numeratorResultStr, mc)
        val denominatorResult = BigDecimal(denominatorResultStr, mc)
        val expected = BigDecimal(expectedStr, mc)
        
        mockkStatic("tpo.maxim.expression.NegativeModulesKt")
        every { module4Numerator(x, epsilon, mc) } returns numeratorResult
        every { module4Denominator(x, epsilon, mc) } returns denominatorResult

        val result = module5(x, epsilon, mc)

        assertTrue(expected.subtract(result, mc).abs().compareTo(testEpsilon) <= 0, "Expected $expected, got $result")
    }

}
