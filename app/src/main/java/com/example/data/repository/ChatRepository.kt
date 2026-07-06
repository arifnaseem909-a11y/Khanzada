package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.database.AppDatabase
import com.example.data.model.ChatEntity
import com.example.data.model.MessageEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ChatRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val chatDao = db.chatDao()
    private val messageDao = db.messageDao()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // Exposed Flows for UI
    val chats: Flow<List<ChatEntity>> = chatDao.getAllChats()
    val users: Flow<List<UserEntity>> = userDao.getAllUsers()
    val syncedContacts: Flow<List<UserEntity>> = userDao.getSyncedContacts()

    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForChat(chatId)
    }

    suspend fun getUserById(id: String): UserEntity? {
        return userDao.getUserById(id)
    }

    // Initialize default premium contacts in database
    suspend fun initializeDefaultData() {
        val currentUsers = userDao.getAllUsers().first()
        if (currentUsers.isEmpty()) {
            val defaults = listOf(
                UserEntity(
                    id = "+923001234567",
                    displayName = "Mir Khanzada",
                    status = "Sardar of the clan. Secure calling only. 👑",
                    avatarUrl = "avatar_mir",
                    isOnline = true,
                    lastSeenText = "Online"
                ),
                UserEntity(
                    id = "+923219876543",
                    displayName = "Princess Zoya",
                    status = "Gold and Glassmorphism enthusiast. ✨ Living in the moment.",
                    avatarUrl = "avatar_zoya",
                    isOnline = true,
                    lastSeenText = "Online"
                ),
                UserEntity(
                    id = "+447700900077",
                    displayName = "Alexander",
                    status = "High-end developer. Designing the future. 💻",
                    avatarUrl = "avatar_alex",
                    isOnline = false,
                    lastSeenText = "Last seen 2 hours ago"
                ),
                UserEntity(
                    id = "+12025550143",
                    displayName = "Zara Malik",
                    status = "Voice notes are my aesthetic 🎙️. Send a wave!",
                    avatarUrl = "avatar_zara",
                    isOnline = true,
                    lastSeenText = "Online"
                )
            )
            userDao.insertUsers(defaults)

            // Create initial empty chats for them to fill the list elegantly
            for (user in defaults) {
                chatDao.insertChat(
                    ChatEntity(
                        id = user.id,
                        contactId = user.id,
                        lastMessageText = "Tap to start chatting with ${user.displayName}",
                        lastMessageTime = System.currentTimeMillis() - (defaults.indexOf(user) * 3600000),
                        unreadCount = 0
                    )
                )
            }
        }
    }

    // Secure contact sync simulation
    suspend fun syncContacts() {
        // Mark all current users as synced contacts
        val currentUsers = userDao.getAllUsers().first()
        for (user in currentUsers) {
            userDao.updateUser(user.copy(isContactSynced = true))
        }
    }

    // Send a message and kick off delivery ticks and auto-reply simulation
    suspend fun sendMessage(
        chatId: String,
        text: String,
        type: String = "TEXT",
        mediaUri: String? = null,
        audioDurationSec: Int = 0
    ) {
        val timestamp = System.currentTimeMillis()

        // 1. Insert message with PENDING status
        val msgId = messageDao.insertMessage(
            MessageEntity(
                chatId = chatId,
                text = text,
                timestamp = timestamp,
                isSentByMe = true,
                type = type,
                mediaUri = mediaUri,
                audioDurationSec = audioDurationSec,
                deliveryStatus = "PENDING"
            )
        )

        // Update active chat info
        val chatText = if (type == "AUDIO") "🎙️ Voice Note (${audioDurationSec}s)" else if (type == "IMAGE") "📷 Image Attachment" else text
        val currentChat = chatDao.getChatById(chatId)
        if (currentChat != null) {
            chatDao.updateChat(
                currentChat.copy(
                    lastMessageText = chatText,
                    lastMessageTime = timestamp
                )
            )
        } else {
            chatDao.insertChat(
                ChatEntity(
                    id = chatId,
                    contactId = chatId,
                    lastMessageText = chatText,
                    lastMessageTime = timestamp,
                    unreadCount = 0
                )
            )
        }

        // Trigger asynchronous simulation of receipt/ticks and replies
        scope.launch {
            // Pending -> Sent (400ms)
            delay(400)
            messageDao.updateMessageStatus(msgId, "SENT")

            // Sent -> Delivered (600ms)
            delay(600)
            messageDao.updateMessageStatus(msgId, "DELIVERED")

            // Delivered -> Read (800ms)
            delay(800)
            messageDao.updateMessageStatus(msgId, "READ")

            // Wait a second, then trigger contact "Typing..."
            delay(1000)
            chatDao.updateTypingStatus(chatId, true)

            // Dynamic Reply delay based on text length
            val replyDelay = (2000L + text.length * 20L).coerceAtMost(6000L)
            delay(replyDelay)

            // typing... -> false
            chatDao.updateTypingStatus(chatId, false)

            // Generate reply text
            val replyText = generateReplyText(chatId, text)
            val replyType = if (replyText.startsWith("[VOICE_NOTE]")) "AUDIO" else "TEXT"
            val processedReplyText = if (replyType == "AUDIO") "🎙️ Voice reply" else replyText
            val audioDur = if (replyType == "AUDIO") 8 else 0

            val replyTimestamp = System.currentTimeMillis()
            messageDao.insertMessage(
                MessageEntity(
                    chatId = chatId,
                    text = processedReplyText,
                    timestamp = replyTimestamp,
                    isSentByMe = false,
                    type = replyType,
                    audioDurationSec = audioDur,
                    deliveryStatus = "READ" // Incoming is already read once the user is in the screen, but we manage unread counts
                )
            )

            // Increment unread count if user is not in active screen (managed in ViewModel)
            val updatedChat = chatDao.getChatById(chatId)
            if (updatedChat != null) {
                chatDao.updateChat(
                    updatedChat.copy(
                        lastMessageText = if (replyType == "AUDIO") "🎙️ Voice Note (${audioDur}s)" else processedReplyText,
                        lastMessageTime = replyTimestamp
                    )
                )
            }
        }
    }

    suspend fun markMessagesAsRead(chatId: String) {
        messageDao.markMessagesAsRead(chatId)
        val chat = chatDao.getChatById(chatId)
        if (chat != null && chat.unreadCount > 0) {
            chatDao.updateChat(chat.copy(unreadCount = 0))
        }
    }

    suspend fun incrementUnreadCount(chatId: String) {
        val chat = chatDao.getChatById(chatId)
        if (chat != null) {
            chatDao.updateChat(chat.copy(unreadCount = chat.unreadCount + 1))
        }
    }

    suspend fun createOrUpdateUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    // Reply generation logic (incorporating Gemini REST endpoint if API key present)
    private suspend fun generateReplyText(contactId: String, userMsg: String): String {
        val contact = userDao.getUserById(contactId) ?: return "Hello! Secure Khanzada messaging is active."
        val apiKey = getApiKey()

        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are simulating a premium secure messenger contact in an app called "Khanzada".
                    You must reply to the user's message as a specific character.
                    
                    Your Character Profile:
                    - Name: ${contact.displayName}
                    - Status/Bio: ${contact.status}
                    
                    Rules:
                    1. Keep your response short, conversational, and highly engaging (1-2 sentences max).
                    2. Maintain your persona:
                       - If you are Mir Khanzada, sound like a wealthy, respectful, royal clan leader, wise, and slightly formal.
                       - If you are Princess Zoya, sound energetic, luxury-loving, modern, obsessed with gorgeous gold aesthetics and glassmorphism. Use ✨.
                       - If you are Alexander, sound like a sleek elite software engineer, passionate about fluent animations and 120fps Kotlin code.
                       - If you are Zara Malik, sound friendly, warm, and musical. Mention voice notes.
                    3. Do not break character. Do not say you are an AI.
                    
                    User's message: "$userMsg"
                    Your response:
                """.trimIndent()

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", prompt)
                                })
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("maxOutputTokens", 120)
                        put("temperature", 0.7)
                    })
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val request = Request.Builder()
                    .url(url)
                    .post(jsonBody.toString().toRequestBody(mediaType))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val respStr = response.body?.string()
                        if (!respStr.isNullOrEmpty()) {
                            val jsonObj = JSONObject(respStr)
                            val candidates = jsonObj.getJSONArray("candidates")
                            if (candidates.length() > 0) {
                                val firstCandidate = candidates.getJSONObject(0)
                                val contentObj = firstCandidate.getJSONObject("content")
                                val parts = contentObj.getJSONArray("parts")
                                if (parts.length() > 0) {
                                    return parts.getJSONObject(0).getString("text").trim()
                                }
                            }
                        }
                    } else {
                        Log.e("GeminiApi", "Error response: ${response.code} ${response.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("GeminiApi", "Failed to generate reply via Gemini, falling back to mock", e)
            }
        }

        // Offline / Fallback local Persona-based replies
        return when (contact.displayName) {
            "Mir Khanzada" -> {
                val replies = listOf(
                    "Salam. I am in a meeting with the elders regarding clan matters. What is the status of our project? 👑",
                    "Khanzada secure calling protocol is fully operational. We value absolute privacy here. 🔒",
                    "A wise decision. Let's arrange a secure call later. May respect guide our steps.",
                    "Excellent update. The Khanzada legacy demands only the finest execution."
                )
                replies.random()
            }
            "Princess Zoya" -> {
                val replies = listOf(
                    "Oh my god, have you seen the gorgeous glassmorphism card gradients on this screen? It's literally glowing gold! ✨",
                    "I am absolutely obsessed with how smooth these delivery ticks animate. Let's send a premium voice note! 🎙️",
                    "Yes, totally! I'm planning a luxury weekend trip, send some beautiful location ideas!",
                    "That is so elegant! The deep royal blue theme is exactly my aesthetic."
                )
                replies.random()
            }
            "Alexander" -> {
                val replies = listOf(
                    "Hey! This app's Jetpack Compose UI is running at a stable 120fps. Super polished. 💻",
                    "Just refactored the local Room database to synchronize reactive flows cleanly. Beautiful architecture.",
                    "A glassmorphism blurred background with a radial color scheme is the pinnacle of modern UX. Brilliant design choice.",
                    "Awesome. Let's schedule a code review to verify thread safety on our coroutines."
                )
                replies.random()
            }
            "Zara Malik" -> {
                val replies = listOf(
                    "[VOICE_NOTE] Hey! Just sending a quick voice note 🎙️. Hope your day is going beautifully!",
                    "Your message received. I absolutely love how clean the animated audio waveform matches our royal gold palette!",
                    "Talk to you in a bit! Recording a music track right now. ✨",
                    "Let's sync up later for a real voice call."
                )
                replies.random()
            }
            else -> "Message received securely. Khanzada Premium system stands ready. 🛡️"
        }
    }

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }
}
