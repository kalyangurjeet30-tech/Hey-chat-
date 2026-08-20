package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.WhatsAppLightGreen

@Composable
fun AddStatusDialog(
  onPostStatus: (String, Long) -> Unit,
  onDismiss: () -> Unit
) {
  var statusText by remember { mutableStateOf("") }
  val backgroundColors = listOf(
    0xFF008069,
    0xFF8E24AA,
    0xFF00897B,
    0xFFE53935,
    0xFF3949AB,
    0xFFFB8C00,
    0xFFD81B60,
    0xFF2E7D32
  )
  var colorIndex by remember { mutableIntStateOf(0) }
  val currentColorHex = backgroundColors[colorIndex]

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(currentColorHex))
    ) {
      // Top Controls
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
          )
        }

        IconButton(
          onClick = {
            colorIndex = (colorIndex + 1) % backgroundColors.size
          },
          modifier = Modifier.testTag("status_color_palette_btn")
        ) {
          Icon(
            imageVector = Icons.Default.ColorLens,
            contentDescription = "Change background color",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
          )
        }
      }

      // Large Central Text Area
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
      ) {
        BasicTextField(
          value = statusText,
          onValueChange = { statusText = it },
          textStyle = TextStyle(
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
          ),
          cursorBrush = SolidColor(Color.White),
          decorationBox = { innerTextField ->
            if (statusText.isEmpty()) {
              Text(
                text = "Type a status...",
                color = Color(0x99FFFFFF),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
              )
            }
            innerTextField()
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("status_text_input")
        )
      }

      // Bottom Post Button
      if (statusText.isNotBlank()) {
        FloatingActionButton(
          onClick = { onPostStatus(statusText.trim(), currentColorHex) },
          containerColor = WhatsAppLightGreen,
          contentColor = Color.White,
          shape = CircleShape,
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("post_status_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Send,
            contentDescription = "Post Status",
            modifier = Modifier.size(26.dp)
          )
        }
      }
    }
  }
}
