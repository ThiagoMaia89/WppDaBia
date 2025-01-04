package com.simplesoftware.wppdabia.ui.messages

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.icu.text.ListFormatter.Width
import android.media.MediaPlayer
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import com.simplesoftware.wppdabia.R
import com.simplesoftware.wppdabia.data.data_store.PreferencesManager
import com.simplesoftware.wppdabia.domain.utils.AudioRecorder
import com.simplesoftware.wppdabia.domain.utils.ChatStateManager
import com.simplesoftware.wppdabia.domain.utils.ImageHandler
import com.simplesoftware.wppdabia.ui.SharedViewModel
import com.simplesoftware.wppdabia.ui.components.AppBaseContent
import com.simplesoftware.wppdabia.ui.components.MessageView
import com.simplesoftware.wppdabia.ui.components.RecordAudioProgressView
import com.simplesoftware.wppdabia.ui.components.bottomsheet.ChooseImageBottomSheet
import com.simplesoftware.wppdabia.ui.components.dialog.ImageDialog
import com.simplesoftware.wppdabia.ui.mock.fakeRepository

@Composable
fun MessageScreen(
    navController: NavController,
    viewModel: MessageViewModel,
    chatId: String,
    contactId: String,
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsState(initial = emptyList())
    var messageInput by remember { mutableStateOf("") }
    var showPhotoBottomSheet by remember { mutableStateOf(false) }
    val imageUrl = viewModel.capturedImageUri.observeAsState().value
    lateinit var imageHandler: ImageHandler

    var showImageDialog by remember { mutableStateOf(false) }
    var clickedImageUrl by remember { mutableStateOf<String?>(null) }

    val placeHolderText = if (imageUrl != null) "legenda" else "mensagem"

    var requestPermission by remember { mutableStateOf(false) }
    var permissionDeniedToast by remember { mutableStateOf(false) }
    var cropErrorToast by remember { mutableStateOf(false) }

    val audioRecorder = remember { AudioRecorder() }
    var isRecording by remember { mutableStateOf(false) }

    imageHandler = remember {
        ImageHandler(
            onImageCaptured = { uri ->
                imageHandler.startCrop(context, uri, false)
            },
            onImageSelected = { uri ->
                imageHandler.startCrop(context, uri, false)
            },
            onImageCropped = { croppedUri ->
                viewModel.saveCapturedImage(croppedUri)
            },
            onCropError = { error ->
                cropErrorToast = error
            }
        )
    }
    if (requestPermission) {
        imageHandler.RequestCameraPermission(
            onPermissionGranted = {
                imageHandler.captureImage(context)
                requestPermission = false
                showPhotoBottomSheet = false
            },
            onPermissionDenied = {
                permissionDeniedToast = true
                requestPermission = false
                showPhotoBottomSheet = false
            }
        )
    }
    imageHandler.InitializeLaunchers()

    LaunchedEffect(Unit) {
        viewModel.fetchMessages(chatId)
    }

    LaunchedEffect(Unit) {
        viewModel.setActiveChatUserId(contactId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setActiveChatUserId(null)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column {
                if (isRecording) {
                    RecordAudioProgressView(
                        onRecordingComplete = {
                            isRecording = false
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        showPhotoBottomSheet = true
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add_photo),
                            contentDescription = "Enviar Foto",
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (imageUrl != null) {
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                SubcomposeAsyncImage(
                                    model = imageUrl,
                                    modifier = Modifier
                                        .size(130.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentDescription = "Imagem enviada",
                                    contentScale = ContentScale.Crop,
                                    loading = {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(12.dp),
                                            strokeWidth = 10.dp,
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
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.tertiary,
                                            shape = RoundedCornerShape(180.dp)
                                        )
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = {
                                            viewModel.cleanImageSent()
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Apagar foto",
                                            tint = MaterialTheme.colorScheme.onTertiary
                                        )
                                    }
                                }
                            }
                        }
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                focusedContainerColor = MaterialTheme.colorScheme.background
                            ),
                            placeholder = {
                                Text(
                                    text = placeHolderText,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                keyboardType = KeyboardType.Text
                            ),
                        )
                    }
                    val animateToRecord = animateDpAsState(
                        targetValue = if (isRecording) 80.dp else 38.dp,
                        animationSpec = tween(durationMillis = 200),
                        label = "animateToRecord"
                    )

                    val animateBackgroundToRecord = animateColorAsState(
                        targetValue = if (isRecording) MaterialTheme.colorScheme.tertiary else Color.Transparent,
                        animationSpec = tween(durationMillis = 200),
                        label = "animateBackgroundToRecord"
                    )

                    Box(
                        modifier = Modifier
                            .size(animateToRecord.value)
                            .background(color = animateBackgroundToRecord.value, shape = RoundedCornerShape(180.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        try {
                                            if (ContextCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.RECORD_AUDIO
                                                )
                                                != PackageManager.PERMISSION_GRANTED
                                            ) {
                                                ActivityCompat.requestPermissions(
                                                    context as Activity,
                                                    arrayOf(Manifest.permission.RECORD_AUDIO),
                                                    200
                                                )
                                            } else {
                                                isRecording = true
                                                audioRecorder.startRecording(context)
                                                awaitRelease()
                                                isRecording = false
                                                val audioUri = audioRecorder.stopRecording()
                                                audioUri?.let {
                                                    val mediaPlayer = MediaPlayer().apply {
                                                        setDataSource(context, it)
                                                        prepare()
                                                    }
                                                    val audioDuration = mediaPlayer.duration / 1000
                                                    mediaPlayer.release()
                                                    viewModel.sendMessage(
                                                        chatId = chatId,
                                                        contactId = contactId,
                                                        audioUrl = it.toString(),
                                                        audioDuration = audioDuration,
                                                        image = imageUrl,
                                                        lastMessage = messageInput
                                                    )
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            isRecording = false
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(if (isRecording) 62.dp else 24.dp),
                            painter = painterResource(R.drawable.ic_mic),
                            contentDescription = "Enviar Áudio",
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }

                    IconButton(
                        onClick = {
                            if (messageInput.isNotBlank() || imageUrl != null) {
                                viewModel.sendMessage(
                                    chatId = chatId,
                                    contactId = contactId,
                                    text = messageInput,
                                    image = imageUrl,
                                    lastMessage = messageInput
                                )
                                messageInput = ""
                                viewModel.cleanImageSent()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar Mensagem",
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        val listState = rememberLazyListState()

        LaunchedEffect(messages) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.Bottom,
            contentPadding = paddingValues
        ) {
            itemsIndexed(messages) { index, message ->
                val isSentByUser = message.sender.uid != contactId
                viewModel.setMessageAsRead(chatId)
                MessageView(
                    messageData = message,
                    isSentByUser = isSentByUser,
                    onImageClick = {
                        clickedImageUrl = message.messageImage
                        showImageDialog = true
                    },
                    isUploading = if (index == messages.lastIndex) viewModel.isUploading.collectAsState().value else false
                )
            }
        }
        if (showPhotoBottomSheet) {
            ChooseImageBottomSheet(
                onCameraClick = {
                    requestPermission = true
                },
                onGalleryClick = {
                    imageHandler.galleryLauncher.launch("image/*")
                    showPhotoBottomSheet = false
                },
                onDeleteClick = {
                    showPhotoBottomSheet = false
                },
                onDismiss = { showPhotoBottomSheet = false }
            )
        }

        if (showImageDialog) {
            ImageDialog(
                imageUrl = clickedImageUrl,
                onDismissRequest = { showImageDialog = false }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MessageScreenPreview() {
    AppBaseContent(
        title = "Mensagem",
        onBackClick = {},
        sharedViewModel = SharedViewModel(fakeRepository, PreferencesManager(LocalContext.current))
    ) {
        MessageScreen(
            rememberNavController(),
            MessageViewModel(fakeRepository, ChatStateManager()),
            "",
            ""
        )
    }
}