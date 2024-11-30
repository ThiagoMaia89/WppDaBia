package com.example.wppdabia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.wppdabia.data.UserData
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.ui.SharedViewModel
import com.example.wppdabia.ui.extensions.getInitials
import com.example.wppdabia.ui.mock.fakeRemote
import com.example.wppdabia.ui.theme.Typography
import com.example.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun AppBaseContent(
    title: String,
    onBackClick: () -> Unit,
    user: UserData? = null,
    sharedViewModel: SharedViewModel,
    content: @Composable () -> Unit
) {
    WppDaBiaTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
        ) {
            WppDaBiaTopBar(
                title = title,
                onBackClick = onBackClick,
                user = user,
                sharedViewModel = sharedViewModel
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WppDaBiaTopBar(
    title: String,
    onBackClick: () -> Unit,
    user: UserData?,
    sharedViewModel: SharedViewModel
) {
    TopAppBar(
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { onBackClick.invoke() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .size(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(180.dp)
                    )
                    .clickable {
                        sharedViewModel.logout() //TODO: DROPDOWNMENU
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!user?.profileImageUrl.isNullOrEmpty()) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(180.dp)),
                        painter = rememberAsyncImagePainter(user?.profileImageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Text(
                        text = user?.name?.getInitials() ?: "",
                        style = Typography.titleMedium.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
@Preview
fun WppDaBiaTopBarPreview() {
    WppDaBiaTopBar(
        title = "Wpp da Bia",
        onBackClick = {},
        user = UserData(name = "Thiago Maia"),
        sharedViewModel = SharedViewModel(
            fakeRemote,
            PreferencesManager(LocalContext.current)
        )
    )

}