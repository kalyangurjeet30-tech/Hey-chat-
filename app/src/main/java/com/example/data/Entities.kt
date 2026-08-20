package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageType {
  TEXT,
  IMAGE,
  VOICE_NOTE,
  DOCUMENT,
  LOCATION,
  CONTACT
}

enum class MessageStatus {
  PENDING,
  SENT,
  DELIVERED,
  READ
}

enum class CallDirection {
  INCOMING,
  OUTGOING,
  MISSED
}

enum class CallType {
  VOICE,
  VIDEO
}

enum class AvatarType {
  DEFAULT,
  SARAH,
  ALEX,
  PRIYA,
  MICHAEL,
  EMMA,
  GROUP_TECH,
  GROUP_FAMILY
}

@Entity(tableName = "contacts")
data class ContactEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val phoneNumber: String,
  val avatarType: AvatarType = AvatarType.DEFAULT,
  val statusAbout: String = "Available",
  val isOnline: Boolean = false,
  val lastSeenText: String = "last seen recently",
  val colorHex: Long = 0xFF00A884
)

@Entity(tableName = "chats")
data class ChatEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val contactId: Long,
  val name: String,
  val isGroup: Boolean = false,
  val unreadCount: Int = 0,
  val isPinned: Boolean = false,
  val isMuted: Boolean = false,
  val isArchived: Boolean = false,
  val isFavourite: Boolean = false,
  val lastMessageText: String = "",
  val lastMessageTime: Long = System.currentTimeMillis(),
  val lastMessageStatus: MessageStatus = MessageStatus.READ,
  val lastMessageFromMe: Boolean = false
)

@Entity(tableName = "messages")
data class MessageEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val chatId: Long,
  val senderId: Long, // 0 = Me, other = ContactId
  val senderName: String = "",
  val text: String,
  val messageType: MessageType = MessageType.TEXT,
  val timestamp: Long = System.currentTimeMillis(),
  val status: MessageStatus = MessageStatus.READ,
  val mediaCaption: String = "",
  val audioDurationSec: Int = 0,
  val reaction: String = "",
  val isStarred: Boolean = false,
  val replyToMessageId: Long? = null,
  val replyToText: String? = null,
  val replyToSenderName: String? = null
)

@Entity(tableName = "statuses")
data class StatusEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val contactId: Long,
  val contactName: String,
  val avatarType: AvatarType = AvatarType.DEFAULT,
  val caption: String = "",
  val textStatus: String = "",
  val mediaBgColorHex: Long = 0xFF008069,
  val timestamp: Long = System.currentTimeMillis(),
  val isViewed: Boolean = false,
  val isMyStatus: Boolean = false
)

@Entity(tableName = "call_logs")
data class CallLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val contactId: Long,
  val contactName: String,
  val avatarType: AvatarType = AvatarType.DEFAULT,
  val callType: CallType = CallType.VOICE,
  val callDirection: CallDirection = CallDirection.INCOMING,
  val timestamp: Long = System.currentTimeMillis(),
  val durationSec: Int = 0
)
