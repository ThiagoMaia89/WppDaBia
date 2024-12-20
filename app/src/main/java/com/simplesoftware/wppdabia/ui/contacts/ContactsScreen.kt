package com.simplesoftware.wppdabia.ui.contacts

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.simplesoftware.wppdabia.data.ContactData
import com.simplesoftware.wppdabia.data.data_store.PreferencesManager
import com.simplesoftware.wppdabia.ui.SharedViewModel
import com.simplesoftware.wppdabia.ui.components.AppBaseContent
import com.simplesoftware.wppdabia.ui.components.ContactCardView
import com.simplesoftware.wppdabia.ui.mock.fakeRepository

@Composable
fun ContactsScreen(navController: NavController, viewModel: ContactsViewModel) {

    val contacts = viewModel.contacts.observeAsState().value
    var searchQuery by remember { mutableStateOf("") }
    val contactsLoading = viewModel.contactsLoading.collectAsState().value

    val filteredContacts = contacts?.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    if (viewModel.errorMessage.observeAsState().value != null) {
        Toast.makeText(LocalContext.current, viewModel.errorMessage.value, Toast.LENGTH_SHORT)
            .show()
        viewModel.errorMessage.value = null
    }

    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Buscar contatos...",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpar busca")
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            singleLine = true
        )

        if (contactsLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {

            Spacer(modifier = Modifier.height(24.dp))

            if (searchQuery.isEmpty()) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Todos os usuários cadastrados:",
                    style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LazyColumn {
                val listToShow: List<ContactData>? =
                    if (searchQuery.isEmpty()) contacts else filteredContacts
                itemsIndexed(listToShow ?: emptyList()) { index, contact ->
                    ContactCardView(
                        contactData = contact,
                        onCardClick = {
                            viewModel.navigateToChat(
                                contactId = contact.id,
                                contactName = contact.name,
                                navController = navController
                            )
                        }
                    )
                    if (listToShow?.isNotEmpty() == true) {
                        if (index < (listToShow.size.minus(1)))
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(1.dp)
                                    .background(color = MaterialTheme.colorScheme.primary)
                            )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsScreenPreview() {
    AppBaseContent(
        title = "Contatos",
        onBackClick = {},
        sharedViewModel = SharedViewModel(fakeRepository, PreferencesManager(LocalContext.current))
    ) {
        ContactsScreen(rememberNavController(), ContactsViewModel(fakeRepository))
    }
}