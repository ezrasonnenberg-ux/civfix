package com.example.civfix.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// MARK: - Reusable CivFix Top App Bar Component
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CivFixTopAppBar(
    screenTitle: String,
    onNotificationClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    modifier: Modifier = Modifier,
    showNotificationBadge: Boolean = true
) {
    val primaryBlue = Color(0xFF004AAD)

    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CivFix",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Optional subtitle or screen context (e.g. "Feed" or "Map")
                Text(
                    text = "•  $screenTitle",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        actions = {
            // ==========================================
            // MARK: - Notification Bell with Badge
            // ==========================================
            Box(
                modifier = Modifier.padding(end = 4.dp)
            ) {
                IconButton(onClick = onNotificationClicked) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.DarkGray
                    )
                }

                if (showNotificationBadge) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD9480F)) // Red/Orange notification dot
                            .align(Alignment.TopEnd)
                    )
                }
            }

            // ==========================================
            // MARK: - User Profile Avatar Button
            // ==========================================
            IconButton(
                onClick = onProfileClicked,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(primaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    // Placeholder initial or avatar image icon
                    Text(
                        text = "U",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}