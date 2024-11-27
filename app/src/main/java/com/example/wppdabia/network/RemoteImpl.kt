package com.example.wppdabia.network

import com.example.wppdabia.data.ContactData
import com.example.wppdabia.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

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
                    storageRef.putFile(userData.profileImageUrl)
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
                    val user = snapshot.getValue(UserData::class.java)
                    onSuccess(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
        }
    }
}