package uz.ziyoakademiya.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uz.ziyoakademiya.app.core.data.preferences.UserPreferencesRepository
import uz.ziyoakademiya.app.core.theme.*

@Composable
fun OnboardingScreen(
    preferencesRepository: UserPreferencesRepository,
    onComplete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedGrade by remember { mutableStateOf("10-sinf") }
    var selectedGoal by remember { mutableStateOf(45) }
    var selectedLevel by remember { mutableStateOf("O'rta daraja") }

    val grades = listOf("10-sinf", "11-sinf", "Litsey / Kollej", "Bitiruvchi")
    val goals = listOf(30, 45, 60, 90)
    val levels = listOf("Boshlang'ich", "O'rta daraja", "Yuqori daraja")

    Scaffold(
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(NavyDark, RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = TurquoiseAccent,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ziyo Akademiya",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyDark
                )
                Text(
                    text = "Shaxsiy o'quv rejangizni sozlang",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Question 1: Grade
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Qaysi bosqichda ta'lim olasiz?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            grades.forEach { g ->
                                FilterChip(
                                    selected = selectedGrade == g,
                                    onClick = { selectedGrade = g },
                                    label = { Text(g, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TurquoiseAccent,
                                        selectedLabelColor = NavyDark
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question 2: Daily Study Goal
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "2. Kuniga qancha vaqt ajrata olasiz?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            goals.forEach { m ->
                                FilterChip(
                                    selected = selectedGoal == m,
                                    onClick = { selectedGoal = m },
                                    label = { Text("$m daq", fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TurquoiseAccent,
                                        selectedLabelColor = NavyDark
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question 3: Self-assessed Level
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "3. O'z bilim darajangizni qanday baholaysiz?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            levels.forEach { lvl ->
                                FilterChip(
                                    selected = selectedLevel == lvl,
                                    onClick = { selectedLevel = lvl },
                                    label = { Text(lvl, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TurquoiseAccent,
                                        selectedLabelColor = NavyDark
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Finish button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            preferencesRepository.updateExamProfile(
                                grade = selectedGrade,
                                targetExam = "Milliy sertifikat va DXX Akademiyasi",
                                targetExamDate = "2026-11-28",
                                dailyMinutes = selectedGoal,
                                level = selectedLevel
                            )
                            preferencesRepository.setOnboardingCompleted(true)
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                ) {
                    Text(
                        text = "O'rganishni boshlash",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
