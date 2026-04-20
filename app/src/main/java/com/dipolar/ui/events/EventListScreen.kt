package com.dipolar.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.dipolar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
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
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, null, tint = TextPrimary)
                    }
                },
                title = {
                    Text(
                        "Dipolar",
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyBlue,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    currentUser?.let {
                        AsyncImage(
                            model = it.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(2.dp, NavyBlue.copy(alpha = 0.2f), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBlueBackground)
            )
        },
        containerColor = LightBlueBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Etkinlik, dil veya host ara...", color = TextSecondary, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardWhite,
                        focusedContainerColor = CardWhite,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = NavyBlue
                    ),
                    singleLine = true
                )
            }

            // Language filter
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(
                        "DİL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterPill(
                                text = "Tümü",
                                selected = selectedLanguage == null,
                                onClick = { onLanguageFilter(null) }
                            )
                        }
                        items(Language.entries) { lang ->
                            FilterPill(
                                text = "${lang.flag} ${lang.label}",
                                selected = selectedLanguage == lang,
                                onClick = { onLanguageFilter(lang) }
                            )
                        }
                    }
                }
            }

            // Interest filter
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        "İLGİ ALANLARI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Interest.entries) { interest ->
                            FilterPill(
                                text = "${interest.emoji} ${interest.label}",
                                selected = interest in selectedInterests,
                                onClick = { onToggleInterest(interest) }
                            )
                        }
                    }
                }
            }

            // Section title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Yaklaşan Etkinlikler",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text("${events.size}", fontSize = 13.sp, color = TextSecondary)
                }
            }

            // Event cards
            val filtered = if (searchQuery.isBlank()) events
            else events.filter {
                it.title.contains(searchQuery, true) ||
                        it.creatorName.contains(searchQuery, true) ||
                        it.language.label.contains(searchQuery, true)
            }

            items(filtered, key = { it.id }) { event ->
                DiscoverEventCard(
                    event = event,
                    currentUserId = currentUser?.id,
                    onClick = { onEventClick(event) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun FilterPill(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) NavyBlue else CardWhite)
            .border(1.dp, if (selected) NavyBlue else Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) Color.White else TextSecondary
        )
    }
}

@Composable
fun DiscoverEventCard(
    event: Event,
    currentUserId: String?,
    onClick: () -> Unit
) {
    val isOwner = event.creatorId == currentUserId
    val pendingCount = event.joinRequests.count { it.status == JoinRequestStatus.PENDING }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = event.creatorAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        // Interest tag
                        val tagText = event.interests.firstOrNull()?.let {
                            "${it.emoji} ${it.label.uppercase()}"
                        } ?: "ETKİNLİK"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TagPink)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(tagText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TagPinkText)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            event.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (isOwner && pendingCount > 0) {
                    Badge(containerColor = Color(0xFFE05C7A)) {
                        Text("$pendingCount", color = Color.White, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Host: ", fontSize = 13.sp, color = TextSecondary)
                Text(event.creatorName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                Text(event.date, fontSize = 13.sp, color = TextSecondary)
                Text("•", color = TextSecondary, fontSize = 13.sp)
                Icon(Icons.Default.Language, null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                Text("${event.language.flag} ${event.language.label}", fontSize = 13.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(23.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text(
                    if (isOwner) "Etkinliğimi Gör" else "Katıl",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
