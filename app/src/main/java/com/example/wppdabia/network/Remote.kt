package com.example.wppdabia.network

import com.example.wppdabia.data.ContactData
import com.example.wppdabia.data.UserData

interface Remote {

    suspend fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun loginUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun getAllContacts(
        onSuccess: (List<ContactData>) -> Unit,
        onError: (String) -> Unit
    )

    suspend fun getCurrentUser(
        onSuccess: (UserData?) -> Unit,
        onError: (String) -> Unit
    )
}