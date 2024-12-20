package com.simplesoftware.wppdabia.ui.components.dialog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import com.simplesoftware.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun ImageDialog(
    imageUrl: String?,
    onDismissRequest: () -> Unit
) {

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    if (imageUrl != null) {
        Dialog(onDismissRequest = onDismissRequest) {

            BackHandler(enabled = scale > 1f) {
                scale = 1f
                offset = Offset.Zero
            }

            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(color = Color.Transparent, shape = RoundedCornerShape(8.dp))
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .transformable(state = rememberTransformableState { zoomChange, offsetChange, _ ->
                        scale = (scale * zoomChange).coerceIn(1f, 5f)

                        val maxX = (size.width * (scale - 1) / 2f)
                        val maxY = (size.height * (scale - 1) / 2f)

                        if (scale > 1f) {
                            val newOffset = offset + offsetChange.times(scale)
                            offset = Offset(
                                newOffset.x.coerceIn(-maxX, maxX),
                                newOffset.y.coerceIn(-maxY, maxY)
                            )
                        } else {
                            offset = Offset.Zero
                        }
                    })
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = {
                            if (scale > 1f) {
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                scale = 3f
                            }
                        })
                    }
                    .onSizeChanged {
                        size = it
                    },
                contentAlignment = Alignment.Center
            ) {

                SubcomposeAsyncImage(
                    model = imageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentDescription = "Imagem expandida",
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 8.dp,
                            color = Color.White
                        )
                    },
                    error = {
                        Text(
                            text = "Erro ao carregar imagem",
                            color = Color.Red,
                            modifier = Modifier.size(140.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ImageDialogPreview() {
    WppDaBiaTheme {
        ImageDialog(
            imageUrl = "https://example.com/image.jpg",
            onDismissRequest = {}
        )
    }
}