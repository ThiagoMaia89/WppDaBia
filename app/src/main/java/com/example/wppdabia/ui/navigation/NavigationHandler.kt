package com.example.wppdabia.ui.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.ui.SharedViewModel
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
fun NavigationHandler(preferencesManager: PreferencesManager, onLogout: () -> Unit) {
    val navigationController = rememberNavController()
    val isRegistered by preferencesManager.isRegistered().collectAsState(initial = false)
    val context = LocalContext.current
    val sharedViewModel: SharedViewModel = hiltViewModel()
    var loginMade by remember { mutableStateOf(false) }

    var logout = sharedViewModel.logout.observeAsState().value

    LaunchedEffect(Unit) {
        sharedViewModel.getCurrentUser()
    }

    LaunchedEffect(loginMade) { if (loginMade) sharedViewModel.getCurrentUser() }

    LaunchedEffect(logout) {
        if (logout == true) {
            loginMade = false
            sharedViewModel.resetLogoutFlag()
            onLogout.invoke()
        }
    }

    val startDestination = if (isRegistered) Screen.Home.route else Screen.Register.route

    NavHost(navController = navigationController, startDestination = startDestination) {
        composable(route = Screen.Register.route) {
            AppBaseContent(
                title = Screen.Register.title,
                onBackClick = { (context as? Activity)?.finish() },
                hasBackButton = false,
                sharedViewModel = sharedViewModel
            ) {
                val registerViewModel: RegisterViewModel = hiltViewModel()
                RegisterScreen(
                    navigationController,
                    registerViewModel,
                    onLogin = { loginMade = true }
                )
            }
        }
        composable(route = Screen.Home.route) {
            AppBaseContent(
                title = Screen.Home.title,
                onBackClick = { (context as? Activity)?.finish() },
                hasBackButton = false,
                user = sharedViewModel.currentUser.collectAsState().value,
                sharedViewModel = sharedViewModel
            ) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                HomeScreen(navigationController, homeViewModel)
            }
        }
        composable(route = Screen.Contacts.route) {
            AppBaseContent(
                title = Screen.Contacts.title,
                onBackClick = { navigationController.navigateUp() },
                user = sharedViewModel.currentUser.collectAsState().value,
                sharedViewModel = sharedViewModel
            ) {
                val contactsViewModel: ContactsViewModel = hiltViewModel()
                ContactsScreen(navigationController, contactsViewModel)
            }
        }
        composable(
            route = Screen.Messages.route,
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("contactId") { type = NavType.StringType },
                navArgument("contactName") { type = NavType.StringType }
            )
        ) {

            val chatId = it.arguments?.getString("chatId") ?: ""
            val contactId = it.arguments?.getString("contactId") ?: ""
            val contactName = it.arguments?.getString("contactName")

            AppBaseContent(
                title = contactName ?: Screen.Messages.title ,
                onBackClick = { navigationController.navigate(Screen.Home.route) },
                user = sharedViewModel.currentUser.collectAsState().value,
                showProfileImage = false,
                sharedViewModel = sharedViewModel
            ) {

                val messageViewModel: MessageViewModel = hiltViewModel()

                MessageScreen(
                    navController = navigationController,
                    viewModel = messageViewModel,
                    chatId = chatId,
                    contactId = contactId
                )
            }
        }
    }
}

sealed class Screen(val route: String, val title: String = "") {
    data object Register : Screen(route = "login_screen", title = "Cadastro")
    data object Home : Screen(route = "home_screen", title = "Wpp da Bia")
    data object Contacts : Screen(route = "contacts_screen", title = "Contatos")
    data object Messages : Screen(route = "messages/{chatId}/{contactId}/{contactName}", title = "Mensagem")
}