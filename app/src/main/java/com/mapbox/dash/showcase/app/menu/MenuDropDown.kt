package com.mapbox.dash.showcase.app.menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.mapbox.dash.sdk.DashNavigationFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun MenuDropDown(
    options: List<String>,
    initial: String,
    dashNavigationFragmentFlow: Flow<DashNavigationFragment?>,
    onValueChange: (DashNavigationFragment, String) -> Unit,
    label: String,
) {
    MenuDropDown(
        options = options,
        initial = initial,
        onValueChange = { value ->
            dashNavigationFragmentFlow.filterNotNull().collect { dashNavigationFragment ->
                onValueChange(dashNavigationFragment, value)
            }
        },
        label = label,
    )
}

@Composable
fun MenuDropDown(
    options: List<String>,
    initial: String,
    onValueChange: suspend (String) -> Unit,
    label: String,
) {
    val state = rememberSaveable(initial) { mutableStateOf(initial) }
    LaunchedEffect(state.value) {
        onValueChange(state.value)
    }
    MenuDropDown(
        options = options,
        state = state,
        label = label,
    )
}

@Composable
fun <T> MenuDropDown(
    options: List<String>,
    flow: StateFlow<T>,
    transform: (T) -> String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    MenuDropDown(
        options = options,
        value = transform(flow.collectAsState().value),
        onValueChange = onValueChange,
        label = label,
    )
}

@Composable
fun MenuDropDown(
    options: List<String>,
    state: MutableState<String>,
    label: String,
) {
    MenuDropDown(
        options = options,
        value = state.value,
        onValueChange = { state.value = it },
        label = label,
    )
}

@Suppress("MagicNumber")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuDropDown(
    options: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    val expanded = rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it },
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    modifier = Modifier.rotate(animateFloatAsState(if (expanded.value) 180f else 0f).value),
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            for (option in options) {
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded.value = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
