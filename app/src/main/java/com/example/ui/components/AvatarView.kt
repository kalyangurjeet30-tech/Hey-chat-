package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AvatarType
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppLightGreen

@Composable
fun AvatarView(
  avatarType: AvatarType,
  name: String,
  colorHex: Long,
  size: Dp = 48.dp,
  showOnlineBadge: Boolean = false,
  isOnline: Boolean = false,
  hasStatusStory: Boolean = false,
  statusViewed: Boolean = false,
  modifier: Modifier = Modifier
) {
  val initials = name.trim().split(" ")
    .mapNotNull { it.firstOrNull()?.toString() }
    .take(2)
    .joinToString("")
    .uppercase()

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    // Status Ring if user has active status stories
    if (hasStatusStory) {
      val ringBrush = if (statusViewed) {
        Brush.sweepGradient(listOf(Color(0xFF8696A0), Color(0xFF8696A0)))
      } else {
        Brush.sweepGradient(listOf(WhatsAppLightGreen, WhatsAppEmerald, WhatsAppLightGreen))
      }
      Box(
        modifier = Modifier
          .size(size)
          .border(2.5.dp, ringBrush, CircleShape)
          .padding(2.5.dp)
      )
    }

    // Avatar Inner Surface
    Box(
      modifier = Modifier
        .size(if (hasStatusStory) size - 5.dp else size)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(
            listOf(
              Color(colorHex),
              Color(colorHex).copy(alpha = 0.8f)
            )
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      when (avatarType) {
        AvatarType.SARAH, AvatarType.ALEX -> {
          // If custom generated avatars exist or initials
          if (initials.isNotEmpty()) {
            Text(
              text = initials,
              color = Color.White,
              fontSize = (size.value * 0.38f).sp,
              fontWeight = FontWeight.Bold
            )
          } else {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = name,
              tint = Color.White,
              modifier = Modifier.size(size * 0.58f)
            )
          }
        }
        AvatarType.GROUP_TECH, AvatarType.GROUP_FAMILY -> {
          Icon(
            imageVector = Icons.Default.Group,
            contentDescription = name,
            tint = Color.White,
            modifier = Modifier.size(size * 0.58f)
          )
        }
        else -> {
          if (initials.isNotEmpty()) {
            Text(
              text = initials,
              color = Color.White,
              fontSize = (size.value * 0.38f).sp,
              fontWeight = FontWeight.Bold
            )
          } else {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = name,
              tint = Color.White,
              modifier = Modifier.size(size * 0.58f)
            )
          }
        }
      }
    }

    // Online Green Indicator Badge
    if (showOnlineBadge && isOnline) {
      val badgeSize = (size.value * 0.26f).coerceAtLeast(10f).dp
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .offset(x = 1.dp, y = 1.dp)
          .size(badgeSize)
          .background(WhatsAppLightGreen, CircleShape)
          .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
      )
    }
  }
}
