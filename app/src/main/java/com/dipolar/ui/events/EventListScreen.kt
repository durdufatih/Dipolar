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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EventListScreen(
    events: List<Event>,
    currentUser: User?,
    selectedInterests: Set<Interest>,
    selectedLanguage: Language?,
    meetingTypeFilter: Boolean?,
    hasActiveFilters: Boolean,
    onToggleInterest: (Interest) -> Unit,
    onLanguageFilter: (Language?) -> Unit,
    onMeetingTypeFilter: (Boolean?) -> Unit,
    onClearFilters: () -> Unit,
    onEventClick: (Event) -> Unit,
    onCreateEvent: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }

    if (showFilterSheet) {
        FilterBottomSheet(
            selectedInterests = selectedInterests,
            selectedLanguage = selectedLanguage,
            meetingTypeFilter = meetingTypeFilter,
            onToggleInterest = onToggleInterest,
            onLanguageFilter = onLanguageFilter,
            onMeetingTypeFilter = onMeetingTypeFilter,
            onClearFilters = onClearFilters,
            onDismiss = { showFilterSheet = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, null, tint = TextPrimary)
                    }
                },
                title = {
                    Text("Dipolar", fontWeight = FontWeight.ExtraBold, color = NavyBlue, fontSize = 20.sp)
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
            // Search + Filtre butonu
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Etkinlik, dil veya host ara...", color = TextSecondary, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = CardWhite,
                            focusedContainerColor = CardWhite,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = NavyBlue
                        ),
                        singleLine = true
                    )
                    // Filtre butonu
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (hasActiveFilters) NavyBlue else CardWhite)
                            .clickable { showFilterSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        BadgedBox(badge = {
                            if (hasActiveFilters) Badge(containerColor = Color(0xFFFF6B9D)) {}
                        }) {
                            Icon(
                                Icons.Default.Tune,
                                contentDescription = "Filtrele",
                                tint = if (hasActiveFilters) Color.White else NavyBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Aktif filtre özeti (var ise)
            if (hasActiveFilters) {
                item {
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        meetingTypeFilter?.let {
                            item {
                                ActiveFilterChip(
                                    text = if (it) "💜 1-on-1 Buluşma" else "👥 Grup Etkinliği",
                                    onRemove = { onMeetingTypeFilter(null) }
                                )
                            }
                        }
                        selectedLanguage?.let {
                            item {
                                ActiveFilterChip(
                                    text = "${it.flag} ${it.label}",
                                    onRemove = { onLanguageFilter(null) }
                                )
                            }
                        }
                        items(selectedInterests.toList()) { interest ->
                            ActiveFilterChip(
                                text = "${interest.emoji} ${interest.label}",
                                onRemove = { onToggleInterest(interest) }
                            )
                        }
                        item {
                            TextButton(onClick = onClearFilters, contentPadding = PaddingValues(horizontal = 8.dp)) {
                                Text("Tümünü Temizle", color = Color(0xFFE05C7A), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Section title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        when (meetingTypeFilter) {
                            true  -> "1-on-1 Buluşmalar"
                            false -> "Grup Etkinlikleri"
                            null  -> "Tüm Etkinlikler"
                        },
                        fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary
                    )
                    Text("${events.size}", fontSize = 13.sp, color = TextSecondary)
                }
            }

            val filtered = if (searchQuery.isBlank()) events
            else events.filter {
                it.title.contains(searchQuery, true) ||
                        it.creatorName.contains(searchQuery, true) ||
                        it.language.label.contains(searchQuery, true)
            }

            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.SearchOff, null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                            Text("Sonuç bulunamadı", color = TextSecondary, fontSize = 15.sp)
                            TextButton(onClick = onClearFilters) { Text("Filtreleri Temizle", color = NavyBlue) }
                        }
                    }
                }
            } else {
                items(filtered, key = { it.id }) { event ->
                    DiscoverEventCard(event = event, currentUserId = currentUser?.id, onClick = { onEventClick(event) })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ActiveFilterChip(text: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(NavyBlue.copy(alpha = 0.12f))
            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text, fontSize = 12.sp, color = NavyBlue, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(NavyBlue.copy(alpha = 0.2f))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Close, null, tint = NavyBlue, modifier = Modifier.size(10.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    selectedInterests: Set<Interest>,
    selectedLanguage: Language?,
    meetingTypeFilter: Boolean?,
    onToggleInterest: (Interest) -> Unit,
    onLanguageFilter: (Language?) -> Unit,
    onMeetingTypeFilter: (Boolean?) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = LightBlueBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filtrele", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                TextButton(onClick = { onClearFilters(); }) {
                    Text("Temizle", color = Color(0xFFE05C7A), fontWeight = FontWeight.SemiBold)
                }
            }

            // Etkinlik Türü
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("ETKİNLİK TÜRÜ", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, letterSpacing = 1.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Tümü
                    MeetingTypeCard(
                        icon = "🌐",
                        label = "Tümü",
                        sublabel = "Her etkinlik",
                        selected = meetingTypeFilter == null,
                        onClick = { onMeetingTypeFilter(null) },
                        modifier = Modifier.weight(1f)
                    )
                    // Grup
                    MeetingTypeCard(
                        icon = "👥",
                        label = "Grup",
                        sublabel = "3+ kişi",
                        selected = meetingTypeFilter == false,
                        onClick = { onMeetingTypeFilter(false) },
                        modifier = Modifier.weight(1f)
                    )
                    // 1-on-1
                    MeetingTypeCard(
                        icon = "💜",
                        label = "1-on-1",
                        sublabel = "Buluşma",
                        selected = meetingTypeFilter == true,
                        onClick = { onMeetingTypeFilter(true) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Dil
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("DİL", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, letterSpacing = 1.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterPill("🌍 Tümü", selectedLanguage == null) { onLanguageFilter(null) }
                    Language.entries.forEach { lang ->
                        FilterPill("${lang.flag} ${lang.label}", selectedLanguage == lang) { onLanguageFilter(lang) }
                    }
                }
            }

            // İlgi Alanları
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("İLGİ ALANLARI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, letterSpacing = 1.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Interest.entries.forEach { interest ->
                        FilterPill("${interest.emoji} ${interest.label}", interest in selectedInterests) { onToggleInterest(interest) }
                    }
                }
            }

            // Uygula butonu
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text("Uygula", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun MeetingTypeCard(icon: String, label: String, sublabel: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) NavyBlue else CardWhite)
            .border(1.dp, if (selected) NavyBlue else Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(icon, fontSize = 22.sp)
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else TextPrimary)
            Text(sublabel, fontSize = 10.sp, color = if (selected) Color.White.copy(alpha = 0.7f) else TextSecondary)
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
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text, fontSize = 13.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, color = if (selected) Color.White else TextSecondary)
    }
}

@Composable
fun DiscoverEventCard(event: Event, currentUserId: String?, onClick: () -> Unit) {
    val isOwner = event.creatorId == currentUserId
    val pendingCount = event.joinRequests.count { it.status == JoinRequestStatus.PENDING }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                    AsyncImage(
                        model = event.creatorAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (event.isDateMeeting) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color(0xFFFFE4F0)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text("💜 1-ON-1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE05C7A))
                                }
                            } else {
                                val tagText = event.interests.firstOrNull()?.let { "${it.emoji} ${it.label.uppercase()}" } ?: "ETKİNLİK"
                                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(TagPink).padding(horizontal = 8.dp, vertical = 2.dp)) {
                                    Text(tagText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TagPinkText)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(event.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
                if (isOwner && pendingCount > 0) {
                    Badge(containerColor = Color(0xFFE05C7A)) {
                        Text("$pendingCount", color = Color.White, fontSize = 10.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Host: ", fontSize = 13.sp, color = TextSecondary)
                Text(event.creatorName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                Text(event.date, fontSize = 13.sp, color = TextSecondary)
                Text("•", color = TextSecondary, fontSize = 13.sp)
                Icon(Icons.Default.Language, null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                Text("${event.language.flag} ${event.language.label}", fontSize = 13.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(23.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text(
                    when {
                        isOwner -> "Etkinliğimi Gör"
                        event.isDateMeeting -> "Buluşmak İstiyorum"
                        else -> "Katıl"
                    },
                    fontWeight = FontWeight.SemiBold, fontSize = 15.sp
                )
            }
        }
    }
}
