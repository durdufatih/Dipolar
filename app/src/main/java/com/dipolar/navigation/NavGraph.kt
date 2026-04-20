package com.dipolar.navigation

import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.dipolar.data.model.JoinRequestStatus
import com.dipolar.ui.auth.*
import com.dipolar.ui.events.*
import com.dipolar.ui.notifications.NotificationsScreen
import com.dipolar.ui.profile.*
import com.dipolar.ui.theme.NavyBlue

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Main : Screen("main")
    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
    object CreateEvent : Screen("create_event")
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector, val iconSelected: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem("tab_events", "DISCOVER", Icons.Outlined.Explore, Icons.Filled.Explore),
    BottomNavItem("tab_saved", "SAVED", Icons.Outlined.Bookmark, Icons.Filled.Bookmark),
    BottomNavItem("tab_create", "CREATE", Icons.Outlined.AddCircle, Icons.Filled.AddCircle),
    BottomNavItem("tab_profile", "PROFILE", Icons.Outlined.Person, Icons.Filled.Person)
)

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
                onNavigateToEventDetail = { navController.navigate(Screen.EventDetail.createRoute(it)) },
                onNavigateToCreateEvent = { navController.navigate(Screen.CreateEvent.route) },
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
        ) { backStack ->
            val eventId = backStack.arguments?.getString("eventId") ?: return@composable
            val vm: EventViewModel = hiltViewModel()
            val user by vm.currentUser.collectAsState()
            val allEvents by vm.filteredEvents.collectAsState()
            val event = remember(allEvents) { vm.getEventById(eventId) } ?: return@composable
            val joinStatus = remember(allEvents) { vm.getJoinStatus(eventId) }

            EventDetailScreen(
                event = event,
                currentUser = user,
                joinStatus = joinStatus,
                onJoinRequest = { vm.sendJoinRequest(eventId) },
                onRespondToRequest = { requestId, accept -> vm.respondToJoinRequest(eventId, requestId, accept) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateEvent.route) {
            val vm: EventViewModel = hiltViewModel()
            CreateEventScreen(
                onCreateEvent = { title, desc, date, loc, max, interests, lang ->
                    vm.createEvent(title, desc, date, loc, max, interests, lang)
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
    val bottomNav = rememberNavController()
    val eventVm: EventViewModel = hiltViewModel()
    val profileVm: ProfileViewModel = hiltViewModel()

    val currentUser by eventVm.currentUser.collectAsState()
    val filteredEvents by eventVm.filteredEvents.collectAsState()
    val myEvents by eventVm.myEvents.collectAsState()
    val selectedInterests by eventVm.selectedInterestFilter.collectAsState()
    val selectedLanguage by eventVm.selectedLanguageFilter.collectAsState()
    val pendingEvents by eventVm.pendingRequestEvents.collectAsState()

    val entry by bottomNav.currentBackStackEntryAsState()
    val currentRoute = entry?.destination?.route

    val totalPending = pendingEvents.sumOf { e -> e.joinRequests.count { it.status == JoinRequestStatus.PENDING } }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                bottomNavItems.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            bottomNav.navigate(item.route) {
                                popUpTo(bottomNav.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            BadgedBox(badge = {
                                if (item.route == "tab_saved" && totalPending > 0)
                                    Badge { Text(totalPending.toString()) }
                            }) {
                                Icon(if (selected) item.iconSelected else item.icon, null)
                            }
                        },
                        label = {
                            Text(item.label, style = MaterialTheme.typography.labelSmall)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyBlue,
                            selectedTextColor = NavyBlue,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(navController = bottomNav, startDestination = "tab_events") {
            composable("tab_events") {
                EventListScreen(
                    events = filteredEvents,
                    currentUser = currentUser,
                    selectedInterests = selectedInterests,
                    selectedLanguage = selectedLanguage,
                    onToggleInterest = eventVm::toggleInterestFilter,
                    onLanguageFilter = eventVm::setLanguageFilter,
                    onClearFilters = eventVm::clearFilters,
                    onEventClick = { onNavigateToEventDetail(it.id) },
                    onCreateEvent = onNavigateToCreateEvent
                )
            }
            composable("tab_saved") {
                NotificationsScreen(
                    events = myEvents,
                    currentUserId = currentUser?.id,
                    onRespondToRequest = { eventId, requestId, accept ->
                        eventVm.respondToJoinRequest(eventId, requestId, accept)
                    }
                )
            }
            composable("tab_create") {
                CreateEventScreen(
                    onCreateEvent = { title, desc, date, loc, max, interests, lang ->
                        eventVm.createEvent(title, desc, date, loc, max, interests, lang)
                        bottomNav.navigate("tab_events") {
                            popUpTo(bottomNav.graph.startDestinationId) { saveState = true }
                        }
                    },
                    onBack = {
                        bottomNav.navigate("tab_events") {
                            popUpTo(bottomNav.graph.startDestinationId)
                        }
                    }
                )
            }
            composable("tab_profile") {
                val user by profileVm.currentUser.collectAsState()
                user?.let { u ->
                    ProfileScreen(
                        user = u,
                        onSave = { bio, interests, languages -> profileVm.updateProfile(bio, interests, languages) },
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}
