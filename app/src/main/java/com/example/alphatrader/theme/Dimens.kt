package com.example.alphatrader.theme

import androidx.compose.ui.unit.dp

/**
 * Shared spacing/radius/elevation scale so every screen in the redesign
 * pulls from the same steps instead of ad-hoc dp values.
 */
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
}

object Radius {
    val sm = 10.dp
    val md = 14.dp
    val lg = 20.dp
    val pill = 999.dp
}

object Elevation {
    val card = 0.dp
    val raised = 2.dp
}
