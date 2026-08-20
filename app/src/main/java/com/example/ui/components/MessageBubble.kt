package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MessageEntity
import com.example.data.MessageStatus
import com.example.data.MessageType
import com.example.ui.theme.WhatsAppCheckBlue
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppIncomingBubbleDark
import com.example.ui.theme.WhatsAppIncomingBubbleLight
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppOutgoingBubbleDark
import com.example.ui.theme.WhatsAppOutgoingBubbleLight
import com.example.ui.theme.WhatsAppTextPrimaryDark
import com.example.ui.theme.WhatsAppTextPrimaryLight
import com.example.ui.theme.WhatsAppTextSecondaryDark
import com.example.ui.theme.WhatsAppTextSecondaryLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageBubble(
  message: MessageEntity,
  isFromMe: Boolean,
  onLongClick: () -> Unit,
  onReplyClick: () -> Unit,
  onReactionClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = isSystemInDarkTheme()

  val bubbleColor = when {
    isFromMe && isDark -> WhatsAppOutgoingBubbleDark
    isFromMe && !isDark -> WhatsAppOutgoingBubbleLight
    !isFromMe && isDark -> WhatsAppIncomingBubbleDark
    else -> WhatsAppIncomingBubbleLight
  }

  val textColor = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight
  val secondaryTextColor = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight

  val shape = if (isFromMe) {
    RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
  } else {
    RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
  }

  val timeFormatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
  val formattedTime = remember(message.timestamp) { timeFormatter.format(Date(message.timestamp)) }

  var isAudioPlaying by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 3.dp),
    contentAlignment = if (isFromMe) Alignment.CenterEnd else Alignment.CenterStart
  ) {
    Column(horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start) {
      Surface(
        color = bubbleColor,
        shape = shape,
        shadowElevation = 1.dp,
        modifier = Modifier
          .widthIn(min = 80.dp, max = 300.dp)
          .pointerInput(message.id) {
            detectTapGestures(
              onLongPress = { onLongClick() },
              onDoubleTap = { onReactionClick("❤️") }
            )
          }
          .testTag("message_bubble_${message.id}")
      ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
          // Quoted Reply Preview
          if (!message.replyToText.isNullOrEmpty()) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isDark) Color(0x33FFFFFF) else Color(0x18000000),
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .clickable { onReplyClick() }
            ) {
              Row(modifier = Modifier.padding(6.dp)) {
                Box(
                  modifier = Modifier
                    .width(3.dp)
                    .height(28.dp)
                    .background(WhatsAppEmerald, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                  Text(
                    text = message.replyToSenderName ?: "Reply",
                    color = WhatsAppEmerald,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = message.replyToText,
                    color = secondaryTextColor,
                    fontSize = 12.sp,
                    maxLines = 1
                  )
                }
              }
            }
          }

          // Content rendering
          when (message.messageType) {
            MessageType.TEXT -> {
              Text(
                text = message.text,
                color = textColor,
                fontSize = 15.sp,
                lineHeight = 20.sp
              )
            }
            MessageType.IMAGE -> {
              Column {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2A3942)),
                  contentAlignment = Alignment.Center
                ) {
                  Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Media image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(90.dp)
                  )
                }
                if (message.mediaCaption.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = message.mediaCaption,
                    color = textColor,
                    fontSize = 14.5.sp
                  )
                }
              }
            }
            MessageType.VOICE_NOTE -> {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
              ) {
                Surface(
                  shape = CircleShape,
                  color = WhatsAppLightGreen,
                  modifier = Modifier.size(38.dp)
                ) {
                  IconButton(onClick = { isAudioPlaying = !isAudioPlaying }) {
                    Icon(
                      imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                      contentDescription = if (isAudioPlaying) "Pause" else "Play",
                      tint = Color.White,
                      modifier = Modifier.size(22.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Simulated Audio Waveform
                Row(
                  modifier = Modifier.weight(1f),
                  horizontalArrangement = Arrangement.spacedBy(2.5.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  val heights = listOf(8, 16, 24, 12, 20, 28, 14, 22, 10, 18, 26, 12, 16, 8)
                  heights.forEach { h ->
                    Box(
                      modifier = Modifier
                        .width(3.dp)
                        .height(h.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isAudioPlaying) WhatsAppLightGreen else secondaryTextColor)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                  text = "0:${String.format("%02d", message.audioDurationSec)}",
                  color = secondaryTextColor,
                  fontSize = 11.5.sp
                )
              }
            }
            MessageType.DOCUMENT -> {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE53935)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Document",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = message.mediaCaption.ifEmpty { "Document.pdf" },
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
            else -> {
              Text(
                text = message.text,
                color = textColor,
                fontSize = 15.sp
              )
            }
          }

          // Timestamp + Double Checkmarks + Star
          Row(
            modifier = Modifier
              .align(Alignment.End)
              .padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            if (message.isStarred) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Starred",
                tint = secondaryTextColor,
                modifier = Modifier.size(12.dp)
              )
            }

            Text(
              text = formattedTime,
              color = secondaryTextColor,
              fontSize = 11.sp
            )

            if (isFromMe) {
              when (message.status) {
                MessageStatus.READ -> {
                  Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Read",
                    tint = WhatsAppCheckBlue,
                    modifier = Modifier.size(15.dp)
                  )
                }
                MessageStatus.DELIVERED -> {
                  Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Delivered",
                    tint = secondaryTextColor,
                    modifier = Modifier.size(15.dp)
                  )
                }
                else -> {
                  Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Sent",
                    tint = secondaryTextColor,
                    modifier = Modifier.size(15.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Reaction Badge Bubble (e.g. ❤️, 👍)
      if (message.reaction.isNotEmpty()) {
        Surface(
          shape = CircleShape,
          color = if (isDark) Color(0xFF222E35) else Color(0xFFFFFFFF),
          shadowElevation = 2.dp,
          modifier = Modifier
            .offset(y = (-8).dp, x = if (isFromMe) (-4).dp else 4.dp)
            .clickable { onReactionClick("") }
        ) {
          Text(
            text = message.reaction,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}
