package com.example.tradingagent.ui.signals

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
import com.example.tradingagent.ui.components.WireframeButton
import com.example.tradingagent.ui.components.WireframeCard
import com.example.tradingagent.ui.components.WireframeChip

import com.example.tradingagent.data.api.Signal
import androidx.compose.foundation.lazy.items
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalsScreen(
    signals: List<Signal>,
    modifier: Modifier = Modifier,
    onStockClick: (String) -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val percentFormatter = NumberFormat.getPercentInstance(Locale.US).apply {
        maximumFractionDigits = 2
    }

    var activeFilter by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("All") }

    val filteredSignals = androidx.compose.runtime.remember(signals, activeFilter) {
        when (activeFilter) {
            "Momentum" -> signals.filter { it.macdSignal == "bullish" && it.emaSignal == "bullish" }
            "Reversal" -> signals.filter { (it.rsi ?: 50.0) < 40.0 }
            "Breakout" -> signals.filter { (it.changePct ?: 0.0) > 2.0 }
            "AI Score" -> signals.filter { (it.mlConfidence ?: 0.0) > 0.6 || (it.trendScore ?: 0.0) > 0.0 }
            else -> signals
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Signals") },
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
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { WireframeChip("All", isActive = activeFilter == "All", onClick = { activeFilter = "All" }) }
                item { WireframeChip("Momentum", isActive = activeFilter == "Momentum", onClick = { activeFilter = "Momentum" }) }
                item { WireframeChip("Reversal", isActive = activeFilter == "Reversal", onClick = { activeFilter = "Reversal" }) }
                item { WireframeChip("Breakout", isActive = activeFilter == "Breakout", onClick = { activeFilter = "Breakout" }) }
                item { WireframeChip("AI Score", isActive = activeFilter == "AI Score", onClick = { activeFilter = "AI Score" }) }
            }

            // Signal list
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (filteredSignals.isEmpty()) {
                    item {
                        Text("No signals currently available.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    items(filteredSignals) { signal ->
                        SignalCard(signal = signal, currencyFormatter = currencyFormatter, percentFormatter = percentFormatter, onStockClick = onStockClick)
                    }
                }
            }
        }
    }
}

@Composable
fun SignalCard(signal: Signal, currencyFormatter: NumberFormat, percentFormatter: NumberFormat, onStockClick: (String) -> Unit) {
    WireframeCard {
        // Row 1
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(signal.symbol, fontWeight = FontWeight.Bold)
            Text(currencyFormatter.format(signal.price ?: 0.0))
            val changePct = signal.changePct ?: 0.0
            val color = if (changePct >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            val sign = if (changePct >= 0) "+" else ""
            Text("$sign${String.format("%.2f", changePct)}%", color = color)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Confidence: ${(signal.confidence ?: 0.0).toInt()}%")
            WireframeChip(signal.signal ?: "HOLD", isActive = signal.signal != "HOLD")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 3
        PlaceholderBox(modifier = Modifier.fillMaxWidth().height(16.dp), text = "Trend Score: ${String.format("%.2f", signal.trendScore ?: 0.0)}")
        
        Spacer(modifier = Modifier.height(8.dp))

        // Row 4
        PlaceholderBox(modifier = Modifier.fillMaxWidth().height(16.dp), text = "Downside Risk: ${signal.downsideRisk ?: -3.5}%")

        Spacer(modifier = Modifier.height(12.dp))

        // Row 5
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WireframeButton("View Thesis", onClick = { onStockClick(signal.symbol) }, modifier = Modifier.weight(1f), isPrimary = false)
            WireframeButton("Watchlist", onClick = { onStockClick(signal.symbol) }, modifier = Modifier.weight(1f), isPrimary = false)
            WireframeButton("Simulate", onClick = { onStockClick(signal.symbol) }, modifier = Modifier.weight(1f))
        }
    }
}
