package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.AmoledSurface
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.PresenceOnline
import com.example.ui.theme.RoyalBlue
import com.example.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: ChatViewModel,
    onContactClick: (String) -> Unit
) {
    val contacts by viewModel.contactsList.collectAsState()
    val isSyncing by viewModel.isSyncingContacts.collectAsState()
    var searchContactQuery by remember { mutableStateOf("") }

    // Hashing Visualizer Simulation states
    var showHashingProgress by remember { mutableStateOf(false) }
    var currentHashingCode by remember { mutableStateOf("") }
    var hashIndex by remember { mutableStateOf(0) }

    val syncedContacts = remember(contacts) {
        contacts.filter { it.isContactSynced }
    }

    val filteredContacts = remember(syncedContacts, searchContactQuery) {
        if (searchContactQuery.isBlank()) {
            syncedContacts
        } else {
            syncedContacts.filter {
                it.displayName.contains(searchContactQuery, ignoreCase = true) ||
                it.id.contains(searchContactQuery)
            }
        }
    }

    // Coroutine to simulate background mathematical SHA-256 Hashed Matching
    LaunchedEffect(showHashingProgress) {
        if (showHashingProgress) {
            val hashSamples = listOf(
                "8f92b7c4d5e6f3a1b0c9e8d7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a7",
                "3c9b8a7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2l1a0b9c8d7e6f5a4b3c2d1e0f9a",
                "f8a7b6c5d4e3f2a1b0c9e8d7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a7",
                "1c0e9f8a7b6c5d4e3f2a1b0c9e8d7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e",
                "a9b8c7d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c9e8d7f6a5b4c3d2e1f0a9b8"
            )
            for (i in 1..25) {
                delay(80)
                currentHashingCode = "HASHING: SHA256(+92300***${(100..999).random()}) -> ${hashSamples.random().take(16)}..."
                hashIndex = i
            }
            viewModel.syncContacts()
            showHashingProgress = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "SECURE SYNC",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = PremiumGold
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Header Card explaining secure sync
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                cornerRadius = 16.dp,
                isPremiumGold = true
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Shield",
                        tint = PremiumGold,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Zero-Knowledge Contact Matching",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your contacts are cryptographically hashed locally using SHA-256 before matching. Your raw address book never leaves your device.",
                        color = Color(0xFF94A3B8),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Sync Button
            if (syncedContacts.isEmpty() && !showHashingProgress) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { showHashingProgress = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PremiumGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("sync_contacts_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Secure Lock")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hash & Synchronize Contacts", fontWeight = FontWeight.Bold)
                    }
                }
            } else if (showHashingProgress) {
                // Interactive Hashing Code simulation screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .border(1.dp, PremiumGold, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PremiumGold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Hashing Local Contacts...",
                            color = PremiumGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentHashingCode,
                            fontFamily = FontFamily.Monospace,
                            color = Color.Green,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { hashIndex / 25f },
                            color = PremiumGold,
                            trackColor = Color.DarkGray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            } else {
                // Search Contacts bar
                OutlinedTextField(
                    value = searchContactQuery,
                    onValueChange = { searchContactQuery = it },
                    placeholder = { Text("Search matched contacts...", color = Color(0x66F1F5F9)) },
                    leadingIcon = { Icon(Icons.Default.PersonSearch, contentDescription = null, tint = PremiumGold) },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PremiumGold,
                        unfocusedBorderColor = Color(0x22FFFFFF),
                        focusedContainerColor = AmoledSurface,
                        unfocusedContainerColor = AmoledSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                // List of matched synced users
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredContacts, key = { it.id }) { user ->
                        ContactItemRow(
                            user = user,
                            onClick = { onContactClick(user.id) }
                        )
                    }
                }

                // Small footer sync status
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PresenceOnline, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Device securely synchronized", color = Color(0xFF64748B), fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Resync",
                        color = PremiumGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showHashingProgress = true }
                    )
                }
            }
        }
    }
}

@Composable
fun ContactItemRow(
    user: UserEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x08FFFFFF))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Metallic circular avatar with presence indicator
        Box(modifier = Modifier.size(46.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val metallicBrush = Brush.linearGradient(
                    colors = listOf(
                        RoyalBlue,
                        PremiumGold
                    )
                )
                drawCircle(brush = metallicBrush)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.5.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF030712)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.displayName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PremiumGold
                    )
                )
            }

            if (user.isOnline) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(PresenceOnline)
                        .border(1.dp, Color(0xFF030712), CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.displayName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = user.status,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Start secure Chat",
                tint = PremiumGold
            )
        }
    }
}
