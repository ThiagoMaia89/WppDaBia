package com.example.wppdabia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.wppdabia.data.ContactData
import com.example.wppdabia.data.MessageData
import com.example.wppdabia.ui.components.dialog.ImageDialog
import com.example.wppdabia.ui.extensions.getInitials
import com.example.wppdabia.ui.extensions.handleTimeStamp
import com.example.wppdabia.ui.extensions.ignoreLineBreak
import com.example.wppdabia.ui.theme.Typography
import com.example.wppdabia.ui.theme.WppDaBiaTheme
import kotlinx.coroutines.delay

@Composable
fun LastMessageCardView(contact: ContactData, onClick: () -> Unit) {

    var showImageDialog by remember { mutableStateOf(false) }

    var formattedTimestamp by remember { mutableStateOf(handleTimeStamp(contact.timestamp)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60000)
            formattedTimestamp = handleTimeStamp(contact.timestamp)
        }
    }

    Column {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    onClick.invoke()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(180.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!contact.profileImageUrl.isNullOrEmpty()) {
                    Image(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(180.dp))
                            .clickable {
                                showImageDialog = true
                            },
                        painter = rememberAsyncImagePainter(contact.profileImageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Text(
                        text = contact.name.getInitials(),
                        style = Typography.titleMedium.copy(
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.wrapContentHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.wrapContentHeight(),
                    text = contact.name,
                    style = Typography.bodySmall.merge(
                        fontSize = 16.sp
                    )
                )
                val textHandled = if (contact.lastMessage?.sender?.uid != contact.id) {
                    "Você: ${contact.lastMessage?.lastMessage?.ignoreLineBreak()}"
                } else {
                    contact.lastMessage.lastMessage.ignoreLineBreak()
                }
                Text(
                    text = textHandled ?: "",
                    style = Typography.bodySmall.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTimestamp,
                        style = Typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = if (contact.wasRead) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                            fontWeight = if (contact.wasRead) FontWeight.Normal else FontWeight.Bold
                        )
                    )
                    if (!contact.wasRead) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)))

        if (showImageDialog) {
            ImageDialog(contact.profileImageUrl) {
                showImageDialog = false
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun HomeMessageCardViewPreview() {
    WppDaBiaTheme {
        Column(modifier = Modifier.wrapContentHeight()) {
            LastMessageCardView(
                ContactData(
                    name = "Thiago Maia",
                    profileImageUrl = null,
                    timestamp = "12:00 - 04/12/2024",
                    lastMessage = MessageData(lastMessage = "Oi filhota!")
                )
            ) {}
        }
    }
}