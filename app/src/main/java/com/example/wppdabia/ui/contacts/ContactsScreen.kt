package com.example.wppdabia.ui.contacts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.wppdabia.R
import com.example.wppdabia.data.ContactData
import com.example.wppdabia.ui.components.AppBaseContent
import com.example.wppdabia.ui.components.ContactCardView
import com.example.wppdabia.ui.components.LastMessageCardView
import com.example.wppdabia.ui.components.NoMessageAlert
import com.example.wppdabia.ui.navigation.Screen
import com.example.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun ContactsScreen(navController: NavController, viewModel: ContactsViewModel) {

    val contacts = viewModel.contacts.observeAsState().value

    Box(modifier = Modifier.fillMaxSize()) {
        if (contacts.isNullOrEmpty()) {
            NoMessageAlert()
        } else {
            LazyColumn {
                itemsIndexed(contacts) { index, contact ->
                    val paddingBottom = if (index != contacts.lastIndex) 8.dp else 0.dp
                    ContactCardView(
                        modifier = Modifier.padding(bottom = paddingBottom),
                        contactData = contact,
                        onCardClick = {
                            navController.navigate(Screen.Home.route)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppBaseContent {
        Column {
            for (i in 1..10) {
                ContactCardView(
                    contactData = ContactData(
                        name = "Thiago Maia",
                        email = "james.francis.byrnes@example-pet-store.com",
                        profileImageUrl = null
                    ),
                    onCardClick = {}
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}