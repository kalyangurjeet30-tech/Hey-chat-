package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WhatsAppRepository(
  private val database: AppDatabase,
  private val coroutineScope: CoroutineScope
) {
  val activeChats: Flow<List<ChatEntity>> = database.chatDao().getActiveChats()
  val archivedChats: Flow<List<ChatEntity>> = database.chatDao().getArchivedChats()
  val contacts: Flow<List<ContactEntity>> = database.contactDao().getAllContacts()
  val statuses: Flow<List<StatusEntity>> = database.statusDao().getAllStatuses()
  val callLogs: Flow<List<CallLogEntity>> = database.callLogDao().getAllCallLogs()
  val starredMessages: Flow<List<MessageEntity>> = database.messageDao().getStarredMessages()

  fun getMessagesForChat(chatId: Long): Flow<List<MessageEntity>> =
    database.messageDao().getMessagesForChat(chatId)

  fun getChatById(chatId: Long): Flow<ChatEntity?> =
    database.chatDao().getChatById(chatId)

  suspend fun markChatAsRead(chatId: Long) = withContext(Dispatchers.IO) {
    database.chatDao().markAsRead(chatId)
  }

  suspend fun togglePin(chatId: Long) = withContext(Dispatchers.IO) {
    database.chatDao().togglePin(chatId)
  }

  suspend fun toggleMute(chatId: Long) = withContext(Dispatchers.IO) {
    database.chatDao().toggleMute(chatId)
  }

  suspend fun toggleArchive(chatId: Long) = withContext(Dispatchers.IO) {
    database.chatDao().toggleArchive(chatId)
  }

  suspend fun toggleFavourite(chatId: Long) = withContext(Dispatchers.IO) {
    database.chatDao().toggleFavourite(chatId)
  }

  suspend fun deleteChat(chatId: Long) = withContext(Dispatchers.IO) {
    database.messageDao().deleteMessagesForChat(chatId)
    database.chatDao().deleteChat(chatId)
  }

  suspend fun updateReaction(messageId: Long, reaction: String) = withContext(Dispatchers.IO) {
    database.messageDao().updateReaction(messageId, reaction)
  }

  suspend fun toggleStarMessage(messageId: Long) = withContext(Dispatchers.IO) {
    database.messageDao().toggleStar(messageId)
  }

  suspend fun deleteMessage(messageId: Long) = withContext(Dispatchers.IO) {
    database.messageDao().deleteMessage(messageId)
  }

  suspend fun markStatusViewed(statusId: Long) = withContext(Dispatchers.IO) {
    database.statusDao().markAsViewed(statusId)
  }

  suspend fun addStatus(
    caption: String,
    textStatus: String = "",
    bgColorHex: Long = 0xFF008069
  ) = withContext(Dispatchers.IO) {
    val status = StatusEntity(
      contactId = 0L,
      contactName = "My Status",
      avatarType = AvatarType.DEFAULT,
      caption = caption,
      textStatus = textStatus,
      mediaBgColorHex = bgColorHex,
      timestamp = System.currentTimeMillis(),
      isViewed = false,
      isMyStatus = true
    )
    database.statusDao().insertStatus(status)
  }

  suspend fun logCall(
    contactId: Long,
    name: String,
    avatarType: AvatarType,
    callType: CallType,
    direction: CallDirection,
    duration: Int = 0
  ) = withContext(Dispatchers.IO) {
    val log = CallLogEntity(
      contactId = contactId,
      contactName = name,
      avatarType = avatarType,
      callType = callType,
      callDirection = direction,
      timestamp = System.currentTimeMillis(),
      durationSec = duration
    )
    database.callLogDao().insertCallLog(log)
  }

  suspend fun sendMessage(
    chatId: Long,
    text: String,
    type: MessageType = MessageType.TEXT,
    mediaCaption: String = "",
    audioDurationSec: Int = 0,
    replyToMessageId: Long? = null,
    replyToText: String? = null,
    replyToSenderName: String? = null
  ) = withContext(Dispatchers.IO) {
    val msg = MessageEntity(
      chatId = chatId,
      senderId = 0L,
      senderName = "You",
      text = text,
      messageType = type,
      timestamp = System.currentTimeMillis(),
      status = MessageStatus.SENT,
      mediaCaption = mediaCaption,
      audioDurationSec = audioDurationSec,
      replyToMessageId = replyToMessageId,
      replyToText = replyToText,
      replyToSenderName = replyToSenderName
    )
    database.messageDao().insertMessage(msg)

    val currentChat = database.chatDao().getChatById(chatId).first()
    if (currentChat != null) {
      val displayText = when (type) {
        MessageType.IMAGE -> "📷 Photo"
        MessageType.VOICE_NOTE -> "🎤 Voice message (${audioDurationSec}s)"
        MessageType.DOCUMENT -> "📄 Document"
        MessageType.LOCATION -> "📍 Location"
        MessageType.CONTACT -> "👤 Contact"
        MessageType.TEXT -> text
      }
      database.chatDao().updateChat(
        currentChat.copy(
          lastMessageText = displayText,
          lastMessageTime = System.currentTimeMillis(),
          lastMessageStatus = MessageStatus.SENT,
          lastMessageFromMe = true
        )
      )
    }

    // Realistic auto-reply simulation for demo if chatting with 1-on-1 contacts
    if (currentChat != null && !currentChat.isGroup && currentChat.contactId > 0) {
      coroutineScope.launch {
        delay(1800)
        val replies = listOf(
          "Sounds amazing! Let's connect soon.",
          "Got it! Thanks for sharing 🙌",
          "I'm working on the design system updates right now.",
          "Awesome, I'll review it and get back to you in a few minutes.",
          "Perfect! Let's do that 👍"
        )
        val replyText = replies.random()
        val replyMsg = MessageEntity(
          chatId = chatId,
          senderId = currentChat.contactId,
          senderName = currentChat.name,
          text = replyText,
          messageType = MessageType.TEXT,
          timestamp = System.currentTimeMillis(),
          status = MessageStatus.READ
        )
        database.messageDao().insertMessage(replyMsg)
        database.chatDao().updateChat(
          currentChat.copy(
            lastMessageText = replyText,
            lastMessageTime = System.currentTimeMillis(),
            lastMessageStatus = MessageStatus.READ,
            lastMessageFromMe = false
          )
        )
      }
    }
  }

  suspend fun createNewChat(contact: ContactEntity): Long = withContext(Dispatchers.IO) {
    val existing = database.chatDao().findChatByContactId(contact.id)
    if (existing != null) {
      existing.id
    } else {
      val newChat = ChatEntity(
        contactId = contact.id,
        name = contact.name,
        isGroup = false,
        unreadCount = 0,
        lastMessageText = "Tap to start conversation",
        lastMessageTime = System.currentTimeMillis()
      )
      val chatId = database.chatDao().insertChat(newChat)
      chatId
    }
  }

  suspend fun createNewGroup(name: String, memberIds: List<Long>): Long = withContext(Dispatchers.IO) {
    val newGroup = ChatEntity(
      contactId = -1L,
      name = name,
      isGroup = true,
      unreadCount = 0,
      lastMessageText = "You created group \"$name\"",
      lastMessageTime = System.currentTimeMillis()
    )
    val chatId = database.chatDao().insertChat(newGroup)

    val introMsg = MessageEntity(
      chatId = chatId,
      senderId = 0L,
      senderName = "You",
      text = "You created group \"$name\"",
      messageType = MessageType.TEXT,
      timestamp = System.currentTimeMillis(),
      status = MessageStatus.READ
    )
    database.messageDao().insertMessage(introMsg)
    chatId
  }

  suspend fun initializePreloadedDataIfEmpty() = withContext(Dispatchers.IO) {
    val existingContacts = database.contactDao().getAllContacts().first()
    if (existingContacts.isNotEmpty()) return@withContext

    val now = System.currentTimeMillis()
    val min = 60 * 1000L
    val hour = 60 * min

    // Prepopulate realistic Contacts
    val initialContacts = listOf(
      ContactEntity(
        id = 1,
        name = "Sarah Connor",
        phoneNumber = "+1 555-0192",
        avatarType = AvatarType.SARAH,
        statusAbout = "Building the future of AI ✨",
        isOnline = true,
        lastSeenText = "online",
        colorHex = 0xFF8E24AA
      ),
      ContactEntity(
        id = 2,
        name = "Alex Rivera",
        phoneNumber = "+1 555-0843",
        avatarType = AvatarType.ALEX,
        statusAbout = "Android Developer | Kotlin & Compose enthusiast",
        isOnline = false,
        lastSeenText = "last seen today at 11:42 AM",
        colorHex = 0xFF00897B
      ),
      ContactEntity(
        id = 3,
        name = "Priya Sharma",
        phoneNumber = "+91 98765-43210",
        avatarType = AvatarType.PRIYA,
        statusAbout = "Designing clean user experiences 🎨",
        isOnline = true,
        lastSeenText = "online",
        colorHex = 0xFFD81B60
      ),
      ContactEntity(
        id = 4,
        name = "Michael Scott",
        phoneNumber = "+1 555-0329",
        avatarType = AvatarType.MICHAEL,
        statusAbout = "That's what she said!",
        isOnline = false,
        lastSeenText = "last seen yesterday at 9:15 PM",
        colorHex = 0xFF1E88E5
      ),
      ContactEntity(
        id = 5,
        name = "Emma Watson",
        phoneNumber = "+44 7700-900123",
        avatarType = AvatarType.EMMA,
        statusAbout = "Books & Coffee ☕📖",
        isOnline = false,
        lastSeenText = "last seen today at 8:05 AM",
        colorHex = 0xFFFB8C00
      )
    )
    database.contactDao().insertContacts(initialContacts)

    // Prepopulate Chats
    val initialChats = listOf(
      ChatEntity(
        id = 1,
        contactId = 1,
        name = "Sarah Connor",
        isGroup = false,
        unreadCount = 2,
        isPinned = true,
        isMuted = false,
        isFavourite = true,
        lastMessageText = "The new UI animations look sensational! 🔥",
        lastMessageTime = now - 4 * min,
        lastMessageStatus = MessageStatus.DELIVERED,
        lastMessageFromMe = false
      ),
      ChatEntity(
        id = 2,
        contactId = 2,
        name = "Android Devs Elite 🚀",
        isGroup = true,
        unreadCount = 5,
        isPinned = true,
        isMuted = false,
        isFavourite = true,
        lastMessageText = "Alex: Check out this new Compose gesture animation demo!",
        lastMessageTime = now - 18 * min,
        lastMessageStatus = MessageStatus.READ,
        lastMessageFromMe = false
      ),
      ChatEntity(
        id = 3,
        contactId = 3,
        name = "Priya Sharma",
        isGroup = false,
        unreadCount = 0,
        isPinned = false,
        isMuted = false,
        isFavourite = false,
        lastMessageText = "Shared the updated Figma design tokens with you.",
        lastMessageTime = now - 45 * min,
        lastMessageStatus = MessageStatus.READ,
        lastMessageFromMe = true
      ),
      ChatEntity(
        id = 4,
        contactId = 4,
        name = "Michael Scott",
        isGroup = false,
        unreadCount = 0,
        isPinned = false,
        isMuted = true,
        isFavourite = false,
        lastMessageText = "🎤 Voice message (15s)",
        lastMessageTime = now - 3 * hour,
        lastMessageStatus = MessageStatus.READ,
        lastMessageFromMe = false
      ),
      ChatEntity(
        id = 5,
        contactId = 5,
        name = "Emma Watson",
        isGroup = false,
        unreadCount = 0,
        isPinned = false,
        isMuted = false,
        isFavourite = false,
        lastMessageText = "Let's meet at 5 PM near the library.",
        lastMessageTime = now - 26 * hour,
        lastMessageStatus = MessageStatus.READ,
        lastMessageFromMe = false
      )
    )
    database.chatDao().insertChats(initialChats)

    // Prepopulate Sample Messages for Chat 1 (Sarah)
    val sarahMessages = listOf(
      MessageEntity(
        id = 1,
        chatId = 1,
        senderId = 1,
        senderName = "Sarah",
        text = "Hey! Have you started testing the new WhatsApp UI design?",
        messageType = MessageType.TEXT,
        timestamp = now - 30 * min,
        status = MessageStatus.READ
      ),
      MessageEntity(
        id = 2,
        chatId = 1,
        senderId = 0,
        senderName = "You",
        text = "Yes! Implementing Jetpack Compose with Material 3 emerald styling right now.",
        messageType = MessageType.TEXT,
        timestamp = now - 22 * min,
        status = MessageStatus.READ
      ),
      MessageEntity(
        id = 3,
        chatId = 1,
        senderId = 1,
        senderName = "Sarah",
        text = "Voice note",
        messageType = MessageType.VOICE_NOTE,
        timestamp = now - 15 * min,
        status = MessageStatus.READ,
        audioDurationSec = 24,
        reaction = "❤️"
      ),
      MessageEntity(
        id = 4,
        chatId = 1,
        senderId = 1,
        senderName = "Sarah",
        text = "The new UI animations look sensational! 🔥",
        messageType = MessageType.TEXT,
        timestamp = now - 4 * min,
        status = MessageStatus.DELIVERED,
        reaction = "👍"
      )
    )
    database.messageDao().insertMessages(sarahMessages)

    // Prepopulate Statuses
    val initialStatuses = listOf(
      StatusEntity(
        id = 1,
        contactId = 1,
        contactName = "Sarah Connor",
        avatarType = AvatarType.SARAH,
        caption = "Sunset at the bay 🌅✨",
        textStatus = "",
        mediaBgColorHex = 0xFF8E24AA,
        timestamp = now - 35 * min,
        isViewed = false
      ),
      StatusEntity(
        id = 2,
        contactId = 2,
        contactName = "Alex Rivera",
        avatarType = AvatarType.ALEX,
        caption = "Hackathon weekend kicks off! 💻⚡",
        textStatus = "",
        mediaBgColorHex = 0xFF00897B,
        timestamp = now - 2 * hour,
        isViewed = false
      ),
      StatusEntity(
        id = 3,
        contactId = 3,
        contactName = "Priya Sharma",
        avatarType = AvatarType.PRIYA,
        caption = "",
        textStatus = "New design sprint starts on Monday! Excited 🎉",
        mediaBgColorHex = 0xFF0088CC,
        timestamp = now - 5 * hour,
        isViewed = true
      )
    )
    database.statusDao().insertStatuses(initialStatuses)

    // Prepopulate Calls
    val initialCalls = listOf(
      CallLogEntity(
        id = 1,
        contactId = 1,
        contactName = "Sarah Connor",
        avatarType = AvatarType.SARAH,
        callType = CallType.VIDEO,
        callDirection = CallDirection.INCOMING,
        timestamp = now - 20 * min,
        durationSec = 240
      ),
      CallLogEntity(
        id = 2,
        contactId = 2,
        contactName = "Alex Rivera",
        avatarType = AvatarType.ALEX,
        callType = CallType.VOICE,
        callDirection = CallDirection.MISSED,
        timestamp = now - 2 * hour,
        durationSec = 0
      ),
      CallLogEntity(
        id = 3,
        contactId = 3,
        contactName = "Priya Sharma",
        avatarType = AvatarType.PRIYA,
        callType = CallType.VOICE,
        callDirection = CallDirection.OUTGOING,
        timestamp = now - 1 * hour * 24,
        durationSec = 180
      )
    )
    database.callLogDao().insertCallLogs(initialCalls)
  }
}
