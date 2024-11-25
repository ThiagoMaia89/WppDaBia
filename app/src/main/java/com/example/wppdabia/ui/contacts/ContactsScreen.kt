package com.example.wppdabia.ui.contacts

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.wppdabia.data.ContactData
import com.example.wppdabia.ui.components.AppBaseContent
import com.example.wppdabia.ui.components.ContactCardView
import com.example.wppdabia.ui.components.NoMessageAlert
import com.example.wppdabia.ui.navigation.Screen

@Composable
fun ContactsScreen(navController: NavController, viewModel: ContactsViewModel) {

    val contacts = viewModel.contacts.observeAsState().value
    var searchQuery by remember { mutableStateOf("") }

    val filteredContacts = contacts?.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    if (viewModel.errorMessage.observeAsState().value != null) {
        Toast.makeText(LocalContext.current, viewModel.errorMessage.value, Toast.LENGTH_SHORT).show()
        viewModel.errorMessage.value = null
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(text = "Buscar contatos...", color = MaterialTheme.colorScheme.primary) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.primary)
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
                .padding(8.dp),
            singleLine = true
        )

        if (contacts.isNullOrEmpty()) {
            NoMessageAlert()
        } else {
            LazyColumn {
                val listToShow: List<ContactData>? = if (searchQuery.isEmpty()) contacts else filteredContacts
                itemsIndexed(listToShow ?: emptyList()) { index, contact ->
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
    AppBaseContent(
        title = "Contatos",
        onBackClick = {}
    ) {
        ContactsScreen(rememberNavController(), ContactsViewModel())
    }
}