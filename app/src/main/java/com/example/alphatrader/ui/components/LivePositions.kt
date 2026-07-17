package com.example.alphatrader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.alphatrader.data.network.PositionResponse
import com.example.alphatrader.theme.BrandBlue
import com.example.alphatrader.theme.BrandGreen
import com.example.alphatrader.theme.BrandRed

/**
 * Live open positions with the executor's REAL protective levels: the active
 * trailing lock when in profit, otherwise the hard stop. Mirrors the web
 * "Live Positions" panel.
 */
@Composable
fun LivePositions(
    positions: List<PositionResponse>,
    currencySymbol: String = "$"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "LIVE POSITIONS",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )

        if (positions.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No open positions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                positions.forEachIndexed { index, p ->
                    PositionRow(p, currencySymbol)
                    if (index < positions.size - 1) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 1.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PositionRow(p: PositionResponse, currencySymbol: String) {
    val pnlPositive = p.pnl >= 0
    val pnlColor = if (pnlPositive) BrandGreen else BrandRed
    val pnlIcon = if (pnlPositive) "▲" else "▼"

    // Show the level the bot will actually act on: an active trailing lock,
    // otherwise the hard stop. "—" when there's no protective level.
    val protLabel = if (p.trailingActive) "TRAIL" else "STOP"
    val protValue = if (p.trailingActive) p.trailingStop else p.stopLoss
    val protText = if (protValue > 0)
        "$currencySymbol${String.format("%,.2f", protValue)}" else "—"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = p.symbol,
                style = MaterialTheme.typography.titleMedium,
                color = BrandBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "qty ${formatQty(p.quantity)}  @ $currencySymbol${String.format("%,.2f", p.entryPrice)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$currencySymbol${String.format("%,.2f", p.currentPrice)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$pnlIcon ${if (pnlPositive) "+" else "-"}$currencySymbol${String.format("%,.2f", Math.abs(p.pnl))} (${String.format("%.2f", p.pnlPct)}%)",
                style = MaterialTheme.typography.bodySmall,
                color = pnlColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$protLabel $protText",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatQty(qty: Double): String {
    return if (qty == qty.toLong().toDouble()) qty.toLong().toString()
    else String.format("%.4f", qty).trimEnd('0').trimEnd('.')
}
