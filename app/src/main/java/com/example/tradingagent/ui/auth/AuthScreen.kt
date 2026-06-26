package com.example.tradingagent.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity

@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val authenticate = {
        if (activity != null) {
            val biometricHelper = BiometricHelper(activity)
            if (biometricHelper.isBiometricAvailable()) {
                biometricHelper.promptBiometricAuth(
                    onSuccess = {
                        errorMsg = null
                        onAuthenticated()
                    },
                    onFailed = {
                        errorMsg = "Authentication failed. Try again."
                    },
                    onError = { code, msg ->
                        errorMsg = "Error: $msg"
                    }
                )
            } else {
                // If biometric is not available, maybe fallback to standard login or let them in (for testing)
                Toast.makeText(context, "Biometric auth not available.", Toast.LENGTH_SHORT).show()
                // For a real app, you'd show a password screen. We'll let them in if unavailable.
                onAuthenticated()
            }
        } else {
            errorMsg = "Activity context is missing."
        }
    }

    // Auto-prompt when screen appears
    LaunchedEffect(Unit) {
        authenticate()
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
                text = "Please authenticate to access your trading dashboard and active positions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = authenticate,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(
                    "Tap to Authenticate",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMsg!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
