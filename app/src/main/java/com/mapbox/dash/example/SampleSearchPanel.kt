package com.mapbox.dash.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mapbox.dash.driver.presentation.search.SearchPanelState
import com.mapbox.dash.sdk.Dash

@Composable
fun SampleSearchPanel(modifier: Modifier, state: SearchPanelState) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(red = 16, green = 18, blue = 23))
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(128.dp)
                .padding(all = 28.dp)
                .clickable { Dash.controller.openSearch(searchQuery = null) },
            painter = painterResource(id = R.drawable.ic_search_panel_query),
            contentDescription = null,
        )
        Spacer(
            modifier = Modifier
                .width(2.dp)
                .height(80.dp)
                .background(Color(red = 72, green = 72, blue = 74)),
        )
        Image(
            modifier = Modifier
                .size(128.dp)
                .padding(all = 28.dp)
                .clickable(onClick = state.onHomeClicked),
            painter = painterResource(id = R.drawable.ic_search_panel_home),
            contentDescription = null,
        )
        Spacer(
            modifier = Modifier
                .width(2.dp)
                .height(80.dp)
                .background(Color(red = 72, green = 72, blue = 74)),
        )
        Image(
            modifier = Modifier
                .size(128.dp)
                .padding(all = 28.dp)
                .clickable(onClick = state.onWorkClicked),
            painter = painterResource(id = R.drawable.ic_search_panel_work),
            contentDescription = null,
        )
    }
}
