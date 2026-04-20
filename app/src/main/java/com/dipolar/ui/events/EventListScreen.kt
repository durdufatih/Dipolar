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
    dateMeetings: List<Event>,
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
    var showDateMeetingsSheet by remember { mutableStateOf(false) }

    if (showDateMeetingsSheet) {
        DateMeetingsBottomSheet(
            meetings = dateMeetings,
            currentUser = currentUser,
            onEventClick = { onEventClick(it); showDateMeetingsSheet = false },
            onDismiss = { showDateMeetingsSheet = false }
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
            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Etkinlik, dil veya host ara...", color = TextSecondary, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
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

            // 1-on-1 Buluşmalar banner
            item {
                DateMeetingsBanner(
                    count = dateMeetings.size,
                    onClick = { showDateMeetingsSheet = true }
                )
            }

            // Language filter
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text("DİL", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterPill("Tümü", selectedLanguage == null) { onLanguageFilter(null) }
                        }
                        items(Language.entries) { lang ->
                            FilterPill("${lang.flag} ${lang.label}", selectedLanguage == lang) { onLanguageFilter(lang) }
                        }
                    }
                }
            }

            // Interest filter
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("İLGİ ALANLARI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Interest.entries) { interest ->
                            FilterPill("${interest.emoji} ${interest.label}", interest in selectedInterests) { onToggleInterest(interest) }
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
                    Text("Yaklaşan Etkinlikler", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                    Text("${events.size}", fontSize = 13.sp, color = TextSecondary)
                }
            }

            val filtered = if (searchQuery.isBlank()) events
            else events.filter {
                it.title.contains(searchQuery, true) ||
                        it.creatorName.contains(searchQuery, true) ||
                        it.language.label.contains(searchQuery, true)
            }

            items(filtered, key = { it.id }) { event ->
                DiscoverEventCard(event = event, currentUserId = currentUser?.id, onClick = { onEventClick(event) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun DateMeetingsBanner(count: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(NavyBlue)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Favorite, null, tint = Color(0xFFFF6B9D), modifier = Modifier.size(16.dp))
                    Text(
                        "1-on-1 Buluşmalar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
                Text(
                    "$count aktif buluşma fırsatı",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Keşfet", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Icon(Icons.Default.ArrowForward, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateMeetingsBottomSheet(
    meetings: List<Event>,
    currentUser: User?,
    onEventClick: (Event) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = LightBlueBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Favorite, null, tint = Color(0xFFFF6B9D), modifier = Modifier.size(20.dp))
                        Text("1-on-1 Buluşmalar", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                    }
                    Text("${meetings.size} buluşma seni bekliyor", fontSize = 13.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (meetings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.FavoriteBorder, null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Text("Şu an buluşma yok", color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 600.dp)
                ) {
                    items(meetings, key = { it.id }) { meeting ->
                        DateMeetingCard(
                            event = meeting,
                            currentUserId = currentUser?.id,
                            onClick = { onEventClick(meeting) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
fun DateMeetingCard(event: Event, currentUserId: String?, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AsyncImage(
                        model = event.creatorAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TagPink)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("1-ON-1 BULUŞMA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TagPinkText)
                        }
                        Text(event.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Host: ", fontSize = 13.sp, color = TextSecondary)
                Text(event.creatorName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }

            if (event.description.isNotBlank()) {
                Text(event.description, fontSize = 13.sp, color = TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(14.dp), tint = NavyBlue)
                    Text(event.date, fontSize = 12.sp, color = TextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = NavyBlue)
                    Text(event.location, fontSize = 12.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                event.interests.take(3).forEach { interest ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(LightBlueBackground)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("${interest.emoji} ${interest.label}", fontSize = 11.sp, color = NavyBlue, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(23.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Icon(Icons.Default.Favorite, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (event.creatorId == currentUserId) "Etkinliğimi Gör" else "Buluşmak İstiyorum",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
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
fun DiscoverEventCard(event: Event, currentUserId: String?, onClick: () -> Unit) {
    val isOwner = event.creatorId == currentUserId
    val pendingCount = event.joinRequests.count { it.status == JoinRequestStatus.PENDING }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(onClick = onClick),
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AsyncImage(
                        model = event.creatorAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        val tagText = event.interests.firstOrNull()?.let { "${it.emoji} ${it.label.uppercase()}" } ?: "ETKİNLİK"
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(TagPink).padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(tagText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TagPinkText)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(event.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
                if (isOwner && pendingCount > 0) {
                    Badge(containerColor = Color(0xFFE05C7A)) {
                        Text("$pendingCount", color = Color.White, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
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
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(23.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text(if (isOwner) "Etkinliğimi Gör" else "Katıl", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}
