/* eslint-disable */
const { onValueCreated } = require("firebase-functions/v2/database");
const admin = require('firebase-admin');

admin.initializeApp();

exports.sendNotification = onValueCreated(
  '/chats/{chatId}/{messageId}',
  async (event) => {
    console.log('sendNotification triggered');
    const message = event.data.val();
    console.log('Message data:', message);

    if (!message || !message.sender) {
      console.log('Message or sender is missing');
      return;
    }
    const chatId = event.params.chatId;

    try {
      
      const recipientId = message.recipientId;
      if (!recipientId) {
        console.log('Recipient ID is missing');
        return;
      }

      const userSnapshot = await admin.database().ref(`/users/${recipientId}/fcmToken`).get();
      if (!userSnapshot.exists()) {
        console.log(`No FCM token found for user ${recipientId}`);
        return;
      }
      
      const recipientToken = userSnapshot.val();

      const payload = {
        notification: {
          title: message.sender.name || 'Nova Mensagem',
          body: message.messageText || 'Você recebeu uma nova mensagem',
        },
        data: {
          senderId: message.sender.uid,
          chatId: chatId,
          messageText: message.messageText,
        },
      };

      await admin.messaging().send({
        token: recipientToken,
        ...payload,
      });

      console.log(`Notification sent to user ${recipientId}`);
    
    } catch (error) {
      console.error('Error sending notification:', error);
    }
  }
);