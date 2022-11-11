package com.premise.firebasedevfest2022.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun PersonalNotesScreen() {
    Text(text = "Personal Notes")

}

object PersonalNotesScreen {
    const val SCREEN_ROUTE = "/personalNotes/"
}