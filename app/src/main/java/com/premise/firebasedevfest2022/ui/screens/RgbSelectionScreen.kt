package com.premise.firebasedevfest2022.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.premise.firebasedevfest2022.domain.RgbSelectionScreenViewModel

@Composable
internal fun RgbSelectionScreen(viewModel: RgbSelectionScreenViewModel) {
    val state = viewModel.dataState.collectAsState()
    Column {
        Text(text = "RGB Battle!  Who will have the most points?")
        Row(modifier = Modifier.weight(1f).padding(4.dp)) {
           Box(modifier = Modifier
               .weight(1f)
               .background(Color(0x44FF0000))
               .padding(6.dp)
               .alpha(0.2f),
               contentAlignment = Alignment.Center) {
               Text(modifier = Modifier.padding(4.dp), text = "red - ${state.value.redPoints}")
           } 
           Box(modifier = Modifier
               .weight(1f)
               .background(Color(0x4400FF00))
               .padding(6.dp),
               contentAlignment = Alignment.Center) {
               Text(modifier = Modifier.padding(4.dp), text = "${state.value.greenPoints}")
           } 
           Box(modifier = Modifier
               .weight(1f)
               .background(Color(0x440000ff))
               .padding(6.dp)
               .alpha(0.2f),
           contentAlignment = Alignment.Center) {
               Text(modifier = Modifier.padding(4.dp), text = "${state.value.bluePoints}")
           } 
        }
        
        Button(modifier = Modifier.padding(4.dp), onClick = { viewModel.addPoint() }) {
            Text(text = "Add a point to the ${state.value.currentTeam} team")
        }
    }
}
object RgbSelectionScreen {
    const val SCREEN_ROUTE = "/rgbSelection/"
}