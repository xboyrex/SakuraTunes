package com.sakuratunes.innertube.pages

import com.sakuratunes.innertube.models.YTItem

data class LibraryContinuationPage(
    val items: List<YTItem>,
    val continuation: String?,
)
