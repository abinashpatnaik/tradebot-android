package com.example.tradingagent

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.fragment.app.FragmentActivity
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.tradingagent.data.SettingsManager
import com.example.tradingagent.theme.TradingAgentTheme
import com.example.tradingagent.ui.TradingApp
import com.example.tradingagent.ui.auth.AuthScreen

class MainActivity : FragmentActivity() {

    private val inactivityTimeout = 2 * 60 * 1000L // 2 minutes
    private val handler = Handler(Looper.getMainLooper())
    private var lastInteractionTime = 0L
    private val lockRunnable = Runnable {
        isAuthenticated.value = false
    }

    // Move to class level so it can be updated by Handler
    private var isAuthenticated = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = SettingsManager(applicationContext)

        enableEdgeToEdge()
        setContent {
            // Observe settingsVersion so UI updates when theme changes
            val version by settings.settingsVersion.collectAsState()
            val isDark = settings.darkMode ?: isSystemInDarkTheme()

            TradingAgentTheme(darkTheme = isDark) {
                if (isAuthenticated.value) {
                    TradingApp()
                } else {
                    AuthScreen(onAuthenticated = { 
                        isAuthenticated.value = true 
                        resetInactivityTimer()
                    })
                }
            }
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        if (isAuthenticated.value) {
            resetInactivityTimer()
        }
    }

    private fun resetInactivityTimer() {
        lastInteractionTime = System.currentTimeMillis()
        handler.removeCallbacks(lockRunnable)
        handler.postDelayed(lockRunnable, inactivityTimeout)
    }

    override fun onResume() {
        super.onResume()
        if (isAuthenticated.value) {
            val now = System.currentTimeMillis()
            if (now - lastInteractionTime > inactivityTimeout) {
                isAuthenticated.value = false
            } else {
                resetInactivityTimer()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(lockRunnable)
    }
}
