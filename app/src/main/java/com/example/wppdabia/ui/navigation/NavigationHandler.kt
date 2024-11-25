package com.example.wppdabia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wppdabia.ui.contacts.ContactsScreen
import com.example.wppdabia.ui.contacts.ContactsViewModel
import com.example.wppdabia.ui.home.HomeScreen
import com.example.wppdabia.ui.home.HomeViewModel
import com.example.wppdabia.ui.login.LoginScreen
import com.example.wppdabia.ui.login.LoginViewModel

@Composable
fun NavigationHandler() {
    val navigationController = rememberNavController()
    NavHost(navController = navigationController, startDestination = Screen.Login.route) {
        composable(route = Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(navigationController, loginViewModel)
        }
        composable(route = Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(navigationController, homeViewModel)
        }
        composable(route = Screen.Contacts.route) {
            val contactsViewModel: ContactsViewModel = hiltViewModel()
            ContactsScreen(navigationController, contactsViewModel)
        }
    }
}

sealed class Screen(val route: String) {
    data object Login : Screen("login_screen")
    data object Home : Screen("home_screen")
    data object Contacts : Screen("contacts_screen")
}