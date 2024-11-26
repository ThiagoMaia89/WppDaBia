package com.example.wppdabia.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wppdabia.data.ContactData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactsViewModel @Inject constructor() : ViewModel() {
    private val _contacts = MutableLiveData<List<ContactData>>()
    val contacts: LiveData<List<ContactData>> = _contacts

    private val database = FirebaseDatabase.getInstance().reference

    var errorMessage = MutableLiveData<String>()

    init {
        getContacts()
    }

    private fun getContacts() {
        viewModelScope.launch {
            database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userList = mutableListOf<ContactData>()
                    for (contactSnapshot in snapshot.children) {
                        val contact = contactSnapshot.getValue(ContactData::class.java)?.copy(id = contactSnapshot.key ?: "")
                        contact?.let { userList.add(it) }
                    }
                    _contacts.value = userList
                }

                override fun onCancelled(error: DatabaseError) {
                    errorMessage.value = error.message
                }
            })
        }
    }
}