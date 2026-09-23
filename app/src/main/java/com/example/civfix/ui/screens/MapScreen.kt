package com.example.civfix.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.civfix.data.Issue
import com.example.civfix.ui.components.common.CivFixButton
import com.example.civfix.ui.components.common.ButtonStyle

// ==========================================
// MARK: - Dynamic Map Screen with Empty State
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    issues: List<Issue>,
    onViewFullReportClicked: (Issue) -> Unit,
    modifier: Modifier = Modifier
) {
    var mapSearchQuery by remember { mutableStateOf("") }
    var selectedFilterChip by remember { mutableStateOf("Hazards") }

    val displayedIssues = issues.filter { issue ->
        issue.title.contains(mapSearchQuery, ignoreCase = true) ||
                issue.address_text.contains(mapSearchQuery, ignoreCase = true)
    }

    var selectedIssue by remember { mutableStateOf(issues.firstOrNull()) }
    val primaryBlue = Color(0xFF004AAD)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Map Background Canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            if (issues.isEmpty()) {
                // TRUE MAP EMPTY STATE WHEN NO ISSUES EXIST
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(text = "🗺️", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No map coordinates to display",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Log an issue or load the demo dataset from your Profile to view markers here.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map Canvas",
                        tint = Color.LightGray,
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Live GPS Vector Map (${displayedIssues.size} markers plotted)",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Render Pins Dynamically for Active Issues
                displayedIssues.forEachIndexed { index, issue ->
                    val offsetX = (index * 50 - 50).dp
                    val offsetY = (index * 40 - 70).dp

                    IconButton(
                        onClick = { selectedIssue = issue },
                        modifier = Modifier.offset(x = offsetX, y = offsetY)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFD9480F),
                            shadowElevation = 4.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = issue.title,
                                tint = Color.White,
                                modifier = Modifier.padding(6.dp).size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Top Search Bar (Only show if issues exist)
        if (issues.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                OutlinedTextField(
                    value = mapSearchQuery,
                    onValueChange = { mapSearchQuery = it },
                    placeholder = { Text("Search map reports...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
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
            }
        }

        // Bottom Preview Card
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