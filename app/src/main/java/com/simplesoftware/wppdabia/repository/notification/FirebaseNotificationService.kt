package com.simplesoftware.wppdabia.repository.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.simplesoftware.wppdabia.R
import com.simplesoftware.wppdabia.domain.utils.ChatStateManager
import com.simplesoftware.wppdabia.ui.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var chatStateManager: ChatStateManager

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val senderId = message.data["senderId"]

        if (chatStateManager.activeChatUserId.value == senderId) {
            return
        }

        val name = message.notification?.title
        val lastMessage = message.notification?.body

        if (name != null && lastMessage != null) {
            sendNotification(name, lastMessage)
        }
    }

    private fun sendNotification(name: String, lastMessage: String) {

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(name)
            .setContentText(lastMessage)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Novo token gerado: $token")
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val databaseRef = FirebaseDatabase.getInstance().getReference("users/$userId/fcmToken")
            databaseRef.setValue(token)
                .addOnSuccessListener {
                    Log.d("FCM", "Novo token registrado no backend")
                }
                .addOnFailureListener { exception ->
                    Log.e("FCM", "Falha ao registrar o novo token no backend", exception)
                }
        }
    }

    companion object {
        const val CHANNEL_ID = "message_notifications"
        const val CHANNEL_NAME = "Notificações"
    }

}