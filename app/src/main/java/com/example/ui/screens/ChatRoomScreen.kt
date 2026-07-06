package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MessageEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.WaveformPlayer
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    viewModel: ChatViewModel,
    onBackClick: () -> Unit
) {
    val activeContact by viewModel.activeContact.collectAsState()
    val messages by viewModel.activeChatMessages.collectAsState()
    var textState by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Share Attachment Dialog Simulation
    var showAttachmentMenu by remember { mutableStateOf(false) }

    // Voice Note Recording Simulation States
    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableStateOf(0) }

    // Auto-scroll list when message size increases
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Timer simulation for voice recording
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingDuration = 0
            while (isRecording) {
                delay(1000)
                recordingDuration += 1
            }
        }
    }

    val contact = activeContact ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // Full-screen custom dark luxury wallpaper
        Image(
            painter = painterResource(id = R.drawable.img_chat_wallpaper),
            contentDescription = "Chat Wallpaper",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.18f) // High contrast for text readability
        )

        Scaffold(
            topBar = {
                // Glassmorphic top bar
                GlassmorphicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    cornerRadius = 20.dp,
                    borderWidth = 1.dp,
                    isPremiumGold = contact.isOnline
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = PremiumGold
                            )
                        }

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(RoyalBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contact.displayName.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PremiumGold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Name & Presence Details
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = contact.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (contact.isOnline) {
                                    // Pulse dot animation
                                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                                    val alpha by infiniteTransition.animateFloat(
                                        initialValue = 0.4f,
                                        targetValue = 1f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1000, easing = EaseInOutSine),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "alpha"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .alpha(alpha)
                                            .clip(CircleShape)
                                            .background(PresenceOnline)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = if (contact.isOnline) "Securely Online" else contact.lastSeenText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (contact.isOnline) PresenceOnline else TextSecondaryDark
                                )
                            }
                        }

                        // Action Icons (Call & Video)
                        IconButton(onClick = { /* simulated */ }) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Secure Call",
                                tint = PremiumGold
                            )
                        }
                        IconButton(onClick = { /* simulated */ }) {
                            Icon(
                                imageVector = Icons.Default.VideoCall,
                                contentDescription = "Secure Video",
                                tint = PremiumGold
                            )
                        }
                    }
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
            ) {
                // Messages Area
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { message ->
                        MessageBubble(message = message)
                    }
                }

                // Recording or Typing bar details
                AnimatedVisibility(
                    visible = isRecording,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AmoledSurface)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val infiniteTransition = rememberInfiniteTransition(label = "mic")
                                val redAlpha by infiniteTransition.animateFloat(
                                    initialValue = 0.2f,
                                    targetValue = 1f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(600),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "redAlpha"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .alpha(redAlpha)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recording Voice Note... 🎙️",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "0:${recordingDuration.toString().padStart(2, '0')}",
                                color = PremiumGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Input Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chat Control Row
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(AmoledSurface)
                            .border(1.dp, Color(0x1Fffffff), RoundedCornerShape(24.dp))
                            .padding(horizontal = 12.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Clip Attachment Button
                        IconButton(onClick = { showAttachmentMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Add attachments",
                                tint = PremiumGold
                            )
                        }

                        // Text Input
                        TextField(
                            value = textState,
                            onValueChange = { textState = it },
                            placeholder = { Text("Secure message...", color = Color(0x66F1F5F9)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (textState.isNotBlank()) {
                                        viewModel.sendMessage(textState)
                                        textState = ""
                                        keyboardController?.hide()
                                    }
                                }
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("active_chat_text_input")
                        )

                        // Voice Recorder Click simulation (Send custom note if text empty)
                        if (textState.isBlank()) {
                            IconButton(
                                onClick = {
                                    if (isRecording) {
                                        isRecording = false
                                        if (recordingDuration > 0) {
                                            viewModel.sendVoiceNote(recordingDuration)
                                        }
                                    } else {
                                        isRecording = true
                                    }
                                },
                                modifier = Modifier.testTag("microphone_record_btn")
                            ) {
                                Icon(
                                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = "Voice note recorder",
                                    tint = if (isRecording) Color.Red else PremiumGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Circular Gold Send Button
                    if (textState.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PremiumGold)
                                .clickable {
                                    viewModel.sendMessage(textState)
                                    textState = ""
                                    keyboardController?.hide()
                                }
                                .testTag("send_msg_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send secure message",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Attachment share selection sheet simulation
        if (showAttachmentMenu) {
            AlertDialog(
                onDismissRequest = { showAttachmentMenu = false },
                title = { Text("Secure Media Attachment", color = PremiumGold, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            "Select premium content to transmit through the Khanzada encrypted network:",
                            color = Color.LightGray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    viewModel.sendImageMessage("📷 Sent an image: Khanzada Palace")
                                    showAttachmentMenu = false
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(RoyalBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Image, contentDescription = "Camera", tint = PremiumGold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Gallery", color = Color.White, fontSize = 12.sp)
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    viewModel.sendVoiceNote(15)
                                    showAttachmentMenu = false
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(RoyalBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AudioFile, contentDescription = "Audio", tint = PremiumGold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Audio File", color = Color.White, fontSize = 12.sp)
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    viewModel.sendMessage("📄 Secure_Doc_Hashed.pdf (1.2 MB)")
                                    showAttachmentMenu = false
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(RoyalBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.FilePresent, contentDescription = "Document", tint = PremiumGold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Document", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAttachmentMenu = false }) {
                        Text("Cancel", color = PremiumGold)
                    }
                },
                containerColor = AmoledSurface,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun MessageBubble(message: MessageEntity) {
    val bubbleShape = if (message.isSentByMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 2.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
    } else {
        RoundedCornerShape(topStart = 2.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
    }

    val bubbleBg = if (message.isSentByMe) {
        RoyalBlueLight // Elegant Dark sent message blue (#1E40AF)
    } else {
        Color(0x0CFFFFFF) // Translucent glassmorphic white/5
    }

    val borderBrush = if (message.isSentByMe) {
        Brush.linearGradient(
            colors = listOf(
                RoyalBlueLight,
                RoyalBlueLight.copy(alpha = 0.5f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0x13FFFFFF), // Elegant translucent border-white/5
                Color(0x05FFFFFF)
            )
        )
    }

    val alignment = if (message.isSentByMe) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (message.isSentByMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(bubbleBg)
                    .border(1.dp, borderBrush, bubbleShape)
                    .padding(12.dp)
            ) {
                Column {
                    // Handle Message Types
                    when (message.type) {
                        "TEXT" -> {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                textAlign = TextAlign.Start
                            )
                        }
                        "AUDIO" -> {
                            WaveformPlayer(
                                durationSec = message.audioDurationSec,
                                isSentByMe = message.isSentByMe,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        "IMAGE" -> {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Simulated high-end image attachment
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = "Image preview",
                                        tint = PremiumGold,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        "ENCRYPTED MEDIA",
                                        color = PremiumGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = message.text,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Date & Status Ticks Row
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val dateStr = SimpleDateFormat("h:mm a", Locale.getDefault())
                            .format(Date(message.timestamp))
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = Color(0x99F1F5F9),
                            textAlign = TextAlign.End
                        )

                        if (message.isSentByMe) {
                            AnimatedTicks(status = message.deliveryStatus)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedTicks(status: String) {
    when (status) {
        "PENDING" -> {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Pending secure send",
                tint = PremiumGold.copy(alpha = 0.6f),
                modifier = Modifier.size(12.dp)
            )
        }
        "SENT" -> {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Message Sent securely",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(12.dp)
            )
        }
        "DELIVERED" -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Message Delivered to recipient",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(12.dp)
            )
        }
        "READ" -> {
            // WhatsApp-like blue ticks
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Message Read by recipient",
                tint = DeliveryBlue,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
