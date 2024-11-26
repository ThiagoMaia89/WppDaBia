package com.example.wppdabia.data.data_store

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor (@ApplicationContext private val context: Context) {

    companion object {
        private const val DATASTORE_NAME = "user_prefs"
        val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)
        val IS_REGISTERED = booleanPreferencesKey("is_registered")
    }

    private val dataStore = context.dataStore

    suspend fun saveIsRegistered(isRegistered: Boolean) {
        dataStore.edit { prefs ->
            prefs[IS_REGISTERED] = isRegistered
        }
    }

    fun isRegistered(): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            prefs[IS_REGISTERED] ?: false
        }
    }

}