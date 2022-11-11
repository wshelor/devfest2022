package com.premise.firebasedevfest2022.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun RealtimeChatScreen() {
    Text(text = "Realtime Chat")
}
object RealtimeChatScreen {
    const val SCREEN_ROUTE = "/realtimeChat/"
}