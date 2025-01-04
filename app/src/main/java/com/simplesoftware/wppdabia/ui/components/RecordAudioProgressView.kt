package com.simplesoftware.wppdabia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
            .height(48.dp)
            .background(color = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 2.dp, horizontal = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            painter = painterResource(R.drawable.ic_mic),
            contentDescription = "Enviar Áudio",
            tint = MaterialTheme.colorScheme.primary
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(end = 2.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Arraste o dedo para cancelar",
                style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onPrimary)
            )
        }
        Text(
            modifier = Modifier.padding(end = 2.dp),
            text = "$timeInSecond/30s",
            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary)
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