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
// MARK: - Bulletproof Persistent Repository
// ==========================================

object IssueRepository {

    private const val PREFS_NAME = "civfix_local_prefs"
    private const val KEY_ISSUES_JSON = "key_saved_issues_json"
    private val persistentCache = mutableListOf<Issue>()

    // Lenient JSON configuration so missing or new fields never crash the app
    private val jsonConfiguration = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    fun init(context: Context) {
        try {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonString = prefs.getString(KEY_ISSUES_JSON, null)
            if (!jsonString.isNullOrEmpty()) {
                try {
                    val decodedList = jsonConfiguration.decodeFromString<List<Issue>>(jsonString)
                    persistentCache.clear()
                    persistentCache.addAll(decodedList)
                    Log.d("IssueRepository", "Successfully loaded ${decodedList.size} items from local storage.")
                } catch (serializationError: Exception) {
                    // If JSON structure changed, log it safely instead of letting it crash the app loop
                    Log.e("IssueRepository", "Cache format mismatch, safely resetting cache: ${serializationError.localizedMessage}")
                    prefs.edit().remove(KEY_ISSUES_JSON).apply()
                    persistentCache.clear()
                }
            }
        } catch (e: Exception) {
            Log.e("IssueRepository", "Critical initialization error: ${e.localizedMessage}")
        }
    }

    private fun saveToDisk(context: Context?) {
        if (context == null) return
        try {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonString = jsonConfiguration.encodeToString(persistentCache)
            prefs.edit().putString(KEY_ISSUES_JSON, jsonString).apply()
        } catch (e: Exception) {
            Log.e("IssueRepository", "Failed to save to disk: ${e.localizedMessage}")
        }
    }

    fun getCachedIssues(): List<Issue> {
        return persistentCache
    }

    suspend fun progressIssueStatus(context: Context?, issueId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val target = persistentCache.find { it.id == issueId }
                if (target != null) {
                    val nextStatus = when (target.status.lowercase()) {
                        "pending" -> "In Progress"
                        "in progress" -> "Resolved"
                        else -> "Resolved"
                    }

                    val updated = target.copy(status = nextStatus)
                    persistentCache.remove(target)
                    persistentCache.add(updated)
                    saveToDisk(context)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
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

                persistentCache.add(0, newIssue)
                saveToDisk(context)

                // Try syncing with Supabase cloud
                try {
                    SupabaseClient.client.from("issues").insert(newIssue)
                } catch (dbError: Exception) {
                    Log.w("SupabaseSync", "Cloud sync offline, local persistence active: ${dbError.localizedMessage}")
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}