package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddIcCall
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AvatarType
import com.example.data.CallDirection
import com.example.data.CallLogEntity
import com.example.data.CallType
import com.example.ui.WhatsAppUiState
import com.example.ui.components.AvatarView
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTextPrimaryDark
import com.example.ui.theme.WhatsAppTextPrimaryLight
import com.example.ui.theme.WhatsAppTextSecondaryDark
import com.example.ui.theme.WhatsAppTextSecondaryLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CallsTab(
  uiState: WhatsAppUiState,
  onStartCall: (Long, String, AvatarType, CallType) -> Unit,
  onNewCallClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = isSystemInDarkTheme()
  val primaryColor = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight
  val secondaryColor = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
      // Create Call Link Section
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .background(WhatsAppLightGreen, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Link,
              contentDescription = "Create call link",
              tint = Color.White,
              modifier = Modifier.size(26.dp)
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Create call link",
              color = primaryColor,
              fontSize = 16.5.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Share a link for your Hey Chat call",
              color = secondaryColor,
              fontSize = 13.5.sp
            )
          }
        }
      }

      // Recent Calls Section Title
      item {
        Text(
          text = "Recent",
          color = primaryColor,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
      }

      // Call logs
      items(uiState.callLogs, key = { it.id }) { log ->
        val contact = uiState.contacts.find { it.id == log.contactId }
        val colorHex = contact?.colorHex ?: 0xFF008069

        CallItemRow(
          log = log,
          colorHex = colorHex,
          isDark = isDark,
          onCall = { onStartCall(log.contactId, log.contactName, log.avatarType, log.callType) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(100.dp))
      }
    }

    // FAB for starting a new call
    FloatingActionButton(
      onClick = onNewCallClick,
      containerColor = WhatsAppLightGreen,
      contentColor = Color.White,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("fab_new_call")
    ) {
      Icon(
        imageVector = Icons.Default.AddIcCall,
        contentDescription = "New call",
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
private fun CallItemRow(
  log: CallLogEntity,
  colorHex: Long,
  isDark: Boolean,
  onCall: () -> Unit
) {
  val primaryColor = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight
  val secondaryColor = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight

  val timeFormatter = remember { SimpleDateFormat("MMMM d, h:mm a", Locale.getDefault()) }
  val formattedTime = remember(log.timestamp) { timeFormatter.format(Date(log.timestamp)) }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCall() }
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AvatarView(
      avatarType = log.avatarType,
      name = log.contactName,
      colorHex = colorHex,
      size = 50.dp
    )

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = log.contactName,
        color = if (log.callDirection == CallDirection.MISSED) Color(0xFFE53935) else primaryColor,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        when (log.callDirection) {
          CallDirection.INCOMING -> {
            Icon(
              imageVector = Icons.Default.CallReceived,
              contentDescription = "Incoming call",
              tint = WhatsAppLightGreen,
              modifier = Modifier.size(15.dp)
            )
          }
          CallDirection.OUTGOING -> {
            Icon(
              imageVector = Icons.Default.CallMade,
              contentDescription = "Outgoing call",
              tint = WhatsAppLightGreen,
              modifier = Modifier.size(15.dp)
            )
          }
          CallDirection.MISSED -> {
            Icon(
              imageVector = Icons.Default.CallMissed,
              contentDescription = "Missed call",
              tint = Color(0xFFE53935),
              modifier = Modifier.size(15.dp)
            )
          }
        }

        Text(
          text = formattedTime,
          color = secondaryColor,
          fontSize = 13.sp
        )
      }
    }

    IconButton(
      onClick = onCall,
      modifier = Modifier.testTag("call_btn_${log.id}")
    ) {
      Icon(
        imageVector = if (log.callType == CallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
        contentDescription = if (log.callType == CallType.VIDEO) "Video call" else "Voice call",
        tint = WhatsAppLightGreen,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}
