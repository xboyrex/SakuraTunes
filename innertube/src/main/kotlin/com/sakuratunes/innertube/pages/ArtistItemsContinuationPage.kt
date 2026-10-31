package com.sakuratunes.innertube.pages

import com.sakuratunes.innertube.models.YTItem

data class ArtistItemsContinuationPage(
    val items: List<YTItem>,
    val continuation: String?,
)
