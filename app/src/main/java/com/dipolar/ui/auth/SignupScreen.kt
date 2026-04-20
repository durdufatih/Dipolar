package com.dipolar.ui.auth

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dipolar.ui.theme.*

@Composable
fun SignupScreen(
    authState: AuthState,
    onSignup: (String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onResetState: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(LightBlueBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(NavyBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Waves, null, tint = Color.White, modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Dipolar", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Hesap Oluştur", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Topluluğa katılmak için kaydolun.", fontSize = 14.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(28.dp))

            listOf(
                Triple("AD SOYAD", name, Icons.Default.Person),
                Triple("E-POSTA", email, Icons.Default.Email),
                Triple("YAŞ", age, Icons.Default.Cake)
            ).forEachIndexed { index, (label, value, icon) ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, letterSpacing = 1.sp)
                    OutlinedTextField(
                        value = value,
                        onValueChange = { v ->
                            when (index) {
                                0 -> name = v
                                1 -> email = v
                                2 -> age = v
                            }
                            onResetState()
                        },
                        leadingIcon = { Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = when (index) {
                                1 -> KeyboardType.Email
                                2 -> KeyboardType.Number
                                else -> KeyboardType.Text
                            }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = CardLight,
                            focusedContainerColor = CardWhite,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = NavyBlue
                        ),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ŞİFRE", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, letterSpacing = 1.sp)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; onResetState() },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null, tint = TextSecondary, modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardLight,
                        focusedContainerColor = CardWhite,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = NavyBlue
                    ),
                    singleLine = true
                )
            }

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(authState.message, color = Color(0xFFE05C7A), fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { onSignup(name, email, password, age) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Kayıt Ol  →", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Zaten hesabın var mı?  ", color = TextSecondary, fontSize = 14.sp)
                TextButton(onClick = { onNavigateToLogin(); onResetState() }, contentPadding = PaddingValues(0.dp)) {
                    Text("Giriş Yap", color = NavyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
