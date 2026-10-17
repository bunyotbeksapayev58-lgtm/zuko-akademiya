package uz.ziyoakademiya.app.ui.screens.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.ziyoakademiya.app.core.domain.model.CommonMistake
import uz.ziyoakademiya.app.core.domain.model.GuidedPracticeItem
import uz.ziyoakademiya.app.core.domain.model.WorkedExample
import uz.ziyoakademiya.app.core.theme.*
import uz.ziyoakademiya.app.ui.components.AppTopBar
import uz.ziyoakademiya.app.ui.components.MathFormulaCard
import uz.ziyoakademiya.app.ui.components.TopicGeometryDiagramCard

@Composable
fun LessonScreen(
    topicId: String,
    viewModel: SubjectsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: (String) -> Unit
) {
    LaunchedEffect(topicId) {
        viewModel.loadTopicDetail(topicId)
    }

    val state by viewModel.uiState.collectAsState()
    val lesson = state.currentLesson

    Scaffold(
        topBar = {
            AppTopBar(
                title = lesson?.title ?: "Dars mashg'uloti",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        if (lesson == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Dars ma'lumotlari yuklanmoqda...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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

                // 1. Definition Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = TurquoiseAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ta'rif va Asosiy tushuncha",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = lesson.definition,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                // 2. Geometry Schematic Diagram (if geometry topic)
                item {
                    TopicGeometryDiagramCard(topicId = topicId)
                }

                // 3. Prerequisites
                if (lesson.prerequisites.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Zaruriy boshlang'ich bilimlar",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                lesson.prerequisites.forEach { req ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = TurquoiseAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = req, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Formulas
                if (lesson.formulas.isNotEmpty()) {
                    item {
                        Text(
                            text = "Asosiy qoidalar va formulalar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            lesson.formulas.forEach { f ->
                                MathFormulaCard(
                                    title = f.title,
                                    formula = f.formula,
                                    explanation = f.explanation
                                )
                            }
                        }
                    }
                }

                // 4. Worked Examples (3 examples)
                if (lesson.workedExamples.isNotEmpty()) {
                    item {
                        Text(
                            text = "Batafsil yechilgan namunaviy misollar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    lesson.workedExamples.forEachIndexed { index, example ->
                        item {
                            WorkedExampleCard(index = index + 1, example = example)
                        }
                    }
                }

                // 5. Common Mistakes
                if (lesson.commonMistakes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Keng tarqalgan xatolar va ularning sababi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    lesson.commonMistakes.forEach { mistake ->
                        item {
                            CommonMistakeCard(mistake = mistake)
                        }
                    }
                }

                // 6. Guided Practice
                if (lesson.guidedPractice.isNotEmpty()) {
                    item {
                        Text(
                            text = "Mustaqil mashq (Yo'naltiruvchi shpora bilan)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    lesson.guidedPractice.forEach { practice ->
                        item {
                            GuidedPracticeCard(item = practice)
                        }
                    }
                }

                // 7. Revision Recommendation & Quiz Button
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyDark)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = TurquoiseAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "O'qituvchi tavsiyasi",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TurquoiseAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = lesson.revisionRecommendation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { onNavigateToQuiz(topicId) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TurquoiseAccent)
                            ) {
                                Text(
                                    text = "Mavzu bo'yicha testni boshlash",
                                    color = NavyDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun WorkedExampleCard(index: Int, example: WorkedExample) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$index-misol",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (example.difficulty) {
                        "Oson" -> SuccessGreen.copy(alpha = 0.15f)
                        "O'rta" -> WarningOrange.copy(alpha = 0.15f)
                        else -> ErrorRed.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = example.difficulty,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (example.difficulty) {
                            "Oson" -> SuccessGreen
                            "O'rta" -> WarningOrange
                            else -> ErrorRed
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = example.problemText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Yechish bosqichlari:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            example.solutionSteps.forEachIndexed { sIdx, step ->
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text(
                        text = "${sIdx + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TurquoiseAccent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = step, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SuccessGreen.copy(alpha = 0.1f))
                    .border(1.dp, SuccessGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Javob: ${example.finalAnswer}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
            }
        }
    }
}

@Composable
private fun CommonMistakeCard(mistake: CommonMistake) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = mistake.mistakeTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = ErrorRed
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Noto'g'ri: ${mistake.wrongApproach}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ErrorRed
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "To'g'ri: ${mistake.correctApproach}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SuccessGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Sababi: ${mistake.reason}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GuidedPracticeCard(item: GuidedPracticeItem) {
    var showHint by remember { mutableStateOf(false) }
    var showAnswer by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = item.questionText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showHint = !showHint },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showHint) "Shporani yashirish" else "Maslahat (shpora)")
                }

                OutlinedButton(
                    onClick = { showAnswer = !showAnswer },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (showAnswer) "Javobni yashirish" else "To'g'ri javob")
                }
            }

            if (showHint) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldAccent.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Maslahat: ${item.hint}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (showAnswer) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SuccessGreen.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "To'g'ri javob: ${item.answer}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Text(
                            text = item.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
