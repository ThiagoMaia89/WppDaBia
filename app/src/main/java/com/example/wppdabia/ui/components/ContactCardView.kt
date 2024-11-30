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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.wppdabia.data.ContactData
import com.example.wppdabia.ui.extensions.getInitials
import com.example.wppdabia.ui.theme.Typography
import com.example.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun ContactCardView(
    modifier: Modifier = Modifier,
    contactData: ContactData,
    onCardClick: () -> Unit
) {
    Column {
        Row(
            modifier = modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    onCardClick.invoke()
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
                if (contactData.profileImageUrl != null) {
                    Image(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(180.dp)),
                        painter = rememberAsyncImagePainter(contactData.profileImageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Text(
                        text = contactData.name.getInitials(),
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
                    text = contactData.name,
                    style = Typography.bodySmall.merge(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = contactData.email,
                    style = Typography.bodySmall.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                )
            }
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(color = MaterialTheme.colorScheme.primary))
    }
}

@Composable
@Preview(showBackground = true)
private fun ContactCardViewPreview() {
    WppDaBiaTheme {
        Column(modifier = Modifier.wrapContentHeight()) {
            ContactCardView(
                contactData = ContactData(
                    id = "1",
                    name = "Thiago Maia",
                    email = "james.a.garfield@examplepetstore.com",
                    profileImageUrl = null
                ),
                onCardClick = {}
            )
        }
    }
}