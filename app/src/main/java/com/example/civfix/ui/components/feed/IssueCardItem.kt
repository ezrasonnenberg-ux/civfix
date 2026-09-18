package com.example.civfix.ui.components.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Import based on where you placed Issue.kt (adjust package if your data package is different)
import com.example.civfix.data.Issue
import com.example.civfix.data.IssueStatus

// ==========================================
// MARK: - Reusable Issue Card Component
// ==========================================

@Composable
fun IssueCardItem(
    issue: Issue,
    onUpvoteClicked: () -> Unit,
    onShareClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ==========================================
            // MARK: - Header: Status Badge & Timestamp
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = issue.status)

                Text(
                    text = "25m ago",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // MARK: - Body: Title, Location & Thumbnail
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = issue.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Pin",
                            tint = Color(0xFF004AAD),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = issue.addressText,
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                // Thumbnail Image placeholder box
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // MARK: - Footer: Upvote & Share Actions
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Upvote Button Pill using clickable Surface safely
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF0F4F8),
                    modifier = Modifier.clickable { onUpvoteClicked() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ThumbUp,
                            contentDescription = "Upvote",
                            tint = Color(0xFF004AAD),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = issue.upvotesCount.toString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF004AAD)
                        )
                    }
                }

                IconButton(onClick = onShareClicked) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share Report",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

// ==========================================
// MARK: - Helper Composable: Status Badge
// ==========================================

// ==========================================
// MARK: - Helper Composable: Status Badge
// ==========================================

@Composable
fun StatusBadge(status: IssueStatus) {
    val colorPair = when (status) {
        IssueStatus.PENDING -> Color(0xFFFFECE5) to Color(0xFFD9480F)
        IssueStatus.SCHEDULED -> Color(0xFFE7F5FF) to Color(0xFF1C7ED6)
        IssueStatus.IN_PROGRESS -> Color(0xFFF3F0FF) to Color(0xFF7048E8)
        IssueStatus.RESOLVED -> Color(0xFFEBFbee) to Color(0xFF2B8A3E)
    }

    val backgroundColor = colorPair.first
    val textColor = colorPair.second

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor
    ) {
        Text(
            text = status.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}