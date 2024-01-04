package com.project.myheavyequipment.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.myheavyequipment.utils.AccountPreferences.Companion.preferenceName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = preferenceName)

class AccountPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    fun getAccountID(): Flow<String> =
        dataStore.data.map { it[id] ?: preferenceDefaultValue }

    fun getAccountName(): Flow<String> =
        dataStore.data.map { it[name] ?: preferenceDefaultValue }

    fun getAccountEmail(): Flow<String> =
        dataStore.data.map { it[email] ?: preferenceDefaultValue }

    fun getAccountToken(): Flow<String> =
        dataStore.data.map { it[token] ?: preferenceDefaultValue }

    suspend fun saveAccountPreferences(
        userId: String,
        userName: String,
        userEmail: String,
        userToken: String
    ) {
        dataStore.edit { preferences ->
            preferences[id] = userId
            preferences[name] = userName
            preferences[email] = userEmail
            preferences[token] = userToken
        }
    }

    suspend fun clearAccountPreferences() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AccountPreferences? = null

        fun getPrefInstance(dataStore: DataStore<Preferences>): AccountPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AccountPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }

        enum class UserPreference {
            UserID,
            Username,
            UserEmail,
            UserToken
        }

        val id = stringPreferencesKey(UserPreference.UserID.name)
        val name = stringPreferencesKey(UserPreference.Username.name)
        val email = stringPreferencesKey(UserPreference.UserEmail.name)
        val token = stringPreferencesKey(UserPreference.UserToken.name)

        const val preferenceName: String = "preferences"
        const val preferenceDefaultValue: String = "default_value"
    }
}