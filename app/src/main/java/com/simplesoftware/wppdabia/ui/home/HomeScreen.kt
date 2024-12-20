package com.simplesoftware.wppdabia.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.simplesoftware.wppdabia.R
import com.simplesoftware.wppdabia.data.data_store.PreferencesManager
import com.simplesoftware.wppdabia.ui.SharedViewModel
import com.simplesoftware.wppdabia.ui.components.AppBaseContent
import com.simplesoftware.wppdabia.ui.components.LastMessageCardView
import com.simplesoftware.wppdabia.ui.components.NoMessageAlert
import com.simplesoftware.wppdabia.ui.extensions.getFirstName
import com.simplesoftware.wppdabia.ui.mock.fakeRepository
import com.simplesoftware.wppdabia.ui.navigation.Screen

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {

    val lastMessages = viewModel.lastMessageCardViews.collectAsState().value
    val currentUser = viewModel.currentUser.collectAsState().value

    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        if (currentUser != null) {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                text = "Olá, ${currentUser.name.getFirstName()}!",
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxSize()) {
            if (lastMessages.isEmpty()) {
                NoMessageAlert()
            } else {
                LazyColumn {
                    itemsIndexed(lastMessages) { index, contact ->
                        val wasRead = if (contact.lastMessage?.sender?.uid == currentUser?.uid) true else contact.wasRead
                        LastMessageCardView(
                            contact = contact.copy(wasRead = wasRead),
                            onClick = {
                                navController.navigate("messages/${contact.chatId}/${contact.id}/${contact.name}")
                            }
                        )
                    }
                }
            }
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 16.dp, end = 16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(180.dp),
                onClick = {
                    navController.navigate(Screen.Contacts.route)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_user),
                    contentDescription = "Selecionar foto",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppBaseContent(
        title = "Home",
        onBackClick = {},
        sharedViewModel = SharedViewModel(fakeRepository, PreferencesManager(LocalContext.current))
    ) {
        HomeScreen(
            navController = rememberNavController(),
            viewModel = HomeViewModel(
                repository = fakeRepository
            )
        )
    }
}