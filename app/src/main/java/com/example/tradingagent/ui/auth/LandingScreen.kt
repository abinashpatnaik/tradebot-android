package com.example.tradingagent.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tradingagent.ui.components.PlaceholderBox
import com.example.tradingagent.ui.components.SummaryMetricTile
import com.example.tradingagent.ui.components.WireframeButton
import com.example.tradingagent.ui.components.WireframeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    onOpenDashboard: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Logo Placeholder", style = MaterialTheme.typography.titleMedium) },
                actions = {
                    TextButton(onClick = { /* info */ }) {
                        Text("Info")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero block
            Text("Trading App", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("AI-driven execution and portfolio management.", style = MaterialTheme.typography.bodyMedium)
            
            // Visual placeholder
            PlaceholderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                text = "Abstract Chart Illustration"
            )

            // Capability row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryMetricTile("Feature", "AI Signals", modifier = Modifier.weight(1f))
                SummaryMetricTile("Feature", "Auto Trades", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Connection status card
            WireframeCard {
                Text("Connection Status", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("State:")
                    Text("Connected", color = MaterialTheme.colorScheme.primary)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Last Sync:")
                    Text("Just now")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Env:")
                    Text("Production")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // CTA Area
            WireframeButton(
                text = "Open Dashboard",
                onClick = onOpenDashboard,
                modifier = Modifier.fillMaxWidth()
            )
            WireframeButton(
                text = "Connection Settings",
                onClick = onSettingsClick,
                isPrimary = false,
                modifier = Modifier.fillMaxWidth()
            )

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("v1.0.0", style = MaterialTheme.typography.labelSmall)
                Text("Help / Legal", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
