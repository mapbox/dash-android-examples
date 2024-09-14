package com.mapbox.dash.example

import com.mapbox.dash.models.Category
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
    override val processingPolicyFlag: Int = DashSearchEnginePolicy.CUSTOM_ENGINE_RESULTS
) : DashSearchEngine {

    override suspend fun search(
        request: DashSearchRequest,
        options: DashSearchOptions,
    ): List<DashSearchSuggestion> = listOf(customSuggestion, customSuggestion2, customSuggestion3)

    override suspend fun categorySearch(
        searchRequest: DashSearchRequest,
        displayName: String,
        options: DashCategorySearchOptions,
    ): List<DashSearchResult> = listOf(customSearchResult, customSearchResult2)

    private companion object {
        val customSuggestion = object : DashSearchSuggestion {
            override val id: String = "customSuggestionId11fse4w33445566"
            override val mapboxId: String? = null
            override val metadata: Map<String, String>? = null
            override val name: String = "Custom suggestion 1"
            override val descriptionText = "Suggestion provided by a third-party service"
            override val address = null
            override val distanceMeters = 3200.0
            override val categories = null
            override val etaMinutes = 5.0
            override val poweredByAi = false
        }
        val customSuggestion2 = object : DashSearchSuggestion {
            override val id: String = "customSuggestionId11fse4w33445577"
            override val mapboxId: String? = null
            override val metadata: Map<String, String>? = null
            override val name: String = "Custom suggestion 2"
            override val descriptionText = "Suggestion provided by a third-party service"
            override val address = null
            override val distanceMeters = 4200.0
            override val categories = null
            override val etaMinutes = 7.0
            override val poweredByAi = false
        }
        val customSuggestion3 = object : DashSearchSuggestion {
            override val id: String = "customSuggestionId11fse4w33445588"
            override val mapboxId: String? = null
            override val metadata: Map<String, String>? = null
            override val name: String = "Custom suggestion 3"
            override val descriptionText = "Suggestion provided by a third-party service"
            override val address = null
            override val distanceMeters = 8800.0
            override val categories = null
            override val etaMinutes = 10.0
            override val poweredByAi = false
        }

        private val customSearchResult = object : DashSearchResult {
            override val address = null
            override val coordinate = Point.fromLngLat(-77.0342, 38.9044)
            override val etaMinutes = 4.5
            override val id = "customSearchResultId1122334455"
            override val mapboxId: String? = null
            override val metadata: Map<String, String>? = emptyMap()
            override val name = "Third-party search result"
            override val pinCoordinate: Point = coordinate
            override val type = DashSearchResultType.ADDRESS
            override val categories = listOf(Category.Food.name)
            override val description = "Great pizza place"
            override val distanceMeters = 950.0
        }

        private val customSearchResult2 = object : DashSearchResult {
            override val address = null
            override val coordinate = Point.fromLngLat(-77.0372, 38.9094)
            override val etaMinutes = 5.6
            override val id = "customSearchResultId112233445566"
            override val mapboxId: String? = null
            override val metadata: Map<String, String>? = emptyMap()
            override val name = "Another third-party search result"
            override val pinCoordinate: Point = coordinate
            override val type = DashSearchResultType.POI
            override val categories = listOf(Category.Food.name)
            override val description = "Another fantastic pizza place"
            override val distanceMeters = 1700.0
        }
    }
}
