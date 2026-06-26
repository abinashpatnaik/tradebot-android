package com.example.tradingagent.ui.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tradingagent.data.api.ChartDataPoint
import com.example.tradingagent.theme.LossRed
import com.example.tradingagent.theme.ProfitGreen
import com.example.tradingagent.ui.components.TradeRow
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailsScreen(
    symbol: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StockDetailsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(symbol) {
        viewModel.loadDetails(symbol)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(symbol) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(visible = state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (state.errorMessage != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.errorMessage ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadDetails(symbol) }) {
                        Text("Retry")
                    }
                }
            } else if (state.details != null) {
                val details = state.details!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Chart Section
                    item {
                        Text(
                            text = "6-Month Trend",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (details.chartData.isNotEmpty()) {
                            StockLineChart(
                                data = details.chartData,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(16.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No chart data available",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Summary Section
                    item {
                        details.summary?.let { summary ->
                            Spacer(modifier = Modifier.height(8.dp))
                            val closedTrades = details.trades.filter { it.pnl != null }
                            val winRate = if (closedTrades.isEmpty()) 0.0 else (closedTrades.count { it.pnl?.toDoubleOrNull() ?: 0.0 > 0 } / closedTrades.size.toDouble()) * 100
                            StockSummaryCard(summary, details.trades.size, winRate)
                        }
                    }

                    // Transactions Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Transaction History",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (details.trades.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No recorded transactions",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(details.trades, key = { it.time + it.action + (it.price ?: "") }) { apiTrade ->
                            val uiTrade = com.example.tradingagent.ui.trades.Trade(
                                id = apiTrade.date + apiTrade.time + apiTrade.symbol,
                                date = apiTrade.date,
                                time = apiTrade.time,
                                action = apiTrade.action,
                                symbol = apiTrade.symbol,
                                quantity = apiTrade.quantity?.toIntOrNull() ?: 0,
                                price = apiTrade.price?.toDoubleOrNull() ?: 0.0,
                                pnl = apiTrade.pnl?.toDoubleOrNull(),
                                mode = (apiTrade.mode ?: "paper").uppercase()
                            )
                            TradeRow(trade = uiTrade)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StockLineChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    
    // Determine color based on trend (first vs last)
    val validData = data.filter { it.price != null }
    val startPrice = validData.firstOrNull()?.price ?: 0.0
    val endPrice = validData.lastOrNull()?.price ?: 0.0   
    val lineColor = if (endPrice >= startPrice) ProfitGreen else LossRed

    Canvas(modifier = modifier) {
        val validPrices = data.mapNotNull { it.price }
        if (validPrices.isEmpty()) return@Canvas
        val maxPrice = validPrices.maxOrNull() ?: 0.0
        val minPrice = validPrices.minOrNull() ?: 0.0
        val priceRange = maxPrice - minPrice
        
        // Prevent division by zero if all prices are the same
        val range = if (priceRange == 0.0) 1.0 else priceRange

        val width = size.width
        val height = size.height

        val pointWidth = width / (data.size - 1).coerceAtLeast(1)

        val path = Path()

        data.forEachIndexed { index, point ->
            val price = point.price ?: return@forEachIndexed
            val normalizedY = ((price - minPrice) / range).toFloat()
            // Invert Y axis since 0 is top in Canvas
            val y = height - (normalizedY * height)
            val x = index * pointWidth

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun StockSummaryCard(summary: com.example.tradingagent.data.api.StockSummary, tradesCount: Int, winRate: Double) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }
    
    val pnlColor = if (summary.totalPnl >= 0) ProfitGreen else LossRed
    val pnlPrefix = if (summary.totalPnl >= 0) "+" else ""

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Performance Summary",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(label = "Total Trades", value = tradesCount.toString())
                SummaryItem(label = "Win Rate", value = "${String.format(Locale.US, "%.0f", winRate)}%")
                SummaryItem(
                    label = "Total P&L", 
                    value = "$pnlPrefix${currencyFormat.format(summary.totalPnl)}",
                    valueColor = pnlColor
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
