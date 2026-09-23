package com.example.civfix.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.InternalSerializationApi

@Serializable
enum class IssueCategory(val displayName: String) {
    POTHOLE("Pothole / Road"),
    STREETLIGHT("Streetlight"),
    SANITATION("Sanitation"),
    GRAFFITI("Graffiti"),
    WATER_DRAIN("Water / Drain"),
    PARK_TREES("Park / Trees")
}

@Serializable
enum class IssueSeverity(val displayName: String, val tier: String) {
    LOW("Low / Cosmetic", "Priority Tier 4"),
    MEDIUM("Medium / Standard", "Priority Tier 3"),
    HIGH("High / Urgent", "Priority Tier 2"),
    CRITICAL("Critical / Danger", "Priority Tier 1")
}

@Serializable
enum class IssueStatus(val displayName: String) {
    PENDING("Pending"),
    SCHEDULED("Scheduled"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved")
}

@Serializable
@OptIn(InternalSerializationApi::class)
data class Issue(
    val id: String? = null,
    val user_id: String? = null,
    val title: String,
    val category: String,
    val description: String,
    val severity: String,
    val status: String = "Pending",
    val latitude: Double,
    val createdAtTimestamp: Long = System.currentTimeMillis(),
    val longitude: Double,
    val address_text: String,
    val upvotes_count: Int = 0
)