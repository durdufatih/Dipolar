package com.dipolar.ui.events

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dipolar.data.model.*

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
    val alreadyJoined = event.joinRequests.any {
        it.requesterId == currentUser?.id && it.status == JoinRequestStatus.ACCEPTED
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etkinlik Detayı", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6C63FF),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F7FF))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        event.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AsyncImage(
                            model = event.creatorAvatarUrl,
                            contentDescription = event.creatorName,
                            modifier = Modifier.size(36.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column {
                            Text(event.creatorName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text("Organizatör", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }

                    HorizontalDivider()

                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        InfoItem(Icons.Default.CalendarMonth, "Tarih", event.date)
                        InfoItem(Icons.Default.LocationOn, "Konum", event.location)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        InfoItem(
                            Icons.Default.People,
                            "Katılımcı",
                            "${event.currentParticipants}/${event.maxParticipants}"
                        )
                        InfoItem(Icons.Default.Language, "Dil", "${event.language.flag} ${event.language.label}")
                    }

                    LinearProgressIndicator(
                        progress = { event.currentParticipants.toFloat() / event.maxParticipants },
                        modifier = Modifier.fillMaxWidth(),
                        color = if (event.isFull) Color(0xFFD32F2F) else Color(0xFF6C63FF),
                        trackColor = Color(0xFFE0E0E0)
                    )
                    Text(
                        if (event.isFull) "Etkinlik dolu!" else "${event.spotsLeft} yer kaldı",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (event.isFull) Color(0xFFD32F2F) else Color.Gray
                    )
                }
            }

            // Description
            if (event.description.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Açıklama", fontWeight = FontWeight.Bold, color = Color(0xFF6C63FF))
                        Text(event.description, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF444444))
                    }
                }
            }

            // Interests
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("İlgi Alanları", fontWeight = FontWeight.Bold, color = Color(0xFF6C63FF))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        event.interests.forEach { interest ->
                            AssistChip(
                                onClick = {},
                                label = { Text("${interest.emoji} ${interest.label}") }
                            )
                        }
                    }
                }
            }

            // Owner: join requests section
            if (isOwner) {
                val pendingRequests = event.joinRequests.filter { it.status == JoinRequestStatus.PENDING }
                val processedRequests = event.joinRequests.filter { it.status != JoinRequestStatus.PENDING }

                if (pendingRequests.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Notifications, null, tint = Color(0xFFFF8F00), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "${pendingRequests.size} Katılma İsteği",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF8F00)
                                )
                            }

                            pendingRequests.forEach { request ->
                                JoinRequestCard(
                                    request = request,
                                    onAccept = { onRespondToRequest(request.id, true) },
                                    onReject = { onRespondToRequest(request.id, false) }
                                )
                            }
                        }
                    }
                }

                if (processedRequests.isNotEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("İşlenen İstekler", fontWeight = FontWeight.Bold, color = Color(0xFF6C63FF))
                            processedRequests.forEach { request ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = request.requesterAvatarUrl,
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp).clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(request.requesterName, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Icon(
                                        if (request.status == JoinRequestStatus.ACCEPTED) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        null,
                                        tint = if (request.status == JoinRequestStatus.ACCEPTED) Color(0xFF388E3C) else Color(0xFFD32F2F)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Join button for non-owners
            if (!isOwner && currentUser != null) {
                when {
                    alreadyJoined -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF388E3C))
                                Text("Bu etkinliğe katılımınız kabul edildi!", color = Color(0xFF388E3C), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    joinStatus == JoinRequestStatus.PENDING -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFFFF8F00), strokeWidth = 2.dp)
                                Text("Katılma isteğiniz bekleniyor...", color = Color(0xFFFF8F00), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    joinStatus == JoinRequestStatus.REJECTED -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Cancel, null, tint = Color(0xFFD32F2F))
                                Text("Katılma isteğiniz reddedildi.", color = Color(0xFFD32F2F), fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    event.isFull -> {
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            enabled = false
                        ) {
                            Text("Etkinlik Dolu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    else -> {
                        Button(
                            onClick = onJoinRequest,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
                        ) {
                            Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Katılmak İstiyorum", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = Color(0xFF6C63FF))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun JoinRequestCard(
    request: JoinRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AsyncImage(
                    model = request.requesterAvatarUrl,
                    contentDescription = request.requesterName,
                    modifier = Modifier.size(44.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(request.requesterName, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                    Text("Katılmak istiyor", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onReject,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Icon(Icons.Default.Close, "Reddet", tint = Color(0xFFD32F2F))
                }
                IconButton(
                    onClick = onAccept,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Icon(Icons.Default.Check, "Kabul Et", tint = Color(0xFF388E3C))
                }
            }
        }
    }
}
