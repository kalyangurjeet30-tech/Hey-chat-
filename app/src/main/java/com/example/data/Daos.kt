package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
  @Query("SELECT * FROM contacts ORDER BY name ASC")
  fun getAllContacts(): Flow<List<ContactEntity>>

  @Query("SELECT * FROM contacts WHERE id = :id")
  suspend fun getContactById(id: Long): ContactEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertContacts(contacts: List<ContactEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertContact(contact: ContactEntity): Long
}

@Dao
interface ChatDao {
  @Query("SELECT * FROM chats WHERE isArchived = 0 ORDER BY isPinned DESC, lastMessageTime DESC")
  fun getActiveChats(): Flow<List<ChatEntity>>

  @Query("SELECT * FROM chats WHERE isArchived = 1 ORDER BY lastMessageTime DESC")
  fun getArchivedChats(): Flow<List<ChatEntity>>

  @Query("SELECT * FROM chats WHERE id = :id")
  fun getChatById(id: Long): Flow<ChatEntity?>

  @Query("SELECT * FROM chats WHERE contactId = :contactId LIMIT 1")
  suspend fun findChatByContactId(contactId: Long): ChatEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChats(chats: List<ChatEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChat(chat: ChatEntity): Long

  @Update
  suspend fun updateChat(chat: ChatEntity)

  @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
  suspend fun markAsRead(chatId: Long)

  @Query("UPDATE chats SET isPinned = NOT isPinned WHERE id = :chatId")
  suspend fun togglePin(chatId: Long)

  @Query("UPDATE chats SET isMuted = NOT isMuted WHERE id = :chatId")
  suspend fun toggleMute(chatId: Long)

  @Query("UPDATE chats SET isArchived = NOT isArchived WHERE id = :chatId")
  suspend fun toggleArchive(chatId: Long)

  @Query("UPDATE chats SET isFavourite = NOT isFavourite WHERE id = :chatId")
  suspend fun toggleFavourite(chatId: Long)

  @Query("DELETE FROM chats WHERE id = :chatId")
  suspend fun deleteChat(chatId: Long)
}

@Dao
interface MessageDao {
  @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
  fun getMessagesForChat(chatId: Long): Flow<List<MessageEntity>>

  @Query("SELECT * FROM messages WHERE isStarred = 1 ORDER BY timestamp DESC")
  fun getStarredMessages(): Flow<List<MessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessages(messages: List<MessageEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: MessageEntity): Long

  @Query("UPDATE messages SET reaction = :reaction WHERE id = :messageId")
  suspend fun updateReaction(messageId: Long, reaction: String)

  @Query("UPDATE messages SET isStarred = NOT isStarred WHERE id = :messageId")
  suspend fun toggleStar(messageId: Long)

  @Query("DELETE FROM messages WHERE id = :messageId")
  suspend fun deleteMessage(messageId: Long)

  @Query("DELETE FROM messages WHERE chatId = :chatId")
  suspend fun deleteMessagesForChat(chatId: Long)
}

@Dao
interface StatusDao {
  @Query("SELECT * FROM statuses ORDER BY isMyStatus DESC, timestamp DESC")
  fun getAllStatuses(): Flow<List<StatusEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStatuses(statuses: List<StatusEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStatus(status: StatusEntity): Long

  @Query("UPDATE statuses SET isViewed = 1 WHERE id = :statusId")
  suspend fun markAsViewed(statusId: Long)
}

@Dao
interface CallLogDao {
  @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
  fun getAllCallLogs(): Flow<List<CallLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCallLogs(logs: List<CallLogEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCallLog(log: CallLogEntity): Long
}
