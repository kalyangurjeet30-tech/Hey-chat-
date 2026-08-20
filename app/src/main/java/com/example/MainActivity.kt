package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CallType
import com.example.ui.ThemePreference
import com.example.ui.WhatsAppTab
import com.example.ui.WhatsAppUiState
import com.example.ui.WhatsAppViewModel
import com.example.ui.dialogs.AddStatusDialog
import com.example.ui.dialogs.NewChatDialog
import com.example.ui.dialogs.NewGroupDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.screens.CallScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.StatusViewerScreen
import com.example.ui.tabs.CallsTab
import com.example.ui.tabs.ChatsTab
import com.example.ui.tabs.CommunitiesTab
import com.example.ui.tabs.UpdatesTab
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTheme
import com.example.ui.theme.WhatsAppTopBarDark
import com.example.ui.theme.WhatsAppTopBarLight

class MainActivity : ComponentActivity() {
  private val viewModel: WhatsAppViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()

      val darkTheme = when (uiState.themePreference) {
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
      }

      WhatsAppTheme(darkTheme = darkTheme) {
        if (!uiState.userProfile.isLoggedIn) {
          LoginScreen(
            onLoginCompleted = { profile ->
              viewModel.saveUserProfile(profile)
            }
          )
        } else {
          WhatsAppMainContainer(
            viewModel = viewModel,
            uiState = uiState
          )
        }
      }
    }
  }
}

@Composable
fun WhatsAppMainContainer(
  viewModel: WhatsAppViewModel,
  uiState: WhatsAppUiState
) {
  val isDark = isSystemInDarkTheme()

  // Handle Android hardware back press when deep inside a screen
  BackHandler(
    enabled = uiState.activeChatId != null ||
        uiState.activeStatusId != null ||
        uiState.activeCallSession != null ||
        uiState.showNewChatDialog ||
        uiState.showSettingsDialog ||
        uiState.showNewGroupDialog ||
        uiState.showAddStatusDialog ||
        uiState.isSearching
  ) {
    when {
      uiState.activeStatusId != null -> viewModel.closeStatusViewer()
      uiState.activeCallSession != null -> viewModel.endCall()
      uiState.activeChatId != null -> viewModel.closeChat()
      uiState.showNewChatDialog -> viewModel.setShowNewChatDialog(false)
      uiState.showSettingsDialog -> viewModel.setShowSettingsDialog(false)
      uiState.showNewGroupDialog -> viewModel.setShowNewGroupDialog(false)
      uiState.showAddStatusDialog -> viewModel.setShowAddStatusDialog(false)
      uiState.isSearching -> viewModel.toggleSearch(false)
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    // Main App Shell (TopBar + Current Tab + BottomBar)
    Scaffold(
      topBar = {
        WhatsAppTopHeader(
          uiState = uiState,
          onSearchToggle = { viewModel.toggleSearch(it) },
          onSearchQueryChange = { viewModel.setSearchQuery(it) },
          onSettingsClick = { viewModel.setShowSettingsDialog(true) },
          onNewGroupClick = { viewModel.setShowNewGroupDialog(true) },
          onAddStatusClick = { viewModel.setShowAddStatusDialog(true) },
          isDark = isDark
        )
      },
      bottomBar = {
        WhatsAppBottomNav(
          currentTab = uiState.currentTab,
          onTabSelected = { viewModel.setTab(it) },
          unreadChatsCount = uiState.activeChats.sumOf { it.unreadCount },
          hasUnviewedStatuses = uiState.statuses.any { !it.isMyStatus && !it.isViewed },
          isDark = isDark
        )
      },
      modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        AnimatedContent(
          targetState = uiState.currentTab,
          transitionSpec = { fadeIn() togetherWith fadeOut() },
          label = "tabTransition"
        ) { targetTab ->
          when (targetTab) {
            WhatsAppTab.CHATS -> ChatsTab(
              uiState = uiState,
              onChatClick = { chatId -> viewModel.openChat(chatId) },
              onFilterSelected = { filter -> viewModel.setChatFilter(filter) },
              onTogglePin = { chatId -> viewModel.togglePin(chatId) },
              onToggleMute = { chatId -> viewModel.toggleMute(chatId) },
              onToggleArchive = { chatId -> viewModel.toggleArchive(chatId) },
              onDeleteChat = { chatId -> viewModel.deleteChat(chatId) },
              onNewChatClick = { viewModel.setShowNewChatDialog(true) }
            )
            WhatsAppTab.UPDATES -> UpdatesTab(
              uiState = uiState,
              onStatusClick = { statusId -> viewModel.openStatusViewer(statusId) },
              onAddStatusClick = { viewModel.setShowAddStatusDialog(true) }
            )
            WhatsAppTab.COMMUNITIES -> CommunitiesTab(
              uiState = uiState,
              onGroupClick = { groupId -> viewModel.openChat(groupId) }
            )
            WhatsAppTab.CALLS -> CallsTab(
              uiState = uiState,
              onStartCall = { contactId, name, avatarType, type ->
                viewModel.startCall(contactId, name, avatarType, type)
              },
              onNewCallClick = { viewModel.setShowNewChatDialog(true) }
            )
          }
        }
      }
    }

    // Chat Screen Overlay
    uiState.activeChatId?.let { chatId ->
      ChatScreen(
        chatId = chatId,
        viewModel = viewModel,
        onBack = { viewModel.closeChat() },
        onStartCall = { contactId, name, avatarType, type ->
          viewModel.startCall(contactId, name, avatarType, type)
        }
      )
    }

    // Fullscreen Status Story Viewer
    uiState.activeStatusId?.let { statusId ->
      val status = uiState.statuses.find { it.id == statusId }
      status?.let { s ->
        StatusViewerScreen(
          status = s,
          onClose = { viewModel.closeStatusViewer() },
          onReply = { reply ->
            if (s.contactId != 0L) {
              viewModel.sendMessage(
                chatId = s.contactId,
                text = "Replied to status: $reply",
                mediaCaption = s.caption
              )
            }
          }
        )
      }
    }

    // Fullscreen Active Call Screen
    uiState.activeCallSession?.let { session ->
      CallScreen(
        session = session,
        onEndCall = { viewModel.endCall() }
      )
    }

    // New Chat / Contact Picker Dialog
    if (uiState.showNewChatDialog) {
      NewChatDialog(
        contacts = uiState.contacts,
        onContactClick = { contact -> viewModel.startNewChatWithContact(contact) },
        onNewGroupClick = {
          viewModel.setShowNewChatDialog(false)
          viewModel.setShowNewGroupDialog(true)
        },
        onDismiss = { viewModel.setShowNewChatDialog(false) }
      )
    }

    // New Group Dialog
    if (uiState.showNewGroupDialog) {
      NewGroupDialog(
        contacts = uiState.contacts,
        onCreateGroup = { name, memberIds ->
          viewModel.createNewGroup(name, memberIds)
        },
        onDismiss = { viewModel.setShowNewGroupDialog(false) }
      )
    }

    // Add Status Composer Dialog
    if (uiState.showAddStatusDialog) {
      AddStatusDialog(
        onPostStatus = { text, colorHex ->
          viewModel.postNewStatus(text, colorHex)
        },
        onDismiss = { viewModel.setShowAddStatusDialog(false) }
      )
    }

    // Settings Screen Dialog
    if (uiState.showSettingsDialog) {
      SettingsDialog(
        userProfile = uiState.userProfile,
        themePreference = uiState.themePreference,
        onThemeChange = { pref -> viewModel.setThemePreference(pref) },
        onUpdateProfile = { name, about, colorHex ->
          viewModel.updateUserProfile(name, about, colorHex)
        },
        onLogout = { viewModel.logout() },
        onDismiss = { viewModel.setShowSettingsDialog(false) }
      )
    }
  }
}

@Composable
private fun WhatsAppTopHeader(
  uiState: WhatsAppUiState,
  onSearchToggle: (Boolean) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onSettingsClick: () -> Unit,
  onNewGroupClick: () -> Unit,
  onAddStatusClick: () -> Unit,
  isDark: Boolean
) {
  var showMenu by remember { mutableStateOf(false) }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(if (isDark) WhatsAppTopBarDark else WhatsAppTopBarLight)
      .statusBarsPadding()
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (uiState.isSearching) {
      BasicTextField(
        value = uiState.searchQuery,
        onValueChange = onSearchQueryChange,
        textStyle = TextStyle(color = Color.White, fontSize = 17.sp),
        cursorBrush = SolidColor(Color.White),
        decorationBox = { innerTextField ->
          if (uiState.searchQuery.isEmpty()) {
            Text("Search...", color = Color(0xAAFFFFFF), fontSize = 17.sp)
          }
          innerTextField()
        },
        modifier = Modifier
          .weight(1f)
          .testTag("top_search_field")
      )

      IconButton(onClick = { onSearchToggle(false) }) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Close search",
          tint = Color.White
        )
      }
    } else {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_hey_chat_logo),
          contentDescription = "Hey Chat Logo",
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "Hey Chat",
          color = Color.White,
          fontSize = 21.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.3.sp
        )
      }

      IconButton(
        onClick = onAddStatusClick,
        modifier = Modifier.testTag("top_camera_icon")
      ) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = "Camera",
          tint = Color.White,
          modifier = Modifier.size(23.dp)
        )
      }

      IconButton(
        onClick = { onSearchToggle(true) },
        modifier = Modifier.testTag("top_search_icon")
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = Color.White,
          modifier = Modifier.size(23.dp)
        )
      }

      Box {
        IconButton(
          onClick = { showMenu = true },
          modifier = Modifier.testTag("top_overflow_menu")
        ) {
          Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More options",
            tint = Color.White,
            modifier = Modifier.size(23.dp)
          )
        }

        DropdownMenu(
          expanded = showMenu,
          onDismissRequest = { showMenu = false }
        ) {
          DropdownMenuItem(
            text = { Text("New group") },
            onClick = {
              showMenu = false
              onNewGroupClick()
            }
          )
          DropdownMenuItem(
            text = { Text("New broadcast") },
            onClick = { showMenu = false }
          )
          DropdownMenuItem(
            text = { Text("Linked devices") },
            onClick = { showMenu = false }
          )
          DropdownMenuItem(
            text = { Text("Starred messages") },
            onClick = { showMenu = false }
          )
          DropdownMenuItem(
            text = { Text("Settings") },
            onClick = {
              showMenu = false
              onSettingsClick()
            }
          )
        }
      }
    }
  }
}

@Composable
private fun WhatsAppBottomNav(
  currentTab: WhatsAppTab,
  onTabSelected: (WhatsAppTab) -> Unit,
  unreadChatsCount: Int,
  hasUnviewedStatuses: Boolean,
  isDark: Boolean
) {
  NavigationBar(
    containerColor = if (isDark) Color(0xFF0B141A) else Color(0xFFFFFFFF),
    contentColor = if (isDark) Color.White else Color(0xFF111B21),
    tonalElevation = 8.dp,
    modifier = Modifier.height(76.dp)
  ) {
    // Chats
    NavigationBarItem(
      selected = currentTab == WhatsAppTab.CHATS,
      onClick = { onTabSelected(WhatsAppTab.CHATS) },
      icon = {
        BadgedBox(
          badge = {
            if (unreadChatsCount > 0) {
              Badge(
                containerColor = WhatsAppLightGreen,
                contentColor = Color.White
              ) {
                Text(
                  text = unreadChatsCount.toString(),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        ) {
          Icon(
            imageVector = if (currentTab == WhatsAppTab.CHATS) Icons.Filled.Chat else Icons.Outlined.Chat,
            contentDescription = "Chats"
          )
        }
      },
      label = {
        Text(
          text = "Chats",
          fontWeight = if (currentTab == WhatsAppTab.CHATS) FontWeight.Bold else FontWeight.Normal,
          fontSize = 12.sp
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = if (isDark) WhatsAppLightGreen else Color(0xFF008069),
        indicatorColor = if (isDark) Color(0xFF103629) else Color(0xFFE7FCE8)
      ),
      modifier = Modifier.testTag("tab_chats")
    )

    // Updates (Status & Channels)
    NavigationBarItem(
      selected = currentTab == WhatsAppTab.UPDATES,
      onClick = { onTabSelected(WhatsAppTab.UPDATES) },
      icon = {
        BadgedBox(
          badge = {
            if (hasUnviewedStatuses) {
              Badge(
                containerColor = WhatsAppLightGreen,
                modifier = Modifier.size(8.dp)
              )
            }
          }
        ) {
          Icon(
            imageVector = if (currentTab == WhatsAppTab.UPDATES) Icons.Filled.Update else Icons.Outlined.Update,
            contentDescription = "Updates"
          )
        }
      },
      label = {
        Text(
          text = "Updates",
          fontWeight = if (currentTab == WhatsAppTab.UPDATES) FontWeight.Bold else FontWeight.Normal,
          fontSize = 12.sp
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = if (isDark) WhatsAppLightGreen else Color(0xFF008069),
        indicatorColor = if (isDark) Color(0xFF103629) else Color(0xFFE7FCE8)
      ),
      modifier = Modifier.testTag("tab_updates")
    )

    // Communities
    NavigationBarItem(
      selected = currentTab == WhatsAppTab.COMMUNITIES,
      onClick = { onTabSelected(WhatsAppTab.COMMUNITIES) },
      icon = {
        Icon(
          imageVector = if (currentTab == WhatsAppTab.COMMUNITIES) Icons.Filled.Groups else Icons.Outlined.Groups,
          contentDescription = "Communities"
        )
      },
      label = {
        Text(
          text = "Communities",
          fontWeight = if (currentTab == WhatsAppTab.COMMUNITIES) FontWeight.Bold else FontWeight.Normal,
          fontSize = 12.sp
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = if (isDark) WhatsAppLightGreen else Color(0xFF008069),
        indicatorColor = if (isDark) Color(0xFF103629) else Color(0xFFE7FCE8)
      ),
      modifier = Modifier.testTag("tab_communities")
    )

    // Calls
    NavigationBarItem(
      selected = currentTab == WhatsAppTab.CALLS,
      onClick = { onTabSelected(WhatsAppTab.CALLS) },
      icon = {
        Icon(
          imageVector = if (currentTab == WhatsAppTab.CALLS) Icons.Filled.Phone else Icons.Outlined.Phone,
          contentDescription = "Calls"
        )
      },
      label = {
        Text(
          text = "Calls",
          fontWeight = if (currentTab == WhatsAppTab.CALLS) FontWeight.Bold else FontWeight.Normal,
          fontSize = 12.sp
        )
      },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = if (isDark) WhatsAppLightGreen else Color(0xFF008069),
        indicatorColor = if (isDark) Color(0xFF103629) else Color(0xFFE7FCE8)
      ),
      modifier = Modifier.testTag("tab_calls")
    )
  }
}
