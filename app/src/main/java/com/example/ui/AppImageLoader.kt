package com.example.ui

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.Coil
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object AppImageLoader {
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        try {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()

            val imageLoader = ImageLoader.Builder(context.applicationContext)
                .okHttpClient(okHttpClient)
                .memoryCache {
                    MemoryCache.Builder(context.applicationContext)
                        .maxSizePercent(0.25)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.applicationContext.cacheDir.resolve("image_cache"))
                        .maxSizeBytes(100L * 1024 * 1024)
                        .build()
                }
                .crossfade(true)
                .respectCacheHeaders(false)
                .build()

            Coil.setImageLoader(imageLoader)
            initialized = true
        } catch (_: Throwable) {
            // Fallback gracefully
        }
    }
}

@Composable
fun ReliableAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallbackText: String = "",
    fallbackCategory: String = "general"
) {
    val context = LocalContext.current
    val imageRequest = remember(model) {
        ImageRequest.Builder(context)
            .data(model)
            .crossfade(true)
            .build()
    }

    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        loading = {
            val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.25f,
                targetValue = 0.65f,
                animationSpec = infiniteRepeatable(
                    animation = tween(750, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "shimmerAlpha"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E293B).copy(alpha = alpha)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        error = {
            ProceduralImageFallback(
                title = fallbackText.ifEmpty { contentDescription ?: "" },
                category = fallbackCategory
            )
        }
    )
}

@Composable
fun ProceduralImageFallback(
    title: String,
    category: String,
    modifier: Modifier = Modifier
) {
    val hash = (title.hashCode() and 0x7FFFFFFF)
    val gradients = listOf(
        listOf(Color(0xFF1E3A8A), Color(0xFF0F172A)),
        listOf(Color(0xFF831843), Color(0xFF18181B)),
        listOf(Color(0xFF14532D), Color(0xFF064E3B)),
        listOf(Color(0xFF581C87), Color(0xFF020617)),
        listOf(Color(0xFFB45309), Color(0xFF1C1917))
    )
    val chosenGradient = gradients[hash % gradients.size]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(chosenGradient)),
        contentAlignment = Alignment.Center
    ) {
        when (category) {
            "avatar", "club" -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title.take(2).uppercase().ifEmpty { "VIP" },
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            "house", "realestate" -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color(0xFF38BDF8).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = title.take(24),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            "car", "vehicle" -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color(0xFFF59E0B).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = title.take(24),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            "scenery", "wallpaper" -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Landscape,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
