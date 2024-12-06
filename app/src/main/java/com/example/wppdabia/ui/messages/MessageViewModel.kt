package com.example.wppdabia.ui.messages

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wppdabia.data.MessageData
import com.example.wppdabia.data.UserData
import com.example.wppdabia.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageData>>(emptyList())
    val messages: StateFlow<List<MessageData>> = _messages

    private var _capturedImageUri = MutableLiveData<String?>()
    val capturedImageUri: LiveData<String?> = _capturedImageUri

    private val currentUser = MutableLiveData<UserData?>()

    private val _isSentByUser = MutableStateFlow(false)

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    init {
        viewModelScope.launch { getCurrentUser() }
    }

    fun fetchMessages(chatId: String) {
        viewModelScope.launch {
            repository.fetchMessages(chatId = chatId).collect { fetchedMessages ->
                _messages.value = fetchedMessages
            }
        }
    }

    fun sendMessage(
        chatId: String,
        contactId: String,
        text: String,
        image: String?,
        lastMessage: String
    ) {
        _isUploading.value = true
        val senderId = currentUser.value?.uid
        _isSentByUser.value = senderId != contactId
        val newMessage = MessageData(
            sender = currentUser.value ?: UserData(),
            messageText = text,
            messageImage = image,
            timestamp = getCurrentTimestamp(),
            isSentByUser = _isSentByUser.value,
            lastMessage = lastMessage
        )
        viewModelScope.launch {
            repository.sendMessage(
                chatId,
                newMessage,
                onSuccess = {
                    _isUploading.value = false
                },
                onError = {
                    _isUploading.value = false
                }
            )
        }
    }

    fun saveCapturedImage(uri: Uri) {
        _capturedImageUri.value = uri.toString()
    }

    fun setMessageAsRead(chatId: String) {
        if (currentUser.value?.uid != null) {
            viewModelScope.launch {
                repository.markMessagesAsRead(chatId, currentUser.value?.uid!!)
            }
        }
    }

    fun cleanImageSent() {
        _capturedImageUri.value = null
    }

    private suspend fun getCurrentUser() {
        repository.getCurrentUser(
            onSuccess = { userData ->
                currentUser.value = userData
            },
            onError = {
                currentUser.value = null
            }
        )
    }

    fun sendPhoto() {
        //TODO: Enviar foto
    }

    private fun getCurrentTimestamp(): String {
        val hours = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val date = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date())
        return "$hours - $date"
    }
}