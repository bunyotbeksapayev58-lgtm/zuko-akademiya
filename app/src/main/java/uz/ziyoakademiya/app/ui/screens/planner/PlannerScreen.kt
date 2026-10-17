package uz.ziyoakademiya.app.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.ziyoakademiya.app.core.domain.model.StudyPlanDay
import uz.ziyoakademiya.app.core.theme.*
import uz.ziyoakademiya.app.ui.components.AppTopBar

@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTopic: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "42-kunlik o'quv rejasi",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Progress Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "O'quv rejasi rivoji",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TurquoiseLight
                            )
                            Text(
                                text = "${state.completedCount} / 42 kun",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        val progress = (state.completedCount / 42f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = TurquoiseAccent,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kunlik reja: ${state.dailyMinutes} daqiqa dars va amaliyot.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Kunbay rejalar jadvali",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(state.planDays, key = { it.dayNumber }) { day ->
                PlanDayCard(
                    day = day,
                    onToggle = { viewModel.toggleDayCompleted(day.dayNumber) },
                    onOpenTopic = {
                        day.topicId?.let { onNavigateToTopic(it) }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun PlanDayCard(
    day: StudyPlanDay,
    onToggle: () -> Unit,
    onOpenTopic: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (day.isCompleted) {
                SuccessGreen.copy(alpha = 0.08f)
            } else if (day.isRestDay) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (day.isCompleted) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                    contentDescription = "Bajarildi deb belgilash",
                    tint = if (day.isCompleted) SuccessGreen else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${day.dayNumber}-kun",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (day.isRestDay) MaterialTheme.colorScheme.onSurfaceVariant else TurquoiseDark
                    )
                    if (day.isRestDay) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Hotel,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = day.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = day.topicTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (day.topicId != null) {
                IconButton(onClick = onOpenTopic) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Darsga o'tish",
                        tint = TurquoiseAccent
                    )
                }
            }
        }
    }
}
