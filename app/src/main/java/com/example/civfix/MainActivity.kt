package com.example.civfix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.civfix.data.DummyDataRepository
import com.example.civfix.ui.components.common.CivFixTopAppBar
import com.example.civfix.ui.components.feed.IssueCardItem
import com.example.civfix.ui.components.feed.SearchAndFilterHeader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF8F9FA)
            ) {
                MainTestScreen()
            }
        }
    }
}

// ==========================================
// MARK: - Temporary Test Screen to Verify UI
// ==========================================

@Composable
fun MainTestScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // Fetch our dummy data repository items!
    val issueList = remember { DummyDataRepository.getInitialCommunityIssues() }

    Scaffold(
        topBar = {
            CivFixTopAppBar(
                screenTitle = "Feed",
                onNotificationClicked = { /* TODO */ },
                onProfileClicked = { /* TODO */ }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search & Filter Header Component
            SearchAndFilterHeader(
                searchQuery = searchQuery,
                onSearchQueryChanged = { searchQuery = it },
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                onMapButtonClicked = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // LazyColumn rendering our IssueCardItems using Dummy Data
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(issueList) { issue ->
                    IssueCardItem(
                        issue = issue,
                        onUpvoteClicked = { /* TODO */ },
                        onShareClicked = { /* TODO */ }
                    )
                }
            }
        }
    }
}