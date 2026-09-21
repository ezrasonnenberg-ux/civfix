package com.example.civfix.ui.components.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.civfix.data.IssueCategory

// ==========================================
// MARK: - Category Selector Grid Component
// ==========================================

@Composable
fun CategorySelectorGrid(
    selectedCategory: IssueCategory,
    onCategorySelected: (IssueCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = IssueCategory.entries.toTypedArray()
    val primaryBlue = Color(0xFF004AAD)

    // Manual safe grid grouping (3 items per row) without using extension functions
    val rows = categories.toList().let { list ->
        val result = mutableListOf<List<IssueCategory>>()
        var i = 0
        while (i < list.size) {
            result.add(list.subList(i, minOf(i + 3, list.size)))
            i += 3
        }
        result
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowCategories.forEach { category ->
                    val isSelected = (category == selectedCategory)

                    CategoryCardItem(
                        category = category,
                        isSelected = isSelected,
                        onClick = { onCategorySelected(category) },
                        modifier = Modifier.weight(1f),
                        primaryColor = primaryBlue
                    )
                }

                repeat(3 - rowCategories.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// ==========================================
// MARK: - Individual Category Card Item
// ==========================================

@Composable
fun CategoryCardItem(
    category: IssueCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) primaryColor else Color(0xFFF8F9FA)
    val contentColor = if (isSelected) Color.White else Color.DarkGray
    val borderColor = if (isSelected) primaryColor else Color.LightGray.copy(alpha = 0.5f)

    val icon: ImageVector = when (category) {
        IssueCategory.POTHOLE -> Icons.Default.Warning
        IssueCategory.STREETLIGHT -> Icons.Default.Lightbulb
        IssueCategory.SANITATION -> Icons.Default.Delete
        IssueCategory.GRAFFITI -> Icons.Default.Create
        IssueCategory.WATER_DRAIN -> Icons.Default.WaterDrop
        IssueCategory.PARK_TREES -> Icons.Default.Home
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = category.displayName,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.displayName.substringBefore(" /"),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}