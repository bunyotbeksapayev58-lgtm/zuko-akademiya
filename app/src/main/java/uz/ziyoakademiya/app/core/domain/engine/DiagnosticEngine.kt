package uz.ziyoakademiya.app.core.domain.engine

import uz.ziyoakademiya.app.core.domain.model.Attempt
import uz.ziyoakademiya.app.core.domain.model.Topic

object DiagnosticEngine {

    val PRIORITY_REVISION_TOPIC_IDS = setOf(
        "topic_logarithms",
        "topic_trigonometry",
        "topic_functions",
        "topic_derivatives",
        "topic_integrals",
        "topic_prisms"
    )

    /**
     * Calculates mastery percentage for each topic based on attempts.
     */
    fun calculateTopicMastery(
        allTopics: List<Topic>,
        attempts: List<Attempt>
    ): Map<String, Int> {
        val attemptsByTopic = attempts.groupBy { it.topicId }
        val masteryMap = mutableMapOf<String, Int>()

        for (topic in allTopics) {
            val topicAttempts = attemptsByTopic[topic.id]
            if (topicAttempts.isNullOrEmpty()) {
                masteryMap[topic.id] = 0
            } else {
                // Group by questionId so repeated attempts don't inflate counts
                val latestByQuestion = topicAttempts
                    .groupBy { it.questionId }
                    .mapValues { (_, atts) -> atts.maxByOrNull { it.timestamp }?.isCorrect == true }

                val correctCount = latestByQuestion.values.count { it }
                val totalCount = latestByQuestion.size

                val percentage = if (totalCount > 0) {
                    ((correctCount.toDouble() / totalCount.toDouble()) * 100).toInt()
                } else 0
                masteryMap[topic.id] = percentage
            }
        }
        return masteryMap
    }

    /**
     * Identifies weak topics where mastery is below 70%,
     * prioritizing the student's designated revision topics.
     */
    fun identifyWeakTopics(
        allTopics: List<Topic>,
        masteryMap: Map<String, Int>
    ): List<Topic> {
        return allTopics.filter { topic ->
            val mastery = masteryMap[topic.id] ?: 0
            val isPriority = PRIORITY_REVISION_TOPIC_IDS.contains(topic.id)
            // If priority topic with mastery < 80% or regular topic with mastery < 70%
            if (isPriority) mastery < 80 else mastery < 70
        }.sortedWith(
            compareByDescending<Topic> { PRIORITY_REVISION_TOPIC_IDS.contains(it.id) }
                .thenBy { masteryMap[it.id] ?: 0 }
        )
    }
}
