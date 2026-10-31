<div align="center">

<img src="assets/ic_launcher-playstore.png" width="140" alt="Sakura Tunes Logo" />

# 🌸 Sakura Tunes

**Next-Generation Liquid Glass Music & Audio Streaming Client for Android**

*Crafted & Engineered by **ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく** ([@xboyrex](https://github.com/xboyrex))*

[![Build Status](https://github.com/xboyrex/SakuraTunes/actions/workflows/build.yml/badge.svg)](https://github.com/xboyrex/SakuraTunes/actions/workflows/build.yml)
[![Latest Release](https://img.shields.io/github/v/release/xboyrex/SakuraTunes?color=FF6BA0&label=Release&logo=github)](https://github.com/xboyrex/SakuraTunes/releases/latest)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-FF6BA0.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Telegram](https://img.shields.io/badge/Telegram-@MayankDroid7-26A5E4?logo=telegram)](https://t.me/MayankDroid7)
[![Telegram Hub](https://img.shields.io/badge/Telegram-@OnyxHub7-26A5E4?logo=telegram)](https://t.me/OnyxHub7)
[![Instagram](https://img.shields.io/badge/Instagram-@mayank.zx7-E4405F?logo=instagram)](https://www.instagram.com/mayank.zx7)
[![YouTube](https://img.shields.io/badge/YouTube-@mayankdroid-FF0000?logo=youtube)](https://youtube.com/@mayankdroid)

</div>

---

## ✨ Overview

**Sakura Tunes** (`com.sakuratunes.music`) is a completely re-engineered Android music streaming client featuring a real-time **Liquid Glass optical shader engine**, physics-driven **Liquid Capsule Navigation**, custom **Sakura Blossom** iconography and themes, and a full studio-grade audio playback pipeline.

### 💎 Liquid Glass Engine & Visual Design
- **Real-Time Refractive Shaders**: Powered by hardware-accelerated backdrop layers (`LocalPageGlassBackdrop`), real-time Gaussian frost blur, chromatic dispersion, and specular rim highlights on Android 12+ (`API 31+`) and AGSL refraction lenses on Android 13+ (`API 33+`), with smooth translucent fallbacks on older devices.
- **Liquid Glass Studio**: Fine-tune optical performance directly inside Settings — switch between **High** (Full Lens + Blur + Chromatic Dispersion), **Balanced** (Blur + Rim Highlight), and **Lite** (Lightweight Translucent Tint), or customize **Blur Radius** (`4dp–42dp`), **Refraction Amount** (`0%–100%`), **Lens Height** (`0dp–36dp`), and **Chromatic Dispersion** (`0%–100%`).
- **Floating Liquid Capsule Navigation Bar**: Physics-based damped drag indicator pill (`DampedDragAnimation`), interactive touch spotlight (`InteractiveHighlight`), spring-loaded scale magnification, and haptic tab selection.
- **Sakura Blossom Palette & Typography**: Expressive `SakuraRoundedHeavy` display typography toggle, custom Sakura Pink (`#FF6BA0`), Midnight Rose, Cyber Violet, Ocean Glass, and Emerald Dew color presets, plus dynamic album-art palette extraction.

### 🎧 Audio, Streaming & Smart Features
- **High-Resolution Audio Pipeline**: Gapless playback, silence skipping, audio normalization, pitch/tempo adjustment, crossfade, and built-in equalizer support via AndroidX Media3.
- **Multi-Source Synchronized Lyrics**: Real-time word-by-word and line-by-line synchronized lyrics via LRCLIB, KuGou, BetterLyrics, and Paxsenix with automatic Romanization (Japanese, Korean, Chinese, Cyrillic, Devanagari) and live translations.
- **Listen Together**: Synchronized group listening sessions with real-time host/guest playback controls.
- **Music Recognition (ShazamKit)**: Identify songs playing around you or from your device with one tap from the Quick Recognition widget or app shortcut.
- **Scrobbling & Rich Presence**: Native Last.fm scrobbling and Discord Rich Presence integration.
- **Quick Storage Analyzer**: Inspect song cache, image/artwork cache, and offline download footprints with one-tap cache cleanup right from Settings.
- **Android Auto, Google Cast & Home Widgets**: Full MediaBrowserService support for Android Auto, Google Cast (`gms` flavor), and customizable home screen glanceable widgets.

---

## 📦 Download & Variants

Every push and release automatically compiles and signs ready-to-install APKs via GitHub Actions:

| Variant | File Name | Description |
| :--- | :--- | :--- |
| **GMS (Recommended)** | `SakuraTunes-with-Google-Cast.apk` | Full version with **Google Cast** support and built-in updater |
| **FOSS** | `SakuraTunes.apk` | Pure open-source build with built-in updater (no proprietary Cast SDK) |
| **Izzy / Reproducible** | `SakuraTunes-izzy.apk` | Reproducible F-Droid / IzzyOnDroid build |

👉 **[Download the latest APK from GitHub Releases](https://github.com/xboyrex/SakuraTunes/releases/latest)**

---

## 🛠️ Building from Source

```bash
git clone https://github.com/xboyrex/SakuraTunes.git
cd SakuraTunes
./gradlew assembleGmsDebug
./gradlew assembleFossRelease assembleGmsRelease
```

---

## 👨‍💻 Developer & Socials

- **Developer**: **ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく** (`xboyrex` · `he/him`)
- **Bio**: Android modding - Web/ App developer
- **GitHub**: [@xboyrex](https://github.com/xboyrex)
- **Repository**: [https://github.com/xboyrex/SakuraTunes](https://github.com/xboyrex/SakuraTunes)
- **Email**: [xboyrex496@gmail.com](mailto:xboyrex496@gmail.com)
- **Instagram**: [@mayank.zx7](https://www.instagram.com/mayank.zx7)
- **YouTube**: [@mayankdroid](https://youtube.com/@mayankdroid) · [@onyxdroid89](https://youtube.com/@onyxdroid89)
- **Telegram**: [@MayankDroid7](https://t.me/MayankDroid7) · [@OnyxHub7](https://t.me/OnyxHub7)

---

## 📄 License

**Sakura Tunes** is free and open-source software licensed under the **GNU General Public License v3.0 (GPL-3.0)**. See [LICENSE](LICENSE) and [NOTICE.md](NOTICE.md) for full details.
