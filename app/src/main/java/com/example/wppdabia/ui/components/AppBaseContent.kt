package com.example.wppdabia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wppdabia.ui.navigation.NavigationHandler
import com.example.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun AppBaseContent(content: @Composable () -> Unit) {
    WppDaBiaTheme {
        Box(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp)
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            content()
        }
    }
}