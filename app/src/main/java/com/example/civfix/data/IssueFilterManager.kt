package com.example.civfix.data

// ==========================================
// MARK: - Centralized Vicinity & Filter Logic
// ==========================================

object IssueFilterManager {

    const val MAX_VICINITY_RADIUS_METERS = 10_000f // Strict 10 km limit

    /**
     * Checks if a given issue is within the user's 10 km vicinity.
     */
    fun isWithinVicinity(
        userLat: Double,
        userLng: Double,
        issueLat: Double,
        issueLng: Double
    ): Boolean {
        val distance = LocationHelper.calculateDistanceMeters(userLat, userLng, issueLat, issueLng)
        return distance <= MAX_VICINITY_RADIUS_METERS
    }

    /**
     * Filters issues strictly for the MAP:
     * - Must NOT be resolved.
     * - Must be strictly within 10 km of the user's current GPS location.
     * - Matches optional map search query.
     */
    fun filterForMap(
        allIssues: List<Issue>,
        userLat: Double,
        userLng: Double,
        searchQuery: String = ""
    ): List<Issue> {
        return allIssues.filter { issue ->
            val isNotResolved = !issue.status.equals("Resolved", ignoreCase = true)
            val inRange = isWithinVicinity(userLat, userLng, issue.latitude, issue.longitude)
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                issue.title.contains(searchQuery, ignoreCase = true) ||
                        issue.address_text.contains(searchQuery, ignoreCase = true)
            }

            isNotResolved && inRange && matchesSearch
        }
    }

    /**
     * Filters issues strictly for the FEED:
     * - Must be within 10 km of user's current GPS location.
     * - Matches category pill ("All", "Potholes", etc.).
     * - Matches search bar query.
     * - Sorted newest first.
     */
    fun filterForFeed(
        allIssues: List<Issue>,
        userLat: Double,
        userLng: Double,
        selectedCategory: String,
        searchQuery: String
    ): List<Issue> {
        return allIssues
            .filter { issue ->
                val inRange = isWithinVicinity(userLat, userLng, issue.latitude, issue.longitude)

                val matchesSearch = if (searchQuery.isBlank()) {
                    true
                } else {
                    issue.title.contains(searchQuery, ignoreCase = true) ||
                            issue.address_text.contains(searchQuery, ignoreCase = true)
                }

                val matchesCategory = when (selectedCategory) {
                    "All" -> true
                    "Potholes" -> issue.category.contains("Pothole", ignoreCase = true)
                    "Streetlights" -> issue.category.contains("Streetlight", ignoreCase = true)
                    "Graffiti" -> issue.category.contains("Graffiti", ignoreCase = true)
                    "Sanitation" -> issue.category.contains("Sanitation", ignoreCase = true)
                    "In Progress" -> issue.status.equals("In Progress", ignoreCase = true)
                    else -> true
                }

                inRange && matchesSearch && matchesCategory
            }
            .sortedByDescending { it.createdAtTimestamp }
    }
}