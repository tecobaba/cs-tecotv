// ! TecoTV Film

package com.tecotv

import android.util.Log
import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer

class IPTVPlugin : MainAPI() {
    override var mainUrl = "https://tinyurl.com/2bhf2qox"
    override var name = "IPTV M3U Player"
    override val supportedTypes = setOf(TvType.Movie)
    override val hasMainPage = true

    override val mainPage = mainPageOf(
        mainUrl to "IPTV Kanallarý"
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val response = app.get(mainUrl).text
        val lines = response.lines()
        val channels = mutableListOf<SearchResponse>()
        var currentTitle = ""
        for (line in lines) {
            when {
                line.startsWith("#EXTINF") -> {
                    currentTitle = line.substringAfter(",").trim()
                }
                line.startsWith("http") -> {
                    val url = line.trim()
                    channels.add(newMovieSearchResponse(currentTitle, url, TvType.Movie) {
                        posterUrl = null
                    })
                }
            }
        }
        return newHomePageResponse(request.name, channels)
    }

    override suspend fun load(url: String): LoadResponse? {
        return newMovieLoadResponse(url, url, TvType.Movie, url)
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        callback.invoke(ExtractorLink(data, "IPTV Kanalý", data))
        return true
    }
}
