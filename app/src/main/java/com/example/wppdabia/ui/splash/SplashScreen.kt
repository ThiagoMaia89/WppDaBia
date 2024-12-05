package com.example.wppdabia.ui.splash

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wppdabia.R
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.ui.theme.WppDaBiaTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    context: Context,
    navigateToHome: () -> Unit,
    navigateToRegister: () -> Unit
) {

    val preferencesManager = PreferencesManager(context)

    val isRegistered by preferencesManager.isRegistered().collectAsState(initial = false)

    LaunchedEffect(Unit) {
        delay(1000)
        if (isRegistered) navigateToHome() else navigateToRegister()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                modifier = Modifier.size(80.dp),
                painter = painterResource(R.drawable.ic_whatsapp_pink),
                contentDescription = "logo",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Wpp da Bia",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontStyle = FontStyle.Italic,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SplashScreenPreview() {
    WppDaBiaTheme {
        SplashScreen(
            context = LocalContext.current,
            navigateToHome = {},
            navigateToRegister = {}
        )
    }
}