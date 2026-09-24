package com.example.alphatrader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.alphatrader.data.SessionManager
import com.example.alphatrader.theme.*

// Same fields, same writes to SessionManager, same onLoginSuccess callback as
// before — only the visuals changed. No auth logic was touched here.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    sessionManager: SessionManager,
    onLoginSuccess: () -> Unit
) {
    var serverUrl by remember { mutableStateOf(sessionManager.serverUrl) }
    var username by remember { mutableStateOf(sessionManager.username) }
    var password by remember { mutableStateOf(sessionManager.password) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = BrandGreen,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedLabelColor = BrandGreen,
        cursorColor = BrandGreen
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.xl),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚡",
                color = BrandGreen,
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "AlphaTrader",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Sign in to your trading dashboard",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.lg))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(Spacing.xl)
            ) {
                OutlinedTextField(
                    value = serverUrl,
                    onValueChange = { serverUrl = it },
                    label = { Text("Server URL") },
                    placeholder = { Text("http://10.0.2.2:3001") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(Radius.sm),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )
                Spacer(modifier = Modifier.height(Spacing.lg))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(Radius.sm),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Spacing.lg))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = fieldColors,
                    shape = RoundedCornerShape(Radius.sm),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(Spacing.xl))

                Button(
                    onClick = {
                        sessionManager.serverUrl = serverUrl
                        sessionManager.username = username
                        sessionManager.password = password
                        onLoginSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = androidx.compose.ui.graphics.Color.Black
                    ),
                    shape = RoundedCornerShape(Radius.sm),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Sign In", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(
                    text = "Credentials are stored on-device and sent only to your server",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
