package uz.ziyoakademiya.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.ziyoakademiya.app.core.theme.*
import uz.ziyoakademiya.app.ui.components.StatCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToTopic: (String) -> Unit,
    onNavigateToMock: () -> Unit,
    onNavigateToDiagnostic: () -> Unit,
    onNavigateToAcademy: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToMistakes: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ziyo Akademiya",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Milliy sertifikat va DXX tayyorgarligi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onNavigateToMistakes) {
                        BadgedBox(badge = {
                            Badge { Text("X") }
                        }) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Xatolar daftari",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TurquoiseAccent)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // 1. Exam Countdown Card
                item {
                    ExamCountdownCard(
                        days = state.daysRemaining,
                        examTitle = state.targetExamTitle,
                        examDate = state.targetExamDate
                    )
                }

                // 2. Statistics Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Kunlik seriya",
                            value = "${state.streakDays} kun",
                            icon = Icons.Default.Whatshot,
                            color = WarningOrange,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Bugun o'qildi",
                            value = "${state.todayStudyMinutes} daq",
                            icon = Icons.Default.Timer,
                            color = TurquoiseAccent,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Yechilgan",
                            value = "${state.totalQuestionsAnswered}",
                            icon = Icons.Default.CheckCircleOutline,
                            color = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 3. Continue Learning Card
                state.continueTopic?.let { topic ->
                    item {
                        ContinueLearningCard(
                            topicTitle = topic.title,
                            topicDescription = topic.description,
                            onContinue = { onNavigateToTopic(topic.id) }
                        )
                    }
                }

                // 4. Quick Action Buttons Grid
                item {
                    Text(
                        text = "Tezkor amallar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionCard(
                            title = "Namunaviy test",
                            subtitle = "30 ta savol, vaqtli",
                            icon = Icons.Default.Timer,
                            color = NavyMedium,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToMock
                        )
                        QuickActionCard(
                            title = "Diagnostika",
                            subtitle = "Bilim darajasini aniqlash",
                            icon = Icons.Default.Assessment,
                            color = TurquoiseDark,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToDiagnostic
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionCard(
                            title = "DXX Yo'nalishi",
                            subtitle = "Qabul va talablar",
                            icon = Icons.Default.Shield,
                            color = NavyDark,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToAcademy
                        )
                        QuickActionCard(
                            title = "42-kunlik reja",
                            subtitle = "Rejali takrorlash",
                            icon = Icons.Default.CalendarMonth,
                            color = GoldAccent,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToPlanner
                        )
                    }
                }

                // 5. Weak Topics / Recommended Revision
                if (state.weakTopics.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoFixHigh,
                                        contentDescription = null,
                                        tint = TurquoiseAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Qaytarish tavsiya etilgan mavzular",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                state.weakTopics.forEach { topic ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onNavigateToTopic(topic.id) }
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = topic.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "${topic.difficulty} | ${topic.estimatedMinutes} daqiqa",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = null,
                                            tint = TurquoiseAccent
                                        )
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                }
                            }
                        }
                    }
                }

                // 6. Weekly Activity Chart
                item {
                    WeeklyProgressCard(counts = state.weeklyDayCounts)
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun ExamCountdownCard(
    days: Long,
    examTitle: String,
    examDate: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavyDark
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = TurquoiseAccent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Imtihon tayyorgarligi",
                        style = MaterialTheme.typography.labelSmall,
                        color = TurquoiseAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = examDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "$days",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 44.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "kun qoldi",
                    style = MaterialTheme.typography.titleMedium,
                    color = TurquoiseLight,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = examTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun ContinueLearningCard(
    topicTitle: String,
    topicDescription: String,
    onContinue: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Darsni davom ettirish",
                    style = MaterialTheme.typography.labelMedium,
                    color = TurquoiseAccent,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = topicTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = topicDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = TurquoiseAccent),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "O'tish",
                    color = NavyDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeeklyProgressCard(counts: List<Int>) {
    val daysLabels = listOf("Dush", "Sesh", "Chor", "Pay", "Juma", "Shan", "Yak")
    val maxCount = (counts.maxOrNull() ?: 1).coerceAtLeast(5)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Haftalik faollik grafigi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                counts.forEachIndexed { index, count ->
                    val fraction = (count.toFloat() / maxCount.toFloat()).coerceIn(0.08f, 1f)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (count > 0) "$count" else "",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    if (count > 0) TurquoiseAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = daysLabels.getOrElse(index) { "" },
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
