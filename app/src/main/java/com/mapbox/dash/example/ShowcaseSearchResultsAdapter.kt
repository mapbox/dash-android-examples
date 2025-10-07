package com.mapbox.dash.example

import android.util.Log
import com.mapbox.dash.sdk.config.api.QUERY_SEARCH_LIMIT_MAX
import com.mapbox.dash.sdk.search.api.DashCategorySearchOptions
import com.mapbox.dash.sdk.search.api.DashForwardOptions
import com.mapbox.dash.sdk.search.api.DashSearchOptions
import com.mapbox.dash.sdk.search.api.DashSearchRequest
import com.mapbox.dash.sdk.search.api.DashSearchResult
import com.mapbox.dash.sdk.search.api.DashSearchResultType
import com.mapbox.dash.sdk.search.api.DashSearchResultsAdapter
import com.mapbox.dash.sdk.search.api.DashSearchSuggestion
import com.mapbox.geojson.Point

private const val TAG = "CustomSearchAdapter"

@Suppress("MagicNumber", "ForbiddenComment")
object ShowcaseSearchResultsAdapter : DashSearchResultsAdapter {

    private val customSuggestion = object : DashSearchSuggestion {
        override val id: String = "customSuggestionId11fse4w334455"
        override val mapboxId: String? = null
        override val name: String = "Custom adapter search suggestion"
        override val descriptionText = "Custom adapter search suggestion"
        override val address = null
        override val type = DashSearchSuggestion.Type.Point
        override val distanceMeters = 3200.0
        override val categories = null
        override val etaMinutes = 5.0
        override val poweredByAi = false
        override val metadata = mapOf("metadata3" to "value3")
    }

    private fun customSearchResult(index: Int = 1) = object : DashSearchResult {
        override val address = null
        override val coordinate = Point.fromLngLat(-77.0342, 38.9044)
        override val etaMinutes = null
        override val id = "customSearchResultId1122334455"
        override val mapboxId: String? = null
        override val name = "Custom result $index"
        override val customName: String? = null
        override val type = DashSearchResultType.ADDRESS
        override val categories = listOf("test category")
        override val description = null
        override val distanceMeters = null
        override val metadata = mapOf("metadata4" to "value4")
        override val pinCoordinate = Point.fromLngLat(-77.0342, 38.9044)
    }

    override suspend fun searchResults(
        request: DashSearchRequest,
        options: DashCategorySearchOptions,
        results: List<DashSearchResult>,
    ): List<DashSearchResult> {
        Log.d(TAG, "Adapt requested for $request, options: $options, search results: $results Coming soon.")
        val adaptedResults = results.toMutableList()
        repeat(23) {
            adaptedResults.add(it, customSearchResult(it + 1))
        }

        return adaptedResults
    }

    override suspend fun searchResults(
        request: DashSearchRequest,
        options: DashForwardOptions,
        results: List<DashSearchResult>,
    ): List<DashSearchResult> {
        Log.d(TAG, "Adapt requested for $request, options: $options, search results: $results Coming soon.")
        val adaptedResults = results.toMutableList()
        repeat(23) {
            adaptedResults.add(it, customSearchResult(it + 1))
        }

        return adaptedResults
    }

    override suspend fun searchSuggestions(
        request: DashSearchRequest,
        options: DashSearchOptions,
        suggestions: List<DashSearchSuggestion>,
    ): List<DashSearchSuggestion> {
        Log.d(TAG, "Adapt requested for $request, options: $options, suggestions: $suggestions.")
        val adaptedSuggestions = suggestions.toMutableList()
        adaptedSuggestions.add(0, customSuggestion)
        if (adaptedSuggestions.size > QUERY_SEARCH_LIMIT_MAX) {
            adaptedSuggestions.removeAt(adaptedSuggestions.lastIndex)
        }
        return adaptedSuggestions
    }

    override suspend fun searchResult(suggestion: DashSearchSuggestion): DashSearchResult {
        Log.d(TAG, "Retrieve a result for: $suggestion")
        return customSearchResult()
    }
}
