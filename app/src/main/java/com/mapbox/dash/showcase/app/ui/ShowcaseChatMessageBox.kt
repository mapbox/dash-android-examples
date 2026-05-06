package com.mapbox.dash.showcase.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.navigation.mapgpt.MapGptCompose

@Composable
internal fun ShowcaseChatMessageBox(
    message: MutableState<TextFieldValue> = remember { mutableStateOf(TextFieldValue()) },
    onSendClick: (String) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Black)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        InputField(
            message = message,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
        )
        SendButton(
            modifier = Modifier.align(Alignment.Bottom),
            message = message,
            onSendClick = onSendClick,
        )
    }
}

@Composable
private fun InputField(
    message: MutableState<TextFieldValue>,
    modifier: Modifier,
) {

    val textStyle = TextStyle(fontSize = 18.sp, color = Color.White)
    BasicTextField(
        value = message.value,
        onValueChange = { message.value = it },
        textStyle = textStyle,
        cursorBrush = SolidColor(Color(color = 0xFF6998FD)),
        modifier = modifier
            .background(
                color = Color.DarkGray,
                shape = RoundedCornerShape(16.dp),
            )
            .heightIn(min = 40.dp)
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        decorationBox = { innerTextField ->
            if (message.value.text.isEmpty()) {
                Text(
                    text = "Message",
                    color = Color.LightGray,
                    style = textStyle,
                )
            }
            innerTextField()
        },
        maxLines = 5,
    )
}

@Composable
private fun SendButton(
    modifier: Modifier = Modifier,
    message: MutableState<TextFieldValue>,
    onSendClick: (String) -> Unit = {},
) {
    val enabled = message.value.text.isNotEmpty()
    Image(
        modifier = modifier
            .clip(CircleShape)
            .background(
                color = if (enabled) {
                    Color.Green
                } else {
                    Color.LightGray
                },
            )
            .size(40.dp)
            .clickable(enabled = enabled) {
                onSendClick(message.value.text)
                message.value = TextFieldValue()
            }
            .padding(all = 10.dp),
        painter = painterResource(id = android.R.drawable.ic_menu_send),
        contentDescription = null,
        alpha = if (enabled) 1f else 0.3f,
    )
}

@Preview
@Composable
internal fun Preview_ShowcaseChatMessageBox_WithoutText() = MapGptCompose().PreviewTheme {
    ShowcaseChatMessageBox()
}

@Preview
@Composable
internal fun Preview_ShowcaseChatMessageBox_WithText() = MapGptCompose().PreviewTheme {
    val text = "What's the weather like today? And please tell me the forecast for the rest of the week."
    ShowcaseChatMessageBox(message = remember { mutableStateOf(TextFieldValue(text)) })
}
