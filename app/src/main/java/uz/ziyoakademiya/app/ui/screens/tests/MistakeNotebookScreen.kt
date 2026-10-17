package uz.ziyoakademiya.app.ui.screens.tests

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uz.ziyoakademiya.app.core.domain.model.Mistake
import uz.ziyoakademiya.app.core.domain.model.Question
import uz.ziyoakademiya.app.core.theme.ErrorRed
import uz.ziyoakademiya.app.core.theme.SuccessGreen
import uz.ziyoakademiya.app.core.theme.TurquoiseAccent
import uz.ziyoakademiya.app.ui.components.AppTopBar

@Composable
fun MistakeNotebookScreen(
    viewModel: TestsViewModel,
    onNavigateBack: () -> Unit
) {
    val mistakes by viewModel.unresolvedMistakes.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Xatolar daftari",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        if (mistakes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Ajoyib! Xatolar daftari bo'sh.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Noto'g'ri ishlangan savollar avtomatik ravishda bu yerga tushadi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Yechilmagan xatolar: ${mistakes.size} ta",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(mistakes, key = { it.questionId }) { mistake ->
                    MistakeCard(
                        mistake = mistake,
                        onResolve = {
                            viewModel.toggleBookmark(mistake.questionId, mistake.topicId)
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun MistakeCard(
    mistake: Mistake,
    onResolve: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Savol ID: ${mistake.questionId}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${mistake.attemptCount} ta urinish",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Oxirgi noto'g'ri tanlov: ${mistake.lastWrongAnswer}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
