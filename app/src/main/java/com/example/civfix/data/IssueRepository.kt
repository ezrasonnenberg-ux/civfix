package com.example.civfix.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.withTimeout

// ==========================================
// MARK: - Synchronized Persistent Repository
// ==========================================

object IssueRepository {

    private const val PREFS_NAME = "civfix_local_prefs"
    private const val KEY_ISSUES_JSON = "key_saved_issues_json"
    private val persistentCache = mutableListOf<Issue>()
    private var isInitialized = false

    private val jsonConfiguration = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    fun init(context: Context) {
        if (isInitialized) return
        runCatching {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val jsonString = prefs.getString(KEY_ISSUES_JSON, null)
            if (!jsonString.isNullOrEmpty()) {
                val decodedList = jsonConfiguration.decodeFromString<List<Issue>>(jsonString)
                val uniqueList = decodedList.distinctBy { it.id }
                persistentCache.clear()
                persistentCache.addAll(uniqueList)
                saveToDisk(context)
                Log.d("IssueRepository", "Loaded ${uniqueList.size} unique items from local storage.")
            } else {
                persistentCache.clear()
                Log.d("IssueRepository", "Storage empty. Ready for initial records.")
            }
        }.onFailure { throwable ->
            Log.e("IssueRepository", "Corrupt storage detected: ${throwable.message}. Resetting.")
            runCatching {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().remove(KEY_ISSUES_JSON).apply()
            }
            persistentCache.clear()
        }
        isInitialized = true
    }

    private fun saveToDisk(context: Context?) {
        if (context == null) return
        try {
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val deduplicatedList = persistentCache.distinctBy { it.id }
            val jsonString = jsonConfiguration.encodeToString(deduplicatedList)
            prefs.edit().putString(KEY_ISSUES_JSON, jsonString).apply()
        } catch (e: Exception) {
            Log.e("IssueRepository", "Failed to save to disk: ${e.localizedMessage}")
        }
    }

    fun getCachedIssues(): List<Issue> {
        return persistentCache.distinctBy { it.id }
    }

    // Pull central data directly from Supabase DB
    suspend fun fetchFreshIssuesFromDatabase(context: Context?): List<Issue> {
        return withContext(Dispatchers.IO) {
            try {
                println("[CIVFIX_LOG] Fetching directly from Supabase 'issues' table...")
                val remoteIssues = withTimeout(6000L) {
                    SupabaseClient.client
                        .from("issues")
                        .select()
                        .decodeList<Issue>()
                }

                println("[CIVFIX_LOG] Fetched ${remoteIssues.size} rows from Supabase!")

                // Clear out any old local junk and replace with the true DB records
                persistentCache.clear()
                persistentCache.addAll(remoteIssues.distinctBy { it.id })

                // Save the true DB records to local disk
                saveToDisk(context)

                persistentCache
            } catch (e: Exception) {
                println("[CIVFIX_LOG] Supabase fetch failed: ${e.message}. Using local disk cache.")
                persistentCache
            }
        }
    }

    // ==========================================
    // MARK: - Safe Auto-Sync Offline Reports to Cloud
    // ==========================================
    suspend fun syncPendingLocalIssuesToCloud() {
        withContext(Dispatchers.IO) {
            runCatching {
                // 1. Fetch remote IDs with a tight 3s timeout so it never hangs
                val remoteIssues = withTimeout(3000L) {
                    SupabaseClient.client.from("issues").select().decodeList<Issue>()
                }

                // Normalize UUIDs to lowercase to prevent casing mismatches
                val remoteIds = remoteIssues.map { it.id.lowercase().trim() }.toSet()

                // 2. Identify local issues that genuinely do not exist in the cloud
                val unSyncedIssues = persistentCache.filter { it.id.lowercase().trim() !in remoteIds }

                if (unSyncedIssues.isNotEmpty()) {
                    println("[CIVFIX_LOG] Background sync: uploading ${unSyncedIssues.size} offline reports...")
                    for (issue in unSyncedIssues) {
                        runCatching {
                            withTimeout(3000L) {
                                SupabaseClient.client.from("issues").insert(issue)
                            }
                            println("[CIVFIX_LOG] Synced issue '${issue.title}' successfully.")
                        }.onFailure { err ->
                            // Catches any duplicate key or format issue silently without breaking anything
                            println("[CIVFIX_LOG] Skipped upload for '${issue.title}': ${err.message}")
                        }
                    }
                }
            }.onFailure { networkErr ->
                // If device is offline or network is slow, fail fast and silently
                println("[CIVFIX_LOG] Background sync deferred (offline or slow): ${networkErr.message}")
            }
        }
    }

    // Progress status: Pending -> In Progress -> Resolved
    suspend fun progressIssueStatus(context: Context?, issueId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val targetIndex = persistentCache.indexOfFirst { it.id == issueId }
                if (targetIndex != -1) {
                    val currentItem = persistentCache[targetIndex]
                    val nextStatus = when (currentItem.status.lowercase()) {
                        "pending" -> "In Progress"
                        "in progress" -> "Resolved"
                        else -> "Resolved"
                    }

                    persistentCache[targetIndex] = currentItem.copy(status = nextStatus)
                    saveToDisk(context)

                    try {
                        SupabaseClient.client.from("issues").update({
                            set("status", nextStatus)
                        }) {
                            filter {
                                eq("id", issueId)
                            }
                        }
                    } catch (dbError: Exception) {
                        Log.w("SupabaseUpdate", "Cloud sync pending: ${dbError.localizedMessage}")
                    }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Insert issue locally to cache and push to Supabase
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
                println("[CIVFIX_LOG] 1. USER CREATING ISSUE: Title='$title', Lat=$latitude, Lng=$longitude")

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

                // 1. Save to local cache & disk first so it's never lost
                persistentCache.add(0, newIssue)
                val cleanedList = persistentCache.distinctBy { it.id }
                persistentCache.clear()
                persistentCache.addAll(cleanedList)
                saveToDisk(context)

                // 2. Network push wrapped in strict 5-second timeout
                println("[CIVFIX_LOG] 2. ATTEMPTING SUPABASE DB WRITE (5s timeout)...")
                try {
                    withTimeout(5000L) {
                        SupabaseClient.client.from("issues").insert(newIssue)
                    }
                    println("[CIVFIX_LOG] >>> SUPABASE DB WRITE SUCCESS!")
                } catch (timeoutEx: kotlinx.coroutines.TimeoutCancellationException) {
                    println("[CIVFIX_LOG] Supabase timed out after 5s. Stored safely in local offline cache.")
                } catch (dbError: Exception) {
                    println("[CIVFIX_LOG] Supabase error: ${dbError.message}. Stored in local cache.")
                }

                Result.success(Unit)
            } catch (e: Exception) {
                println("[CIVFIX_LOG] Local Repository Insert Exception: ${e.localizedMessage}")
                Result.failure(e)
            }
        }

    }
}