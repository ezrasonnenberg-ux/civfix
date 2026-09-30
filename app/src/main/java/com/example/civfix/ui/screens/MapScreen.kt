package com.example.civfix.ui.screens

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.civfix.data.Issue
import com.example.civfix.ui.components.common.CivFixButton
import com.example.civfix.ui.components.common.ButtonStyle
import com.example.civfix.ui.components.feed.StatusBadge

// ==========================================
// MARK: - Full Street Map Screen with Search & Filters
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    issues: List<Issue>,
    onViewFullReportClicked: (Issue) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryBlue = Color(0xFF004AAD)

    // Interactive Search and Filter states
    var mapSearchQuery by remember { mutableStateOf("") }
    var selectedFilterChip by remember { mutableStateOf("All") }

    // Filter issues: only active (unresolved) AND matching search/chip
    val activeIssues = issues.filter { issue ->
        val isNotResolved = !issue.status.equals("Resolved", ignoreCase = true)
        val matchesSearch = issue.title.contains(mapSearchQuery, ignoreCase = true) ||
                issue.address_text.contains(mapSearchQuery, ignoreCase = true)
        val matchesChip = when (selectedFilterChip) {
            "All" -> true
            "Hazards" -> issue.severity.contains("High", ignoreCase = true) || issue.severity.contains("Critical", ignoreCase = true)
            "Lighting" -> issue.category.contains("Streetlight", ignoreCase = true)
            "Sanitation" -> issue.category.contains("Sanitation", ignoreCase = true)
            "Water / Drain" -> issue.category.contains("Water", ignoreCase = true)
            else -> true
        }
        isNotResolved && matchesSearch && matchesChip
    }

    var selectedIssue by remember(activeIssues) {
        mutableStateOf(activeIssues.firstOrNull())
    }

    val centerLat = selectedIssue?.latitude ?: -33.9221
    val centerLng = selectedIssue?.longitude ?: 18.4231

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (activeIssues.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(text = "🎉", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No active map markers",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All issues are resolved or no reports match your current filter.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            // Real OpenStreetMap Tile View
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = WebViewClient()
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            userAgentString = "CivFix-MobileApp/1.0 (Android; Educational-Project)"
                        }

                        val mapUrl = "https://www.openstreetmap.org/export/embed.html?bbox=${centerLng - 0.01}%2C${centerLat - 0.01}%2C${centerLng + 0.01}%2C${centerLat + 0.01}&layer=mapnik&marker=$centerLat%2C$centerLng"
                        loadUrl(mapUrl)
                    }
                },
                update = { webView ->
                    val mapUrl = "https://www.openstreetmap.org/export/embed.html?bbox=${centerLng - 0.01}%2C${centerLat - 0.01}%2C${centerLng + 0.01}%2C${centerLat + 0.01}&layer=mapnik&marker=$centerLat%2C$centerLng"
                    webView.loadUrl(mapUrl)
                },
                modifier = Modifier.fillMaxSize()
            )

            // Active counter badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 90.dp), // Pushed below the search overlay
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "📍 Live Grid (${activeIssues.size} Active)",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
            }
        }

        // Top Search Bar and Filter Chips Floating Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            OutlinedTextField(
                value = mapSearchQuery,
                onValueChange = { mapSearchQuery = it },
                placeholder = { Text("Filter map by address or keyword...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    if (mapSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { mapSearchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = primaryBlue
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            val filterChips = listOf("All", "Hazards", "Lighting", "Sanitation", "Water / Drain")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterChips.forEach { chip ->
                    val isSelected = (chip == selectedFilterChip)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilterChip = chip },
                        label = { Text(text = chip, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color.DarkGray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Bottom Preview Card
        if (selectedIssue != null && activeIssues.isNotEmpty()) {
            val issue = selectedIssue!!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(status = issue.status)

                        Text(
                            text = "${issue.severity.substringBefore(" /")} Priority",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = issue.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Lat: %.4f, Lng: %.4f • %d upvotes".format(
                            issue.latitude,
                            issue.longitude,
                            issue.upvotes_count
                        ),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CivFixButton(
                        buttonText = "View Full Report",
                        onClickAction = { onViewFullReportClicked(issue) },
                        style = ButtonStyle.PRIMARY_CTA
                    )
                }
            }
        }
    }
}