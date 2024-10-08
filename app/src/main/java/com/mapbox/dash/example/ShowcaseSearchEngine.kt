package com.mapbox.dash.example

import com.mapbox.dash.sdk.search.api.DashCategorySearchOptions
import com.mapbox.dash.sdk.search.api.DashSearchEngine
import com.mapbox.dash.sdk.search.api.DashSearchEnginePolicy
import com.mapbox.dash.sdk.search.api.DashSearchOptions
import com.mapbox.dash.sdk.search.api.DashSearchRequest
import com.mapbox.dash.sdk.search.api.DashSearchResult
import com.mapbox.dash.sdk.search.api.DashSearchResultType
import com.mapbox.dash.sdk.search.api.DashSearchSuggestion
import com.mapbox.geojson.Point

@Suppress("MagicNumber", "ForbiddenComment")
class ShowcaseSearchEngine(
    override val processingPolicyFlag: Int = DashSearchEnginePolicy.ORIGIN_ENGINES_RESULTS
) : DashSearchEngine {

    override suspend fun search(
        request: DashSearchRequest,
        options: DashSearchOptions,
    ): List<DashSearchSuggestion> = listOf(customSuggestion)

    override suspend fun categorySearch(
        searchRequest: DashSearchRequest,
        displayName: String,
        options: DashCategorySearchOptions,
    ): List<DashSearchResult> = listOf(customSearchResult)

    private companion object {
        val customSuggestion = object : DashSearchSuggestion {
            override val id: String = "customSuggestionId11fse4w334455"
            override val mapboxId: String? = null
            override val metadata: Map<String, String>? = null
            override val name: String = "Custom suggestion"
            override val descriptionText = "Suggestion provided by a third-party service"
            override val address = null
            override val distanceMeters = 3200.0
            override val categories = null
            override val etaMinutes = 5.0
            override val poweredByAi = false
            override val type: DashSearchSuggestion.Type = DashSearchSuggestion.Type.Point
        }

        private val customSearchResult = object : DashSearchResult {
            override val address = null
            override val coordinate = Point.fromLngLat(-77.0342, 38.9044)
            override val customName: String? = null
            override val etaMinutes = null
            override val id = "customSearchResultId1122334455"
            override val mapboxId: String? = null
            override val metadata: Map<String, String>? = emptyMap()
            override val name = "The search result for the custom suggestion"
            override val pinCoordinate: Point = coordinate
            override val type = DashSearchResultType.ADDRESS
            override val categories = listOf("test category")
            override val description = null
            override val distanceMeters = null
        }
    }
}
