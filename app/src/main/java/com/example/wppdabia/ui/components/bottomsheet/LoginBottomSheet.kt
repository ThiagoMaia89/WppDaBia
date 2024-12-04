package com.example.wppdabia.ui.components.bottomsheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.example.wppdabia.R
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.network.RemoteImpl
import com.example.wppdabia.ui.login.RegisterViewModel
import com.example.wppdabia.ui.theme.WppDaBiaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBottomSheet(
    registerViewModel: RegisterViewModel,
    onDismiss: () -> Unit,
    onLogin: (String, String) -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))}
    ) {
        LoginBottomSheetContent(
            registerViewModel = registerViewModel,
            onLogin = onLogin
        )
    }
}

@Composable
private fun LoginBottomSheetContent(
    registerViewModel: RegisterViewModel,
    onLogin: (String, String) -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoading = registerViewModel.loginLoading.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp,top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Faça Login",
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
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

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth().height(IntrinsicSize.Min),
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            onClick = {
                if (email.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    onLogin.invoke(email, password)
                }
            }
        ) {
            if (isLoading) CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 3.dp,
                color = Color.White
            )
            else {
                Text(
                    modifier = Modifier.wrapContentWidth(),
                    text = "Login",
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun LoginBottomSheetPreview() {
    WppDaBiaTheme {
        LoginBottomSheetContent(
            registerViewModel = RegisterViewModel(PreferencesManager(LocalContext.current), remote = RemoteImpl()),
            onLogin = ({ _, _ -> })
        )
    }
}