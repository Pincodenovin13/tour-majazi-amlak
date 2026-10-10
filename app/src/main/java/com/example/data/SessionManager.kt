package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

data class UserSession(
    val isLoggedIn: Boolean,
    val userId: String,
    val role: UserRole,
    val jwtToken: String,
    val name: String,
    val phone: String,
    val agencyName: String,
    val province: String,
    val city: String,
    val referralCode: String
)

class SessionManager(private val context: Context) {

    companion object {
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_USER_ID = stringPreferencesKey("user_id")
        val KEY_ROLE = stringPreferencesKey("user_role")
        val KEY_JWT_TOKEN = stringPreferencesKey("jwt_token")
        val KEY_NAME = stringPreferencesKey("user_name")
        val KEY_PHONE = stringPreferencesKey("user_phone")
        val KEY_AGENCY = stringPreferencesKey("agency_name")
        val KEY_PROVINCE = stringPreferencesKey("user_province")
        val KEY_CITY = stringPreferencesKey("user_city")
        val KEY_REFERRAL_CODE = stringPreferencesKey("referral_code")
    }

    val sessionFlow: Flow<UserSession> = context.dataStore.data.map { prefs ->
        val isLoggedIn = prefs[KEY_IS_LOGGED_IN] ?: false
        val roleStr = prefs[KEY_ROLE] ?: UserRole.PUBLIC_VISITOR.name
        val role = try {
            UserRole.valueOf(roleStr)
        } catch (_: Exception) {
            UserRole.PUBLIC_VISITOR
        }
        UserSession(
            isLoggedIn = isLoggedIn,
            userId = prefs[KEY_USER_ID] ?: "",
            role = role,
            jwtToken = prefs[KEY_JWT_TOKEN] ?: "",
            name = prefs[KEY_NAME] ?: "",
            phone = prefs[KEY_PHONE] ?: "",
            agencyName = prefs[KEY_AGENCY] ?: "",
            province = prefs[KEY_PROVINCE] ?: "مازندران",
            city = prefs[KEY_CITY] ?: "ساری",
            referralCode = prefs[KEY_REFERRAL_CODE] ?: ""
        )
    }

    suspend fun getSession(): UserSession = sessionFlow.first()

    suspend fun saveSession(
        userId: String = "user_${System.currentTimeMillis()}",
        role: UserRole,
        jwtToken: String = "mock_jwt_token_${System.currentTimeMillis()}",
        name: String,
        phone: String,
        agencyName: String = "",
        province: String = "مازندران",
        city: String = "ساری",
        referralCode: String = ""
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = true
            prefs[KEY_USER_ID] = userId
            prefs[KEY_ROLE] = role.name
            prefs[KEY_JWT_TOKEN] = jwtToken
            prefs[KEY_NAME] = name
            prefs[KEY_PHONE] = phone
            prefs[KEY_AGENCY] = agencyName
            prefs[KEY_PROVINCE] = province
            prefs[KEY_CITY] = city
            prefs[KEY_REFERRAL_CODE] = referralCode
        }
    }

    suspend fun saveSelectedCity(province: String, city: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PROVINCE] = province
            prefs[KEY_CITY] = city
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = false
            prefs[KEY_USER_ID] = ""
            prefs[KEY_ROLE] = UserRole.PUBLIC_VISITOR.name
            prefs[KEY_JWT_TOKEN] = ""
            prefs[KEY_NAME] = ""
            prefs[KEY_PHONE] = ""
            prefs[KEY_AGENCY] = ""
            prefs[KEY_REFERRAL_CODE] = ""
        }
    }
}
