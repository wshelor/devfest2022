@file:OptIn(ExperimentalAnimationApi::class)

package com.premise.firebasedevfest2022

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.premise.firebasedevfest2022.ui.screens.PersonalNotesScreen
import com.premise.firebasedevfest2022.ui.screens.RealtimeChatScreen
import com.premise.firebasedevfest2022.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val navController = rememberAnimatedNavController()

                AnimatedNavHost(navController = navController, startDestination = "/") {
                    composable("/") {
                        DefaultScreen(navController)
                    }

                    composable(RealtimeChatScreen.SCREEN_ROUTE) {
                        RealtimeChatScreen()
                    }

                    composable(PersonalNotesScreen.SCREEN_ROUTE) {
                        PersonalNotesScreen()
                    }
                }

                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                }
            }
        }
    }

    @Composable
    fun DefaultScreen(navController: NavController) {
        Column {
            Text(text = "Welcome to Firebase DevFest 2022! ")
            Button(onClick = {
                navController.navigate(RealtimeChatScreen.SCREEN_ROUTE)
            }) {
                Text(text = "Realtime Chat")
            }
            Button(onClick = {
                navController.navigate(PersonalNotesScreen.SCREEN_ROUTE)
            }) {
                Text(text = "Personal Notes")
            }
            Button(onClick = {
            }) {
                Text(text = "Crash the App")
            }
            Button(onClick = {
                throw java.lang.RuntimeException("Oh no!")
            }) {
                Text(text = "Crash the App")
            }
        }

    }
}