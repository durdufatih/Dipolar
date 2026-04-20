package com.dipolar.navigation

import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.dipolar.data.model.JoinRequestStatus
import com.dipolar.ui.auth.*
import com.dipolar.ui.events.*
import com.dipolar.ui.notifications.NotificationsScreen
import com.dipolar.ui.profile.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Main : Screen("main")
    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
    object CreateEvent : Screen("create_event")
}

sealed class BottomTab(val route: String, val label: String) {
    object Events : BottomTab("tab_events", "Etkinlikler")
    object Notifications : BottomTab("tab_notifications", "Bildirimler")
    object Profile : BottomTab("tab_profile", "Profil")
}

@Composable
fun DipolarNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        if (currentUser == null && navController.currentDestination?.route !in listOf(
                Screen.Login.route, Screen.Signup.route
            )
        ) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                authState = authState,
                onLogin = authViewModel::login,
                onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                onResetState = authViewModel::resetState
            )
            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                authState = authState,
                onSignup = authViewModel::signup,
                onNavigateToLogin = { navController.popBackStack() },
                onResetState = authViewModel::resetState
            )
            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToEventDetail = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                },
                onNavigateToCreateEvent = {
                    navController.navigate(Screen.CreateEvent.route)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Screen.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            val eventViewModel: EventViewModel = hiltViewModel()
            val user by eventViewModel.currentUser.collectAsState()
            val latestEvents by eventViewModel.filteredEvents.collectAsState()
            val latestEvent = remember(latestEvents) { eventViewModel.getEventById(eventId) } ?: return@composable
            val joinStatus = remember(latestEvents) { eventViewModel.getJoinStatus(eventId) }

            EventDetailScreen(
                event = latestEvent,
                currentUser = user,
                joinStatus = joinStatus,
                onJoinRequest = { eventViewModel.sendJoinRequest(eventId) },
                onRespondToRequest = { requestId, accept ->
                    eventViewModel.respondToJoinRequest(eventId, requestId, accept)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateEvent.route) {
            val eventViewModel: EventViewModel = hiltViewModel()
            CreateEventScreen(
                onCreateEvent = { title, desc, date, loc, max, interests, lang ->
                    eventViewModel.createEvent(title, desc, date, loc, max, interests, lang)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainScreen(
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToCreateEvent: () -> Unit,
    onLogout: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val eventViewModel: EventViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()

    val currentUser by eventViewModel.currentUser.collectAsState()
    val filteredEvents by eventViewModel.filteredEvents.collectAsState()
    val allEvents by profileViewModel.currentUser.collectAsState()
    val selectedInterests by eventViewModel.selectedInterestFilter.collectAsState()
    val selectedLanguage by eventViewModel.selectedLanguageFilter.collectAsState()
    val pendingCount by eventViewModel.pendingRequestEvents.collectAsState()

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == BottomTab.Events.route,
                    onClick = {
                        bottomNavController.navigate(BottomTab.Events.route) {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Explore, null) },
                    label = { Text(BottomTab.Events.label) }
                )
                NavigationBarItem(
                    selected = currentRoute == BottomTab.Notifications.route,
                    onClick = {
                        bottomNavController.navigate(BottomTab.Notifications.route) {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        BadgedBox(badge = {
                            val count = pendingCount.sumOf { e ->
                                e.joinRequests.count { it.status == JoinRequestStatus.PENDING }
                            }
                            if (count > 0) Badge { Text(count.toString()) }
                        }) {
                            Icon(Icons.Default.Notifications, null)
                        }
                    },
                    label = { Text(BottomTab.Notifications.label) }
                )
                NavigationBarItem(
                    selected = currentRoute == BottomTab.Profile.route,
                    onClick = {
                        bottomNavController.navigate(BottomTab.Profile.route) {
                            popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text(BottomTab.Profile.label) }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomTab.Events.route
        ) {
            composable(BottomTab.Events.route) {
                EventListScreen(
                    events = filteredEvents,
                    currentUser = currentUser,
                    selectedInterests = selectedInterests,
                    selectedLanguage = selectedLanguage,
                    onToggleInterest = eventViewModel::toggleInterestFilter,
                    onLanguageFilter = eventViewModel::setLanguageFilter,
                    onClearFilters = eventViewModel::clearFilters,
                    onEventClick = { event -> onNavigateToEventDetail(event.id) },
                    onCreateEvent = onNavigateToCreateEvent
                )
            }

            composable(BottomTab.Notifications.route) {
                val myEvents by eventViewModel.myEvents.collectAsState()
                NotificationsScreen(
                    events = myEvents,
                    currentUserId = currentUser?.id,
                    onRespondToRequest = { eventId, requestId, accept ->
                        eventViewModel.respondToJoinRequest(eventId, requestId, accept)
                    }
                )
            }

            composable(BottomTab.Profile.route) {
                val user by profileViewModel.currentUser.collectAsState()
                user?.let { u ->
                    ProfileScreen(
                        user = u,
                        onSave = { bio, interests, languages ->
                            profileViewModel.updateProfile(bio, interests, languages)
                        },
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}
