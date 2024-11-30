package com.example.wppdabia.network

import androidx.navigation.NavController
import com.example.wppdabia.data.ContactData
import com.example.wppdabia.data.MessageData
import com.example.wppdabia.data.UserData
import kotlinx.coroutines.flow.Flow

interface Remote {

    suspend fun registerUser(userData: UserData, onSuccess: () -> Unit, onError: (String) -> Unit)

    fun loginUser(userData: UserData, onSuccess: () -> Unit, onError: (String) -> Unit)

    suspend fun getAllContacts(onSuccess: (List<ContactData>) -> Unit, onError: (String) -> Unit)

    suspend fun getCurrentUser(onSuccess: (UserData?) -> Unit, onError: (String) -> Unit)

    suspend fun sendMessage(chatId: String, message: MessageData)

    suspend fun fetchMessages(chatId: String): Flow<List<MessageData>>

    suspend fun getAllChats(currentUserUid: String, onSuccess: (List<ContactData>) -> Unit, onError: (String) -> Unit)

    fun logout()
}