package com.premise.firebasedevfest2022.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.premise.firebasedevfest2022.domain.RealtimeChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RealtimeChatScreen(viewModel: RealtimeChatViewModel) {
    Column {
        Text(text = "Realtime Chat")
        val state = viewModel.dataState.collectAsState()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp)
        ) {
            items(state.value) {
                Text(text = it.message, fontSize = 16.sp)
                Text(text = it.sender, fontSize = 12.sp)
            }
        }
        Row {
            var text by remember { mutableStateOf("") }

            TextField(
                value = text,
                onValueChange = {
                    text = it
                },
                label = { Text("Enter your message here") }
            )
            Button(onClick = {
                viewModel.addMessage(text)
                text = ""
            }) {
                Image(painter = painterResource(id = com.premise.firebasedevfest2022.R.drawable.send), contentDescription = null)
            }
        }
    }
}
object RealtimeChatScreen {
    const val SCREEN_ROUTE = "/realtimeChat/"
}