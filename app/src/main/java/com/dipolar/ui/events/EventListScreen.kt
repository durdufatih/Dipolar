package com.dipolar.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dipolar.data.model.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EventListScreen(
    events: List<Event>,
    currentUser: User?,
    selectedInterests: Set<Interest>,
    selectedLanguage: Language?,
    onToggleInterest: (Interest) -> Unit,
    onLanguageFilter: (Language?) -> Unit,
    onClearFilters: () -> Unit,
    onEventClick: (Event) -> Unit,
    onCreateEvent: () -> Unit
) {
    var showLanguageMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Etkinlikler", fontWeight = FontWeight.Bold)
                        currentUser?.let {
                            Text(
                                "Merhaba, ${it.name.split(" ").first()}!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onCreateEvent) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Etkinlik Oluştur")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6C63FF),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateEvent,
                containerColor = Color(0xFF6C63FF),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Etkinlik Oluştur")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F7FF))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.FilterList,
                                null,
                                tint = Color(0xFF6C63FF),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Filtrele",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6C63FF),
                                fontSize = 14.sp
                            )
                        }
                        if (selectedInterests.isNotEmpty() || selectedLanguage != null) {
                            TextButton(onClick = onClearFilters) {
                                Text("Temizle", fontSize = 12.sp, color = Color(0xFFD32F2F))
                            }
                        }
                    }

                    // Interest filter chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(Interest.entries) { interest ->
                            val selected = interest in selectedInterests
                            FilterChip(
                                selected = selected,
                                onClick = { onToggleInterest(interest) },
                                label = {
                                    Text(
                                        "${interest.emoji} ${interest.label}",
                                        fontSize = 12.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF6C63FF),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Language filter
                    ExposedDropdownMenuBox(
                        expanded = showLanguageMenu,
                        onExpandedChange = { showLanguageMenu = it }
                    ) {
                        OutlinedTextField(
                            value = selectedLanguage?.let { "${it.flag} ${it.label}" } ?: "Tüm Diller",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Dil Filtresi", fontSize = 12.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showLanguageMenu) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                        ExposedDropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🌍 Tüm Diller") },
                                onClick = { onLanguageFilter(null); showLanguageMenu = false }
                            )
                            Language.entries.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.flag} ${lang.label}") },
                                    onClick = { onLanguageFilter(lang); showLanguageMenu = false }
                                )
                            }
                        }
                    }
                }
            }

            // Results count
            Text(
                text = "${events.size} etkinlik bulundu",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            // Event list
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        currentUserId = currentUser?.id,
                        onClick = { onEventClick(event) }
                    )
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    currentUserId: String?,
    onClick: () -> Unit
) {
    val isOwner = event.creatorId == currentUserId
    val pendingCount = event.joinRequests.count { it.status == JoinRequestStatus.PENDING }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = event.creatorAvatarUrl,
                        contentDescription = event.creatorName,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            event.creatorName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        if (isOwner) {
                            Text(
                                "Senin etkinliğin",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF6C63FF)
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                "${event.language.flag} ${event.language.label}",
                                fontSize = 11.sp
                            )
                        }
                    )
                    if (isOwner && pendingCount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Badge(containerColor = Color(0xFFFF6B6B)) {
                            Text("$pendingCount istek", color = Color.White)
                        }
                    }
                }
            }

            Text(
                event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (event.description.isNotBlank()) {
                Text(
                    event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(event.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        event.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interest chips
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    event.interests.take(2).forEach { interest ->
                        AssistChip(
                            onClick = {},
                            label = { Text("${interest.emoji} ${interest.label}", fontSize = 11.sp) }
                        )
                    }
                    if (event.interests.size > 2) {
                        AssistChip(
                            onClick = {},
                            label = { Text("+${event.interests.size - 2}", fontSize = 11.sp) }
                        )
                    }
                }

                // Participants
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.People,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = if (event.isFull) Color(0xFFD32F2F) else Color(0xFF388E3C)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${event.currentParticipants}/${event.maxParticipants}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (event.isFull) Color(0xFFD32F2F) else Color(0xFF388E3C)
                    )
                }
            }
        }
    }
}
