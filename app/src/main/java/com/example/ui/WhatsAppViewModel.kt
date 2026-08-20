package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AvatarType
import com.example.data.CallDirection
import com.example.data.CallLogEntity
import com.example.data.CallType
import com.example.data.ChatEntity
import com.example.data.ContactEntity
import com.example.data.MessageEntity
import com.example.data.MessageType
import com.example.data.StatusEntity
import com.example.data.UserProfile
import com.example.data.WhatsAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class WhatsAppTab {
  CHATS,
  UPDATES,
  COMMUNITIES,
  CALLS
}

enum class ChatFilter {
  ALL,
  UNREAD,
  FAVOURITES,
  GROUPS
}

enum class ThemePreference {
  SYSTEM,
  LIGHT,
  DARK
}

data class ActiveCallSession(
  val contactId: Long,
  val contactName: String,
  val avatarType: AvatarType,
  val callType: CallType,
  val isOutgoing: Boolean = true,
  val startTime: Long = System.currentTimeMillis()
)

data class WhatsAppUiState(
  val userProfile: UserProfile = UserProfile(),
  val currentTab: WhatsAppTab = WhatsAppTab.CHATS,
  val chatFilter: ChatFilter = ChatFilter.ALL,
  val searchQuery: String = "",
  val isSearching: Boolean = false,
  val activeChatId: Long? = null,
  val activeStatusId: Long? = null,
  val activeCallSession: ActiveCallSession? = null,
  val showNewChatDialog: Boolean = false,
  val showSettingsDialog: Boolean = false,
  val showNewGroupDialog: Boolean = false,
  val showAddStatusDialog: Boolean = false,
  val replyingToMessage: MessageEntity? = null,
  val selectedMessageForActions: MessageEntity? = null,
  val themePreference: ThemePreference = ThemePreference.SYSTEM,
  val activeChats: List<ChatEntity> = emptyList(),
  val archivedChats: List<ChatEntity> = emptyList(),
  val contacts: List<ContactEntity> = emptyList(),
  val statuses: List<StatusEntity> = emptyList(),
  val callLogs: List<CallLogEntity> = emptyList(),
  val starredMessages: List<MessageEntity> = emptyList()
)

class WhatsAppViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: WhatsAppRepository
  private val prefs = application.getSharedPreferences("hey_chat_prefs", Context.MODE_PRIVATE)

  private val _userProfile = MutableStateFlow(loadUserProfile())
  private val _currentTab = MutableStateFlow(WhatsAppTab.CHATS)
  private val _chatFilter = MutableStateFlow(ChatFilter.ALL)
  private val _searchQuery = MutableStateFlow("")
  private val _isSearching = MutableStateFlow(false)
  private val _activeChatId = MutableStateFlow<Long?>(null)
  private val _activeStatusId = MutableStateFlow<Long?>(null)
  private val _activeCallSession = MutableStateFlow<ActiveCallSession?>(null)
  private val _showNewChatDialog = MutableStateFlow(false)
  private val _showSettingsDialog = MutableStateFlow(false)
  private val _showNewGroupDialog = MutableStateFlow(false)
  private val _showAddStatusDialog = MutableStateFlow(false)
  private val _replyingToMessage = MutableStateFlow<MessageEntity?>(null)
  private val _selectedMessageForActions = MutableStateFlow<MessageEntity?>(null)
  private val _themePreference = MutableStateFlow(ThemePreference.SYSTEM)

  init {
    val database = AppDatabase.getDatabase(application)
    repository = WhatsAppRepository(database, viewModelScope)
    viewModelScope.launch {
      repository.initializePreloadedDataIfEmpty()
    }
  }

  val uiState: StateFlow<WhatsAppUiState> = combine(
    _userProfile,
    _currentTab,
    _chatFilter,
    _searchQuery,
    _isSearching,
    _activeChatId,
    _activeStatusId,
    _activeCallSession,
    repository.activeChats,
    repository.archivedChats,
    repository.contacts,
    repository.statuses,
    repository.callLogs
  ) { args: Array<Any?> ->
    val userProfile = args[0] as UserProfile
    val currentTab = args[1] as WhatsAppTab
    val chatFilter = args[2] as ChatFilter
    val searchQuery = args[3] as String
    val isSearching = args[4] as Boolean
    val activeChatId = args[5] as Long?
    val activeStatusId = args[6] as Long?
    val activeCallSession = args[7] as ActiveCallSession?
    val activeChats = args[8] as List<ChatEntity>
    val archivedChats = args[9] as List<ChatEntity>
    val contacts = args[10] as List<ContactEntity>
    val statuses = args[11] as List<StatusEntity>
    val callLogs = args[12] as List<CallLogEntity>

    val filteredChats = activeChats.filter { chat ->
      val matchesFilter = when (chatFilter) {
        ChatFilter.ALL -> true
        ChatFilter.UNREAD -> chat.unreadCount > 0
        ChatFilter.FAVOURITES -> chat.isFavourite
        ChatFilter.GROUPS -> chat.isGroup
      }
      val matchesSearch = if (searchQuery.isBlank()) true else {
        chat.name.contains(searchQuery, ignoreCase = true) ||
            chat.lastMessageText.contains(searchQuery, ignoreCase = true)
      }
      matchesFilter && matchesSearch
    }

    WhatsAppUiState(
      userProfile = userProfile,
      currentTab = currentTab,
      chatFilter = chatFilter,
      searchQuery = searchQuery,
      isSearching = isSearching,
      activeChatId = activeChatId,
      activeStatusId = activeStatusId,
      activeCallSession = activeCallSession,
      showNewChatDialog = _showNewChatDialog.value,
      showSettingsDialog = _showSettingsDialog.value,
      showNewGroupDialog = _showNewGroupDialog.value,
      showAddStatusDialog = _showAddStatusDialog.value,
      replyingToMessage = _replyingToMessage.value,
      selectedMessageForActions = _selectedMessageForActions.value,
      themePreference = _themePreference.value,
      activeChats = filteredChats,
      archivedChats = archivedChats,
      contacts = contacts,
      statuses = statuses,
      callLogs = callLogs
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = WhatsAppUiState()
  )

  private fun loadUserProfile(): UserProfile {
    val isLoggedIn = prefs.getBoolean("is_logged_in", false)
    val phone = prefs.getString("phone_number", "") ?: ""
    val dialCode = prefs.getString("country_code", "+1") ?: "+1"
    val countryName = prefs.getString("country_name", "United States") ?: "United States"
    val countryFlag = prefs.getString("country_flag", "🇺🇸") ?: "🇺🇸"
    val name = prefs.getString("display_name", "Hey Chat User") ?: "Hey Chat User"
    val about = prefs.getString("status_about", "Hey there! I am using Hey Chat.") ?: "Hey there! I am using Hey Chat."
    val colorHex = prefs.getLong("avatar_color_hex", 0xFF008069)

    return UserProfile(
      phoneNumber = phone,
      countryCode = dialCode,
      countryName = countryName,
      countryFlag = countryFlag,
      displayName = name,
      statusAbout = about,
      avatarColorHex = colorHex,
      isLoggedIn = isLoggedIn
    )
  }

  fun saveUserProfile(profile: UserProfile) {
    prefs.edit()
      .putBoolean("is_logged_in", profile.isLoggedIn)
      .putString("phone_number", profile.phoneNumber)
      .putString("country_code", profile.countryCode)
      .putString("country_name", profile.countryName)
      .putString("country_flag", profile.countryFlag)
      .putString("display_name", profile.displayName)
      .putString("status_about", profile.statusAbout)
      .putLong("avatar_color_hex", profile.avatarColorHex)
      .apply()
    _userProfile.value = profile
  }

  fun updateUserProfile(name: String, statusAbout: String, avatarColorHex: Long) {
    val current = _userProfile.value
    val updated = current.copy(
      displayName = name,
      statusAbout = statusAbout,
      avatarColorHex = avatarColorHex
    )
    saveUserProfile(updated)
  }

  fun logout() {
    prefs.edit().putBoolean("is_logged_in", false).apply()
    _userProfile.value = _userProfile.value.copy(isLoggedIn = false)
    _showSettingsDialog.value = false
    _activeChatId.value = null
    _activeStatusId.value = null
  }

  fun getMessagesForChat(chatId: Long) = repository.getMessagesForChat(chatId)
  fun getChatById(chatId: Long) = repository.getChatById(chatId)

  fun setTab(tab: WhatsAppTab) {
    _currentTab.value = tab
  }

  fun setChatFilter(filter: ChatFilter) {
    _chatFilter.value = filter
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun toggleSearch(active: Boolean) {
    _isSearching.value = active
    if (!active) _searchQuery.value = ""
  }

  fun openChat(chatId: Long) {
    _activeChatId.value = chatId
    viewModelScope.launch {
      repository.markChatAsRead(chatId)
    }
  }

  fun closeChat() {
    _activeChatId.value = null
    _replyingToMessage.value = null
    _selectedMessageForActions.value = null
  }

  fun openStatusViewer(statusId: Long) {
    _activeStatusId.value = statusId
    viewModelScope.launch {
      repository.markStatusViewed(statusId)
    }
  }

  fun closeStatusViewer() {
    _activeStatusId.value = null
  }

  fun startCall(contactId: Long, contactName: String, avatarType: AvatarType, callType: CallType) {
    _activeCallSession.value = ActiveCallSession(
      contactId = contactId,
      contactName = contactName,
      avatarType = avatarType,
      callType = callType,
      isOutgoing = true
    )
    viewModelScope.launch {
      repository.logCall(
        contactId = contactId,
        name = contactName,
        avatarType = avatarType,
        callType = callType,
        direction = CallDirection.OUTGOING,
        duration = 0
      )
    }
  }

  fun endCall() {
    _activeCallSession.value = null
  }

  fun sendMessage(
    chatId: Long,
    text: String,
    type: MessageType = MessageType.TEXT,
    mediaCaption: String = "",
    audioDurationSec: Int = 0
  ) {
    val reply = _replyingToMessage.value
    viewModelScope.launch {
      repository.sendMessage(
        chatId = chatId,
        text = text,
        type = type,
        mediaCaption = mediaCaption,
        audioDurationSec = audioDurationSec,
        replyToMessageId = reply?.id,
        replyToText = reply?.text ?: reply?.mediaCaption,
        replyToSenderName = reply?.senderName
      )
      _replyingToMessage.value = null
    }
  }

  fun startNewChatWithContact(contact: ContactEntity) {
    viewModelScope.launch {
      val chatId = repository.createNewChat(contact)
      _showNewChatDialog.value = false
      openChat(chatId)
    }
  }

  fun createNewGroup(name: String, memberIds: List<Long>) {
    viewModelScope.launch {
      val chatId = repository.createNewGroup(name, memberIds)
      _showNewGroupDialog.value = false
      openChat(chatId)
    }
  }

  fun togglePin(chatId: Long) {
    viewModelScope.launch { repository.togglePin(chatId) }
  }

  fun toggleMute(chatId: Long) {
    viewModelScope.launch { repository.toggleMute(chatId) }
  }

  fun toggleArchive(chatId: Long) {
    viewModelScope.launch { repository.toggleArchive(chatId) }
  }

  fun toggleFavourite(chatId: Long) {
    viewModelScope.launch { repository.toggleFavourite(chatId) }
  }

  fun deleteChat(chatId: Long) {
    viewModelScope.launch {
      repository.deleteChat(chatId)
      if (_activeChatId.value == chatId) {
        _activeChatId.value = null
      }
    }
  }

  fun selectMessageForActions(message: MessageEntity?) {
    _selectedMessageForActions.value = message
  }

  fun addReactionToMessage(messageId: Long, reaction: String) {
    viewModelScope.launch {
      repository.updateReaction(messageId, reaction)
      _selectedMessageForActions.value = null
    }
  }

  fun toggleStarMessage(messageId: Long) {
    viewModelScope.launch {
      repository.toggleStarMessage(messageId)
      _selectedMessageForActions.value = null
    }
  }

  fun deleteMessage(messageId: Long) {
    viewModelScope.launch {
      repository.deleteMessage(messageId)
      _selectedMessageForActions.value = null
    }
  }

  fun setReplyToMessage(message: MessageEntity?) {
    _replyingToMessage.value = message
    _selectedMessageForActions.value = null
  }

  fun setShowNewChatDialog(show: Boolean) {
    _showNewChatDialog.value = show
  }

  fun setShowSettingsDialog(show: Boolean) {
    _showSettingsDialog.value = show
  }

  fun setShowNewGroupDialog(show: Boolean) {
    _showNewGroupDialog.value = show
  }

  fun setShowAddStatusDialog(show: Boolean) {
    _showAddStatusDialog.value = show
  }

  fun postNewStatus(text: String, bgColorHex: Long) {
    viewModelScope.launch {
      repository.addStatus(caption = text, textStatus = text, bgColorHex = bgColorHex)
      _showAddStatusDialog.value = false
    }
  }

  fun setThemePreference(pref: ThemePreference) {
    _themePreference.value = pref
  }
}
