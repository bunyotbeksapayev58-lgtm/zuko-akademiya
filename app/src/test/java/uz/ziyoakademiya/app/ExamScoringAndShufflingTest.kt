package uz.ziyoakademiya.app

import org.junit.Assert.*
import org.junit.Test
import uz.ziyoakademiya.app.core.domain.engine.MathEngine
import uz.ziyoakademiya.app.core.domain.model.QuestionOption

class ExamScoringAndShufflingTest {

    @Test
    fun testOptionShuffling_preservesCorrectAnswer() {
        val originalOptions = listOf(
            QuestionOption("A", "4"),
            QuestionOption("B", "5"), // correct
            QuestionOption("C", "6"),
            QuestionOption("D", "16")
        )
        val originalCorrectAnswer = "B"

        // Perform 50 shuffles and check that mapping always points to original answer
        for (seed in 1..50) {
            val shuffled = originalOptions.shuffled()
            val mapping = mutableMapOf<String, String>()
            shuffled.forEachIndexed { idx, opt ->
                val displayId = ('A' + idx).toString()
                mapping[displayId] = opt.id
            }

            // Find which display letter corresponds to "B"
            val displayCorrectLetter = mapping.entries.first { it.value == originalCorrectAnswer }.key

            // When user picks that display letter, mapped answer must be "B"
            val userSelectedDisplay = displayCorrectLetter
            val resolvedOriginalOptionId = mapping[userSelectedDisplay]

            assertEquals(originalCorrectAnswer, resolvedOriginalOptionId)
            assertTrue(MathEngine.checkAnswer(originalCorrectAnswer, resolvedOriginalOptionId ?: ""))

            // Ensure wrong options are NOT evaluated as correct
            val wrongDisplayLetters = mapping.keys.filter { it != displayCorrectLetter }
            for (wrong in wrongDisplayLetters) {
                val resolvedWrong = mapping[wrong]
                assertFalse(MathEngine.checkAnswer(originalCorrectAnswer, resolvedWrong ?: ""))
            }
        }
    }

    @Test
    fun testScoreCalculation_accuracy() {
        val correctAnswers = mapOf(
            "q1" to "A",
            "q2" to "B",
            "q3" to "C",
            "q4" to "D",
            "q5" to "A"
        )

        val userAnswers = mapOf(
            "q1" to "A", // correct
            "q2" to "B", // correct
            "q3" to "D", // wrong
            "q4" to "D", // correct
            "q5" to "C"  // wrong
        )

        var score = 0
        for ((qId, correct) in correctAnswers) {
            val studentAns = userAnswers[qId] ?: ""
            if (MathEngine.checkAnswer(correct, studentAns)) {
                score++
            }
        }

        assertEquals(3, score)
    }
}
