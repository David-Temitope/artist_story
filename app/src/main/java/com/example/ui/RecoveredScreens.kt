package com.example.ui
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

val imageWallpapers = listOf(
    Triple("DEFAULT", "Neon Grid", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?q=80&w=600&auto=format&fit=crop"),
    Triple("WALLPAPER_1", "Cyber City", "https://images.unsplash.com/photo-1601042879354-f8d227f6f1c4?q=80&w=600&auto=format&fit=crop"),
    Triple("WALLPAPER_2", "Abstract Flow", "https://images.unsplash.com/photo-1557672172-298e090bd0f1?q=80&w=600&auto=format&fit=crop")
)

@Composable
fun WallpaperSettingsScreen(state: GameStateEntity, viewModel: GameViewModel) {
    Column(modifier = Modifier.fillMaxSize().background(Color.Black).verticalScroll(rememberScrollState())) {
        Text("Wallpapers", color = Color.White, modifier = Modifier.padding(16.dp))
        imageWallpapers.forEach { wp ->
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { viewModel.updateState { it.copy(wallpaperId = wp.first) } }) {
                ReliableAsyncImage(
                    model = wp.third,
                    contentDescription = wp.second,
                    fallbackCategory = "wallpaper",
                    fallbackText = wp.second,
                    modifier = Modifier.size(60.dp)
                )
                Text(wp.second, color = Color.White, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun TechStoreScreen(state: GameStateEntity, viewModel: GameViewModel, onPhoneBought: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Text("Tech store migrated to WebHub.", color = Color.White)
    }
}

@Composable
fun ApexBankingScreen(state: GameStateEntity, viewModel: GameViewModel) {
    val scrollState = rememberScrollState()
    var inputAmount by remember { mutableStateOf("") }
    var activeAction by remember { mutableStateOf("DEPOSIT") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E1E)).verticalScroll(scrollState).padding(16.dp)) {
        Text("APEX BANKING", color = Color(0xFFD4AF37), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Balances
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Available Cash: $${String.format("%,.2f", state.cash)}", color = Color.White, fontSize = 18.sp)
                Text("Savings Balance: $${String.format("%,.2f", state.banking.savingsBalance)}", color = Color.Green, fontSize = 18.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Debt Info
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF3C2A2A))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Active Loan: $${String.format("%,.2f", state.banking.loanAmount)}", color = Color.Red)
                Text("Accumulated Interest: $${String.format("%,.2f", state.banking.accumulatedLoanInterest)}", color = Color.Red)
                Text("Tax Debt: $${String.format("%,.2f", state.banking.taxDebt)}", color = Color.Red)
                Text("Tithe Debt: $${String.format("%,.2f", state.banking.titheDebt)}", color = Color.Red)
                Text("Mortgage: $${String.format("%,.2f", state.banking.mortgageAmount)}", color = Color.Red)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Text("Perform Transaction", color = Color.White, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("DEPOSIT", "WITHDRAW", "LOAN", "REPAY LOAN", "PAY TAX", "PAY TITHE", "PAY MORTGAGE").forEach { action ->
                Button(
                    onClick = { activeAction = action },
                    colors = ButtonDefaults.buttonColors(containerColor = if (activeAction == action) Color(0xFFD4AF37) else Color.DarkGray)
                ) {
                    Text(action, color = Color.Black)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = inputAmount,
            onValueChange = { inputAmount = it },
            label = { Text("Amount ($)", color = Color.Gray) },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                val amt = inputAmount.toDoubleOrNull() ?: 0.0
                if (amt > 0) {
                    when (activeAction) {
                        "DEPOSIT" -> viewModel.depositToSavings(amt)
                        "WITHDRAW" -> viewModel.withdrawFromSavings(amt)
                        "LOAN" -> viewModel.applyForLoan(amt)
                        "REPAY LOAN" -> viewModel.repayLoan(amt)
                        "PAY TAX" -> viewModel.payTaxDebt(amt)
                        "PAY TITHE" -> viewModel.titheCash(amt)
                        "PAY MORTGAGE" -> viewModel.repayMortgage(amt)
                    }
                    inputAmount = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CONFIRM TRANSACTION", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun NotesAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var text by remember { mutableStateOf(state.activeNoteText) }
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFFFAED)).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("My Notes", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Button(onClick = { viewModel.saveActiveNoteText(text) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                Text("SAVE", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxSize(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black, cursorColor = Color.Black)
        )
    }
}

@Composable
fun ProfessionalEmailScreen(state: GameStateEntity, viewModel: GameViewModel) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF6200EE)).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Inbox", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Button(onClick = { viewModel.markAllMailAsRead() }, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                Text("Mark All Read", color = Color(0xFF6200EE))
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (state.mailbox.isEmpty()) {
                item { Text("No emails.", color = Color.Gray) }
            }
            items(state.mailbox) { mail ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = if (mail.isRead) Color.White else Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(mail.sender, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(mail.subject, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(mail.body, fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!mail.isRead) {
                                Button(onClick = { viewModel.markMailAsRead(mail.id) }) { Text("Read") }
                            }
                            if (!mail.isAccepted && (mail.cashOffer > 0.0 || mail.type != "NORMAL")) {
                                Button(onClick = { viewModel.acceptMail(mail.id) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("Accept") }
                            }
                            Button(onClick = { viewModel.deleteMail(mail.id) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdCompanyPromotionScreen(state: GameStateEntity, viewModel: GameViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().background(Color.Black).verticalScroll(scrollState).padding(16.dp)) {
        Text("Ad Company Promotion", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Ad Budget: $${String.format("%,.2f", state.adCompanyBudget)}", color = Color.Green, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Active Campaigns", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        if (state.activeAdCampaigns.isEmpty()) {
            Text("No active campaigns.", color = Color.Gray)
        } else {
            state.activeAdCampaigns.forEach { camp ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.DarkGray)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(camp.agencyName, color = Color.Yellow, fontWeight = FontWeight.Bold)
                        Text("Budget: $${String.format("%,.2f", camp.weeklyBudget)}/wk", color = Color.White)
                        Text("Weeks Left: ${camp.weeksRemaining}", color = Color.White)
                        Button(onClick = { viewModel.haltAdCampaign(camp.id) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                            Text("HALT")
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Launch New Campaign", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        listOf(
            "beatwave" to "BeatWave Media ($8k/wk)",
            "starvision" to "StarVision TV ($22k/wk)",
            "urban" to "UrbanBillboards ($6.5k/wk)",
            "soundspot" to "SoundSpot Radio ($4.2k/wk)"
        ).forEach { (id, label) ->
            Button(
                onClick = {
                    val targetSong = state.songs.maxByOrNull { it.totalStreams }?.id ?: "unknown"
                    viewModel.launchAdCampaign(id, targetSong)
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA))
            ) {
                Text(label, color = Color.White)
            }
        }
    }
}

@Composable
fun MerchStudioAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E1E)).verticalScroll(scrollState).padding(16.dp)) {
        Text("Merch Studio", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (state.merchStyles.isEmpty()) {
            Text("No merchandise designs yet.", color = Color.Gray)
        } else {
            state.merchStyles.forEach { style ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(style.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${style.type} - Stock: ${style.stockRemaining}", color = Color.LightGray)
                        Text("Cost: $${String.format("%,.2f", style.unitCost)} | Price: $${String.format("%,.2f", style.sellPrice)}", color = Color.Green)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            Button(onClick = { viewModel.updateMerchPrice(style.id, style.sellPrice + 5.0) }) { Text("+Price") }
                            Button(onClick = { viewModel.updateMerchPrice(style.id, (style.sellPrice - 5.0).coerceAtLeast(style.unitCost)) }) { Text("-Price") }
                            Button(onClick = { viewModel.restockMerchStyle(style.id, 100) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("Restock 100") }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.designMerchStyle("Tour Tee", "T-Shirt", 5.0, 25.0, 500)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA))
        ) {
            Text("Design Basic Tee ($2.5k Setup + $2.5k Stock)", color = Color.White)
        }
        Button(
            onClick = {
                viewModel.designMerchStyle("Premium Hoodie", "Hoodie", 15.0, 60.0, 500)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA))
        ) {
            Text("Design Premium Hoodie ($5k Setup + $7.5k Stock)", color = Color.White)
        }
    }
}

@Composable
fun LegacyAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF221100)).verticalScroll(scrollState).padding(16.dp)) {
        Text("LEGACY SYSTEM", color = Color(0xFFFFD700), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Legacy Points: ${state.legacyPoints}", color = Color.White, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        val legacyMap = listOf(
            Triple("Wikipedia Page", state.legacy.hasWikipediaPage, { viewModel.buyWikipediaPage() }),
            Triple("Official Biography", state.legacy.hasBiography, { viewModel.buyBiography() }),
            Triple("Documentary", state.legacy.hasDocumentary, { viewModel.buyDocumentary() }),
            Triple("Netflix Series", state.legacy.hasNetflixSeries, { viewModel.buyNetflixSeries() }),
            Triple("Charity Scholarships", state.legacy.hasScholarships, { viewModel.toggleScholarships() }),
            Triple("Personal Museum", state.legacy.hasMuseum, { viewModel.buyMuseum() }),
            Triple("Rock & Roll Hall of Fame", state.legacy.hasHallOfFame, { viewModel.buyHallOfFame() })
        )

        legacyMap.forEach { (name, hasIt, onClick) ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF332200))) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(name, color = Color.White, fontWeight = FontWeight.Bold)
                    if (hasIt) {
                        Text("ACQUIRED", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                    } else {
                        Button(onClick = onClick as () -> Unit, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37))) {
                            Text("PURCHASE", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OSUpgradeAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    val scrollState = rememberScrollState()

    data class PhoneOption(val id: String, val name: String, val price: Double, val perkDesc: String)
    val phones = listOf(
        PhoneOption("TECCNO", "TECCNO Spark", 500.0, "Basic smartphone. No special perks."),
        PhoneOption("Xiaomax", "Xiaomax HyperOS", 1500.0, "Faster Processing. Energy costs for social media reduced."),
        PhoneOption("No-Thing", "No-Thing GLYPH", 3000.0, "Zen Interface. Passive stress reduction."),
        PhoneOption("Pixxelate", "Pixxelate Pro", 6000.0, "Pro Camera. Social media posts gain more fans."),
        PhoneOption("Samsong", "Samsong Galaxy", 10000.0, "Multitasking Beast & Unhackable Enclave. Digital streaming revenue +10%, completely unhackable hardware security."),
        PhoneOption("hiPhone", "hiPhone 16 Pro", 15000.0, "Ultimate Status & Unhackable Enclave. Capital club relationship gains +15%, completely unhackable biometric security.")
    )

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)).verticalScroll(scrollState).padding(16.dp)) {
        Text("OS UPGRADE STORE", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        phones.forEach { phone ->
            val isOwned = state.ownedPhones.contains(phone.id)
            val isActive = state.activePhone == phone.id
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.height(80.dp).width(40.dp).clip(RoundedCornerShape(6.dp)).background(Color.Black).border(2.dp, Color(0xFF444444), RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.fillMaxSize().padding(2.dp).clip(RoundedCornerShape(4.dp)).background(Color.DarkGray))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(phone.name, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(if(isActive) "ACTIVE" else if (isOwned) "OWNED" else "$${phone.price}", color = if(isActive) Color(0xFF6200EA) else Color.Gray)
                        Text(phone.perkDesc, color = Color.LightGray, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (!isOwned) {
                            viewModel.buyPhone(phone.id, phone.price)
                        } else if (!isActive) {
                            viewModel.equipPhone(phone.id)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) Color.DarkGray else if (isOwned) Color(0xFF6200EA) else Color(0xFF4CAF50)
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    enabled = !isActive
                ) {
                    Text(
                        text = if (isActive) "Currently Equipped" else if (isOwned) "Equip Device" else "Purchase Device",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A)).verticalScroll(scrollState).padding(16.dp)) {
        Text("AEGIS SECURITY", color = Color(0xFF00E676), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Active Detail: ${state.hiredSecurityCompany}", color = Color.White, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Vanguard Tactical", color = Color.White, fontWeight = FontWeight.Bold)
                Text("$5,000 / week", color = Color.Gray)
                Button(onClick = { viewModel.hireSecurityCompany("Vanguard Tactical") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))) { Text("Hire Vanguard") }
            }
        }

        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Aegis Elite Command", color = Color.White, fontWeight = FontWeight.Bold)
                Text("$15,000 / week", color = Color.Gray)
                Button(onClick = { viewModel.hireSecurityCompany("Aegis Elite Command") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))) { Text("Hire Aegis") }
            }
        }

        Button(onClick = { viewModel.hireSecurityCompany("") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
            Text("Cancel Security Contract", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Insurance Policies", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Cyber Insurance", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Protects crypto assets", color = Color.Gray, fontSize = 12.sp)
            }
            Switch(checked = state.security.hasCyberInsurance, onCheckedChange = { viewModel.toggleCyberInsurance() })
        }

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Studio Insurance", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Protects equipment", color = Color.Gray, fontSize = 12.sp)
            }
            Switch(checked = state.security.hasStudioInsurance, onCheckedChange = { viewModel.toggleStudioInsurance() })
        }
    }
}

@Composable
fun UnderworldAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F)), contentAlignment = Alignment.Center) {
        Text("Underworld Access Denied", color = Color.Red)
    }
}

@Composable
fun ChronicleAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
        Text("Chronicle", color = Color.White)
    }
}

@Composable
fun CameraAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var showFlash by remember { mutableStateOf(false) }
    var currentMode by remember { mutableStateOf("PHOTO") }
    var isSelfieMode by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (showFlash) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White))
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(100)
                showFlash = false
            }
        }
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.White)
                Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
            }
            Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp).clip(RoundedCornerShape(16.dp)).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                if (isSelfieMode) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
                } else {
                    Icon(Icons.Default.Camera, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
                }
            }
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                    listOf("VIDEO", "PHOTO", "PORTRAIT").forEach { mode ->
                        Text(
                            text = mode,
                            color = if (currentMode == mode) Color.Yellow else Color.White,
                            fontWeight = if (currentMode == mode) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.clickable { currentMode = mode }
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Gray).clickable {})
                    Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White).clickable {
                        showFlash = true
                        val newPhotoUrl = if(isSelfieMode) "https://images.unsplash.com/photo-1544005313-94ddf0286df2?q=80&w=400&auto=format&fit=crop&sig=${System.currentTimeMillis()}" else "https://images.unsplash.com/photo-1520114878144-6123749968dd?q=80&w=400&auto=format&fit=crop&sig=${System.currentTimeMillis()}"
                        viewModel.savePhotoToPhone(newPhotoUrl)
                        viewModel.showToast("Photo saved to gallery!")
                    }, contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(60.dp).clip(CircleShape).border(2.dp, Color.Black, CircleShape))
                    }
                    Icon(Icons.Default.Cameraswitch, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp).clickable { isSelfieMode = !isSelfieMode })
                }
            }
        }
    }
}
