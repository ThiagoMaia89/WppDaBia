package com.example.wppdabia.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.wppdabia.data.ContactData
import com.example.wppdabia.data.UserData
import com.example.wppdabia.network.Remote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(private val remote: Remote) : ViewModel() {
    private val _contacts = MutableLiveData<List<ContactData>>()
    val contacts: LiveData<List<ContactData>> = _contacts

    private val _contactsLoading = MutableStateFlow(false)
    val contactsLoading: StateFlow<Boolean> = _contactsLoading

    private val currentUser = MutableLiveData<UserData?>()

    var errorMessage = MutableLiveData<String>()

    init {
        getContacts()
        viewModelScope.launch { getCurrentUser() }
    }

    private fun getContacts() {
        _contactsLoading.value = true
        viewModelScope.launch {
            remote.getAllContacts(
                onSuccess = { contactsList ->
                    _contactsLoading.value = false
                    _contacts.value = contactsList
                },
                onError = { message ->
                    _contactsLoading.value = false
                    errorMessage.value = message
                }
            )
        }
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

    fun navigateToChat(contactId: String, navController: NavController) {
        val chatId = generateChatId(currentUser.value?.uid ?: "", contactId)
        navController.navigate("messages/$chatId/$contactId")
    }

    private fun generateChatId(user1: String, user2: String): String {
        return if (user1 < user2) "$user1-$user2" else "$user2-$user1"
    }
}