package expression.notMocked

import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import tpo.maxim.expression.*
import java.math.BigDecimal
import java.math.MathContext

@Disabled("Отладочные тесты")
class NegativeModulesTest {

    private val mc = MathContext.DECIMAL128
    private val epsilon = BigDecimal("1E-20", mc)
    private val testEpsilon = BigDecimal("1E-9", mc)

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @ParameterizedTest
    @CsvSource(
        "0.1, 1281.4664052922287",
        "0.5, 11.069360642352557",
        "1.1, 0.000363335065442135",
        "2.1, -7.448043474703522",
        "3.1, -15755.783309384391"
    )
    fun testModule1(xStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)

        val result = module1(x, epsilon, mc)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "0.1, 11.011690296912802",
        "0.5, 2.963412204823861",
        "1.1, 1.5756694399527773",
        "2.1, 0.6536213989238482",
        "3.1, 23.050505752017294",
        "-3.1, -25.048776052563852",
        "-2.1, -1.6633136081235633",
        "-1.1, -0.6684771971016228",
        "-0.5, -1.2082470810431154",
        "-0.1, -9.02168196635675"
    )
    fun testModule2(xStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)

        val result = module2(x, epsilon, mc)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "0.1, 0.905187501753627",
        "0.5, 0.660068388720346",
        "1.1, 1.313397028655924",
        "2.1, -2.844011022616097",
        "3.1, -1.042446260772498",
        "4.1, -0.9213859736608236"
    )
    fun testModule3(xStr: String, expectedStr: String) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)

        val result = module3(x, epsilon, mc)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "0.1, 1291.572908087388",
        "0.5, 13.372704458456072",
        "1.1, 0.2626357463622955",
        "2.1, -3.950411053163576",
        "3.1, -15731.690357371603",
        "4.1, -0.029104643666557628",
        "5.1, -3.555439601865298",
        "6.1, -81.51726390565082",
    )
    fun testModule4Numerator(
        xStr: String,
        expectedStr: String
    ) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)

        val result = module4Numerator(x, epsilon, mc)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "0.1, 9.0125785968306",
        "0.5, 1.038867954284654",
        "1.1, 0.07787004180973381",
        "2.1, -0.4159464321213888",
        "3.1, 25.050581643274707",
        "4.1, -0.16661785189445344",
        "5.1, 0.1064690318122283",
        "6.1, -6.514294082354029"
    )
    fun testModule4Denominator(
        xStr: String,
        expectedStr: String
    ) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)

        val result = module4Denominator(x, epsilon, mc)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "0.1, 2.0400624440871025",
        "0.5, 8.753917569527017",
        "1.1, 7.175991474755993",
        "1.3, 7878.970331304815",
    )
    fun testModule6Numerator(
        xStr: String,
        expectedStr: String
    ) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)

        val result = module6Numerator(x, epsilon, mc)

        assertEquals(expected, result)
    }

    @ParameterizedTest
    @CsvSource(
        "-1.3, -80860.44755328876",
        "-1.2, -3237.420375950311",
        "-0.7, -0.03257213586684889",
        "-0.2, -0.02908162888594679",
    )
    fun testNegativeExpression(
        xStr: String,
        expectedStr: String
    ) {
        val x = BigDecimal(xStr, mc)
        val expected = BigDecimal(expectedStr, mc)

        val result = negativeExpression(x, epsilon, mc)

        assertEquals(expected, result)
    }


    private fun assertEquals(expected: BigDecimal, result: BigDecimal) {
        assertTrue(
            expected.subtract(result, mc).abs() <= testEpsilon,
            "Expected $expected, got $result difference: ${expected.subtract(result, mc).abs()}"
        )
    }

}
