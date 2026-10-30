package com.example.ui.social

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.ui.ArtistFaceAvatar
import com.example.AvatarWidget
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import com.example.getNpcAvatar
import com.example.NpcAvatarAppearance
import com.example.data.*

import com.example.data.GameStateEntity
import com.example.data.GameViewModel
import com.example.ui.theme.*

fun extractWebsiteLink(content: String): String? {
    val domains = listOf(
        "alphadom.online", "alphadom.com",
        "apexrecord.org", "apexrecords.com",
        "wgt.show", "whoisgottalent.show",
        "sold.com",
        "pulsewire.com", "pulsewire.game",
        "bh.online", "blackhat.online",
        "thegrandawards.org", "grandawards.org",
        "omegaelectoralcommission.gov", "oec.gov",
        "wikipedia.org/wiki/artists", "wikipedia.org",
        "omnisearch.com/market", "omnisearch.com",
        "statecrimecommand.gov"
    )
    for (domain in domains) {
        if (content.contains(domain, ignoreCase = true)) {
            return domain
        }
    }
    val match = Regex("""\b([a-zA-Z0-9-]+\.(?:online|org|com|gov|net|show|io|xyz)(?:/[a-zA-Z0-9_\-\./]*)?)\b""", RegexOption.IGNORE_CASE).find(content)
    return match?.value
}

@Composable
fun CastSocialScreen(state: GameStateEntity, viewModel: GameViewModel, onOpenBrowser: ((String) -> Unit)? = null) {
    var newPostContent by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("For You") }
    var artistSearchQuery by remember { mutableStateOf("") }
    var selectedProfileArtistName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            viewModel.incrementCastTimeSpent(1)
        }
    }

    if (selectedProfileArtistName != null) {
        ArtistProfileScreen(
            artistName = selectedProfileArtistName!!,
            state = state,
            viewModel = viewModel,
            onBack = { selectedProfileArtistName = null },
            onOpenBrowser = onOpenBrowser
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Cast Top Header with Cast Time Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CAST",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif
            )
            val mins = state.castTimeSpentThisWeek / 60
            val secs = state.castTimeSpentThisWeek % 60
            Surface(
                color = Color(0xFF1D9BF0).copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF1D9BF0).copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Timer",
                        tint = Color(0xFF1D9BF0),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Time on Cast: ${String.format("%02d:%02d", mins, secs)}",
                        color = Color(0xFF1D9BF0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Player's Cast Profile & Stats Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp)
                .background(Color(0xFF15181C), RoundedCornerShape(8.dp))
                .border(0.5.dp, Color(0xFF2F3336), RoundedCornerShape(8.dp))
                .clickable { selectedProfileArtistName = state.artistName }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarWidget(
                    baseLook = state.avatarBaseLook,
                    skinTone = state.avatarSkinTone,
                    hairStyleId = state.avatarHairStyleId,
                    clothingId = state.avatarClothingId,
                    jewelryId = state.avatarJewelryId,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = state.artistName,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val mins = state.castTimeSpentThisWeek / 60
                    val secs = state.castTimeSpentThisWeek % 60
                    Text(
                        text = "⏱️ ${String.format("%02d:%02d", mins, secs)} • Tap to view profile",
                        color = Color(0xFF1D9BF0),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val playerPostsCount = state.castPosts.count { it.isByPlayer }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%,d", playerPostsCount),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Posts",
                        color = Color.Gray,
                        fontSize = 8.sp
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%,d", state.followers),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Followers",
                        color = Color.Gray,
                        fontSize = 8.sp
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%,d", state.industryFollowers),
                        color = Color(0xFF1D9BF0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Industry",
                        color = Color.Gray,
                        fontSize = 8.sp
                    )
                }
            }
        }

        // For You / Following Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.5.dp, color = Color(0xFF2F3336)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("For You", "Following").forEach { tab ->
                val isSelected = selectedTab == tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = tab }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.White else Color(0xFF71767B),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF1D9BF0))
                        )
                    }
                }
            }
        }

        if (selectedTab == "For You") {
            // Draft Post Area (Only on For You tab)
            var selectedCategory by remember { mutableStateOf("Music & Art") }
            var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
            var showImagePicker by remember { mutableStateOf(false) }
            val categories = listOf("Music & Art", "Tech & Gadgets", "Fashion & Lifestyle", "Gaming & Entertainment", "Fitness & Wellness", "Business & Finance")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.5.dp, color = Color(0xFF2F3336))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    AvatarWidget(
                        baseLook = state.avatarBaseLook,
                        skinTone = state.avatarSkinTone,
                        hairStyleId = state.avatarHairStyleId,
                        clothingId = state.avatarClothingId,
                        jewelryId = state.avatarJewelryId,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = newPostContent,
                        onValueChange = { newPostContent = it },
                        placeholder = { Text("What's happening?!", color = Color(0xFF71767B), fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 3
                    )
                }

                // Horizontal Category Selection Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSel = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) Color(0xFF1D9BF0) else Color(0xFF1F2225))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) Color.White else Color.LightGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Image attachment preview if selected
                if (selectedImageIndex != null) {
                    val previewRes = com.example.ui.CastMediaRegistry.SCENE_AND_UNNAMED_DRAWABLES.getOrNull(selectedImageIndex!!)
                    if (previewRes != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                painter = painterResource(id = previewRes),
                                contentDescription = "Attached Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { selectedImageIndex = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                    .size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove Photo", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                // Image picker row
                if (showImagePicker) {
                    Text("Select Photo Attachment:", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        com.example.ui.CastMediaRegistry.SCENE_AND_UNNAMED_DRAWABLES.forEachIndexed { idx, dRes ->
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(
                                        width = if (selectedImageIndex == idx) 2.dp else 0.5.dp,
                                        color = if (selectedImageIndex == idx) Color(0xFF1D9BF0) else Color.DarkGray,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        selectedImageIndex = if (selectedImageIndex == idx) null else idx
                                        showImagePicker = false
                                    }
                            ) {
                                Image(
                                    painter = painterResource(id = dRes),
                                    contentDescription = "Photo #$idx",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { showImagePicker = !showImagePicker },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Attach Image",
                                tint = if (selectedImageIndex != null) Color(0xFF1D9BF0) else Color(0xFF71767B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Cost: 3 Energy",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = {
                            if (newPostContent.isNotBlank()) {
                                val imgResStr = selectedImageIndex?.let { "img_media_$it" }
                                viewModel.createCastPost(newPostContent.trim(), selectedCategory, imgResStr)
                                newPostContent = ""
                                selectedImageIndex = null
                                showImagePicker = false
                            }
                        },
                        enabled = newPostContent.isNotBlank() && state.energy >= 3,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1D9BF0),
                            disabledContainerColor = Color(0xFF1D9BF0).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                    ) {
                        Text("Post", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // For You Feed: All posts stay permanently intact on the feed
            val forYouPosts = state.castPosts

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (forYouPosts.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 50.dp, start = 20.dp, end = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Welcome to CAST!", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Your feed displays your lifetime updates and posts from artists you follow. Share an update above or visit the Following tab to follow other artists!",
                                color = Color(0xFF71767B),
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(forYouPosts, key = { it.id }) { post ->
                        CastPostCard(
                            post = post,
                            state = state,
                            viewModel = viewModel,
                            onOpenBrowser = onOpenBrowser,
                            onOpenProfile = { selectedProfileArtistName = it }
                        )
                    }
                }
            }
        } else {
            // Following Tab: Directory to search and follow/unfollow artists
            val filteredNpcs = remember(state.npcs, artistSearchQuery) {
                if (artistSearchQuery.isBlank()) state.npcs
                else state.npcs.filter { it.name.contains(artistSearchQuery, ignoreCase = true) || it.genrePreference.contains(artistSearchQuery, ignoreCase = true) }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Header & Search
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Artist Directory",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1D9BF0).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${state.npcs.count { it.isFollowedByPlayer }} Following / ${state.npcs.size} Total",
                                color = Color(0xFF1D9BF0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    TextField(
                        value = artistSearchQuery,
                        onValueChange = { artistSearchQuery = it },
                        placeholder = { Text("Search artists or genres...", color = Color(0xFF71767B), fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF15181C),
                            unfocusedContainerColor = Color(0xFF15181C),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(20.dp),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        },
                        singleLine = true
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredNpcs) { npc ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF15181C)),
                            border = BorderStroke(0.5.dp, Color(0xFF2F3336)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedProfileArtistName = npc.name }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ArtistFaceAvatar(
                                        artistIdentifier = npc.id,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = npc.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(text = "${npc.genrePreference} • ${String.format("%,d", npc.fans)} fans", color = Color.Gray, fontSize = 10.sp)
                                        Text(text = "Affinity: ${npc.relationshipWithPlayer.toInt()}%", color = Color(0xFF1D9BF0), fontSize = 10.sp)
                                    }
                                }
                                Button(
                                    onClick = { viewModel.toggleFollowNpcArtist(npc.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (npc.isFollowedByPlayer) Color(0xFF2C2C2C) else Color(0xFF1D9BF0)
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Text(
                                        text = if (npc.isFollowedByPlayer) "Following" else "Follow",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CastPostCard(
    post: CastPost,
    state: GameStateEntity,
    viewModel: GameViewModel,
    onOpenBrowser: ((String) -> Unit)? = null,
    onOpenProfile: ((String) -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(width = 0.3.dp, color = Color(0xFF2F3336)),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = if (onOpenProfile != null) Modifier.clickable { onOpenProfile(post.authorName) } else Modifier
                ) {
                    ArtistFaceAvatar(
                        artistIdentifier = if (post.isByPlayer) state.artistName else post.authorName,
                        isPlayer = post.isByPlayer,
                        playerState = state,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF1D9BF0),
                                modifier = Modifier.size(13.dp)
                            )
                            if (state.npcs.any { it.name.equals(post.authorName, ignoreCase = true) && it.isFollowedByPlayer }) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF1D9BF0).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("Following", color = Color(0xFF1D9BF0), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorHandle,
                                color = Color(0xFF71767B),
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• Y${post.year} W${post.week}",
                                color = Color.DarkGray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                if (post.isByPlayer) {
                    IconButton(
                        onClick = { viewModel.deleteCastPost(post.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete Post",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.content,
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 22.sp
            )

            val embeddedLink = remember(post.content) { extractWebsiteLink(post.content) }
            if (embeddedLink != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF16181C),
                    border = BorderStroke(1.dp, Color(0xFF2F3336)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onOpenBrowser?.invoke(embeddedLink)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Visit Website",
                            tint = Color(0xFF1D9BF0),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = embeddedLink,
                            color = Color(0xFF1D9BF0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open",
                            tint = Color(0xFF71767B),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            val postMediaDrawable = remember(post.id, post.imageRes, post.content) {
                com.example.ui.CastMediaRegistry.getDrawableForPost(post.imageRes, post.id, post.content)
            }
            if (postMediaDrawable != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = postMediaDrawable),
                    contentDescription = "Post Media Attachment",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp, max = 220.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(0.5.dp, Color(0xFF2F3336), RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reply / Comments count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Comments", tint = Color(0xFF71767B), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${post.commentsCount}", color = Color(0xFF71767B), fontSize = 11.sp)
                }
                // Reposts
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Repeat, contentDescription = "Reposts", tint = Color(0xFF71767B), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${post.reposts}", color = Color(0xFF71767B), fontSize = 11.sp)
                }
                // Likes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.likeCastPost(post.id) }
                ) {
                    Icon(
                        imageVector = if (post.isLikedByPlayer) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Likes",
                        tint = if (post.isLikedByPlayer) Color(0xFFF4212E) else Color(0xFF71767B),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.likes}",
                        color = if (post.isLikedByPlayer) Color(0xFFF4212E) else Color(0xFF71767B),
                        fontSize = 11.sp
                    )
                }
                // Share icon placeholder
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF71767B), modifier = Modifier.size(15.dp))
            }
        }
    }
}

@Composable
fun ArtistProfileScreen(
    artistName: String,
    state: GameStateEntity,
    viewModel: GameViewModel,
    onBack: () -> Unit,
    onOpenBrowser: ((String) -> Unit)? = null
) {
    val isPlayer = artistName.equals(state.artistName, ignoreCase = true)
    val npc = remember(state.npcs, artistName) {
        state.npcs.find { it.name.equals(artistName, ignoreCase = true) }
    }
    val artistPosts = remember(state.castPosts, artistName, isPlayer) {
        state.castPosts.filter { post ->
            if (isPlayer) post.isByPlayer
            else post.authorName.equals(artistName, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isPlayer) state.artistName else (npc?.name ?: artistName),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = Color(0xFF1D9BF0),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = "${artistPosts.size} posts",
                    color = Color(0xFF71767B),
                    fontSize = 11.sp
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                // Header banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1D9BF0).copy(alpha = 0.6f), Color(0xFF0F1419))
                            )
                        )
                )

                // Avatar and Follow row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-36).dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Surface(
                            shape = CircleShape,
                            border = BorderStroke(3.dp, Color.Black),
                            color = Color(0xFF16181C)
                        ) {
                            if (isPlayer) {
                                AvatarWidget(
                                    baseLook = state.avatarBaseLook,
                                    skinTone = state.avatarSkinTone,
                                    hairStyleId = state.avatarHairStyleId,
                                    clothingId = state.avatarClothingId,
                                    jewelryId = state.avatarJewelryId,
                                    modifier = Modifier.size(72.dp)
                                )
                            } else {
                                ArtistFaceAvatar(
                                    artistIdentifier = npc?.id ?: artistName,
                                    modifier = Modifier.size(72.dp)
                                )
                            }
                        }

                        if (!isPlayer && npc != null) {
                            Button(
                                onClick = { viewModel.toggleFollowNpcArtist(npc.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (npc.isFollowedByPlayer) Color(0xFF2C2C2C) else Color(0xFF1D9BF0)
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    text = if (npc.isFollowedByPlayer) "Following" else "Follow",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Profile Info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-24).dp)
                ) {
                    Text(
                        text = if (isPlayer) state.artistName else (npc?.name ?: artistName),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (isPlayer) "@${state.artistName.lowercase().replace(" ", "")}" else "@${artistName.lowercase().replace(" ", "")}",
                        color = Color(0xFF71767B),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    val bioText = if (isPlayer) {
                        "⭐ Popularity: ${state.popularity.toInt()}% • Voice: ${state.voiceSkill.toInt()}% • Net Worth: $${String.format("%,d", state.cash.toLong())}"
                    } else if (npc != null) {
                        "🎤 ${npc.genrePreference} Artist • Affinity: ${npc.relationshipWithPlayer.toInt()}% • Music industry talent on CAST."
                    } else {
                        "Verified creator & artist on CAST."
                    }
                    Text(
                        text = bioText,
                        color = Color(0xFFE7E9EA),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        val fans = if (isPlayer) state.fans else (npc?.fans ?: 25000)
                        Row {
                            Text(text = String.format("%,d", fans), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Fans", color = Color(0xFF71767B), fontSize = 12.sp)
                        }
                        Row {
                            Text(text = "${artistPosts.size}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Posts", color = Color(0xFF71767B), fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    // Tab line
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Posts",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    HorizontalDivider(color = Color(0xFF2F3336), thickness = 0.5.dp)
                }
            }

            if (artistPosts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No posts yet from this artist.",
                            color = Color(0xFF71767B),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(artistPosts, key = { it.id }) { post ->
                    CastPostCard(
                        post = post,
                        state = state,
                        viewModel = viewModel,
                        onOpenBrowser = onOpenBrowser,
                        onOpenProfile = null
                    )
                }
            }
        }
    }
}
