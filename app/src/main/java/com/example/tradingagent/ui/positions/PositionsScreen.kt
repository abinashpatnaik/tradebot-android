package com.example.tradingagent.ui.positions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tradingagent.ui.components.PlaceholderBox
import com.example.tradingagent.ui.components.SummaryMetricTile
import com.example.tradingagent.ui.components.WireframeButton
import com.example.tradingagent.ui.components.WireframeCard
import com.example.tradingagent.ui.components.WireframeChip

import com.example.tradingagent.data.api.PortfolioResponse
import com.example.tradingagent.data.api.Position
import androidx.compose.foundation.lazy.items
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PositionsScreen(
    positions: List<Position>,
    modifier: Modifier = Modifier,
    onStockClick: (String) -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val percentFormatter = NumberFormat.getPercentInstance(Locale.US).apply {
        maximumFractionDigits = 2
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Positions") },
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
                WireframeChip("Winners")
                WireframeChip("Losers")
            }

            // Position list
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (positions.isEmpty()) {
                    item {
                        Text("No active positions.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    items(positions) { pos ->
                        PositionCard(position = pos, currencyFormatter = currencyFormatter, onStockClick = onStockClick)
                    }
                }
            }
        }
    }
}

@Composable
fun PositionCard(position: Position, currencyFormatter: NumberFormat, onStockClick: (String) -> Unit) {
    val pnl = position.unrealizedPnl ?: position.realizedPnl ?: 0.0
    val pnlStr = (if (pnl >= 0) "+" else "") + currencyFormatter.format(pnl)
    val pnlColor = if (pnl >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    WireframeCard {
        // Row 1
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(position.displaySymbol, fontWeight = FontWeight.Bold)
                if (position.strategy != null && position.strategy != "Unknown") {
                    WireframeChip(position.strategy)
                }
            }
            Text(pnlStr, color = pnlColor, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row 2
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Qty: ${position.displayQuantity}")
            Text("Avg: ${currencyFormatter.format(position.displayAvgCost)}")
            Text("LTP: ${currencyFormatter.format(position.displayCurrentPrice)}")
            if (position.allocation != null) {
                Text("Alloc: ${position.allocation}%")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 3
        PlaceholderBox(modifier = Modifier.fillMaxWidth().height(40.dp), text = "Chart: ${position.displaySymbol}")
        
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (position.stopLoss != null) {
                Text("Stop-loss: ${currencyFormatter.format(position.stopLoss)}", style = MaterialTheme.typography.labelSmall)
            } else {
                Text("Stop-loss: None", style = MaterialTheme.typography.labelSmall)
            }
            if (position.takeProfit != null) {
                Text("Target: ${currencyFormatter.format(position.takeProfit)}", style = MaterialTheme.typography.labelSmall)
            } else {
                Text("Target: None", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 4
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WireframeButton("Add", onClick = {}, modifier = Modifier.weight(1f), isPrimary = false)
            WireframeButton("Reduce", onClick = {}, modifier = Modifier.weight(1f), isPrimary = false)
            WireframeButton("Exit", onClick = {}, modifier = Modifier.weight(1f))
        }
    }
}
