package com.example.alphatrader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.alphatrader.theme.BrandGreen
import com.example.alphatrader.theme.BrandRed

/**
 * Hero card: the account's Net Asset Value with today's change, plus inline
 * Cash / Day P&L / Win Rate chips — the numbers that matter most, at a glance.
 */
@Composable
fun HeroCard(
    nav: Double,
    dailyPnl: Double,
    dailyPnlPct: Double,
    cash: Double,
    winRate: Double,
    currencySymbol: String = "$"
) {
    val pnlPositive = dailyPnl >= 0
    val pnlColor = if (pnlPositive) BrandGreen else BrandRed

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "NET ASSET VALUE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$currencySymbol${String.format("%,.2f", nav)}",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${if (pnlPositive) "▲ +" else "▼ -"}$currencySymbol${String.format("%,.2f", Math.abs(dailyPnl))} (${String.format("%.2f", dailyPnlPct)}%) today",
            style = MaterialTheme.typography.bodyMedium,
            color = pnlColor
        )

        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatChip(
                modifier = Modifier.weight(1f),
                label = "CASH",
                value = "$currencySymbol${String.format("%,.2f", cash)}",
                valueColor = MaterialTheme.colorScheme.onSurface
            )
            StatChip(
                modifier = Modifier.weight(1f),
                label = "DAY P&L",
                value = "${if (pnlPositive) "+" else "-"}$currencySymbol${String.format("%,.2f", Math.abs(dailyPnl))}",
                valueColor = pnlColor
            )
            StatChip(
                modifier = Modifier.weight(1f),
                label = "WIN RATE",
                value = "${winRate.toInt()}%",
                valueColor = BrandGreen
            )
        }
    }
}

@Composable
private fun StatChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueColor: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor
        )
    }
}
