package com.example.civfix.ui.components.report

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.civfix.data.LocationHelper
import com.example.civfix.ui.components.common.CivFixButton
import com.example.civfix.ui.components.common.ButtonStyle

// ==========================================
// MARK: - Location Verification Box Component
// ==========================================

@Composable
fun LocationVerificationBox(
    addressText: String,
    onAddressUpdated: (String, Double, Double) -> Unit,
    onAdjustClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primaryBlue = Color(0xFF004AAD)

    var gpsStatusText by remember { mutableStateOf("GPS: ±3m • Ready") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            gpsStatusText = "GPS: Acquiring..."
            LocationHelper.fetchCurrentLocation(
                context = context,
                onLocationFetched = { lat, lng, readable ->
                    gpsStatusText = "GPS: Locked (±2m accuracy)"
                    onAddressUpdated(readable, lat, lng)
                    Toast.makeText(context, "Live GPS synchronized!", Toast.LENGTH_SHORT).show()
                },
                onError = { e ->
                    gpsStatusText = "GPS Error"
                    Toast.makeText(context, e.localizedMessage ?: "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, "Location permission is required to fetch GPS coordinates.", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // ==========================================
        // MARK: - Header & GPS Status Tag
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
                        text = gpsStatusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1C7ED6)
                    )
                }
            }
        }

        // ==========================================
        // MARK: - Mini Map Preview Box
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFD8DCDF))
                .border(1.dp, Color.LightGray, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Map Pin",
                tint = primaryBlue,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ==========================================
        // MARK: - Address & Centered Action Buttons
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
                    text = addressText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Centered buttons row matching VisualProofUploader layout exactly
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(130.dp)) {
                CivFixButton(
                    buttonText = "Adjust",
                    onClickAction = onAdjustClicked,
                    style = ButtonStyle.SECONDARY_OUTLINE
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.width(130.dp)) {
                CivFixButton(
                    buttonText = "Current",
                    onClickAction = {
                        val permissionCheck = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            gpsStatusText = "GPS: Fetching..."
                            LocationHelper.fetchCurrentLocation(
                                context = context,
                                onLocationFetched = { lat, lng, readable ->
                                    gpsStatusText = "GPS: Locked (±2m)"
                                    onAddressUpdated(readable, lat, lng)
                                    Toast.makeText(context, "Live GPS synchronized!", Toast.LENGTH_SHORT).show()
                                },
                                onError = { e ->
                                    gpsStatusText = "GPS Error"
                                    Toast.makeText(context, e.localizedMessage ?: "Error", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    style = ButtonStyle.SECONDARY_OUTLINE
                )
            }
        }
    }
}