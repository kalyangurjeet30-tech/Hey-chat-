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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WhatsAppUiState
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTextPrimaryDark
import com.example.ui.theme.WhatsAppTextPrimaryLight
import com.example.ui.theme.WhatsAppTextSecondaryDark
import com.example.ui.theme.WhatsAppTextSecondaryLight

@Composable
fun CommunitiesTab(
  uiState: WhatsAppUiState,
  onGroupClick: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = isSystemInDarkTheme()
  val primaryColor = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight
  val secondaryColor = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight

  LazyColumn(
    modifier = modifier.fillMaxSize()
  ) {
    // New Community Action Banner
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
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDark) Color(0xFF202C33) else Color(0xFFE9EDEF)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Groups,
            contentDescription = "New community",
            tint = if (isDark) Color.White else Color(0xFF54656F),
            modifier = Modifier.size(28.dp)
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
          text = "New community",
          color = primaryColor,
          fontSize = 16.5.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      HorizontalDivider(
        color = if (isDark) Color(0xFF202C33) else Color(0xFFE9EDEF),
        thickness = 8.dp
      )
    }

    // Community 1: Android Dev Community
    item {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF3DDC84)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Groups,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(28.dp)
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          Text(
            text = "Android Developers Global",
            color = primaryColor,
            fontSize = 16.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
          )
        }

        HorizontalDivider(
          color = if (isDark) Color(0xFF202C33) else Color(0xFFE9EDEF),
          modifier = Modifier.padding(start = 80.dp)
        )

        // Subgroups in this community
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onGroupClick(2) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isDark) Color(0xFF103629) else Color(0xFFE7FCE8)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Campaign,
              contentDescription = "Announcements",
              tint = WhatsAppLightGreen,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Announcements",
              color = primaryColor,
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "~ Alex: Welcome to the Jetpack Compose sprint!",
              color = secondaryColor,
              fontSize = 13.sp,
              maxLines = 1
            )
          }

          Text(
            text = "10:30 AM",
            color = secondaryColor,
            fontSize = 12.sp
          )
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onGroupClick(2) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF00897B)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "EL",
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Android Devs Elite 🚀",
              color = primaryColor,
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "Alex: Check out this new Compose gesture animation demo!",
              color = secondaryColor,
              fontSize = 13.sp,
              maxLines = 1
            )
          }

          Text(
            text = "11:15 AM",
            color = secondaryColor,
            fontSize = 12.sp
          )
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = secondaryColor
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = "View all (4 groups)",
            color = secondaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      HorizontalDivider(
        color = if (isDark) Color(0xFF202C33) else Color(0xFFE9EDEF),
        thickness = 8.dp
      )
    }

    item {
      Spacer(modifier = Modifier.height(100.dp))
    }
  }
}
