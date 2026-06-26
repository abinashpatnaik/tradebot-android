package com.example.tradingagent.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tradingagent.TradingAgentApp
import com.example.tradingagent.data.api.StockDetailsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StockDetailsUiState(
    val isLoading: Boolean = true,
    val details: StockDetailsResponse? = null,
    val errorMessage: String? = null
)

class StockDetailsViewModel : ViewModel() {
    private val apiService = TradingAgentApp.instance.apiService

    private val _uiState = MutableStateFlow(StockDetailsUiState())
    val uiState: StateFlow<StockDetailsUiState> = _uiState.asStateFlow()

    fun loadDetails(symbol: String) {
        _uiState.value = StockDetailsUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val details = apiService.getStockDetails(symbol)
                if (details != null) {
                    _uiState.value = StockDetailsUiState(
                        isLoading = false,
                        details = details
                    )
                } else {
                    _uiState.value = StockDetailsUiState(
                        isLoading = false,
                        errorMessage = "Failed to load stock details."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = StockDetailsUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "An unknown error occurred."
                )
            }
        }
    }
}
