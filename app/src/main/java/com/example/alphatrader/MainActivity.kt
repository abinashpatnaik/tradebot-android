package com.example.alphatrader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alphatrader.data.SessionManager
import com.example.alphatrader.data.network.RetrofitClient
import com.example.alphatrader.theme.AlphaTraderTheme
import com.example.alphatrader.ui.screens.AuthScreen
import com.example.alphatrader.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sessionManager = SessionManager.getInstance(this)
        RetrofitClient.initialize(sessionManager)

        enableEdgeToEdge()
        setContent {
            AlphaTraderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination = if (sessionManager.isLoggedIn) "main" else "auth"

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("auth") {
                            AuthScreen(
                                sessionManager = sessionManager,
                                onLoginSuccess = {
                                    navController.navigate("main") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("main") {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}
