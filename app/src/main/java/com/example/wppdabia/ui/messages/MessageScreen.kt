package com.example.wppdabia.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.wppdabia.R
import com.example.wppdabia.data.data_store.PreferencesManager
import com.example.wppdabia.ui.SharedViewModel
import com.example.wppdabia.ui.components.AppBaseContent
import com.example.wppdabia.ui.components.MessageView
import com.example.wppdabia.ui.mock.fakeRemote

@Composable
fun MessageScreen(
    navController: NavController,
    viewModel: MessageViewModel,
    chatId: String,
    contactId: String,
) {
    val messages by viewModel.messages.collectAsState(initial = emptyList())
    var messageInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchMessages(chatId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
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
                IconButton(onClick = { viewModel.sendPhoto() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_photo),
                        contentDescription = "Enviar Foto",
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }

                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background
                    ),
                    placeholder = {
                        Text(
                            text = "Mensagem",
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text
                    ),
                )

                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank()) {
                            viewModel.sendMessage(chatId = chatId, contactId = contactId, content = messageInput, lastMessage = messageInput)
                            messageInput = ""
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
            items(messages) { message ->
                val isSentByUser = message.sender.uid != contactId
                viewModel.setMessageAsRead(chatId)
                MessageView(messageData = message, isSentByUser = isSentByUser)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MessageScreenPreview() {
    AppBaseContent(
        title = "Mensagem",
        onBackClick = {},
        sharedViewModel = SharedViewModel(fakeRemote, PreferencesManager(LocalContext.current))
    ) {
        MessageScreen(rememberNavController(), MessageViewModel(fakeRemote), "", "")
    }
}