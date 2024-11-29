package com.example.wppdabia.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.wppdabia.R
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.ui.components.AppBaseContent
import com.example.wppdabia.ui.components.LastMessageCardView
import com.example.wppdabia.ui.components.NoMessageAlert
import com.example.wppdabia.ui.extensions.getFirstName
import com.example.wppdabia.ui.mock.fakeRemote
import com.example.wppdabia.ui.navigation.Screen

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {

    val lastMessages = viewModel.lastMessageCardViews.collectAsState().value
    val currentUser = viewModel.currentUser.observeAsState().value

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)) {
        if (currentUser != null) {
            Text(
                text = "Olá, ${currentUser.name.getFirstName()}!",
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxSize()) {
            if (lastMessages.isEmpty()) {
                NoMessageAlert()
            } else {
                LazyColumn {
                    itemsIndexed(lastMessages) { _, contact ->
                        LastMessageCardView(
                            contact = contact,
                            onClick = {
                                navController.navigate("messages/${contact.chatId}/${contact.id}")
                            }
                        )
                    }
                }
            }
            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 2.dp, end = 2.dp),
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
            TextButton(
                modifier = Modifier.align(Alignment.BottomStart),
                onClick = {
                    viewModel.logout()
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                Text(
                    text = "Logout",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
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
        onBackClick = {}
    ) {
        HomeScreen(
            navController = rememberNavController(),
            viewModel = HomeViewModel(
                remote = fakeRemote,
                preferencesManager = PreferencesManager(LocalContext.current)
            )
        )
    }
}