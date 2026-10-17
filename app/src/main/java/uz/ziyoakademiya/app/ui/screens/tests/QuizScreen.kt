package uz.ziyoakademiya.app.ui.screens.tests

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.ziyoakademiya.app.core.theme.*
import uz.ziyoakademiya.app.ui.components.OriginalContentPill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: TestsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: (String) -> Unit
) {
    val state by viewModel.activeTestState.collectAsState()
    var showSubmitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSubmitted) {
        if (state.isSubmitted && state.completedSession != null) {
            onNavigateToResult(state.completedSession!!.sessionId)
        }
    }

    val currentQ = state.questions.getOrNull(state.currentIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        val mins = state.remainingSeconds / 60
                        val secs = state.remainingSeconds % 60
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (state.remainingSeconds < 300) ErrorRed else TurquoiseAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%02d:%02d", mins, secs),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (state.remainingSeconds < 300) ErrorRed else TurquoiseAccent
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Ortga"
                        )
                    }
                },
                actions = {
                    currentQ?.let { q ->
                        val isBookmarked = state.bookmarkedQuestionIds.contains(q.originalQuestion.id)
                        IconButton(onClick = {
                            viewModel.toggleBookmark(q.originalQuestion.id, q.originalQuestion.topicId)
                        }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Saqlash",
                                tint = if (isBookmarked) GoldAccent else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    TextButton(onClick = { showSubmitDialog = true }) {
                        Text("Yakunlash", fontWeight = FontWeight.Bold, color = TurquoiseAccent)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        if (state.isLoading || currentQ == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TurquoiseAccent)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Progress Bar
                LinearProgressIndicator(
                    progress = { ((state.currentIndex + 1).toFloat() / state.questions.size.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = TurquoiseAccent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(10.dp)) }

                    // Header: Question index & Tag
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Savol ${state.currentIndex + 1} / ${state.questions.size}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            OriginalContentPill()
                        }
                    }

                    // Question Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = currentQ.originalQuestion.questionText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }

                    // Options List
                    val selectedOriginalOpt = state.userAnswers[currentQ.originalQuestion.id]
                    items(currentQ.displayOptions.size) { optIdx ->
                        val opt = currentQ.displayOptions[optIdx]
                        val originalOptId = currentQ.optionMapping[opt.id]
                        val isSelected = selectedOriginalOpt == originalOptId

                        OptionCard(
                            displayId = opt.id,
                            text = opt.text,
                            isSelected = isSelected,
                            onSelect = {
                                viewModel.selectAnswer(currentQ.originalQuestion.id, opt.id)
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }

                // Bottom Navigation Row
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.goToQuestion(state.currentIndex - 1) },
                            enabled = state.currentIndex > 0,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Oldingisi")
                        }

                        if (state.currentIndex < state.questions.size - 1) {
                            Button(
                                onClick = { viewModel.goToQuestion(state.currentIndex + 1) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TurquoiseAccent)
                            ) {
                                Text("Keyingisi", color = NavyDark, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { showSubmitDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                            ) {
                                Text("Testni yakunlash", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSubmitDialog) {
        val answeredCount = state.userAnswers.size
        val totalCount = state.questions.size
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Testni yakunlash") },
            text = {
                Text(
                    text = "Siz $totalCount ta savoldan $answeredCount tasiga javob berdingiz. Testni haqiqatan ham yakunlamoqchimisiz?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitDialog = false
                        viewModel.submitActiveTest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Ha, yakunlash")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Davom ettirish")
                }
            }
        )
    }
}

@Composable
private fun OptionCard(
    displayId: String,
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) TurquoiseAccent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, TurquoiseAccent) else null,
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) TurquoiseAccent else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayId,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) NavyDark else MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
