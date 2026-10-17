package uz.ziyoakademiya.app.ui.screens.academy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.ziyoakademiya.app.core.domain.model.AcademyRequirement
import uz.ziyoakademiya.app.core.theme.*
import uz.ziyoakademiya.app.ui.components.AppTopBar
import uz.ziyoakademiya.app.ui.components.SourceBadgePill

@Composable
fun AcademyScreen(
    viewModel: AcademyViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val filteredRequirements = if (state.selectedCategory == "Barchasi") {
        state.requirements
    } else {
        state.requirements.filter { it.category == state.selectedCategory }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "DXX Akademiyasiga tayyorgarlik",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Mandatory Disclaimer Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = TurquoiseAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Muhim rasmiy ogohlantirish",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TurquoiseAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ushbu ilova mustaqil ta'limiy dastur bo'lib, Davlat xavfsizlik xizmati (DXX) yoki boshqa davlat idoralari bilan rasmiy aloqador emas va ularning nomidan ish yuritmaydi. Qabul talablari ochiq qonunchilik hujjatlariga asoslangan bo'lib, o'zgarishi mumkin. Sertifikat qabulni kafolatlamaydi.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            // 2. Category Filter Pills
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.categories) { category ->
                        FilterChip(
                            selected = state.selectedCategory == category,
                            onClick = { viewModel.selectCategory(category) },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TurquoiseAccent,
                                selectedLabelColor = NavyDark
                            )
                        )
                    }
                }
            }

            // 3. Requirements list
            items(filteredRequirements, key = { it.id }) { req ->
                RequirementCard(req = req)
            }

            // 4. Safe Fitness & Healthy Habits Section
            item {
                FitnessGuideCard()
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun RequirementCard(req: AcademyRequirement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                    text = req.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = TurquoiseDark,
                    fontWeight = FontWeight.Bold
                )
                SourceBadgePill(isVerified = req.isVerified)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = req.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = req.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Manba: ${req.officialSource} (${req.lastVerifiedDate})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (req.verificationNotice.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = req.verificationNotice,
                    style = MaterialTheme.typography.bodySmall,
                    color = WarningOrange
                )
            }
        }
    }
}

@Composable
private fun FitnessGuideCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DirectionsRun,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Xavfsiz jismoniy tayyorgarlik va sog'lom rejim",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "1. Turnikda tortilish: To'g'ri nafas olish, qaddini rost tutish va jarohatlarsiz asta-sekin ko'paytirish (kuniga 3-4 yondashuv).\n" +
                        "2. Yugurish: 100 m tezkorlik va 3000 m kross uchun yurak urish maromini nazorat qilish.\n" +
                        "3. Uyqu va dam olish: Miya va mushaklar to'liq tiklanishi uchun kamida 7-8 soatlik sifatli uyqu shart.\n" +
                        "4. Suv va ovqatlanish: Yetarli miqdorda toza suv ichish va muvozanatli oqsilli taomlar iste'mol qilish.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
