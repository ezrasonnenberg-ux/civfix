package com.example.civfix.ui.components.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.CheckCircle
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
// MARK: - Visual Proof Uploader Component
// ==========================================

@Composable
fun VisualProofUploader(
    photoCount: Int,
    onRetakeClicked: () -> Unit,
    onAddPhotoClicked: () -> Unit,
    onRemovePhotoClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryBlue = Color(0xFF004AAD)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // ==========================================
        // MARK: - Photo Preview Container Box
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE9ECEF))
                .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // If photos are attached, show preview layout; otherwise show placeholder state
            if (photoCount > 0) {
                // Mock preview representation matching your design
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Photo Attached",
                        tint = Color(0xFF2B8A3E),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$photoCount of 3 photos added",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Add Photo",
                        tint = primaryBlue,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to capture visual proof",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ==========================================
        // MARK: - Retake & Add Photo Action Buttons
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CivFixButton(
                    buttonText = "Retake",
                    onClickAction = onRetakeClicked,
                    style = ButtonStyle.SECONDARY_OUTLINE
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                CivFixButton(
                    buttonText = "Add Photo",
                    onClickAction = onAddPhotoClicked,
                    style = ButtonStyle.SECONDARY_OUTLINE
                )
            }
        }
    }
}