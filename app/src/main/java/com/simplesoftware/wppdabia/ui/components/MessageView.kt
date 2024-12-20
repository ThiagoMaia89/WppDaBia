package com.simplesoftware.wppdabia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.simplesoftware.wppdabia.data.MessageData
import com.simplesoftware.wppdabia.data.UserData
import com.simplesoftware.wppdabia.ui.extensions.getDateFromTimeStamp
import com.simplesoftware.wppdabia.ui.extensions.getHourFromTimeStamp
import com.simplesoftware.wppdabia.ui.extensions.getInitials
import com.simplesoftware.wppdabia.ui.theme.Typography
import com.simplesoftware.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun MessageView(
    messageData: MessageData,
    isSentByUser: Boolean,
    isUploading: Boolean,
    onImageClick: () -> Unit = {}
) {
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
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
    ) {
        val topStartShape = if (isSentByUser) 8.dp else 0.dp
        val topEndShape = if (isSentByUser) 0.dp else 8.dp

        if (isSentByUser) {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .widthIn(min = 80.dp, max = screenWidth.times(0.5f))
                    .shadow(1.dp, shape = RoundedCornerShape(8.dp))
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(
                            topStart = topStartShape,
                            topEnd = topEndShape,
                            bottomStart = 8.dp,
                            bottomEnd = 8.dp
                        )
                    )
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (messageData.messageImage != null) {
                    SubcomposeAsyncImage(
                        model = messageData.messageImage,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onImageClick.invoke()
                            },
                        contentDescription = "Imagem enviada",
                        contentScale = ContentScale.Crop,
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(60.dp),
                                strokeWidth = 2.dp,
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
                }
                if (messageData.messageText.isNotEmpty()) {
                    Text(
                        modifier = Modifier.widthIn(min = 80.dp, max = screenWidth.times(0.5f)),
                        text = messageData.messageText,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Start
                    )
                }
                Spacer(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .width(34.dp)
                        .height(2.dp)
                        .background(color = Color.White)
                )
                Text(
                    text = messageData.timestamp.getHourFromTimeStamp(),
                    textAlign = TextAlign.Center,
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
                    SubcomposeAsyncImage(
                        model = messageData.sender.profileImageUrl,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(180.dp)),
                        contentDescription = "Imagem profile",
                        contentScale = ContentScale.FillBounds,
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 3.dp,
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
                    SubcomposeAsyncImage(
                        model = messageData.sender.profileImageUrl,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(180.dp)),
                        contentDescription = "Imagem profile",
                        contentScale = ContentScale.FillBounds,
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 3.dp,
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
                        text = messageData.sender.name.getInitials(),
                        style = Typography.titleMedium.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            BoxWithConstraints {
                val screenWidth = this.maxWidth
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .widthIn(min = 80.dp, max = screenWidth.times(0.5f))
                        .background(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(
                                topStart = topStartShape,
                                topEnd = topEndShape,
                                bottomStart = 8.dp,
                                bottomEnd = 8.dp
                            )
                        )
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (messageData.messageImage != null) {
                        SubcomposeAsyncImage(
                            model = messageData.messageImage,
                            modifier = Modifier
                                .size(140.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onImageClick.invoke()
                                },
                            contentDescription = "Imagem enviada",
                            contentScale = ContentScale.Crop,
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(60.dp),
                                    strokeWidth = 2.dp,
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
                    }
                    if (messageData.messageText.isNotEmpty()) {
                        Text(
                            modifier = Modifier.widthIn(min = 80.dp, max = screenWidth.times(0.5f)),
                            text = messageData.messageText,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Start
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .width(34.dp)
                            .height(2.dp)
                            .background(color = Color.White)
                    )
                    Text(
                        text = messageData.timestamp.getHourFromTimeStamp(),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                    )
                }
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
                    messageText = "adoliamndasçndja piondjaidlç jaslçkdmna lçdmnsadlçknjalkdjadlçkmnaslkdnja lkçdja lçkdja lçkdja dlçkja çdlkja sldçknjasdlçkhajd laçkjdalçkdja dçlkajsd açkldja skd",
                    timestamp = "10:00",
                    isSentByUser = true
                ),
                isSentByUser = true,
                true
            )
            MessageView(
                messageData = MessageData(
                    sender = UserData(name = "Beatriz Maia"),
                    messageText = "Olá papai! Tudo bem sim!",
                    timestamp = "10:05",
                    isSentByUser = false
                ),
                isSentByUser = false,
                true
            )
        }
    }
}