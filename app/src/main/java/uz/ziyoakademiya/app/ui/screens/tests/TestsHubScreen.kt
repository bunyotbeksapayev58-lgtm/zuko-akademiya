package uz.ziyoakademiya.app.ui.screens.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import uz.ziyoakademiya.app.core.domain.model.Topic
import uz.ziyoakademiya.app.core.theme.*
import uz.ziyoakademiya.app.ui.components.AppTopBar

@Composable
fun TestsHubScreen(
    viewModel: TestsViewModel,
    onStartMockExam: () -> Unit,
    onStartDiagnostic: () -> Unit,
    onNavigateToMistakes: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onStartTopicQuiz: (String) -> Unit
) {
    val topics by viewModel.allTopics.collectAsState()
    val mistakes by viewModel.unresolvedMistakes.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(title = "Testlar va Imtihonlar")
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Mock Exam Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.startMockExam()
                            onStartMockExam()
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TurquoiseAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Rasmiy formatdagi Mock",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TurquoiseAccent,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "60 daqiqa • 30 savol",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Namunaviy Milliy Sertifikat Testi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Algebra, geometriya va matematik analiz bo'yicha to'liq taymerli sinov.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                viewModel.startMockExam()
                                onStartMockExam()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TurquoiseAccent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Imtihonni boshlash", color = NavyDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Secondary Tests Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HubActionCard(
                        title = "Diagnostik Test",
                        subtitle = "15 ta savol",
                        icon = Icons.Default.Assessment,
                        badge = "Bilim darajasi",
                        color = TurquoiseDark,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.startDiagnosticTest()
                            onStartDiagnostic()
                        }
                    )
                    HubActionCard(
                        title = "Xatolar daftari",
                        subtitle = "${mistakes.size} ta xato",
                        icon = Icons.Default.WarningAmber,
                        badge = "Qayta ishlash",
                        color = ErrorRed,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToMistakes
                    )
                }
            }

            item {
                HubActionCard(
                    title = "Saqlangan savollar",
                    subtitle = "${bookmarks.size} ta tanlangan savol",
                    icon = Icons.Default.Bookmark,
                    badge = "Sevimlilar",
                    color = GoldAccent,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToBookmarks
                )
            }

            // 3. Topic Quizzes List
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Mavzuli tezkor testlar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(topics, key = { it.id }) { topic ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.startTopicQuiz(topic.id)
                            onStartTopicQuiz(topic.id)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = topic.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${topic.difficulty} • 5 ta savol",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.startTopicQuiz(topic.id)
                                onStartTopicQuiz(topic.id)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = "Boshlash",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun HubActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = color.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
