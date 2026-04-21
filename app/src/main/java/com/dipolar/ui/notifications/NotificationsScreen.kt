package com.dipolar.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dipolar.data.model.*
import com.dipolar.ui.events.JoinRequestCard
import com.dipolar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    events: List<Event>,
    currentUserId: String?,
    onRespondToRequest: (String, String, Boolean) -> Unit
) {
    val pendingTotal = events.sumOf { e ->
        e.joinRequests.count { it.status == JoinRequestStatus.PENDING }
    }
    val processedRequests = events.flatMap { event ->
        event.joinRequests
            .filter { it.status != JoinRequestStatus.PENDING }
            .map { event to it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Bildirimler", fontWeight = FontWeight.ExtraBold, color = NavyBlue, fontSize = 20.sp)
                        if (pendingTotal > 0) {
                            Badge(containerColor = Color(0xFFE05C7A)) {
                                Text(pendingTotal.toString(), color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBlueBackground)
            )
        },
        containerColor = LightBlueBackground
    ) { padding ->
        if (events.isEmpty() && pendingTotal == 0) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.NotificationsNone, null, modifier = Modifier.size(72.dp), tint = TextSecondary)
                    Text("Henüz bildirim yok", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Etkinliklerinize gelen istekler\nburada görünecek", fontSize = 14.sp, color = TextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pending requests section
                val eventsWithPending = events.filter { e ->
                    e.joinRequests.any { it.status == JoinRequestStatus.PENDING }
                }

                if (eventsWithPending.isNotEmpty()) {
                    item {
                        Text(
                            "BEKLEYENLİR  •  $pendingTotal",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    items(eventsWithPending, key = { it.id }) { event ->
                        val pending = event.joinRequests.filter { it.status == JoinRequestStatus.PENDING }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Event header
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    AsyncImage(
                                        model = event.creatorAvatarUrl,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(event.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                            if (event.isDateMeeting) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFFFFE4F0))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("💜 1-ON-1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE05C7A))
                                                }
                                            }
                                            Text(
                                                "${pending.size} bekleyen istek",
                                                fontSize = 12.sp,
                                                color = Color(0xFFFF8F00),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                    Icon(Icons.Default.NotificationImportant, null, tint = Color(0xFFFF8F00), modifier = Modifier.size(20.dp))
                                }

                                HorizontalDivider(color = Color(0xFFF0F0F0))

                                pending.forEach { request ->
                                    JoinRequestCard(
                                        request = request,
                                        isDateMeeting = event.isDateMeeting,
                                        onAccept = { onRespondToRequest(event.id, request.id, true) },
                                        onReject = { onRespondToRequest(event.id, request.id, false) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Processed requests section
                if (processedRequests.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "İŞLENENLER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    items(processedRequests, key = { (event, req) -> "${event.id}_${req.id}" }) { (event, request) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardLight),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AsyncImage(
                                    model = request.requesterAvatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier.size(38.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(request.requesterName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                                    Text(event.title, fontSize = 12.sp, color = TextSecondary, maxLines = 1)
                                }
                                Icon(
                                    if (request.status == JoinRequestStatus.ACCEPTED) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    null,
                                    tint = if (request.status == JoinRequestStatus.ACCEPTED) Color(0xFF388E3C) else Color(0xFFE05C7A),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
