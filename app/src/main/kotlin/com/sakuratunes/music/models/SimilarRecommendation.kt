/**
 * Sakura Tunes Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.sakuratunes.music.models

import com.sakuratunes.innertube.models.YTItem
import com.sakuratunes.music.db.entities.LocalItem

data class SimilarRecommendation(
    val title: LocalItem,
    val items: List<YTItem>,
)
