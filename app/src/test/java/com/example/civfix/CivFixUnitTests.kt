package com.example.civfix

import com.example.civfix.data.Issue
import com.example.civfix.ui.components.feed.formatTimeAgo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// ==========================================
// MARK: - Core Functionality JUnit Tests
// ==========================================

class CivicFixUnitTests {

    @Test
    fun testTimeAgoFormatting_justNow() {
        val currentTime = System.currentTimeMillis()
        val formatted = formatTimeAgo(currentTime)
        assertEquals("Just now", formatted)
    }

    @Test
    fun testTimeAgoFormatting_minutesAgo() {
        val fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000)
        val formatted = formatTimeAgo(fiveMinutesAgo)
        assertEquals("5m ago", formatted)
    }

    fun sanitizeText(rawInput: String, maxLength: Int): String {
        return rawInput
            // 1. Remove entire <script>...</script> blocks including contents (case-insensitive & multiline/dotall)
            .replace(Regex("(?is)<script.*?>.*?</script>"), "")
            // 2. Remove any remaining HTML tags (like <b>, </i>, <p>)
            .replace(Regex("<[^>]*>"), "")
            // 3. Collapse multiple spaces/tabs into a single space
            .replace(Regex("[ \\t]+"), " ")
            // 4. Normalize newlines
            .replace(Regex("(\\r?\\n){3,}"), "\n\n")
            // 5. Trim leading and trailing spaces
            .trim()
            // 6. Enforce max length
            .take(maxLength)
    }

    @Test
    fun testInputSanitizer_validationFailsOnBlankInput() {
        val blankTitle = "     "
        val validDescription = "Streetlight is out completely."
        val result = com.example.civfix.data.InputSanitizer.validateReportInput(blankTitle, validDescription)

        org.junit.Assert.assertTrue(result is com.example.civfix.data.ValidationResult.Error)
    }


    @Test
    fun testIssueDataModel_defaultValues() {
        val testIssue = Issue(
            title = "Broken Traffic Light",
            category = "Streetlight",
            description = "Light is completely out at Main St intersection.",
            severity = "High",
            latitude = -33.9221,
            longitude = 18.4231,
            address_text = "Main St"
        )

        assertEquals("Pending", testIssue.status)
        assertEquals(0, testIssue.upvotes_count)
        assertEquals("Broken Traffic Light", testIssue.title)
        assertTrue(testIssue.latitude != 0.0)
    }
}