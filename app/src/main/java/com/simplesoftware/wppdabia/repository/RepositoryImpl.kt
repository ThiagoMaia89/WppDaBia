package com.simplesoftware.wppdabia.repository

import androidx.core.net.toUri
import com.simplesoftware.wppdabia.data.ContactData
import com.simplesoftware.wppdabia.data.MessageData
import com.simplesoftware.wppdabia.data.UserData
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.CHATS
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.CHAT_IMAGES
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.CREATE_USER_ERROR
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.DELETE_IMAGE_ERROR
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.EMAIL
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.FCM_TOKEN
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.NAME
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.OBTAIN_IMAGE_ERROR
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.PROFILE_IMAGES
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.PROFILE_IMAGE_URL
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.SAVE_DATA_ERROR
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.SAVE_IMAGE_ERROR
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.UNKNOWN_ERROR
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.UPLOAD_IMAGE_ERROR
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.USERS
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.USER_NOT_AUTHENTICATED_ERROR
import com.simplesoftware.wppdabia.repository.FirebasePathConstants.WAS_READ
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RepositoryImpl : Repository {

    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override suspend fun registerUserToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            } else {
                val token = task.result
                val currentUser = auth.currentUser
                val userUpdates = hashMapOf<String, Any>(
                    FCM_TOKEN to token
                )
                if (currentUser != null) {
                    val databaseReference = database.getReference(USERS).child(currentUser.uid)
                    databaseReference.updateChildren(userUpdates)
                }
            }
        }
    }

    override suspend fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(userData.email, userData.password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                if (userData.profileImageUrl != null) {
                    val storageRef = storage.reference.child("$PROFILE_IMAGES/$userId.jpg")
                    storageRef.putFile(userData.profileImageUrl!!.toUri())
                        .addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                val user = mapOf(
                                    NAME to userData.name,
                                    EMAIL to userData.email,
                                    PROFILE_IMAGE_URL to downloadUri.toString()
                                )

                                database.reference.child(USERS).child(userId)
                                    .setValue(user)
                                    .addOnSuccessListener {
                                        onSuccess()
                                    }
                                    .addOnFailureListener {
                                        onError(SAVE_DATA_ERROR)
                                    }
                            }
                        }
                        .addOnFailureListener {
                            onError(UPLOAD_IMAGE_ERROR)
                        }
                } else {
                    val user = mapOf(
                        NAME to userData.name,
                        EMAIL to userData.email
                    )

                    database.reference.child(USERS).child(userId)
                        .setValue(user)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onError(SAVE_DATA_ERROR)
                        }
                }
            }
            .addOnFailureListener {
                onError(CREATE_USER_ERROR)
            }
    }

    override fun loginUser(userData: UserData, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(userData.email, userData.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.localizedMessage ?: UNKNOWN_ERROR)
                }
            }
    }

    override suspend fun getAllContacts(
        onSuccess: (List<ContactData>) -> Unit,
        onError: (String) -> Unit
    ) {
        database.reference.child(USERS)
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
            val reference = database.getReference(USERS).child(currentUser.uid)
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

    override suspend fun sendMessage(
        chatId: String,
        message: MessageData,
        recipientId: String,
        onSuccess: () -> Unit,
        onError: () -> Unit,
        onTemporaryMessageAdded: (MessageData) -> Unit,
    ) {
        val chatRef = database.getReference("$CHATS/$chatId")
        val newMessageRef = chatRef.push()

        if (message.messageImage != null) {

            val tempMessage = message.copy(messageImage = message.messageImage.toUri().toString())
            onTemporaryMessageAdded.invoke(tempMessage)

            val storageRef = storage.reference.child(
                "$CHAT_IMAGES/$chatId/${
                    message.timestamp.replace(
                        "/",
                        ""
                    )
                }.jpg"
            )
            storageRef.putFile(message.messageImage.toUri())
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val messageWithImage = message.copy(
                            recipientId = recipientId,
                            messageImage = downloadUri.toString()
                        )
                        newMessageRef.setValue(messageWithImage)
                            .addOnSuccessListener {
                                onSuccess.invoke()
                            }
                            .addOnFailureListener {
                                onError.invoke()
                            }
                    }
                }
                .addOnFailureListener {
                    onError.invoke()
                }
        } else {
            newMessageRef.setValue(message.copy(recipientId = recipientId))
                .addOnSuccessListener {
                    onSuccess.invoke()
                }
                .addOnFailureListener {
                    onError.invoke()
                }
        }
    }

    override suspend fun fetchMessages(chatId: String): Flow<List<MessageData>> {
        val chatRef = database.getReference("$CHATS/$chatId")
        val userRef = database.getReference(USERS)

        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = snapshot.children.mapNotNull {
                        it.getValue(MessageData::class.java)
                    }
                    messages.forEach { message ->
                        val senderRef = userRef.child("${message.sender.uid}/$PROFILE_IMAGE_URL")
                        senderRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(senderSnapshot: DataSnapshot) {
                                message.sender.profileImageUrl =
                                    senderSnapshot.getValue(String::class.java)
                                trySend(messages)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                close(error.toException())
                            }
                        })
                    }
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
        val databaseRef = database.reference.child(CHATS)

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

                                database.reference.child(USERS).child(contactUid)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(contactSnapshot: DataSnapshot) {
                                            val contactName =
                                                contactSnapshot.child(NAME).value as? String ?: ":("
                                            val contactImage =
                                                contactSnapshot.child(PROFILE_IMAGE_URL).value as? String
                                            val email =
                                                contactSnapshot.child(EMAIL).value as? String

                                            chatPreviews.add(
                                                ContactData(
                                                    id = contactUid,
                                                    name = contactName,
                                                    email = email ?: "",
                                                    profileImageUrl = contactImage ?: "",
                                                    lastMessage = lastMessage,
                                                    timestamp = lastMessage.timestamp,
                                                    chatId = chatId,
                                                    wasRead = lastMessage.wasRead
                                                )
                                            )

                                            val sortedChatPreviews =
                                                chatPreviews.sortedByDescending {
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

    override suspend fun updateProfileImage(
        newImageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError(USER_NOT_AUTHENTICATED_ERROR)
            return
        }
        val userId = currentUser.uid
        val storageRef = storage.reference.child("$PROFILE_IMAGES/$userId.jpg")

        storageRef.putFile(newImageUrl.toUri())
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    database.reference.child(USERS).child(userId)
                        .child(PROFILE_IMAGE_URL)
                        .setValue(downloadUri.toString())
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onError(SAVE_IMAGE_ERROR)
                        }
                }.addOnFailureListener {
                    onError(OBTAIN_IMAGE_ERROR)
                }
            }
            .addOnFailureListener {
                onError(UPLOAD_IMAGE_ERROR)
            }
    }

    override suspend fun markMessagesAsRead(chatId: String, currentUserId: String) {
        val chatRef = database.getReference("$CHATS/$chatId")

        chatRef.get().addOnSuccessListener { snapshot ->
            for (messageSnapshot in snapshot.children) {
                val message = messageSnapshot.getValue(MessageData::class.java)
                val messageKey = messageSnapshot.key

                if (message != null && !message.wasRead && message.sender.uid != currentUserId) {
                    chatRef.child(messageKey!!).child(WAS_READ).setValue(true)
                }
            }
        }
    }

    override suspend fun deletePhoto(
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val photoRef = storage.reference.child("$PROFILE_IMAGES/$userId.jpg")

        photoRef.delete()
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("$USERS/$userId/$PROFILE_IMAGE_URL")
                    .removeValue()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(DELETE_IMAGE_ERROR) }
            }
            .addOnFailureListener { onError(DELETE_IMAGE_ERROR) }
    }

    override fun logout() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}