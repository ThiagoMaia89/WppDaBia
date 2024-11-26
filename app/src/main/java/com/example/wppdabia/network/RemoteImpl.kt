package com.example.wppdabia.network

import com.example.wppdabia.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class RemoteImpl : Remote {
    override suspend fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val storage = FirebaseStorage.getInstance()
        val database = FirebaseDatabase.getInstance()

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
}