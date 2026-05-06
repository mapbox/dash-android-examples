package com.mapbox.dash.showcase.app

import androidx.lifecycle.ViewModel
import com.mapbox.dash.sdk.search.api.DashSearchEnginePolicy
import kotlinx.coroutines.flow.MutableStateFlow

class SearchViewModel : ViewModel() {

    private val valuesToNamesMapping = mapOf(
        DashSearchEnginePolicy.ORIGIN_ENGINES_RESULTS to "ORIGIN_ENGINES_RESULTS",
        DashSearchEnginePolicy.CUSTOM_ENGINE_RESULTS to "CUSTOM_ENGINE_RESULTS",
        DashSearchEnginePolicy.ORIGIN_ENGINES_RESULTS or DashSearchEnginePolicy.CUSTOM_ENGINE_RESULTS to "COMBINED",
    )
    val allPolicies = valuesToNamesMapping.values.toTypedArray()

    val selectedPolicyName = MutableStateFlow(policyToName(ShowcaseSearchEngine.processingPolicyFlag))

    fun setPolicy(name: String) {
        nameToPolicy(name)?.let { ShowcaseSearchEngine.processingPolicyFlag = it }
    }

    private fun policyToName(@DashSearchEnginePolicy.Type policy: Int): String {
        return valuesToNamesMapping[policy] ?: "UNKNOWN"
    }

    private fun nameToPolicy(name: String) =
        valuesToNamesMapping.entries.firstOrNull { it.value == name }?.key
}
