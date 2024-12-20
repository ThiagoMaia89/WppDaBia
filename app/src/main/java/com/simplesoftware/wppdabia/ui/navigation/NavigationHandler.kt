package com.simplesoftware.wppdabia.ui.navigation

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.simplesoftware.wppdabia.ui.SharedViewModel
import com.simplesoftware.wppdabia.ui.components.AppBaseContent
import com.simplesoftware.wppdabia.ui.contacts.ContactsScreen
import com.simplesoftware.wppdabia.ui.contacts.ContactsViewModel
import com.simplesoftware.wppdabia.ui.home.HomeScreen
import com.simplesoftware.wppdabia.ui.home.HomeViewModel
import com.simplesoftware.wppdabia.ui.login.RegisterScreen
import com.simplesoftware.wppdabia.ui.login.RegisterViewModel
import com.simplesoftware.wppdabia.ui.messages.MessageScreen
import com.simplesoftware.wppdabia.ui.messages.MessageViewModel
import com.simplesoftware.wppdabia.ui.splash.SplashScreen

@Composable
fun NavigationHandler(context: Context, onLogout: () -> Unit) {
    val navigationController = rememberNavController()
    val sharedViewModel: SharedViewModel = hiltViewModel()
    var loginMade by remember { mutableStateOf(false) }

    val logout = sharedViewModel.logout.observeAsState().value

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

    NavHost(navController = navigationController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            AppBaseContent(title = "", onBackClick = {}, sharedViewModel = sharedViewModel) {
                SplashScreen(
                    context = context,
                    navigateToHome = { navigationController.navigate(Screen.Home.route) },
                    navigateToRegister = { navigationController.navigate(Screen.Register.route) }
                )
            }
        }

        composable(route = Screen.Register.route) {
            AppBaseContent(
                title = Screen.Register.title,
                onBackClick = { (context as? Activity)?.finish() },
                hasBackButton = false,
                showProfileImage = false,
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
    data object Splash : Screen(route = "splash_screen")
    data object Register : Screen(route = "login_screen", title = "Cadastro")
    data object Home : Screen(route = "home_screen", title = "Wpp da Bia")
    data object Contacts : Screen(route = "contacts_screen", title = "Contatos")
    data object Messages : Screen(route = "messages/{chatId}/{contactId}/{contactName}", title = "Mensagem")
}