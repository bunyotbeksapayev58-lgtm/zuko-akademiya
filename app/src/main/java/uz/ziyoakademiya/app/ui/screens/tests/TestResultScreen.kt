package uz.ziyoakademiya.app.ui.screens.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.ziyoakademiya.app.core.domain.engine.MathEngine
import uz.ziyoakademiya.app.core.theme.*
import uz.ziyoakademiya.app.ui.components.AppTopBar

@Composable
fun TestResultScreen(
    sessionId: String,
    viewModel: TestsViewModel,
    onNavigateHome: () -> Unit,
    onNavigateToMistakes: () -> Unit
) {
    val state by viewModel.activeTestState.collectAsState()
    val session = state.completedSession

    val score = session?.score ?: 0
    val total = session?.totalQuestions ?: state.questions.size.coerceAtLeast(1)
    val percentage = ((score.toFloat() / total.toFloat()) * 100).toInt()

    Scaffold(
        topBar = {
            AppTopBar(title = "Natijalar tahlili")
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Score Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (percentage >= 70) "Ajoyib natija!" else "Mashq qilishda davom eting!",
                            style = MaterialTheme.typography.titleMedium,
                            color = TurquoiseLight,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "$score / $total",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 42.sp
                        )
                        Text(
                            text = "Aniqlik darajasi: $percentage%",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (percentage >= 70) SuccessGreen else WarningOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Disclaimer Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Ushbu ball amaliyot natijasidir. Rasmiy sertifikat ballari DTM/BMB mezonlariga asosan hisoblanadi.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNavigateHome,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TurquoiseAccent)
                    ) {
                        Text("Bosh sahifa", color = NavyDark, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onNavigateToMistakes,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Xatolar daftari")
                    }
                }
            }

            // Question Review Header
            item {
                Text(
                    text = "Savollar bo'yicha batafsil hisobot",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Questions breakdown
            items(state.questions.size) { index ->
                val qItem = state.questions[index]
                val q = qItem.originalQuestion
                val studentOptId = state.userAnswers[q.id] ?: ""
                val isCorrect = MathEngine.checkAnswer(q.correctAnswer, studentOptId)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}-savol",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (isCorrect) SuccessGreen else ErrorRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCorrect) "To'g'ri" else "Noto'g'ri",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) SuccessGreen else ErrorRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = q.questionText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val studentOptText = q.options.find { it.id == studentOptId }?.text ?: studentOptId.ifEmpty { "Belgilanmagan" }
                        val correctOptText = q.options.find { it.id == q.correctAnswer }?.text ?: q.correctAnswer

                        Text(
                            text = "Sizning javobingiz: $studentOptId ($studentOptText)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCorrect) SuccessGreen else ErrorRed
                        )
                        if (!isCorrect) {
                            Text(
                                text = "To'g'ri javob: ${q.correctAnswer} ($correctOptText)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tushuntirish: ${q.explanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}
