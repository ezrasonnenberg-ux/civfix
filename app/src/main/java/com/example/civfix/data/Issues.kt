package com.example.civfix.data

import kotlinx.serialization.SerialName
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
enum class IssueSeverity(
    val displayName: String,
    val tier: String,
    val slaDescription: String
) {
    LOW(
        displayName = "Low / Cosmetic",
        tier = "Priority Tier 4",
        slaDescription = "Minor cosmetic issue or general maintenance requirement. Scheduled during regular municipal maintenance cycles within 7–14 business days."
    ),
    MEDIUM(
        displayName = "Medium / Standard",
        tier = "Priority Tier 3",
        slaDescription = "Standard municipal service request. Poses no immediate bodily danger but impacts neighborhood utility. Typically inspected within 3–5 business days."
    ),
    HIGH(
        displayName = "High / Urgent",
        tier = "Priority Tier 2",
        slaDescription = "Requires urgent municipal attention: Hazard poses direct risk of vehicular damage or pedestrian harm. Dispatched within 24–48 hours."
    ),
    CRITICAL(
        displayName = "Critical / Danger",
        tier = "Priority Tier 1",
        slaDescription = "Immediate emergency hazard: Poses severe, active risk to public safety or major infrastructure failure. Flagged for emergency escalation within 2–6 hours."
    )
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
    val id: String = java.util.UUID.randomUUID().toString(),
    val user_id: String? = null,
    val title: String,
    val category: String,
    val description: String,
    val severity: String,
    val status: String = "Pending",
    val latitude: Double,
    val longitude: Double,
    val address_text: String,
    val upvotes_count: Int = 0,
    @SerialName("createdattimestamp") val createdAtTimestamp: Long = System.currentTimeMillis())