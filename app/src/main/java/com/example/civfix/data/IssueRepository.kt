package com.example.civfix.data

import android.util.Log
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ==========================================
// MARK: - Safe Hybrid Issue Repository
// ==========================================

object IssueRepository {

    // Local runtime cache list to display new posts in the Feed
    private val localRuntimeCache = mutableListOf<Issue>()

    fun getCachedIssues(): List<Issue> {
        return localRuntimeCache
    }

    suspend fun insertIssue(
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

                // 1. Instantly save to local runtime cache
                localRuntimeCache.add(0, newIssue)

                // 2. Safely attempt Supabase network insertion if Docker/Cloud is active
                try {
                    SupabaseClient.client.from("issues").insert(newIssue)
                } catch (dbError: Exception) {
                    Log.w("SupabaseSync", "Network/Docker offline, using local app state: ${dbError.localizedMessage}")
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}