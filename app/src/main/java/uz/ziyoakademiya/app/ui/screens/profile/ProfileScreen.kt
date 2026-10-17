package uz.ziyoakademiya.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.ziyoakademiya.app.core.theme.*
import uz.ziyoakademiya.app.ui.components.AppTopBar

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel
) {
    val state by viewModel.uiState.collectAsState()
    var showDateDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(title = "Profil va Sozlamalar")
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

            // 1. Learner Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(TurquoiseAccent.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = TurquoiseAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "O'quvchi profili",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.surface
                                )
                                Text(
                                    text = "${state.grade} • ${state.targetExam}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TurquoiseLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${state.totalQuestionsAnswered}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.surface
                                )
                                Text(
                                    text = "Jami urinishlar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                )
                            }
                            Column {
                                val acc = if (state.totalQuestionsAnswered > 0) {
                                    ((state.totalCorrect.toFloat() / state.totalQuestionsAnswered.toFloat()) * 100).toInt()
                                } else 0
                                Text(
                                    text = "$acc%",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                                Text(
                                    text = "To'g'ri javoblar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                )
                            }
                            Column {
                                Text(
                                    text = "${state.dailyMinutes} daq",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                                Text(
                                    text = "Kunlik maqsad",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Settings Section
            item {
                Text(
                    text = "O'quv sozlamalari",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SettingActionCard(
                    title = "Maqsadli imtihon sanasi",
                    subtitle = state.targetExamDate,
                    icon = Icons.Default.CalendarToday,
                    onClick = { showDateDialog = true }
                )
            }

            item {
                SettingActionCard(
                    title = "Kunlik o'quv vaqti maqsadi",
                    subtitle = "${state.dailyMinutes} daqiqa (O'zgartirish uchun bosing)",
                    icon = Icons.Default.Timer,
                    onClick = {
                        val next = when (state.dailyMinutes) {
                            30 -> 45
                            45 -> 60
                            60 -> 90
                            else -> 30
                        }
                        viewModel.updateDailyGoal(next)
                    }
                )
            }

            item {
                SettingActionCard(
                    title = "Sinf / Bosqich",
                    subtitle = state.grade,
                    icon = Icons.Default.School,
                    onClick = {
                        val next = when (state.grade) {
                            "10-sinf" -> "11-sinf"
                            "11-sinf" -> "Litsey / Kollej"
                            "Litsey / Kollej" -> "Bitiruvchi"
                            else -> "10-sinf"
                        }
                        viewModel.updateGrade(next)
                    }
                )
            }

            // 3. Data & Privacy Section
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ma'lumotlar va Maxfiylik",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                SettingActionCard(
                    title = "O'quv natijalarini eksport qilish (JSON)",
                    subtitle = "Natijalarni fayl yoki matn ko'rinishida saqlab olish",
                    icon = Icons.Default.FileDownload,
                    onClick = { viewModel.exportProgressJson() }
                )
            }

            item {
                SettingActionCard(
                    title = "Maxfiylik siyosati va Ma'lumotlar xavfsizligi",
                    subtitle = "Lokal ma'lumotlar va foydalanish shartlari",
                    icon = Icons.Default.PrivacyTip,
                    onClick = { showPrivacyDialog = true }
                )
            }

            item {
                SettingActionCard(
                    title = "Barcha ma'lumotlarni tozalash",
                    subtitle = "Natijalar va testlar tarixini qayta boshlash",
                    icon = Icons.Default.DeleteForever,
                    color = ErrorRed,
                    onClick = { showResetDialog = true }
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    // Exam Date Dialog
    if (showDateDialog) {
        var tempDate by remember { mutableStateOf(state.targetExamDate) }
        AlertDialog(
            onDismissRequest = { showDateDialog = false },
            title = { Text("Imtihon sanasini belgilash") },
            text = {
                Column {
                    Text(
                        text = "Imtihon sanasini YYYY-MM-DD ko'rinishida kiriting:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempDate,
                        onValueChange = { tempDate = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    showDateDialog = false
                    viewModel.updateTargetExamDate(tempDate)
                }) {
                    Text("Saqlash")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateDialog = false }) {
                    Text("Bekor qilish")
                }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Ma'lumotlarni tozalash") },
            text = {
                Text(
                    text = "Barcha yechilgan testlar, xatolar daftari va o'quv natijalari to'liq o'chiriladi. Bu amalni qaytarib bo'lmaydi. Davom etasizmi?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetAllUserData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Ha, o'chirish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Bekor qilish")
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Maxfiylik va Xavfsizlik") },
            text = {
                Text(
                    text = "1. Ushbu ilova shaxsiy ma'lumotlarni yig'maydi va tashqi serverlarga yubormaydi.\n" +
                            "2. Barcha testlar, darslar, xatolar tarixi va o'quv rejasi faqat qurilmangiz xotirasida (Room ma'lumotlar bazasida) saqlanadi.\n" +
                            "3. Dastur to'liq oflayn ishlaydi va internet ulanishini talab qilmaydi.\n" +
                            "4. DXX qabul talablari ochiq manbalarga tayangan bo'lib, rasmiy e'lonlar orqali tekshirib borilishi zarur."
                )
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }) {
                    Text("Tushundim")
                }
            }
        )
    }

    // Export Dialog
    state.exportJsonString?.let { json ->
        AlertDialog(
            onDismissRequest = { viewModel.clearExportDialog() },
            title = { Text("Eksport qilingan JSON") },
            text = {
                Text(
                    text = json,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                Button(onClick = { viewModel.clearExportDialog() }) {
                    Text("Yopish")
                }
            }
        )
    }
}

@Composable
private fun SettingActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color = TurquoiseAccent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (color == ErrorRed) ErrorRed else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
