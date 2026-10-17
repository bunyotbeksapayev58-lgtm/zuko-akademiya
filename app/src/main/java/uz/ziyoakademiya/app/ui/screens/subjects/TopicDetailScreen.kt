package uz.ziyoakademiya.app.ui.screens.subjects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.ziyoakademiya.app.core.theme.NavyDark
import uz.ziyoakademiya.app.core.theme.TurquoiseAccent
import uz.ziyoakademiya.app.ui.components.AppTopBar
import uz.ziyoakademiya.app.ui.components.MathFormulaCard
import uz.ziyoakademiya.app.ui.components.TopicGeometryDiagramCard

@Composable
fun TopicDetailScreen(
    topicId: String,
    viewModel: SubjectsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLesson: (String) -> Unit,
    onNavigateToQuiz: (String) -> Unit
) {
    LaunchedEffect(topicId) {
        viewModel.loadTopicDetail(topicId)
    }

    val state by viewModel.uiState.collectAsState()
    val topic = state.currentTopic
    val lesson = state.currentLesson

    Scaffold(
        topBar = {
            AppTopBar(
                title = topic?.title ?: "Mavzu tafsilotlari",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        if (topic == null) {
            Box(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // Topic Header Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "${topic.gradeRange} • ${topic.difficulty} daraja",
                                style = MaterialTheme.typography.labelSmall,
                                color = TurquoiseAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = topic.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = topic.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Geometry Schematic Diagram (if geometry topic)
                item {
                    TopicGeometryDiagramCard(topicId = topic.id)
                }

                // Action Buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToLesson(topic.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TurquoiseAccent)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = NavyDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Darsni o'qish", color = NavyDark, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onNavigateToQuiz(topic.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test yechish", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Formulas section
                lesson?.let { l ->
                    if (l.formulas.isNotEmpty()) {
                        item {
                            Text(
                                text = "Mavzuning asosiy formulalari",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                l.formulas.forEach { item ->
                                    MathFormulaCard(
                                        title = item.title,
                                        formula = item.formula,
                                        explanation = item.explanation
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}
