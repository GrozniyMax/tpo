package tpo.maxim

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class BasicTrigonometryTest {

    private val epsilon = 1e-9

    @ParameterizedTest
    @CsvSource(
        "0.0, 1.0",
        "1.5707963267948966, 0.0",
        "3.141592653589793, -1.0",
        "4.71238898038469, 0.0",
        "6.283185307179586, 1.0",
        "0.5, 0.8775825618903728",
        "1.0, 0.5403023058681398",
        "-1.5707963267948966, 0.0",
        "-3.141592653589793, -1.0"
    )
    fun `проверка значений cos`(x: Double, expected: Double) {
        val result = cos(x, 1e-10)
        assertEquals(expected, result, epsilon)
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 0.0",
        "1.5707963267948966, 1.0",
        "3.141592653589793, 0.0",
        "4.71238898038469, -1.0",
        "6.283185307179586, 0.0",
        "0.5, 0.479425538604203",
        "1.0, 0.8414709848078965",
        "-1.5707963267948966, -1.0",
        "-3.141592653589793, 0.0"
    )
    fun `проверка значений sin`(x: Double, expected: Double) {
        val result = sin(x, 1e-10)
        assertEquals(expected, result, epsilon)
    }

    @ParameterizedTest
    @CsvSource(
        "0.0, 1.0",
        "1.0, 1.8508157176809255",
        "2.0, -2.402997961722381",
        "3.0, -1.0101086659079939",
        "4.0, -1.5298856564663975",
        "5.0, 3.525320085102471",
        "6.0, 1.0064211722860962"
    )
    fun `проверка значений sec`(x: Double, expected: Double) {
        val result = sec(x, 1e-10)
        assertEquals(expected, result, epsilon)
    }

    @ParameterizedTest
    @CsvSource(
        "0.5, 2.085829642933488",
        "1.0, 1.1883951057781212",
        "1.5, 1.0025113042467406",
        "2.0, 1.0997501702946164",
        "2.5, 1.6709215455586793",
        "3.0, 7.086167396459651"
    )
    fun `проверка значений csc`(x: Double, expected: Double) {
        val result = csc(x, 1e-10)
        assertEquals(expected, result, epsilon)
    }

    @ParameterizedTest
    @CsvSource(
        "0.5, 1.830487721712452",
        "1.0, 0.6420926159343308",
        "1.5, 0.07091484430265245",
        "2.0, -0.45765755436028577",
        "2.5, -1.3386481283041514",
        "3.0, -7.015252551434534"
    )
    fun `проверка значений cot`(x: Double, expected: Double) {
        val result = cot(x, 1e-10)
        assertEquals(expected, result, epsilon)
    }
}
