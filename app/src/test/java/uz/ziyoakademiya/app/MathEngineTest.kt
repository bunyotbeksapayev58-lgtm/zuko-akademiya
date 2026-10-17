package uz.ziyoakademiya.app

import org.junit.Assert.*
import org.junit.Test
import uz.ziyoakademiya.app.core.domain.engine.MathEngine
import kotlin.math.abs

class MathEngineTest {

    @Test
    fun testLogarithmEvaluation_log2Of32Equals5() {
        // 1. log base 2 of 32 equals 5
        val result = MathEngine.evaluateLog(2.0, 32.0)
        assertEquals(5.0, result, 1e-6)
    }

    @Test
    fun testLogarithmEquation_log3OfXMinus1Equals2() {
        // 2. Solve log base 3 of (x - 1) = 2 => x = 10, domain x > 1
        val (x, isValid) = MathEngine.solveLogBase3OfXMinus1Equals2()
        assertEquals(10.0, x, 1e-6)
        assertTrue("Domain restriction x > 1 and equation satisfaction must be true", isValid)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testLogarithmDomain_negativeArgumentThrows() {
        MathEngine.evaluateLog(2.0, -4.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testLogarithmDomain_invalidBaseThrows() {
        MathEngine.evaluateLog(1.0, 10.0)
    }

    @Test
    fun testTrigonometricSum_sin30PlusCos60Equals1() {
        // 3. sin(30 degrees) + cos(60 degrees) = 1
        val sum = MathEngine.evaluateSin30PlusCos60()
        assertEquals(1.0, sum, 1e-6)
    }

    @Test
    fun testTrigonometricEquation_2SinXEquals1() {
        // 4. Solve 2 sin(x) = 1 on [0, 360] degrees => x = 30 and x = 150
        val roots = MathEngine.solve2SinXEquals1()
        assertEquals(2, roots.size)
        assertTrue(roots.contains(30.0))
        assertTrue(roots.contains(150.0))
    }

    @Test
    fun testFunctionDomain_sqrt2XMinus6() {
        // 5. The domain of sqrt(2x - 6) is x >= 3
        assertTrue(MathEngine.isDomainValidForSqrt2xMinus6(3.0))
        assertTrue(MathEngine.isDomainValidForSqrt2xMinus6(5.0))
        assertFalse(MathEngine.isDomainValidForSqrt2xMinus6(2.99))
        assertFalse(MathEngine.isDomainValidForSqrt2xMinus6(0.0))
    }

    @Test
    fun testFunctionEvaluation_quadraticF2Equals0() {
        // 6. For f(x) = x^2 - 3x + 2, f(2) = 0
        val f2 = MathEngine.evaluateQuadraticF2(2.0)
        assertEquals(0.0, f2, 1e-6)
    }

    @Test
    fun testDerivative_xCubeMinus4X() {
        // 7. Derivative of x^3 - 4x is 3x^2 - 4
        // At x = 2: 3(4) - 4 = 8
        val dAt2 = MathEngine.derivativeOfXCubeMinus4X(2.0)
        assertEquals(8.0, dAt2, 1e-6)
        // At x = 0: 3(0) - 4 = -4
        val dAt0 = MathEngine.derivativeOfXCubeMinus4X(0.0)
        assertEquals(-4.0, dAt0, 1e-6)
    }

    @Test
    fun testDerivativeAtPoint_y3XSquaredMinus6XPlus1() {
        // 8. For y = 3x^2 - 6x + 1, y'(2) = 6
        val yPrime2 = MathEngine.derivativeOf3XSquaredMinus6XPlus1At(2.0)
        assertEquals(6.0, yPrime2, 1e-6)
    }

    @Test
    fun testIndefiniteIntegral_2XPlus3() {
        // 9. The indefinite integral of 2x + 3 is x^2 + 3x + C
        // With C = 5 at x = 3: 3^2 + 3(3) + 5 = 9 + 9 + 5 = 23
        val value = MathEngine.integralOf2XPlus3(3.0, 5.0)
        assertEquals(23.0, value, 1e-6)
    }

    @Test
    fun testDefiniteIntegral_xFrom0To2Equals2() {
        // 10. The definite integral of x from 0 to 2 is 2
        val area = MathEngine.definiteIntegralOfX(0.0, 2.0)
        assertEquals(2.0, area, 1e-6)
    }

    @Test
    fun testPrismVolume_base18Height7Equals126() {
        // 11. A prism with base area 18 cm^2 and height 7 cm has volume 126 cm^3
        val volume = MathEngine.calculatePrismVolume(18.0, 7.0)
        assertEquals(126.0, volume, 1e-6)
    }

    @Test
    fun testRectangularPrismSurfaceArea_3x4x5Equals94() {
        // 12. A rectangular prism measuring 3 cm by 4 cm by 5 cm has total surface area 94 cm^2
        val surfaceArea = MathEngine.calculateRectangularPrismSurfaceArea(3.0, 4.0, 5.0)
        assertEquals(94.0, surfaceArea, 1e-6)
    }

    @Test
    fun testAnswerChecking_normalizationAndTolerances() {
        // Option matching
        assertTrue(MathEngine.checkAnswer("A", "a"))
        assertTrue(MathEngine.checkAnswer("B", " B "))

        // Numeric matching with decimal and comma
        assertTrue(MathEngine.checkAnswer("10", "10.0"))
        assertTrue(MathEngine.checkAnswer("0.5", "0,5"))
        assertTrue(MathEngine.checkAnswer("0.5", "1/2"))

        // Set matching
        assertTrue(MathEngine.checkAnswer("30, 150", "150, 30"))
        assertTrue(MathEngine.checkAnswer("30°, 150°", "150, 30"))
        assertTrue(MathEngine.checkAnswer("x = 10", "10"))
    }

    @Test
    fun testHeronFormula_3_4_5_and_5_6_7() {
        // 3, 4, 5 right triangle: area 6.0
        val s1 = MathEngine.calculateTriangleHeronArea(3.0, 4.0, 5.0)
        assertEquals(6.0, s1, 1e-6)

        // 5, 6, 7 triangle: p = 9, S = sqrt(9 * 4 * 3 * 2) = sqrt(216) = 6*sqrt(6) ≈ 14.696938
        val s2 = MathEngine.calculateTriangleHeronArea(5.0, 6.0, 7.0)
        assertEquals(14.696938, s2, 1e-4)
    }

    @Test
    fun testTrapezoidArea_bases6and14Height8() {
        val s = MathEngine.calculateTrapezoidArea(6.0, 14.0, 8.0)
        assertEquals(80.0, s, 1e-6)
    }

    @Test
    fun testCircleAreaAndCircumference() {
        val r = 7.0
        val area = MathEngine.calculateCircleArea(r)
        val circumference = MathEngine.calculateCircleCircumference(r)
        assertEquals(Math.PI * 49.0, area, 1e-6)
        assertEquals(2.0 * Math.PI * 7.0, circumference, 1e-6)

        // 90 degree sector of radius 6: area should be (36 * pi) / 4 = 9 * pi
        val sectorArea = MathEngine.calculateCircleSectorArea(6.0, 90.0)
        assertEquals(9.0 * Math.PI, sectorArea, 1e-6)
    }

    @Test
    fun testPyramidVolume() {
        // baseArea 36, height 10 => V = 1/3 * 36 * 10 = 120
        val v = MathEngine.calculatePyramidVolume(36.0, 10.0)
        assertEquals(120.0, v, 1e-6)
    }

    @Test
    fun testCylinderVolumeAndArea() {
        // r = 4, h = 6 => V = pi * 16 * 6 = 96 * pi
        val v = MathEngine.calculateCylinderVolume(4.0, 6.0)
        assertEquals(96.0 * Math.PI, v, 1e-6)

        // S_total = 2*pi*r*(r + h) = 2*pi*4*10 = 80*pi
        val s = MathEngine.calculateCylinderTotalArea(4.0, 6.0)
        assertEquals(80.0 * Math.PI, s, 1e-6)
    }

    @Test
    fun testConeVolumeAndSlantHeight() {
        // r = 3, h = 4 => l = 5, V = 1/3 * pi * 9 * 4 = 12 * pi
        val l = MathEngine.calculateConeSlantHeight(3.0, 4.0)
        assertEquals(5.0, l, 1e-6)

        val v = MathEngine.calculateConeVolume(3.0, 4.0)
        assertEquals(12.0 * Math.PI, v, 1e-6)
    }

    @Test
    fun testSphereVolumeAndArea() {
        // r = 3 => V = 4/3 * pi * 27 = 36 * pi, S = 4 * pi * 9 = 36 * pi
        val v = MathEngine.calculateSphereVolume(3.0)
        val s = MathEngine.calculateSphereSurfaceArea(3.0)
        assertEquals(36.0 * Math.PI, v, 1e-6)
        assertEquals(36.0 * Math.PI, s, 1e-6)
    }

    @Test
    fun testCombinatorics_factorialsAndCombinations() {
        assertEquals(1L, MathEngine.factorial(0))
        assertEquals(1L, MathEngine.factorial(1))
        assertEquals(120L, MathEngine.factorial(5))
        assertEquals(720L, MathEngine.factorial(6))

        // C(5, 2) = 10, C(7, 3) = 35
        assertEquals(10L, MathEngine.combinations(5, 2))
        assertEquals(35L, MathEngine.combinations(7, 3))
    }

    @Test
    fun testProgressions_arithmeticTermsAndSums() {
        // a1 = 3, d = 4 => a_10 = 3 + 9*4 = 39
        val a10 = MathEngine.arithmeticNthTerm(3.0, 4.0, 10)
        assertEquals(39.0, a10, 1e-6)

        // a1 = 1, d = 1, n = 50 => S_50 = 51 * 25 = 1275
        val s50 = MathEngine.arithmeticSum(1.0, 1.0, 50)
        assertEquals(1275.0, s50, 1e-6)
    }
}
