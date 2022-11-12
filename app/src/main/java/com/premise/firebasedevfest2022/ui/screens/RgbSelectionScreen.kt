package com.premise.firebasedevfest2022.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.premise.firebasedevfest2022.domain.RgbSelectionScreenViewModel

@Composable
internal fun RgbSelectionScreen(viewModel: RgbSelectionScreenViewModel) {
    val state = viewModel.dataState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "RGB Battle!", fontSize = 24.sp)
                },
            )
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            Text(modifier = Modifier.padding(10.dp), text = "Who will earn the most points?")
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            ) {
                Box(Modifier
                    .weight(1f)
                    .height(100.dp)
                    .padding(6.dp)) {
                    Box(
                        modifier = Modifier.background(Color(0x44FF0000)).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(modifier = Modifier.padding(4.dp), text = "${state.value.redPoints}")
                    }
                }
                Box(Modifier
                    .weight(1f)
                    .height(100.dp)
                    .padding(6.dp)) {
                    Box(
                        modifier = Modifier.background(Color(0x4400FF00)).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(modifier = Modifier.padding(4.dp), text = "${state.value.greenPoints}")
                    }
                }
                Box(Modifier
                    .weight(1f)
                    .height(100.dp)
                    .padding(6.dp)) {
                    Box(
                        modifier = Modifier.background(Color(0x440000FF)).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(modifier = Modifier.padding(4.dp), text = "${state.value.bluePoints}")
                    }
                }
            }

            Button(modifier = Modifier.padding(4.dp).fillMaxWidth(), onClick = { viewModel.addPoint() }) {
                Text(text = "Add a point to the ${state.value.currentTeam} team")
            }
        }
    }
}

object RgbSelectionScreen {
    const val SCREEN_ROUTE = "/rgbSelection/"
}