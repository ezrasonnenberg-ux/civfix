package com.example.civfix.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// ==========================================
// MARK: - Persistent Local Storage Repository
// ==========================================

object IssueRepository {

    private const val PREFS_NAME = "civfix_local_prefs"
    private const val KEY_ISSUES_JSON = "key_saved_issues_json"

    private var isInitialized = false
    private val persistentCache = mutableListOf<Issue>()

    // Initialize storage from device SharedPreferences on app startup
    fun init(context: Context) {
        if (isInitialized) return
        try {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonString = prefs.getString(KEY_ISSUES_JSON, null)
            if (!jsonString.isNullOrEmpty()) {
                val decodedList = Json.decodeFromString<List<Issue>>(jsonString)
                persistentCache.clear()
                persistentCache.addAll(decodedList)
            }
        } catch (e: Exception) {
            Log.e("IssueRepository", "Failed to load persistent storage: ${e.localizedMessage}")
        }
        isInitialized = true
    }

    private fun saveToDisk(context: Context?) {
        if (context == null) return
        try {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonString = Json.encodeToString(persistentCache)
            prefs.edit().putString(KEY_ISSUES_JSON, jsonString).apply()
        } catch (e: Exception) {
            Log.e("IssueRepository", "Failed to save to persistent storage: ${e.localizedMessage}")
        }
    }

    fun getCachedIssues(): List<Issue> {
        return persistentCache
    }

    suspend fun insertIssue(
        context: Context?,
        title: String,
        category: String,
        description: String,
        severity: String,
        latitude: Double,
        longitude: Double,
        addressText: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val newIssue = Issue(
                    title = title,
                    category = category,
                    description = description,
                    severity = severity,
                    latitude = latitude,
                    longitude = longitude,
                    address_text = addressText,
                    status = "Pending",
                    upvotes_count = 1
                )

                // 1. Add to active memory cache
                persistentCache.add(0, newIssue)

                // 2. Commit and persist immediately to device storage so it survives reboots!
                saveToDisk(context)

                // 3. Try syncing with Supabase/Docker in the background
                try {
                    SupabaseClient.client.from("issues").insert(newIssue)
                } catch (dbError: Exception) {
                    Log.w("SupabaseSync", "Docker backend offline, local persistence active: ${dbError.localizedMessage}")
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}