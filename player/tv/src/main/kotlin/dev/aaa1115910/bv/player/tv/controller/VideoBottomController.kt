package dev.aaa1115910.bv.player.tv.controller

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekData
import dev.aaa1115910.bv.util.formatHourMinSec
import dev.aaa1115910.bv.util.formatMinSec

/**
 * @author LiCheng
 * @date 2025/6/4
 * @desc
 */
@Composable
fun VideoBottomController(
    modifier: Modifier = Modifier,
    seekData: VideoPlayerSeekData,
    isPlayingLambda: () -> Boolean,
    isShowDanmakuLambda: () -> Boolean,
    isLikedLambda: () -> Boolean,
    focusRequester: FocusRequester,
    isFocused: Boolean,
    onClickPlay: () -> Unit = {},
    onClickLike: () -> Unit = {},
    onLongClickLike: () -> Unit = {},
    onClickDanmaku: () -> Unit = {},
    onClickSetting: () -> Unit = {},
    onClickBack: () -> Unit = {},
    onFocusBack: () -> Unit = {},
    onFocusUp: () -> Unit = {},
    onClickVideoInfo: () -> Unit = {},
    onClickUserInfo: () -> Unit = {},
    onKeyClicked: (Boolean) -> Unit = {},
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val controlItems = remember {
        listOf(
            //暂停/播放
            ControlItemData(
                imageVector = { if (isPlayingLambda()) Icons.Rounded.Pause else Icons.Rounded.PlayArrow },
                onClick = {
                    Log.d("TAG", "VideoBottomController onFirst button clicked, index: $selectedIndex")
                    onClickPlay()
                },
                showSlash = { false }
            ),
            //弹幕
            ControlItemData(
                imageVector = { Icons.Outlined.ClearAll },
                onClick = onClickDanmaku,
                showSlash = { !isShowDanmakuLambda() }
            ),
            //点赞
            ControlItemData(
                imageVector = { if (isLikedLambda()) Icons.Rounded.ThumbUp else Icons.Outlined.ThumbUp },
                tint = { if (isLikedLambda()) Color(0xfffb7299) else Color.Gray },
                onClick = onClickLike,
                onLongClick = onLongClickLike,
                showSlash = { false }
            ),
            //视频信息
            ControlItemData(
                imageVector = { Icons.Rounded.Info },
                onClick = onClickVideoInfo,
                showSlash = { false }
            ),
            //up主页
            ControlItemData(
                imageVector = { Icons.Rounded.Person },
                onClick = onClickUserInfo,
                showSlash = { false }
            ),
            //设置
            ControlItemData(
                imageVector = { Icons.Rounded.Settings },
                onClick = onClickSetting,
                showSlash = { false }
            ),
            //关闭
            ControlItemData(
                imageVector = { Icons.Rounded.Close },
                onClick = onClickBack,
                showSlash = { false }
            )
        )
    }

    Row(
        modifier = modifier
            .focusRequester(focusRequester)
            .onKeyEvent { keyEvent ->
                Log.d(
                    "TAG",
                    "VideoBottomController onKeyEvent type: ${keyEvent.type}, key: ${keyEvent.key},isFocused:$isFocused"
                )
                onKeyClicked(keyEvent.type == KeyEventType.KeyUp)
                if (!isFocused) return@onKeyEvent false
                when (keyEvent.key) {
                    Key.DirectionLeft -> {
                        if (keyEvent.type == KeyEventType.KeyDown) return@onKeyEvent true
                        selectedIndex =
                            if (selectedIndex > 0) selectedIndex - 1 else controlItems.size - 1
                        true
                    }

                    Key.DirectionRight -> {
                        if (keyEvent.type == KeyEventType.KeyDown) return@onKeyEvent true
                        selectedIndex =
                            if (selectedIndex < controlItems.size - 1) selectedIndex + 1 else 0
                        Log.d(
                            "TAG",
                            "VideoBottomController button right selectedIndex: $selectedIndex"
                        )
                        true
                    }

                    Key.DirectionCenter, Key.Enter -> {
                        if (keyEvent.type == KeyEventType.KeyDown) return@onKeyEvent true
                        Log.d("TAG", "VideoBottomController button selectedIndex: $selectedIndex")
                        controlItems[selectedIndex].onClick()
                        true
                    }

                    Key.DirectionUp -> {
                        if (keyEvent.type == KeyEventType.KeyDown) return@onKeyEvent true
                        onFocusUp()
                        true
                    }

                    Key.DirectionDown -> {
                        if (keyEvent.type == KeyEventType.KeyDown) return@onKeyEvent true
                        onFocusBack()
                        true
                    }

                    Key.Back -> {
                        if (keyEvent.type == KeyEventType.KeyDown) return@onKeyEvent true
                        onFocusBack()
                        true
                    }

                    else -> false
                }
            }
    ) {
        controlItems.forEachIndexed { index, item ->
            val isItemSelected by remember(selectedIndex) {
                derivedStateOf { selectedIndex == index }
            }
            ControlItem(
                imageVector = item.imageVector(),
                showSlash = item.showSlash(),
                isSelected = isFocused && isItemSelected,
                onClick = item.onClick,
                tint = item.tint(),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TimeInfo(
            seekData = seekData
        )
    }
}

private data class ControlItemData(
    val imageVector: () -> ImageVector,
    val onClick: () -> Unit,
    val showSlash: () -> Boolean,
    val onLongClick: () -> Unit = {},
    val tint: () -> Color = { Color.White },
)

@Composable
private fun ControlItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    showSlash: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    tint: Color,
) {
    Surface(
        modifier = modifier
            .padding(end = 10.dp)
            .focusable()
            .clickable {
                onClick()
            },
        colors = SurfaceDefaults.colors(
            containerColor = if (isSelected) Color.White.copy(0.5f) else Color.Black.copy(0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            modifier = Modifier
                .padding(6.dp)
                .size(30.dp),
            imageVector = imageVector,
            contentDescription = null,
            tint = tint,
        )
        if (showSlash) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .padding(6.dp)
            ) {
                val gap = 12f
                drawLine(
                    color = Color.White,
                    start = Offset(gap, gap),
                    end = Offset(size.width - gap, size.height - gap),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun TimeInfo(
    modifier: Modifier = Modifier,
    seekData: VideoPlayerSeekData,
) {
    Text(
        modifier = modifier.padding(top = 16.dp, bottom = 0.dp, end = 40.dp),
        text = "${seekData.position.formatMinSec()} / ${seekData.duration.formatHourMinSec()}",
        color = Color.White
    )
}