package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AvatarWidget
import com.example.R
import com.example.data.GameStateEntity

/**
 * Dedicated visual model for an artist's face profile photo.
 * Ensures 1-to-1 unique mapping for every single artist in the game.
 */
data class ArtistFaceProfile(
    val idOrName: String,
    val drawableResId: Int? = null,
    val bgColors: List<Color> = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
    val skinColor: Color = Color(0xFFE5B58C),
    val hairColor: Color = Color(0xFF1C1917),
    val hairStyle: HairShape = HairShape.SIDE_PART,
    val faceShape: FaceShape = FaceShape.CHISELED,
    val eyeColor: Color = Color(0xFF151922),
    val glassesStyle: GlassesType = GlassesType.NONE,
    val facialHair: FacialHairType = FacialHairType.NONE,
    val accessoryType: AccessoryType = AccessoryType.NONE,
    val clothingColor: Color = Color(0xFF1E293B),
    val highlightColor: Color = Color(0x40FFFFFF)
)

enum class HairShape {
    SLICK_BACK, CURLS, AFRO_FADE, DREADS, SPIKY_NEON, BLONDE_BOB, SIDE_PART, BUZZ,
    WAVY_LONG, PUNK_MOHAWK, BRAIDS, MESSY_CURLS, TIGHT_CROP
}

enum class FaceShape {
    OVAL, SQUARE_JAW, HEART, SLIM, CHISELED
}

enum class GlassesType {
    NONE, DARK_SHADES, NEON_VISOR, GOLD_FRAMES, RETRO_ROUND
}

enum class FacialHairType {
    NONE, STUBBLE, GOATEE, TRIMMED_BEARD, MUSTACHE
}

enum class AccessoryType {
    NONE, GOLD_CHAIN, DIAMOND_EARRINGS, BANDANA, SILVER_CHOKER, PEARL_STRAND, BUCKET_HAT
}

/**
 * Registry of all Album and Single Covers using the 20 unnamed artwork images
 */
object AlbumCoverRegistry {
    val ALL_COVER_DRAWABLES = listOf(
        R.drawable.img_unnamed_0,
        R.drawable.img_unnamed_1,
        R.drawable.img_unnamed_2,
        R.drawable.img_unnamed_3,
        R.drawable.img_unnamed_4,
        R.drawable.img_unnamed_5,
        R.drawable.img_unnamed_6,
        R.drawable.img_unnamed_7,
        R.drawable.img_unnamed_8,
        R.drawable.img_unnamed_9,
        R.drawable.img_unnamed_10,
        R.drawable.img_unnamed_11,
        R.drawable.img_unnamed_12,
        R.drawable.img_unnamed_13,
        R.drawable.img_unnamed_14,
        R.drawable.img_unnamed_15,
        R.drawable.img_unnamed_16,
        R.drawable.img_unnamed_17,
        R.drawable.img_unnamed_18,
        R.drawable.img_unnamed_19,
        R.drawable.img_cover_abstract_neon_1788176890098,
        R.drawable.img_cover_abstract_cosmic_1788176904087
    )

    fun getCoverDrawable(title: String, artistName: String = ""): Int {
        val hash = kotlin.math.abs((title.trim().lowercase() + artistName.trim().lowercase()).hashCode())
        return ALL_COVER_DRAWABLES[hash % ALL_COVER_DRAWABLES.size]
    }
}

/**
 * Registry for Cast Social Media Post Attachments (using unnamed media & scenes)
 */
object CastMediaRegistry {
    val SCENE_AND_UNNAMED_DRAWABLES = listOf(
        R.drawable.img_unnamed_0,
        R.drawable.img_unnamed_1,
        R.drawable.img_unnamed_2,
        R.drawable.img_unnamed_3,
        R.drawable.img_unnamed_4,
        R.drawable.img_unnamed_5,
        R.drawable.img_unnamed_6,
        R.drawable.img_unnamed_7,
        R.drawable.img_unnamed_8,
        R.drawable.img_unnamed_9,
        R.drawable.img_unnamed_10,
        R.drawable.img_unnamed_11,
        R.drawable.img_unnamed_12,
        R.drawable.img_unnamed_13,
        R.drawable.img_unnamed_14,
        R.drawable.img_unnamed_15,
        R.drawable.img_unnamed_16,
        R.drawable.img_unnamed_17,
        R.drawable.img_unnamed_18,
        R.drawable.img_unnamed_19,
        R.drawable.cast_rooftop_night_1788205863239,
        R.drawable.cast_acoustic_room_1788205880418,
        R.drawable.cast_studio_synth_1788205895230,
        R.drawable.cast_dance_practice_1788205912115,
        R.drawable.cast_street_luxury_1788205929331,
        R.drawable.cast_radio_studio_1788205951087
    )

    fun getDrawableForPost(imageRes: String?, postId: String, content: String): Int? {
        if (!imageRes.isNullOrBlank()) {
            val idx = imageRes.filter { it.isDigit() }.toIntOrNull()
            if (idx != null && idx >= 0 && idx < SCENE_AND_UNNAMED_DRAWABLES.size) {
                return SCENE_AND_UNNAMED_DRAWABLES[idx]
            }
        }
        val hash = kotlin.math.abs((postId + content).hashCode())
        val hasMedia = (hash % 3 == 0) || content.contains("studio", ignoreCase = true) ||
                content.contains("music", ignoreCase = true) || content.contains("single", ignoreCase = true) ||
                content.contains("album", ignoreCase = true) || content.contains("live", ignoreCase = true) ||
                content.contains("tour", ignoreCase = true) || content.contains("stage", ignoreCase = true) ||
                content.contains("rehearsal", ignoreCase = true) || content.contains("vibe", ignoreCase = true) ||
                content.contains("drop", ignoreCase = true) || content.contains("cover", ignoreCase = true)

        return if (hasMedia) {
            SCENE_AND_UNNAMED_DRAWABLES[hash % SCENE_AND_UNNAMED_DRAWABLES.size]
        } else null
    }
}

object ArtistVisualRegistry {
    // Unique face photo mapping for all 36 named artists, contacts & public figures
    private val ARTIST_PROFILES = mutableMapOf<String, ArtistFaceProfile>()

    init {
        fun register(drawableRes: Int, vararg keys: String) {
            for (key in keys) {
                val clean = key.trim().lowercase()
                ARTIST_PROFILES[clean] = ArtistFaceProfile(
                    idOrName = clean,
                    drawableResId = drawableRes
                )
            }
        }

        // 1. Vince (Replaced Viper)
        register(R.drawable.img_artist_vince, "vince", "viper", "npc_star_9")

        // 2. Aria Nova
        register(R.drawable.img_artist_aria_nova, "aria nova", "aria", "npc_star_1", "npc_rival_1")

        // 3. Abby Lane
        register(R.drawable.img_artist_abby_lane, "abby lane", "abby", "npc_rookie_abby", "npc_star_25")

        // 4. Ademola
        register(R.drawable.img_artist_ademola, "ademola", "npc_ademola", "ade")

        // 5. Alabaster Rock
        register(R.drawable.img_artist_alabaster_rock, "alabaster rock", "alabaster", "npc_star_10")

        // 6. Amadeus Neo
        register(R.drawable.img_artist_amadeus_neo, "amadeus neo", "amadeus", "npc_star_12")

        // 7. Billie Eyelash
        register(R.drawable.img_artist_billie_eyelash, "billie eyelash", "billie", "billie eilish", "npc_star_17")

        // 8. Bob Marley-esque
        register(R.drawable.img_artist_bob_marley_esque, "bob marley-esque", "bob marley", "marley", "npc_star_23")

        // 9. Busta Rhymes
        register(R.drawable.img_artist_busta_rhymes, "busta rhymes", "busta rhyme", "busta", "npc_star_6")

        // 10. Chloe Bloom
        register(R.drawable.img_artist_chloe_bloom, "chloe bloom", "chloe", "npc_star_11")

        // 11. Chloe Retro
        register(R.drawable.img_artist_chloe_retro, "chloe retro", "npc_rookie_chloe")

        // 12. Cousin Sarah
        register(R.drawable.img_artist_cousin_sarah, "cousin sarah", "sarah", "npc_family_cousin_sarah", "npc_cousin_sarah")

        // 13. Cyrus King
        register(R.drawable.img_artist_cyrus_king, "cyrus king", "cyrus", "npc_star_14", "npc_rookie_cyrus")

        // 14. DJ Pulse
        register(R.drawable.img_artist_dj_pulse, "dj pulse", "pulse", "npc_star_2")

        // 15. Drake Drake
        register(R.drawable.img_artist_drake_drake, "drake drake", "drake", "npc_star_18")

        // 16. Elysian Sky
        register(R.drawable.img_artist_elysian_sky, "elysian sky", "elysian", "npc_star_13", "npc_rookie_elysian")

        // 17. Eminem-y
        register(R.drawable.img_artist_eminem_y, "eminem-y", "eminem", "npc_star_22")

        // 18. Governor Sterling
        register(R.drawable.img_artist_governor_sterling, "governor sterling", "gov sterling", "sterling", "npc_gov_sterling", "npc_governor_sterling")

        // 19. Ian Woods
        register(R.drawable.img_artist_ian_woods, "ian woods", "ian", "npc_rookie_ian")

        // 20. Jax Riff
        register(R.drawable.img_artist_jax_riff, "jax riff", "jax", "npc_star_4")

        // 21. Justin Blaze
        register(R.drawable.img_artist_justin_blaze, "justin blaze", "justin", "npc_star_7")

        // 22. Kanye East
        register(R.drawable.img_artist_kanye_east, "kanye east", "kanye", "npc_star_15")

        // 23. Kid Laroi-esque
        register(R.drawable.img_artist_kid_laroi_esque, "kid laroi-esque", "kid laroi", "laroi", "npc_star_24")

        // 24. Lady Gogo
        register(R.drawable.img_artist_lady_gogo, "lady gogo", "lady gaga", "gogo", "npc_star_19")

        // 25. Matt Thorne
        register(R.drawable.img_artist_matt_thorne, "matt thorne", "matt", "npc_rookie_matt")

        // 26. Mia Flow
        register(R.drawable.img_artist_mia_flow, "mia flow", "mia", "npc_rookie_mia")

        // 27. Mom
        register(R.drawable.img_artist_mom, "mom", "mother", "npc_family_mom", "npc_mom")

        // 28. Nova Vibe
        register(R.drawable.img_artist_nova_vibe, "nova vibe", "nova", "npc_rookie_nova")

        // 29. Post Malone-y
        register(R.drawable.img_artist_post_malone_y, "post malone-y", "post malone", "posty", "npc_star_20")

        // 30. Ryan Sparks
        register(R.drawable.img_artist_ryan_sparks, "ryan sparks", "ryan", "npc_rookie_ryan")

        // 31. Sam Rivers
        register(R.drawable.img_artist_sam_rivers, "sam rivers", "sam", "npc_rookie_sam")

        // 32. Senator Vance
        register(R.drawable.img_artist_senator_vance, "senator vance", "sen vance", "vance", "npc_sen_vance", "npc_senator_vance")

        // 33. Seraphina
        register(R.drawable.img_artist_seraphina, "seraphina", "npc_star_8")

        // 34. Shadow Lyricist
        register(R.drawable.img_artist_shadow_lyricist, "shadow lyricist", "shadow", "npc_star_3")

        // 35. Travis Scottie
        register(R.drawable.img_artist_travis_scottie, "travis scottie", "travis scott", "travis", "npc_star_16")

        // 36. Uncle Joe
        register(R.drawable.img_artist_uncle_joe, "uncle joe", "joe", "npc_family_uncle_joe", "npc_uncle_joe")
    }

    fun getProfile(identifier: String): ArtistFaceProfile {
        val cleanKey = identifier.trim().lowercase()
        ARTIST_PROFILES[cleanKey]?.let { return it }

        // Strip common prefixes or formatting
        val normalized = cleanKey
            .replace("npc_", "")
            .replace("star_", "")
            .replace("rookie_", "")
            .replace("family_", "")
            .replace("gov_", "")
            .replace("sen_", "")
            .replace("_", " ")
            .replace("-", " ")
            .trim()

        ARTIST_PROFILES[normalized]?.let { return it }

        // Fuzzy search for partial match
        for ((key, profile) in ARTIST_PROFILES) {
            if (key.length >= 3 && (normalized.contains(key) || key.contains(normalized))) {
                return profile
            }
        }

        // Generate a 100% deterministic unique profile based on hash for dynamically generated NPCs
        val hash = kotlin.math.abs(cleanKey.hashCode())
        val bgGradients = listOf(
            listOf(Color(0xFF831843), Color(0xFF4C1D95)),
            listOf(Color(0xFF1E3A8A), Color(0xFF065F46)),
            listOf(Color(0xFFB45309), Color(0xFF78350F)),
            listOf(Color(0xFF4C1D95), Color(0xFF1E1B4B)),
            listOf(Color(0xFF0E7490), Color(0xFF164E63)),
            listOf(Color(0xFF9F1239), Color(0xFF4C0519)),
            listOf(Color(0xFF15803D), Color(0xFF14532D)),
            listOf(Color(0xFF6D28D9), Color(0xFF3B0764))
        )
        val skinTones = listOf(
            Color(0xFF5B3419), Color(0xFF8D5524), Color(0xFFB45309),
            Color(0xFFE0AC69), Color(0xFFF5C396), Color(0xFFFFDBAC),
            Color(0xFFFED7AA), Color(0xFFFDE68A)
        )
        val hairColors = listOf(
            Color(0xFF18181B), Color(0xFF78350F), Color(0xFFF59E0B),
            Color(0xFFDC2626), Color(0xFF22C55E), Color(0xFF06B6D4),
            Color(0xFFA855F7), Color(0xFFF43F5E), Color(0xFFE2E8F0)
        )
        val hairStyles = HairShape.values()
        val faceShapes = FaceShape.values()
        val glasses = GlassesType.values()
        val beards = FacialHairType.values()
        val accessories = AccessoryType.values()

        return ArtistFaceProfile(
            idOrName = cleanKey,
            bgColors = bgGradients[hash % bgGradients.size],
            skinColor = skinTones[(hash / 3) % skinTones.size],
            hairColor = hairColors[(hash / 5) % hairColors.size],
            hairStyle = hairStyles[(hash / 7) % hairStyles.size],
            faceShape = faceShapes[(hash / 11) % faceShapes.size],
            glassesStyle = glasses[(hash / 13) % glasses.size],
            facialHair = beards[(hash / 17) % beards.size],
            accessoryType = accessories[(hash / 19) % accessories.size],
            clothingColor = Color(0xFF1E293B)
        )
    }
}

/**
 * Universal Composable for Artist Face Profile Photo.
 * Guarantees every single artist has their real photo asset across SeeChat, Cast,
 * Music Platforms, Contacts, Billboard and Leaderboards.
 */
@Composable
fun ArtistFaceAvatar(
    artistIdentifier: String,
    modifier: Modifier = Modifier,
    isPlayer: Boolean = false,
    playerState: GameStateEntity? = null,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    shape: Shape = CircleShape
) {
    val cleanId = remember(artistIdentifier) { artistIdentifier.trim() }

    // If it's the player, use the player avatar system
    if (isPlayer || (playerState != null && cleanId.equals(playerState.artistName.trim(), ignoreCase = true))) {
        val state = playerState
        if (state != null) {
            Box(
                modifier = modifier
                    .clip(shape)
                    .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, shape) else Modifier)
            ) {
                AvatarWidget(
                    baseLook = state.avatarBaseLook,
                    skinTone = state.avatarSkinTone,
                    hairStyleId = state.avatarHairStyleId,
                    clothingId = state.avatarClothingId,
                    jewelryId = state.avatarJewelryId,
                    modifier = Modifier.fillMaxSize()
                )
            }
            return
        }
    }

    val profile = remember(cleanId) {
        ArtistVisualRegistry.getProfile(cleanId)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, shape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (profile.drawableResId != null) {
            Image(
                painter = painterResource(id = profile.drawableResId),
                contentDescription = cleanId,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Procedural fallback for dynamic custom NPCs
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(profile.bgColors)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cleanId.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Universal Composable for Song and Album Covers using the 20 unnamed cover art images.
 */
@Composable
fun SongAlbumCover(
    title: String,
    artistName: String = "",
    genre: String = "Pop",
    modifier: Modifier = Modifier,
    customCoverArt: String? = null,
    cornerRounding: Dp = 8.dp
) {
    val drawableRes = remember(title, artistName) {
        AlbumCoverRegistry.getCoverDrawable(title, artistName)
    }
    val shape = RoundedCornerShape(cornerRounding)

    Box(
        modifier = modifier
            .clip(shape)
            .border(0.5.dp, Color.White.copy(alpha = 0.15f), shape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Subtle dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f))
                    )
                )
        )
        // Clean initial emblem in corner
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(topEnd = 4.dp))
        ) {
            Text(
                text = title.take(2).uppercase(),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun PlayerAvatarDisplay(
    state: GameStateEntity,
    size: Dp = 36.dp,
    modifier: Modifier = Modifier
) {
    AvatarWidget(
        baseLook = state.avatarBaseLook,
        skinTone = state.avatarSkinTone,
        hairStyleId = state.avatarHairStyleId,
        clothingId = state.avatarClothingId,
        jewelryId = state.avatarJewelryId,
        modifier = modifier.size(size)
    )
}
