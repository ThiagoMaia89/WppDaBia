package com.simplesoftware.wppdabia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.simplesoftware.wppdabia.data.ContactData
import com.simplesoftware.wppdabia.data.MessageData
import com.simplesoftware.wppdabia.ui.components.dialog.ImageDialog
import com.simplesoftware.wppdabia.ui.extensions.getInitials
import com.simplesoftware.wppdabia.ui.extensions.handleTimeStamp
import com.simplesoftware.wppdabia.ui.extensions.ignoreLineBreak
import com.simplesoftware.wppdabia.ui.theme.Typography
import com.simplesoftware.wppdabia.ui.theme.WppDaBiaTheme
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

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick.invoke()
        }
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
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
                    SubcomposeAsyncImage(
                        model = contact.profileImageUrl,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(180.dp))
                            .clickable {
                                showImageDialog = true
                            },
                        contentDescription = "Imagem enviada",
                        contentScale = ContentScale.FillBounds,
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 8.dp,
                                color = Color.White
                            )
                        },
                        error = {
                            Text(
                                text = "Erro ao carregar imagem",
                                color = Color.Red,
                                modifier = Modifier.size(140.dp),
                                textAlign = TextAlign.Center
                            )
                        }
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
                    if (contact.lastMessage?.messageText?.isEmpty() == true && contact.lastMessage.messageImage != null) {
                        "Você: <imagem>"
                    } else {
                        "Você: ${contact.lastMessage?.lastMessage?.ignoreLineBreak()}"
                    }
                } else {
                    if (contact.lastMessage.messageText.isEmpty() && contact.lastMessage.messageImage != null) {
                        "<imagem>"
                    } else {
                        contact.lastMessage.lastMessage.ignoreLineBreak()
                    }
                }
                BoxWithConstraints {
                    val screenSize = maxWidth
                    Text(
                        modifier = Modifier.widthIn(min = 0.dp, max = screenSize.times(0.8f)),
                        text = textHandled,
                        style = Typography.bodySmall.copy(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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