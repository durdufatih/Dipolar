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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dipolar.ui.theme.*

@Composable
fun LoginScreen(
    authState: AuthState,
    onLogin: (String, String) -> Unit,
    onNavigateToSignup: () -> Unit,
    onResetState: () -> Unit
) {
    var email by remember { mutableStateOf("ayse@example.com") }
    var password by remember { mutableStateOf("1234") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlueBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            // App icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(NavyBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Waves,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Dipolar",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                "İlgi alanlarınla bağlan.",
                fontSize = 15.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Sign in section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Giriş Yap", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    "Hesabınıza erişmek için bilgilerinizi girin.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Email field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "E-POSTA ADRESİ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; onResetState() },
                    placeholder = { Text("ad@ornek.com", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "ŞİFRE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Unuttum?",
                        fontSize = 13.sp,
                        color = NavyBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
                onClick = { onLogin(email, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Devam Et  →", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Hesabın yok mu?  ", color = TextSecondary, fontSize = 14.sp)
                TextButton(onClick = { onNavigateToSignup(); onResetState() }, contentPadding = PaddingValues(0.dp)) {
                    Text("Hesap Oluştur", color = NavyBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (it == 0) NavyBlue else TextSecondary.copy(alpha = 0.3f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "YÜKSEK DENEYİM  •  GÜVENLİ BAĞLANTI",
                fontSize = 10.sp,
                color = TextSecondary.copy(alpha = 0.5f),
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
