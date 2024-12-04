package com.example.wppdabia.ui.components.bottomsheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wppdabia.R
import com.example.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun BottomSheetButton(
    onClick: () -> Unit,
    text: String,
    icon: Int,
    enabled: Boolean = true
) {
    Button(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(width = 1.dp, color = if (enabled) MaterialTheme.colorScheme.primary else Color.Transparent),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        onClick = {
            onClick.invoke()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(icon), contentDescription = "")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.wrapContentWidth(),
                text = text,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Preview
@Composable
fun BottomSheetButtonPreview() {
    WppDaBiaTheme {
        BottomSheetButton(
            onClick = { },
            text = "Exemplo",
            icon = R.drawable.ic_add_photo
        )
    }
}