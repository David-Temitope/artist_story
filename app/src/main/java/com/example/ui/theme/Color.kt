package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Background: Deep midnight neutral / blue-black (Never pure black)
val DarkBg = Color(0xFF0B0D14)

// Card & Panel Surfaces: Layered surfaces with high visual depth
val CardBg = Color(0xFF141722)
val SurfaceColor = Color(0xFF1D2230)
val SurfaceVariant = Color(0xFF262D3E)

// Typography Text Colors
val TextPrimary = Color(0xFFF8FAFC)     // Bright crisp soft-white
val TextSecondary = Color(0xFF94A3B8)   // Muted descriptions and secondary info
val TextTertiary = Color(0xFF64748B)    // Subtle metadata, timestamps

// Subtle Borders & Dividers
val BorderColor = Color(0xFF2A3142)
val BorderSubtle = Color(0xFF1E2432)

// Primary Brand Color: Electric/Cobalt Blue (Primary actions, selection, progression)
val PrimaryBrandColor = Color(0xFF2563EB)
val PrimaryBrandLight = Color(0xFF3B82F6)

// Semantic Action & Status Colors
val SuccessColor = Color(0xFF10B981)    // Income, profits, growth, completed goals
val WarningColor = Color(0xFFF59E0B)    // Energy, deadlines, moderate risks, opportunities
val DangerColor = Color(0xFFEF4444)     // Critical stress, debt, losses, destructive decisions
val InfoColor = Color(0xFF06B6D4)       // Analytics, reports, streaming insights
val CreativePurple = Color(0xFF8B5CF6)  // Music creation, studio, artistry
val PremiumGold = Color(0xFFFBBF24)     // Prestige, awards, legendary milestones

// Compatibility aliases for existing references throughout the codebase
val NeonTeal = PrimaryBrandLight
val NeonPink = DangerColor
val NeonPurple = CreativePurple
val AccentGreen = SuccessColor
val AccentGold = WarningColor

val Purple80 = PrimaryBrandLight
val PurpleGrey80 = TextSecondary
val Pink80 = DangerColor

val Purple40 = PrimaryBrandColor
val PurpleGrey40 = TextSecondary
val Pink40 = DangerColor

val PrimaryPurple = Color(0xFF6C5CE7)
val SecondarySalmon = Color(0xFFFF7675)
