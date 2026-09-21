package com.example.civfix.data

import com.example.civfix.data.Issue
import com.example.civfix.data.IssueCategory
import com.example.civfix.data.IssueSeverity
import com.example.civfix.data.IssueStatus

// ==========================================
// MARK: - Dummy Data Repository for UI Testing
// ==========================================

object DummyDataRepository {

    fun getInitialCommunityIssues(): List<Issue> {
        return listOf(
            Issue(
                title = "Deep Pothole on Elm & 4th Ave",
                category = IssueCategory.POTHOLE,
                description = "Deep pothole roughly 2 feet wide right in the northbound lane. Cars are swerving into the bike lane to avoid it.",
                severity = IssueSeverity.HIGH,
                status = IssueStatus.PENDING,
                latitude = 45.5152,
                longitude = -122.6784,
                addressText = "Elm St & 4th Ave",
                imageUrls = listOf("https://picsum.photos/seed/pothole/600/400"),
                upvotesCount = 24,
                userId = "user_001"
            ),
            Issue(
                title = "Flickering Streetlight Near Elementary School",
                category = IssueCategory.STREETLIGHT,
                description = "Streetlight is flashing erratically, creating low visibility for children walking home after evening programs.",
                severity = IssueSeverity.MEDIUM,
                status = IssueStatus.SCHEDULED,
                latitude = 45.5231,
                longitude = -122.6712,
                addressText = "Pine St & Maple Dr",
                imageUrls = listOf("https://picsum.photos/seed/streetlight/600/400"),
                upvotesCount = 41,
                userId = "user_002"
            ),
            Issue(
                title = "Bulk Waste Blocking Accessible Walkway",
                category = IssueCategory.SANITATION,
                description = "Discarded furniture and mattresses blocking the sidewalk ramp, making it impossible for wheelchairs to pass.",
                severity = IssueSeverity.HIGH,
                status = IssueStatus.IN_PROGRESS,
                latitude = 45.5122,
                longitude = -122.6585,
                addressText = "Oakland Blvd, Northside",
                imageUrls = listOf("https://picsum.photos/seed/sanitation/600/400"),
                upvotesCount = 18,
                userId = "user_003"
            ),
            Issue(
                title = "Vandalized Bench at Riverfront Park",
                category = IssueCategory.GRAFFITI,
                description = "Spray paint vandalism across the main wooden bench facing the river trail.",
                severity = IssueSeverity.LOW,
                status = IssueStatus.RESOLVED,
                latitude = 45.5051,
                longitude = -122.6750,
                addressText = "Riverfront Park Trail",
                imageUrls = listOf("https://picsum.photos/seed/parkbench/600/400"),
                upvotesCount = 56,
                userId = "user_004"
            )
        )
    }
}