package com.mapbox.dash.showcase.app

import com.mapbox.dash.sdk.search.api.DashCategorySearchOptions
import com.mapbox.dash.sdk.search.api.DashForwardOptions
import com.mapbox.dash.sdk.search.api.DashSearchEngine
import com.mapbox.dash.sdk.search.api.DashSearchEnginePolicy
import com.mapbox.dash.sdk.search.api.DashSearchOptions
import com.mapbox.dash.sdk.search.api.DashSearchRequest
import com.mapbox.dash.sdk.search.api.DashSearchResult
import com.mapbox.dash.sdk.search.api.DashSearchResultType
import com.mapbox.dash.sdk.search.api.DashSearchSuggestion
import com.mapbox.geojson.Point

@Suppress("MagicNumber")
object ShowcaseSearchEngine : DashSearchEngine {

    override var processingPolicyFlag: Int = DashSearchEnginePolicy.CUSTOM_ENGINE_RESULTS

    override suspend fun search(
        request: DashSearchRequest,
        options: DashSearchOptions,
    ): List<DashSearchSuggestion> = listOf(customSuggestion)

    override suspend fun forward(
        request: DashSearchRequest,
        options: DashForwardOptions,
    ): List<DashSearchResult> = listOf(customSearchResult)

    override suspend fun categorySearch(
        searchRequest: DashSearchRequest,
        displayName: String,
        options: DashCategorySearchOptions,
    ): List<DashSearchResult> = listOf(customSearchResult)

    private val customSuggestion = object : DashSearchSuggestion {
        override val id: String = "customSuggestionId11fse4w334455"
        override val mapboxId: String? = null
        override val name: String = "Custom Engine search suggestion"
        override val descriptionText = "Custom Engine search suggestion"
        override val address = null
        override val type = DashSearchSuggestion.Type.Point
        override val distanceMeters = 3200.0
        override val categories = listOf("Test Category")
        override val categoryIds = listOf("test_category")
        override val etaMinutes = 5.0
        override val poweredByAi = false
        override val metadata = mapOf("metadata1" to "value1")
    }

    private val customSearchResult = object : DashSearchResult {
        override val address = null
        override val coordinate = Point.fromLngLat(-77.0342, 38.9044)
        override val etaMinutes = null
        override val id = "customSearchResultId1122334455"
        override val mapboxId: String? = null
        override val name = "Custom Engine search result"
        override val customName: String? = null
        override val type = DashSearchResultType.ADDRESS
        override val categories = listOf("Test Category")
        override val categoryIds = listOf("test_category")
        override val description = null
        override val distanceMeters = null
        override val pinCoordinate = Point.fromLngLat(-77.0342, 38.9044)
        override val metadata = mapOf("metadata2" to "value2")
    }
}
