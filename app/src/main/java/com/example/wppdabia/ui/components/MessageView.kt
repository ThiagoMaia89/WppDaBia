package com.example.wppdabia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wppdabia.data.MessageData
import com.example.wppdabia.data.UserData
import com.example.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun MessageView(messageData: MessageData, isSentByUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
    ) {
        val topStartShape = if (isSentByUser) 8.dp else 0.dp
        val topEndShape = if (isSentByUser) 0.dp else 8.dp
        Column(
            modifier = Modifier
                .background(
                    if (isSentByUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(topStart = topStartShape, topEnd = topEndShape, bottomStart = 8.dp, bottomEnd = 8.dp)
                )
                .padding(8.dp)
        ) {
            Text(text = messageData.content, color = MaterialTheme.colorScheme.onPrimary)
            Text(text = messageData.timestamp, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageViewPreview() {
    WppDaBiaTheme {
        Column {
            MessageView(
                messageData = MessageData(
                    sender = UserData(name = "Thiago Maia"),
                    content = "Oi filhota! Tudo bem?",
                    timestamp = "10:00 AM",
                    isSentByUser = true
                ),
                isSentByUser = true
            )
            MessageView(
                messageData = MessageData(
                    sender = UserData(name ="Beatriz Maia"),
                    content = "Olá papai! Tudo bem sim!",
                    timestamp = "10:05 AM",
                    isSentByUser = false
                ),
                isSentByUser = false
            )
        }
    }
}