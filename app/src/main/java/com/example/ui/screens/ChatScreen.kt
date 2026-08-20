package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AvatarType
import com.example.data.CallType
import com.example.data.ChatEntity
import com.example.data.ContactEntity
import com.example.data.MessageEntity
import com.example.data.MessageType
import com.example.ui.WhatsAppViewModel
import com.example.ui.components.AvatarView
import com.example.ui.components.MessageBubble
import com.example.ui.components.WhatsAppWallpaper
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTopBarDark
import com.example.ui.theme.WhatsAppTopBarLight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  chatId: Long,
  viewModel: WhatsAppViewModel,
  onBack: () -> Unit,
  onStartCall: (Long, String, AvatarType, CallType) -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = isSystemInDarkTheme()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val messages by viewModel.getMessagesForChat(chatId).collectAsStateWithLifecycle(initialValue = emptyList())
  val chat by viewModel.getChatById(chatId).collectAsStateWithLifecycle(initialValue = null)
  val contact = uiState.contacts.find { it.id == chat?.contactId }

  var messageInputText by remember { mutableStateOf("") }
  var showAttachmentSheet by remember { mutableStateOf(false) }
  var isRecordingVoice by remember { mutableStateOf(false) }
  var recordingSeconds by remember { mutableStateOf(0) }
  var showChatMenu by remember { mutableStateOf(false) }

  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  // Voice note timer simulation
  LaunchedEffect(isRecordingVoice) {
    if (isRecordingVoice) {
      recordingSeconds = 0
      while (isRecordingVoice) {
        kotlinx.coroutines.delay(1000)
        recordingSeconds++
      }
    }
  }

  val selectedMsg = uiState.selectedMessageForActions

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(if (isDark) Color(0xFF0B141A) else Color(0xFFEFEAE2))
  ) {
    // Custom Chat Wallpaper Pattern Background
    WhatsAppWallpaper(isDarkTheme = isDark)

    Column(modifier = Modifier.fillMaxSize()) {
      // Top App Bar
      if (selectedMsg != null) {
        // Contextual Action Top Bar for selected message
        MessageActionTopBar(
          message = selectedMsg,
          onClose = { viewModel.selectMessageForActions(null) },
          onReply = { viewModel.setReplyToMessage(selectedMsg) },
          onStar = { viewModel.toggleStarMessage(selectedMsg.id) },
          onDelete = { viewModel.deleteMessage(selectedMsg.id) },
          onReact = { reaction -> viewModel.addReactionToMessage(selectedMsg.id, reaction) },
          isDark = isDark
        )
      } else {
        ChatTopBar(
          chat = chat,
          contact = contact,
          onBack = onBack,
          onVoiceCall = {
            chat?.let {
              onStartCall(
                it.contactId,
                it.name,
                contact?.avatarType ?: AvatarType.DEFAULT,
                CallType.VOICE
              )
            }
          },
          onVideoCall = {
            chat?.let {
              onStartCall(
                it.contactId,
                it.name,
                contact?.avatarType ?: AvatarType.DEFAULT,
                CallType.VIDEO
              )
            }
          },
          showMenu = showChatMenu,
          onMenuToggle = { showChatMenu = it },
          onDeleteChat = { viewModel.deleteChat(chatId) },
          isDark = isDark
        )
      }

      // Message List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        item {
          // Encryption Banner
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isDark) Color(0xFF182229) else Color(0xFFFEEDC9),
              shadowElevation = 1.dp
            ) {
              Text(
                text = "🔒 Messages and calls are end-to-end encrypted. No one outside of this chat, not even Hey Chat, can read or listen to them.",
                color = if (isDark) Color(0xFFFFD279) else Color(0xFF5E5448),
                fontSize = 11.5.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                  .widthIn(max = 320.dp)
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }

        items(messages, key = { it.id }) { msg ->
          MessageBubble(
            message = msg,
            isFromMe = msg.senderId == 0L,
            onLongClick = { viewModel.selectMessageForActions(msg) },
            onReplyClick = { viewModel.setReplyToMessage(msg) },
            onReactionClick = { reaction ->
              viewModel.addReactionToMessage(msg.id, reaction)
            }
          )
        }

        item {
          Spacer(modifier = Modifier.height(6.dp))
        }
      }

      // Quoted Reply Preview Bar
      uiState.replyingToMessage?.let { replyMsg ->
        ReplyPreviewBar(
          message = replyMsg,
          onClose = { viewModel.setReplyToMessage(null) },
          isDark = isDark
        )
      }

      // Bottom Message Input & Recording Bar
      ChatBottomInputBar(
        inputText = messageInputText,
        onInputTextChange = { messageInputText = it },
        isRecording = isRecordingVoice,
        recordingSeconds = recordingSeconds,
        onStartRecording = { isRecordingVoice = true },
        onCancelRecording = { isRecordingVoice = false },
        onSendVoiceNote = {
          viewModel.sendMessage(
            chatId = chatId,
            text = "Voice message",
            type = MessageType.VOICE_NOTE,
            audioDurationSec = recordingSeconds.coerceAtLeast(1)
          )
          isRecordingVoice = false
        },
        onSendText = {
          if (messageInputText.isNotBlank()) {
            viewModel.sendMessage(chatId = chatId, text = messageInputText.trim())
            messageInputText = ""
          }
        },
        onAttachmentClick = { showAttachmentSheet = true },
        isDark = isDark
      )
    }

    // Attachment Modal Sheet
    if (showAttachmentSheet) {
      AttachmentBottomSheet(
        onDismiss = { showAttachmentSheet = false },
        onAttach = { type, caption ->
          showAttachmentSheet = false
          viewModel.sendMessage(
            chatId = chatId,
            text = caption,
            type = type,
            mediaCaption = caption
          )
        }
      )
    }
  }
}

@Composable
private fun ChatTopBar(
  chat: ChatEntity?,
  contact: ContactEntity?,
  onBack: () -> Unit,
  onVoiceCall: () -> Unit,
  onVideoCall: () -> Unit,
  showMenu: Boolean,
  onMenuToggle: (Boolean) -> Unit,
  onDeleteChat: () -> Unit,
  isDark: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(if (isDark) WhatsAppTopBarDark else WhatsAppTopBarLight)
      .statusBarsPadding()
      .padding(horizontal = 4.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(
      onClick = onBack,
      modifier = Modifier.testTag("chat_back_btn")
    ) {
      Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = "Back",
        tint = Color.White
      )
    }

    AvatarView(
      avatarType = if (chat?.isGroup == true) AvatarType.GROUP_TECH else contact?.avatarType ?: AvatarType.DEFAULT,
      name = chat?.name ?: "Chat",
      colorHex = contact?.colorHex ?: 0xFF008069,
      size = 38.dp,
      showOnlineBadge = chat?.isGroup == false,
      isOnline = contact?.isOnline ?: false
    )

    Spacer(modifier = Modifier.width(10.dp))

    Column(
      modifier = Modifier
        .weight(1f)
        .clickable { }
    ) {
      Text(
        text = chat?.name ?: "Chat",
        color = Color.White,
        fontSize = 16.5.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      val subtitle = when {
        chat?.isGroup == true -> "Tap here for group info"
        contact?.isOnline == true -> "online"
        else -> contact?.lastSeenText ?: "tap for contact info"
      }

      Text(
        text = subtitle,
        color = if (contact?.isOnline == true) WhatsAppLightGreen else Color(0xCCFFFFFF),
        fontSize = 12.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    IconButton(
      onClick = onVideoCall,
      modifier = Modifier.testTag("chat_video_call_btn")
    ) {
      Icon(
        imageVector = Icons.Default.Videocam,
        contentDescription = "Video Call",
        tint = Color.White
      )
    }

    IconButton(
      onClick = onVoiceCall,
      modifier = Modifier.testTag("chat_voice_call_btn")
    ) {
      Icon(
        imageVector = Icons.Default.Call,
        contentDescription = "Voice Call",
        tint = Color.White
      )
    }

    Box {
      IconButton(onClick = { onMenuToggle(true) }) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Chat options",
          tint = Color.White
        )
      }

      DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { onMenuToggle(false) }
      ) {
        DropdownMenuItem(
          text = { Text("View contact") },
          onClick = { onMenuToggle(false) }
        )
        DropdownMenuItem(
          text = { Text("Media, links, and docs") },
          onClick = { onMenuToggle(false) }
        )
        DropdownMenuItem(
          text = { Text("Search") },
          onClick = { onMenuToggle(false) }
        )
        DropdownMenuItem(
          text = { Text("Mute notifications") },
          onClick = { onMenuToggle(false) }
        )
        DropdownMenuItem(
          text = { Text("Clear chat", color = Color.Red) },
          onClick = {
            onMenuToggle(false)
            onDeleteChat()
          }
        )
      }
    }
  }
}

@Composable
private fun MessageActionTopBar(
  message: MessageEntity,
  onClose: () -> Unit,
  onReply: () -> Unit,
  onStar: () -> Unit,
  onDelete: () -> Unit,
  onReact: (String) -> Unit,
  isDark: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(if (isDark) Color(0xFF1F2C34) else WhatsAppEmerald)
      .statusBarsPadding()
      .padding(horizontal = 4.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(onClick = onClose) {
      Icon(
        imageVector = Icons.Default.ArrowBack,
        contentDescription = "Close action bar",
        tint = Color.White
      )
    }

    // Reaction quick picker emojis
    Row(
      modifier = Modifier.weight(1f),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      listOf("❤️", "👍", "😂", "😮", "😢", "🙏").forEach { emoji ->
        Text(
          text = emoji,
          fontSize = 20.sp,
          modifier = Modifier
            .clip(CircleShape)
            .clickable { onReact(emoji) }
            .padding(4.dp)
        )
      }
    }

    IconButton(onClick = onReply) {
      Icon(
        imageVector = Icons.Default.Reply,
        contentDescription = "Reply",
        tint = Color.White
      )
    }

    IconButton(onClick = onStar) {
      Icon(
        imageVector = if (message.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
        contentDescription = "Star message",
        tint = Color.White
      )
    }

    IconButton(onClick = onDelete) {
      Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = "Delete message",
        tint = Color.White
      )
    }
  }
}

@Composable
private fun ReplyPreviewBar(
  message: MessageEntity,
  onClose: () -> Unit,
  isDark: Boolean
) {
  Surface(
    color = if (isDark) Color(0xFF1F2C34) else Color(0xFFF0F2F5),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .width(4.dp)
          .height(36.dp)
          .background(WhatsAppEmerald, RoundedCornerShape(2.dp))
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = message.senderName.ifEmpty { "Message" },
          color = WhatsAppEmerald,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = message.text.ifEmpty { message.mediaCaption },
          color = if (isDark) Color(0xFF8696A0) else Color(0xFF667781),
          fontSize = 13.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
      IconButton(onClick = onClose) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Cancel reply",
          tint = if (isDark) Color.White else Color.Black
        )
      }
    }
  }
}

@Composable
private fun ChatBottomInputBar(
  inputText: String,
  onInputTextChange: (String) -> Unit,
  isRecording: Boolean,
  recordingSeconds: Int,
  onStartRecording: () -> Unit,
  onCancelRecording: () -> Unit,
  onSendVoiceNote: () -> Unit,
  onSendText: () -> Unit,
  onAttachmentClick: () -> Unit,
  isDark: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 6.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (isRecording) {
      // Voice Note Recording In-Progress Bar
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (isDark) Color(0xFF1F2C34) else Color(0xFFFFFFFF),
        shadowElevation = 2.dp,
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .background(Color.Red, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "0:${String.format("%02d", recordingSeconds)}",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = if (isDark) Color.White else Color.Black
            )
          }

          Text(
            text = "Cancel",
            color = Color.Red,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onCancelRecording() }
          )
        }
      }

      Spacer(modifier = Modifier.width(6.dp))

      // Send Voice Note Button
      Surface(
        shape = CircleShape,
        color = WhatsAppLightGreen,
        shadowElevation = 2.dp,
        modifier = Modifier.size(48.dp)
      ) {
        IconButton(
          onClick = onSendVoiceNote,
          modifier = Modifier.testTag("send_voice_note_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Send,
            contentDescription = "Send voice note",
            tint = Color.White
          )
        }
      }
    } else {
      // Standard Text Field Input Bar
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (isDark) Color(0xFF1F2C34) else Color(0xFFFFFFFF),
        shadowElevation = 2.dp,
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = { }) {
            Icon(
              imageVector = Icons.Default.SentimentSatisfied,
              contentDescription = "Emoji picker",
              tint = if (isDark) Color(0xFF8696A0) else Color(0xFF667781)
            )
          }

          OutlinedTextField(
            value = inputText,
            onValueChange = onInputTextChange,
            placeholder = {
              Text(
                text = "Message",
                color = if (isDark) Color(0xFF8696A0) else Color(0xFF667781),
                fontSize = 16.sp
              )
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent,
              focusedTextColor = if (isDark) Color.White else Color.Black,
              unfocusedTextColor = if (isDark) Color.White else Color.Black
            ),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("chat_input_field")
          )

          IconButton(
            onClick = onAttachmentClick,
            modifier = Modifier.testTag("chat_attach_btn")
          ) {
            Icon(
              imageVector = Icons.Default.AttachFile,
              contentDescription = "Attach file",
              tint = if (isDark) Color(0xFF8696A0) else Color(0xFF667781)
            )
          }

          if (inputText.isEmpty()) {
            IconButton(onClick = { }) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Camera",
                tint = if (isDark) Color(0xFF8696A0) else Color(0xFF667781)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.width(6.dp))

      // Send / Mic Action Button
      Surface(
        shape = CircleShape,
        color = WhatsAppLightGreen,
        shadowElevation = 2.dp,
        modifier = Modifier.size(48.dp)
      ) {
        IconButton(
          onClick = {
            if (inputText.isNotBlank()) {
              onSendText()
            } else {
              onStartRecording()
            }
          },
          modifier = Modifier.testTag("chat_send_or_mic_btn")
        ) {
          Icon(
            imageVector = if (inputText.isNotBlank()) Icons.Default.Send else Icons.Default.Mic,
            contentDescription = if (inputText.isNotBlank()) "Send message" else "Voice record",
            tint = Color.White
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttachmentBottomSheet(
  onDismiss: () -> Unit,
  onAttach: (MessageType, String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        AttachmentOptionItem(
          icon = Icons.Default.Description,
          title = "Document",
          bgColor = Color(0xFF7F66FF),
          onClick = { onAttach(MessageType.DOCUMENT, "Project_Spec_2026.pdf") }
        )
        AttachmentOptionItem(
          icon = Icons.Default.CameraAlt,
          title = "Camera",
          bgColor = Color(0xFFE91E63),
          onClick = { onAttach(MessageType.IMAGE, "Captured photo") }
        )
        AttachmentOptionItem(
          icon = Icons.Default.Image,
          title = "Gallery",
          bgColor = Color(0xFF9C27B0),
          onClick = { onAttach(MessageType.IMAGE, "Shared an image from gallery") }
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        AttachmentOptionItem(
          icon = Icons.Default.Headphones,
          title = "Audio",
          bgColor = Color(0xFFFF9800),
          onClick = { onAttach(MessageType.VOICE_NOTE, "Audio clip") }
        )
        AttachmentOptionItem(
          icon = Icons.Default.LocationOn,
          title = "Location",
          bgColor = Color(0xFF00C853),
          onClick = { onAttach(MessageType.LOCATION, "Live Location: Innovation Hub") }
        )
        AttachmentOptionItem(
          icon = Icons.Default.Person,
          title = "Contact",
          bgColor = Color(0xFF0091EA),
          onClick = { onAttach(MessageType.CONTACT, "Shared Contact Card") }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun AttachmentOptionItem(
  icon: ImageVector,
  title: String,
  bgColor: Color,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .size(56.dp)
        .background(bgColor, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = Color.White,
        modifier = Modifier.size(28.dp)
      )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = title,
      fontSize = 12.5.sp,
      fontWeight = FontWeight.Medium
    )
  }
}
