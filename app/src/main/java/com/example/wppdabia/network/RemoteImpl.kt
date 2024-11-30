package com.example.wppdabia.network

import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.wppdabia.data.ContactData
import com.example.wppdabia.data.MessageData
import com.example.wppdabia.data.UserData
import com.example.wppdabia.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RemoteImpl : Remote {

    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override suspend fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(userData.email, userData.password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                if (userData.profileImageUrl != null) {
                    val storageRef = storage.reference.child("profile_images/$userId.jpg")
                    storageRef.putFile(userData.profileImageUrl.toUri())
                        .addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                val user = mapOf(
                                    "name" to userData.name,
                                    "email" to userData.email,
                                    "profileImageUrl" to downloadUri.toString()
                                )

                                database.reference.child("users").child(userId)
                                    .setValue(user)
                                    .addOnSuccessListener {
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        onError("Erro ao salvar dados: ${e.message}")
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            onError("Erro ao fazer upload da imagem: ${e.message}")
                        }
                } else {
                    val user = mapOf(
                        "name" to userData.name,
                        "email" to userData.email
                    )

                    database.reference.child("users").child(userId)
                        .setValue(user)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onError("Erro ao salvar dados: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                onError("Erro ao criar usuário: ${e.message}")
            }
    }

    override fun loginUser(userData: UserData, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(userData.email, userData.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.localizedMessage ?: "Erro desconhecido")
                }
            }
    }

    override suspend fun getAllContacts(
        onSuccess: (List<ContactData>) -> Unit,
        onError: (String) -> Unit
    ) {
        database.reference.child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userList = mutableListOf<ContactData>()
                    for (contactSnapshot in snapshot.children) {
                        val contact = contactSnapshot.getValue(ContactData::class.java)
                            ?.copy(id = contactSnapshot.key ?: "")
                        contact?.let { userList.add(it) }
                    }
                    onSuccess(userList)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    override suspend fun getCurrentUser(
        onSuccess: (UserData?) -> Unit,
        onError: (String) -> Unit
    ) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val reference = database.getReference("users").child(currentUser.uid)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserData::class.java)?.copy(uid = currentUser.uid)
                    onSuccess(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
        }
    }

    override suspend fun sendMessage(chatId: String, message: MessageData) {
        val chatRef = database.getReference("chats/$chatId")
        chatRef.push().setValue(message)
    }

    override suspend fun fetchMessages(chatId: String): Flow<List<MessageData>> {
        val chatRef = database.getReference("chats/$chatId")
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = snapshot.children.mapNotNull {
                        it.getValue(MessageData::class.java)
                    }
                    trySend(messages)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            chatRef.addValueEventListener(listener)
            awaitClose { chatRef.removeEventListener(listener) }
        }
    }

    override suspend fun getAllChats(
        currentUserUid: String,
        onSuccess: (List<ContactData>) -> Unit,
        onError: (String) -> Unit
    ) {
        val databaseRef = database.reference.child("chats")

        databaseRef.orderByKey()
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatPreviews = mutableListOf<ContactData>()

                    for (chatSnapshot in snapshot.children) {
                        val chatId = chatSnapshot.key ?: continue

                        if (chatId.contains(currentUserUid)) {
                            val lastMessageSnapshot = chatSnapshot.children.lastOrNull()
                            val lastMessage = lastMessageSnapshot?.getValue(MessageData::class.java)

                            if (lastMessage != null) {
                                val contactUid = chatId.replace(currentUserUid, "").replace("-", "")

                                database.reference.child("users").child(contactUid)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(contactSnapshot: DataSnapshot) {
                                            val contactName = contactSnapshot.child("name").value as? String ?: "Desconhecido"
                                            val contactImage = contactSnapshot.child("profileImageUrl").value as? String
                                            val email = contactSnapshot.child("email").value as? String

                                            chatPreviews.add(
                                                ContactData(
                                                    id = contactUid,
                                                    name = contactName,
                                                    email = email ?: "",
                                                    profileImageUrl = contactImage ?: "",
                                                    lastMessage = lastMessage.content,
                                                    timestamp = lastMessage.timestamp,
                                                    chatId = chatId
                                                )
                                            )

                                            val sortedChatPreviews = chatPreviews.sortedByDescending {
                                                it.timestamp
                                            }
                                            onSuccess(sortedChatPreviews)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            onError(error.message)
                                        }
                                    })
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    override fun logout() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}