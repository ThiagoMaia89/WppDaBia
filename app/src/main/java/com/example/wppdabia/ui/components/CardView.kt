package com.example.wppdabia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.wppdabia.data.CardViewData
import com.example.wppdabia.ui.extensions.getInitials
import com.example.wppdabia.ui.theme.Typography
import com.example.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun CardView(cardViewData: CardViewData) {
    val lastMessage = remember { mutableStateOf(cardViewData.lastMessage) }
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraSmall
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .size(24.dp)
                .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.medium
            ),
            contentAlignment = Alignment.Center
        ) {
                if (cardViewData.imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(cardViewData.imageUrl),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = cardViewData.senderName.getInitials(),
                        style = Typography.titleMedium.copy(
                            fontSize = 12.sp,
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
                text = cardViewData.senderName,
                style = Typography.bodySmall.merge(
                    fontSize = 12.sp
                )
            )
            Text(
                text = lastMessage.value,
                style = Typography.bodySmall.copy(
                   fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = cardViewData.timeStamp,
                style = Typography.bodySmall.copy(
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun HomeMessageCardViewPreview() {
    WppDaBiaTheme {
        Column(modifier = Modifier.wrapContentHeight()) {
            CardView(
                CardViewData(
                    imageUrl = null,
                    senderName = "Thiago Maia",
                    lastMessage = "Oi filhota, tudo bem?",
                    timeStamp = "12:00"
                )
            )
        }
    }
}