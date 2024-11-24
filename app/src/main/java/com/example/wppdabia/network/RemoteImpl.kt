package com.example.wppdabia.network

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class RemoteImpl : Remote {
    override suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        profileImageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val storage = FirebaseStorage.getInstance()
        val database = FirebaseDatabase.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                if (profileImageUri != null) {
                    val storageRef = storage.reference.child("profile_images/$userId.jpg")
                    storageRef.putFile(profileImageUri)
                        .addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                val userData = mapOf(
                                    "name" to name,
                                    "email" to email,
                                    "profileImageUrl" to downloadUri.toString()
                                )

                                database.reference.child("users").child(userId)
                                    .setValue(userData)
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
                    val userData = mapOf(
                        "name" to name,
                        "email" to email
                    )

                    database.reference.child("users").child(userId)
                        .setValue(userData)
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