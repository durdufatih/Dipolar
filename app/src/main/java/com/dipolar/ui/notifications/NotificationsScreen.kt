package com.dipolar.ui.notifications

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
import coil.compose.AsyncImage
import com.dipolar.data.model.*
import com.dipolar.ui.events.JoinRequestCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    events: List<Event>,
    currentUserId: String?,
    onRespondToRequest: (String, String, Boolean) -> Unit
) {
    val eventsWithPending = events.filter { event ->
        event.creatorId == currentUserId &&
                event.joinRequests.any { it.status == JoinRequestStatus.PENDING }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Bildirimler", fontWeight = FontWeight.Bold)
                        if (eventsWithPending.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(containerColor = Color(0xFFFF6B6B)) {
                                Text(
                                    eventsWithPending.sumOf { e ->
                                        e.joinRequests.count { it.status == JoinRequestStatus.PENDING }
                                    }.toString(),
                                    color = Color.White
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6C63FF),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (eventsWithPending.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.NotificationsNone,
                        null,
                        modifier = Modifier.size(80.dp),
                        tint = Color(0xFFBDBDBD)
                    )
                    Text(
                        "Bekleyen istek yok",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                    Text(
                        "Etkinliklerinize katılma istekleri burada görünecek",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(eventsWithPending) { event ->
                    val pendingRequests = event.joinRequests.filter { it.status == JoinRequestStatus.PENDING }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Event,
                                    null,
                                    tint = Color(0xFF6C63FF),
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(event.title, fontWeight = FontWeight.Bold)
                                    Text(
                                        "${pendingRequests.size} bekleyen istek",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFFF8F00)
                                    )
                                }
                            }

                            pendingRequests.forEach { request ->
                                JoinRequestCard(
                                    request = request,
                                    onAccept = { onRespondToRequest(event.id, request.id, true) },
                                    onReject = { onRespondToRequest(event.id, request.id, false) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
