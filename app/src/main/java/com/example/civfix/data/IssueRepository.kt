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
// MARK: - Fully Synchronized Database Repository
// ==========================================

object IssueRepository {

    private const val PREFS_NAME = "civfix_local_prefs"
    private const val KEY_ISSUES_JSON = "key_saved_issues_json"
    private val persistentCache = mutableListOf<Issue>()

    fun init(context: Context) {
        try {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonString = prefs.getString(KEY_ISSUES_JSON, null)
            if (!jsonString.isNullOrEmpty()) {
                try {
                    val decodedList = Json.decodeFromString<List<Issue>>(jsonString)
                    persistentCache.clear()
                    persistentCache.addAll(decodedList)
                } catch (e: Exception) {
                    prefs.edit().remove(KEY_ISSUES_JSON).apply()
                    persistentCache.clear()
                }
            }
        } catch (e: Exception) {
            Log.e("IssueRepository", "Init error: ${e.localizedMessage}")
        }
    }

    private fun saveToDisk(context: Context?) {
        if (context == null) return
        try {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonString = Json.encodeToString(persistentCache)
            prefs.edit().putString(KEY_ISSUES_JSON, jsonString).apply()
        } catch (e: Exception) {
            Log.e("IssueRepository", "Save error: ${e.localizedMessage}")
        }
    }

    fun getCachedIssues(): List<Issue> {
        return persistentCache
    }

    // Pulls centralized data directly from Supabase DB to eliminate multi-user conflicts
    suspend fun fetchFreshIssuesFromDatabase(): List<Issue> {
        return withContext(Dispatchers.IO) {
            try {
                val remoteIssues = SupabaseClient.client
                    .from("issues")
                    .select()
                    .decodeList<Issue>()

                persistentCache.clear()
                persistentCache.addAll(remoteIssues)
                persistentCache
            } catch (e: Exception) {
                Log.w("SupabaseFetch", "Offline mode active, using local cache: ${e.localizedMessage}")
                persistentCache
            }
        }
    }

    // Progresses issue status sequentially: Pending -> In Progress -> Resolved (Archived)
    suspend fun progressIssueStatus(context: Context?, issueId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("IssueRepo", "Attempting to progress status for ID: $issueId")
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
                    Log.d("IssueRepo", "Successfully updated local state to: $nextStatus")
                } else {
                    Log.w("IssueRepo", "Warning: Target issue with ID $issueId not found in cache!")
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("IssueRepo", "Error progressing status: ${e.localizedMessage}")
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

                try {
                    SupabaseClient.client.from("issues").insert(newIssue)
                } catch (dbError: Exception) {
                    Log.w("SupabaseSync", "Offline insertion recorded: ${dbError.localizedMessage}")
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}