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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AvatarType
import com.example.data.StatusEntity
import com.example.ui.WhatsAppUiState
import com.example.ui.components.AvatarView
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTextPrimaryDark
import com.example.ui.theme.WhatsAppTextPrimaryLight
import com.example.ui.theme.WhatsAppTextSecondaryDark
import com.example.ui.theme.WhatsAppTextSecondaryLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UpdatesTab(
  uiState: WhatsAppUiState,
  onStatusClick: (Long) -> Unit,
  onAddStatusClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = isSystemInDarkTheme()
  val myStatus = uiState.statuses.find { it.isMyStatus }
  val recentStatuses = uiState.statuses.filter { !it.isMyStatus && !it.isViewed }
  val viewedStatuses = uiState.statuses.filter { !it.isMyStatus && it.isViewed }

  val primaryColor = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight
  val secondaryColor = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier.fillMaxSize()
    ) {
      // Header: Status
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Status",
            color = primaryColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
          )

          IconButton(onClick = { }) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "Status options",
              tint = secondaryColor
            )
          }
        }
      }

      // My Status Row
      item {
        MyStatusRow(
          myStatus = myStatus,
          onAddStatus = onAddStatusClick,
          onViewStatus = { myStatus?.id?.let(onStatusClick) },
          isDark = isDark
        )
      }

      // Recent Updates Section
      if (recentStatuses.isNotEmpty()) {
        item {
          Text(
            text = "Recent updates",
            color = secondaryColor,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }

        items(recentStatuses, key = { it.id }) { status ->
          StatusItemRow(
            status = status,
            isDark = isDark,
            onClick = { onStatusClick(status.id) }
          )
        }
      }

      // Viewed Updates Section
      if (viewedStatuses.isNotEmpty()) {
        item {
          Text(
            text = "Viewed updates",
            color = secondaryColor,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
          )
        }

        items(viewedStatuses, key = { it.id }) { status ->
          StatusItemRow(
            status = status,
            isDark = isDark,
            onClick = { onStatusClick(status.id) }
          )
        }
      }

      // Channels Section Header
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Channels",
              color = primaryColor,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Stay updated on topics you care about",
              color = secondaryColor,
              fontSize = 13.sp
            )
          }
        }
      }

      // Channels Directory Carousel
      item {
        val sampleChannels = listOf(
          Triple("WhatsApp Official", "Official news and announcements", 0xFF00A884),
          Triple("Android Developers", "Tips, releases, and guides for Kotlin", 0xFF3DDC84),
          Triple("Google AI", "Gemini, Android AI and breakthroughs", 0xFF4285F4),
          Triple("Tech Insider", "Daily tech highlights & gadgets", 0xFFFF6D00)
        )

        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(sampleChannels) { channel ->
            ChannelCard(
              title = channel.first,
              desc = channel.second,
              colorHex = channel.third,
              isDark = isDark
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(100.dp))
      }
    }

    // Stacked FABs for Text Status and Camera Status
    Column(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      SmallFloatingActionButton(
        onClick = onAddStatusClick,
        containerColor = if (isDark) Color(0xFF202C33) else Color(0xFFF0F2F5),
        contentColor = if (isDark) Color.White else Color(0xFF111B21),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("fab_text_status")
      ) {
        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = "Text status",
          modifier = Modifier.size(18.dp)
        )
      }

      FloatingActionButton(
        onClick = onAddStatusClick,
        containerColor = WhatsAppLightGreen,
        contentColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("fab_camera_status")
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = "Camera status",
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}

@Composable
private fun MyStatusRow(
  myStatus: StatusEntity?,
  onAddStatus: () -> Unit,
  onViewStatus: () -> Unit,
  isDark: Boolean
) {
  val primaryColor = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight
  val secondaryColor = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable {
        if (myStatus != null) onViewStatus() else onAddStatus()
      }
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(contentAlignment = Alignment.BottomEnd) {
      AvatarView(
        avatarType = AvatarType.DEFAULT,
        name = "My Status",
        colorHex = 0xFF008069,
        size = 52.dp,
        hasStatusStory = myStatus != null,
        statusViewed = false
      )

      if (myStatus == null) {
        Box(
          modifier = Modifier
            .size(20.dp)
            .background(WhatsAppLightGreen, CircleShape)
            .padding(2.dp),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add status",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = "My status",
        color = primaryColor,
        fontSize = 16.5.sp,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = if (myStatus != null) "Tap to view update" else "Tap to add status update",
        color = secondaryColor,
        fontSize = 13.5.sp
      )
    }
  }
}

@Composable
private fun StatusItemRow(
  status: StatusEntity,
  isDark: Boolean,
  onClick: () -> Unit
) {
  val primaryColor = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight
  val secondaryColor = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight

  val timeFormatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
  val formattedTime = remember(status.timestamp) { timeFormatter.format(Date(status.timestamp)) }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AvatarView(
      avatarType = status.avatarType,
      name = status.contactName,
      colorHex = status.mediaBgColorHex,
      size = 52.dp,
      hasStatusStory = true,
      statusViewed = status.isViewed
    )

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = status.contactName,
        color = primaryColor,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = "Today at $formattedTime",
        color = secondaryColor,
        fontSize = 13.sp
      )
    }
  }
}

@Composable
private fun ChannelCard(
  title: String,
  desc: String,
  colorHex: Long,
  isDark: Boolean
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = if (isDark) Color(0xFF1F2C34) else Color(0xFFF7F8FA),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isDark) Color(0xFF2B3A42) else Color(0xFFE9EDEF)
    ),
    modifier = Modifier
      .width(150.dp)
      .padding(vertical = 4.dp)
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(CircleShape)
          .background(Color(colorHex)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = title.take(1),
          color = Color.White,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = title,
          color = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(3.dp))
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Verified",
          tint = WhatsAppEmerald,
          modifier = Modifier.size(14.dp)
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = desc,
        color = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight,
        fontSize = 11.5.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = { },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isDark) Color(0xFF103629) else Color(0xFFE7FCE8),
          contentColor = WhatsAppLightGreen
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(32.dp)
      ) {
        Text(
          text = "Follow",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
