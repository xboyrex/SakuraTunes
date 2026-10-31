package com.sakuratunes.innertube.pages

import com.sakuratunes.innertube.models.SongItem

data class PlaylistContinuationPage(
    val songs: List<SongItem>,
    val continuation: String?,
)
