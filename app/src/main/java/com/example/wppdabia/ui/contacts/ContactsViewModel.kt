package com.example.wppdabia.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wppdabia.data.ContactData
import com.example.wppdabia.network.Remote
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    var errorMessage = MutableLiveData<String>()

    init {
        getContacts()
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
}