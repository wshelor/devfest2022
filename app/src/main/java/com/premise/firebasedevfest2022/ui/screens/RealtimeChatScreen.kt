package com.premise.firebasedevfest2022.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
                .padding(vertical = 4.dp, horizontal = 4.dp),
            contentPadding = PaddingValues(6.dp)
        ) {
            items(state.value) {
                Card(Modifier.padding(4.dp).fillMaxWidth()) {
                    Text(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), text = it.message, fontSize = 16.sp)
                    Text(modifier = Modifier.padding(horizontal = 8.dp), text = it.sender, fontSize = 12.sp)
                }
            }
        }
        Row(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            var text by remember { mutableStateOf("") }

            TextField(
                modifier = Modifier.weight(1f),
                value = text,
                onValueChange = {
                    text = it
                },
                label = { Text("Enter your message here") }
            )
            Button(modifier = Modifier.padding(8.dp),
                onClick = {
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