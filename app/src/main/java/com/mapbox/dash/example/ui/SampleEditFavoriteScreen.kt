package com.mapbox.dash.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.example.R
import com.mapbox.dash.fullscreen.search.favorites.EditFavoriteScreenState

@Composable
fun SampleEditFavoriteScreen(
    modifier: Modifier,
    state: EditFavoriteScreenState,
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(red = 16, green = 18, blue = 23))
            .systemBarsPadding()
            .padding(all = 24.dp)
            .wrapContentHeight(align = Alignment.Top),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Image(
            modifier = Modifier
                .size(128.dp)
                .padding(all = 28.dp)
                .clip(CircleShape)
                .clickable(onClick = state.onBackClicked),
            painter = painterResource(id = R.drawable.ic_full_screen_search_back),
            contentDescription = null,
        )
        val name = rememberSaveable { mutableStateOf(value = state.currentName) }
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            value = name.value,
            onValueChange = { name.value = it },
            textStyle = TextStyle(
                fontSize = 62.sp,
                lineHeight = 80.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
            ),
            cursorBrush = SolidColor(Color.White),
            singleLine = true,
        )
        DoneButton(onClick = { state.onDoneClicked(name.value) })
    }
}

@Composable
private fun DoneButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(128.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Continue",
            fontSize = 42.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White.copy(alpha = 0.8f),
        )
        Image(
            modifier = Modifier.size(56.dp),
            painter = painterResource(R.drawable.ic_edit_favorite_continue),
            contentDescription = null,
        )
    }
}
