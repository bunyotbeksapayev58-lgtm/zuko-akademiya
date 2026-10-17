package uz.ziyoakademiya.app.core.domain.engine

import kotlin.math.*

object MathEngine {

    /**
     * Normalizes a text answer by removing unnecessary whitespace,
     * converting unicode dashes/minuses, and lowercasing.
     */
    fun normalizeText(input: String): String {
        return input.trim()
            .replace("−", "-")
            .replace("—", "-")
            .replace("–", "-")
            .replace("·", "*")
            .replace("×", "*")
            .replace(":", "/")
            .replace("\\s+".toRegex(), " ")
    }

    /**
     * Parses numeric values including integers, decimals (both . and , separators),
     * and simple fractions like "3/4" or "-5/2".
     */
    fun parseNumber(input: String): Double? {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return null

        // If it contains semicolon or space-separated items, it's not a single number
        if (trimmed.contains(";") || trimmed.contains(" ")) return null

        // Try direct double parse with dot or comma
        val standardDecimal = trimmed.replace(",", ".")
        standardDecimal.toDoubleOrNull()?.let { return it }

        // Try fraction parse "a/b"
        if (standardDecimal.contains("/")) {
            val parts = standardDecimal.split("/")
            if (parts.size == 2) {
                val num = parts[0].toDoubleOrNull()
                val den = parts[1].toDoubleOrNull()
                if (num != null && den != null && den != 0.0) {
                    return num / den
                }
            }
        }
        return null
    }

    /**
     * Compares two numbers with a floating-point tolerance.
     */
    fun isNumericEqual(expected: Double, actual: Double, tolerance: Double = 1e-5): Boolean {
        return abs(expected - actual) <= tolerance
    }

    /**
     * Compares student answer with expected answer, handling:
     * - Option letters ("A", "B", "C", "D")
     * - Numeric values and fractions
     * - Sets of solutions (e.g., "30, 150" vs "150, 30" or "30°, 150°")
     * - Normalized string equality
     */
    fun checkAnswer(expected: String, studentAnswer: String): Boolean {
        val normExp = normalizeText(expected)
        val normAct = normalizeText(studentAnswer)

        if (normExp.equals(normAct, ignoreCase = true)) {
            return true
        }

        // Check set of values first (e.g. angle sets "30, 150", "30 va 150", "x = 10")
        val expCleaned = normExp.replace("°", "").replace("x=", "").replace("x =", "").replace(" va ", ", ")
        val actCleaned = normAct.replace("°", "").replace("x=", "").replace("x =", "").replace(" va ", ", ")

        if (expCleaned.contains(",") || expCleaned.contains(";") || actCleaned.contains(",") || actCleaned.contains(";")) {
            val expSet = expCleaned.split(",", ";").map { it.trim() }.mapNotNull { parseNumber(it) }
            val actSet = actCleaned.split(",", ";").map { it.trim() }.mapNotNull { parseNumber(it) }

            if (expSet.isNotEmpty() && expSet.size == actSet.size) {
                val matched = expSet.all { expVal ->
                    actSet.any { actVal -> isNumericEqual(expVal, actVal) }
                }
                if (matched) return true
            }
        }

        // Check if both are numeric (single number or fraction)
        val expNum = parseNumber(expCleaned)
        val actNum = parseNumber(actCleaned)
        if (expNum != null && actNum != null) {
            return isNumericEqual(expNum, actNum)
        }

        return false
    }

    // --- Core Curriculum Verifications Required by Specification ---

    /**
     * 1. Logarithm evaluation: log_base(value)
     * e.g., log2(32) = 5
     */
    fun evaluateLog(base: Double, value: Double): Double {
        require(base > 0.0 && base != 1.0) { "Logarifm asosi 0 dan katta va 1 ga teng bo'lmasligi kerak: $base" }
        require(value > 0.0) { "Logarifm ostidagi ifoda musbat bo'lishi kerak: $value" }
        return ln(value) / ln(base)
    }

    /**
     * 2. Logarithm equation solver check: log_3(x - 1) = 2
     * Domain: x - 1 > 0 => x > 1
     * Solution: x - 1 = 3^2 = 9 => x = 10
     */
    fun solveLogBase3OfXMinus1Equals2(): Pair<Double, Boolean> {
        val x = 10.0
        val isDomainValid = (x - 1.0) > 0.0
        val satisfiesEquation = isNumericEqual(evaluateLog(3.0, x - 1.0), 2.0)
        return Pair(x, isDomainValid && satisfiesEquation)
    }

    /**
     * 3. Trigonometric sum: sin(30°) + cos(60°) = 0.5 + 0.5 = 1.0
     */
    fun evaluateSin30PlusCos60(): Double {
        val rad30 = Math.toRadians(30.0)
        val rad60 = Math.toRadians(60.0)
        return sin(rad30) + cos(rad60)
    }

    /**
     * 4. Solve 2 sin(x) = 1 on [0°, 360°]
     * sin(x) = 1/2 => x = 30° va x = 150°
     */
    fun solve2SinXEquals1(): List<Double> {
        val solutions = mutableListOf<Double>()
        for (deg in 0..360) {
            val rad = Math.toRadians(deg.toDouble())
            if (isNumericEqual(2 * sin(rad), 1.0, tolerance = 1e-4)) {
                solutions.add(deg.toDouble())
            }
        }
        return solutions
    }

    /**
     * 5. Function domain for f(x) = sqrt(2x - 6):
     * 2x - 6 >= 0 => 2x >= 6 => x >= 3
     */
    fun isDomainValidForSqrt2xMinus6(x: Double): Boolean {
        return (2 * x - 6) >= 0.0
    }

    /**
     * 6. Function evaluation: f(x) = x^2 - 3x + 2, calculate f(2)
     * f(2) = 2^2 - 3*2 + 2 = 4 - 6 + 2 = 0
     */
    fun evaluateQuadraticF2(x: Double = 2.0): Double {
        return x * x - 3 * x + 2
    }

    /**
     * 7. Derivative of x^3 - 4x at x:
     * d/dx(x^3 - 4x) = 3x^2 - 4
     */
    fun derivativeOfXCubeMinus4X(x: Double): Double {
        return 3 * x * x - 4
    }

    /**
     * 8. Derivative of y = 3x^2 - 6x + 1:
     * y' = 6x - 6. At x = 2: y'(2) = 6(2) - 6 = 12 - 6 = 6
     */
    fun derivativeOf3XSquaredMinus6XPlus1At(x: Double): Double {
        return 6 * x - 6
    }

    /**
     * 9. Indefinite integral of 2x + 3:
     * \int (2x + 3) dx = x^2 + 3x + C
     */
    fun integralOf2XPlus3(x: Double, c: Double = 0.0): Double {
        return x * x + 3 * x + c
    }

    /**
     * 10. Definite integral of x from 0 to 2:
     * \int_0^2 x dx = [x^2 / 2]_0^2 = 4/2 - 0 = 2.0
     */
    fun definiteIntegralOfX(a: Double = 0.0, b: Double = 2.0): Double {
        val fUpper = (b * b) / 2.0
        val fLower = (a * a) / 2.0
        return fUpper - fLower
    }

    /**
     * 11. Prism Volume:
     * V = S_asos * h = 18 cm^2 * 7 cm = 126 cm^3
     */
    fun calculatePrismVolume(baseArea: Double, height: Double): Double {
        require(baseArea > 0 && height > 0) { "Asos yuzi va balandlik musbat bo'lishi kerak" }
        return baseArea * height
    }

    /**
     * 12. Rectangular Prism Total Surface Area:
     * S_to'la = 2*(ab + bc + ac) = 2*(3*4 + 4*5 + 3*5) = 2*(12 + 20 + 15) = 2*47 = 94 cm^2
     */
    fun calculateRectangularPrismSurfaceArea(a: Double, b: Double, c: Double): Double {
        require(a > 0 && b > 0 && c > 0) { "O'lchamlar musbat bo'lishi kerak" }
        return 2 * (a * b + b * c + a * c)
    }

    /**
     * 13. Heron's Formula for Triangle Area:
     * S = sqrt(p * (p - a) * (p - b) * (p - c)), where p = (a + b + c) / 2
     * e.g., a=5, b=6, c=7 => p=9, S = sqrt(9*4*3*2) = sqrt(216) ≈ 14.6969
     * e.g., a=3, b=4, c=5 => p=6, S = sqrt(6*3*2*1) = 6.0
     */
    fun calculateTriangleHeronArea(a: Double, b: Double, c: Double): Double {
        require(a > 0 && b > 0 && c > 0) { "Tomonlar musbat bo'lishi kerak" }
        require(a + b > c && a + c > b && b + c > a) { "Uchburchak tengsizligi bajarilishi kerak" }
        val p = (a + b + c) / 2.0
        return sqrt(p * (p - a) * (p - b) * (p - c))
    }

    /**
     * 14. Trapezoid Area: S = (a + b) / 2 * h
     */
    fun calculateTrapezoidArea(a: Double, b: Double, h: Double): Double {
        require(a > 0 && b > 0 && h > 0) { "Asoslar va balandlik musbat bo'lishi kerak" }
        return (a + b) / 2.0 * h
    }

    /**
     * 15. Circle Area and Circumference
     */
    fun calculateCircleArea(r: Double): Double {
        require(r > 0) { "Radius musbat bo'lishi kerak" }
        return Math.PI * r * r
    }

    fun calculateCircleCircumference(r: Double): Double {
        require(r > 0) { "Radius musbat bo'lishi kerak" }
        return 2 * Math.PI * r
    }

    fun calculateCircleSectorArea(r: Double, angleDeg: Double): Double {
        require(r > 0 && angleDeg > 0) { "Radius va burchak musbat bo'lishi kerak" }
        return (Math.PI * r * r * angleDeg) / 360.0
    }

    /**
     * 16. Regular Pyramid Volume: V = (1/3) * S_asos * h
     */
    fun calculatePyramidVolume(baseArea: Double, height: Double): Double {
        require(baseArea > 0 && height > 0) { "Asos yuzi va balandlik musbat bo'lishi kerak" }
        return (1.0 / 3.0) * baseArea * height
    }

    /**
     * 17. Cylinder Volume & Total Surface Area:
     * V = π * r^2 * h
     * S_to'la = 2πr(r + h)
     */
    fun calculateCylinderVolume(r: Double, h: Double): Double {
        require(r > 0 && h > 0) { "Radius va balandlik musbat bo'lishi kerak" }
        return Math.PI * r * r * h
    }

    fun calculateCylinderTotalArea(r: Double, h: Double): Double {
        require(r > 0 && h > 0) { "Radius va balandlik musbat bo'lishi kerak" }
        return 2 * Math.PI * r * (r + h)
    }

    /**
     * 18. Cone Volume & Lateral Surface Area:
     * V = (1/3) * π * r^2 * h
     * S_yon = π * r * l, where l = sqrt(r^2 + h^2)
     */
    fun calculateConeVolume(r: Double, h: Double): Double {
        require(r > 0 && h > 0) { "Radius va balandlik musbat bo'lishi kerak" }
        return (1.0 / 3.0) * Math.PI * r * r * h
    }

    fun calculateConeSlantHeight(r: Double, h: Double): Double {
        require(r > 0 && h > 0) { "Radius va balandlik musbat bo'lishi kerak" }
        return sqrt(r * r + h * h)
    }

    /**
     * 19. Sphere Volume & Surface Area:
     * V = (4/3) * π * r^3
     * S = 4 * π * r^2
     */
    fun calculateSphereVolume(r: Double): Double {
        require(r > 0) { "Radius musbat bo'lishi kerak" }
        return (4.0 / 3.0) * Math.PI * r * r * r
    }

    fun calculateSphereSurfaceArea(r: Double): Double {
        require(r > 0) { "Radius musbat bo'lishi kerak" }
        return 4.0 * Math.PI * r * r
    }

    /**
     * 20. Combinatorics:
     * Permutations: P(n) = n!
     * Combinations: C(n, k) = n! / (k! * (n - k)!)
     */
    fun factorial(n: Int): Long {
        require(n >= 0) { "n manfiy bo'lmasligi kerak: $n" }
        var res = 1L
        for (i in 2..n) res *= i
        return res
    }

    fun combinations(n: Int, k: Int): Long {
        require(n >= 0 && k in 0..n) { "k 0 va n oralig'ida bo'lishi kerak" }
        if (k == 0 || k == n) return 1L
        val minK = minOf(k, n - k)
        var num = 1L
        var den = 1L
        for (i in 1..minK) {
            num *= (n - i + 1)
            den *= i
        }
        return num / den
    }

    /**
     * 21. Arithmetic & Geometric Progression terms:
     * a_n = a_1 + (n - 1) * d
     * S_n = (a_1 + a_n) / 2 * n
     */
    fun arithmeticNthTerm(a1: Double, d: Double, n: Int): Double {
        require(n >= 1) { "n >= 1 bo'lishi kerak" }
        return a1 + (n - 1) * d
    }

    fun arithmeticSum(a1: Double, d: Double, n: Int): Double {
        require(n >= 1) { "n >= 1 bo'lishi kerak" }
        val an = arithmeticNthTerm(a1, d, n)
        return ((a1 + an) / 2.0) * n
    }
}
