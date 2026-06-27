package com.example.tradingagent.ui.trades

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tradingagent.ui.components.SummaryMetricTile
import com.example.tradingagent.ui.components.WireframeCard
import com.example.tradingagent.ui.components.WireframeChip

import com.example.tradingagent.data.api.Trade
import androidx.compose.foundation.lazy.items
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradesScreen(
    tradesToday: List<Trade>,
    allTrades: List<Trade>,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    
    val winRate = if (allTrades.count { it.action == "SELL" && it.pnl != null } > 0) {
        val sells = allTrades.filter { it.action == "SELL" && it.pnl != null }
        val winners = sells.count { (it.pnl ?: 0.0) > 0 }
        (winners.toDouble() / sells.size.toDouble()) * 100
    } else 0.0

    val realizedPnl = allTrades.filter { it.action == "SELL" }.sumOf { it.pnl ?: 0.0 }
    val pnlStr = (if (realizedPnl >= 0) "+" else "") + currencyFormatter.format(realizedPnl)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Trades") },
                actions = {
                    IconButton(onClick = { /* refresh */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Filter row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WireframeChip("All", isActive = true)
                WireframeChip("Today")
                WireframeChip("Week")
                WireframeChip("Month")
            }

            // Summary mini strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryMetricTile(label = "Trades", value = "${allTrades.size}", modifier = Modifier.weight(1f))
                SummaryMetricTile(label = "Win Rate", value = "${String.format("%.1f", winRate)}%", modifier = Modifier.weight(1f))
                SummaryMetricTile(label = "Realized P&L", value = pnlStr, modifier = Modifier.weight(1f))
            }

            // Trade history list
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (allTrades.isEmpty()) {
                    item {
                        Text("No trades recorded.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    items(allTrades) { trade ->
                        TradeCard(trade = trade, currencyFormatter = currencyFormatter)
                    }
                }
            }
        }
    }
}

@Composable
fun TradeCard(trade: Trade, currencyFormatter: NumberFormat) {
    WireframeCard {
        // Row 1
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                WireframeChip(trade.action, isActive = trade.action == "BUY")
                Text(trade.symbol, fontWeight = FontWeight.Bold)
            }
            Text("${trade.date} ${trade.time}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            WireframeChip(trade.mode?.capitalize(Locale.ROOT) ?: "Live")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Qty: ${trade.quantity}")
            Text("Avg Exec: ${currencyFormatter.format(trade.price ?: 0.0)}")
            Text("Type: MKT")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row 3
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Notional: ${currencyFormatter.format(trade.notional ?: 0.0)}", style = MaterialTheme.typography.labelSmall)
            if (trade.action == "SELL" && trade.pnl != null) {
                val pnlColor = if (trade.pnl >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                val sign = if (trade.pnl >= 0) "+" else ""
                Text("Realized P&L: $sign${currencyFormatter.format(trade.pnl)}", color = pnlColor, fontWeight = FontWeight.Bold)
            } else {
                 Text("")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row 4
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.ExpandMore, contentDescription = "Expand", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
