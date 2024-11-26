package com.example.wppdabia.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.ui.components.AppBaseContent
import com.example.wppdabia.ui.navigation.NavigationHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val preferencesManager by lazy { PreferencesManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NavigationHandler(preferencesManager)
        }
    }
}