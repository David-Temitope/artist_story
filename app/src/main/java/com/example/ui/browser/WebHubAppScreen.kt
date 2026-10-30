package com.example.ui.browser

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.AvatarWidget
import androidx.compose.ui.platform.testTag
import com.example.getNpcAvatar
import com.example.NpcAvatarAppearance
import com.example.data.*
import com.example.ui.AlbumCoverRegistry
import com.example.ui.SongAlbumCover
import com.example.ui.ReliableAsyncImage

import com.example.data.GameStateEntity
import com.example.data.GameViewModel
import com.example.ui.theme.*

fun getPoliticianDrawable(idOrName: String): Int {
    val lower = idOrName.lowercase()
    return when {
        lower.contains("vance") -> R.drawable.img_artist_senator_vance
        lower.contains("sterling") -> R.drawable.img_artist_governor_sterling
        lower.contains("thorne") -> R.drawable.img_artist_matt_thorne
        lower.contains("rivers") -> R.drawable.img_artist_sam_rivers
        lower.contains("sparks") -> R.drawable.img_artist_ryan_sparks
        lower.contains("woods") -> R.drawable.img_artist_ian_woods
        else -> {
            val list = listOf(
                R.drawable.img_artist_senator_vance,
                R.drawable.img_artist_governor_sterling,
                R.drawable.img_artist_matt_thorne,
                R.drawable.img_artist_sam_rivers
            )
            list[kotlin.math.abs(idOrName.hashCode()) % list.size]
        }
    }
}

fun getArtistPhotoDrawable(nameOrId: String): Int {
    val lower = nameOrId.lowercase()
    return when {
        lower.contains("aria") -> R.drawable.img_artist_aria_nova
        lower.contains("pulse") -> R.drawable.img_artist_dj_pulse
        lower.contains("vince") -> R.drawable.img_artist_vince
        lower.contains("seraphina") -> R.drawable.img_artist_seraphina
        lower.contains("chloe") -> R.drawable.img_artist_chloe_bloom
        lower.contains("cyrus") || lower.contains("king") -> R.drawable.img_artist_cyrus_king
        lower.contains("drake") -> R.drawable.img_artist_drake_drake
        lower.contains("eminem") -> R.drawable.img_artist_eminem_y
        lower.contains("busta") -> R.drawable.img_artist_busta_rhymes
        lower.contains("travis") || lower.contains("scott") -> R.drawable.img_artist_travis_scottie
        lower.contains("kanye") -> R.drawable.img_artist_kanye_east
        lower.contains("gogo") || lower.contains("lady") -> R.drawable.img_artist_lady_gogo
        lower.contains("shadow") -> R.drawable.img_artist_shadow_lyricist
        lower.contains("nova") -> R.drawable.img_artist_nova_vibe
        lower.contains("post") -> R.drawable.img_artist_post_malone_y
        lower.contains("marley") || lower.contains("bob") -> R.drawable.img_artist_bob_marley_esque
        lower.contains("amadeus") -> R.drawable.img_artist_amadeus_neo
        lower.contains("ademola") -> R.drawable.img_artist_ademola
        lower.contains("jax") -> R.drawable.img_artist_jax_riff
        lower.contains("laroi") -> R.drawable.img_artist_kid_laroi_esque
        lower.contains("justin") -> R.drawable.img_artist_justin_blaze
        lower.contains("billie") -> R.drawable.img_artist_billie_eyelash
        lower.contains("abby") -> R.drawable.img_artist_abby_lane
        lower.contains("elysian") -> R.drawable.img_artist_elysian_sky
        else -> {
            val list = listOf(
                R.drawable.img_artist_seraphina,
                R.drawable.img_artist_vince,
                R.drawable.img_artist_mia_flow,
                R.drawable.img_artist_chloe_bloom,
                R.drawable.img_artist_aria_nova,
                R.drawable.img_artist_dj_pulse,
                R.drawable.img_artist_shadow_lyricist,
                R.drawable.img_artist_nova_vibe,
                R.drawable.img_artist_drake_drake,
                R.drawable.img_artist_cyrus_king
            )
            list[kotlin.math.abs(nameOrId.hashCode()) % list.size]
        }
    }
}

fun getProductDrawable(itemId: String, category: String): Int {
    return when {
        category.contains("Synth", ignoreCase = true) -> R.drawable.cast_studio_synth_1788205895230
        category.contains("Guitar", ignoreCase = true) -> R.drawable.cast_acoustic_room_1788205880418
        category.contains("Studio", ignoreCase = true) || category.contains("Microphone", ignoreCase = true) -> R.drawable.cast_radio_studio_1788205951087
        category.contains("Luxury", ignoreCase = true) -> R.drawable.cast_street_luxury_1788205929331
        else -> AlbumCoverRegistry.ALL_COVER_DRAWABLES[kotlin.math.abs(itemId.hashCode()) % AlbumCoverRegistry.ALL_COVER_DRAWABLES.size]
    }
}

@Composable
fun WebHubAppScreen(state: GameStateEntity, viewModel: GameViewModel, initialUrl: String? = null) {
    val startUrl = initialUrl ?: "webhub.game/search"
    var currentUrl by remember(initialUrl) { mutableStateOf(startUrl) }
    val urlHistory = remember(initialUrl) { mutableStateListOf(startUrl) }
    var historyIndex by remember(initialUrl) { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") } // "All", "Music", "Commerce", "Gov"
    var showMenu by remember { mutableStateOf(false) }

    fun navigateTo(url: String) {
        while (urlHistory.size > historyIndex + 1) {
            urlHistory.removeAt(urlHistory.size - 1)
        }
        urlHistory.add(url)
        historyIndex = urlHistory.size - 1
        currentUrl = url
    }

    fun goBack() {
        if (historyIndex > 0) {
            historyIndex--
            currentUrl = urlHistory[historyIndex]
        } else if (currentUrl != "webhub.game/search") {
            currentUrl = "webhub.game/search"
        }
    }

    fun goForward() {
        if (historyIndex < urlHistory.size - 1) {
            historyIndex++
            currentUrl = urlHistory[historyIndex]
        }
    }

    fun goHome() {
        navigateTo("webhub.game/search")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F15))
    ) {
        // 1. Top Browser URL & Navigation Bar (Matches uploaded design)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F1720))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Emerald Green Back Button
            IconButton(
                onClick = { goBack() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(22.dp)
                )
            }

            // URL Search Capsule
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .background(Color(0xFF1E2732), RoundedCornerShape(20.dp))
                    .border(0.5.dp, Color(0xFF2F3C4C), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    if (currentUrl == "webhub.game/search") {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    text = "webhub.game/search?q=...",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = currentUrl,
                            color = Color.White,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Emerald Green 3-Dots Menu
            Box {
                IconButton(
                    onClick = { showMenu = !showMenu },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(22.dp)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(0xFF1E2732))
                ) {
                    DropdownMenuItem(
                        text = { Text("New Search", color = Color.White, fontSize = 12.sp) },
                        onClick = {
                            showMenu = false
                            goHome()
                        },
                        leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Refresh Page", color = Color.White, fontSize = 12.sp) },
                        onClick = {
                            showMenu = false
                            viewModel.showToast("Page reloaded")
                        },
                        leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Clear History", color = Color.White, fontSize = 12.sp) },
                        onClick = {
                            showMenu = false
                            urlHistory.clear()
                            urlHistory.add("webhub.game/search")
                            historyIndex = 0
                            currentUrl = "webhub.game/search"
                            viewModel.showToast("History cleared")
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp)) }
                    )
                }
            }
        }

        // 2. Main Body Content (Search Results Feed OR Target Website)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentUrl) {
                "wgt.show", "www.whoisgottalent.show", "whoisgottalent.show" -> {
                    if (state.isUnderArrest) {
                        com.example.ui.StateCustodyLockoutScreen(
                            state = state,
                            viewModel = viewModel,
                            featureName = "World's Got Talent Television Broadcast",
                            onOpenScc = { currentUrl = "statecrimecommand.gov" }
                        )
                    } else {
                        WgtWebsitePage(state = state, viewModel = viewModel)
                    }
                }
                "apexrecord.org", "www.apexrecord.org", "apexrecords.com" -> {
                    ApexRecordsWebsitePage(state = state, viewModel = viewModel)
                }
                "alphadom.online", "www.alphadom.online", "alphadom.com" -> {
                    AlphadomStoreWebsitePage(state = state, viewModel = viewModel)
                }
                "omegaelectoralcommission.gov", "www.omegaelectoralcommission.gov", "oec.gov" -> {
                    OecPortalWebsitePage(state = state, viewModel = viewModel)
                }
                "pulsewire.com", "www.pulsewire.com", "pulsewire.game" -> {
                    PulsewireNewsWebsitePage(state = state, viewModel = viewModel)
                }
                "bh.online", "www.bh.online", "blackhat.online" -> {
                    BlackHatWebsitePage(state = state, viewModel = viewModel)
                }
                "grandawards.org", "thegrandawards.org", "www.grandawards.org" -> {
                    GrandAwardsWebsitePage(state = state, viewModel = viewModel)
                }
                "sold.com", "www.sold.com" -> {
                    SoldRealEstateWebsitePage(state = state, viewModel = viewModel)
                }
                "statecrimecommand.gov", "www.statecrimecommand.gov", "scc.gov" -> {
                    StateCrimeCommandWebsitePage(state = state, viewModel = viewModel)
                }
                "omnisearch.com/market", "finance.omnisearch.com" -> {
                    MarketIntelligenceWebsitePage(state = state, viewModel = viewModel)
                }
                "wikisinga.org", "www.wikisinga.org", "wikisinga.org/wiki/artists", "wikisinga.org/wiki", "wikisinga.com", "wikipedia.org/wiki/artists", "wikipedia.org" -> {
                    WikisingaArtistsWebsitePage(state = state, viewModel = viewModel)
                }
                else -> {
                    WebHubSearchFeed(
                        state = state,
                        viewModel = viewModel,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onCategorySelect = { selectedCategory = it },
                        onNavigate = { navigateTo(it) }
                    )
                }
            }
        }

        // 3. Bottom Browser Toolbar (Matches uploaded design)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F1720))
                .border(0.5.dp, Color(0xFF1E2732))
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { goBack() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Back",
                    tint = if (historyIndex > 0 || currentUrl != "webhub.game/search") Color.LightGray else Color.DarkGray,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = { goForward() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Forward",
                    tint = if (historyIndex < urlHistory.size - 1) Color.LightGray else Color.DarkGray,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = { viewModel.showToast("Page reloaded") },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(
                onClick = { goHome() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (currentUrl == "webhub.game/search") Color(0xFF00E676) else Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
            // Green Tabs Container
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF00E676), RoundedCornerShape(8.dp))
                    .clickable {
                        viewModel.showToast("WebHub Active Sessions: ${urlHistory.size}")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FilterNone,
                    contentDescription = "Tabs",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun WebHubSearchFeed(
    state: GameStateEntity,
    viewModel: GameViewModel,
    searchQuery: String,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Category Pills (All, Music, Commerce, Civic, News, Underground)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categories = listOf(
                Pair("🌐 All", "All"),
                Pair("🎵 Music", "Music"),
                Pair("🛒 Commerce", "Commerce"),
                Pair("🏛️ Civic", "Civic"),
                Pair("⚡ News", "News"),
                Pair("🕵️ Underground", "Underground")
            )
            categories.forEach { (label, key) ->
                val isSelected = selectedCategory == key
                Surface(
                    onClick = { onCategorySelect(key) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFF161E28),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) Color(0xFF00E676) else Color(0xFF263242)
                    )
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color(0xFF00E676) else Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Search Results List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // 1. WGT Official
            if (selectedCategory == "All" || selectedCategory == "Music") {
                if (searchQuery.isBlank() || "who is got talent wgt audition show".contains(searchQuery, ignoreCase = true)) {
                    item {
                        SearchResultCard(
                            iconBg = Color(0xFFFFA000),
                            icon = Icons.Default.Star,
                            siteName = "WGT Official",
                            url = "wgt.show",
                            title = "Who Is Got Talent - The Grand Auditions",
                            description = "Official registration and screening portal for the bi-annual televised talent competition. Compete for $30,000 and major industry acclaim.",
                            subLinks = listOf(
                                Pair("Auditions", "wgt.show"),
                                Pair("Leaderboard", "wgt.show"),
                                Pair("Judges Panel", "wgt.show")
                            ),
                            onClick = { onNavigate("wgt.show") }
                        )
                    }
                }
            }

            // 2. Apex Records (Image 1)
            if (selectedCategory == "All" || selectedCategory == "Music") {
                if (searchQuery.isBlank() || "apex records demo label contract sign artist".contains(searchQuery, ignoreCase = true)) {
                    item {
                        SearchResultCard(
                            iconBg = Color(0xFF8B5CF6),
                            icon = Icons.Default.Album,
                            siteName = "Apex Records",
                            url = "apexrecord.org",
                            title = "Apex Records - Your Sound. Our Platform.",
                            description = "Premier record label and artist development house. Submit demos directly to the A&R Committee, secure signing advances, and access global SoundStream distribution.",
                            subLinks = listOf(
                                Pair("Submit Demo", "apexrecord.org"),
                                Pair("Roster", "apexrecord.org"),
                                Pair("A&R Portal", "apexrecord.org")
                            ),
                            onClick = { onNavigate("apexrecord.org") }
                        )
                    }
                }
            }

            // 3. Alphadom Audio & Luxury (Image 2)
            if (selectedCategory == "All" || selectedCategory == "Commerce") {
                if (searchQuery.isBlank() || "alphadom instruments synth gear audio luxury shop store".contains(searchQuery, ignoreCase = true)) {
                    item {
                        SearchResultCard(
                            iconBg = Color(0xFFD4AF37),
                            icon = Icons.Default.ShoppingBag,
                            siteName = "Alphadom",
                            url = "alphadom.online",
                            title = "Alphadom - Engineered For Sound | Luxury Gear & Instruments",
                            description = "Curated collection of professional polyphonic synthesizers, vintage guitars, Neve preamps, Neumann microphones, and luxury timepieces.",
                            subLinks = listOf(
                                Pair("Synthesizers", "alphadom.online"),
                                Pair("Studio Gear", "alphadom.online"),
                                Pair("Prestige", "alphadom.online")
                            ),
                            onClick = { onNavigate("alphadom.online") }
                        )
                    }
                }
            }

            // 4. Sold.com Real Estate & Asset Auctions (Image 7)
            if (selectedCategory == "All" || selectedCategory == "Commerce") {
                if (searchQuery.isBlank() || "sold real estate cars property penthouse villa auction".contains(searchQuery, ignoreCase = true)) {
                    item {
                        SearchResultCard(
                            iconBg = Color(0xFF3B82F6),
                            icon = Icons.Default.Home,
                            siteName = "Sold.com",
                            url = "sold.com",
                            title = "Sold.com - Discover Extraordinary Real Estate & Assets",
                            description = "Exclusive access to off-market luxury penthouses, oceanfront villas, recording studio compounds, exotic hypercars, and corporate label acquisitions.",
                            subLinks = listOf(
                                Pair("Real Estate", "sold.com"),
                                Pair("Hypercars", "sold.com"),
                                Pair("Auctions", "sold.com")
                            ),
                            onClick = { onNavigate("sold.com") }
                        )
                    }
                }
            }

            // 5. Omega Electoral Commission (Image 3)
            if (selectedCategory == "All" || selectedCategory == "Civic") {
                if (searchQuery.isBlank() || "omega electoral commission oec election vote mayor politician".contains(searchQuery, ignoreCase = true)) {
                    item {
                        SearchResultCard(
                            iconBg = Color(0xFF1D4ED8),
                            icon = Icons.Default.HowToVote,
                            siteName = "Omega Electoral Commission",
                            url = "omegaelectoralcommission.gov",
                            title = "Omega Electoral Commission - Mayoral Elections 2024",
                            description = "Official municipal civic voting portal. Review certified candidate agendas on creative arts funding, cast official ballots, and monitor live district reporting.",
                            subLinks = listOf(
                                Pair("Cast Ballot", "omegaelectoralcommission.gov"),
                                Pair("Candidates", "omegaelectoralcommission.gov"),
                                Pair("Results", "omegaelectoralcommission.gov")
                            ),
                            onClick = { onNavigate("omegaelectoralcommission.gov") }
                        )
                    }
                }
            }

            // 6. Pulsewire News (Image 4)
            if (selectedCategory == "All" || selectedCategory == "News" || selectedCategory == "Music") {
                if (searchQuery.isBlank() || "pulsewire news journalism music gossip trending leaks".contains(searchQuery, ignoreCase = true)) {
                    item {
                        SearchResultCard(
                            iconBg = Color(0xFF00E5FF),
                            icon = Icons.Default.FlashOn,
                            siteName = "Pulsewire News",
                            url = "pulsewire.com",
                            title = "Pulsewire - Breaking Music Journalism & Industry Intel",
                            description = "High-voltage music journalism, WGT tournament breakdowns, producer leaks, gear reviews, and real-time artist chart clashes.",
                            subLinks = listOf(
                                Pair("WGT Coverage", "pulsewire.com"),
                                Pair("Gear Tech", "pulsewire.com"),
                                Pair("Trending", "pulsewire.com")
                            ),
                            onClick = { onNavigate("pulsewire.com") }
                        )
                    }
                }
            }

            // 7. Black Hat Underground (Image 5)
            if (selectedCategory == "All" || selectedCategory == "Underground" || selectedCategory == "Commerce") {
                if (searchQuery.isBlank() || "black hat darknet bh online streams botnet votes hacker".contains(searchQuery, ignoreCase = true)) {
                    item {
                        SearchResultCard(
                            iconBg = Color(0xFF00FF66),
                            icon = Icons.Default.Terminal,
                            siteName = "bh.online",
                            url = "bh.online",
                            title = "BH.ONLINE // Encrypted Escrow & Darknet Market",
                            description = "Decentralized P2P marketplace. Cast botnet followers, instant stream injectors, civic proxy vote bundles, and unmarked high-end studio hardware.",
                            subLinks = listOf(
                                Pair("Botnets", "bh.online"),
                                Pair("Stream Packs", "bh.online"),
                                Pair("P2P Escrow", "bh.online")
                            ),
                            onClick = { onNavigate("bh.online") }
                        )
                    }
                }
            }

            // 8. The Grand Awards (Image 6)
            if (selectedCategory == "All" || selectedCategory == "Music") {
                if (searchQuery.isBlank() || "the grand awards gala laureate ceremony trophy honor".contains(searchQuery, ignoreCase = true)) {
                    item {
                        SearchResultCard(
                            iconBg = Color(0xFFE5C07B),
                            icon = Icons.Default.EmojiEvents,
                            siteName = "The Grand Awards",
                            url = "thegrandawards.org",
                            title = "The Grand Awards - The Pinnacle of Achievement",
                            description = "Celebrating visionary artistry, unparalleled creativity, and defining moments of the global music industry in an evening of uncompromising elegance.",
                            subLinks = listOf(
                                Pair("Laureates", "thegrandawards.org"),
                                Pair("VIP Gala", "thegrandawards.org"),
                                Pair("Categories", "thegrandawards.org")
                            ),
                            onClick = { onNavigate("thegrandawards.org") }
                        )
                    }
                }
            }

            // 9. State Crime Command (Image 8)
            if (selectedCategory == "All" || selectedCategory == "Civic" || selectedCategory == "Underground") {
                if (searchQuery.isBlank() || "state crime command scc wanted police fine warrants cyber".contains(searchQuery, ignoreCase = true)) {
                    item {
                        SearchResultCard(
                            iconBg = Color(0xFFEF4444),
                            icon = Icons.Default.Security,
                            siteName = "State Crime Command",
                            url = "statecrimecommand.gov",
                            title = "State Crime Command // Federal Taskforce & Cyber Most Wanted",
                            description = "Official security agency registry. Tracks active criminal suspects, sector surveillance radar, darknet syndicates, and civic clearance settlement processing.",
                            subLinks = listOf(
                                Pair("Most Wanted", "statecrimecommand.gov"),
                                Pair("Cyber Map", "statecrimecommand.gov"),
                                Pair("Pay Settlement", "statecrimecommand.gov")
                            ),
                            onClick = { onNavigate("statecrimecommand.gov") }
                        )
                    }
                }
            }

            // 10. Wikisinga Artists Registry
            if (selectedCategory == "All") {
                if (searchQuery.isBlank() || "wikisinga wikipedia artists encyclopedia biography".contains(searchQuery, ignoreCase = true)) {
                    item {
                        SearchResultCard(
                            iconBg = Color(0xFF1E293B),
                            icon = Icons.Default.MenuBook,
                            siteName = "Wikisinga",
                            url = "wikisinga.org/wiki/artists",
                            title = "Wikisinga: The Living Encyclopedia of Music Artists & Cultural Icons",
                            description = "Open encyclopedic records of living musical artists, discographies, historical career milestones, fan metrics, and industry acclaim index.",
                            subLinks = listOf(
                                Pair("Top Artists", "wikisinga.org/wiki/artists"),
                                Pair("Discography", "wikisinga.org/wiki/artists")
                            ),
                            onClick = { onNavigate("wikisinga.org/wiki/artists") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    iconBg: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    siteName: String,
    url: String,
    title: String,
    description: String,
    subLinks: List<Pair<String, String>>,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
            Column {
                Text(
                    text = siteName,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$url  ⋮",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            color = Color(0xFF5B98FF),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            lineHeight = 15.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            subLinks.forEach { (name, linkUrl) ->
                Text(
                    text = name,
                    color = Color(0xFF00E676),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onClick() }
                )
            }
        }
    }
}

// =================== 1. WHO IS GOT TALENT (WGT) WEBSITE ===================
@Composable
fun WgtWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedWgtNav by remember { mutableStateOf("HOME") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0D14))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Top WGT Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF131722))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFFFFA000), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "WGT Logo",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "WGT",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("HOME", "AUDITIONS", "MOVIES", "VOTING", "NEWS").forEach { nav ->
                    Text(
                        text = nav,
                        color = if (selectedWgtNav == nav) Color(0xFFFFA000) else Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = if (selectedWgtNav == nav) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.clickable { selectedWgtNav = nav }
                    )
                }
            }
        }

        // 2. High-Impact Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF20163B),
                            Color(0xFF0B0D14)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFFFFA000).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFA000).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "✨ THE NATION'S PREMIERE TALENT SHOWCASE",
                        color = Color(0xFFFFA000),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "THE STAGE IS SET.\nYOUR MOMENT AWAITS.",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 24.sp
                )

                Text(
                    text = "Join thousands of visionary performers competing for national acclaim, a $30,000 recording contract prize, and millions of global fans.",
                    color = Color(0xFFB0B7C3),
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (state.wgtApplied) {
                    val audYear = if (state.wgtAuditionYear > 0) state.wgtAuditionYear else if (state.wgtSeasonYear > 0) state.wgtSeasonYear else state.currentYear
                    val audWeek = if (state.wgtAuditionWeek > 0) state.wgtAuditionWeek else 8
                    val semiWeek = if (state.wgtSemiFinalsWeek > 0) state.wgtSemiFinalsWeek else audWeek + 4
                    val finaleWeek = if (state.wgtGrandFinaleWeek > 0) state.wgtGrandFinaleWeek else semiWeek + 4

                    val isAuditionLive = (state.wgtRegistrationStatus in listOf("REGISTERED_SELECTED", "REGISTERED_ROLLED_OVER")) &&
                        ((state.currentWeek == audWeek || (state.currentWeek >= audWeek && state.currentWeek < semiWeek)) && (state.currentYear == audYear || audYear == 0))

                    val isSemiFinalsLive = (state.wgtRegistrationStatus == "ADVANCED_SEMI_FINALS") &&
                        ((state.currentWeek == semiWeek || (state.currentWeek >= semiWeek && state.currentWeek < finaleWeek)) && (state.currentYear == state.wgtSeasonYear || state.wgtSeasonYear == 0))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Surface(
                            color = when {
                                isAuditionLive || isSemiFinalsLive -> Color(0xFFF59E0B).copy(alpha = 0.25f)
                                state.wgtRegistrationStatus in listOf("WINNER", "RUNNER_UP", "ADVANCED_FINALE") -> Color(0xFF10B981).copy(alpha = 0.25f)
                                else -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, when {
                                isAuditionLive || isSemiFinalsLive -> Color(0xFFF59E0B)
                                state.wgtRegistrationStatus in listOf("WINNER", "RUNNER_UP", "ADVANCED_FINALE") -> Color(0xFF10B981)
                                else -> Color(0xFF3B82F6)
                            })
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAuditionLive || isSemiFinalsLive) Icons.Default.Star else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isAuditionLive || isSemiFinalsLive) Color(0xFFF59E0B) else Color(0xFF10B981),
                                    modifier = Modifier.size(16.dp)
                                )
                                val statusText = when {
                                    isAuditionLive -> "🎙️ YOUR AUDITION SLOT IS LIVE THIS WEEK!"
                                    isSemiFinalsLive -> "🌟 NATIONAL SEMI-FINALS BROADCAST IS LIVE THIS WEEK!"
                                    state.wgtRegistrationStatus == "REGISTERED_ROLLED_OVER" -> "ROLLED-OVER TICKET CONFIRMED: WEEK $audWeek, YEAR $audYear"
                                    state.wgtRegistrationStatus == "REGISTERED_SELECTED" -> "AUDITION SCHEDULED: WEEK $audWeek, YEAR $audYear"
                                    state.wgtRegistrationStatus == "ADVANCED_SEMI_FINALS" -> "SEMI-FINALS SCHEDULED: WEEK $semiWeek, YEAR ${state.wgtSeasonYear}"
                                    state.wgtRegistrationStatus == "ADVANCED_FINALE" -> "GRAND FINALE SCHEDULED: WEEK $finaleWeek, YEAR ${state.wgtSeasonYear}"
                                    state.wgtRegistrationStatus == "WINNER" -> "🏆 REIGNING WHO IS GOT TALENT CHAMPION"
                                    state.wgtRegistrationStatus == "RUNNER_UP" -> "🥈 NATIONAL RUNNER-UP"
                                    else -> "AUDITION SCHEDULED: WEEK $audWeek, YEAR $audYear"
                                }
                                Text(
                                    text = statusText,
                                    color = if (isAuditionLive || isSemiFinalsLive) Color(0xFFF59E0B) else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (isAuditionLive) {
                            Button(
                                onClick = { viewModel.performWgtAudition() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("wgt_perform_audition_button")
                            ) {
                                Text(
                                    text = "🎤 PERFORM AUDITION PIECE NOW",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        } else if (isSemiFinalsLive) {
                            Button(
                                onClick = { viewModel.performWgtAudition() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("wgt_perform_semifinals_button")
                            ) {
                                Text(
                                    text = "🌟 PERFORM IN NATIONAL SEMI-FINALS NOW",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        } else {
                            Text(
                                text = "Keep training your vocal skills and writing depth before your stage call!",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    val nextSeasonName = if (state.currentWeek <= 6 || state.currentWeek > 30) "SEASON 1 (SPRING/SUMMER)" else "SEASON 2 (FALL/WINTER)"
                    val targetAuditionWeek = if (state.currentWeek <= 6) 8 else if (state.currentWeek in 7..30) 32 else 8
                    val targetAuditionYear = if (state.currentWeek > 30) state.currentYear + 1 else state.currentYear
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Button(
                            onClick = { viewModel.applyToWhoIsGotTalent() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("wgt_register_now_button")
                        ) {
                            Text(
                                text = "REGISTER FOR $nextSeasonName ($2,000)",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = "Audition Cohort: Week $targetAuditionWeek, Year $targetAuditionYear • Cutoff is 2 weeks prior",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // 3. Featured Auditions / Live Cohort
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "BI-ANNUAL CHAMPIONSHIP CALENDAR",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151926)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📅 Official Broadcast Schedule", color = Color(0xFFFFA000), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("2 Seasons Per Year", color = Color.Gray, fontSize = 10.sp)
                    }
                    Text(
                        text = "• Season 1 (Spring/Summer):\n  - Auditions: Weeks 8–10\n  - Semi-Finals: Week 14\n  - Grand Finale: Week 20\n\n• Season 2 (Fall/Winter):\n  - Auditions: Weeks 32–34\n  - Semi-Finals: Week 37\n  - Grand Finale: Week 40",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151926)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐ Industry Judges Criteria", color = Color(0xFFFFA000), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("3 Major Disciplines", color = Color.Gray, fontSize = 10.sp)
                    }
                    Text(
                        text = "• Vocal Mastery (Minimum 35 required for Semi-Finals)\n• Lyric Artistry & Concept Polish\n• Stage Charisma & Public Acclaim",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // 4. Feature Film Casting & Movie Acting Auditions (WGT Pictures & Global Cinema)
            val hasFans = state.fans > 70_000L
            val millionStreamSongsCount = state.songs.count { it.isReleased && it.totalStreams >= 1_000_000L }
            val hasMillionHits = millionStreamSongsCount >= 5
            val hasVocalSkill = state.voiceSkill >= 80f
            val hasCastFollowers = state.followers >= 50_000L
            val goodArtistRelationsCount = state.npcs.count { !it.isRetired && it.relationshipWithPlayer >= 60f }
            val hasArtistNetwork = goodArtistRelationsCount >= 10
            val hasMeTubeSubs = state.meTubeSubscribers >= 100_000L
            val metCount = listOf(hasFans, hasMillionHits, hasVocalSkill, hasCastFollowers, hasArtistNetwork, hasMeTubeSubs).count { it }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1528)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                            Text("🎬 WGT PICTURES: MOVIE ACTING CASTING", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
                        }
                        Surface(
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Fee: $50,000", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Text(
                        text = "Feature Film Auditions: 'Echoes of the Neon Streets' (Dir. Marcus Sterling)",
                        color = Color(0xFFFDE68A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "WGT Pictures is casting music industry icons for leading and featured cinema roles. Casting directors evaluate your industry stardom, catalog dominance, vocal skill, and community reach across 6 benchmarks:",
                        color = Color(0xFFCBD5E1),
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    // 6 Live Benchmarks Progress
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F0B18))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text("INDUSTRY STARDOM CHECKLIST ($metCount/6 SATISFIED)", color = Color(0xFFFFA000), fontSize = 9.sp, fontWeight = FontWeight.Bold)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("1. Fans > 70,000:", color = Color.White, fontSize = 10.sp)
                            Text("${String.format("%,d", state.fans)} / 70,000 ${if (hasFans) "✓" else "✕"}", color = if (hasFans) Color(0xFF10B981) else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("2. ≥ 5 Songs with 1M+ Streams:", color = Color.White, fontSize = 10.sp)
                            Text("$millionStreamSongsCount / 5 Songs ${if (hasMillionHits) "✓" else "✕"}", color = if (hasMillionHits) Color(0xFF10B981) else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("3. Vocal Skill ≥ 80:", color = Color.White, fontSize = 10.sp)
                            Text("${state.voiceSkill.toInt()} / 80 ${if (hasVocalSkill) "✓" else "✕"}", color = if (hasVocalSkill) Color(0xFF10B981) else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("4. Cast Social Followers ≥ 50k:", color = Color.White, fontSize = 10.sp)
                            Text("${String.format("%,d", state.followers)} / 50,000 ${if (hasCastFollowers) "✓" else "✕"}", color = if (hasCastFollowers) Color(0xFF10B981) else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("5. Peer Network (≥10 Artists at 60+ Rel):", color = Color.White, fontSize = 10.sp)
                            Text("$goodArtistRelationsCount / 10 Artists ${if (hasArtistNetwork) "✓" else "✕"}", color = if (hasArtistNetwork) Color(0xFF10B981) else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("6. MeTube Subscribers ≥ 100k:", color = Color.White, fontSize = 10.sp)
                            Text("${String.format("%,d", state.meTubeSubscribers)} / 100,000 ${if (hasMeTubeSubs) "✓" else "✕"}", color = if (hasMeTubeSubs) Color(0xFF10B981) else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Role Tiers Explanation
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF131024))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("PRODUCTION PAYOUT & ROLE TIERS:", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("🌟 Lead Role (Needs All 6): Pays $100,000 – $200,000 + massive box office acclaim", color = Color(0xFFFDE68A), fontSize = 10.sp)
                        Text("🎭 Supporting Role (3–5 Met): Pays $70,000 – $80,000 + national cinematic fans", color = Color(0xFF93C5FD), fontSize = 10.sp)
                        Text("🎬 Cameo Role (Under 3 Met): Approved for guest appearance, pays $25,000 – $30,000", color = Color(0xFFCBD5E1), fontSize = 10.sp)
                    }

                    Button(
                        onClick = { viewModel.applyForWgtMovieActingRole() },
                        enabled = state.cash >= 50_000.0,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("apply_wgt_movie_role_button")
                    ) {
                        Text(
                            text = if (state.cash >= 50_000.0) "APPLY FOR MOVIE ACTING ROLE ($50,000)" else "INSUFFICIENT FUNDS ($50,000 REQUIRED)",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

// =================== 2. APEX RECORDS WEBSITE (Matches uploaded Image 1) ===================
@Composable
fun ApexRecordsWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    var showDemoSubmissionModal by remember { mutableStateOf(false) }
    var selectedSongForDemo by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E17))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Top Header (Matches Image 1)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF161524))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = "APEX RECORDS",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Button(
                onClick = { showDemoSubmissionModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp).testTag("apex_header_submit_demo_btn")
            ) {
                Text(
                    text = "SUBMIT DEMO",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. Big Typography Hero Section (Matches Image 1)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2E1065),
                            Color(0xFF1E1035),
                            Color(0xFF0F0E17)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Large Headline Stacks
                Text(
                    text = "YOUR\nSOUND.\nOUR\nPLATFORM.",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "We don't just sign artists; we architect careers. Apex Records provides the infrastructure, vision, and relentless drive to amplify your unique frequency to a global audience.",
                    color = Color(0xFFB4B0C8),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // If player is already signed
                if (state.signedLabelId == "apex_records") {
                    Surface(
                        color = Color(0xFF8B5CF6).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF8B5CF6))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(16.dp))
                            Text(
                                text = "OFFICIALLY SIGNED ARTIST • ${( (1f - state.signedLabelCut) * 100 ).toInt()}% ROYALTY SHARE",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = { showDemoSubmissionModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("apex_hero_submit_demo_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "SUBMIT DEMO",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2.5 A&R Scouting Spotlight (Record Labels Seeking Artists)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF19122B)),
            border = BorderStroke(1.2.dp, Color(0xFF8B5CF6)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(color = Color(0xFF8B5CF6), shape = RoundedCornerShape(4.dp)) {
                        Text("A&R SCOUTING CALL", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Text("ACTIVE CYCLE • YEAR ${state.currentYear}", color = Color(0xFFC4B5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFF8B5CF6), CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cast_radio_studio_1788205951087),
                            contentDescription = "Apex Studio",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column {
                        Text("APEX TALENT ACQUISITION COMMITTEE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("\"We are actively scouting independent artists with chart-ready demos and distinct sonic identities for multi-album global distribution deals.\"", color = Color(0xFFCBD5E1), fontSize = 10.sp, fontStyle = FontStyle.Italic)
                    }
                }

                Surface(color = Color(0xFF110D1E), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ADVANCE FUNDING", color = Color.Gray, fontSize = 9.sp)
                            Text("Up to $150K", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ROYALTY SPLIT", color = Color.Gray, fontSize = 9.sp)
                            Text("70% Artist Share", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DISTRIBUTION", color = Color.Gray, fontSize = 9.sp)
                            Text("Global SoundStream", color = Color(0xFFA855F7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Current Roster Section (Matches Image 1)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(14.dp)
                        .background(Color(0xFF8B5CF6))
                )
                Text(
                    text = "FEATURED APEX ROSTER ARTISTS",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Roster Item 1: Echo Vane
            RosterArtistCard(
                badge = "ALT-R&B",
                name = "ECHO VANE",
                latestRelease = "LATEST: \"VOID CALLING\"",
                photoRes = R.drawable.img_artist_vince,
                coverRes = R.drawable.img_unnamed_4
            )

            // Roster Item 2: KXNG
            RosterArtistCard(
                badge = "TRAP & HIP-HOP",
                name = "KXNG",
                latestRelease = "LATEST: \"CROWN HEAVY\"",
                photoRes = R.drawable.img_artist_cyrus_king,
                coverRes = R.drawable.img_unnamed_8
            )

            // Roster Item 3: Lyric Nova
            RosterArtistCard(
                badge = "INDIE SOUL",
                name = "LYRIC NOVA",
                latestRelease = "LATEST: \"ELECTRIC WHISPERS\"",
                photoRes = R.drawable.img_artist_seraphina,
                coverRes = R.drawable.img_unnamed_12
            )

            if (state.signedLabelId == "apex_records") {
                RosterArtistCard(
                    badge = "EXCLUSIVE SIGNING",
                    name = state.artistName.uppercase(),
                    latestRelease = "DISCOGRAPHY: ${state.songs.size} MASTERED TRACKS • GLOBAL SOUNDSTREAM",
                    photoRes = R.drawable.img_artist_aria_nova,
                    coverRes = R.drawable.img_cover_abstract_neon_1788176890098
                )
            }
        }

        // 4. Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A0910))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "APEX RECORDS",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Privacy Policy", color = Color.DarkGray, fontSize = 10.sp)
                Text("•", color = Color.DarkGray, fontSize = 10.sp)
                Text("Terms of Service", color = Color.DarkGray, fontSize = 10.sp)
                Text("•", color = Color.DarkGray, fontSize = 10.sp)
                Text("Artist Portal", color = Color.DarkGray, fontSize = 10.sp)
            }
            Text("© 2024 APEX RECORDS. ALL RIGHTS RESERVED.", color = Color.DarkGray, fontSize = 9.sp)
        }
    }

    // Demo Submission Modal
    if (showDemoSubmissionModal) {
        AlertDialog(
            onDismissRequest = { showDemoSubmissionModal = false },
            containerColor = Color(0xFF1E1B2E),
            title = {
                Text("Apex Records A&R Demo Portal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Select a mastered studio track to submit directly to the Apex A&R Committee. If your demo meets our threshold, you will receive an official recording contract offer in your Email inbox.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp
                    )

                    if (state.songs.isEmpty()) {
                        Text(
                            text = "No recorded songs found. You can submit an Artist Pitch package based on your current Vocal (${state.voiceSkill}%) and Lyrics (${state.writingSkill}%) ratings.",
                            color = Color(0xFFFBBF24),
                            fontSize = 11.sp
                        )
                    } else {
                        LazyColumn(modifier = Modifier.height(140.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(state.songs) { song ->
                                Surface(
                                    onClick = { selectedSongForDemo = song.id },
                                    color = if (selectedSongForDemo == song.id) Color(0xFF8B5CF6).copy(alpha = 0.3f) else Color(0xFF28253B),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, if (selectedSongForDemo == song.id) Color(0xFF8B5CF6) else Color.Transparent),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(song.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Rating: ${String.format("%.1f", song.rating)} ⭐ • ${song.genreId}", color = Color.Gray, fontSize = 10.sp)
                                        }
                                        if (selectedSongForDemo == song.id) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitDemoToApexRecords(selectedSongForDemo)
                        showDemoSubmissionModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                    modifier = Modifier.testTag("apex_submit_confirm_button")
                ) {
                    Text("Submit to A&R", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDemoSubmissionModal = false }) {
                    Text("Cancel", color = Color.Gray, fontSize = 11.sp)
                }
            }
        )
    }
}

@Composable
fun RosterArtistCard(badge: String, name: String, latestRelease: String, photoRes: Int? = null, coverRes: Int? = null) {
    val artistPhoto = photoRes ?: getArtistPhotoDrawable(name)
    val coverArt = coverRes ?: AlbumCoverRegistry.getCoverDrawable(latestRelease, name)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1A29)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF2E2640)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Artist Portrait Thumbnail
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = artistPhoto),
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    color = Color(0xFF8B5CF6).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color(0xFFC4B5FD),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(text = name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Text(text = latestRelease, color = Color(0xFF94A3B8), fontSize = 11.sp)
            }

            // Single Artwork Thumbnail
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            ) {
                Image(
                    painter = painterResource(id = coverArt),
                    contentDescription = latestRelease,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// =================== 3. ALPHADOM STORE WEBSITE (Matches uploaded Image 2) ===================
@Composable
fun AlphadomStoreWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedCategory by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121214))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Header (Matches Image 2)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF18181B))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(24.dp))

            Text(
                text = "ALPHADOM",
                color = Color(0xFFD4AF37),
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            BadgedBox(
                badge = {
                    Badge(containerColor = Color(0xFFD4AF37)) {
                        Text("${state.ownedInstruments.size}", color = Color.Black, fontSize = 9.sp)
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }

        // 2. Hero Banner (Matches Image 2)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF262115), Color(0xFF181611), Color(0xFF121214))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "PREMIUM AUDIO INSTRUMENTS",
                        color = Color(0xFFD4AF37),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "ENGINEERED FOR SOUND",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Discover uncompromising quality in our curated collection of professional synthesizers, guitars, and studio gear.",
                    color = Color(0xFFA1A1AA),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = { selectedCategory = "Synthesizers" },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text("EXPLORE COLLECTION", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // 3. Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Synthesizers", "Guitars", "Studio Gear", "Microphones", "Luxury").forEach { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    onClick = { selectedCategory = cat },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) Color(0xFFD4AF37) else Color(0xFF202024),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFFD4AF37) else Color(0xFF33333A))
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.Black else Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 4. Products Catalog Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val storeProducts = listOf(
                // Synthesizers
                StoreItemDef("synth_prophet", "Prophet Rev2 8-Voice", "Synthesizers", 2200.0, "+0.35 Song Quality • Analog warmth", 0.35f, false),
                StoreItemDef("synth_moog", "Moog Modular System Studio 55", "Synthesizers", 14500.0, "+0.70 Song Quality • Iconic modular bass", 0.70f, false),
                StoreItemDef("synth_jupiter", "Roland Jupiter-X Synthesizer", "Synthesizers", 2800.0, "+0.40 Song Quality • Modern polyphonic beast", 0.40f, false),
                StoreItemDef("synth_kronos", "Korg Kronos 88-Key Flagship", "Synthesizers", 3900.0, "+0.45 Song Quality • Complete workstation", 0.45f, false),

                // Guitars
                StoreItemDef("strat_60s", "Fender Custom 1960 Stratocaster", "Guitars", 4500.0, "+0.45 Song Quality • Handwound pickups", 0.45f, false),
                StoreItemDef("gibson_lespaul", "Gibson Les Paul 1959 Reissue", "Guitars", 6800.0, "+0.55 Song Quality • Holy grail tone", 0.55f, false),
                StoreItemDef("martin_d28", "Martin D-28 Authentic Acoustic", "Guitars", 3600.0, "+0.40 Song Quality • Resonant Brazilian Rosewood", 0.40f, false),

                // Studio Gear
                StoreItemDef("ssl_console", "SSL 4000 G-Series Console Unit", "Studio Gear", 18500.0, "+0.80 Song Quality • Pro master summing", 0.80f, false),
                StoreItemDef("neve_1073", "Neve 1073 Dual Preamp Strip", "Studio Gear", 3800.0, "+0.45 Song Quality • British class-A drive", 0.45f, false),
                StoreItemDef("la2a_comp", "Teletronix LA-2A Leveling Amplifier", "Studio Gear", 4200.0, "+0.50 Song Quality • Smooth optical compression", 0.50f, false),

                // Microphones
                StoreItemDef("mic_u87", "Neumann U87 Ai Multi-Pattern", "Microphones", 3600.0, "+0.45 Song Quality • Industry vocal standard", 0.45f, false),
                StoreItemDef("mic_c800g", "Sony C-800G Tube Studio Condenser", "Microphones", 11000.0, "+0.75 Song Quality • Elite modern clarity", 0.75f, false),

                // Luxury
                StoreItemDef("lux_rolex", "Rolex Oyster Perpetual Gold", "Luxury", 35000.0, "+20 Public Image • +15 Prestige", 0f, true),
                StoreItemDef("lux_cuban", "18k Solid Diamond Cuban Chain", "Luxury", 28000.0, "+18 Public Image • +12 Prestige", 0f, true),
                StoreItemDef("lux_tailor", "Italian Bespoke Tailored Wardrobe", "Luxury", 12000.0, "+15 Public Image • +10 Prestige", 0f, true)
            )

            val filteredProducts = if (selectedCategory == "All") storeProducts else storeProducts.filter { it.category == selectedCategory }

            filteredProducts.forEach { item ->
                val isOwned = if (item.isLuxury) {
                    state.timelineEvents.any { it.contains(item.name) }
                } else {
                    state.ownedInstruments.any { it.name == item.name || it.id == item.id }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B20)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF2A2A33)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Product Real Photo Header with Category Badge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                        ) {
                            Image(
                                painter = painterResource(id = getProductDrawable(item.id, item.category)),
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xFF1B1B20))
                                        )
                                    )
                            )
                            Surface(
                                color = Color(0xFFD4AF37).copy(alpha = 0.9f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(8.dp).align(Alignment.TopStart)
                            ) {
                                Text(
                                    text = item.category.uppercase(),
                                    color = Color.Black,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(8.dp).align(Alignment.TopEnd)
                            ) {
                                Text(
                                    text = "★★★★★ 4.9",
                                    color = Color(0xFFF59E0B),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.name,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "$${String.format("%,.0f", item.price)}",
                                    color = Color(0xFFD4AF37),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Text(text = item.benefit, color = Color(0xFF94A3B8), fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(2.dp))

                            if (isOwned) {
                                Surface(
                                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color(0xFF10B981))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("OWNED & IN STUDIO", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (item.isLuxury) {
                                            viewModel.buyLuxuryItem(item.id)
                                        } else {
                                            viewModel.buyInstrument(item.id)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(34.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                        Text("BUY NOW ($${formatCompactMoney(item.price)})", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class StoreItemDef(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val benefit: String,
    val bonus: Float,
    val isLuxury: Boolean
)

// =================== 4. OMEGA ELECTORAL COMMISSION (Matches uploaded Image 3) ===================
@Composable
fun OecPortalWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    var showLiveResultsTab by remember { mutableStateOf(state.hasVotedInCurrentElection) }
    val race = remember(state.currentWeek, state.currentYear) {
        PoliticsSimulationHelper.getActiveElectionRace(state.currentWeek, state.currentYear)
    }
    val racePosition = if (state.currentWeek <= 20) "State Governor" else "City Mayor & Council"
    val isVotingSeason = state.isElectionWeek || state.currentWeek == 14 || state.currentWeek == 38
    val liveResults = remember(state.currentWeek, state.currentYear, state.webHub.electionVotedCandidateId, state.electionVotedCandidateId, state.endorsedPoliticianId, state.endorsedCandidateId, isVotingSeason) {
        PoliticsSimulationHelper.calculateLiveResults(
            contestants = race.contestants,
            position = racePosition,
            playerVotedCandidateId = state.webHub.electionVotedCandidateId ?: state.electionVotedCandidateId,
            playerEndorsedCandidateId = state.endorsedPoliticianId ?: state.endorsedCandidateId,
            playerPopularity = state.popularity,
            currentWeek = state.currentWeek,
            currentYear = state.currentYear,
            isVotingSeason = isVotingSeason
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B263B))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(24.dp))

            Text(
                text = "Omega Electoral Commission",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = "Security", tint = Color(0xFF60A5FA), modifier = Modifier.size(22.dp))
        }

        // 2. Deep Blue Graphic Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1E3A8A), Color(0xFF172554), Color(0xFF0D1B2A))
                    )
                )
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(Color(0xFF60A5FA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.HowToVote, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = "OFFICIAL ELECTION PORTAL • YEAR ${state.currentYear}",
                        color = Color(0xFF93C5FD),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${race.position.uppercase()} ELECTION",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Welcome citizen ${state.artistName}. Official voter registration and ballot processing under state electoral oversight.",
                    color = Color(0xFFBFDBFE),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                if (state.isElectionWeek || state.currentWeek == 14 || state.currentWeek == 38) {
                    Surface(
                        color = Color(0xFFEF4444).copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFFEF4444))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MANDATORY ELECTION WEEK: You cannot advance to next week until you cast your official ballot!",
                                color = Color(0xFFFCA5A5),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (!state.isVoterRegistered) {
                                viewModel.registerAsVoter()
                            } else {
                                viewModel.showToast("Citizen Voter ID verified active: ${state.voterIdNumber.ifEmpty { "OEC-VTR-8841" }}")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (state.isVoterRegistered) Color(0xFF10B981) else Color(0xFF3B82F6)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(if (state.isVoterRegistered) Icons.Default.CheckCircle else Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text(if (state.isVoterRegistered) "REGISTERED" else "REGISTER TO VOTE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { showLiveResultsTab = !showLiveResultsTab },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF60A5FA)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(14.dp))
                            Text(if (showLiveResultsTab) "VIEW CANDIDATES" else "LIVE RESULTS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Voter Registration Status Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("CITIZEN VOTER STATUS", color = Color(0xFF93C5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (state.isVoterRegistered) "Registered (ID: ${state.voterIdNumber.ifEmpty { "OEC-VTR-8841" }})" else "Status: Unregistered Citizen",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Surface(
                    color = if (state.hasVotedInCurrentElection) Color(0xFF10B981).copy(alpha = 0.2f) else if (state.isVoterRegistered) Color(0xFF3B82F6).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (state.hasVotedInCurrentElection) "BALLOT CAST" else if (state.isVoterRegistered) "READY TO VOTE" else "REGISTRATION REQ",
                        color = if (state.hasVotedInCurrentElection) Color(0xFF10B981) else if (state.isVoterRegistered) Color(0xFF60A5FA) else Color(0xFFEF4444),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Endorsement Warning / Info
        if (state.tookEndorsementMoney || state.endorsedPoliticianId != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261E0D)),
                border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ACTIVE CAMPAIGN ENDORSEMENT PACT", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "You accepted $${String.format("%,.0f", state.endorsementMoneyAmount.takeIf { it > 0.0 } ?: 40000.0)} to endorse ${state.endorsedPoliticianName ?: "your candidate"}. Voting for a rival candidate is high-stakes wire fraud: if they win, you will be ARRESTED by State Crime Command; if they lose, your public integrity will crash.",
                        color = Color(0xFFFEF3C7),
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        if (showLiveResultsTab || state.hasVotedInCurrentElection) {
            // Live Results Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DYNAMIC LIVE RETURNS (${race.position})",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Turnout: ${String.format("%.1f", liveResults.turnoutPercentage)}%",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Total Certified Ballots Counted: ${String.format("%,d", liveResults.totalBallotsCounted)}",
                            color = Color(0xFF93C5FD),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )

                        for (candidateShare in liveResults.candidateShares) {
                            val candidate = race.contestants.find { it.politician.id == candidateShare.candidateId }
                            val isPlayerVote = (state.webHub.electionVotedCandidateId ?: state.electionVotedCandidateId) == candidateShare.candidateId
                            val isLeader = candidateShare.candidateId == liveResults.leadingCandidateId

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(candidateShare.candidateName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        if (isLeader) {
                                            Surface(color = Color(0xFFF59E0B), shape = RoundedCornerShape(3.dp)) {
                                                Text("PROJECTED LEADER", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                        if (isPlayerVote) {
                                            Surface(color = Color(0xFF10B981), shape = RoundedCornerShape(3.dp)) {
                                                Text("YOUR VOTE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            color = when (candidateShare.weeklyTrendDirection) {
                                                "RISING" -> Color(0xFF10B981).copy(alpha = 0.2f)
                                                "FALLING" -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                                else -> Color(0xFF64748B).copy(alpha = 0.2f)
                                            },
                                            shape = RoundedCornerShape(3.dp)
                                        ) {
                                            Text(
                                                text = candidateShare.weeklyDeltaText,
                                                color = when (candidateShare.weeklyTrendDirection) {
                                                    "RISING" -> Color(0xFF10B981)
                                                    "FALLING" -> Color(0xFFEF4444)
                                                    else -> Color(0xFF94A3B8)
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                        Text(
                                            text = "${String.format("%.1f", candidateShare.percentage)}%",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                LinearProgressIndicator(
                                    progress = { (candidateShare.percentage / 100f).toFloat() },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = if (isLeader) Color(0xFF38BDF8) else Color(0xFF64748B),
                                    trackColor = Color(0xFF0F172A),
                                )
                            }
                        }

                        Divider(color = Color(0xFF2D3E5B), thickness = 0.8.dp)

                        Text("DISTRICT RETURNS BREAKDOWN", color = Color(0xFF93C5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)

                        for (district in liveResults.districtReturns) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(district.districtName, color = Color(0xFFCBD5E1), fontSize = 10.sp)
                                Text("Leading: ${district.leadingParty} (${String.format("%.0f", district.turnout)}% Turnout)", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Candidates & Agendas Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "CERTIFIED CONTESTING CANDIDATES (${race.contestants.size} CONTENDERS)",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            for (contestant in race.contestants) {
                val politician = contestant.politician
                val isVotedForThis = (state.webHub.electionVotedCandidateId ?: state.electionVotedCandidateId) == politician.id
                val isEndorsedByPlayer = (state.endorsedPoliticianId ?: state.endorsedCandidateId) == politician.id

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.2.dp, if (isVotedForThis) Color(0xFF10B981) else if (isEndorsedByPlayer) Color(0xFF38BDF8) else Color(0xFF2D3E5B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.5.dp, Color(0xFF60A5FA).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = getPoliticianDrawable(politician.name)),
                                        contentDescription = politician.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Column {
                                    Text(politician.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("${politician.title} • ${politician.party}", color = Color(0xFF93C5FD), fontSize = 10.sp)
                                    Text("\"${contestant.campaignCatchphrase}\"", color = Color(0xFFE2E8F0), fontSize = 10.sp, fontStyle = FontStyle.Italic)
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (isEndorsedByPlayer) {
                                    Surface(color = Color(0xFF38BDF8).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                        Text("ENDORSED ALLY", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                if (isVotedForThis) {
                                    Surface(color = Color(0xFF10B981).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                        Text("BALLOT CAST ✓", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }

                        Text("Electoral Platform & Campaign Promises:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)

                        for (promise in contestant.promises) {
                            Column(modifier = Modifier.padding(start = 4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("• ${promise.title}", color = Color(0xFFE2E8F0), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(promise.description, color = Color(0xFF94A3B8), fontSize = 10.sp, lineHeight = 13.sp)
                                Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(3.dp)) {
                                    Text("Impact: ${promise.mechanicalEffect}", color = Color(0xFF38BDF8), fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }

                        if (!state.hasVotedInCurrentElection) {
                            if (isVotingSeason) {
                                Button(
                                    onClick = {
                                        if (!state.isVoterRegistered) {
                                            viewModel.registerAsVoter()
                                        }
                                        viewModel.voteInOmegaElection(politician.id, politician.name, politician.party)
                                        showLiveResultsTab = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag("vote_btn_${politician.id}")
                                ) {
                                    Icon(Icons.Default.HowToVote, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("CAST BALLOT FOR ${politician.name.uppercase()}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Surface(
                                    color = Color(0xFF1E293B),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color(0xFF334155)),
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("POLLS OPEN IN VOTING SEASON (WEEKS 14 & 38)", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else if (isVotedForThis) {
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Color(0xFF10B981)),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            ) {
                                Text("BALLOT RECORDED IN DISTRICT REGISTER", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                }
            }
        }

        // Official Announcements
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Official Announcements",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B263B)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(color = Color(0xFFEF4444).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text("URGENT", color = Color(0xFFEF4444), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Text("Official Voter ID Authentication Active", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("All certified ballots are permanently entered into the district ledger. Double voting is penalized under federal election statutes.", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }
    }
}

// =================== 5. PULSEWIRE NEWS WEBSITE (Matches uploaded Image 4) ===================
@Composable
fun PulsewireNewsWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Header (Matches Image 4)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1F2937))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(24.dp))

            Text(
                text = "⚡ PULSEWIRE",
                color = Color(0xFF00E5FF),
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.White, modifier = Modifier.size(22.dp))
        }

        // 2. Cyan Breaking Ticker (Matches Image 4)
        Surface(
            color = Color(0xFF00E5FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "BREAKING: WGT COHORT ANNOUNCED // PRODUCER BATTLE HEATS UP // ROYALTIES SURGE //",
                color = Color.Black,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        // DYNAMIC NEWS ARTICLES
        val latestNews = state.newsArticles.reversed()
        val heroArticle = latestNews.firstOrNull()

        if (heroArticle != null) {
            // 3. Exclusive Hero Article (Dynamic)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = Color(0xFF00E5FF).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                            Text("BREAKING", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Text("LATEST: BY ${heroArticle.author.uppercase()}", color = Color.Gray, fontSize = 10.sp)
                    }

                    com.example.ui.ReliableAsyncImage(
                        model = heroArticle.coverImageId,
                        contentDescription = "Cover Image",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp))
                    )

                    Text(
                        text = heroArticle.headline,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = heroArticle.body.take(150) + "...",
                        color = Color(0xFF9CA3AF),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Button(
                        onClick = { viewModel.showToast("Opening exclusive editorial breakdown...") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("READ FULL STORY", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // 4. Feature Cards Grid (Dynamic)
        if (latestNews.size > 1) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                latestNews.drop(1).take(3).forEachIndexed { index, article ->
                    val badgeName = when (index) {
                        0 -> "GOSSIP"
                        1 -> "INDUSTRY"
                        else -> "TRENDS"
                    }
                    val badgeColor = when (index) {
                        0 -> Color(0xFFF59E0B)
                        1 -> Color(0xFF10B981)
                        else -> Color(0xFF8B5CF6)
                    }
                    PulsewireArticleRow(
                        badge = badgeName,
                        badgeColor = badgeColor,
                        title = article.headline,
                        description = article.subtitle,
                        imageUrl = article.coverImageId
                    )
                }
            }
        } else {
             Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Tech
                PulsewireArticleRow(
                    badge = "TECH",
                    badgeColor = Color(0xFF10B981),
                    title = "TOP 10 GEAR TO BOOST PRODUCTION",
                    description = "The renaissance of analog polyphonic synths in modern chart-topping records."
                )

                // Card 2: Gossip
                PulsewireArticleRow(
                    badge = "GOSSIP",
                    badgeColor = Color(0xFFF59E0B),
                    title = "STUDIO BRAWL: WHO THREW THE FIRST PUNCH?",
                    description = "Behind the scenes at Sector 4 recording facility: ego clashes and master tape disputes."
                )
            }
        }

        // 5. Trending Section (Matches Image 4)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📈 TRENDING",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            listOf(
                "01. BASS DROP PLUGIN REVIEW ➔",
                "02. SYNTH WARS 2024 ➔",
                "03. INDIE LABEL TAKEOVER ➔"
            ).forEach { item ->
                Text(
                    text = item,
                    color = Color(0xFF00E5FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { viewModel.showToast("Loading article: $item") }
                )
            }
        }
    }
}

@Composable
fun PulsewireArticleRow(badge: String, badgeColor: Color, title: String, description: String, imageUrl: String? = null) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = badgeColor.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                    Text(badge, color = badgeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            if (!imageUrl.isNullOrBlank()) {
                com.example.ui.ReliableAsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = painterResource(id = AlbumCoverRegistry.getCoverDrawable(title)),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = Color(0xFF9CA3AF),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}@Composable
fun BlackHatWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080C08))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Monospace Header (Matches Image 5)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F170F))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("[>]", color = Color(0xFF00FF66), fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Text("BH.ONLINE", color = Color(0xFF00FF66), fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }

            Icon(imageVector = Icons.Default.Shield, contentDescription = "Encrypted", tint = Color(0xFF00FF66), modifier = Modifier.size(18.dp))
        }

        // 2. Terminal Alert Box (Matches Image 5)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF140808)),
            border = BorderStroke(1.dp, Color(0xFFEF4444)),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "⚠️ >> ALERT: SCC TRACKING DETECTED IN SECTOR 7G",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Evade active surveillance nodes. All communications encrypted via P2P escrow. Purge local cache before exit.",
                    color = Color(0xFFFCA5A5),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // 3. Risk Status Shield Indicator
        val isSafe = state.reliability >= 70f && (state.industryRespect >= 30f || state.influence >= 30f)
        Surface(
            color = if (isSafe) Color(0xFF00FF66).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
            border = BorderStroke(1.dp, if (isSafe) Color(0xFF00FF66) else Color(0xFFEF4444)),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isSafe) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isSafe) Color(0xFF00FF66) else Color(0xFFEF4444),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isSafe) {
                        "ESCROW SHIELD SECURE (Reliability: ${state.reliability.toInt()}%, Influence: ${state.industryRespect.toInt()}%)"
                    } else {
                        "CRITICAL RISK: SCC INTERCEPTION RISK 85% (Need ≥70% Rel & ≥30% Inf)"
                    },
                    color = if (isSafe) Color(0xFF00FF66) else Color(0xFFEF4444),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // 4. Market Listings (Matches Image 5)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("> MARKET_LISTINGS.exe", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Text("STATUS: ACTIVE", color = Color(0xFF00FF66), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }

            // Item 1: Cast Followers
            BlackHatListingItem(
                title = "CAST FOLLOWERS (10K BULK)",
                description = "Botnet deployed via decentralized nodes. High retention rate.",
                priceBtc = "0.05 BTC ($3,200)",
                imageRes = R.drawable.cast_rooftop_night_1788205863239,
                reqTag = null,
                onExecute = { viewModel.executeBlackHatPurchase("CAST_FOLLOWERS") }
            )

            // Item 2: Streams Pack
            BlackHatListingItem(
                title = "INSTANT STREAMS PACK (50K STREAMS)",
                description = "Automated playback across 500+ proxy IPs. Bypass standard detection algorithms.",
                priceBtc = "0.12 BTC ($5,500)",
                imageRes = R.drawable.img_cover_abstract_neon_1788176890098,
                reqTag = null,
                onExecute = { viewModel.executeBlackHatPurchase("STREAM_PACK") }
            )

            // Item 3: Politician Votes
            BlackHatListingItem(
                title = "BLACK MARKET CIVIC PROXY VOTES (5K VOTES)",
                description = "Syndicate proxy ballot stuffing for regional municipal elections.",
                priceBtc = "0.20 BTC ($8,000)",
                imageRes = R.drawable.img_artist_senator_vance,
                reqTag = null,
                onExecute = { viewModel.executeBlackHatPurchase("POLITICIAN_VOTES") }
            )

            // Item 4: Stolen Hardware
            BlackHatListingItem(
                title = "SURPLUS STUDIO HARDWARE (UNMARKED)",
                description = "High-end audio interfaces and mics. Serial numbers expunged. Shipped via dark-drop.",
                priceBtc = "0.15 BTC ($3,500)",
                imageRes = R.drawable.cast_studio_synth_1788205895230,
                reqTag = "REQ: VERIFIED ACC",
                onExecute = { viewModel.executeBlackHatPurchase("STOLEN_HARDWARE") }
            )
        }

        // 5. Recent Transactions Log (Matches Image 5)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A100A))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("> RECENT_TX_LOG", color = Color(0xFF00FF66), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text("0x7A...3F9: 0.05 BTC [DELIVERED]", color = Color.Gray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Text("0xB2...1C4: 0.12 BTC [DELIVERED]", color = Color.Gray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Text("0x9F...E2A: 0.20 BTC [CONFIRMED]", color = Color.Gray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun BlackHatListingItem(title: String, description: String, priceBtc: String, imageRes: Int? = null, reqTag: String?, onExecute: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E160E)),
        border = BorderStroke(1.dp, Color(0xFF1E331E)),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (imageRes != null) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, Color(0xFF00FF66).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF080C08).copy(alpha = 0.35f))
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = Color(0xFF00FF66), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    if (reqTag != null) {
                        Text(reqTag, color = Color(0xFFFBBF24), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    }
                }

                Text(description, color = Color(0xFF86EFAC), fontSize = 9.sp, fontFamily = FontFamily.Monospace)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(priceBtc, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                    Button(
                        onClick = { onExecute() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66)),
                        shape = RoundedCornerShape(2.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("[EXECUTE]", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

// =================== 7. THE GRAND AWARDS WEBSITE (Matches uploaded Image 6) ===================
@Composable
fun GrandAwardsWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0C0A))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Header (Matches Image 6)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF17130F))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFE5C07B), modifier = Modifier.size(16.dp))
                Text(
                    text = "THE GRAND AWARDS",
                    color = Color(0xFFE5C07B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(22.dp))
        }

        // 2. Hero Section (Matches Image 6)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF261D12), Color(0xFF18130D), Color(0xFF0E0C0A))
                    )
                )
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5C07B).copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "WELCOME TO",
                        color = Color(0xFFE5C07B),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "THE\nPINNACLE\nOF\nACHIEVEMENT",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 32.sp
                )

                Text(
                    text = "Celebrating visionary artistry, unparalleled creativity, and defining moments of the industry in a night of uncompromising elegance.",
                    color = Color(0xFFD1C7BD),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.showToast("Laureate Spotlight: Shadow Collective & Top Producers.") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5C07B)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Text("VIEW WINNERS", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.requestGrandAwardsInvitation() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE5C07B)),
                        border = BorderStroke(1.dp, Color(0xFFE5C07B)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Text(
                            if (state.grandAwardVipAccess) "VIP CONFIRMED" else "GALA DETAILS",
                            color = Color(0xFFE5C07B),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. Latest Laureates & Alumni Hall of Fame (Grand Award Alumni)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("GRAND AWARD ALUMNI & LAUREATES", color = Color(0xFFE5C07B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Surface(color = Color(0xFFE5C07B).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                    Text("HALL OF FAME", color = Color(0xFFE5C07B), fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            // Current / Recent Grand Laureate
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1611)),
                border = BorderStroke(1.2.dp, Color(0xFFE5C07B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFFE5C07B), CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = if (state.grandAwardWon) R.drawable.img_artist_vince else R.drawable.img_artist_seraphina),
                            contentDescription = "Grand Laureate",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFE5C07B), modifier = Modifier.size(14.dp))
                            Text("ARTIST OF THE YEAR • RECENT LAUREATE", color = Color(0xFFE5C07B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(if (state.grandAwardWon) state.artistName else "Seraphina Vance", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        Text(if (state.grandAwardWon) "Grammy-Equivalent Pinnacle Honoree" else "Album: \"Neon Hymns\" • 12x Platinum", color = Color(0xFFD1C7BD), fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(0.5.dp, Color(0xFFE5C07B).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_unnamed_2),
                            contentDescription = "Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Alumni 1: Cyrus King
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF17130F)),
                border = BorderStroke(1.dp, Color(0xFF33271C)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFE5C07B).copy(alpha = 0.5f), CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_artist_cyrus_king),
                            contentDescription = "Cyrus King",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("RECORD OF THE YEAR ALUMNI", color = Color(0xFFE5C07B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("Cyrus King", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Single: \"Empire Mindstate\" • Multi-Grammy Alumni", color = Color.Gray, fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_unnamed_7),
                            contentDescription = "Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Alumni 2: Aria Nova
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF17130F)),
                border = BorderStroke(1.dp, Color(0xFF33271C)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFE5C07B).copy(alpha = 0.5f), CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_artist_aria_nova),
                            contentDescription = "Aria Nova",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("BEST NEW ARTIST ALUMNI", color = Color(0xFFE5C07B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("Aria Nova", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Single: \"Celestial Drift\" • Breakthrough Phenom", color = Color.Gray, fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_unnamed_15),
                            contentDescription = "Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("ACADEMY VOTING DISCIPLINES", color = Color(0xFFE5C07B), fontSize = 12.sp, fontWeight = FontWeight.Bold)

            listOf("🎧 Contemporary Pop", "🎙️ Urban & R&B", "🎸 Alternative & Indie", "🎛️ Electronic & Dance").forEach { disc ->
                Surface(
                    color = Color(0xFF17130F),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(disc, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(10.dp))
                }
            }
        }
    }
}

// =================== 8. SOLD.COM REAL ESTATE & ASSETS (Matches uploaded Image 7) ===================
@Composable
fun SoldRealEstateWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedFilter by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF14171C))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Header (Matches Image 7)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1C222B))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(24.dp))

            Text(
                text = "SOLD.COM",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(22.dp))
        }

        // 2. Urgent Banner (Matches Image 7)
        Surface(
            color = Color(0xFFF59E0B),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "⚠️ AUCTION ENDING SOON: THE SKYLINE VIEW STUDIO CLOSES IN 2H 14M",
                color = Color.Black,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        // 3. Hero Section (Matches Image 7)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1E293B), Color(0xFF14171C))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Discover Extraordinary Assets",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Exclusive access to off-market real estate, premier recording estates, and verified hypercars.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Category Filter Chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Real Estate", "Cars & Luxury").forEach { cat ->
                        val isSel = selectedFilter == cat
                        Surface(
                            onClick = { selectedFilter = cat },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSel) Color(0xFF38BDF8) else Color(0xFF1E242E),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF38BDF8) else Color(0xFF334155))
                        ) {
                            Text(
                                text = cat,
                                color = if (isSel) Color.Black else Color.LightGray,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Asset Listings
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ================= REAL ESTATE LISTINGS =================
            if (selectedFilter == "All" || selectedFilter == "Real Estate") {
                // House 1: Cozy Studio Apartment
                SoldAssetCard(
                    title = "Cozy Studio Apartment",
                    location = "Arts District, Sector 4",
                    price = 20000.0,
                    specs = "+5 Energy / Week • Compact Creative Space • Acoustic Treatment",
                    imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "house_studio" || it.name.contains("Cozy Studio") },
                    onBuy = { viewModel.buyHouse("house_studio") }
                )

                // House 2: Suburban Townhouse
                SoldAssetCard(
                    title = "Suburban Townhouse",
                    location = "Greenfield Borough, Sector 6",
                    price = 80000.0,
                    specs = "+12 Energy / Week • Private Garage • Home Writing Room",
                    imageUrl = "https://images.unsplash.com/photo-1580587771525-78b9dba3b914?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "house_townhouse" || it.name.contains("Townhouse") },
                    onBuy = { viewModel.buyHouse("house_townhouse") }
                )

                // House 3: Downtown Loft
                SoldAssetCard(
                    title = "Downtown Loft",
                    location = "Central Arts Quarter",
                    price = 200000.0,
                    specs = "+25 Energy / Week • Brick Exposed Walls • High Ceilings & Mixing Bay",
                    imageUrl = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "house_loft" || it.name.contains("Downtown Loft") },
                    onBuy = { viewModel.buyHouse("house_loft") }
                )

                // House 4: Downtown Executive High-Rise Loft
                SoldAssetCard(
                    title = "Downtown Executive High-Rise Loft",
                    location = "Metropolitan Arts District",
                    price = 850000.0,
                    specs = "+30 Energy / Week • 20ft Ceilings • Acoustic Isolation Booth",
                    imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "executive_loft" || it.name.contains("Executive") },
                    onBuy = { viewModel.buyHouse("executive_loft") }
                )

                // House 5: Sunset Hills Mansion
                SoldAssetCard(
                    title = "Sunset Hills Mansion",
                    location = "Sunset Hills Ridge",
                    price = 1000000.0,
                    specs = "+50 Energy / Week • Gated Security • Private Pool & Vocal Studio",
                    imageUrl = "https://images.unsplash.com/photo-1600585154526-990dced4db0d?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "house_mansion" || (it.name.contains("Sunset") && !it.name.contains("Ultra")) },
                    onBuy = { viewModel.buyHouse("house_mansion") }
                )

                // House 6: Suburban Palm Estate & Grounds
                SoldAssetCard(
                    title = "Suburban Palm Estate & Grounds",
                    location = "Emerald Valley Gated Reserve",
                    price = 1800000.0,
                    specs = "+45 Energy / Week • Expansive Lawn & Private Pool Sanctuary",
                    imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "palm_estate" || it.name.contains("Palm Estate") },
                    onBuy = { viewModel.buyHouse("palm_estate") }
                )

                // House 7: Alpine Mountain Modern Sanctuary
                SoldAssetCard(
                    title = "Alpine Mountain Modern Sanctuary",
                    location = "Aspen High Ridges",
                    price = 2900000.0,
                    specs = "+55 Energy / Week • Panoramic Glacier Views • Pure Rejuvenation",
                    imageUrl = "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "alpine_retreat" || it.name.contains("Alpine") },
                    onBuy = { viewModel.buyHouse("alpine_retreat") }
                )

                // House 8: Skyline View Studio Penthouse
                SoldAssetCard(
                    title = "Skyline View Penthouse & Studio",
                    location = "Upper West Side, Sector 1",
                    price = 4250000.0,
                    specs = "+60 Energy / Week • +25 Prestige • Private Rooftop Heliport & Studio",
                    imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "skyline_penthouse" || it.name.contains("Skyline") },
                    onBuy = { viewModel.buyHouse("skyline_penthouse") }
                )

                // House 9: Coastal Villa
                SoldAssetCard(
                    title = "Coastal Villa - The Glass Pavilion",
                    location = "Pacific Coastline, Sector 8",
                    price = 5100000.0,
                    specs = "+70 Energy / Week • Oceanfront Infinity Pool & Mastering Suite",
                    imageUrl = "https://images.unsplash.com/photo-1613977257363-707ba9348227?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "coastal_villa" || it.name.contains("Coastal") },
                    onBuy = { viewModel.buyHouse("coastal_villa") }
                )

                // House 10: Miami Waterfront Modern Estate
                SoldAssetCard(
                    title = "Miami Waterfront Modern Estate",
                    location = "Biscayne Bay Islands",
                    price = 7800000.0,
                    specs = "+78 Energy / Week • +35 Prestige • 100ft Yacht Dock & Private Beach",
                    imageUrl = "https://images.unsplash.com/photo-1584738766473-61c083514bf4?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "miami_estate" || it.name.contains("Miami") },
                    onBuy = { viewModel.buyHouse("miami_estate") }
                )

                // House 11: Sunset Hills Ultra-Mansion
                SoldAssetCard(
                    title = "Sunset Hills Ultra-Mansion",
                    location = "Sunset Hills Heights, Sector 3",
                    price = 12500000.0,
                    specs = "+85 Energy / Week • +40 Prestige • 12-Car Gallery & Private Compound",
                    imageUrl = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "sunset_ultra_mansion" || it.name.contains("Sunset Hills Ultra") },
                    onBuy = { viewModel.buyHouse("sunset_ultra_mansion") }
                )

                // House 12: Beverly Hills Classical Manor
                SoldAssetCard(
                    title = "Beverly Hills Classical Manor",
                    location = "Beverly Hills Ultra-Enclave",
                    price = 22000000.0,
                    specs = "+100 Energy / Week • +50 Prestige • 5-Acre Private Botanical Park & Ballroom",
                    imageUrl = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?q=80&w=800&auto=format&fit=crop",
                    category = "house",
                    isOwned = state.ownedHouses.any { it.id == "beverly_manor" || it.name.contains("Beverly Hills") },
                    onBuy = { viewModel.buyHouse("beverly_manor") }
                )
            }

            // ================= VEHICLES & HYPERCARS =================
            if (selectedFilter == "All" || selectedFilter == "Cars & Luxury") {
                // Car 1: Beat-up Sedan
                SoldAssetCard(
                    title = "Beat-up Urban Sedan",
                    location = "Local Pre-Owned Lots",
                    price = 5000.0,
                    specs = "15% Tour Transport Cost Reduction • Dependable Commuter",
                    imageUrl = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "car_sedan" || it.name.contains("Sedan") },
                    onBuy = { viewModel.buyCar("car_sedan") }
                )

                // Car 2: Midrange SUV
                SoldAssetCard(
                    title = "Midrange All-Wheel SUV",
                    location = "Suburban Auto Center",
                    price = 15000.0,
                    specs = "30% Tour Transport Cost Reduction • Ample Gear Storage",
                    imageUrl = "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "car_suv" || it.name.contains("SUV") },
                    onBuy = { viewModel.buyCar("car_suv") }
                )

                // Car 3: Luxury Sports Coupe
                SoldAssetCard(
                    title = "Luxury Sports Coupe",
                    location = "Metro Executive Imports",
                    price = 60000.0,
                    specs = "50% Tour Transport Cost Reduction • +8 Prestige",
                    imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "car_sports" || it.name.contains("Coupe") },
                    onBuy = { viewModel.buyCar("car_sports") }
                )

                // Car 4: Touring Coach Sleeper Bus
                SoldAssetCard(
                    title = "Touring Coach Sleeper Bus",
                    location = "Heavy Coach Logistics",
                    price = 250000.0,
                    specs = "80% Tour Transport Cost Reduction • Full Crew Bunks & Rehearsal Booth",
                    imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "car_bus" || it.name.contains("Bus") },
                    onBuy = { viewModel.buyCar("car_bus") }
                )

                // Car 5: Porsche 911 GT3 RS
                SoldAssetCard(
                    title = "Porsche 911 GT3 RS",
                    location = "Stuttgart Motorsport Division",
                    price = 295000.0,
                    specs = "+18 Prestige • +10 Energy • Carbon Aero Wing • Pure Track Precision",
                    imageUrl = "https://images.unsplash.com/photo-1614162692292-7ac56d7f7f1e?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "porsche_gt3" || it.name.contains("GT3") },
                    onBuy = { viewModel.buyCar("porsche_gt3") }
                )

                // Car 6: Mercedes-Maybach S680
                SoldAssetCard(
                    title = "Mercedes-Maybach S680",
                    location = "Executive Limousine Fleet",
                    price = 340000.0,
                    specs = "+16 Prestige • +12 Energy • Twin-Turbo V12 • First-Class Rear Lounge",
                    imageUrl = "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "maybach_s680" || it.name.contains("Maybach") },
                    onBuy = { viewModel.buyCar("maybach_s680") }
                )

                // Car 7: Aston Martin DBS Superleggera
                SoldAssetCard(
                    title = "Aston Martin DBS Superleggera",
                    location = "British Grand Tourer Collection",
                    price = 385000.0,
                    specs = "+18 Prestige • +12 Energy • 715 HP Twin-Turbo • Sculpted Carbon Body",
                    imageUrl = "https://images.unsplash.com/photo-1617814076367-b759c7d7e738?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "aston_dbs" || it.name.contains("DBS") },
                    onBuy = { viewModel.buyCar("aston_dbs") }
                )

                // Car 8: McLaren 750S Spider
                SoldAssetCard(
                    title = "McLaren 750S Spider Carbon",
                    location = "Woking Performance Centre",
                    price = 460000.0,
                    specs = "+20 Prestige • +14 Energy • Carbon Monocage • Retractable Hardtop",
                    imageUrl = "https://images.unsplash.com/photo-1621135802920-133df287f89c?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "mclaren_750s" || it.name.contains("McLaren") },
                    onBuy = { viewModel.buyCar("mclaren_750s") }
                )

                // Car 9: Rolls-Royce Phantom
                SoldAssetCard(
                    title = "Rolls-Royce Phantom Series II",
                    location = "Goodwood Bespoke Atelier",
                    price = 480000.0,
                    specs = "+20 Prestige • +14 Energy • Starlight Headliner • Silent Magic Carpet",
                    imageUrl = "https://images.unsplash.com/photo-1631295868223-63265840d001?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "rolls_phantom" || it.name.contains("Phantom") },
                    onBuy = { viewModel.buyCar("rolls_phantom") }
                )

                // Car 10: Ferrari SF90 Stradale
                SoldAssetCard(
                    title = "Ferrari SF90 Stradale Hybrid",
                    location = "Maranello Exotic Division",
                    price = 620000.0,
                    specs = "+25 Prestige • +15 Energy • 986 HP • 0-60 in 2.5s",
                    imageUrl = "https://images.unsplash.com/photo-1583121274602-3e2820c69888?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "ferrari_sf90" || it.name.contains("SF90") },
                    onBuy = { viewModel.buyCar("ferrari_sf90") }
                )

                // Car 11: Lamborghini Revuelto V12
                SoldAssetCard(
                    title = "Lamborghini Revuelto V12",
                    location = "Sant'Agata Hypercar Reserve",
                    price = 780000.0,
                    specs = "+30 Prestige • +16 Energy • 1001 HP Naturally Aspirated V12",
                    imageUrl = "https://images.unsplash.com/photo-1544829099-b9a0c07fad1a?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "lambo_revuelto" || it.name.contains("Revuelto") },
                    onBuy = { viewModel.buyCar("lambo_revuelto") }
                )

                // Car 12: Bugatti Chiron Super Sport
                SoldAssetCard(
                    title = "Bugatti Chiron Super Sport 300+",
                    location = "Molsheim Atelier Vault",
                    price = 3800000.0,
                    specs = "+50 Prestige • +20 Energy • 1577 HP Quad-Turbo W16 • Top Speed 304 mph",
                    imageUrl = "https://images.unsplash.com/photo-1600712242805-5f78671b24da?q=80&w=800&auto=format&fit=crop",
                    category = "car",
                    isOwned = state.ownedCars.any { it.id == "bugatti_chiron" || it.name.contains("Bugatti") },
                    onBuy = { viewModel.buyCar("bugatti_chiron") }
                )
            }
        }
    }
}

@Composable
fun SoldAssetCard(
    title: String,
    location: String,
    price: Double,
    specs: String,
    imageUrl: String,
    category: String = "house",
    isOwned: Boolean,
    onBuy: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E242E)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Visual Image Header with reliable AsyncImage and fallbacks
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                com.example.ui.ReliableAsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    fallbackText = title,
                    fallbackCategory = category
                )
                Surface(
                    color = Color(0xFF0F172A).copy(alpha = 0.80f),
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = if (category == "house") "REAL ESTATE" else "HYPERCAR",
                        color = if (category == "house") Color(0xFF38BDF8) else Color(0xFFF59E0B),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(location, color = Color.Gray, fontSize = 10.sp)
                }
                Text("$${String.format("%,.0f", price)}", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Text(specs, color = Color(0xFFCBD5E1), fontSize = 11.sp)

            if (isOwned) {
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF10B981)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✓ OWNED & REGISTERED IN PORTFOLIO", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else {
                Button(
                    onClick = { onBuy() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Text("ACQUIRE ASSET ($${String.format("%,.0f", price)})", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// =================== 9. STATE CRIME COMMAND WEBSITE (Matches uploaded Image 8) ===================
@Composable
fun StateCrimeCommandWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1D))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Header (Matches Image 8)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF111936))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp))
                Text(
                    text = "STATE CRIME COMMAND",
                    color = Color(0xFF60A5FA),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Surface(color = Color(0xFF10B981).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                Text("● LIVE", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }

        // 2. Judicial Remand & Correctional Lockout (If Player Under Arrest!)
        if (state.isUnderArrest) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF260D0D)),
                border = BorderStroke(1.5.dp, Color(0xFFEF4444)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = Color(0xFFEF4444), shape = RoundedCornerShape(4.dp)) {
                            Text("🚨 REMANDED IN CUSTODY - ACTIVE INJUNCTION", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Text("BOOKING: SCC-9941-FL", color = Color(0xFFFCA5A5), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "INMATE: ${state.artistName.uppercase()}",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "CHARGES: ${state.arrestReason.ifEmpty { "Campaign Bribery & High-Level Political Wire Fraud" }}",
                        color = Color(0xFFFCA5A5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("CIVIL LOCKOUT ORDER:", color = Color(0xFF93C5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("• Commercial Ventures & Business Enterprises: LOCKED", color = Color(0xFFCBD5E1), fontSize = 10.sp)
                            Text("• Sound Recording Studio Facilities: LOCKED", color = Color(0xFFCBD5E1), fontSize = 10.sp)
                            Text("• World Concert Tour Operations: LOCKED", color = Color(0xFFCBD5E1), fontSize = 10.sp)
                            Text("• World's Got Talent Television Broadcast: LOCKED", color = Color(0xFFCBD5E1), fontSize = 10.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Bail Bond Amount:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("$${String.format("%,.0f", state.arrestBailAmount)}", color = Color(0xFF38BDF8), fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sentenced Detention Remaining:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("${state.arrestSentenceWeeksRemaining} Weeks", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.Black)
                    }

                    Button(
                        onClick = { viewModel.payArrestBail() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(38.dp).testTag("pay_bail_button")
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("POST BAIL BOND ($${String.format("%,.0f", state.arrestBailAmount)}) & RESTORE ACCESS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }

                    OutlinedButton(
                        onClick = { viewModel.serveDetentionWeek() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp).testTag("serve_detention_button")
                    ) {
                        Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SERVE 1 WEEK DETENTION (${state.arrestSentenceWeeksRemaining} WEEKS LEFT)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // Legal Defense Counsel
                    Divider(color = Color(0xFF374151), thickness = 0.8.dp)
                    Text("RETAIN CRIMINAL DEFENSE COUNSEL", color = Color(0xFF93C5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)

                    if (state.arrestAttorneyHired != null) {
                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("RETAINED COUNSEL: ${state.arrestAttorneyHired} (Advocacy in effect)", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                        }
                    } else {
                        PoliticsSimulationHelper.DEFENSE_ATTORNEYS.forEach { attorney ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(attorney.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(attorney.firm, color = Color(0xFF94A3B8), fontSize = 9.sp)
                                        Text("${(attorney.bailDiscount * 100).toInt()}% Bail Reduction • -${attorney.sentenceReductionWeeks} Weeks Sentence", color = Color(0xFF38BDF8), fontSize = 9.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.hireDefenseAttorney(attorney) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("RETAIN ($${String.format("%,.0f", attorney.cost)})", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Critical Wanted Player Card (If Wanted!)
        if (state.isSccWanted) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF260D0D)),
                border = BorderStroke(1.5.dp, Color(0xFFEF4444)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = Color(0xFFEF4444), shape = RoundedCornerShape(4.dp)) {
                            Text("🚨 CRITICAL THREAT - TARGET WANTED", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Text("ID: XH-771", color = Color.Gray, fontSize = 10.sp)
                    }

                    Text(
                        text = "SUBJECT: ${state.artistName.uppercase()}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "WANTED FOR: ${state.sccCrimeReason}",
                        color = Color(0xFFFCA5A5),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "SANCTIONS: Brand deals frozen • Commercial assets under federal surveillance • Public integrity penalized 50%.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 10.sp
                    )

                    Button(
                        onClick = { viewModel.paySccGovernmentFine() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(38.dp).testTag("pay_scc_fine_button")
                    ) {
                        Text("PAY GOVERNMENT FINE ($${String.format("%,.0f", state.sccFineAmount)}) & EXPUNGE DOSSIER", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        } else {
            Surface(
                color = Color(0xFF10B981).copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFF10B981)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    Text(
                        text = "CITIZEN STATUS: CLEAR (No active warrants or federal tracking)",
                        color = Color(0xFF10B981),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3. Priority Targets: Most Wanted (Matches Image 8)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "// PRIORITY TARGETS: MOST WANTED",
                color = Color(0xFF60A5FA),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            listOf(
                Triple("HIGH", "XH-992", Pair("\"GHOST_NODE\"", "CYBER-EXTORTION & DNS SPOOF")),
                Triple("HIGH", "XH-994", Pair("\"SILVER_FOX\"", "CRITICAL INFRASTRUCTURE SABOTAGE")),
                Triple("MED", "XH-993", Pair("\"NEON_RAID\"", "P2P ESCROW MONEY LAUNDERING")),
                Triple("LOW", "XH-995", Pair("\"CIPHER\"", "WIRE FRAUD & SYNDICATE STREAMS"))
            ).forEachIndexed { idx, (threat, id, crimePair) ->
                val (alias, charges) = crimePair
                val mugshot = when (idx) {
                    0 -> R.drawable.img_artist_shadow_lyricist
                    1 -> R.drawable.img_artist_travis_scottie
                    2 -> R.drawable.img_artist_busta_rhymes
                    else -> R.drawable.img_artist_cyrus_king
                }
                val bounty = when (threat) {
                    "HIGH" -> "$75,000 BOUNTY"
                    "MED" -> "$35,000 BOUNTY"
                    else -> "$15,000 BOUNTY"
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D36)),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (threat == "HIGH") Color(0xFFEF4444).copy(alpha = 0.6f) else Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Suspect Mugshot
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.5.dp, if (threat == "HIGH") Color(0xFFEF4444) else Color(0xFF60A5FA), RoundedCornerShape(6.dp))
                        ) {
                            Image(
                                painter = painterResource(id = mugshot),
                                contentDescription = alias,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                color = Color.Black.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(bottomStart = 4.dp),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Text("WANTED", color = Color(0xFFEF4444), fontSize = 7.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(2.dp))
                            }
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (threat == "HIGH") Color(0xFFEF4444) else Color(0xFFF59E0B),
                                    shape = RoundedCornerShape(2.dp)
                                ) {
                                    Text(threat, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                                Text("DOSSIER: $id", color = Color(0xFF93C5FD), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                Text(bounty, color = Color(0xFFFBBF24), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(alias, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
                            Text("WANTED: $charges", color = Color(0xFFCBD5E1), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // 4. Activity Map (Matches Image 8)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("// ILLEGAL ACTIVITY MAP: SECTOR 4", color = Color(0xFF60A5FA), fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F182E)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("LAT: 34.0522 N, LON: 118.2437 W", color = Color.Gray, fontSize = 9.sp)
                    Text("DETECTED ANOMALIES: 3 • THREAT LEVEL: ELEVATED", color = Color(0xFFFCA5A5), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { viewModel.showToast("Anonymous cyber intel submitted to Sector 4 dispatch.") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth().height(32.dp)
                    ) {
                        Text("SUBMIT ANONYMOUS TIP", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// =================== 10. GLOBAL MARKET INTELLIGENCE ===================
@Composable
fun MarketIntelligenceWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("GLOBAL MARKET INTELLIGENCE", color = Color(0xFF38BDF8), fontSize = 15.sp, fontWeight = FontWeight.Black)
                Text("omnisearch.com/market • Macroeconomic Feed", color = Color.Gray, fontSize = 10.sp)
            }
            Surface(color = Color(0xFF38BDF8).copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                Text(state.economyCycle, color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Macro Indicators", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Economic Cycle:", color = Color.Gray, fontSize = 11.sp)
                            Text(state.economyCycle, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Inflation Rate:", color = Color.Gray, fontSize = 11.sp)
                            Text("${String.format("%.1f", state.inflationRate * 100)}%", color = Color(0xFFF43F5E), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Fuel Price:", color = Color.Gray, fontSize = 11.sp)
                            Text("$${state.fuelPrice}/L", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =================== 11. WIKISINGA ARTIST & MUSIC ENCYCLOPEDIA ===================
@Composable
fun WikisingaArtistsWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    var searchWikiQuery by remember { mutableStateOf("") }
    val featuredArtist = state.npcs.maxByOrNull { it.fans } ?: state.npcs.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF131518))
    ) {
        // 1. Wikisinga Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E232B))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("W", color = Color(0xFF38BDF8), fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Serif)
                    }
                }
                Column {
                    Text("WIKISINGA", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    Text("wikisinga.org • The Living Music Encyclopedia", color = Color(0xFF94A3B8), fontSize = 9.sp)
                }
            }

            Surface(color = Color(0xFF38BDF8).copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                Text("${state.npcs.size + 1} Biographies", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }

        // Search Bar in Wiki
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF191D24))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchWikiQuery,
                onValueChange = { searchWikiQuery = it },
                placeholder = { Text("Search Wikisinga artists, producers, albums...", color = Color.Gray, fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f).height(44.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF38BDF8),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Featured Article of the Day / Player Infobox
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232B)),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = Color(0xFFF59E0B).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text("FEATURED BIOGRAPHY OF THE WEEK", color = Color(0xFFFBBF24), fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Text("VERIFIED ENTRY", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Infobox Image
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(8.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = getArtistPhotoDrawable(state.artistName)),
                                        contentDescription = state.artistName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(state.artistName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Lead Artist", color = Color.Gray, fontSize = 9.sp)
                            }

                            // Infobox Metadata Table
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "${state.artistName} is a contemporary recording artist, songwriter, and executive producer based in Metro Sound City. Known for prolific studio sessions and commercial momentum.",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Active: Year ${state.currentYear}, Week ${state.currentWeek}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                Text("• Discography: ${state.songs.size} Singles • ${state.albums.size} Albums", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                Text("• SoundStream Reach: ${formatCompactMoney(state.fans.toDouble()).replace("$", "")} Fans", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("• Estimated Net Worth: $${formatCompactMoney(state.cash)}", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Section Header
            item {
                Text(
                    text = "LIVING ARTIST DIRECTORY & ENCYCLOPEDIC DOSSIERS",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Filtered NPC list
            val filteredNpcs = state.npcs.filter {
                searchWikiQuery.isBlank() ||
                it.name.contains(searchWikiQuery, ignoreCase = true) ||
                it.genrePreference.contains(searchWikiQuery, ignoreCase = true)
            }

            items(filteredNpcs) { npc ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232B)),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.8.dp, Color(0xFF2E3846)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Artist Portrait
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        ) {
                            Image(
                                painter = painterResource(id = getArtistPhotoDrawable(npc.name)),
                                contentDescription = npc.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(npc.name, color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                                Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(3.dp)) {
                                    Text(npc.genrePreference, color = Color(0xFFCBD5E1), fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Text(
                                text = "Fans: ${formatCompactMoney(npc.fans.toDouble()).replace("$", "")} • Industry Acclaim: ${npc.influence.toInt()}/100 • Affinity: ${npc.relationshipWithPlayer.toInt()}%",
                                color = Color.LightGray,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "A prominent musician based in the city. Discography verified under open music archives.",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Wikisinga content is licensed under Creative Commons BY-SA 4.0", color = Color.Gray, fontSize = 9.sp)
                    Text("Music charts, fan counts, and streaming statistics updated weekly.", color = Color.DarkGray, fontSize = 8.sp)
                }
            }
        }
    }
}
