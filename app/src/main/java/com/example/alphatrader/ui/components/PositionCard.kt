package com.example.alphatrader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alphatrader.data.network.PositionResponse
import com.example.alphatrader.theme.*
import kotlin.math.abs

/**
 * A single open position, front and center on the Dashboard — this is the
 * "what is my bot actually doing right now" view the redesign adds. Shows
 * live P&L and, just as important, the REAL protective-stop state the
 * executor will act on: an armed trailing stop (locked profit) vs. still
 * only the original hard stop (no profit locked in yet).
 */
@Composable
fun PositionCard(
    position: PositionResponse,
    currencySymbol: String = "$",
    onClick: () -> Unit = {}
) {
    val isUp = position.pnl >= 0
    val pnlColor = if (isUp) BrandGreen else BrandRed

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(Radius.lg))
            .clickable(onClick = onClick)
            .padding(Spacing.lg)
    ) {
        // Header: symbol + strategy tag, live price
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = position.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = Spacing.sm, vertical = 2.dp)
                ) {
                    Text(
                        text = position.strategy.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "$currencySymbol${String.format("%.2f", position.currentPrice)}",
                style = NumericMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        // P&L — the number that actually matters at a glance
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "P&L",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${if (isUp) "+" else "-"}$currencySymbol${String.format("%.2f", abs(position.pnl))}",
                    style = NumericLarge,
                    color = pnlColor
                )
            }
            Text(
                text = "${if (isUp) "+" else ""}${String.format("%.2f", position.pnlPct)}%",
                style = NumericMedium,
                color = pnlColor
            )
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        // Entry / qty / allocation — secondary detail
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PositionDetail(label = "QTY", value = formatQty(position.quantity))
            PositionDetail(label = "ENTRY", value = "$currencySymbol${String.format("%.2f", position.entryPrice)}")
            PositionDetail(label = "ALLOC", value = "${String.format("%.1f", position.allocation)}%")
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        ProtectionBadge(position = position, currencySymbol = currencySymbol)
    }
}

@Composable
private fun PositionDetail(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = NumericSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * The real protective state — mirrors what the executor will actually act
 * on (see order_executor.py::check_exit_conditions). Armed = a trailing
 * stop is live and profit is locked in, shown in blue with the tier
 * progress. Not yet armed = only the original hard stop protects this
 * position, shown in amber with the gain still needed to arm it.
 */
@Composable
private fun ProtectionBadge(position: PositionResponse, currencySymbol: String) {
    val armed = position.trailingActive
    val color = if (armed) ProtectionArmed else ProtectionBase
    val dimColor = if (armed) ProtectionArmedDim else ProtectionBaseDim

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(dimColor)
            .padding(Spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color)
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = if (armed) "TRAILING STOP ARMED" else "HARD STOP ONLY",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            Text(
                text = "$currencySymbol${String.format("%.2f", position.trailingStop)}",
                style = NumericSmall,
                color = color
            )
        }

        val tier = position.trailTier
        val subtitle = if (armed && tier?.index != null && tier.count != null) {
            "Tier ${tier.index}/${tier.count} · gap ${String.format("%.2f", position.trailingPct * 100)}%" +
                if (tier.nextAt != null) " · next tier at +${String.format("%.1f", tier.nextAt * 100)}%" else ""
        } else if (!armed && tier?.activatesAt != null) {
            "Arms at +${String.format("%.2f", tier.activatesAt * 100)}% gain · hard stop $currencySymbol${String.format("%.2f", position.stopLoss)}"
        } else {
            "Hard stop $currencySymbol${String.format("%.2f", position.stopLoss)}"
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatQty(qty: Double): String {
    return if (qty == qty.toLong().toDouble()) qty.toLong().toString()
    else String.format("%.4f", qty)
}

/** Section header + stacked position cards, or a clear empty state when flat. */
@Composable
fun PositionsSection(
    positions: List<PositionResponse>,
    currencySymbol: String = "$",
    onPositionClick: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OPEN POSITIONS",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (positions.isNotEmpty()) {
                Text(
                    text = "${positions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(Spacing.md))

        if (positions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .clip(RoundedCornerShape(Radius.lg))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(Radius.lg))
                    .padding(Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Flat — no open positions",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "The bot is waiting for a setup that clears every gate.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                positions.forEach { pos ->
                    PositionCard(
                        position = pos,
                        currencySymbol = currencySymbol,
                        onClick = { onPositionClick(pos.symbol) }
                    )
                }
            }
        }
    }
}
