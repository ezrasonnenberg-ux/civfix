package com.example.civfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.civfix.data.DummyDataRepository
import com.example.civfix.data.IssueRepository
import com.example.civfix.ui.components.common.CivFixBottomBar
import com.example.civfix.ui.components.common.CivFixTopAppBar
import com.example.civfix.ui.components.common.Screen
import com.example.civfix.ui.components.feed.IssueCardItem
import com.example.civfix.ui.components.feed.SearchAndFilterHeader
import com.example.civfix.ui.screens.MapScreen
import com.example.civfix.ui.screens.ProfileScreen
import com.example.civfix.ui.screens.ReportFormScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize persistent local storage on app boot
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

    // Single source of truth from repository cache (starts empty for clean launch)
    var issueList by remember { mutableStateOf(IssueRepository.getCachedIssues()) }

    val primaryBlue = Color(0xFF004AAD)

    if (isReportFormOpen) {
        ReportFormScreen(
            onCloseClicked = { isReportFormOpen = false },
            onSubmitSuccess = {
                issueList = IssueRepository.getCachedIssues()
                isReportFormOpen = false
            }
        )
        return
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
                    onMapButtonClicked = { currentRoute = Screen.Map.route }
                )
                Screen.Map.route -> MapScreen(
                    issues = issueList,
                    onViewFullReportClicked = { issue ->
                        currentRoute = Screen.Feed.route
                    }
                )
                Screen.Profile.route -> ProfileScreen(
                    onLoadDemoDataClicked = {
                        // Seed dummy data on command for presentation demo
                        DummyDataRepository.getInitialCommunityIssues()
                        issueList = IssueRepository.getCachedIssues()
                    }
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
    onMapButtonClicked: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredIssues = issueList.filter { issue ->
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
        matchesSearch && matchesCategory
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
                Text(text = "No matching reports for this filter.", fontSize = 14.sp, color = Color.Gray)
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
                        onShareClicked = {}
                    )
                }
            }
        }
    }
}