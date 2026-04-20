package com.dipolar.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dipolar.data.model.Interest
import com.dipolar.data.model.Language
import com.dipolar.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateEventScreen(
    onCreateEvent: (String, String, String, String, Int, List<Interest>, Language) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isDateMeeting by remember { mutableStateOf(false) }
    var maxParticipants by remember { mutableStateOf(4) }
    var selectedInterests by remember { mutableStateOf<Set<Interest>>(emptySet()) }
    var selectedLanguage by remember { mutableStateOf(Language.TURKISH) }
    var selectedDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Buluşma seçilince kapasite 2'ye kilitlenir; iptal edilince minimum 3'e çıkar
    LaunchedEffect(isDateMeeting) {
        maxParticipants = if (isDateMeeting) 2 else 3
    }

    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val d = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        selectedDate = "${d.year}-${d.monthValue.toString().padStart(2, '0')}-${d.dayOfMonth.toString().padStart(2, '0')}"
                    }
                    showDatePicker = false
                }) { Text("Tamam", color = NavyBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("İptal", color = TextSecondary) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
                    }
                },
                title = {},
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NavyBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(20.dp))
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "ETKİNLİK OLUŞTUR",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Yeni Bir\nAn Paylaş",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                lineHeight = 38.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Etkinliğinizi topluluğa duyurmak için bilgileri doldurun.",
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Event Title
            FormLabel("Etkinlik Başlığı")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; errorMessage = "" },
                placeholder = { Text("Etkinliğinize açık bir isim verin...", color = TextSecondary, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardWhite,
                    focusedContainerColor = CardWhite,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = NavyBlue
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Description
            FormLabel("Açıklama")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Etkinliğinizi tanıtın...", color = TextSecondary, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                minLines = 3,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = CardWhite,
                    focusedContainerColor = CardWhite,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = NavyBlue
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Date & Location
            FormLabel("Tarih & Konum")
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        placeholder = { Text("GÜN SEÇ  gg/aa/yyyy", color = TextSecondary, fontSize = 13.sp) },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarMonth, null, tint = NavyBlue)
                            }
                        },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        )
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("KONUM", color = TextSecondary, fontSize = 13.sp) },
                        trailingIcon = {
                            Icon(Icons.Default.LocationOn, null, tint = NavyBlue)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Language
            FormLabel("Dil")
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(expanded = showLanguageMenu, onExpandedChange = { showLanguageMenu = it }) {
                OutlinedTextField(
                    value = "${selectedLanguage.flag} ${selectedLanguage.label}",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            if (showLanguageMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            null, tint = TextSecondary
                        )
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardWhite,
                        focusedContainerColor = CardWhite,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = NavyBlue
                    )
                )
                ExposedDropdownMenu(expanded = showLanguageMenu, onDismissRequest = { showLanguageMenu = false }) {
                    Language.entries.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text("${lang.flag} ${lang.label}") },
                            onClick = { selectedLanguage = lang; showLanguageMenu = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Meeting type toggle
            FormLabel("Etkinlik Türü")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Grup etkinliği
                val groupSelected = !isDateMeeting
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (groupSelected) NavyBlue else CardWhite)
                        .border(
                            1.dp,
                            if (groupSelected) NavyBlue else Color(0xFFE0E0E0),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { isDateMeeting = false }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Groups,
                            null,
                            tint = if (groupSelected) Color.White else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Grup Etkinliği",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (groupSelected) Color.White else TextSecondary
                        )
                        Text(
                            "Min. 3 kişi",
                            fontSize = 11.sp,
                            color = if (groupSelected) Color.White.copy(alpha = 0.7f) else TextSecondary.copy(alpha = 0.6f)
                        )
                    }
                }

                // Buluşma (date)
                val dateSelected = isDateMeeting
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (dateSelected) NavyBlue else CardWhite)
                        .border(
                            1.dp,
                            if (dateSelected) NavyBlue else Color(0xFFE0E0E0),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { isDateMeeting = true }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            null,
                            tint = if (dateSelected) Color.White else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Buluşma",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (dateSelected) Color.White else TextSecondary
                        )
                        Text(
                            "2 kişi (1-on-1)",
                            fontSize = 11.sp,
                            color = if (dateSelected) Color.White.copy(alpha = 0.7f) else TextSecondary.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Capacity
            FormLabel("Kapasite")
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDateMeeting) LightBlueBackground else CardWhite
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (!isDateMeeting && maxParticipants > 3) maxParticipants--
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isDateMeeting) Color(0xFFDDE5FF) else LightBlueBackground),
                        enabled = !isDateMeeting && maxParticipants > 3
                    ) {
                        Icon(Icons.Default.Remove, null, tint = if (isDateMeeting) TextSecondary else NavyBlue)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "$maxParticipants",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        if (isDateMeeting) {
                            Text("Kilitli • Buluşma", fontSize = 11.sp, color = NavyBlue)
                        }
                    }

                    IconButton(
                        onClick = {
                            if (!isDateMeeting && maxParticipants < 50) maxParticipants++
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isDateMeeting) Color(0xFFDDE5FF) else LightBlueBackground),
                        enabled = !isDateMeeting && maxParticipants < 50
                    ) {
                        Icon(Icons.Default.Add, null, tint = if (isDateMeeting) TextSecondary else NavyBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Interests
            FormLabel("İlgi Alanları")
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Interest.entries.forEach { interest ->
                    val selected = interest in selectedInterests
                    FilterPill(
                        text = "${interest.emoji} ${interest.label}",
                        selected = selected,
                        onClick = {
                            selectedInterests = if (selected) selectedInterests - interest else selectedInterests + interest
                        }
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMessage, color = Color(0xFFE05C7A), fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    when {
                        title.isBlank() -> errorMessage = "Etkinlik başlığı gerekli"
                        location.isBlank() -> errorMessage = "Konum gerekli"
                        selectedDate.isEmpty() -> errorMessage = "Lütfen bir tarih seçin"
                        selectedInterests.isEmpty() -> errorMessage = "En az bir ilgi alanı seçin"
                        else -> onCreateEvent(title, description, selectedDate, location, maxParticipants, selectedInterests.toList(), selectedLanguage)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text("Etkinliği Yayınla", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Yayınlayarak topluluk kurallarını kabul etmiş olursunuz.",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
}
