package com.example.civfix.ui.screens

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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

// ==========================================
// MARK: - Live WebView OpenStreetMap Component
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    issues: List<Issue>,
    onViewFullReportClicked: (Issue) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIssue by remember { mutableStateOf(issues.firstOrNull()) }
    val primaryBlue = Color(0xFF004AAD)

    // Fallback coordinates if no issues exist (defaults to your current region or general center)
    val centerLat = selectedIssue?.latitude ?: -33.9221
    val centerLng = selectedIssue?.longitude ?: 18.4231

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (issues.isEmpty()) {
            // Empty State for Map
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Text(text = "🗺️", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "No map coordinates to display", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Log an issue with active GPS to plot markers here.", fontSize = 13.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        } else {
            // ==========================================
            // MARK: - Real Interactive OpenStreetMap via WebView
            // ==========================================
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true

                        // Load OpenStreetMap centered dynamically on the issue's GPS coordinates
                        val mapUrl = "https://www.openstreetmap.org/?mlat=$centerLat&mlon=$centerLng#map=16/$centerLat/$centerLng"
                        loadUrl(mapUrl)
                    }
                },
                update = { webView ->
                    val mapUrl = "https://www.openstreetmap.org/?mlat=$centerLat&mlon=$centerLng#map=16/$centerLat/$centerLng"
                    webView.loadUrl(mapUrl)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // ==========================================
        // MARK: - Bottom Preview Card Overlay
        // ==========================================
        if (selectedIssue != null && issues.isNotEmpty()) {
            val issue = selectedIssue!!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = issue.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${issue.address_text}  •  ${issue.upvotes_count} upvotes",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(14.dp))
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