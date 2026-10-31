/**
 * Sakura Tunes Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.sakuratunes.music.listentogether

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ListenTogetherServer(
    val name: String,
    val url: String,
    val location: String,
    val operator: String
)

object ListenTogetherServers {
    private const val ServersJson = """
        [
          {
            "name": "Sakura Tunes Server",
            "url": "wss://listen.sakuratunes.app/ws",
            "location": "Global",
            "operator": "Sakura Tunes Studio"
          }
        ]
    """

    private val json = Json { ignoreUnknownKeys = true }

    val servers: List<ListenTogetherServer> by lazy {
        json.decodeFromString(ServersJson)
    }

    val defaultServerUrl: String
        get() = servers.first().url

    fun findByUrl(url: String): ListenTogetherServer? = servers.firstOrNull { it.url == url }
}
