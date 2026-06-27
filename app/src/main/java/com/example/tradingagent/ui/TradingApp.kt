package com.example.tradingagent.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.tradingagent.ui.home.HomeScreen
import com.example.tradingagent.ui.positions.PositionsScreen
import com.example.tradingagent.ui.signals.SignalsScreen
import com.example.tradingagent.ui.trades.TradesScreen

private data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

// Settings removed from bottom nav — accessible via gear icon in Home top bar
private val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    BottomNavItem("Positions", Icons.AutoMirrored.Filled.TrendingUp, Icons.AutoMirrored.Outlined.TrendingUp),
    BottomNavItem("Signals", Icons.Filled.Insights, Icons.Outlined.Insights),
    BottomNavItem("Trades", Icons.Filled.Receipt, Icons.Outlined.Receipt),
)

@Composable
fun TradingApp(modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showSettings by rememberSaveable { mutableIntStateOf(0) } // 0 = hidden, 1 = showing
    var showLanding by rememberSaveable { mutableIntStateOf(1) } // 1 = showing, 0 = dashboard
    var selectedSymbol by rememberSaveable { androidx.compose.runtime.mutableStateOf<String?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val app = (context.applicationContext as com.example.tradingagent.TradingAgentApp)
    val repository = app.repository
    val settingsManager = app.settingsManager

    if (showLanding == 1) {
        com.example.tradingagent.ui.auth.LandingScreen(
            onOpenDashboard = { showLanding = 0 },
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    if (showSettings == 1) {
        com.example.tradingagent.ui.settings.SettingsScreen(
            settingsManager = settingsManager,
            onSave = { app.reconfigureApi() },
            modifier = Modifier.fillMaxSize(),
            onBack = { showSettings = 0 },
        )
        return
    }

    if (selectedSymbol != null) {
        com.example.tradingagent.ui.details.StockDetailsScreen(
            symbol = selectedSymbol!!,
            onBack = { selectedSymbol = null },
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val scope = androidx.compose.runtime.rememberCoroutineScope()
    
    val portfolio by repository.portfolio.collectAsState()
    val positions by repository.positions.collectAsState()
    val signals by repository.signals.collectAsState()
    val tradesToday by repository.tradesToday.collectAsState()
    val allTrades by repository.allTrades.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.label,
                            )
                        },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_transition",
        ) { tab ->
            when (tab) {
                0 -> HomeScreen(
                    portfolio = portfolio,
                    positions = positions,
                    signals = signals,
                    tradesToday = tradesToday,
                    onRefresh = { scope.launch { repository.refresh() } },
                    modifier = screenModifier,
                    onSettingsClick = { showSettings = 1 },
                )
                1 -> PositionsScreen(
                    positions = positions,
                    modifier = screenModifier,
                    onStockClick = { selectedSymbol = it }
                )
                2 -> SignalsScreen(
                    signals = signals,
                    modifier = screenModifier,
                    onStockClick = { selectedSymbol = it }
                )
                3 -> TradesScreen(
                    tradesToday = tradesToday,
                    allTrades = allTrades,
                    modifier = screenModifier
                )
            }
        }
    }
}
