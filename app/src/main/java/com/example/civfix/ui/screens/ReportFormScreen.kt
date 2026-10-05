package com.example.civfix.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.civfix.data.IssueCategory
import com.example.civfix.data.IssueRepository
import com.example.civfix.data.IssueSeverity
import com.example.civfix.ui.components.common.CivFixButton
import com.example.civfix.ui.components.common.ButtonStyle
import com.example.civfix.ui.components.report.CategorySelectorGrid
import com.example.civfix.ui.components.report.LocationVerificationBox
import com.example.civfix.ui.components.report.VisualProofUploader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Form inputs and selection states
    var selectedCategory by remember { mutableStateOf(IssueCategory.POTHOLE) }
    var hasPhotoSelected by remember { mutableStateOf(false) }
    var selectedSeverity by remember { mutableStateOf(IssueSeverity.HIGH) }
    var addressText by remember { mutableStateOf("Tap 'Current' to fetch GPS location") }
    var currentLatitude by remember { mutableStateOf(-33.9221) }
    var currentLongitude by remember { mutableStateOf(18.4231) }

    var issueTitleInput by remember { mutableStateOf("") }
    var issueDescriptionInput by remember { mutableStateOf("") }
    var smsNotificationsEnabled by remember { mutableStateOf(true) }

    // Submission guard to prevent double-tap duplicates
    var isSubmitting by remember { mutableStateOf(false) }

    val primaryBlue = Color(0xFF004AAD)

    // Gallery Picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            hasPhotoSelected = true
            Toast.makeText(context, "Photo attached successfully", Toast.LENGTH_SHORT).show()
        }
    }

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
                hasPhotoSelected = hasPhotoSelected,
                onRetakeClicked = { hasPhotoSelected = false },
                onAddPhotoClicked = { galleryLauncher.launch("image/*") }
            )

            // 3. Severity Assessment Section
            Text(
                text = "Severity Assessment *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
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

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFE7F5FF)
            ) {
                Text(
                    text = selectedSeverity.slaDescription,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 12.sp,
                    color = Color(0xFF1C7ED6)
                )
            }

            // 4. Location Verification Section
            LocationVerificationBox(
                addressText = addressText,
                onAddressUpdated = { newAddress, lat, lng ->
                    addressText = newAddress
                    currentLatitude = lat
                    currentLongitude = lng
                },
                onAdjustClicked = { /* Handle manual adjust */ }
            )

            // 5. Title Input Field
            Text(
                text = "Issue Title *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            OutlinedTextField(
                value = issueTitleInput,
                onValueChange = { issueTitleInput = it },
                placeholder = { Text("e.g. Severe pothole damaging car tires") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // 6. Description Input Field
            Text(
                text = "Detailed Description *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            OutlinedTextField(
                value = issueDescriptionInput,
                onValueChange = { issueDescriptionInput = it },
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

            // 7. SMS Toggle Row
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

            // 8. Submit CTA Button with Debounce Protection & Asynchronous Dispatch
            CivFixButton(
                buttonText = if (isSubmitting) "Submitting..." else "Submit CivFix Report",
                isButtonEnabled = !isSubmitting,
                onClickAction = {
                    if (isSubmitting) return@CivFixButton

                    if (issueTitleInput.isBlank() || issueDescriptionInput.isBlank()) {
                        Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                        return@CivFixButton
                    }

                    isSubmitting = true

                    coroutineScope.launch(Dispatchers.IO) {
                        val result = IssueRepository.insertIssue(
                            context = context,
                            title = issueTitleInput,
                            category = selectedCategory.displayName,
                            description = issueDescriptionInput,
                            severity = selectedSeverity.displayName,
                            latitude = currentLatitude,
                            longitude = currentLongitude,
                            addressText = addressText
                        )

                        withContext(Dispatchers.Main) {
                            isSubmitting = false
                            if (result.isSuccess) {
                                Toast.makeText(context, "Report submitted successfully!", Toast.LENGTH_SHORT).show()
                                onSubmitSuccess()
                            } else {
                                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                                Toast.makeText(context, "Failed: $errorMsg", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                style = ButtonStyle.PRIMARY_CTA
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}