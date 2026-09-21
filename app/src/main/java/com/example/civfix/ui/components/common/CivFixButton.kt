package com.example.civfix.ui.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// MARK: - Button Style Enumeration
// ==========================================

enum class ButtonStyle {
    PRIMARY_CTA,
    SECONDARY_OUTLINE
}

// ==========================================
// MARK: - Reusable Civ Fix Button Component
// ==========================================

@Composable
fun CivFixButton(
    buttonText: String,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.PRIMARY_CTA,
    isButtonEnabled: Boolean = true
) {
    val primaryBlue = Color(0xFF004AAD)
    val white = Color(0xFFFFFFFF)

    when (style) {
        ButtonStyle.PRIMARY_CTA -> {
            Button(
                onClick = onClickAction,
                enabled = isButtonEnabled,
                modifier = modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue,
                    contentColor = white,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.DarkGray
                )
            ) {
                Text(
                    text = buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        ButtonStyle.SECONDARY_OUTLINE -> {
            OutlinedButton(
                onClick = onClickAction,
                enabled = isButtonEnabled,
                modifier = modifier
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryBlue
                )
            ) {
                Text(
                    text = buttonText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}