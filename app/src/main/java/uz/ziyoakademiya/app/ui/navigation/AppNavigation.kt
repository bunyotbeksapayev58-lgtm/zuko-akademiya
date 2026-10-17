package uz.ziyoakademiya.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uz.ziyoakademiya.app.ZiyoApplication
import uz.ziyoakademiya.app.ui.screens.academy.AcademyScreen
import uz.ziyoakademiya.app.ui.screens.academy.AcademyViewModel
import uz.ziyoakademiya.app.ui.screens.aitutor.AiTutorScreen
import uz.ziyoakademiya.app.ui.screens.aitutor.AiTutorViewModel
import uz.ziyoakademiya.app.ui.screens.home.HomeScreen
import uz.ziyoakademiya.app.ui.screens.home.HomeViewModel
import uz.ziyoakademiya.app.ui.screens.onboarding.OnboardingScreen
import uz.ziyoakademiya.app.ui.screens.planner.PlannerScreen
import uz.ziyoakademiya.app.ui.screens.planner.PlannerViewModel
import uz.ziyoakademiya.app.ui.screens.profile.ProfileScreen
import uz.ziyoakademiya.app.ui.screens.profile.ProfileViewModel
import uz.ziyoakademiya.app.ui.screens.subjects.LessonScreen
import uz.ziyoakademiya.app.ui.screens.subjects.SubjectsScreen
import uz.ziyoakademiya.app.ui.screens.subjects.SubjectsViewModel
import uz.ziyoakademiya.app.ui.screens.subjects.TopicDetailScreen
import uz.ziyoakademiya.app.ui.screens.tests.*

@Composable
fun AppNavigation(
    app: ZiyoApplication,
    navController: NavHostController,
    startDestination: String
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    preferencesRepository = app.userPreferencesRepository,
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // Top-Level Tabs
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(
                        app.userPreferencesRepository,
                        app.curriculumRepository,
                        app.questionRepository
                    )
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToTopic = { topicId ->
                        navController.navigate(Screen.TopicDetail.createRoute(topicId))
                    },
                    onNavigateToMock = {
                        navController.navigate(Screen.Tests.route)
                    },
                    onNavigateToDiagnostic = {
                        navController.navigate(Screen.Diagnostic.route)
                    },
                    onNavigateToAcademy = {
                        navController.navigate(Screen.Academy.route)
                    },
                    onNavigateToPlanner = {
                        navController.navigate(Screen.Planner.route)
                    },
                    onNavigateToMistakes = {
                        navController.navigate(Screen.Mistakes.route)
                    }
                )
            }

            composable(Screen.Subjects.route) {
                val subjectsViewModel: SubjectsViewModel = viewModel(
                    factory = SubjectsViewModel.Factory(
                        app.curriculumRepository,
                        app.questionRepository
                    )
                )
                SubjectsScreen(
                    viewModel = subjectsViewModel,
                    onNavigateToTopic = { topicId ->
                        navController.navigate(Screen.TopicDetail.createRoute(topicId))
                    }
                )
            }

            composable(Screen.Tests.route) {
                val testsViewModel: TestsViewModel = viewModel(
                    factory = TestsViewModel.Factory(
                        app.questionRepository,
                        app.examRepository,
                        app.curriculumRepository
                    )
                )
                TestsHubScreen(
                    viewModel = testsViewModel,
                    onStartMockExam = {
                        navController.navigate(Screen.Quiz.createRoute("mock_exam"))
                    },
                    onStartDiagnostic = {
                        navController.navigate(Screen.Quiz.createRoute("diagnostic"))
                    },
                    onNavigateToMistakes = {
                        navController.navigate(Screen.Mistakes.route)
                    },
                    onNavigateToBookmarks = {
                        navController.navigate(Screen.Bookmarks.route)
                    },
                    onStartTopicQuiz = { topicId ->
                        navController.navigate(Screen.Quiz.createRoute(topicId))
                    }
                )
            }

            composable(Screen.AiTutor.route) {
                val aiViewModel: AiTutorViewModel = viewModel(
                    factory = AiTutorViewModel.Factory(
                        app.userPreferencesRepository,
                        app.curriculumRepository,
                        app.questionRepository
                    )
                )
                AiTutorScreen(viewModel = aiViewModel)
            }

            composable(Screen.Profile.route) {
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModel.Factory(
                        app.userPreferencesRepository,
                        app.questionRepository,
                        app.examRepository
                    )
                )
                ProfileScreen(viewModel = profileViewModel)
            }

            // Sub-screens
            composable(
                route = Screen.TopicDetail.route,
                arguments = listOf(navArgument("topicId") { type = NavType.StringType })
            ) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
                val subjectsViewModel: SubjectsViewModel = viewModel(
                    factory = SubjectsViewModel.Factory(
                        app.curriculumRepository,
                        app.questionRepository
                    )
                )
                TopicDetailScreen(
                    topicId = topicId,
                    viewModel = subjectsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToLesson = { id ->
                        navController.navigate(Screen.Lesson.createRoute(id))
                    },
                    onNavigateToQuiz = { id ->
                        navController.navigate(Screen.Quiz.createRoute(id))
                    }
                )
            }

            composable(
                route = Screen.Lesson.route,
                arguments = listOf(navArgument("topicId") { type = NavType.StringType })
            ) { backStackEntry ->
                val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
                val subjectsViewModel: SubjectsViewModel = viewModel(
                    factory = SubjectsViewModel.Factory(
                        app.curriculumRepository,
                        app.questionRepository
                    )
                )
                LessonScreen(
                    topicId = topicId,
                    viewModel = subjectsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToQuiz = { id ->
                        navController.navigate(Screen.Quiz.createRoute(id))
                    }
                )
            }

            composable(
                route = Screen.Quiz.route,
                arguments = listOf(navArgument("topicId") { type = NavType.StringType })
            ) {
                val testsViewModel: TestsViewModel = viewModel(
                    factory = TestsViewModel.Factory(
                        app.questionRepository,
                        app.examRepository,
                        app.curriculumRepository
                    )
                )
                QuizScreen(
                    viewModel = testsViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToResult = { sessionId ->
                        navController.navigate(Screen.TestResult.createRoute(sessionId))
                    }
                )
            }

            composable(
                route = Screen.TestResult.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
                val testsViewModel: TestsViewModel = viewModel(
                    factory = TestsViewModel.Factory(
                        app.questionRepository,
                        app.examRepository,
                        app.curriculumRepository
                    )
                )
                TestResultScreen(
                    sessionId = sessionId,
                    viewModel = testsViewModel,
                    onNavigateHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateToMistakes = {
                        navController.navigate(Screen.Mistakes.route)
                    }
                )
            }

            composable(Screen.Mistakes.route) {
                val testsViewModel: TestsViewModel = viewModel(
                    factory = TestsViewModel.Factory(
                        app.questionRepository,
                        app.examRepository,
                        app.curriculumRepository
                    )
                )
                MistakeNotebookScreen(
                    viewModel = testsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Bookmarks.route) {
                val testsViewModel: TestsViewModel = viewModel(
                    factory = TestsViewModel.Factory(
                        app.questionRepository,
                        app.examRepository,
                        app.curriculumRepository
                    )
                )
                BookmarksScreen(
                    viewModel = testsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Academy.route) {
                val academyViewModel: AcademyViewModel = viewModel(
                    factory = AcademyViewModel.Factory(app.academyRepository)
                )
                AcademyScreen(
                    viewModel = academyViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Planner.route) {
                val plannerViewModel: PlannerViewModel = viewModel(
                    factory = PlannerViewModel.Factory(
                        app.userPreferencesRepository,
                        app.curriculumRepository
                    )
                )
                PlannerScreen(
                    viewModel = plannerViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTopic = { topicId ->
                        navController.navigate(Screen.TopicDetail.createRoute(topicId))
                    }
                )
            }
        }
    }
}
