package com.example.alphatrader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alphatrader.data.network.VettingResponse
import com.example.alphatrader.theme.BrandGreen
import com.example.alphatrader.theme.BrandGreenDim
import com.example.alphatrader.theme.BrandRed
import com.example.alphatrader.theme.BrandRedDim

/**
 * Today's stock vetting: the symbols approved to trade, and the ones blocked
 * (with the backtest/liquidity reason). Mirrors the web "Stock Vetting" panel.
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun VettingPanel(vetting: VettingResponse?) {
    val vetted = vetting?.vetted ?: return
    val approved = vetted.approved ?: emptyList()
    val blocked = vetted.blocked ?: emptyMap()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STOCK VETTING",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            vetted.session_date?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "· $it",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Approved
        Text(
            text = "APPROVED FOR TODAY",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
        )
        if (approved.isEmpty()) {
            Text(
                text = "Waiting for pre-market vetting…",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                approved.forEach { sym -> VetChip(sym, BrandGreen, BrandGreenDim) }
            }
        }

        // Blocked
        if (blocked.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "BLOCKED BY BACKTEST / LIQUIDITY",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                blocked.forEach { (sym, reason) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        VetChip(sym, BrandRed, BrandRedDim)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VetChip(
    text: String,
    textColor: androidx.compose.ui.graphics.Color,
    bgColor: androidx.compose.ui.graphics.Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
