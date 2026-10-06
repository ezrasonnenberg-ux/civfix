package com.example.civfix.ui.screens

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.civfix.data.IssueFilterManager
import com.example.civfix.ui.components.common.CivFixButton
import com.example.civfix.ui.components.common.ButtonStyle
import com.example.civfix.ui.components.feed.StatusBadge

// ==========================================
// MARK: - Option A: Strict Location-Centric Map Screen
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    issues: List<Issue>,
    userLat: Double,
    userLng: Double,
    onViewFullReportClicked: (Issue) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryBlue = Color(0xFF004AAD)

    // Single source of truth: 10km vicinity & active-only
    val mapIssues = remember(issues, userLat, userLng) {
        IssueFilterManager.filterForMap(
            allIssues = issues,
            userLat = userLat,
            userLng = userLng
        )
    }

    var selectedIssue by remember(mapIssues) {
        mutableStateOf(mapIssues.firstOrNull())
    }

    // MAP ALWAYS CENTERS ON THE USER'S LOCATION

    val centerLat = userLat
    val centerLng = userLng

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (mapIssues.isEmpty()) {
            // Clean local empty state
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
                    Text(text = "📍", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No active hazards in your 10 km area",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All reported issues in your vicinity have been resolved or none exist.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            // Live map centered on the USER, showing local pins
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

                        val mapUrl = "https://www.openstreetmap.org/export/embed.html?bbox=${centerLng - 0.02}%2C${centerLat - 0.02}%2C${centerLng + 0.02}%2C${centerLat + 0.02}&layer=mapnik&marker=$centerLat%2C$centerLng"
                        loadUrl(mapUrl)
                    }
                },
                update = { webView ->
                    val mapUrl = "https://www.openstreetmap.org/export/embed.html?bbox=${centerLng - 0.02}%2C${centerLat - 0.02}%2C${centerLng + 0.02}%2C${centerLat + 0.02}&layer=mapnik&marker=$centerLat%2C$centerLng"
                    webView.loadUrl(mapUrl)
                },
                modifier = Modifier.fillMaxSize()
            )

            // Local vicinity counter
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "📍 Your 10 km Area (${mapIssues.size} Active)",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
            }
        }

        // Bottom Preview Card (only if local issues exist)
        if (selectedIssue != null && mapIssues.isNotEmpty()) {
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
                        text = "${issue.address_text} • ${issue.upvotes_count} upvotes",
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