package com.example.civfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.civfix.data.DummyDataRepository
import com.example.civfix.data.Issue
import com.example.civfix.data.IssueRepository
import com.example.civfix.data.LocationHelper
import com.example.civfix.ui.components.common.CivFixBottomBar
import com.example.civfix.ui.components.common.CivFixButton
import com.example.civfix.ui.components.common.CivFixTopAppBar
import com.example.civfix.ui.components.common.Screen
import com.example.civfix.ui.components.feed.IssueCardItem
import com.example.civfix.ui.components.feed.SearchAndFilterHeader
import com.example.civfix.ui.components.feed.StatusBadge
import com.example.civfix.ui.screens.MapScreen
import com.example.civfix.ui.screens.ProfileScreen
import com.example.civfix.ui.screens.ReportFormScreen
import kotlinx.coroutines.launch



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        IssueRepository.init(applicationContext)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF8F9FA)
            ) {
                CivFixMainApp()
            }
        }
    }
}

// ==========================================
// MARK: - Main Application Root with Routing
// ==========================================

@Composable
fun CivFixMainApp() {
    var currentRoute by remember { mutableStateOf(Screen.Feed.route) }
    var isReportFormOpen by remember { mutableStateOf(false) }
    var detailedReportToShow by remember { mutableStateOf<Issue?>(null) }

    // Start with local cache immediately so the UI doesn't stutter on open
    var issueList by remember { mutableStateOf(IssueRepository.getCachedIssues()) }

    // ==========================================
    // READ FROM DATABASE ON STARTUP EVERY TIME
    // ==========================================
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        println("[CIVFIX_LOG] Fetching latest issues from Supabase DB on startup...")
        // context
        val freshList = IssueRepository.fetchFreshIssuesFromDatabase(context)
        if (freshList.isNotEmpty()) {
            issueList = freshList.toMutableList()
            println("[CIVFIX_LOG] Successfully synced ${freshList.size} issues from cloud DB into UI!")
        }
    }

    val primaryBlue = Color(0xFF004AAD)

    // Log An Issue Form Modal
    if (isReportFormOpen) {
        ReportFormScreen(
            onCloseClicked = { isReportFormOpen = false },
            onSubmitSuccess = {
                issueList = IssueRepository.getCachedIssues().toMutableList()
                isReportFormOpen = false
            }
        )
        return
    }

    // Full Report Details Modal Dialog
    if (detailedReportToShow != null) {
        FullReportDetailsDialog(
            issue = detailedReportToShow!!,
            onDismiss = { detailedReportToShow = null }
        )
    }

    Scaffold(
        bottomBar = {
            CivFixBottomBar(
                currentRoute = currentRoute,
                onItemClicked = { screen ->
                    currentRoute = screen.route
                }
            )
        },
        floatingActionButton = {
            if (currentRoute == Screen.Feed.route) {
                FloatingActionButton(
                    onClick = { isReportFormOpen = true },
                    containerColor = primaryBlue,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Log New Issue")
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                Screen.Feed.route -> FeedScreenContent(
                    issueList = issueList,
                    onIssueActionTriggered = {
                        issueList = IssueRepository.getCachedIssues().toMutableList()
                    },
                    onMapButtonClicked = { currentRoute = Screen.Map.route }
                )
                Screen.Map.route -> {
                    println("[CIVFIX_LOG] 3. USER OPENED MAP SCREEN. Total active issues passed to map: ${issueList.size}")
                    MapScreen(
                        issues = issueList,
                        onViewFullReportClicked = { issue ->
                            println("[CIVFIX_LOG] 4. USER CLICKED 'VIEW FULL REPORT': ID=${issue.id}, Title='${issue.title}'")
                            detailedReportToShow = issue
                        }
                    )
                }
                Screen.Profile.route -> ProfileScreen(
                    onLoadDemoDataClicked = {
                        DummyDataRepository.getInitialCommunityIssues().forEach { demo ->
                            // Seed into repository
                        }
                        issueList = IssueRepository.getCachedIssues().toMutableList()
                    }
                )
            }
        }
    }
}

// ==========================================
// MARK: - Full Report Details Modal Dialog
// ==========================================

@Composable
fun FullReportDetailsDialog(
    issue: Issue,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(status = issue.status)

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = issue.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Category: ${issue.category}  •  Severity: ${issue.severity}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Description",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Text(
                    text = if (issue.description.isNotBlank()) issue.description else "No additional description provided.",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFF004AAD),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = issue.address_text,
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "GPS: Lat ${issue.latitude}, Lng ${issue.longitude}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                CivFixButton(
                    buttonText = "Close Details",
                    onClickAction = onDismiss
                )
            }
        }
    }
}

// ==========================================
// MARK: - Feed Screen Content Wrapper
// ==========================================

@Composable
fun FeedScreenContent(
    issueList: List<com.example.civfix.data.Issue>,
    onIssueActionTriggered: () -> Unit,
    onMapButtonClicked: () -> Unit
) {
    // 1. Declare state variables FIRST
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // 2. Define location filter constants
    val userLat = -33.9221
    val userLng = 18.4231
    val maxRadiusMeters = 10_000f // 10 km limit

    // 3. Filter using the state variables declared above
    val filteredIssues = issueList.filter { issue ->
        val distance = LocationHelper.calculateDistanceMeters(
            userLat, userLng,
            issue.latitude, issue.longitude
        )
        val isWithinVicinity = distance <= maxRadiusMeters

        val matchesSearch = issue.title.contains(searchQuery, ignoreCase = true) ||
                issue.address_text.contains(searchQuery, ignoreCase = true)

        val matchesCategory = when (selectedCategory) {
            "All" -> true
            "Potholes" -> issue.category.contains("Pothole", ignoreCase = true)
            "Streetlights" -> issue.category.contains("Streetlight", ignoreCase = true)
            "Graffiti" -> issue.category.contains("Graffiti", ignoreCase = true)
            "Sanitation" -> issue.category.contains("Sanitation", ignoreCase = true)
            "In Progress" -> issue.status.equals("In Progress", ignoreCase = true)
            else -> true
        }

        isWithinVicinity && matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        CivFixTopAppBar(
            screenTitle = "Feed",
            onNotificationClicked = {},
            onProfileClicked = {}
        )

        SearchAndFilterHeader(
            searchQuery = searchQuery,
            onSearchQueryChanged = { searchQuery = it },
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            onMapButtonClicked = onMapButtonClicked
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (issueList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "📭", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No civic reports yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap the '+' button to log an issue, or load demo data from your Profile.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else if (filteredIssues.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matching reports within 10 km.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredIssues) { issue ->
                    IssueCardItem(
                        issue = issue,
                        onUpvoteClicked = {},
                        onShareClicked = {},
                        onResolveClicked = {
                            if (issue.id.isNotBlank()) {
                                coroutineScope.launch {
                                    IssueRepository.progressIssueStatus(context, issue.id)
                                    onIssueActionTriggered()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}