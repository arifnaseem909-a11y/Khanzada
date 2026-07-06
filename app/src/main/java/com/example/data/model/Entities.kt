package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // Phone number or unique ID
    val displayName: String,
    val status: String,
    val avatarUrl: String, // Drawable resource name or local path
    val isOnline: Boolean,
    val lastSeenText: String,
    val isContactSynced: Boolean = false
)

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String, // Chat session ID (corresponds to contactId for 1-on-1 chats)
    val contactId: String,
    val lastMessageText: String,
    val lastMessageTime: Long,
    val unreadCount: Int,
    val isTyping: Boolean = false
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chatId: String,
    val text: String,
    val timestamp: Long,
    val isSentByMe: Boolean,
    val type: String, // "TEXT", "AUDIO", "IMAGE"
    val mediaUri: String? = null,
    val audioDurationSec: Int = 0,
    val deliveryStatus: String // "PENDING", "SENT", "DELIVERED", "READ"
)
