@file:OptIn(ExperimentalAnimationApi::class)

package com.premise.firebasedevfest2022

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.premise.firebasedevfest2022.domain.RealtimeChatViewModel
import com.premise.firebasedevfest2022.domain.RgbSelectionScreenViewModel
import com.premise.firebasedevfest2022.ui.screens.RealtimeChatScreen
import com.premise.firebasedevfest2022.ui.screens.RgbSelectionScreen
import com.premise.firebasedevfest2022.ui.theme.AppTheme
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    private lateinit var rootView: ViewGroup
    private lateinit var navController: NavHostController
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        firebaseAnalytics.logEvent("auth_completed") {
            param("result", it.resultCode.toAuthResult())
        }
        FirebaseCrashlytics.getInstance().log("User Authenticated: ${FirebaseAuth.getInstance().currentUser?.displayName}")
        navController.navigate("/", NavOptions.Builder().setPopUpTo("/", true).build())
    }

    private fun launchSigninFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build()
        )

        signInLauncher.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics

        initializeRemoteConfig()
        initializeFcm()
        rootView = window.decorView.findViewById(android.R.id.content)

        setContent {
            AppTheme {
                navController = rememberAnimatedNavController()

                AnimatedNavHost(navController = navController, startDestination = "/") {
                    composable("/") {
                        DefaultScreen(navController)
                    }

                    composable(RealtimeChatScreen.SCREEN_ROUTE) {
                        val viewModel = RealtimeChatViewModel()
                        RealtimeChatScreen(viewModel)
                    }

                    composable(RgbSelectionScreen.SCREEN_ROUTE) {
                        val viewModel = RgbSelectionScreenViewModel()
                        RgbSelectionScreen(viewModel)
                    }
                }
            }
        }
    }

    private fun initializeFcm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(POST_NOTIFICATIONS)
        }
        lifecycleScope.launchWhenStarted {
            ActiveFirebaseMessagingService.dataState.collect {
                it?.let {
                    Toast.makeText(baseContext, "${it.notification?.title} - ${it.notification?.body}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
        })

    }

    private fun initializeRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
    }

    @Composable
    fun DefaultScreen(navController: NavController) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(modifier = Modifier.padding(14.dp),
                painter = painterResource(id = R.drawable.firebase_logo),
                contentDescription = "Firebase Logo"
            )
            Text(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                text = "Firebase 101 \n @ DevFest Seattle 2022",
                fontSize = 25.sp, textAlign = TextAlign.Center
            )

            MainScreenButton("Part 1 - Build\nRealtime Chat") {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    launchSigninFlow()
                    firebaseAnalytics.logEvent("auth_requested") {
                        param("source", RealtimeChatScreen.SCREEN_ROUTE)
                    }
                } else {
                    firebaseAnalytics.logEvent("realtime_chat", null)
                    navController.navigate(RealtimeChatScreen.SCREEN_ROUTE)
                }
            }

            MainScreenButton("Part 2 - Engage\nRGB Battle") {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    launchSigninFlow()
                    firebaseAnalytics.logEvent("auth_requested") {
                        param("source", RgbSelectionScreen.SCREEN_ROUTE)
                    }
                } else {
                    firebaseAnalytics.logEvent("rgb_battle", null)
                    navController.navigate(RgbSelectionScreen.SCREEN_ROUTE)
                }
            }

            MainScreenButton("Part 3 - Release & Monitor\nReport a non-fatal problem") {
                FirebaseCrashlytics.getInstance().recordException(RuntimeException("This one isn't that bad"))
                Toast.makeText(baseContext, "Problem reported to Firebase", Toast.LENGTH_SHORT).show()
            }

            MainScreenButton("Part 3 - Release & Monitor\nCrash the App") {
                throw java.lang.RuntimeException("Something bad happened!!")
            }

            if (FirebaseAuth.getInstance().currentUser != null) {
                MainScreenButton("Log Out") {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("/", NavOptions.Builder().setPopUpTo("/", true).build())
                }
            } else {
                MainScreenButton("Log In") {
                    launchSigninFlow()
                    firebaseAnalytics.logEvent("auth_requested") {
                        param("source", "Login Button")
                    }
                }

            }
        }
    }

    @Composable
    fun MainScreenButton(text: String, onClick: () -> Unit) {
        Button(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
            onClick = onClick) {
            Text(modifier = Modifier.padding(4.dp), text = text, textAlign = TextAlign.Center)
        }
    }
}

private fun Int.toAuthResult() = if (this == RESULT_OK) "success" else "failure"
