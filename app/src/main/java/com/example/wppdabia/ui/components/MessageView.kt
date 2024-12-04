package com.example.wppdabia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.wppdabia.data.MessageData
import com.example.wppdabia.data.UserData
import com.example.wppdabia.ui.extensions.getDateFromTimeStamp
import com.example.wppdabia.ui.extensions.getHourFromTimeStamp
import com.example.wppdabia.ui.extensions.getInitials
import com.example.wppdabia.ui.theme.Typography
import com.example.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun MessageView(messageData: MessageData, isSentByUser: Boolean) {
    val datePaddingStart = if (isSentByUser) 0.dp else 40.dp
    val datePaddingEnd = if (isSentByUser) 40.dp else 0.dp
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = datePaddingStart, end = datePaddingEnd),
        text = messageData.timestamp.getDateFromTimeStamp(),
        textAlign = if (isSentByUser) TextAlign.End else TextAlign.Start,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.primary
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp).padding(bottom = 8.dp),
        horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
    ) {
        val topStartShape = if (isSentByUser) 8.dp else 0.dp
        val topEndShape = if (isSentByUser) 0.dp else 8.dp

        if (isSentByUser) {
            Column(
                modifier = Modifier
                    .defaultMinSize(minWidth = 100.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(
                            topStart = topStartShape,
                            topEnd = topEndShape,
                            bottomStart = 8.dp,
                            bottomEnd = 8.dp
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(text = messageData.content, color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    text = messageData.timestamp.getHourFromTimeStamp(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(180.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!messageData.sender.profileImageUrl.isNullOrEmpty()) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(180.dp)),
                        painter = rememberAsyncImagePainter(messageData.sender.profileImageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Text(
                        text = messageData.sender.name.getInitials(),
                        style = Typography.titleMedium.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(180.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!messageData.sender.profileImageUrl.isNullOrEmpty()) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(180.dp)),
                        painter = rememberAsyncImagePainter(messageData.sender.profileImageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Text(
                        text = messageData.sender.name.getInitials(),
                        style = Typography.titleMedium.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .defaultMinSize(minWidth = 100.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(
                            topStart = topStartShape,
                            topEnd = topEndShape,
                            bottomStart = 8.dp,
                            bottomEnd = 8.dp
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(text = messageData.content, color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    text = messageData.timestamp.getHourFromTimeStamp(),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            }
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
                    timestamp = "10:00 AM - 04/12/2024",
                    isSentByUser = true
                ),
                isSentByUser = true
            )
            MessageView(
                messageData = MessageData(
                    sender = UserData(name = "Beatriz Maia"),
                    content = "Olá papai! Tudo bem sim!",
                    timestamp = "10:05 AM - 04/12/2024",
                    isSentByUser = false
                ),
                isSentByUser = false
            )
        }
    }
}