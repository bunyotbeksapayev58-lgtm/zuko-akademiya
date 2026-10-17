package uz.ziyoakademiya.app.ui.screens.aitutor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uz.ziyoakademiya.app.core.theme.*
import uz.ziyoakademiya.app.ui.components.AppTopBar

@Composable
fun AiTutorScreen(
    viewModel: AiTutorViewModel
) {
    val state by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val suggestions = listOf(
        "Logarifm xossalarini tushuntir",
        "sin(30) + cos(60) qanday hisoblanadi?",
        "Hosila olish qoidalarini ko'rsat",
        "Prizma hajmi va to'la sirti formulasi",
        "sqrt(2x - 6) aniqlanish sohasi"
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "AI Matematika Repetitori",
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TurquoiseAccent.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = if (state.isOnlineEnabled) "Onlayn AI" else "Oflayn ta'limiy baza",
                            style = MaterialTheme.typography.labelSmall,
                            color = TurquoiseDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Chat message list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(6.dp)) }

                items(state.messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg)
                }

                if (state.isThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = TurquoiseAccent
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Repetitor javob tayyorlamoqda...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(10.dp)) }
            }

            // Quick suggestion chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { s ->
                    SuggestionChip(
                        onClick = {
                            viewModel.sendMessage(s)
                            scope.launch {
                                listState.animateScrollToItem(state.messages.size)
                            }
                        },
                        label = {
                            Text(
                                text = s,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Savolingiz yoki formulani yozing...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TurquoiseAccent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val text = inputText
                                inputText = ""
                                viewModel.sendMessage(text)
                                scope.launch {
                                    listState.animateScrollToItem(state.messages.size)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(TurquoiseAccent)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Yuborish",
                            tint = NavyDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "user"
    var showHint by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavyMedium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = TurquoiseAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = if (isUser) 14.dp else 2.dp,
                    bottomEnd = if (isUser) 2.dp else 14.dp
                ),
                color = if (isUser) TurquoiseAccent else MaterialTheme.colorScheme.surface,
                tonalElevation = if (isUser) 0.dp else 2.dp,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUser) NavyDark else MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    if (!message.hintText.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { showHint = !showHint },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showHint) "Maslahatni yashirish" else "Maslahat (shpora)",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (showHint) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldAccent.copy(alpha = 0.15f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = message.hintText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
