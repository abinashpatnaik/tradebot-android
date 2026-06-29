package com.example.tradingagent.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tradingagent.ui.components.WireframeButton
import com.example.tradingagent.ui.components.WireframeCard
import com.example.tradingagent.ui.components.WireframeChip

import com.example.tradingagent.data.SettingsManager
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    var serverUrl by remember { mutableStateOf(settingsManager.serverUrl) }
    var username by remember { mutableStateOf(settingsManager.username ?: "") }
    var password by remember { mutableStateOf(settingsManager.password ?: "") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Account Group
            Text("Account & Security", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            WireframeCard {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text("Sign Out", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
            }

            // Advanced Connection Group
            Text("Broker Connection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            WireframeCard {
                OutlinedTextField(
                    value = serverUrl,
                    onValueChange = { serverUrl = it },
                    label = { Text("Server URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Status")
                    WireframeChip("Connected", isActive = true)
                }
                Spacer(modifier = Modifier.height(16.dp))
                WireframeButton("Save & Reconnect", onClick = {
                    settingsManager.serverUrl = serverUrl
                    settingsManager.username = username
                    settingsManager.password = password
                    onSave()
                }, modifier = Modifier.fillMaxWidth(), isPrimary = true)
            }

            // Notifications Group
            Text("Notifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            WireframeCard {
                SettingsToggleRow("Trade Executed", true)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsToggleRow("Signal Detected", true)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsToggleRow("Risk Alert", true)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsToggleRow("Market Open/Close", false)
            }

            // Appearance Group
            Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            WireframeCard {
                var currentTheme by remember { mutableStateOf(settingsManager.darkMode) }
                Text("Theme", modifier = Modifier.padding(bottom = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WireframeButton(
                        "Light", 
                        onClick = { currentTheme = false; settingsManager.darkMode = false }, 
                        modifier = Modifier.weight(1f), 
                        isPrimary = currentTheme == false
                    )
                    WireframeButton(
                        "Gray", 
                        onClick = { currentTheme = true; settingsManager.darkMode = true }, 
                        modifier = Modifier.weight(1f), 
                        isPrimary = currentTheme == true
                    )
                    WireframeButton(
                        "System", 
                        onClick = { currentTheme = null; settingsManager.darkMode = null }, 
                        modifier = Modifier.weight(1f), 
                        isPrimary = currentTheme == null
                    )
                }
            }

            // About Group
            Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            WireframeCard {
                SettingsRow("App Version", "1.0.0-wireframe")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsRow("Environment", "Production")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Support & Help", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SettingsToggleRow(label: String, isChecked: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = isChecked, onCheckedChange = { /* toggle */ })
    }
}
