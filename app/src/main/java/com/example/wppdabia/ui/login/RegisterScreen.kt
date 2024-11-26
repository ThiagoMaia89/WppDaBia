package com.example.wppdabia.ui.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.wppdabia.R
import com.example.wppdabia.data.UserData
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.domain.utils.ImageHandler
import com.example.wppdabia.ui.components.AppBaseContent
import com.example.wppdabia.ui.components.ImagePickerBottomSheet
import com.example.wppdabia.ui.extensions.getInitials
import com.example.wppdabia.ui.extensions.toUri
import com.example.wppdabia.ui.navigation.Screen
import com.example.wppdabia.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val imageUri = viewModel.capturedImageUri.observeAsState().value
    var imageLoading by remember { mutableStateOf(false) }
    var registerLoading by remember { mutableStateOf(false) }
    lateinit var imageHandler: ImageHandler

    var requestPermission by remember { mutableStateOf(false) }
    var permissionDeniedToast by remember { mutableStateOf(false) }
    var cropErrorToast by remember { mutableStateOf(false) }

    imageHandler = remember {
        ImageHandler(
            onImageCaptured = { bitmap ->
                imageHandler.startCrop(context, bitmap.toUri(context))
            },
            onImageSelected = { uri ->
                imageHandler.startCrop(context, uri)
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
                imageHandler.cameraLauncher.launch(null)
                requestPermission = false
                showBottomSheet = false
            },
            onPermissionDenied = {
                permissionDeniedToast = true
                requestPermission = false
                showBottomSheet = false
            }
        )
    }

    imageHandler.InitializeLaunchers()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth()
                .padding(top = 24.dp)
                .size(180.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(180.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = imageUri,
                        onLoading = {
                            imageLoading = true
                        },
                        onError = {
                            imageLoading = false
                        },
                        onSuccess = {
                            imageLoading = false
                        }
                    ),
                    contentDescription = "Imagem de perfil",
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                )
            } else if (imageLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    strokeWidth = 5.dp,
                    color = Color.White
                )
            } else {
                Text(
                    text = userName.getInitials(),
                    style = Typography.titleMedium.copy(
                        fontSize = 60.sp,
                    )
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.inversePrimary,
                        shape = RoundedCornerShape(180.dp)
                    )
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        showBottomSheet = true
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_photo),
                        contentDescription = "Selecionar foto"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = userName,
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = MaterialTheme.colorScheme.primary),
            onValueChange = { userName = it },
            label = {
                Text(
                    text = "Nome de usuário",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = MaterialTheme.colorScheme.primary),
            onValueChange = { email = it },
            label = {
                Text(
                    text = "Email",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = MaterialTheme.colorScheme.primary),
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = "Password",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            maxLines = 1,
            trailingIcon = {
                val eyeIcon =
                    if (passwordVisible) R.drawable.ic_visibility_enabled else R.drawable.ic_visibility_disabled
                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Icon(
                        painter = painterResource(eyeIcon),
                        contentDescription = "Password Visibility",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                onClick = {
                    if (userName.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        viewModel.viewModelScope.launch {
                            registerLoading = true
                            viewModel.registerUser(
                                userData = UserData(
                                    name = userName,
                                    email = email,
                                    password = password,
                                    profileImageUrl = imageUri

                                ),
                                onSuccess = {
                                    registerLoading = false
                                    navController.navigate(Screen.Home.route)
                                },
                                onError = {
                                    registerLoading = false
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            ) {
                if (registerLoading) CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp,
                    color = Color.White
                )
                else {
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = "Cadastrar",
                        textAlign = TextAlign.Start
                    )
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
    }
    if (showBottomSheet) {
        ImagePickerBottomSheet(
            onCameraClick = {
                requestPermission = true
            },
            onGalleryClick = { imageHandler.galleryLauncher.launch("image/*") },
            onDismiss = { showBottomSheet = false }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    AppBaseContent(
        title = "Cadastro",
        onBackClick = {}
    ) {
        RegisterScreen(rememberNavController(), RegisterViewModel(PreferencesManager(LocalContext.current)))
    }
}