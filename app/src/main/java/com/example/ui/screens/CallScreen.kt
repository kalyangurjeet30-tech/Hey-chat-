package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CallType
import com.example.ui.ActiveCallSession
import com.example.ui.components.AvatarView
import com.example.ui.theme.WhatsAppLightGreen
import kotlinx.coroutines.delay

@Composable
fun CallScreen(
  session: ActiveCallSession,
  onEndCall: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isMuted by remember { mutableStateOf(false) }
  var isSpeakerOn by remember { mutableStateOf(false) }
  var isVideoOff by remember { mutableStateOf(session.callType == CallType.VOICE) }
  var callDurationSec by remember { mutableStateOf(0) }
  var isConnected by remember { mutableStateOf(false) }

  // Simulated ringing then connected timer
  LaunchedEffect(Unit) {
    delay(2000)
    isConnected = true
    while (true) {
      delay(1000)
      callDurationSec++
    }
  }

  val minutes = callDurationSec / 60
  val seconds = callDurationSec % 60
  val timeString = String.format("%02d:%02d", minutes, seconds)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF0F1E24),
            Color(0xFF1B2A30),
            Color(0xFF0B141A)
          )
        )
      )
  ) {
    // Header & Contact Information
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(top = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = session.contactName,
        color = Color.White,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = if (isConnected) timeString else "Ringing...",
        color = if (isConnected) WhatsAppLightGreen else Color(0xBBFFFFFF),
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(60.dp))

      // Large Central Avatar
      AvatarView(
        avatarType = session.avatarType,
        name = session.contactName,
        colorHex = 0xFF008069,
        size = 140.dp
      )

      Spacer(modifier = Modifier.height(20.dp))

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x33000000)
      ) {
        Text(
          text = "🔒 End-to-end encrypted",
          color = Color(0x99FFFFFF),
          fontSize = 12.sp,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
      }
    }

    // Call Control Action Buttons
    Surface(
      shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
      color = Color(0xFF1F2C34),
      shadowElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .navigationBarsPadding()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Speaker button
          IconButton(
            onClick = { isSpeakerOn = !isSpeakerOn },
            modifier = Modifier
              .size(54.dp)
              .background(
                if (isSpeakerOn) Color.White else Color(0xFF2A3942),
                CircleShape
              )
          ) {
            Icon(
              imageVector = Icons.Default.VolumeUp,
              contentDescription = "Speaker",
              tint = if (isSpeakerOn) Color.Black else Color.White
            )
          }

          // Video toggle button
          if (session.callType == CallType.VIDEO) {
            IconButton(
              onClick = { isVideoOff = !isVideoOff },
              modifier = Modifier
                .size(54.dp)
                .background(
                  if (isVideoOff) Color.White else Color(0xFF2A3942),
                  CircleShape
                )
            ) {
              Icon(
                imageVector = if (isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                contentDescription = "Video toggle",
                tint = if (isVideoOff) Color.Black else Color.White
              )
            }
          }

          // Mute Mic button
          IconButton(
            onClick = { isMuted = !isMuted },
            modifier = Modifier
              .size(54.dp)
              .background(
                if (isMuted) Color.White else Color(0xFF2A3942),
                CircleShape
              )
          ) {
            Icon(
              imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
              contentDescription = "Mute mic",
              tint = if (isMuted) Color.Black else Color.White
            )
          }

          // End Call button
          IconButton(
            onClick = onEndCall,
            modifier = Modifier
              .size(60.dp)
              .background(Color(0xFFE53935), CircleShape)
              .testTag("end_call_btn")
          ) {
            Icon(
              imageVector = Icons.Default.CallEnd,
              contentDescription = "End call",
              tint = Color.White,
              modifier = Modifier.size(30.dp)
            )
          }
        }
      }
    }
  }
}
