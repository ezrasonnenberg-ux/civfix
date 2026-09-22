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
import com.example.civfix.data.DummyDataRepository
import com.example.civfix.data.Issue
import com.example.civfix.ui.components.common.CivFixButton
import com.example.civfix.ui.components.common.ButtonStyle

// ==========================================
// MARK: - Map Screen Component
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onViewFullReportClicked: (Issue) -> Unit,
    modifier: Modifier = Modifier
) {
    val sampleIssues = remember { DummyDataRepository.getInitialCommunityIssues() }
    var selectedIssue by remember { mutableStateOf<Issue?>(sampleIssues.firstOrNull()) }
    var selectedFilterChip by remember { mutableStateOf("Hazards") }

    val filterChips = listOf("Hazards", "Lighting", "Sanitation", "Water / Drain")
    val primaryBlue = Color(0xFF004AAD)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // ==========================================
        // MARK: - Map Background Simulation Box
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
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
                    text = "Interactive Map Layer (Portland / Downtown)",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
            }

            // User marker
            Box(
                modifier = Modifier
                    .offset(x = 10.dp, y = 20.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(primaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            // Hazard pin
            IconButton(
                onClick = { selectedIssue = sampleIssues.getOrNull(0) },
                modifier = Modifier
                    .offset(x = (-40).dp, y = (-80).dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFD9480F),
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Hazard Pin",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp).size(20.dp)
                    )
                }
            }
        }

        // ==========================================
        // MARK: - Top Floating Search & Filter Bar
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            OutlinedTextField(
                value = "Elm & 4th Precinct",
                onValueChange = {},
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Search",
                        tint = primaryBlue
                    )
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterChips.forEach { chip ->
                    val isSelected = (chip == selectedFilterChip)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilterChip = chip },
                        label = { Text(text = chip) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (chip == "Hazards") Color(0xFFD9480F) else primaryBlue,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color.DarkGray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // ==========================================
        // MARK: - Bottom Floating Issue Preview Card
        // ==========================================
        if (selectedIssue != null) {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFECE5)
                        ) {
                            Text(
                                text = "Hazard",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD9480F)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = "Distance",
                                tint = primaryBlue,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "0.2 mi away",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = issue.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "25m ago  •  ${issue.upvotesCount} upvotes",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            CivFixButton(
                                buttonText = "View Full Report",
                                onClickAction = { onViewFullReportClicked(issue) },
                                style = ButtonStyle.PRIMARY_CTA
                            )
                        }
                        Box(modifier = Modifier.width(90.dp)) {
                            CivFixButton(
                                buttonText = "Route",
                                onClickAction = { },
                                style = ButtonStyle.SECONDARY_OUTLINE
                            )
                        }
                    }
                }
            }
        }
    }
}