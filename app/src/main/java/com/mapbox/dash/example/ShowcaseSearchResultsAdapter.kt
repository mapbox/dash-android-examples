package com.mapbox.dash.example

import android.util.Log
import com.mapbox.dash.sdk.search.DashCategorySearchOptions
import com.mapbox.dash.sdk.search.DashSearchOptions
import com.mapbox.dash.sdk.search.DashSearchRequest
import com.mapbox.dash.sdk.search.DashSearchResult
import com.mapbox.dash.sdk.search.DashSearchResultType
import com.mapbox.dash.sdk.search.DashSearchResultsAdapter
import com.mapbox.dash.sdk.search.DashSearchSuggestion
import com.mapbox.geojson.Point

private const val TAG = "CustomSearchAdapter"

@Suppress("MagicNumber", "ForbiddenComment")
class ShowcaseSearchResultsAdapter : DashSearchResultsAdapter {

    private val customSuggestion = object : DashSearchSuggestion {
        override val id: String = "customSuggestionId1234567890"
        override val mapboxId: String? = null
        override val name: String = "Custom suggestion"
        override val poweredByAi = false
        override val descriptionText = "Suggestion provided by a third-party service"
        override val address = null
        override val distanceMeters = 3200.0
        override val categories = null
        override val etaMinutes = 5.0
    }

    private val customSearchResult = object : DashSearchResult {
        override val address = null
        override val coordinate = Point.fromLngLat(-77.0342, 38.9044)
        override val etaMinutes = null
        override val id = "customSearchResultId1122334455"
        override val mapboxId: String? = null
        override val metadata: Map<String, String>? = emptyMap()
        override val name = "The search result for the custom suggestion"
        override val type = DashSearchResultType.ADDRESS
        override val categories = listOf("Food")
        override val description = null
        override val distanceMeters = null
        override val favoriteType = null
    }

    override suspend fun searchResults(
        request: DashSearchRequest,
        options: DashCategorySearchOptions,
        results: List<DashSearchResult>,
    ): List<DashSearchResult> {
        Log.d(TAG, "Adapt requested for $request, options: $options, search results: $results Coming soon.")
        val adaptedResults = results.toMutableList()
        adaptedResults.add(0, customSearchResult)
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
        return adaptedSuggestions
    }

    override suspend fun searchResult(suggestion: DashSearchSuggestion): DashSearchResult {
        Log.d(TAG, "Retrieve a result for: $suggestion")
        return customSearchResult
    }
}
