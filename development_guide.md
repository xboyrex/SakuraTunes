# Sakura Tunes — Architecture & Development Guide

## 1. Module Overview
- **`:app`** (`com.sakuratunes.music`): Main Android application containing the Jetpack Compose UI, **Liquid Glass shader engine** (`ui/glass/`), Media3 `MusicService` playback engine, Room database (`db/`), Lyrics providers, Listen Together client, and ShazamKit audio recognition.
- **`:innertube`** (`com.sakuratunes.innertube`): Streaming API client, cipher deobfuscation, and `NewPipeExtractor` integration.
- **`:lrclib`**, **`:kugou`**, **`:betterlyrics`**, **`:paxsenix`**: Synchronized line-by-line and word-by-word lyrics providers.
- **`:lastfm`**: Last.fm authentication and scrobbling client.
- **`:shazamkit`**: Audio fingerprinting and song recognition module.

## 2. Liquid Glass Architecture (`com.sakuratunes.music.ui.glass`)
- **`LiquidGlass.kt`**:
  - Manages persistent optical settings (`GlassSettings`) including `GlassQuality` (`HIGH`, `BALANCED`, `LITE`), `blurRadiusDp`, `refractionAmount`, `refractionHeightDp`, and `chromaticDispersion`.
  - Exposes `LocalPageGlassBackdrop` (captured from the main content layer in `MainActivity.kt`) and `Modifier.liquidGlass(...)` / `Modifier.fallbackGlass(...)`.
- **`GlassMotion.kt`**:
  - Provides expressive organic shapes (`SakuraPetalShape`, `LobedShape`, `CookieShape`, `SunnyShape`), `DampedDragAnimation` spring physics, and `InteractiveHighlight` touch-tracked specular shaders.
- **`GlassNavBar.kt`**:
  - Implements `LiquidBottomTabs` and `LiquidBottomTab`, rendering a floating refractive capsule bar with a draggable, spring-animated selection pill.

## 3. Building & Releasing
- Run `./gradlew assembleGmsDebug` for local development builds.
- Run `./gradlew assembleGmsRelease assembleFossRelease` for production builds.
- Pushing to `main` triggers `.github/workflows/build.yml` and `.github/workflows/release.yml` automatically.
