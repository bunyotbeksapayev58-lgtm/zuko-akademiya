package uz.ziyoakademiya.app.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Subjects : Screen("subjects")
    object Tests : Screen("tests")
    object AiTutor : Screen("ai_tutor")
    object Profile : Screen("profile")
    object Academy : Screen("academy")
    object Planner : Screen("planner")
    object Mistakes : Screen("mistakes")
    object Bookmarks : Screen("bookmarks")
    object Diagnostic : Screen("diagnostic")

    object TopicDetail : Screen("topic_detail/{topicId}") {
        fun createRoute(topicId: String) = "topic_detail/$topicId"
    }

    object Lesson : Screen("lesson/{topicId}") {
        fun createRoute(topicId: String) = "lesson/$topicId"
    }

    object Quiz : Screen("quiz/{topicId}") {
        fun createRoute(topicId: String) = "quiz/$topicId"
    }

    object MockExam : Screen("mock_exam/{sessionId}") {
        fun createRoute(sessionId: String) = "mock_exam/$sessionId"
    }

    object TestResult : Screen("test_result/{sessionId}") {
        fun createRoute(sessionId: String) = "test_result/$sessionId"
    }
}
