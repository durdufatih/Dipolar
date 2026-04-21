package com.dipolar.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dipolar.data.model.*
import com.dipolar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EventDetailScreen(
    event: Event,
    currentUser: User?,
    joinStatus: JoinRequestStatus?,
    onJoinRequest: () -> Unit,
    onRespondToRequest: (String, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val isOwner = event.creatorId == currentUser?.id
    val alreadyAccepted = event.joinRequests.any {
        it.requesterId == currentUser?.id && it.status == JoinRequestStatus.ACCEPTED
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
                    }
                },
                title = { Text("Dipolar", fontWeight = FontWeight.ExtraBold, color = NavyBlue) },
                actions = {
                    AsyncImage(
                        model = event.creatorAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 12.dp).size(36.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBlueBackground)
            )
        },
        bottomBar = {
            if (!isOwner && currentUser != null) {
                Surface(shadowElevation = 8.dp, color = LightBlueBackground) {
                    Box(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        when {
                            alreadyAccepted -> {
                                Button(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                                ) {
                                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Kabul Edildi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                            joinStatus == JoinRequestStatus.PENDING -> {
                                Button(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8F00)),
                                    enabled = false
                                ) {
                                    Text(
                                        if (event.isDateMeeting) "Buluşma İsteği Bekleniyor..." else "İstek Bekleniyor...",
                                        fontWeight = FontWeight.Bold, fontSize = 16.sp
                                    )
                                }
                            }
                            joinStatus == JoinRequestStatus.REJECTED -> {
                                Button(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE05C7A)),
                                    enabled = false
                                ) {
                                    Text("İstek Reddedildi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                            event.isFull -> {
                                Button(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                    enabled = false
                                ) {
                                    Text("Etkinlik Dolu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                            else -> {
                                Button(
                                    onClick = onJoinRequest,
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (event.isDateMeeting) Color(0xFFE05C7A) else NavyBlue
                                    )
                                ) {
                                    if (event.isDateMeeting) {
                                        Text("💜  Buluşmak İstiyorum", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    } else {
                                        Text("Katılma İsteği Gönder", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = LightBlueBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // Cover image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1B3F8B), Color(0xFF4A6FA5))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = event.creatorAvatarUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop,
                    alpha = 0.4f
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.People, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // 1-on-1 badge or limited access badge
                if (event.isDateMeeting) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFE4F0))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "💜 1-ON-1 BULUŞMA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE05C7A),
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else if (event.isFull || event.spotsLeft <= 2) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(TagPink)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TagPinkText))
                            Text(
                                "SINIRLI ERİŞİM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TagPinkText,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    event.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Host
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = event.creatorAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text("Düzenleyen: ", fontSize = 14.sp, color = TextSecondary)
                    Text(event.creatorName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NavyBlue)
                }

                if (event.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        event.description,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 22.sp,
                        maxLines = 3
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Info cards
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    InfoCard(icon = Icons.Default.CalendarMonth, label = "TARİH", value = event.date)
                    InfoCard(icon = Icons.Default.Language, label = "DİL", value = "${event.language.flag} ${event.language.label}")
                    InfoCard(
                        icon = Icons.Default.People,
                        label = "KAPASİTE",
                        value = "${event.spotsLeft} Yer Kaldı",
                        valueColor = if (event.isFull) Color(0xFFE05C7A) else TextPrimary
                    )
                    InfoCard(icon = Icons.Default.LocationOn, label = "KONUM", value = event.location)
                }

                // Interest tags
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    event.interests.forEach { interest ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(CardWhite)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("${interest.emoji} ${interest.label}", fontSize = 13.sp, color = TextPrimary)
                        }
                    }
                }

                // Owner: join requests
                if (isOwner) {
                    val pending = event.joinRequests.filter { it.status == JoinRequestStatus.PENDING }
                    if (pending.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            if (event.isDateMeeting) "Buluşma İstekleri" else "Katılma İstekleri",
                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        pending.forEach { req ->
                            JoinRequestCard(
                                request = req,
                                isDateMeeting = event.isDateMeeting,
                                onAccept = { onRespondToRequest(req.id, true) },
                                onReject = { onRespondToRequest(req.id, false) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    val processed = event.joinRequests.filter { it.status != JoinRequestStatus.PENDING }
                    if (processed.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("İşlenen İstekler", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        processed.forEach { req ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = CardWhite),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = req.requesterAvatarUrl,
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp).clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(req.requesterName, fontWeight = FontWeight.Medium)
                                    }
                                    Icon(
                                        if (req.status == JoinRequestStatus.ACCEPTED) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        null,
                                        tint = if (req.status == JoinRequestStatus.ACCEPTED) Color(0xFF388E3C) else Color(0xFFE05C7A)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun InfoCard(icon: ImageVector, label: String, value: String, valueColor: Color = TextPrimary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardLight),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(icon, null, tint = NavyBlue, modifier = Modifier.size(22.dp))
            Column {
                Text(label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, letterSpacing = 0.8.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
            }
        }
    }
}

@Composable
fun JoinRequestCard(
    request: JoinRequest,
    isDateMeeting: Boolean = false,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                AsyncImage(
                    model = request.requesterAvatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(request.requesterName, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(
                        if (isDateMeeting) "Buluşmak istiyor" else "Katılmak istiyor",
                        fontSize = 12.sp, color = TextSecondary
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onReject,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFFE4E8))
                ) {
                    Icon(Icons.Default.Close, null, tint = Color(0xFFE05C7A), modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = onAccept,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE8F5E9))
                ) {
                    Icon(Icons.Default.Check, null, tint = Color(0xFF388E3C), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
