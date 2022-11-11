@file:OptIn(ExperimentalAnimationApi::class)

package com.premise.firebasedevfest2022

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
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
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {  }

    private fun launchSigninFlow() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build())

        signInLauncher.launch(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        Column(modifier = Modifier.padding(4.dp)) {
            Text(text = "Welcome to Firebase DevFest 2022!")

            Button(modifier = Modifier.padding(4.dp), onClick = {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    launchSigninFlow()
                } else {
                    navController.navigate(RealtimeChatScreen.SCREEN_ROUTE)
                }
            }) {
                Text(text = "Realtime Chat")
            }

            Button(modifier = Modifier.padding(4.dp), onClick = {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    launchSigninFlow()
                } else {
                    navController.navigate(RgbSelectionScreen.SCREEN_ROUTE)
                }
            }) {
                Text(text = "RGB Battle")
            }

            Button(modifier = Modifier.padding(4.dp), onClick = {
                FirebaseCrashlytics.getInstance().recordException(RuntimeException("This one isn't that bad"))
            }) {
                Text(text = "Report a non-fatal problem")
            }

            Button(modifier = Modifier.padding(4.dp), onClick = {
                throw java.lang.RuntimeException("Something bad happened!!")
            }) {
                Text(text = "Crash the App")
            }
        }
    }
}