package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.AmoledSurface
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.RoyalBlue
import com.example.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ChatViewModel) {
    val myName by viewModel.myProfileName.collectAsState()
    val myStatus by viewModel.myProfileStatus.collectAsState()
    val myPhone by viewModel.myProfilePhone.collectAsState()

    var nameInput by remember { mutableStateOf(myName) }
    var statusInput by remember { mutableStateOf(myStatus) }

    // Avatar cropping simulation overlay states
    var showCropOverlay by remember { mutableStateOf(false) }
    var zoomScale by remember { mutableStateOf(1.2f) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "PROFILE SETUP",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Avatar container with click to crop edit
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { showCropOverlay = true }
                        .testTag("avatar_container"),
                    contentAlignment = Alignment.Center
                ) {
                    // Metallic Border Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFFD4AF37),
                                    Color(0xFF0F2545),
                                    Color(0xFFD4AF37),
                                    Color(0xFF1E3A8A),
                                    Color(0xFFD4AF37)
                                )
                            ),
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }

                    // Simulated Profile Avatar content
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF030712)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Edit Avatar",
                                tint = PremiumGold,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("EDIT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Tap to edit & crop avatar", color = Color(0xFF64748B), fontSize = 12.sp)

                Spacer(modifier = Modifier.height(24.dp))

                // Gold Premium Badge Card
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 16.dp,
                    isPremiumGold = true
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "Verified crown badge",
                            tint = PremiumGold,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Khanzada Verified Premium",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Secure calling & cryptographically signed metadata active.",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Display Name Text Field
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Display Name", color = PremiumGold) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
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
                        .padding(bottom = 16.dp)
                        .testTag("profile_name_input")
                )

                // Status/Bio Text Field
                OutlinedTextField(
                    value = statusInput,
                    onValueChange = { statusInput = it },
                    label = { Text("Status / Bio", color = PremiumGold) },
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
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
                        .padding(bottom = 24.dp)
                        .testTag("profile_status_input")
                )

                // Phone Read-only Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x06FFFFFF))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PremiumGold)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Encrypted Contact Address", color = Color(0xFF64748B), fontSize = 11.sp)
                        Text(text = myPhone, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save Profile Button
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            viewModel.updateProfile(nameInput, statusInput)
                            Toast.makeText(context, "Premium profile successfully updated!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PremiumGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_profile_btn")
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            // Interactive Crop Overlay simulator
            AnimatedVisibility(
                visible = showCropOverlay,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFA020408)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            "Interactive Crop Simulator",
                            color = PremiumGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Adjust grid alignment bounds below for flawless circle cropping:",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Outer image zoom box
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            // Simulated photo being zoomed
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.LightGray.copy(alpha = 0.5f),
                                modifier = Modifier.size(160.dp * zoomScale)
                            )

                            // Bounding gold crop grid overlay
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = Color(0x66000000),
                                    radius = 100.dp.toPx()
                                )
                                drawCircle(
                                    color = Color(0xFFD4AF37),
                                    radius = 100.dp.toPx(),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Zoom Slider controls
                        Text("Zoom Alignment", color = Color.White, fontSize = 14.sp)
                        Slider(
                            value = zoomScale,
                            onValueChange = { zoomScale = it },
                            valueRange = 1f..2.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = PremiumGold,
                                activeTrackColor = PremiumGold,
                                inactiveTrackColor = Color.DarkGray
                            ),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(
                                onClick = { showCropOverlay = false },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = borderStroke(1.dp, Color.White),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    showCropOverlay = false
                                    Toast.makeText(context, "Avatar crop updated successfully!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PremiumGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Apply Crop", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)
