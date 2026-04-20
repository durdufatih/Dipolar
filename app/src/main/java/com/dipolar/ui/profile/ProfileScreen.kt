package com.dipolar.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.dipolar.data.model.Interest
import com.dipolar.data.model.Language
import com.dipolar.data.model.User
import com.dipolar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    user: User,
    onSave: (String, List<Interest>, List<Language>) -> Unit,
    onLogout: () -> Unit
) {
    var bio by remember(user.id) { mutableStateOf(user.bio) }
    var selectedInterests by remember(user.id) { mutableStateOf(user.interests.toSet()) }
    var selectedLanguages by remember(user.id) { mutableStateOf(user.languages.toSet()) }
    var showSaved by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Çıkış Yap", fontWeight = FontWeight.Bold) },
            text = { Text("Hesabınızdan çıkmak istediğinize emin misiniz?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE05C7A)),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Çıkış Yap") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }, shape = RoundedCornerShape(12.dp)) { Text("İptal") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profilim", fontWeight = FontWeight.ExtraBold, color = NavyBlue) },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.Logout, null, tint = Color(0xFFE05C7A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBlueBackground)
            )
        },
        containerColor = LightBlueBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Avatar section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = user.name,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(3.dp, NavyBlue, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(user.name, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                    Text(user.email, fontSize = 14.sp, color = TextSecondary)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(LightBlueBackground)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("${user.age} yaşında", fontSize = 13.sp, color = NavyBlue, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Bio
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Hakkımda", fontWeight = FontWeight.Bold, color = TextPrimary)
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it; showSaved = false },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Kendinizi tanıtın...", color = TextSecondary) },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = CardLight,
                            focusedContainerColor = CardLight,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = NavyBlue
                        )
                    )
                }
            }

            // Interests
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("İlgi Alanlarım", fontWeight = FontWeight.Bold, color = TextPrimary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Interest.entries.forEach { interest ->
                            val selected = interest in selectedInterests
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    selectedInterests = if (selected) selectedInterests - interest else selectedInterests + interest
                                    showSaved = false
                                },
                                label = { Text("${interest.emoji} ${interest.label}", fontSize = 13.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Languages
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Konuştuğum Diller", fontWeight = FontWeight.Bold, color = TextPrimary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Language.entries.forEach { lang ->
                            val selected = lang in selectedLanguages
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    selectedLanguages = if (selected) selectedLanguages - lang else selectedLanguages + lang
                                    showSaved = false
                                },
                                label = { Text("${lang.flag} ${lang.label}", fontSize = 13.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            if (showSaved) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF388E3C))
                    Text("Profil kaydedildi!", color = Color(0xFF388E3C), fontWeight = FontWeight.Medium)
                }
            }

            Button(
                onClick = {
                    onSave(bio, selectedInterests.toList(), selectedLanguages.toList())
                    showSaved = true
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text("Profili Kaydet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
