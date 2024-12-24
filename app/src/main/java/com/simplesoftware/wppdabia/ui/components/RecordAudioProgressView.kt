package com.simplesoftware.wppdabia.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simplesoftware.wppdabia.R
import com.simplesoftware.wppdabia.ui.theme.WppDaBiaTheme
import kotlinx.coroutines.delay

@Composable
fun RecordAudioProgressView(
    maxDurationInSeconds: Int = 30,
    onRecordingComplete: () -> Unit
) {

    var progress by remember { mutableFloatStateOf(0.0f) }
    var timeInSecond by remember { mutableStateOf("0") }

    LaunchedEffect(Unit) {
        for (second in 1..maxDurationInSeconds) {
            delay(1000L)
            progress = second / maxDurationInSeconds.toFloat()
            timeInSecond = second.toString()

            if (second == maxDurationInSeconds) {
                onRecordingComplete()
            }
        }
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 2.dp, horizontal = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            painter = painterResource(R.drawable.ic_mic),
            contentDescription = "Enviar Áudio",
            tint = Color.Black
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .height(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .weight(1f)
                .padding(end = 2.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "$timeInSecond/30s",
            style = MaterialTheme.typography.titleMedium.copy(color = Color.Black)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun RecordAudioProgressViewPreview() {
    WppDaBiaTheme {
        RecordAudioProgressView(
            onRecordingComplete = {}
        )
    }
}