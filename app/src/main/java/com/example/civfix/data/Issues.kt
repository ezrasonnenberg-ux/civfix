package com.example.civfix.data

import java.util.UUID

// ==========================================
// MARK: - Enums for Categories, Severity, and Status
// ==========================================

enum class IssueCategory(val displayName: String) {
    POTHOLE("Pothole / Road"),
    STREETLIGHT("Streetlight"),
    SANITATION("Sanitation"),
    GRAFFITI("Graffiti"),
    WATER_DRAIN("Water / Drain"),
    PARK_TREES("Park / Trees")
}

enum class IssueSeverity(val displayName: String, val tier: String) {
    LOW("Low / Cosmetic", "Priority Tier 4"),
    MEDIUM("Medium / Standard", "Priority Tier 3"),
    HIGH("High / Urgent", "Priority Tier 2"),
    CRITICAL("Critical / Danger", "Priority Tier 1")
}

enum class IssueStatus(val displayName: String) {
    PENDING("Pending"),
    SCHEDULED("Scheduled"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved")
}

// ==========================================
// MARK: - Main Issue Data Class
// ==========================================

data class Issue(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val category: IssueCategory,
    val description: String,
    val severity: IssueSeverity,
    val status: IssueStatus = IssueStatus.PENDING,
    val latitude: Double,
    val longitude: Double,
    val addressText: String,
    val imageUrls: List<String> = emptyList(),
    val upvotesCount: Int = 0,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)