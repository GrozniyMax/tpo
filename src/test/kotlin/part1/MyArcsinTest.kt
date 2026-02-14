package part1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import org.junit.jupiter.params.provider.CsvSource
import tpo.maxim.part1.myArcsin

const val DELTA = 1e-6

class MyArcsinTest {

    @ParameterizedTest
    @DisplayName("Проверка функции в рамках ОДЗ")
    @CsvFileSource(resources = ["/part1/odz.csv"], numLinesToSkip = 1)
    fun testFunctionInOdz(x: Double, expected: Double) {
        assertEquals(expected, myArcsin(x), DELTA)
    }

    @ParameterizedTest
    @DisplayName("Проверка функции вне ОДЗ")
    @CsvFileSource(resources = ["/part1/negativeOut.csv", "/part1/positiveOut.csv"], numLinesToSkip = 1)
    fun testFunctionOutOfOdz(x: Double, expected: Double) {
        assertEquals(expected, myArcsin(x), DELTA)
    }

    @ParameterizedTest
    @DisplayName("Проверка функции в интересных точках")
    @CsvSource(value = [
        "0.0,0.0",
        "0.5,0.5235988", // PI/6
        "0.707106781,0.7853982", // PI/4
        "0.866025404,1.0471976", // PI/3
        "1.0, 1.5707963", // PI/2
        "-0.5, -0.5235988", // -PI/6
        "-0.707106781, -0.7853982", // -PI/4
        "-0.866025404, -1.0471976", // -PI/3
        "-1.0, -1.5707963" // -PI/2
    ])
    fun testInterestingDots(x: Double, expected: Double) {
        assertEquals(expected, myArcsin(x), DELTA)
    }
}