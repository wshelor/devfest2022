@file:OptIn(ExperimentalAnimationApi::class)

package com.premise.firebasedevfest2022

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.premise.firebasedevfest2022.domain.RealtimeChatViewModel
import com.premise.firebasedevfest2022.domain.RgbSelectionScreenViewModel
import com.premise.firebasedevfest2022.ui.screens.RealtimeChatScreen
import com.premise.firebasedevfest2022.ui.screens.RgbSelectionScreen
import com.premise.firebasedevfest2022.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        firebaseAnalytics.logEvent("auth_completed") {
            param("result", it.resultCode.toAuthResult())
        }
        FirebaseCrashlytics.getInstance().log("User Authenticated: ${FirebaseAuth.getInstance().currentUser?.displayName}")
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

        setContent {
            AppTheme {
                val navController = rememberAnimatedNavController()

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

            MainScreenButton("Realtime Chat") {
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

            MainScreenButton("RGB Battle") {
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

            MainScreenButton("Report a non-fatal problem") {
                FirebaseCrashlytics.getInstance().recordException(RuntimeException("This one isn't that bad"))
            }

            MainScreenButton("Crash the App") {
                throw java.lang.RuntimeException("Something bad happened!!")
            }

            if (FirebaseAuth.getInstance().currentUser != null) {
                MainScreenButton("Log Out") {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("/", NavOptions.Builder().setPopUpTo("/", true).build())
                }

            }
        }
    }

    @Composable
    fun MainScreenButton(text: String,
                         onClick: () -> Unit) {
        Button(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
            onClick = onClick) {
            Text(modifier = Modifier.padding(4.dp), text = text)
        }
    }
}

private fun Int.toAuthResult() = if (this == RESULT_OK) "success" else "failure"
