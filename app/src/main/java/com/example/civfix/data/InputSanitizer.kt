package com.example.civfix.data

// ==========================================
// MARK: - Input Sanitization & Validation Utility
// ==========================================

object InputSanitizer {

    private const val MAX_TITLE_LENGTH = 100
    private const val MAX_DESCRIPTION_LENGTH = 1000

    /**
     * Strip HTML/script tags, collapse duplicate whitespaces/newlines
     */
    fun sanitizeText(rawInput: String, maxLength: Int): String {
        return rawInput
            .trim()
            // Strip HTML/JavaScript tags to prevent injection/formatting corruption
            .replace(Regex("<.*?>"), "")
            // Collapse multiple consecutive spaces or tabs into a single space
            .replace(Regex("[ \\t]+"), " ")
            // Normalize excessive blank lines to a single newline
            .replace(Regex("(\\r?\\n){3,}"), "\n\n")
            // Enforce maximum length constraint
            .take(maxLength)
    }

    fun sanitizeTitle(title: String): String {
        return sanitizeText(title, MAX_TITLE_LENGTH)
    }

    fun sanitizeDescription(description: String): String {
        return sanitizeText(description, MAX_DESCRIPTION_LENGTH)
    }

    /**
     * Validate sanitized user input against civic hazard requirements.
     */
    fun validateReportInput(title: String, description: String): ValidationResult {
        val cleanTitle = sanitizeTitle(title)
        val cleanDescription = sanitizeDescription(description)

        return when {
            cleanTitle.isBlank() -> {
                ValidationResult.Error("Report title cannot be empty or contain only blank spaces.")
            }
            cleanTitle.length < 3 -> {
                ValidationResult.Error("Title must be at least 3 characters long.")
            }
            cleanDescription.isBlank() -> {
                ValidationResult.Error("Please provide a description of the infrastructure hazard.")
            }
            cleanDescription.length < 5 -> {
                ValidationResult.Error("Description is too brief. Please provide at least 5 characters.")
            }
            else -> {
                ValidationResult.Success(cleanTitle, cleanDescription)
            }
        }
    }
}

// ==========================================
// MARK: - Validation Result Sealed Type
// ==========================================

sealed class ValidationResult {
    data class Success(val sanitizedTitle: String, val sanitizedDescription: String) : ValidationResult()
    data class Error(val errorMessage: String) : ValidationResult()
}