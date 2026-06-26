package com.example.tradingagent.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.tradingagent.TradingAgentApp
import com.example.tradingagent.data.SettingsManager

enum class AuthState {
    CHECKING,
    SETUP_PIN,
    CONFIRM_PIN,
    BIO_PROMPT,
    PIN_ENTRY
}

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val settings = remember { TradingAgentApp.instance.settingsManager }
    
    var state by remember { mutableStateOf(AuthState.CHECKING) }
    var setupPin by remember { mutableStateOf("") }
    var currentPinInput by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val handleBiometric = {
        if (activity != null) {
            val biometricHelper = BiometricHelper(activity)
            if (biometricHelper.isBiometricAvailable()) {
                biometricHelper.promptBiometricAuth(
                    onSuccess = {
                        errorMsg = null
                        onAuthenticated()
                    },
                    onFailed = {
                        // Fallback to PIN
                        state = AuthState.PIN_ENTRY
                    },
                    onError = { code, msg ->
                        // Fallback to PIN
                        state = AuthState.PIN_ENTRY
                    }
                )
            } else {
                state = AuthState.PIN_ENTRY
            }
        } else {
            errorMsg = "Activity context is missing."
            state = AuthState.PIN_ENTRY
        }
    }

    LaunchedEffect(Unit) {
        if (settings.pinHash.isNullOrEmpty()) {
            state = AuthState.SETUP_PIN
        } else {
            state = AuthState.BIO_PROMPT
            handleBiometric()
        }
    }

    Scaffold(modifier = modifier) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (state) {
                AuthState.CHECKING, AuthState.BIO_PROMPT -> {
                    // Similar to the old UI, just showing the fingerprint
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Fingerprint",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "TradeBot Security",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Please authenticate to access your trading dashboard.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    if (state == AuthState.BIO_PROMPT) {
                        Button(
                            onClick = handleBiometric,
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Text(
                                "Tap to Authenticate",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { state = AuthState.PIN_ENTRY }) {
                            Text("Use PIN instead")
                        }
                    }
                }

                AuthState.SETUP_PIN -> {
                    PinEntryView(
                        title = "Create a 4-digit PIN",
                        pin = currentPinInput,
                        onPinChange = { newPin ->
                            currentPinInput = newPin
                            errorMsg = null
                            if (newPin.length == 4) {
                                setupPin = newPin
                                currentPinInput = ""
                                state = AuthState.CONFIRM_PIN
                            }
                        },
                        errorMsg = errorMsg
                    )
                }

                AuthState.CONFIRM_PIN -> {
                    PinEntryView(
                        title = "Confirm your PIN",
                        pin = currentPinInput,
                        onPinChange = { newPin ->
                            currentPinInput = newPin
                            errorMsg = null
                            if (newPin.length == 4) {
                                if (newPin == setupPin) {
                                    settings.pinHash = SettingsManager.hashPin(newPin)
                                    onAuthenticated()
                                } else {
                                    errorMsg = "PINs do not match. Try again."
                                    currentPinInput = ""
                                    setupPin = ""
                                    state = AuthState.SETUP_PIN
                                }
                            }
                        },
                        errorMsg = errorMsg
                    )
                }

                AuthState.PIN_ENTRY -> {
                    PinEntryView(
                        title = "Enter your PIN",
                        pin = currentPinInput,
                        onPinChange = { newPin ->
                            currentPinInput = newPin
                            errorMsg = null
                            if (newPin.length == 4) {
                                val hash = SettingsManager.hashPin(newPin)
                                if (hash == settings.pinHash) {
                                    onAuthenticated()
                                } else {
                                    errorMsg = "Incorrect PIN."
                                    currentPinInput = ""
                                }
                            }
                        },
                        errorMsg = errorMsg
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(onClick = { 
                        state = AuthState.BIO_PROMPT
                        handleBiometric()
                    }) {
                        Text("Use Biometrics")
                    }
                }
            }
        }
    }
}

@Composable
fun PinEntryView(
    title: String,
    pin: String,
    onPinChange: (String) -> Unit,
    errorMsg: String?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // PIN Dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            for (i in 0 until 4) {
                val isFilled = i < pin.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFilled) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        if (errorMsg != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Spacer(modifier = Modifier.height(28.dp)) // Maintain spacing
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Keypad
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "DEL")
        )

        keys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { key ->
                    KeypadButton(
                        key = key,
                        onClick = {
                            if (key == "DEL") {
                                if (pin.isNotEmpty()) {
                                    onPinChange(pin.dropLast(1))
                                }
                            } else if (key.isNotEmpty() && pin.length < 4) {
                                onPinChange(pin + key)
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun KeypadButton(key: String, onClick: () -> Unit) {
    if (key.isEmpty()) {
        Spacer(modifier = Modifier.size(72.dp))
        return
    }

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
                if (key == "DEL") Color.Transparent 
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (key == "DEL") {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = key,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
