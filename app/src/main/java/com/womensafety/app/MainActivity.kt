package com.womensafety.app

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import com.womensafety.app.data.models.EmergencyContact
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.womensafety.app.ui.screens.*
import com.womensafety.app.ui.theme.*
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.womensafety.app.ui.components.*
import com.womensafety.app.logging.ActivityRecorder
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
        private val simpleViewModel: SimpleMainViewModel by viewModels {
                SimpleMainViewModelFactory(application)
        }
        // Shared GroupListViewModel to allow updates from any screen (e.g. after creation)
        private val groupListViewModel: com.womensafety.app.ui.screens.GroupListViewModel by viewModels()

        private lateinit var permissionManager: PermissionManager

        override fun onCreate(savedInstanceState: Bundle?) {
                // Enable edge-to-edge display for Android 15 (SDK 35) compatibility
                enableEdgeToEdge()
                super.onCreate(savedInstanceState)
                
                // Initialize Activity Recorder
                ActivityRecorder.init(this)
                
                permissionManager = PermissionManager(this)

                // Initial check on app launch
                checkAndProceed()
        }

        override fun onResume() {
                super.onResume()

                // Re-check on resume (user may have enabled location in settings)
                checkAndProceed()
        }

        /**
         * Correct flow:
         * 1. Check if permission granted -> if NO, check if we should request
         * 2. If permission YES -> check if location services ON
         * 3. If location services OFF -> redirect to Location Settings
         * 4. If everything OK -> show main app
         */
        private fun checkAndProceed() {
                val hasPermission = LocationPermissionEnforcer.isLocationPermissionGranted(this)

                if (!hasPermission) {
                        // Permission NOT granted
                        // Check if we should show permission request (first time only)
                        val prefs = getSharedPreferences("app_state", MODE_PRIVATE)
                        val hasAskedBefore = prefs.getBoolean("location_permission_asked", false)

                        if (!hasAskedBefore) {
                                // First time - request permission
                                prefs.edit().putBoolean("location_permission_asked", true).apply()
                                requestLocationPermission()
                        } else {
                                // Permission was denied before - show blocking screen
                                showPermissionDeniedScreen()
                        }
                        return
                }

                // Permission IS granted - now check location services
                val locationServicesOn = LocationPermissionEnforcer.areLocationServicesEnabled(this)

                if (!locationServicesOn) {
                        // Permission OK, but GPS/Location is OFF
                        showLocationServicesOffScreen()
                        return
                }

                // Everything is OK - show main app
                showMainApp()
        }

        private fun requestLocationPermission() {
                permissionManager.requestAllPermissions { granted ->
                        if (granted) {
                                // Permission granted - re-check everything
                                checkAndProceed()
                        } else {
                                // Permission denied - show blocking screen
                                showPermissionDeniedScreen()
                        }
                }
        }

        private fun showPermissionDeniedScreen() {
                setContent {
                        AuraTheme {
                                com.womensafety.app.ui.screens.LocationPermissionBlockedScreen(
                                        onSettingsClick = {
                                                LocationPermissionEnforcer
                                                        .openAppPermissionSettings(this)
                                        },
                                        onExitApp = {
                                                finish() // Just close, don't force kill
                                        }
                                )
                        }
                }
        }

        private fun showLocationServicesOffScreen() {
                setContent {
                        AuraTheme {
                                com.womensafety.app.ui.screens.LocationServicesOffScreen(
                                        onOpenLocationSettings = {
                                                LocationPermissionEnforcer.openLocationSettings(
                                                        this
                                                )
                                        }
                                )
                        }
                }
        }

        private fun showMainApp() {
                setContent {
                        AuraTheme {
                                val contacts by simpleViewModel.contacts.collectAsState()
                                val otpState by simpleViewModel.otpState.collectAsState()
                                val settingsState by simpleViewModel.settingsState.collectAsState()
                                val sosState by simpleViewModel.sosState.collectAsState()

                                AuraNavigationWrapper(
                                        viewModel = simpleViewModel,
                                        groupListViewModel = groupListViewModel, // Pass shared instance
                                        permissionManager = permissionManager,
                                        contacts = contacts,
                                        otpState = otpState,
                                        settingsState = settingsState,
                                        sosState = sosState
                                )
                        }
                }
        }
}

@Composable
fun AuraNavigationWrapper(
        viewModel: SimpleMainViewModel,
        groupListViewModel: com.womensafety.app.ui.screens.GroupListViewModel, // Accept shared instance
        permissionManager: PermissionManager,
        contacts: List<EmergencyContact>,
        otpState: SimpleOTPState,
        settingsState: SimpleSettingsState,
        sosState: SOSState
) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current

        // Check registration status - REACTIVE (not cached)
        val userPreferences = remember { com.womensafety.app.data.UserPreferences.getInstance(context) }
        var isRegistrationComplete by remember { mutableStateOf(userPreferences.isRegistrationComplete()) }
        
        // Update registration status when it changes
        LaunchedEffect(Unit) {
            // This ensures the state updates if changed elsewhere
            isRegistrationComplete = userPreferences.isRegistrationComplete()
        }

        // Detect MIUI device and check setup status
        val isMiuiDevice = remember { com.womensafety.app.utils.MiuiDeviceDetector.isMiuiDevice() }
        val needsMiuiSetup = isMiuiDevice && !settingsState.miuiSetupCompleted

        // Navigation state - Start at WelcomeIntro as a Splash Screen
        var currentScreen by remember { mutableStateOf(AuraScreen.WelcomeIntro) }
        
        // Observe screen changes and update registration status
        LaunchedEffect(currentScreen) {
            isRegistrationComplete = userPreferences.isRegistrationComplete()
        }
        var selectedContact by remember { mutableStateOf<EmergencyContact?>(null) }
        var showBatteryDialog by remember { mutableStateOf(false) }
        var registrationMobileNumber by remember { mutableStateOf("") }
        
        // Group creation state
        var createdGroupName by remember { mutableStateOf("") }
        var createdGroupMembers by remember { mutableStateOf<List<EmergencyContact>>(emptyList()) }
        
        // Dialog states
        var showAddContactDialog by remember { mutableStateOf(false) }
        var showEditNameDialog by remember { mutableStateOf(false) }
        var showEditPhoneDialog by remember { mutableStateOf(false) }
        var showEditEmailDialog by remember { mutableStateOf(false) }
        var showEditIotDialog by remember { mutableStateOf(false) }


        // Add Contact Dialog
        if (showAddContactDialog) {
            val context = androidx.compose.ui.platform.LocalContext.current
            AuraAddContactDialog(
                onDismiss = { showAddContactDialog = false },
                onConfirm = { name, phone, relationship, email, location ->
                    viewModel.createContactDirectly(
                        name = name, 
                        phone = phone, 
                        relationship = relationship, 
                        email = email, 
                        location = location,
                        onSuccess = {
                            showAddContactDialog = false
                            android.widget.Toast.makeText(context, "Contact Added Successfully", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        onError = { error ->
                            android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_LONG).show()
                        }
                    )
                },
                isDuplicate = { phone ->
                    val cleanInput = phone.filter { it.isDigit() }.takeLast(10)
                    contacts.any { it.phoneNumber.filter { char -> char.isDigit() }.takeLast(10) == cleanInput }
                }
            )
        }

        // Edit Name Dialog
        if (showEditNameDialog) {
            AuraEditProfileDialog(
                title = "Your Name",
                initialValue = settingsState.userName,
                onDismiss = { showEditNameDialog = false },
                onConfirm = { 
                    viewModel.updateUserName(it)
                    showEditNameDialog = false
                },
                label = "Enter name",
                isPhoneField = false
            )
        }

        // Edit Profile Dialogs
        if (showEditPhoneDialog) {
            AuraEditProfileDialog(
                title = "Your Phone Number",
                initialValue = settingsState.userPhoneNumber,
                onDismiss = { showEditPhoneDialog = false },
                onConfirm = { 
                    viewModel.updateUserPhoneNumber(it)
                    showEditPhoneDialog = false
                },
                label = "Enter number",
                isPhoneField = true
            )
        }
        if (showEditEmailDialog) {
            AuraEditProfileDialog(
                title = "Your Email",
                initialValue = settingsState.userEmail,
                onDismiss = { showEditEmailDialog = false },
                onConfirm = { 
                    viewModel.updateUserEmail(it)
                    showEditEmailDialog = false
                },
                label = "Enter email",
                isEmailField = true
            )
        }
        if (showEditIotDialog) {
            AuraEditProfileDialog(
                title = "IoT SIM Number",
                initialValue = settingsState.iotSimNumber,
                onDismiss = { showEditIotDialog = false },
                onConfirm = { 
                    viewModel.updateIotSimNumber(it)
                    showEditIotDialog = false
                },
                label = "Enter number",
                isPhoneField = true
            )
        }



        // Battery optimization check
        LaunchedEffect(Unit) {
                if (!BatteryOptimizationHelper.isOptimizationDisabled(context)) {
                        kotlinx.coroutines.delay(1000)
                        showBatteryDialog = true
                }
        }

        // Handle back presses
        // Only enable BackHandler for screens that have a local back destination
        val screensWithBack = listOf(
            AuraScreen.EmergencyContacts, AuraScreen.CreateGroup, AuraScreen.GroupList,
            AuraScreen.GroupDetails, AuraScreen.NeedHelp, AuraScreen.GuardianDetail,
            AuraScreen.Profile, AuraScreen.LoginOTP, AuraScreen.ForgotPassword,
            AuraScreen.Registration, AuraScreen.BuildCircle
        )
        
        BackHandler(enabled = currentScreen in screensWithBack) {
                when (currentScreen) {
                        AuraScreen.EmergencyContacts -> currentScreen = AuraScreen.Home
                        AuraScreen.CreateGroup -> currentScreen = AuraScreen.EmergencyContacts 
                        AuraScreen.GroupList -> currentScreen = AuraScreen.EmergencyContacts
                        AuraScreen.GroupDetails -> {
                                currentScreen = AuraScreen.EmergencyContacts 
                        }
                        AuraScreen.NeedHelp -> currentScreen = AuraScreen.Home
                        AuraScreen.GuardianDetail -> currentScreen = AuraScreen.EmergencyContacts
                        AuraScreen.Profile -> currentScreen = if (settingsState.userRole == "receiver") 
                            AuraScreen.ReceiverHome else AuraScreen.Home
                        AuraScreen.LoginOTP -> currentScreen = AuraScreen.Login
                        AuraScreen.ForgotPassword -> currentScreen = AuraScreen.Login
                        AuraScreen.Registration -> currentScreen = AuraScreen.PermissionStory
                        AuraScreen.BuildCircle -> currentScreen = AuraScreen.Registration
                        else -> { /* System handles others */ }
                }
        }

        // Global Delete Confirmation
        var showGlobalDeleteDialog by remember { mutableStateOf(false) }
        var contactToGlobalDelete by remember { mutableStateOf<EmergencyContact?>(null) }
        
        if (showGlobalDeleteDialog && contactToGlobalDelete != null) {
            AlertDialog(
                onDismissRequest = { showGlobalDeleteDialog = false },
                title = { Text("Delete Guardian Contact") },
                text = { Text("WARNING: This will permanently delete '${contactToGlobalDelete?.name}' from your account and remove them from ALL your groups. Continue?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            contactToGlobalDelete?.let { contact ->
                                viewModel.globalDeleteContact(contact)
                            }
                            showGlobalDeleteDialog = false
                            contactToGlobalDelete = null
                            currentScreen = AuraScreen.EmergencyContacts
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
                    ) {
                        Text("YES, DELETE")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showGlobalDeleteDialog = false 
                        contactToGlobalDelete = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .windowInsetsPadding(WindowInsets.systemBars) // Edge-to-edge: pad content away from status bar & nav bar
        ) {
                if (showBatteryDialog) {
                        BatteryOptimizationDialog(
                                onDismiss = { showBatteryDialog = false },
                                onConfirm = { showBatteryDialog = false }
                        )
                }

                when (currentScreen) {
                        AuraScreen.MiuiSetup -> MiuiSetupScreen(
                                onSetupComplete = {
                                        viewModel.markMiuiSetupCompleted()
                                        currentScreen = if (!viewModel.isOnboardingComplete()) {
                                                AuraScreen.WelcomeIntro
                                        } else if (settingsState.userRole == "receiver") {
                                                AuraScreen.ReceiverHome
                                        } else {
                                                AuraScreen.Home
                                        }
                                }
                        )

                        AuraScreen.WelcomeIntro -> WelcomeIntroScreen(
                                onContinue = { 
                                    currentScreen = when {
                                        needsMiuiSetup -> AuraScreen.MiuiSetup
                                        !isRegistrationComplete -> AuraScreen.PermissionStory
                                        !viewModel.isOnboardingComplete() -> AuraScreen.PermissionStory
                                        settingsState.userRole == "receiver" -> AuraScreen.ReceiverHome
                                        else -> AuraScreen.Home
                                    }
                                }
                        )

                        AuraScreen.PermissionStory -> PermissionStoryScreen(
                                onContinue = { 
                                    permissionManager.requestAllPermissions {
                                        // Skip Registration if already complete
                                        currentScreen = if (isRegistrationComplete) {
                                            AuraScreen.BuildCircle
                                        } else {
                                            AuraScreen.Registration
                                        }
                                    }
                                },
                                onBack = { (context as? android.app.Activity)?.finish() }
                        )

                        AuraScreen.Registration -> RegistrationScreen(
                                onNavigateToLogin = {
                                    // Navigate to Login screen
                                    currentScreen = AuraScreen.Login
                                },
                                onBack = { currentScreen = AuraScreen.PermissionStory }
                        )

                        AuraScreen.Login -> LoginScreen(
                                onNavigateToOTP = { mobileNumber ->
                                        // Store mobile number for OTP verification
                                        registrationMobileNumber = mobileNumber
                                        // Navigate to OTP verification screen
                                        currentScreen = AuraScreen.LoginOTP
                                },
                                onNavigateToSignup = {
                                        // Navigate to Registration screen
                                        currentScreen = AuraScreen.Registration
                                },
                                onForgotPassword = {
                                        android.util.Log.d("AuraNavigation", "Navigating to Forgot Password")
                                        currentScreen = AuraScreen.ForgotPassword
                                }
                        )

                        AuraScreen.ForgotPassword -> ForgotPasswordScreen(
                                onBack = { currentScreen = AuraScreen.Login }
                        )

                        AuraScreen.LoginOTP -> NewOTPVerificationScreen(
                                mobileNumber = registrationMobileNumber,
                                isLoginFlow = true,
                                onVerificationSuccess = {
                                        // OTP verified successfully
                                        // Mark user as logged in
                                        val userPrefs = com.womensafety.app.data.UserPreferences.getInstance(context)
                                        userPrefs.setRegistrationComplete(true)
                                        
                                        // CRITICAL: Refresh settings to load username into state
                                        viewModel.refreshSettings()
                                        android.util.Log.d("USERNAME_FLOW", "🔄 REFRESH: Triggered refreshSettings() after login")
                                        
                                        // Navigate to Home
                                        currentScreen = AuraScreen.Home
                                },
                                onBack = { currentScreen = AuraScreen.Login }
                        )

                        AuraScreen.RegistrationOTP -> NewOTPVerificationScreen(
                                mobileNumber = registrationMobileNumber,
                                onVerificationSuccess = {
                                        // OTP verified successfully
                                        // CRITICAL: Refresh settings to load username into state
                                        viewModel.refreshSettings()
                                        android.util.Log.d("USERNAME_FLOW", "🔄 REFRESH: Triggered refreshSettings() after registration")
                                        
                                        // Proceed to BuildCircle
                                        currentScreen = AuraScreen.BuildCircle
                                },
                                onBack = { currentScreen = AuraScreen.Registration }
                        )

                        AuraScreen.BuildCircle -> BuildCircleScreen(
                                onAddManually = {
                                        showAddContactDialog = true
                                },
                                onSkip = { currentScreen = AuraScreen.Home },
                                onBack = { currentScreen = AuraScreen.RegistrationOTP }
                        )

                        AuraScreen.Home -> SanctuaryScreen(
                                userName = settingsState.userName.ifBlank { "there" },
                                userPhoneNumber = settingsState.userPhoneNumber,
                                iotSimNumber = settingsState.iotSimNumber,
                                contactCount = contacts.size,
                                isLocationEnabled = true, // Simplified for now
                                profileImageUri = settingsState.profileImageUri,
                                onNeedHelp = { currentScreen = AuraScreen.NeedHelp },
                                onShareLocation = {
                                        coroutineScope.launch {
                                                viewModel.shareLocation()
                                                Toast.makeText(context, "Location shared", Toast.LENGTH_SHORT).show()
                                        }
                                },
                                onNavigateToCircle = { currentScreen = AuraScreen.EmergencyContacts },
                                onNavigateToProfile = { currentScreen = AuraScreen.Profile }
                        )

                        AuraScreen.ReceiverHome -> AuraListeningScreen(
                                userPhoneNumber = settingsState.userPhoneNumber
                        )

                        AuraScreen.EmergencyContacts -> YourCircleScreen(
                                contacts = contacts,
                                onAddPerson = {
                                        showAddContactDialog = true
                                },
                                onContactClick = { contact ->
                                        selectedContact = contact
                                        currentScreen = AuraScreen.GuardianDetail
                                },
                                onCreateGroup = {
                                        currentScreen = AuraScreen.CreateGroup
                                },
                                onSeeAllGroups = {
                                        currentScreen = AuraScreen.GroupList // NEW: Navigate to Group List
                                },
                                onDeleteContact = { contact ->
                                        contactToGlobalDelete = contact
                                        showGlobalDeleteDialog = true
                                },
                                onBack = { 
                                        currentScreen = if (settingsState.userRole == "receiver") 
                                                AuraScreen.ReceiverHome else AuraScreen.Home
                                }
                        )

                        AuraScreen.CreateGroup -> CreateGroupScreen(
                                onBack = { currentScreen = AuraScreen.EmergencyContacts },
                                onGroupCreated = { groupName, members ->
                                        // Store details and navigate to details screen
                                        createdGroupName = groupName
                                        createdGroupMembers = members
                                        
                                        // CRITICAL: Immediately refresh group list in shared ViewModel
                                        // This ensures that when the user goes to "See All Groups", the new group is there.
                                        groupListViewModel.fetchGroups()
                                        
                                        currentScreen = AuraScreen.GroupDetails
                                        Toast.makeText(context, "Group created successfully!", Toast.LENGTH_SHORT).show()
                                }
                        )
                        
                        AuraScreen.GroupList -> com.womensafety.app.ui.screens.GroupListScreen(
                                viewModel = groupListViewModel,
                                onBack = { currentScreen = AuraScreen.EmergencyContacts },
                                onGroupClick = { group ->
                                    // Resolve members from local contacts list using backendId (c_id)
                                    val memberIds = group.memberIds.mapNotNull { it.toIntOrNull() }.toSet()
                                    val members = contacts.filter { contact -> 
                                        memberIds.contains(contact.backendId) 
                                    }
                                    
                                    // Set data for detail screen
                                    createdGroupName = group.name
                                    createdGroupMembers = members
                                    
                                    // Navigate
                                    currentScreen = AuraScreen.GroupDetails
                                }
                        )
                        
                        AuraScreen.GroupDetails -> {
                                com.womensafety.app.ui.screens.GroupDetailsScreen(
                                        groupName = createdGroupName,
                                        members = createdGroupMembers,
                                        onBack = { 
                                            // Crude back navigation: if we just viewed a list, go back to list
                                            // But for now, since we overwrite 'createdGroupMembers' which is shared state,
                                            // we can't easily distinguish source without another flag.
                                            // Let's assume if it was "Create -> Detail" it goes to ContactList.
                                            // If "List -> Detail", we might want List.
                                            // For simplicity, let's go back to EmergencyContacts (Hub) as implemented before.
                                            // OR: We can change currentScreen to GroupList if we want.
                                            // Let's stick to EmergencyContacts to be safe/consistent with Create flow
                                            currentScreen = AuraScreen.EmergencyContacts 
                                        }
                                )
                        }

                        AuraScreen.GuardianDetail -> selectedContact?.let { contact ->
                                GuardianDetailScreen(
                                        contact = contact,
                                        onCall = {
                                                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                                        data = android.net.Uri.parse("tel:${contact.phoneNumber}")
                                                }
                                                context.startActivity(intent)
                                        },
                                        onMessage = {
                                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                        data = android.net.Uri.parse("smsto:${contact.phoneNumber}")
                                                }
                                                context.startActivity(intent)
                                        },
                                        onRemove = {
                                                contactToGlobalDelete = contact
                                                showGlobalDeleteDialog = true
                                        },
                                        onBack = { currentScreen = AuraScreen.EmergencyContacts }
                                )
                        }

                        AuraScreen.NeedHelp -> NeedHelpScreen(
                                contactCount = contacts.size,
                                contactNames = contacts.map { it.name },
                                isSOSActive = sosState.isActive,
                                isInCooldown = sosState.isInCooldown,
                                cooldownSeconds = if (sosState.isInCooldown) {
                                    val remaining = ((sosState.cooldownUntil - System.currentTimeMillis()) / 1000).toInt().coerceAtLeast(0)
                                    remaining
                                } else 0,
                                onHelpSent = {
                                        if (!sosState.isActive && !sosState.isInCooldown) {
                                                viewModel.triggerSOS()
                                                currentScreen = AuraScreen.HelpSent
                                        }
                                },
                                onCancel = { currentScreen = AuraScreen.Home }
                        )

                         AuraScreen.HelpSent -> HelpSentScreen(
                                 contactNames = contacts.map { it.name },
                                 onReturnToHome = { currentScreen = AuraScreen.Home }
                         )

                        AuraScreen.Profile -> {
                                // Refresh settings when Profile screen is opened
                                LaunchedEffect(Unit) {
                                        viewModel.refreshSettings()
                                }
                                SanctuaryProfileScreen(
                                        userName = settingsState.userName,
                                        userPhoneNumber = settingsState.userPhoneNumber,
                                        userEmail = settingsState.userEmail,
                                        iotSimNumber = settingsState.iotSimNumber,
                                        isSirenEnabled = settingsState.enableSound,
                                        profileImageUri = settingsState.profileImageUri,
                                        onUpdateProfileImage = { viewModel.updateProfileImage(it) },
                                        onEditName = { showEditNameDialog = true },
                                        onEditPhone = { showEditPhoneDialog = true },
                                        onEditEmail = { showEditEmailDialog = true },
                                        onEditIot = { showEditIotDialog = true },
                                        onSirenToggle = { viewModel.updateSoundSetting(it) },
                                        onLogout = {
                                            // Clear all preferences and data
                                            val sharedPrefs = context.getSharedPreferences("app_state", android.content.Context.MODE_PRIVATE)
                                            sharedPrefs.edit().clear().apply()
                                            
                                            com.womensafety.app.data.UserPreferences.getInstance(context).clearAllData()
                                            com.womensafety.app.data.SecurePreferences.clearAll(context)
                                            viewModel.clearAllData()
                                            
                                            // Reset local registration state
                                            isRegistrationComplete = false
                                            
                                            // Navigate to Welcome screen
                                            currentScreen = AuraScreen.WelcomeIntro
                                            android.widget.Toast.makeText(context, "Logged out successfully", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        onBack = { 
                                                currentScreen = if (settingsState.userRole == "receiver") 
                                                         AuraScreen.ReceiverHome else AuraScreen.Home 
                                        }
                                )
                        }

                        else -> SanctuaryScreen(
                                userName = settingsState.userName.ifBlank { "there" },
                                userPhoneNumber = settingsState.userPhoneNumber,
                                iotSimNumber = settingsState.iotSimNumber,
                                contactCount = contacts.size,
                                profileImageUri = settingsState.profileImageUri,
                                onNeedHelp = { currentScreen = AuraScreen.NeedHelp },
                                onShareLocation = {},
                                onNavigateToCircle = { currentScreen = AuraScreen.EmergencyContacts },
                                onNavigateToProfile = { currentScreen = AuraScreen.Profile }
                        )
                }

                // Floating Navigation Orb (Show only on main screens)
                if (currentScreen in listOf(AuraScreen.Home, AuraScreen.EmergencyContacts, AuraScreen.Profile)) {
                        Box(
                                modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = AuraSpacing.generous)
                        ) {
                                AuraOrbNavigation(
                                        currentScreen = currentScreen,
                                        onScreenSelected = { 
                                                // Handle screen selection based on role
                                                if (it == AuraScreen.Home && settingsState.userRole == "receiver") {
                                                        currentScreen = AuraScreen.ReceiverHome
                                                } else {
                                                        currentScreen = it
                                                }
                                        }
                                )
                        }
                }

                // OTP Screen (Z-indexed to top)
                if (otpState.isActive) {
                        SimpleOTPScreen(
                                phoneNumber = otpState.phoneNumber,
                                isLoading = otpState.isLoading,
                                errorMessage = otpState.errorMessage,
                                resendCooldownSeconds = otpState.resendCooldownSeconds,
                                onOTPVerified = { viewModel.verifyOTP(it) },
                                onResendOTP = { viewModel.resendOTP() },
                                onBack = { viewModel.cancelOTPVerification() }
                        )
                }
        }
}

/**
 * AURA Screen Enum for Navigation
 */
enum class AuraScreen {
        MiuiSetup,
        WelcomeIntro,
        PermissionStory,
        Login,
        ForgotPassword,
        LoginOTP,
        Registration,
        RegistrationOTP,
        BuildCircle,
        Home,
        ReceiverHome,
        EmergencyContacts,
        CreateGroup,
        GroupList,
        GroupDetails,
        GuardianDetail,
        NeedHelp,
        HelpSent,
        Profile
}

/**
 * AURA ORB NAVIGATION
 * 
 * Minimalist floating navigation
 */
@Composable
fun AuraOrbNavigation(
        currentScreen: AuraScreen,
        onScreenSelected: (AuraScreen) -> Unit
) {
        Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = CircleShape,
                shadowElevation = AuraElevation.floating,
                modifier = Modifier.height(AuraSizes.floatingOrbSize)
        ) {
                Row(
                        modifier = Modifier.padding(horizontal = AuraSpacing.default),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AuraSpacing.comfortable)
                ) {
                        OrbItem(
                                icon = Icons.Default.Home,
                                selected = currentScreen == AuraScreen.Home || currentScreen == AuraScreen.ReceiverHome,
                                onClick = { 
                                        // Receiver role logic is handled in onScreenSelected
                                        onScreenSelected(AuraScreen.Home) 
                                }
                        )
                        OrbItem(
                                icon = Icons.Default.People,
                                selected = currentScreen == AuraScreen.EmergencyContacts,
                                onClick = { onScreenSelected(AuraScreen.EmergencyContacts) }
                        )
                        OrbItem(
                                icon = Icons.Default.Person,
                                selected = currentScreen == AuraScreen.Profile,
                                onClick = { onScreenSelected(AuraScreen.Profile) }
                        )
                }
        }
}

@Composable
private fun OrbItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        selected: Boolean,
        onClick: () -> Unit
) {
        IconButton(onClick = onClick) {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(if (selected) 28.dp else 24.dp)
                )
        }
}

/**
 * PROFILE SCREEN (REPLACING SETTINGS)
 * 
 * Rebranded as Sanctuary Profile - Pink Theme update
 */
@Composable
fun SanctuaryProfileScreen(
        userName: String,
        userPhoneNumber: String,
        userEmail: String,
        iotSimNumber: String,
        isSirenEnabled: Boolean,
        profileImageUri: String,
        onUpdateProfileImage: (String) -> Unit,
        onEditName: () -> Unit,
        onEditPhone: () -> Unit,
        onEditEmail: () -> Unit,
        onEditIot: () -> Unit,
        onSirenToggle: (Boolean) -> Unit,
        onLogout: () -> Unit,
        onBack: () -> Unit
) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: android.net.Uri? ->
            uri?.let { onUpdateProfileImage(it.toString()) }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFCE4EC), // Light pink
                            Color(0xFFFFF9E6)  // Light cream
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF2D2D2D)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Your profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Profile Picture Circle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, Color(0xFFE91E63), CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                     if (profileImageUri.isNotEmpty()) {
                         AsyncImage(
                             model = profileImageUri,
                             contentDescription = "Profile Picture",
                             modifier = Modifier.fillMaxSize(),
                             contentScale = ContentScale.Crop
                         )
                     } else {
                         Icon(
                             imageVector = Icons.Default.CameraAlt,
                             contentDescription = "Add Photo",
                             tint = Color(0xFFE91E63),
                             modifier = Modifier.size(40.dp)
                         )
                     }
                }
                
                Text(
                    text = "Tap to change photo",
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Profile options
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Add Your Name - Static/Read-only
                    // UI BIND: Display exact username from UserPreferences
                    val displayName = userName.ifBlank { "Add Your Name" }
                    android.util.Log.d("USERNAME_FLOW", "🖼️ UI BIND: Displaying username in Profile: '$displayName'")
                    ProfileInfoItem(
                        icon = Icons.Default.Person,
                        title = displayName,
                        enabled = true,
                        onClick = onEditName
                    )

                    // Add Your Number - Static/Read-only
                    ProfileInfoItem(
                        icon = Icons.Default.Phone,
                        title = userPhoneNumber.ifBlank { "Add Your Number" },
                        enabled = true,
                        onClick = onEditPhone
                    )

                    // Add Your Email - Static/Read-only
                    ProfileInfoItem(
                        icon = Icons.Default.Email,
                        title = userEmail.ifBlank { "Add Your Email" },
                        enabled = true,
                        onClick = onEditEmail
                    )

                    // IoT SIM number - Remains editable
                    ProfileInfoItem(
                        icon = Icons.Default.Devices,
                        title = if (iotSimNumber.isBlank()) "Setup IoT Device" else "IoT SIM: $iotSimNumber",
                        enabled = true,
                        onClick = onEditIot
                    )

                    // Siren Alert Toggle
                    ProfileToggleItem(
                        icon = Icons.Default.VolumeUp,
                        title = "Siren Alert",
                        isChecked = isSirenEnabled,
                        onCheckedChange = onSirenToggle
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Logout Option
                    ProfileInfoItem(
                        icon = Icons.Default.Logout,
                        title = "Logout",
                        enabled = true,
                        onClick = onLogout
                    )
                }
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        onClick = { if (enabled) onClick() },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            disabledContainerColor = Color.White
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFCE4EC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D2D2D),
                modifier = Modifier.weight(1f)
            )
            
            if (enabled) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF999999)
                )
            }
        }
    }
}

@Composable
private fun ProfileToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFCE4EC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D2D2D),
                modifier = Modifier.weight(1f)
            )
            
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFE91E63),
                    checkedTrackColor = Color(0xFFFCE4EC),
                    uncheckedThumbColor = Color(0xFFBDBDBD),
                    uncheckedTrackColor = Color(0xFFE0E0E0)
                )
            )
        }
    }
}
@Composable
fun AuraListeningScreen(userPhoneNumber: String) {
        val context = LocalContext.current
        var showBatteryDialog by remember { mutableStateOf(false) }

        // Check battery optimization on first composition
        LaunchedEffect(Unit) {
                if (!BatteryOptimizationHelper.isOptimizationDisabled(context)) {
                        kotlinx.coroutines.delay(1000)
                        showBatteryDialog = true
                }
        }

        if (showBatteryDialog) {
                BatteryOptimizationDialog(
                        onDismiss = { showBatteryDialog = false },
                        onConfirm = { showBatteryDialog = false }
                )
        }

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = AuraSpacing.screenHorizontal),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                // Large Aura Ring in "Listening" mode
                AuraRing(
                        protectionLevel = ProtectionLevel.Listening,
                        modifier = Modifier.size(AuraSizes.auraRingLarge)
                )

                Spacer(modifier = Modifier.height(AuraSpacing.sectionGap))

                Text(
                        text = "Guardian Mode Active",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(AuraSpacing.space2))

                Text(
                        text = "Your device is ready to receive alerts.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AuraSpacing.sectionGap))

                // Info Card
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = MaterialTheme.shapes.extraLarge,
                        elevation = CardDefaults.cardElevation(defaultElevation = AuraElevation.soft)
                ) {
                        Column(
                                modifier = Modifier.padding(AuraSpacing.comfortable),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                Icon(
                                        imageVector = Icons.Default.PhoneInTalk,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(AuraSizes.iconLarge)
                                )
                                Spacer(modifier = Modifier.height(AuraSpacing.space4))
                                Text(
                                        text = userPhoneNumber.ifBlank { "Not Registered" },
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                )
                                Text(
                                        text = "Registered Receiver Number",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }
                }
                
                Spacer(modifier = Modifier.height(AuraSpacing.sectionGap * 2))
        }
}
