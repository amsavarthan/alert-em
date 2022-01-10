package com.amsavarthan.security.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val PREFERENCES_NAME = "app_preference"

val Context.dataStore by preferencesDataStore(
    name = PREFERENCES_NAME
)

class AppDataStore(dataStore: DataStore<Preferences>) {
    private val IS_FIRST_RUN = booleanPreferencesKey("is_first_run")

    val preferenceFlow: Flow<Boolean> = dataStore.data.catch {
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }
        .map { preferences ->
            preferences[IS_FIRST_RUN] ?: true
        }

    suspend fun saveFirstRunStatusToPreferencesStore(context: Context, isFirstRun: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_RUN] = isFirstRun
        }
    }

}