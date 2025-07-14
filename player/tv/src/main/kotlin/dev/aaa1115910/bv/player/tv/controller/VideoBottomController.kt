package dev.aaa1115910.bv.player.tv.controller

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
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.runtime.Composable
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
    focusRequester: FocusRequester? = null,
    onClickPlay: () -> Unit = {},
    onClickLike: () -> Unit = {},
    onLongClickLike: () -> Unit = {},
    onClickDanmaku: () -> Unit = {},
    onClickSetting: () -> Unit = {},
    onClickBack: () -> Unit = {},
    onFocusBack: () -> Unit = {},
) {
    val controlItems = remember {
        listOf(
            //暂停/播放
            ControlItemData(
                imageVector = { if (isPlayingLambda()) Icons.Rounded.Pause else Icons.Rounded.PlayArrow },
                onClick = onClickPlay,
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

    var selectedIndex by remember { mutableIntStateOf(0) }

    Row(
        modifier = modifier
            .focusRequester(focusRequester ?: remember { FocusRequester() })
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionLeft -> {
                            selectedIndex =
                                if (selectedIndex > 0) selectedIndex - 1 else controlItems.size - 1
                            true
                        }

                        Key.DirectionRight -> {
                            selectedIndex =
                                if (selectedIndex < controlItems.size - 1) selectedIndex + 1 else 0
                            true
                        }

                        Key.DirectionCenter, Key.Enter -> {
                            controlItems[selectedIndex].onClick()
                            true
                        }

                        Key.DirectionUp,
                        Key.DirectionDown,
                        Key.Back -> {
                            onFocusBack()
                            true
                        }

                        else -> false
                    }
                } else false
            }
    ) {
        controlItems.forEachIndexed { index, item ->
            ControlItem(
                imageVector = item.imageVector(),
                showSlash = item.showSlash(),
                isSelected = selectedIndex == index,
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
                .padding(12.dp, 4.dp)
                .size(40.dp),
            imageVector = imageVector,
            contentDescription = null,
            tint = tint,
        )
        if (showSlash) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .padding(8.dp)
            ) {
                val gap = 15f
                drawLine(
                    color = Color.White,
                    start = Offset(gap, gap),
                    end = Offset(size.width - gap, size.height - gap),
                    strokeWidth = 4f,
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
        text = "${seekData.position.formatMinSec()} / ${seekData.duration.formatMinSec()}",
        color = Color.White
    )
}