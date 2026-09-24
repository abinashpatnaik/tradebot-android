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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.alphatrader.theme.*

enum class MetricVariant(val topColor: Color) {
    NAV(BrandGreen),
    CASH(BrandPurple),
    POSITIONS(BrandAmber)
}

@Composable
fun MetricCard(
    variant: MetricVariant,
    label: String,
    value: String,
    subLabel: String,
    isPositiveDelta: Boolean? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surface)
            .drawBehind {
                drawLine(
                    color = variant.topColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx()
                )
            }
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(Radius.lg))
            .padding(Spacing.lg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label.uppercase(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                style = NumericLarge
            )

            val subLabelColor = when (isPositiveDelta) {
                true -> BrandGreen
                false -> BrandRed
                null -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            val icon = when (isPositiveDelta) {
                true -> "▲"
                false -> "▼"
                null -> ""
            }

            Text(
                text = if (icon.isNotEmpty()) "$subLabel $icon" else subLabel,
                color = subLabelColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
