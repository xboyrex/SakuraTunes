/**
 * Sakura Tunes Project (C) 2026
 * Crafted by ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく (@xboyrex)
 * Licensed under GPL-3.0
 */

package com.sakuratunes.music.utils

import com.sakuratunes.music.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

data class ReleaseInfo(
    val tagName: String,
    val versionName: String,
    val description: String,
    val releaseDate: String,
    val assets: List<ReleaseAsset>,
)

data class ReleaseAsset(
    val name: String,
    val downloadUrl: String,
    val size: Long,
    val architecture: String,
    val variant: String, // "foss" or "gms"
)

object Updater {
    private val client = HttpClient()
    var lastCheckTime = -1L
        private set

    private var cachedReleaseInfo: ReleaseInfo? = null
    private var cachedAllReleases: List<ReleaseInfo> = emptyList()

    private const val CHECK_INTERVAL_MILLIS = 2 * 60 * 60 * 1000L // 2 hours
    private const val GITHUB_API_BASE = "https://api.github.com/repos/xboyrex/SakuraTunes"
    const val GITHUB_RELEASES_URL = "https://github.com/xboyrex/SakuraTunes/releases"

    private fun extractSemver(raw: String): String {
        val match = Regex("""(\d+\.\d+\.\d+)""").find(raw)
        return match?.groupValues?.get(1) ?: raw.removePrefix("v").trim()
    }

    /**
     * Compares two version strings.
     * Returns: 1 if v1 > v2, -1 if v1 < v2, 0 if equal
     */
    fun compareVersions(v1: String, v2: String): Int {
        val clean1 = extractSemver(v1)
        val clean2 = extractSemver(v2)
        val v1Parts = clean1.split(".").map { it.toIntOrNull() ?: 0 }
        val v2Parts = clean2.split(".").map { it.toIntOrNull() ?: 0 }
        val maxLength = maxOf(v1Parts.size, v2Parts.size)

        for (i in 0 until maxLength) {
            val part1 = v1Parts.getOrNull(i) ?: 0
            val part2 = v2Parts.getOrNull(i) ?: 0
            when {
                part1 > part2 -> return 1
                part1 < part2 -> return -1
            }
        }
        return 0
    }

    /**
     * Checks if the latest version is newer than the current version.
     */
    fun isUpdateAvailable(currentVersion: String, latestVersion: String): Boolean {
        return compareVersions(latestVersion, currentVersion) > 0
    }

    private fun getCurrentAppVariant(): Pair<String, String> {
        val architecture = BuildConfig.ARCHITECTURE
        val variant = if (BuildConfig.CAST_AVAILABLE) "gms" else "foss"
        return architecture to variant
    }

    private fun parseAssets(assetsArray: JSONArray): List<ReleaseAsset> {
        val assets = mutableListOf<ReleaseAsset>()

        for (i in 0 until assetsArray.length()) {
            val asset = assetsArray.getJSONObject(i)
            val name = asset.optString("name", "")
            if (!name.endsWith(".apk", ignoreCase = true)) continue

            val downloadUrl = asset.optString("browser_download_url", "")
            val size = asset.optLong("size", 0L)

            val (arch, variant) = when {
                name.contains("Google-Cast", ignoreCase = true) || name.contains("gms", ignoreCase = true) ->
                    "universal" to "gms"
                name.contains("SakuraTunes", ignoreCase = true) || name.endsWith("-release.apk", ignoreCase = true) ->
                    "universal" to "foss"
                else -> "universal" to "gms"
            }

            if (downloadUrl.isNotBlank()) {
                assets.add(ReleaseAsset(name, downloadUrl, size, arch, variant))
            }
        }

        return assets
    }

    private fun createFallbackCurrentRelease(): ReleaseInfo = ReleaseInfo(
        tagName = "v${BuildConfig.BASE_VERSION_NAME}",
        versionName = BuildConfig.BASE_VERSION_NAME,
        description = "### 🌸 Sakura Tunes v${BuildConfig.BASE_VERSION_NAME} — Liquid Glass Edition\n- Real-time Liquid Glass optical shader engine\n- Floating Liquid Capsule Navigation Bar\n- Liquid Glass Studio & Quick Storage Analyzer\n- Crafted by ㅤᏞᴀᴢʏㅤᎷꫝʏᴀɴк まやんく (@xboyrex)",
        releaseDate = "Latest",
        assets = emptyList(),
    )

    suspend fun getLatestRelease(forceRefresh: Boolean = false): Result<ReleaseInfo> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (cachedReleaseInfo != null && !forceRefresh) {
                    return@runCatching cachedReleaseInfo!!
                }

                val httpResponse = client.get("$GITHUB_API_BASE/releases/latest") {
                    header("User-Agent", "SakuraTunes-Android/${BuildConfig.BASE_VERSION_NAME}")
                    header("Accept", "application/vnd.github+json")
                }

                if (!httpResponse.status.isSuccess()) {
                    // If no GitHub release is published yet (404), gracefully report current version
                    val fallback = createFallbackCurrentRelease()
                    cachedReleaseInfo = fallback
                    lastCheckTime = System.currentTimeMillis()
                    return@runCatching fallback
                }

                val response = httpResponse.bodyAsText()
                val json = JSONObject(response)
                if (!json.has("tag_name")) {
                    val fallback = createFallbackCurrentRelease()
                    cachedReleaseInfo = fallback
                    lastCheckTime = System.currentTimeMillis()
                    return@runCatching fallback
                }

                val rawTag = json.optString("tag_name", "v${BuildConfig.BASE_VERSION_NAME}")
                val cleanVersion = extractSemver(rawTag.ifBlank { json.optString("name", BuildConfig.BASE_VERSION_NAME) })

                val releaseInfo = ReleaseInfo(
                    tagName = rawTag,
                    versionName = cleanVersion,
                    description = json.optString("body", "").ifBlank { "Sakura Tunes v$cleanVersion" },
                    releaseDate = json.optString("published_at", ""),
                    assets = if (json.has("assets")) parseAssets(json.getJSONArray("assets")) else emptyList(),
                )

                cachedReleaseInfo = releaseInfo
                lastCheckTime = System.currentTimeMillis()
                releaseInfo
            }
        }

    suspend fun getAllReleases(forceRefresh: Boolean = false): Result<List<ReleaseInfo>> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (cachedAllReleases.isNotEmpty() && !forceRefresh) {
                    return@runCatching cachedAllReleases
                }

                val releases = mutableListOf<ReleaseInfo>()
                val httpResponse = client.get("$GITHUB_API_BASE/releases?page=1&per_page=20") {
                    header("User-Agent", "SakuraTunes-Android/${BuildConfig.BASE_VERSION_NAME}")
                    header("Accept", "application/vnd.github+json")
                }

                if (httpResponse.status.isSuccess()) {
                    val json = JSONArray(httpResponse.bodyAsText())
                    for (i in 0 until json.length()) {
                        val releaseObj = json.getJSONObject(i)
                        val rawTag = releaseObj.optString("tag_name", "")
                        val cleanVersion = extractSemver(rawTag.ifBlank { releaseObj.optString("name", BuildConfig.BASE_VERSION_NAME) })
                        releases.add(
                            ReleaseInfo(
                                tagName = rawTag,
                                versionName = cleanVersion,
                                description = releaseObj.optString("body", ""),
                                releaseDate = releaseObj.optString("published_at", ""),
                                assets = if (releaseObj.has("assets")) parseAssets(releaseObj.getJSONArray("assets")) else emptyList(),
                            )
                        )
                    }
                }

                if (releases.isEmpty()) {
                    releases.add(createFallbackCurrentRelease())
                }

                cachedAllReleases = releases
                releases
            }
        }

    fun getDownloadUrlForCurrentVariant(releaseInfo: ReleaseInfo): String? {
        val (currentArch, currentVariant) = getCurrentAppVariant()
        return releaseInfo.assets
            .find { it.architecture == currentArch && it.variant == currentVariant }
            ?.downloadUrl
            ?: releaseInfo.assets.firstOrNull()?.downloadUrl
            ?: GITHUB_RELEASES_URL
    }

    fun getAllDownloadUrls(releaseInfo: ReleaseInfo): Map<String, String> {
        return releaseInfo.assets.associate { "${it.architecture}-${it.variant}" to it.downloadUrl }
    }

    suspend fun checkForUpdate(forceRefresh: Boolean = false): Result<Pair<ReleaseInfo?, Boolean>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val shouldFetch = forceRefresh ||
                    (System.currentTimeMillis() - lastCheckTime) > CHECK_INTERVAL_MILLIS

                if (!shouldFetch && cachedReleaseInfo != null) {
                    val hasUpdate = isUpdateAvailable(
                        BuildConfig.BASE_VERSION_NAME,
                        cachedReleaseInfo!!.versionName,
                    )
                    return@runCatching cachedReleaseInfo!! to hasUpdate
                }

                val result = getLatestRelease(forceRefresh = true)
                val releaseInfo = result.getOrElse { createFallbackCurrentRelease() }
                val hasUpdate = isUpdateAvailable(
                    BuildConfig.BASE_VERSION_NAME,
                    releaseInfo.versionName,
                )
                releaseInfo to hasUpdate
            }
        }

    fun getLatestDownloadUrl(): String? {
        return cachedReleaseInfo?.let { getDownloadUrlForCurrentVariant(it) } ?: GITHUB_RELEASES_URL
    }

    fun getCachedLatestRelease(): ReleaseInfo? = cachedReleaseInfo
}
