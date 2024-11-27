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

    fun sendMessage(content: String) {
        val newMessage = MessageData(
            sender = "user_id", // Substitua pelo ID do remetente
            content = content,
            timestamp = getCurrentTimestamp(),
            isSentByUser = true
        )
      //  repository.sendMessage(newMessage)
    }

    fun sendPhoto() {
        // Lógica para enviar uma foto
        // Você pode usar um seletor de imagens aqui ou capturar da câmera
    }

    private fun getCurrentTimestamp(): String {
        // Gera o timestamp no formato desejado
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

}