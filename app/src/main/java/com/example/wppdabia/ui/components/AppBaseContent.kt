package com.example.wppdabia.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.wppdabia.R
import com.example.wppdabia.data.UserData
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.domain.utils.ImageHandler
import com.example.wppdabia.ui.SharedViewModel
import com.example.wppdabia.ui.components.bottomsheet.ChooseImageBottomSheet
import com.example.wppdabia.ui.components.dialog.ImageDialog
import com.example.wppdabia.ui.extensions.getInitials
import com.example.wppdabia.ui.extensions.toUri
import com.example.wppdabia.ui.mock.fakeRepository
import com.example.wppdabia.ui.theme.Typography
import com.example.wppdabia.ui.theme.WppDaBiaTheme

@Composable
fun AppBaseContent(
    title: String,
    onBackClick: () -> Unit,
    hasBackButton: Boolean = true,
    showProfileImage: Boolean = true,
    user: UserData? = null,
    sharedViewModel: SharedViewModel,
    content: @Composable () -> Unit
) {
    WppDaBiaTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
        ) {
            WppDaBiaTopBar(
                title = title,
                onBackClick = onBackClick,
                hasBackButton = hasBackButton,
                showProfileImage = showProfileImage,
                user = user,
                sharedViewModel = sharedViewModel
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WppDaBiaTopBar(
    title: String,
    onBackClick: () -> Unit,
    hasBackButton: Boolean = true,
    showProfileImage: Boolean = true,
    user: UserData?,
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var dropDownImageLoading by remember { mutableStateOf(false) }
    var showPhotoBottomSheet by remember { mutableStateOf(false) }
    lateinit var imageHandler: ImageHandler
    var requestPermission by remember { mutableStateOf(false) }
    var permissionDeniedToast by remember { mutableStateOf(false) }
    var cropErrorToast by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    val errorMessage = sharedViewModel.errorMessage.observeAsState().value

    imageHandler = remember {
        ImageHandler(
            onImageCaptured = { bitmap ->
                imageHandler.startCrop(context, bitmap.toUri(context))
            },
            onImageSelected = { uri ->
                imageHandler.startCrop(context, uri)
            },
            onImageCropped = { croppedUri ->
                dropDownImageLoading = true
                sharedViewModel.saveCapturedImage(croppedUri)
                sharedViewModel.updateProfileImage(
                    newImageUrl = croppedUri.toString(),
                    onSuccess = {
                        dropDownImageLoading = false
                    },
                    onError = {
                        dropDownImageLoading = false
                        cropErrorToast = true
                    }
                )
            },
            onCropError = { error ->
                cropErrorToast = error
            }
        )
    }

    if (requestPermission) {
        imageHandler.RequestCameraPermission(
            onPermissionGranted = {
                imageHandler.cameraLauncher.launch(null)
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

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontStyle = FontStyle.Italic,
                    fontSize = 20.sp
                )
            )
        },
        navigationIcon = {
            if (hasBackButton) {
                IconButton(onClick = { onBackClick.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        actions = {
            if (showProfileImage) {
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .wrapContentWidth()
                        .size(24.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(180.dp))
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(180.dp)
                        )
                        .clickable {
                            showMenu = !showMenu
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!user?.profileImageUrl.isNullOrEmpty()) {
                        Image(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(180.dp)),
                            painter = rememberAsyncImagePainter(user?.profileImageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds
                        )
                    } else {
                        Text(
                            text = user?.name?.getInitials() ?: "",
                            style = Typography.titleMedium.copy(
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))

                DropdownMenu(
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.background)
                        .shadow(elevation = 1.dp),
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .wrapContentWidth()
                                .size(100.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(180.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (user?.profileImageUrl != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = user.profileImageUrl,
                                        onLoading = {
                                            dropDownImageLoading = true
                                        },
                                        onError = {
                                            dropDownImageLoading = false
                                        },
                                        onSuccess = {
                                            dropDownImageLoading = false
                                        }
                                    ),
                                    contentDescription = "Imagem de perfil",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            showImageDialog = true
                                        },
                                )
                            } else if (dropDownImageLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(60.dp),
                                    strokeWidth = 5.dp,
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = user?.name?.getInitials() ?: "Desconhecido",
                                    style = Typography.titleMedium.copy(
                                        fontSize = 60.sp,
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        shape = RoundedCornerShape(180.dp)
                                    )
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(
                                    onClick = {
                                        showMenu = false
                                        showPhotoBottomSheet = true
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_add_photo),
                                        contentDescription = "Selecionar foto",
                                        tint = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(4.dp)
                                .clickable {
                                    sharedViewModel.logout()
                                    showMenu = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Logout",
                                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }

            if (permissionDeniedToast) {
                Toast.makeText(LocalContext.current, "Permissão Negada", Toast.LENGTH_SHORT).show()
                permissionDeniedToast = !permissionDeniedToast
            }
            if (cropErrorToast) {
                Toast.makeText(LocalContext.current, "Erro ao cortar a imagem", Toast.LENGTH_SHORT)
                    .show()
                cropErrorToast = !cropErrorToast
            }

            if (showPhotoBottomSheet) {
                ChooseImageBottomSheet(
                    removePhotoEnabled = user?.profileImageUrl != null,
                    onCameraClick = {
                        requestPermission = true
                    },
                    onGalleryClick = { imageHandler.galleryLauncher.launch("image/*") },
                    onDeleteClick = {
                        sharedViewModel.deleteProfilePhoto()
                    },
                    onDismiss = { showPhotoBottomSheet = false }
                )
            }

            if (showImageDialog) {
                ImageDialog(
                    imageUrl = user?.profileImageUrl,
                    onDismissRequest = { showImageDialog = false })
            }

            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_SHORT).show()
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
@Preview
fun WppDaBiaTopBarPreview() {
    WppDaBiaTopBar(
        title = "Wpp da Bia",
        onBackClick = {},
        user = UserData(name = "Thiago Maia"),
        sharedViewModel = SharedViewModel(
            fakeRepository,
            PreferencesManager(LocalContext.current)
        )
    )

}