package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MailMessage

/**
 * Renders an authentic contract proposal document matching the 4 official templates:
 * 1. VERTEX MEDIA GROUP (Recorded Music Division — Artist Services Agreement)
 * 2. GRIME CITY RECORDS (STREET CO-SIGNED • NO SOFT HANDS / Artist Deal Memo)
 * 3. TITAN RECORDS (The Standard the Industry Measures Itself Against / Artist Development Contract)
 * 4. INDIE SOUND COLLECTIVE (small label, big ears — est. for artists who do it their way / Artist Agreement)
 */
@Composable
fun ContractProposalView(
    mail: MailMessage,
    playerName: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val template = resolveContractTemplate(mail)
    val durationWeeks = if (mail.contractDurationWeeks > 0) mail.contractDurationWeeks else 52
    val startW = if (mail.startWeek > 0) mail.startWeek else 1
    val startY = if (mail.startYear > 0) mail.startYear else 1
    val endW = if (mail.endWeek > 0) mail.endWeek else (startW + durationWeeks % 52).coerceIn(1, 52)
    val endY = if (mail.endYear > 0) mail.endYear else (startY + durationWeeks / 52)
    val timelineWeeks = (mail.expiresWeek - startW).coerceIn(2, 6)
    val levelText = if (mail.labelLevelText.isNotBlank()) mail.labelLevelText else if (mail.labelLevel >= 3) "major" else "independent"
    val roleText = mail.labelRole ?: "Recording Artist"
    val advanceStr = String.format("%,.0f", mail.cashOffer)
    val percentageStr = "${(mail.labelCut * 100).toInt()}%"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (template) {
            "VERTEX" -> VertexContractDocument(
                mail = mail,
                labelName = mail.sender,
                levelText = levelText,
                playerName = playerName,
                roleText = roleText,
                startW = startW,
                startY = startY,
                endW = endW,
                endY = endY,
                advanceStr = advanceStr,
                percentageStr = percentageStr,
                timelineWeeks = timelineWeeks
            )
            "GRIME_CITY" -> GrimeCityContractDocument(
                mail = mail,
                labelName = mail.sender,
                levelText = levelText,
                playerName = playerName,
                roleText = roleText,
                startW = startW,
                startY = startY,
                endW = endW,
                endY = endY,
                advanceStr = advanceStr,
                percentageStr = percentageStr,
                timelineWeeks = timelineWeeks
            )
            "TITAN" -> TitanContractDocument(
                mail = mail,
                labelName = mail.sender,
                levelText = levelText,
                playerName = playerName,
                roleText = roleText,
                startW = startW,
                startY = startY,
                endW = endW,
                endY = endY,
                advanceStr = advanceStr,
                percentageStr = percentageStr,
                timelineWeeks = timelineWeeks
            )
            else -> IndieSoundContractDocument(
                mail = mail,
                labelName = mail.sender,
                levelText = levelText,
                playerName = playerName,
                roleText = roleText,
                startW = startW,
                startY = startY,
                endW = endW,
                endY = endY,
                advanceStr = advanceStr,
                percentageStr = percentageStr,
                timelineWeeks = timelineWeeks
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contract Sign & Decision Panel
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1E1E26),
            border = BorderStroke(1.dp, Color(0xFF33333F)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (mail.isAccepted) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Accepted",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CONTRACT EXECUTED & ACTIVE",
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Deliverables must be satisfied by Week $endW, Year $endY. View progress in the Company / Management tab.",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                } else {
                    Text(
                        text = "Legal Execution Actions",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Executing this contract awards the upfront \$$advanceStr advance immediately into your balance and commits you to all listed deliverables.",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (template) {
                                    "VERTEX" -> Color(0xFF1565C0)
                                    "GRIME_CITY" -> Color(0xFFFF1744)
                                    "TITAN" -> Color(0xFFFFD700)
                                    else -> Color(0xFF00897B)
                                }
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("contract_accept_btn")
                        ) {
                            Text(
                                text = "SIGN AGREEMENT",
                                color = if (template == "TITAN") Color.Black else Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                        }
                        OutlinedButton(
                            onClick = onDecline,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray),
                            border = BorderStroke(1.dp, Color.Gray),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("contract_decline_btn")
                        ) {
                            Text(
                                text = "DECLINE OFFER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

fun resolveContractTemplate(mail: MailMessage): String {
    if (mail.contractTemplate.isNotBlank() && mail.contractTemplate != "DEFAULT" && mail.contractTemplate != "VERTEX") {
        return mail.contractTemplate
    }
    val sender = mail.sender.lowercase()
    return when {
        sender.contains("grime") || sender.contains("def jam") -> "GRIME_CITY"
        sender.contains("titan") || sender.contains("apex") || sender.contains("sony") -> "TITAN"
        sender.contains("indie") || sender.contains("sub pop") -> "INDIE"
        sender.contains("vertex") -> "VERTEX"
        else -> mail.contractTemplate.ifBlank { "VERTEX" }
    }
}

// =================== 1. VERTEX MEDIA GROUP TEMPLATE ===================
@Composable
private fun VertexContractDocument(
    mail: MailMessage,
    labelName: String,
    levelText: String,
    playerName: String,
    roleText: String,
    startW: Int,
    startY: Int,
    endW: Int,
    endY: Int,
    advanceStr: String,
    percentageStr: String,
    timelineWeeks: Int
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFBFD)),
        border = BorderStroke(1.dp, Color(0xFFCFD8DC)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1B2A))
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Column {
                    Text(
                        text = "VERTEX MEDIA GROUP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Recorded Music Division — Artist Services Agreement",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF90CAF9)
                    )
                }
            }

            // Paper Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "This Artist Services Agreement (“Agreement”) is entered into between $labelName, a $levelText recorded-music entity operating under Vertex Media Group (the “Label”), and $playerName (the “Artist”), to be engaged in the capacity of $roleText.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF263238)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1
                Text(
                    text = "1. Term of Engagement",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D1B2A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This Agreement governs the period commencing Week $startW, Year $startY and concluding Week $endW, Year $endY (the “Contract Term”). Failure to satisfy the deliverables set out in Clause 2 prior to the expiry of the Contract Term shall trigger the penalty provisions herein.",
                    fontSize = 11.5.sp,
                    lineHeight = 17.sp,
                    color = Color(0xFF37474F)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Deliverables Table
                Text(
                    text = "2. Deliverables",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D1B2A)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF90A4AE))
                ) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0D1B2A))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Item",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            text = "Quantity",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    val deliverables = listOf(
                        "Singles" to mail.reqSingles.coerceAtLeast(2),
                        "Albums (LP)" to mail.reqAlbumsLP.coerceAtLeast(if (mail.reqAlbumsEP == 0) 1 else 0),
                        "Album (EP)" to mail.reqAlbumsEP,
                        "MeTube videos" to mail.reqMeTubeVideos.coerceAtLeast(2)
                    )

                    deliverables.forEachIndexed { index, (item, qty) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (index % 2 == 0) Color.White else Color(0xFFECEFF1))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = item,
                                color = Color(0xFF263238),
                                fontSize = 11.sp,
                                modifier = Modifier.weight(2f)
                            )
                            Text(
                                text = qty.toString(),
                                color = Color(0xFF263238),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Advance and Recoupment
                Text(
                    text = "3. Advance and Recoupment",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D1B2A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                val subClauses = listOf(
                    "3.1" to "The Label shall extend to the Artist an advance in the amount of \$$advanceStr (the “Advance”).",
                    "3.2" to "The Advance shall be repaid in full to the Label prior to termination of this Agreement or the Artist's departure from the Label. This obligation survives (a) completion of all deliverables under Clause 2, and (b) the natural expiry of the Contract Term.",
                    "3.3" to "Where any portion of the Advance remains outstanding, the Artist shall not be entitled to terminate this Agreement or depart from the Label other than through formal legal representation, and shall in such event remain personally liable for the outstanding balance together with any fine assessed by the Label.",
                    "3.4" to "Until the Advance is recouped in full, the Label shall deduct $percentageStr from the Artist's tour revenue, merchandise sales, royalty payments, and MeTube income."
                )
                subClauses.forEach { (num, text) ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(
                            text = "$num ",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF263238)
                        )
                        Text(
                            text = text,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            color = Color(0xFF37474F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 4: Offer Validity
                Text(
                    text = "4. Offer Validity",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D1B2A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This offer shall remain open for a period of $timelineWeeks weeks from the date of issue. In the absence of a response within this period, the offer shall be deemed expired.",
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF37474F)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Signature block
                Text(
                    text = "For and on behalf of Vertex Media Group,",
                    fontSize = 11.sp,
                    color = Color(0xFF455A64)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(1.dp)
                        .background(Color(0xFF263238))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = labelName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D1B2A)
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Vertex Media Group | Document generated for internal review purposes",
                    fontSize = 9.sp,
                    color = Color(0xFF90A4AE),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// =================== 2. GRIME CITY RECORDS TEMPLATE ===================
@Composable
private fun GrimeCityContractDocument(
    mail: MailMessage,
    labelName: String,
    levelText: String,
    playerName: String,
    roleText: String,
    startW: Int,
    startY: Int,
    endW: Int,
    endY: Int,
    advanceStr: String,
    percentageStr: String,
    timelineWeeks: Int
) {
    val RedAccent = Color(0xFFFF1744)

    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111114)),
        border = BorderStroke(1.dp, Color(0xFF2C2C34)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF08080A))
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "GRIME CITY RECORDS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "STREET CO-SIGNED • NO SOFT HANDS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RedAccent,
                    letterSpacing = 1.sp
                )
            }

            // Red divider bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(RedAccent)
            )

            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Yo $playerName,",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Word's been going around about you and we don't co-sign just anybody. $labelName is running things as a $levelText label out here, and we're offering you a spot as $roleText. This is real, not a favour — so we need results between Week $startW, Year $startY and Week $endW, Year $endY. Miss the mark and there's a price for that, laid out below.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFFCCCCCC)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: What we need from you
                Text(
                    text = "What we need from you",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = RedAccent
                )
                Spacer(modifier = Modifier.height(6.dp))

                val reqList = listOf(
                    "${mail.reqSingles.coerceAtLeast(2)} singles",
                    "${mail.reqAlbumsLP.coerceAtLeast(if (mail.reqAlbumsEP == 0) 1 else 0)} albums (LP)",
                    "${mail.reqAlbumsEP} album (EP)",
                    "${mail.reqMeTubeVideos.coerceAtLeast(2)} MeTube videos"
                )
                reqList.forEach { req ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(text = "• ", color = RedAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = req, color = Color(0xFFE0E0E0), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: The money
                Text(
                    text = "The money",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = RedAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "We're fronting you \$$advanceStr to get started. That's not a gift — that's owed back to the label, full stop, before you walk or before this contract runs out, even after you've done everything on the list above. Owe us and you don't walk — not without a lawyer, and even then you're covering what's left out your own pocket, plus a fine on top. Until it's settled, we're taking $percentageStr off your tours, merch, royalties, and MeTube money. That's how it works down here.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFFCCCCCC)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Deadline
                Text(
                    text = "Deadline",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = RedAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "You've got $timelineWeeks weeks to respond. No word by then, this offer's dead.",
                    fontSize = 12.sp,
                    color = Color(0xFFCCCCCC)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Stay solid,", fontSize = 12.sp, color = Color(0xFF9E9E9E))
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(1.dp)
                        .background(Color(0xFF616161))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = labelName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "GRIME CITY RECORDS — ARTIST DEAL MEMO",
                    fontSize = 9.sp,
                    color = Color(0xFF616161),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// =================== 3. TITAN RECORDS TEMPLATE ===================
@Composable
private fun TitanContractDocument(
    mail: MailMessage,
    labelName: String,
    levelText: String,
    playerName: String,
    roleText: String,
    startW: Int,
    startY: Int,
    endW: Int,
    endY: Int,
    advanceStr: String,
    percentageStr: String,
    timelineWeeks: Int
) {
    val GoldAccent = Color(0xFFFFD700)
    val SoftGold = Color(0xFFD4AF37)

    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101013)),
        border = BorderStroke(1.dp, SoftGold.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF09090C))
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TITAN RECORDS",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = GoldAccent,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "The Standard the Industry Measures Itself Against",
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFFE0D0A0)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SoftGold.copy(alpha = 0.6f))
            )

            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Dear $playerName,",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "It is with great pleasure that Titan Records extends this formal offer of representation. Your recent achievements have not gone unnoticed by our talent scouts, and we are prepared to welcome you into our roster as $roleText. As $labelName stands among the most prestigious record labels in the country, we hold our artists to a correspondingly exacting standard. This agreement sets forth the obligations you must fulfil between Week $startW, Year $startY and Week $endW, Year $endY. Should these obligations remain unmet upon the contract's expiry, penalties shall apply in accordance with the terms below.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFD6D6D6)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Deliverables
                Text(
                    text = "Deliverables Required of the Artist",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.height(6.dp))

                val deliverables = listOf(
                    "${mail.reqSingles.coerceAtLeast(2)} singles",
                    "${mail.reqAlbumsLP.coerceAtLeast(if (mail.reqAlbumsEP == 0) 1 else 0)} albums (LP)",
                    "${mail.reqAlbumsEP} album (EP)",
                    "${mail.reqMeTubeVideos.coerceAtLeast(2)} MeTube promotional videos"
                )
                deliverables.forEach { item ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(text = "• ", color = GoldAccent, fontSize = 12.sp)
                        Text(text = item, color = Color(0xFFEAEAEA), fontSize = 12.sp, fontFamily = FontFamily.Serif)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Advance & Recoupment
                Text(
                    text = "Advance & Recoupment",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "The Artist shall receive an advance of \$$advanceStr, extended in good faith by the label. This sum constitutes a recoupable advance and must be repaid to Titan Records in full prior to any termination of this agreement or departure from the label — this obligation survives the completion of the deliverables above and the natural expiry of the contract term. Until the advance is fully recouped, the Artist may not terminate this agreement or leave the label; an Artist wishing to exit while a balance remains outstanding may only do so by way of legal counsel, and shall be liable for the remaining balance from personal funds, together with any associated penalty. While under contract, Titan Records shall deduct $percentageStr of the Artist's tour revenue, merchandise sales, royalties, and MeTube income, applied continuously until the advance is recouped in full.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFD6D6D6)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Term
                Text(
                    text = "Term",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "This offer remains open for $timelineWeeks weeks from the date of issue. Should Titan Records receive no response within this window, the offer shall be deemed expired and withdrawn without further notice.",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFD6D6D6)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Yours sincerely,", fontSize = 12.sp, fontFamily = FontFamily.Serif, color = Color(0xFFAAAAAA))
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(1.dp)
                        .background(Color(0xFF757575))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = labelName, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, color = GoldAccent)

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Titan Records — Artist Development Contract — Strictly Confidential",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// =================== 4. INDIE SOUND COLLECTIVE TEMPLATE ===================
@Composable
private fun IndieSoundContractDocument(
    mail: MailMessage,
    labelName: String,
    levelText: String,
    playerName: String,
    roleText: String,
    startW: Int,
    startY: Int,
    endW: Int,
    endY: Int,
    advanceStr: String,
    percentageStr: String,
    timelineWeeks: Int
) {
    val TealAccent = Color(0xFF00897B)
    val CoralAccent = Color(0xFFE76F51)

    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF9F6)),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar with Indie Emblem
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2EFE9))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Overlapping circles brand emblem
                Box(modifier = Modifier.size(36.dp)) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(CoralAccent)
                            .align(Alignment.TopStart)
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(TealAccent.copy(alpha = 0.9f))
                            .align(Alignment.BottomEnd)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Indie Sound Collective",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealAccent
                    )
                    Text(
                        text = "small label, big ears — est. for artists who do it their way",
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF5D6D7E)
                    )
                }
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Hey $playerName,",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We've been following what you've been putting out lately and honestly, we're into it. We'd love to have you join $labelName as $roleText. We're a $levelText label and we're not about to pretend we're bigger than we are — what we do have is a real ear for good music and a team that wants to see you grow. Here's what we're hoping you can put out between Week $startW, Year $startY and Week $endW, Year $endY. If things don't come together by then, there are some penalties written in below, so let's talk if life gets in the way.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF34495E)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: What we're hoping for
                Text(
                    text = "What we're hoping for",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealAccent
                )
                Spacer(modifier = Modifier.height(6.dp))

                val deliverables = listOf(
                    "${mail.reqSingles.coerceAtLeast(2)} singles",
                    "${mail.reqAlbumsLP.coerceAtLeast(if (mail.reqAlbumsEP == 0) 1 else 0)} albums (LP)",
                    "${mail.reqAlbumsEP} album (EP)",
                    "${mail.reqMeTubeVideos.coerceAtLeast(2)} MeTube videos"
                )
                deliverables.forEach { item ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(text = "• ", color = TealAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = item, color = Color(0xFF2C3E50), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: The advance, in plain terms
                Text(
                    text = "The advance, in plain terms",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "You'll get an advance of \$$advanceStr up front. This isn't free money though — it has to be paid back to the label before you leave or the contract ends, even if you've already hit every target above and the term has run out. As long as any of it is still owed, you can't leave the label; the only way out is through a lawyer, and you'd be on the hook to cover whatever's left yourself, plus a fine. Until it's paid off, we'll take $percentageStr off your tour income, merch, royalties, and MeTube earnings.",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFF34495E)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: How long this offer stands
                Text(
                    text = "How long this offer stands",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealAccent
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "This offer's good for $timelineWeeks weeks. If we don't hear back by then, we'll assume it's a no and let it expire.",
                    fontSize = 12.sp,
                    color = Color(0xFF34495E)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Cheers,", fontSize = 12.sp, color = Color(0xFF7F8C8D))
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(1.dp)
                        .background(Color(0xFF95A5A6))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = labelName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Indie Sound Collective · Artist Agreement",
                    fontSize = 9.sp,
                    color = Color(0xFF95A5A6),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
