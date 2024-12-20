package com.simplesoftware.wppdabia.ui.components

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import com.simplesoftware.wppdabia.data.ContactData
import com.simplesoftware.wppdabia.ui.components.dialog.ImageDialog
import com.simplesoftware.wppdabia.ui.extensions.getInitials
import com.simplesoftware.wppdabia.ui.theme.Typography
import com.simplesoftware.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun ContactCardView(
    modifier: Modifier = Modifier,
    contactData: ContactData,
    onCardClick: () -> Unit
) {

    var showImageDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onCardClick.invoke()
        }) {
        Row(
            modifier = modifier
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
                if (contactData.profileImageUrl != null) {
                    SubcomposeAsyncImage(
                        model = contactData.profileImageUrl,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(180.dp))
                            .clickable {
                                showImageDialog = true
                            },
                        contentDescription = "Imagem profile",
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
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = contactData.email,
                    style = Typography.bodySmall.copy(
                        fontSize = 14.sp
                    )
                )
            }
        }

        if (showImageDialog) {
            ImageDialog(contactData.profileImageUrl) {
                showImageDialog = false
            }
        }

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