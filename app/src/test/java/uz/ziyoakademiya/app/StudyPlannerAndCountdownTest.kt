package uz.ziyoakademiya.app

import org.junit.Assert.*
import org.junit.Test
import uz.ziyoakademiya.app.core.domain.engine.StudyPlannerEngine
import java.time.LocalDate

class StudyPlannerAndCountdownTest {

    @Test
    fun testCountdownCalculation_futureDate() {
        val currentDate = LocalDate.of(2026, 10, 15)
        val examDateStr = "2026-11-28"

        val days = StudyPlannerEngine.calculateDaysRemaining(examDateStr, currentDate)
        // From Oct 15 to Nov 28 is 44 days (16 in Oct + 28 in Nov)
        assertEquals(44L, days)
    }

    @Test
    fun testCountdownCalculation_pastDateReturnsZero() {
        val currentDate = LocalDate.of(2026, 12, 1)
        val examDateStr = "2026-11-28"

        val days = StudyPlannerEngine.calculateDaysRemaining(examDateStr, currentDate)
        assertEquals(0L, days)
    }

    @Test
    fun test42DayPlan_structureAndDays() {
        val plan = StudyPlannerEngine.generate42DayPlan(
            topics = emptyList(),
            dailyMinutes = 45,
            completedDays = setOf(1, 2)
        )

        assertEquals(42, plan.size)

        // Day 1 and 2 should be marked completed
        assertTrue(plan[0].isCompleted)
        assertTrue(plan[1].isCompleted)
        assertFalse(plan[2].isCompleted)

        // Day 7, 14, 21, 28, 35 should be rest days
        assertTrue(plan[6].isRestDay)
        assertTrue(plan[13].isRestDay)
        assertTrue(plan[20].isRestDay)
        assertTrue(plan[27].isRestDay)
        assertTrue(plan[34].isRestDay)

        // Day 42 is the final comprehensive mock day
        assertEquals(42, plan[41].dayNumber)
        assertFalse(plan[41].isRestDay)
    }
}
