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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.civfix.data.DummyDataRepository
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

    val primaryBlue = Color(0xFF004AAD)

    // If report form modal is active, display it over everything
    if (isReportFormOpen) {
        ReportFormScreen(
            onCloseClicked = { isReportFormOpen = false },
            onSubmitSuccess = { isReportFormOpen = false }
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
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log New Issue"
                    )
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
                    onMapButtonClicked = { currentRoute = Screen.Map.route }
                )
                Screen.Map.route -> MapScreen(
                    onViewFullReportClicked = { issue ->
                        // Handle opening detail view or feed filter
                    }
                )
                Screen.Profile.route -> ProfileScreen()
            }
        }
    }
}

// ==========================================
// MARK: - Feed Screen Content Wrapper
// ==========================================

@Composable
fun FeedScreenContent(
    onMapButtonClicked: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val issueList = remember { DummyDataRepository.getInitialCommunityIssues() }

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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(issueList) { issue ->
                IssueCardItem(
                    issue = issue,
                    onUpvoteClicked = {},
                    onShareClicked = {}
                )
            }
        }
    }
}