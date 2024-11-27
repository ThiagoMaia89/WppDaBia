package com.example.wppdabia.ui.navigation

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.ui.components.AppBaseContent
import com.example.wppdabia.ui.contacts.ContactsScreen
import com.example.wppdabia.ui.contacts.ContactsViewModel
import com.example.wppdabia.ui.home.HomeScreen
import com.example.wppdabia.ui.home.HomeViewModel
import com.example.wppdabia.ui.login.RegisterScreen
import com.example.wppdabia.ui.login.RegisterViewModel
import com.example.wppdabia.ui.messages.MessageScreen
import com.example.wppdabia.ui.messages.MessageViewModel

@Composable
fun NavigationHandler(preferencesManager: PreferencesManager) {
    val navigationController = rememberNavController()
    val isRegistered by preferencesManager.isRegistered().collectAsState(initial = false)
    val context = LocalContext.current

    val startDestination = if (isRegistered) Screen.Home.route else Screen.Register.route

    NavHost(navController = navigationController, startDestination = startDestination) {
        composable(route = Screen.Register.route) {
            AppBaseContent(
                title = Screen.Register.title,
                onBackClick = { (context as? Activity)?.finish() }
            ) {
                val registerViewModel: RegisterViewModel = hiltViewModel()
                RegisterScreen(navigationController, registerViewModel)
            }
        }
        composable(route = Screen.Home.route) {
            AppBaseContent(
                title = Screen.Home.title,
                onBackClick = { (context as? Activity)?.finish() }
            ) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                HomeScreen(navigationController, homeViewModel)
            }
        }
        composable(route = Screen.Contacts.route) {
            AppBaseContent(
                title = Screen.Contacts.title,
                onBackClick = { navigationController.navigateUp() }
            ) {
                val contactsViewModel: ContactsViewModel = hiltViewModel()
                ContactsScreen(navigationController, contactsViewModel)
            }
        }
        composable(route = Screen.Messages.route) {
            AppBaseContent(
                title = Screen.Messages.title , // TODO: Pegar nome do contato
                onBackClick = { navigationController.navigate(Screen.Home.route) },
            ) {
                val messageViewModel: MessageViewModel = hiltViewModel()
                MessageScreen(navigationController, messageViewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String = "") {
    data object Register : Screen(route = "login_screen", title = "Cadastro")
    data object Home : Screen(route = "home_screen", title = "Wpp da Bia")
    data object Contacts : Screen(route = "contacts_screen", title = "Contatos")
    data object Messages : Screen(route = "messages_screen", title = "Mensagem")
}