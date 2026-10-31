---v2.0.0
### 🌸 Sakura Tunes v2.0.0 — Liquid Glass Edition

#### ✨ Liquid Glass Engine & Visual Overhaul
- **Real-Time Liquid Glass Shader Pipeline**: Added full refractive lens shaders, Gaussian frost blur backdrop (`LocalPageGlassBackdrop`), chromatic dispersion, and interactive touch highlights across navigation, miniplayer, player controls, filter chips, and sheets.
- **Liquid Glass Studio (Settings)**: Added real-time optical tuning controls in Settings — switch between **High**, **Balanced**, and **Lite** quality modes, and customize **Blur Radius** (`4dp–42dp`), **Refraction Amount** (`0%–100%`), **Lens Height** (`0dp–36dp`), and **Chromatic Dispersion** (`0%–100%`).
- **Floating Liquid Capsule Navigation Bar**: Replaced standard bottom navigation with `LiquidBottomTabs` featuring physics-driven damped drag pill animations (`DampedDragAnimation`), spring scale magnification, and haptic feedback.
- **Redesigned Settings Hub & Quick Storage Analyzer**: Added interactive Liquid Glass hero banner, Quick Storage Analyzer sheet with visual storage breakdown and one-tap cache cleaning, and **Rounded Heavy Typography** toggle (`SakuraRoundedHeavy`).
- **Custom Sakura Tunes Branding & Iconography**: All-new Sakura Blossom adaptive launcher icons, custom vector/SVG icons across the entire UI, and Sakura Pink (`#FF6BA0`) expressive color presets.
- **Automated Zero-Config CI/CD**: Updated GitHub Actions workflows (`build.yml`, `release.yml`, `build_pr.yml`, `build_quick.yml`) for automatic APK compilation, persistent keystore generation/signing, and automated GitHub Releases.
