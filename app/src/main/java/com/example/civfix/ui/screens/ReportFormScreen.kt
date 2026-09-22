package com.example.civfix.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.civfix.data.IssueCategory
import com.example.civfix.data.IssueSeverity
import com.example.civfix.ui.components.common.CivFixButton
import com.example.civfix.ui.components.common.ButtonStyle
import com.example.civfix.ui.components.report.CategorySelectorGrid
import com.example.civfix.ui.components.report.LocationVerificationBox
import com.example.civfix.ui.components.report.VisualProofUploader

// ==========================================
// MARK: - Report Form Screen Component
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFormScreen(
    onCloseClicked: () -> Unit,
    onSubmitSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Form state variables matching design mockups
    var selectedCategory by remember { mutableStateOf(IssueCategory.POTHOLE) }
    var photoCount by remember { mutableStateOf(1) }
    var selectedSeverity by remember { mutableStateOf(IssueSeverity.HIGH) }
    var addressText by remember { mutableStateOf("742 Evergreen Terrace") }
    var issueTitle by remember { mutableStateOf("") }
    var issueDescription by remember { mutableStateOf("") }
    var smsNotificationsEnabled by remember { mutableStateOf(true) }

    val primaryBlue = Color(0xFF004AAD)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Log an Issue",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCloseClicked) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Report Form",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Helper text banner matching design mockup
            Text(
                text = "Help city public works identify and fix problems in your neighborhood swiftly.",
                fontSize = 13.sp,
                color = Color.Gray
            )

            // 1. Issue Type Section
            Text(
                text = "Issue Type *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            CategorySelectorGrid(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            // 2. Visual Proof Section
            Text(
                text = "Visual Proof *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            VisualProofUploader(
                photoCount = photoCount,
                onRetakeClicked = { /* Handle camera retake */ },
                onAddPhotoClicked = { if (photoCount < 3) photoCount++ },
                onRemovePhotoClicked = { if (photoCount > 0) photoCount-- }
            )

            // 3. Severity Assessment Section
            Text(
                text = "Severity Assessment *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            // Severity pills selection row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IssueSeverity.entries.forEach { severity ->
                    val isSelected = (severity == selectedSeverity)
                    Surface(
                        onClick = { selectedSeverity = severity },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) primaryBlue else Color.White,
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        tonalElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = severity.displayName.substringBefore(" /"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.DarkGray
                            )
                        }
                    }
                }
            }

            // Severity Info Box Banner
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFE7F5FF)
            ) {
                Text(
                    text = "Requires urgent municipal attention: Hazard poses direct risk of vehicular damage or pedestrian harm. Dispatched within 24 hours.",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp,
                    color = Color(0xFF1C7ED6)
                )
            }

            // 4. Location Verification Section
            LocationVerificationBox(
                addressText = addressText,
                gpsAccuracyText = "GPS: ±3m • Auto-detected",
                onAdjustClicked = { /* Handle map coordinate adjustment */ },
                onCurrentClicked = { addressText = "Current GPS Location Verified" }
            )

            // 5. Title & Description Section
            Text(
                text = "Issue Title *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            OutlinedTextField(
                value = issueTitle,
                onValueChange = { issueTitle = it },
                placeholder = { Text("e.g. Severe pothole damaging car tires") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Text(
                text = "Detailed Description *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            OutlinedTextField(
                value = issueDescription,
                onValueChange = { issueDescription = it },
                placeholder = { Text("Describe the hazard and precise surroundings...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // 6. SMS Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Status notifications",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Get SMS updates on resolution progress",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = smsNotificationsEnabled,
                    onCheckedChange = { smsNotificationsEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = primaryBlue)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 7. Submit CTA Button
            CivFixButton(
                buttonText = "Submit CivFix Report",
                onClickAction = onSubmitSuccess,
                style = ButtonStyle.PRIMARY_CTA
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}