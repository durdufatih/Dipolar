package com.dipolar.ui.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dipolar.data.model.Interest
import com.dipolar.data.model.Language

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateEventScreen(
    onCreateEvent: (String, String, String, String, Int, List<Interest>, Language) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var maxParticipants by remember { mutableStateOf("4") }
    var selectedInterests by remember { mutableStateOf<Set<Interest>>(emptySet()) }
    var selectedLanguage by remember { mutableStateOf(Language.TURKISH) }
    var selectedDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showLanguageMenu by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val d = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        selectedDate = "${d.year}-${d.monthValue.toString().padStart(2,'0')}-${d.dayOfMonth.toString().padStart(2,'0')}"
                    }
                    showDatePicker = false
                }) { Text("Tamam") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("İptal") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etkinlik Oluştur", fontWeight = FontWeight.Bold) },
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Etkinlik Bilgileri", fontWeight = FontWeight.Bold, color = Color(0xFF6C63FF))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it; errorMessage = "" },
                        label = { Text("Etkinlik Başlığı") },
                        leadingIcon = { Icon(Icons.Default.Title, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Açıklama") },
                        leadingIcon = { Icon(Icons.Default.Description, null) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Konum") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Tarih & Katılımcı", fontWeight = FontWeight.Bold, color = Color(0xFF6C63FF))

                    OutlinedTextField(
                        value = if (selectedDate.isEmpty()) "" else selectedDate,
                        onValueChange = {},
                        label = { Text("Tarih Seçin") },
                        leadingIcon = { Icon(Icons.Default.DateRange, null) },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarMonth, null)
                            }
                        },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Tarih seçmek için dokunun") }
                    )

                    OutlinedTextField(
                        value = maxParticipants,
                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 2) maxParticipants = it },
                        label = { Text("Maksimum Katılımcı Sayısı") },
                        leadingIcon = { Icon(Icons.Default.Group, null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Konuşma Dili", fontWeight = FontWeight.Bold, color = Color(0xFF6C63FF))

                    ExposedDropdownMenuBox(
                        expanded = showLanguageMenu,
                        onExpandedChange = { showLanguageMenu = it }
                    ) {
                        OutlinedTextField(
                            value = "${selectedLanguage.flag} ${selectedLanguage.label}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Dil") },
                            leadingIcon = { Icon(Icons.Default.Language, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showLanguageMenu) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            Language.entries.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text("${lang.flag} ${lang.label}") },
                                    onClick = { selectedLanguage = lang; showLanguageMenu = false }
                                )
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "İlgi Alanları (${selectedInterests.size} seçildi)",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C63FF)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Interest.entries.forEach { interest ->
                            val selected = interest in selectedInterests
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    selectedInterests = if (selected)
                                        selectedInterests - interest
                                    else
                                        selectedInterests + interest
                                },
                                label = { Text("${interest.emoji} ${interest.label}") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF6C63FF),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            if (errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Button(
                onClick = {
                    when {
                        title.isBlank() -> errorMessage = "Etkinlik başlığı gerekli"
                        location.isBlank() -> errorMessage = "Konum gerekli"
                        selectedDate.isEmpty() -> errorMessage = "Lütfen bir tarih seçin"
                        selectedInterests.isEmpty() -> errorMessage = "En az bir ilgi alanı seçin"
                        else -> {
                            onCreateEvent(
                                title, description, selectedDate, location,
                                maxParticipants.toIntOrNull() ?: 4,
                                selectedInterests.toList(), selectedLanguage
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Etkinlik Oluştur", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
