package com.example.tradingagent.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tradingagent.ui.components.SummaryMetricTile
import com.example.tradingagent.ui.components.WireframeButton
import com.example.tradingagent.ui.components.WireframeCard

import com.example.tradingagent.data.api.PortfolioResponse
import com.example.tradingagent.data.api.Position
import com.example.tradingagent.data.api.Signal
import com.example.tradingagent.data.api.Trade
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    portfolio: PortfolioResponse,
    positions: List<Position>,
    signals: List<Signal>,
    tradesToday: List<Trade>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit,
    onNavigateToPositions: () -> Unit,
    onNavigateToSignals: () -> Unit,
    onNavigateToTrades: () -> Unit,
    onStockClick: (String) -> Unit,
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val percentFormatter = NumberFormat.getPercentInstance(Locale.US).apply {
        maximumFractionDigits = 2
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
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

            // Summary Strip
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryMetricTile(label = "Portfolio", value = currencyFormatter.format(portfolio.nav), modifier = Modifier.weight(1f))
                val pnlStr = (if (portfolio.dailyPnl >= 0) "+" else "") + currencyFormatter.format(portfolio.dailyPnl)
                SummaryMetricTile(label = "Today P&L", value = pnlStr, modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryMetricTile(label = "Active Pos", value = "${positions.size}", modifier = Modifier.weight(1f))
                SummaryMetricTile(label = "Signals", value = "${signals.filter { it.signal != "HOLD" }.size} New", modifier = Modifier.weight(1f))
            }

            // Market Pulse
            Text("Market Pulse", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            WireframeCard {
                if (portfolio.marketPulse.isEmpty()) {
                    Text("Market data unavailable", style = MaterialTheme.typography.bodyMedium)
                } else {
                    portfolio.marketPulse.forEach { ticker ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(ticker.symbol)
                            val changePct = ticker.changePercent
                            val color = if ((changePct ?: 0.0) >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            val sign = if ((changePct ?: 0.0) >= 0) "+" else ""
                            val displayPct = if (changePct != null) String.format("%.2f", changePct) else "0.00"
                            Text("$sign$displayPct%", color = color)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Quick Actions
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WireframeButton("View Pos", onClick = onNavigateToPositions, modifier = Modifier.weight(1f), isPrimary = false)
                WireframeButton("Signals", onClick = onNavigateToSignals, modifier = Modifier.weight(1f), isPrimary = false)
                WireframeButton("Trade", onClick = onNavigateToTrades, modifier = Modifier.weight(1f))
            }

            // Highlights
            Text("Highlights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            WireframeCard {
                val topSignal = signals.firstOrNull { it.signal != "HOLD" }
                if (topSignal != null) {
                    Text("Top Signal: ${topSignal.symbol} (${topSignal.signal})", fontWeight = FontWeight.Bold)
                    Text("AI reasoning: ${topSignal.aiReason ?: "Algorithm selected based on trend scores."}")
                } else {
                    Text("Top Signal: None", fontWeight = FontWeight.Bold)
                    Text("No immediate actionable signals.")
                }
                Spacer(modifier = Modifier.height(8.dp))
                WireframeButton(
                    "View Detail",
                    onClick = {
                        if (topSignal != null) {
                            onStockClick(topSignal.symbol)
                        } else {
                            onNavigateToSignals()
                        }
                    },
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Recent Activity
            Text("Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            WireframeCard {
                if (tradesToday.isEmpty()) {
                    Text("No trades executed today.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    tradesToday.take(2).forEachIndexed { index, trade ->
                        val action = if (trade.action == "BUY") "Bought" else "Sold"
                        Text("$action ${trade.quantity} ${trade.symbol} @ ${currencyFormatter.format(trade.price)}", style = MaterialTheme.typography.bodyMedium)
                        Text(trade.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (index < tradesToday.size - 1 && index < 1) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
