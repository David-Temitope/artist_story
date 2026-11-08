package com.example.ui.lifesim

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

private val DarkBg = Color(0xFF0F172A)
private val CardBg = Color(0xFF1E293B)
private val SurfaceColor = Color(0xFF334155)
private val PrimaryBrandColor = Color(0xFF6C5CE7)
private val AccentGreen = Color(0xFF00B894)
private val AccentGold = Color(0xFFFFB800)
private val DangerColor = Color(0xFFFF7675)
private val TextPrimary = Color(0xFFF8FAFC)
private val TextSecondary = Color(0xFF94A3B8)
private val BorderColor = Color(0xFF475569)

@Composable
fun CharacterCreationScreen(
    onStartGame: (email: String, firstName: String, lastName: String, gender: String, country: String, avatar: String) -> Unit
) {
    var emailInput by remember { mutableStateOf("player_" + System.currentTimeMillis().toString().takeLast(6)) }
    var firstNameInput by remember { mutableStateOf("") }
    var lastNameInput by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    var selectedCountry by remember { mutableStateOf("United States") }
    var selectedAvatarIndex by remember { mutableStateOf(0) }

    val countries = listOf("United States", "United Kingdom", "Canada", "Nigeria", "Japan", "Germany", "France", "Brazil", "Australia")
    val genders = listOf("Male", "Female", "Non-Binary")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.img_app_icon_1783749117204),
                    contentDescription = "Genesis icon",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "GENESIS",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            Text(
                text = "The Ultimate Life Simulator • Age 18",
                style = MaterialTheme.typography.bodyMedium,
                color = AccentGold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create Your Character",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryBrandColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Portrait Selector
                    Text(
                        text = "Choose Avatar Portrait:",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LifeSimData.PORTRAITS.forEachIndexed { idx, portrait ->
                            val isSelected = selectedAvatarIndex == idx
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) PrimaryBrandColor else BorderColor,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .background(if (isSelected) PrimaryBrandColor.copy(alpha = 0.2f) else SurfaceColor)
                                    .clickable { selectedAvatarIndex = idx },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (idx % 2 == 0) Icons.Default.Face else Icons.Default.Person,
                                    contentDescription = "Portrait $idx",
                                    tint = if (isSelected) PrimaryBrandColor else TextSecondary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // First & Last Name
                    OutlinedTextField(
                        value = firstNameInput,
                        onValueChange = { firstNameInput = it },
                        label = { Text("First Name") },
                        placeholder = { Text("e.g. Alex") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBrandColor,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = lastNameInput,
                        onValueChange = { lastNameInput = it },
                        label = { Text("Last Name (Family Name)") },
                        placeholder = { Text("e.g. Vance") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBrandColor,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Your mother and father will share this last name.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.Start).padding(top = 4.dp, bottom = 12.dp)
                    )

                    // Gender Selection
                    Text(
                        text = "Gender:",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genders.forEach { gender ->
                            val isSelected = selectedGender == gender
                            Button(
                                onClick = { selectedGender = gender },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) PrimaryBrandColor else SurfaceColor,
                                    contentColor = if (isSelected) Color.White else TextSecondary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text(gender, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Country Selection
                    Text(
                        text = "Country of Birth:",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    var countryExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { countryExpanded = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(selectedCountry, color = TextPrimary)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                            }
                        }
                        DropdownMenu(
                            expanded = countryExpanded,
                            onDismissRequest = { countryExpanded = false },
                            modifier = Modifier.background(CardBg)
                        ) {
                            countries.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c, color = TextPrimary) },
                                    onClick = {
                                        selectedCountry = c
                                        countryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Starting Perks Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🎂 Starting Age: 18 Years Old", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("💵 Starting Bank Balance: \$10,000", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("🏡 Living: With Parents (Free lodging until age 21)", color = TextSecondary, fontSize = 11.sp)
                            Text("⚡ Energy: 100% | Aura: 0/100", color = TextSecondary, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val fName = if (firstNameInput.isNotBlank()) firstNameInput.trim() else "Alex"
                            val lName = if (lastNameInput.isNotBlank()) lastNameInput.trim() else "Vance"
                            val avatar = LifeSimData.PORTRAITS.getOrElse(selectedAvatarIndex) { LifeSimData.PORTRAITS[0] }
                            onStartGame(emailInput, fName, lName, selectedGender, selectedCountry, avatar)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBrandColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text(
                            text = "START YOUR LIFE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
