# Genesis Android project notes

Genesis is an offline-first, text-led life simulator built from the Artist Story Android app. Preserve the native Kotlin, Jetpack Compose, and Room architecture unless a migration is needed for a verified product reason.

## Product constraints

- Keep the game playable without internet, accounts, or AI services. Any remote media must have a useful local fallback.
- Build believable outcomes from player state, world conditions, NPC memory, and time. Prefer deterministic seeded simulation so saves reproduce outcomes and bug reports can be replayed.
- Keep decisions legible and consequential. Record major choices and outcomes in the life history.
- No animated characters/scenes or 3D. Keep the interface calm, readable, and suited to phones.
- Ship a small, complete playable slice. Do not mark a screen complete when its primary action is a no-op or a “Coming Soon” placeholder.
- Protect existing player saves. Do not enable destructive database migrations without an explicit versioned reset plan.

## Build and release

- Use the included Gradle wrapper and the JDK required by the Android Gradle Plugin.
- Debug builds use the standard Android debug key. Release signing values come from `GENESIS_KEYSTORE_PATH`, `GENESIS_STORE_PASSWORD`, `GENESIS_KEY_ALIAS`, and `GENESIS_KEY_PASSWORD`; never commit keys or credentials.
- Preserve the existing application ID until the owner verifies Play Store ownership and migration requirements.
- Do not add network or AI dependencies without a user-approved product need and an offline behavior plan.
