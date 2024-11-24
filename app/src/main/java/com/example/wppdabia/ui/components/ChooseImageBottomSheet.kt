package com.example.wppdabia.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wppdabia.R
import com.example.wppdabia.ui.theme.WppDaBiaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerBottomSheet(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismiss: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))}
    ) {
        ImagePickerBottomSheetContent(
            onCameraClick = onCameraClick,
            onGalleryClick = onGalleryClick
        )
    }
}

@Composable
private fun ImagePickerBottomSheetContent(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp,top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Escolha uma opção",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        BottomSheetButton(
            onClick = {
                onCameraClick.invoke()
            },
            text = "Tirar foto",
            icon = R.drawable.ic_add_photo
        )
        Spacer(modifier = Modifier.height(4.dp))
        BottomSheetButton(
            onClick = {
                onGalleryClick.invoke()
            },
            text = "Acessar galeria",
            icon = R.drawable.ic_gallery
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun ImagePickerBottomSheetPreview() {
    WppDaBiaTheme {
        ImagePickerBottomSheetContent(
            onCameraClick = {},
            onGalleryClick = {}
        )
    }
}