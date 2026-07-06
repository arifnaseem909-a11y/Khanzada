package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatEntity
import com.example.data.model.MessageEntity
import com.example.data.model.UserEntity
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatWithUser(
    val chat: ChatEntity,
    val user: UserEntity
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChatRepository(application)

    // Current active chat room
    private val _activeChatId = MutableStateFlow<String?>(null)
    val activeChatId: StateFlow<String?> = _activeChatId.asStateFlow()

    // Current active contact
    private val _activeContact = MutableStateFlow<UserEntity?>(null)
    val activeContact: StateFlow<UserEntity?> = _activeContact.asStateFlow()

    // Is contacts sync in progress
    private val _isSyncingContacts = MutableStateFlow(false)
    val isSyncingContacts: StateFlow<Boolean> = _isSyncingContacts.asStateFlow()

    // My Profile State
    private val _myProfileName = MutableStateFlow("Al-Khanzada")
    val myProfileName: StateFlow<String> = _myProfileName.asStateFlow()

    private val _myProfileStatus = MutableStateFlow("Securely Encrypted. 🛡️")
    val myProfileStatus: StateFlow<String> = _myProfileStatus.asStateFlow()

    private val _myProfilePhone = MutableStateFlow("+923000000000")
    val myProfilePhone: StateFlow<String> = _myProfilePhone.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultData()
        }
    }

    // Expose combined chats and user details
    val chatsWithUser: StateFlow<List<ChatWithUser>> = combine(
        repository.chats,
        repository.users
    ) { chats, users ->
        chats.mapNotNull { chat ->
            val user = users.find { it.id == chat.contactId }
            if (user != null) {
                ChatWithUser(chat, user)
            } else {
                null
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Expose contact list
    val contactsList: StateFlow<List<UserEntity>> = repository.users
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Expose current chat's messages
    val activeChatMessages: StateFlow<List<MessageEntity>> = _activeChatId
        .flatMapLatest { chatId ->
            if (chatId != null) {
                repository.getMessagesForChat(chatId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setActiveChat(chatId: String?) {
        _activeChatId.value = chatId
        if (chatId != null) {
            viewModelScope.launch {
                repository.markMessagesAsRead(chatId)
                _activeContact.value = repository.getUserById(chatId)
            }
        } else {
            _activeContact.value = null
        }
    }

    fun sendMessage(text: String) {
        val chatId = _activeChatId.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(chatId, text, type = "TEXT")
        }
    }

    fun sendVoiceNote(durationSec: Int) {
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                text = "Voice Note (${durationSec}s)",
                type = "AUDIO",
                audioDurationSec = durationSec
            )
        }
    }

    fun sendImageMessage(description: String) {
        val chatId = _activeChatId.value ?: return
        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                text = description.ifEmpty { "Shared an image" },
                type = "IMAGE"
            )
        }
    }

    fun syncContacts() {
        viewModelScope.launch {
            _isSyncingContacts.value = true
            repository.syncContacts()
            _isSyncingContacts.value = false
        }
    }

    fun updateProfile(name: String, status: String) {
        _myProfileName.value = name
        _myProfileStatus.value = status
    }
}
