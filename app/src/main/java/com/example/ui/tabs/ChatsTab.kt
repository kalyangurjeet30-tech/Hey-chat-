package com.example.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AvatarType
import com.example.data.ChatEntity
import com.example.data.MessageStatus
import com.example.ui.ChatFilter
import com.example.ui.WhatsAppUiState
import com.example.ui.components.AvatarView
import com.example.ui.theme.WhatsAppCheckBlue
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
fun ChatsTab(
  uiState: WhatsAppUiState,
  onChatClick: (Long) -> Unit,
  onFilterSelected: (ChatFilter) -> Unit,
  onTogglePin: (Long) -> Unit,
  onToggleMute: (Long) -> Unit,
  onToggleArchive: (Long) -> Unit,
  onDeleteChat: (Long) -> Unit,
  onNewChatClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = isSystemInDarkTheme()
  var selectedChatForMenu by remember { mutableStateOf<ChatEntity?>(null) }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier.fillMaxSize()
    ) {
      // Filter Chips Row
      item {
        FilterChipsRow(
          activeFilter = uiState.chatFilter,
          onFilterSelected = onFilterSelected,
          isDark = isDark
        )
      }

      // Archived Section banner if any archived chats exist
      if (uiState.archivedChats.isNotEmpty()) {
        item {
          ArchivedBanner(
            count = uiState.archivedChats.size,
            isDark = isDark,
            onClick = { }
          )
        }
      }

      // Empty State
      if (uiState.activeChats.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 60.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = null,
                tint = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight,
                modifier = Modifier.size(54.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "No chats found",
                color = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      // Chat List Items
      items(
        items = uiState.activeChats,
        key = { it.id }
      ) { chat ->
        val contact = uiState.contacts.find { it.id == chat.contactId }
        val avatarType = if (chat.isGroup) AvatarType.GROUP_TECH else contact?.avatarType ?: AvatarType.DEFAULT
        val colorHex = contact?.colorHex ?: 0xFF00A884
        val isOnline = contact?.isOnline ?: false

        ChatItemRow(
          chat = chat,
          avatarType = avatarType,
          colorHex = colorHex,
          isOnline = isOnline,
          isDark = isDark,
          onClick = { onChatClick(chat.id) },
          onLongClick = { selectedChatForMenu = chat }
        )
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }

    // FAB for New Chat
    FloatingActionButton(
      onClick = onNewChatClick,
      containerColor = WhatsAppLightGreen,
      contentColor = Color.White,
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("fab_new_chat")
    ) {
      Icon(
        imageVector = Icons.Default.Chat,
        contentDescription = "New chat",
        modifier = Modifier.size(24.dp)
      )
    }
  }

  // Long-press Dialog Menu for Chat Actions
  selectedChatForMenu?.let { chat ->
    AlertDialog(
      onDismissRequest = { selectedChatForMenu = null },
      title = { Text(text = chat.name, fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onTogglePin(chat.id)
                selectedChatForMenu = null
              }
              .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.PushPin,
              contentDescription = null,
              tint = WhatsAppGreen
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(if (chat.isPinned) "Unpin chat" else "Pin chat")
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onToggleMute(chat.id)
                selectedChatForMenu = null
              }
              .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (chat.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeMute,
              contentDescription = null,
              tint = WhatsAppGreen
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(if (chat.isMuted) "Unmute notifications" else "Mute notifications")
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onToggleArchive(chat.id)
                selectedChatForMenu = null
              }
              .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Archive,
              contentDescription = null,
              tint = WhatsAppGreen
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Archive chat")
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onDeleteChat(chat.id)
                selectedChatForMenu = null
              }
              .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = null,
              tint = Color.Red
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Delete chat", color = Color.Red)
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { selectedChatForMenu = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun FilterChipsRow(
  activeFilter: ChatFilter,
  onFilterSelected: (ChatFilter) -> Unit,
  isDark: Boolean
) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(ChatFilter.values()) { filter ->
      val isSelected = activeFilter == filter
      val label = when (filter) {
        ChatFilter.ALL -> "All"
        ChatFilter.UNREAD -> "Unread"
        ChatFilter.FAVOURITES -> "Favourites"
        ChatFilter.GROUPS -> "Groups"
      }

      Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) {
          if (isDark) Color(0xFF103629) else Color(0xFFE7FCE8)
        } else {
          if (isDark) Color(0xFF202C33) else Color(0xFFF0F2F5)
        },
        border = if (isSelected) {
          androidx.compose.foundation.BorderStroke(1.dp, WhatsAppLightGreen)
        } else null,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .clickable { onFilterSelected(filter) }
          .testTag("filter_chip_${label.lowercase()}")
      ) {
        Text(
          text = label,
          color = if (isSelected) {
            WhatsAppLightGreen
          } else {
            if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight
          },
          fontSize = 13.5.sp,
          fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
      }
    }
  }
}

@Composable
private fun ArchivedBanner(
  count: Int,
  isDark: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.Archive,
      contentDescription = "Archived",
      tint = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight,
      modifier = Modifier.size(22.dp)
    )
    Spacer(modifier = Modifier.width(24.dp))
    Text(
      text = "Archived",
      color = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight,
      fontSize = 16.sp,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.weight(1f)
    )
    Text(
      text = count.toString(),
      color = WhatsAppLightGreen,
      fontSize = 13.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
private fun ChatItemRow(
  chat: ChatEntity,
  avatarType: AvatarType,
  colorHex: Long,
  isOnline: Boolean,
  isDark: Boolean,
  onClick: () -> Unit,
  onLongClick: () -> Unit
) {
  val primaryColor = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight
  val secondaryColor = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight

  val timeFormatter = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
  val formattedTime = remember(chat.lastMessageTime) {
    val diff = System.currentTimeMillis() - chat.lastMessageTime
    if (diff < 24 * 60 * 60 * 1000) {
      timeFormatter.format(Date(chat.lastMessageTime))
    } else {
      "Yesterday"
    }
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .pointerInput(chat.id) {
        detectTapGestures(
          onTap = { onClick() },
          onLongPress = { onLongClick() }
        )
      }
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AvatarView(
      avatarType = avatarType,
      name = chat.name,
      colorHex = colorHex,
      size = 52.dp,
      showOnlineBadge = !chat.isGroup,
      isOnline = isOnline
    )

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = chat.name,
          color = primaryColor,
          fontSize = 16.5.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f, fill = false)
        )

        Text(
          text = formattedTime,
          color = if (chat.unreadCount > 0) WhatsAppLightGreen else secondaryColor,
          fontSize = 12.sp,
          fontWeight = if (chat.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
        )
      }

      Spacer(modifier = Modifier.height(3.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (chat.lastMessageFromMe) {
            when (chat.lastMessageStatus) {
              MessageStatus.READ -> {
                Icon(
                  imageVector = Icons.Default.DoneAll,
                  contentDescription = "Read",
                  tint = WhatsAppCheckBlue,
                  modifier = Modifier
                    .size(16.dp)
                    .padding(end = 3.dp)
                )
              }
              MessageStatus.DELIVERED -> {
                Icon(
                  imageVector = Icons.Default.DoneAll,
                  contentDescription = "Delivered",
                  tint = secondaryColor,
                  modifier = Modifier
                    .size(16.dp)
                    .padding(end = 3.dp)
                )
              }
              else -> {
                Icon(
                  imageVector = Icons.Default.Done,
                  contentDescription = "Sent",
                  tint = secondaryColor,
                  modifier = Modifier
                    .size(16.dp)
                    .padding(end = 3.dp)
                )
              }
            }
          }

          Text(
            text = chat.lastMessageText,
            color = if (chat.unreadCount > 0) primaryColor else secondaryColor,
            fontSize = 14.sp,
            fontWeight = if (chat.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          if (chat.isMuted) {
            Icon(
              imageVector = Icons.Default.VolumeMute,
              contentDescription = "Muted",
              tint = secondaryColor,
              modifier = Modifier.size(16.dp)
            )
          }

          if (chat.isPinned) {
            Icon(
              imageVector = Icons.Default.PushPin,
              contentDescription = "Pinned",
              tint = secondaryColor,
              modifier = Modifier.size(16.dp)
            )
          }

          if (chat.unreadCount > 0) {
            Box(
              modifier = Modifier
                .size(20.dp)
                .background(WhatsAppLightGreen, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = chat.unreadCount.toString(),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}
