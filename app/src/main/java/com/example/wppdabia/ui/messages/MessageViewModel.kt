package com.example.wppdabia.ui.messages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wppdabia.data.MessageData
import com.example.wppdabia.data.UserData
import com.example.wppdabia.network.Remote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(private val remote: Remote) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageData>>(emptyList())
    val messages: StateFlow<List<MessageData>> = _messages

    private val currentUser = MutableLiveData<UserData?>()

    private val _isSentByUser = MutableStateFlow(false)

    init {
        viewModelScope.launch { getCurrentUser() }
    }

    fun fetchMessages(chatId: String) {
        viewModelScope.launch {
            remote.fetchMessages(chatId = chatId).collect { fetchedMessages ->
                _messages.value = fetchedMessages
            }
        }
    }

    fun sendMessage(chatId: String, contactId: String, content: String, lastMessage: String) {
        val senderId = currentUser.value?.uid
        _isSentByUser.value = senderId != contactId
        val newMessage = MessageData(
            sender = currentUser.value ?: UserData(),
            content = content,
            timestamp = getCurrentTimestamp(),
            isSentByUser = _isSentByUser.value,
            lastMessage = lastMessage
        )
        viewModelScope.launch { remote.sendMessage(chatId, newMessage) }
    }

    private suspend fun getCurrentUser() {
        remote.getCurrentUser(
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
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

}