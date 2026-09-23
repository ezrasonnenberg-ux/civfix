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
                category = IssueCategory.POTHOLE.displayName,
                description = "Deep pothole roughly 2 feet wide right in the northbound lane. Cars are swerving into the bike lane to avoid it.",
                severity = IssueSeverity.HIGH.displayName,
                status = IssueStatus.PENDING.displayName,
                latitude = 45.5152,
                longitude = -122.6784,
                address_text = "Elm St & 4th Ave",
                upvotes_count = 24,
                user_id = "user_001"
            ),
            Issue(
                title = "Flickering Streetlight Near Elementary School",
                category = IssueCategory.STREETLIGHT.displayName,
                description = "Streetlight is flashing erratically, creating low visibility for children walking home after evening programs.",
                severity = IssueSeverity.MEDIUM.displayName,
                status = IssueStatus.SCHEDULED.displayName,
                latitude = 45.5231,
                longitude = -122.6712,
                address_text = "Pine St & Maple Dr",
                upvotes_count = 41,
                user_id = "user_002"
            ),
            Issue(
                title = "Bulk Waste Blocking Accessible Walkway",
                category = IssueCategory.SANITATION.displayName,
                description = "Discarded furniture and mattresses blocking the sidewalk ramp, making it impossible for wheelchairs to pass.",
                severity = IssueSeverity.HIGH.displayName,
                status = IssueStatus.IN_PROGRESS.displayName,
                latitude = 45.5122,
                longitude = -122.6585,
                address_text = "Oakland Blvd, Northside",
                upvotes_count = 18,
                user_id = "user_003"
            ),
            Issue(
                title = "Vandalized Bench at Riverfront Park",
                category = IssueCategory.GRAFFITI.displayName,
                description = "Spray paint vandalism across the main wooden bench facing the river trail.",
                severity = IssueSeverity.LOW.displayName,
                status = IssueStatus.RESOLVED.displayName,
                latitude = 45.5051,
                longitude = -122.6750,
                address_text = "Riverfront Park Trail",
                upvotes_count = 56,
                user_id = "user_004"
            )
        )
    }
}