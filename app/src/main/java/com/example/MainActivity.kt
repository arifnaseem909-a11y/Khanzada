package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.components.ElegantDarkBackground
import com.example.ui.screens.ChatRoomScreen
import com.example.ui.screens.ChatsScreen
import com.example.ui.screens.ContactsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.AmoledSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PremiumGold
import com.example.ui.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: ChatViewModel = viewModel()
                val navController = rememberNavController()

                // Observe active route changes
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Only show bottom navigation on landing dashboard routes
                val showBottomNav = currentRoute in listOf("chats_list", "contacts_list", "profile_tab")

                ElegantDarkBackground {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent, // Let ElegantDarkBackground draw the base color and grid dots
                        bottomBar = {
                            if (showBottomNav) {
                                NavigationBar(
                                    containerColor = AmoledSurface, // Exact #0A0A0A surface color
                                    contentColor = PremiumGold,
                                    modifier = Modifier
                                        .windowInsetsPadding(WindowInsets.navigationBars)
                                        .testTag("app_bottom_nav_bar")
                                ) {
                                NavigationBarItem(
                                    selected = currentRoute == "chats_list",
                                    onClick = {
                                        navController.navigate("chats_list") {
                                            popUpTo("chats_list") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Chat, contentDescription = "Chats") },
                                    label = { Text("Chats") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = PremiumGold,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray,
                                        indicatorColor = PremiumGold
                                    ),
                                    modifier = Modifier.testTag("nav_chats_tab")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "contacts_list",
                                    onClick = {
                                        navController.navigate("contacts_list") {
                                            popUpTo("chats_list") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.ContactPage, contentDescription = "Contacts") },
                                    label = { Text("Contacts") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = PremiumGold,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray,
                                        indicatorColor = PremiumGold
                                    ),
                                    modifier = Modifier.testTag("nav_contacts_tab")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "profile_tab",
                                    onClick = {
                                        navController.navigate("profile_tab") {
                                            popUpTo("chats_list") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                    label = { Text("Profile") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = PremiumGold,
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray,
                                        indicatorColor = PremiumGold
                                    ),
                                    modifier = Modifier.testTag("nav_profile_tab")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "chats_list",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("chats_list") {
                            ChatsScreen(
                                viewModel = viewModel,
                                onChatClick = { chatId ->
                                    viewModel.setActiveChat(chatId)
                                    navController.navigate("chat_room/$chatId")
                                },
                                onSyncClick = {
                                    navController.navigate("contacts_list")
                                }
                            )
                        }

                        composable("contacts_list") {
                            ContactsScreen(
                                viewModel = viewModel,
                                onContactClick = { contactId ->
                                    viewModel.setActiveChat(contactId)
                                    navController.navigate("chat_room/$contactId")
                                }
                            )
                        }

                        composable("profile_tab") {
                            ProfileScreen(viewModel = viewModel)
                        }

                        composable(
                            route = "chat_room/{chatId}",
                            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
                        ) {
                            ChatRoomScreen(
                                viewModel = viewModel,
                                onBackClick = {
                                    viewModel.setActiveChat(null)
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
}
