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
import com.example.alphatrader.data.network.ProtectiveOrderResponse
import com.example.alphatrader.theme.BrandAmber
import com.example.alphatrader.theme.BrandBlue

/**
 * The executor's active protective orders — the hard stop and trailing gap
 * guarding each position. Mirrors the web "Protective Orders" panel. A hard
 * stop of 0 shows "—".
 */
@Composable
fun ProtectiveOrders(
    orders: List<ProtectiveOrderResponse>,
    currencySymbol: String = "$"
) {
    if (orders.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "PROTECTIVE ORDERS",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            orders.forEachIndexed { index, o ->
                ProtectiveOrderRow(o, currencySymbol)
                if (index < orders.size - 1) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProtectiveOrderRow(o: ProtectiveOrderResponse, currencySymbol: String) {
    val stopText = if (o.stopLoss > 0)
        "$currencySymbol${String.format("%,.2f", o.stopLoss)}" else "—"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = o.symbol,
                style = MaterialTheme.typography.titleMedium,
                color = BrandBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "qty ${formatQtyPo(o.quantity)}  @ $currencySymbol${String.format("%,.2f", o.entryPrice)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "HARD STOP  $stopText",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "TRAIL  ${String.format("%.1f", o.trailingPct * 100)}%",
                style = MaterialTheme.typography.labelSmall,
                color = BrandAmber
            )
        }
    }
}

private fun formatQtyPo(qty: Double): String {
    return if (qty == qty.toLong().toDouble()) qty.toLong().toString()
    else String.format("%.4f", qty).trimEnd('0').trimEnd('.')
}
