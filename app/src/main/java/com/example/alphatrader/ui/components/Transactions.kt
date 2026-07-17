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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.alphatrader.data.network.TradeResponse
import com.example.alphatrader.theme.BrandBlue
import com.example.alphatrader.theme.BrandGreen
import com.example.alphatrader.theme.BrandRed

/**
 * Recent transactions, mirroring the web panel. A SELL with an unknown P&L
 * (null) shows "—" rather than a misleading "+0.00", and each SELL shows its
 * exit-reason badge (STOP LOSS, TRAILING STOP, EOD…).
 */
@Composable
fun Transactions(
    trades: List<TradeResponse>,
    currencySymbol: String = "$"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "TRANSACTIONS",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )

        if (trades.isEmpty()) {
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
                    text = "No recent transactions",
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
                val shown = trades.take(30)
                shown.forEachIndexed { index, t ->
                    TransactionRow(t, currencySymbol)
                    if (index < shown.size - 1) {
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
private fun TransactionRow(t: TradeResponse, currencySymbol: String) {
    val isBuy = t.action.equals("BUY", ignoreCase = true)
    val action = if (isBuy) SignalAction.BUY else SignalAction.SELL

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = t.time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(60.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = t.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    color = BrandBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                SignalBadge(action = action, text = t.action)
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            if (isBuy) {
                Text(
                    text = "Entry $currencySymbol${String.format("%,.2f", t.price)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                t.quantity?.let {
                    Text(
                        text = "qty ${formatQty(it)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val pnl = t.pnl?.toDoubleOrNull()
                if (pnl == null) {
                    // Unknown P&L (no confirmed fill) — honest "—", not "+0.00".
                    Text(
                        text = "— @ $currencySymbol${String.format("%,.2f", t.price)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val positive = pnl >= 0
                    Text(
                        text = "${if (positive) "▲ +" else "▼ -"}$currencySymbol${String.format("%,.2f", Math.abs(pnl))}  @ $currencySymbol${String.format("%,.2f", t.price)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (positive) BrandGreen else BrandRed
                    )
                }
                t.exit_reason?.takeIf { it.isNotBlank() }?.let { reason ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reason.replace('_', ' '),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

private fun formatQty(qty: Double): String {
    return if (qty == qty.toLong().toDouble()) qty.toLong().toString()
    else String.format("%.4f", qty).trimEnd('0').trimEnd('.')
}
