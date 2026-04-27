package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import org.jsoup.nodes.Element

class ProtonMovies : MainAPI() { 
    override var mainUrl = "https://protonmovies.com" 
    override var name = "Proton Movies"
    override val hasMainPage = true
    override var lang = "en"
    override val hasQuickSearch = true

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/search?q=$query"
        val document = app.get(url).document
        return document.select(".iq-card").mapNotNull {
            it.toSearchResult()
        }
    }

    private fun Element.toSearchResult(): SearchResponse {
        val title = this.selectFirst(".iq-title a")?.text() ?: ""
        val href = fixUrl(this.selectFirst(".iq-title a")?.attr("href") ?: "")
        val posterUrl = this.selectFirst("img")?.attr("src")
        val isMovie = this.selectFirst(".movie-tag .badge")?.text()?.contains("Movie", true) ?: true
        
        val type = if (isMovie) TvType.Movie else TvType.TvSeries
        
        return newMovieSearchResponse(title, href, type) {
            this.posterUrl = posterUrl
        }
    }
}
