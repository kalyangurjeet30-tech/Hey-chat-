package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AvatarType
import com.example.data.StatusEntity
import com.example.ui.components.AvatarView
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppLightGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatusViewerScreen(
  status: StatusEntity,
  onClose: () -> Unit,
  onReply: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val progress = remember { Animatable(0f) }
  var isPaused by remember { mutableStateOf(false) }
  var replyText by remember { mutableStateOf("") }

  val timeFormatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
  val formattedTime = remember(status.timestamp) { timeFormatter.format(Date(status.timestamp)) }

  LaunchedEffect(isPaused) {
    if (!isPaused) {
      progress.animateTo(
        targetValue = 1f,
        animationSpec = tween(
          durationMillis = ((1f - progress.value) * 5000).toInt(),
          easing = LinearEasing
        )
      )
      if (progress.value >= 1f) {
        onClose()
      }
    } else {
      progress.stop()
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
      .pointerInput(Unit) {
        detectTapGestures(
          onPress = {
            isPaused = true
            tryAwaitRelease()
            isPaused = false
          },
          onTap = { offset ->
            if (offset.x > size.width / 2) {
              onClose()
            } else {
              onClose()
            }
          }
        )
      }
  ) {
    // Status Story Content
    if (status.textStatus.isNotEmpty()) {
      // Text status with rich gradient backdrop
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(
                Color(status.mediaBgColorHex),
                Color(status.mediaBgColorHex).copy(alpha = 0.7f),
                Color.Black
              )
            )
          )
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = status.textStatus,
          color = Color.White,
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
          textAlign = TextAlign.Center,
          lineHeight = 36.sp
        )
      }
    } else {
      // Photo / Media status
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(
                Color(status.mediaBgColorHex),
                Color(0xFF1F2C34),
                Color.Black
              )
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Status photo",
            modifier = Modifier.size(160.dp)
          )
          if (status.caption.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(0x88000000)
            ) {
              Text(
                text = status.caption,
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              )
            }
          }
        }
      }
    }

    // Top Progress Bars & Header Controls
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      // Animated Progress Bar
      LinearProgressIndicator(
        progress = { progress.value },
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .clip(RoundedCornerShape(2.dp)),
        color = Color.White,
        trackColor = Color(0x55FFFFFF)
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onClose,
          modifier = Modifier.testTag("status_close_btn")
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
          )
        }

        AvatarView(
          avatarType = status.avatarType,
          name = status.contactName,
          colorHex = status.mediaBgColorHex,
          size = 40.dp
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = status.contactName,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Today at $formattedTime",
            color = Color(0xDDFFFFFF),
            fontSize = 12.sp
          )
        }

        IconButton(onClick = { }) {
          Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More",
            tint = Color.White
          )
        }
      }
    }

    // Bottom Reply Bar (if not My Status)
    if (!status.isMyStatus) {
      Column(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .navigationBarsPadding()
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        // Quick reaction emojis
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          listOf("😍", "😂", "😮", "😢", "🙏", "👏", "🎉", "💯").forEach { emoji ->
            Text(
              text = emoji,
              fontSize = 24.sp,
              modifier = Modifier
                .clip(CircleShape)
                .clickable {
                  onReply(emoji)
                  onClose()
                }
                .padding(4.dp)
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0x66000000),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFFFFF)),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
              contentAlignment = Alignment.CenterStart
            ) {
              BasicTextField(
                value = replyText,
                onValueChange = { replyText = it },
                textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                cursorBrush = SolidColor(Color.White),
                decorationBox = { innerTextField ->
                  if (replyText.isEmpty()) {
                    Text("Reply to ${status.contactName}...", color = Color(0xAAFFFFFF), fontSize = 15.sp)
                  }
                  innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
              )
            }
          }

          if (replyText.isNotBlank()) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = CircleShape,
              color = WhatsAppLightGreen,
              modifier = Modifier.size(44.dp)
            ) {
              IconButton(onClick = {
                onReply(replyText)
                replyText = ""
                onClose()
              }) {
                Icon(
                  imageVector = Icons.Default.Send,
                  contentDescription = "Send reply",
                  tint = Color.White
                )
              }
            }
          }
        }
      }
    }
  }
}
