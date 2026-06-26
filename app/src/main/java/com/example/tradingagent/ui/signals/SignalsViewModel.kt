package com.example.tradingagent.ui.signals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tradingagent.TradingAgentApp
import com.example.tradingagent.data.api.Signal as ApiSignal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class SortColumn(val displayName: String) {
    SYMBOL("Symbol"),
    PRICE("Price"),
    RSI("RSI"),
    TREND("Trend"),
    SCORE("Score"),
    ALGO("Algo"),
    ML_CONF("ML Conf")
}

/** UI-friendly signal model. */
data class Signal(
    val symbol: String,
    val price: Double,
    val rsi: Double,
    val changePct: Double,
    val trendScore: Double,
    val combinedScore: Double,
    val signal: String,     // "BUY", "SELL", "HOLD", "GATED"
    val aiDecision: String, // "APPROVED", "REJECTED", "IDLE", "OFF", "GHOST_APPROVED"
    val mlConfidence: Double?,
    val aiReason: String?,
    val holdReason: String?,
)

data class SignalsUiState(
    val signals: List<Signal> = emptyList(),
    val isLoading: Boolean = true,
    val sortColumn: SortColumn = SortColumn.SCORE,
    val isAscending: Boolean = false
)

class SignalsViewModel : ViewModel() {
    private val repository = TradingAgentApp.instance.repository
    private val _sortColumn = MutableStateFlow(SortColumn.SCORE)
    private val _isAscending = MutableStateFlow(false)
    private val _uiState = MutableStateFlow(SignalsUiState())
    val uiState: StateFlow<SignalsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.signals,
                _sortColumn,
                _isAscending
            ) { apiSignals, sortCol, isAsc ->
                val mapped = apiSignals.map { it.toUiSignal() }
                
                val sorted = when (sortCol) {
                    SortColumn.SYMBOL -> mapped.sortedBy { it.symbol }
                    SortColumn.PRICE -> mapped.sortedBy { it.price }
                    SortColumn.RSI -> mapped.sortedBy { it.rsi }
                    SortColumn.TREND -> mapped.sortedBy { it.trendScore }
                    SortColumn.SCORE -> mapped.sortedBy { it.combinedScore }
                    SortColumn.ALGO -> mapped.sortedBy { it.signal }
                    SortColumn.ML_CONF -> mapped.sortedBy { it.mlConfidence ?: 0.0 }
                }.let {
                    if (isAsc) it else it.reversed()
                }

                SignalsUiState(
                    signals = sorted,
                    isLoading = false,
                    sortColumn = sortCol,
                    isAscending = isAsc
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun refresh() {
        viewModelScope.launch { repository.refresh() }
    }

    fun setSort(column: SortColumn) {
        if (_sortColumn.value == column) {
            _isAscending.value = !_isAscending.value
        } else {
            _sortColumn.value = column
            _isAscending.value = false // Default to descending when switching columns
        }
    }

    private fun ApiSignal.toUiSignal(): Signal {
        val combined = combinedScore ?: trendScore ?: 0.0
        val isGated = signal == "HOLD" && !holdReason.isNullOrBlank() && combined >= (buyThreshold ?: 0.48)
        return Signal(
            symbol = symbol,
            price = price ?: 0.0,
            rsi = rsi ?: 0.0,
            changePct = changePct ?: 0.0,
            trendScore = trendScore ?: 0.0,
            combinedScore = combined,
            signal = if (isGated) "GATED" else (signal ?: "HOLD"),
            aiDecision = aiDecision ?: "OFF",
            mlConfidence = mlConfidence,
            aiReason = aiReason,
            holdReason = if (isGated) holdReason else null,
        )
    }
}
