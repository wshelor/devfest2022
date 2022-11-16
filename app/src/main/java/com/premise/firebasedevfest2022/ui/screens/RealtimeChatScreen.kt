package com.premise.firebasedevfest2022.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.premise.firebasedevfest2022.domain.RealtimeChatViewModel
import kotlinx.coroutines.delay

@Composable
internal fun RealtimeChatScreen(viewModel: RealtimeChatViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Realtime Chat", fontSize = 18.sp)
            })
        }

    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            val state = viewModel.dataState.collectAsState()
            val listState = rememberLazyListState()

            LaunchedEffect(state.value) {
                if (state.value.lastIndex > 1) {
                    listState.animateScrollToItem(state.value.lastIndex)
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                state = listState,
                contentPadding = PaddingValues(6.dp)
            ) {
                items(state.value) {
                    Card(
                        Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        backgroundColor = Color(0x22888888),
                        elevation = 8.dp
                    ) {
                        Column {
                            Text(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp), text = it.message, fontSize = 16.sp)
                            Text(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp), text = it.sender, fontSize = 12.sp)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
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
                        if(text.isNotBlank()){
                            viewModel.addMessage(text)
                            text = ""
                        }
                    }) {
                    Image(
                        painter = painterResource(id = com.premise.firebasedevfest2022.R.drawable.send),
                        contentDescription = "Send button"
                    )
                }
            }
        }
    }
}

object RealtimeChatScreen {
    const val SCREEN_ROUTE = "/realtimeChat/"
}