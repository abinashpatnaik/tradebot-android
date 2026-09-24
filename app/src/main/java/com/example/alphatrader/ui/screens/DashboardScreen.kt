package com.example.alphatrader.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alphatrader.theme.BrandGreen
import com.example.alphatrader.theme.Spacing
import com.example.alphatrader.ui.components.*
import com.example.alphatrader.ui.viewmodels.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (state.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
            }
        } else {
            val currencySymbol = if (state.marketRegion == MarketRegion.US) "$" else "₹"

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    TickerTape(
                        tickers = state.tickers,
                        currencySymbol = currencySymbol,
                        onTickerClick = { viewModel.openStockDetails(it) }
                    )
                }

                // Account headline — NAV + cash, the two top-line numbers.
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        state.portfolio?.let { p ->
                            MetricCard(
                                variant = MetricVariant.NAV,
                                label = "NET ASSET VALUE",
                                value = "$currencySymbol${String.format("%.2f", p.nav)}",
                                subLabel = "${if (p.dailyPnl >= 0) "+" else ""}$currencySymbol${String.format("%.2f", p.dailyPnl)} (${String.format("%.2f", p.dailyPnlPct)}%) Today",
                                isPositiveDelta = p.dailyPnl >= 0
                            )

                            MetricCard(
                                variant = MetricVariant.CASH,
                                label = "AVAILABLE CASH",
                                value = "$currencySymbol${String.format("%.2f", p.cash)}",
                                subLabel = "Intraday BP: $currencySymbol${String.format("%.2f", p.buyingPower)}",
                                isPositiveDelta = null
                            )
                        }
                    }
                }

                // Open positions — what the bot is actually doing right now,
                // and whether each one is protected by an armed trailing
                // stop or still just the original hard stop.
                item {
                    PositionsSection(
                        positions = state.positions,
                        currencySymbol = currencySymbol,
                        onPositionClick = { viewModel.openStockDetails(it) }
                    )
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }

                item {
                    PortfolioChartCard(
                        history = state.navHistory,
                        selectedRange = state.selectedNavRange,
                        onRangeSelected = { viewModel.updateNavRange(it) }
                    )
                }

                item {
                    val risk = state.analytics?.risk
                    val winRate = state.portfolio?.winRate ?: 0.0
                    AnalyticsPanel(
                        maxDrawdown = risk?.maxDrawdown?.let { "${String.format("%.2f", it)}%" } ?: "N/A",
                        portfolioVar = risk?.var95?.let { "${String.format("%.2f", it)}%" } ?: "N/A",
                        portfolioBeta = risk?.beta?.let { String.format("%.2f", it) } ?: "N/A",
                        volAnn = risk?.volatility?.let { "${String.format("%.1f", it)}%" } ?: "N/A",
                        winRate = winRate
                    )
                }
            }
        }
    }
}
