package com.example.civfix.ui.components.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.civfix.ui.components.common.CivFixButton
import com.example.civfix.ui.components.common.ButtonStyle

// ==========================================
// MARK: - Location Verification Box Component
// ==========================================

@Composable
fun LocationVerificationBox(
    addressText: String,
    gpsAccuracyText: String,
    onAdjustClicked: () -> Unit,
    onCurrentClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryBlue = Color(0xFF004AAD)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // ==========================================
        // MARK: - GPS Status Tag & Mini Map Box
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Location Verification *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // GPS Auto-detected pill tag matching design
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE7F5FF)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "GPS Status",
                        tint = Color(0xFF1C7ED6),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = gpsAccuracyText, // e.g. "GPS: ±3m • Auto-detected"
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1C7ED6)
                    )
                }
            }
        }

        // Mini map simulation card container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFD8DCDF))
                .border(1.dp, Color.LightGray, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Center map pin icon representing auto-detected coordinates
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Map Pin",
                tint = primaryBlue,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ==========================================
        // MARK: - Address Display & Action Buttons Row
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Address",
                    tint = primaryBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = addressText, // e.g. "742 Evergreen Terrace"
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    maxLines = 1
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.width(80.dp)) {
                    CivFixButton(
                        buttonText = "Adjust",
                        onClickAction = onAdjustClicked,
                        style = ButtonStyle.SECONDARY_OUTLINE
                    )
                }
                Box(modifier = Modifier.width(85.dp)) {
                    CivFixButton(
                        buttonText = "Current",
                        onClickAction = onCurrentClicked,
                        style = ButtonStyle.SECONDARY_OUTLINE
                    )
                }
            }
        }
    }
}