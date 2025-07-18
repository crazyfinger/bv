package dev.aaa1115910.bv.player.tv.controller

import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerClockData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerSeekThumbData
import dev.aaa1115910.bv.player.entity.LocalVideoPlayerVideoInfoData
import dev.aaa1115910.bv.player.entity.VideoPlayerClockData
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekData
import dev.aaa1115910.bv.player.entity.VideoPlayerSeekThumbData
import dev.aaa1115910.bv.player.entity.VideoPlayerVideoInfoData
import dev.aaa1115910.bv.player.seekbar.SeekMoveState
import dev.aaa1115910.bv.player.tv.VideoSeekBar

@Composable
fun ControllerVideoInfo(
    modifier: Modifier = Modifier,
    show: Boolean,
    onHideInfo: () -> Unit,
    focusRequester: FocusRequester? = null,
    isPlayingLambda: () -> Boolean,
    isShowDanmakuLambda: () -> Boolean,
    onClickPlay: () -> Unit = {},
    isLikedLambda: () -> Boolean,
    onClickLike: () -> Unit = {},
    onLongClickClickLike: () -> Unit = {},
    onClickDanmaku: () -> Unit = {},
    onClickSetting: () -> Unit = {},
    onClickBack: () -> Unit = {},
    ) {
    val videoPlayerClockData = LocalVideoPlayerClockData.current
    val videoPlayerSeekData = LocalVideoPlayerSeekData.current
    val videoPlayerSeekThumbData = LocalVideoPlayerSeekThumbData.current
    val videoPlayerVideoInfoData = LocalVideoPlayerVideoInfoData.current

    var seekHideTimer: CountDownTimer? by remember { mutableStateOf(null) }
    val setCloseInfoTimer: () -> Unit = {
        if (show) {
            seekHideTimer?.cancel()
            seekHideTimer = object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() = onHideInfo()
            }
            seekHideTimer?.start()
        } else {
            seekHideTimer?.cancel()
            seekHideTimer = null
        }
    }

    LaunchedEffect(Unit) {
        setCloseInfoTimer()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            modifier = modifier
                .align(Alignment.TopEnd),
            visible = show,
            enter = expandVertically(),
            exit = shrinkVertically(),
            label = "ControllerTopVideoInfo"
        ) {
            ControllerVideoInfoTop(
                modifier = Modifier.align(Alignment.TopCenter),
                title = videoPlayerVideoInfoData.title,
                clock = Triple(
                    videoPlayerClockData.hour,
                    videoPlayerClockData.minute,
                    videoPlayerClockData.second
                )
            )
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = show,
            enter = expandVertically(),
            exit = shrinkVertically(),
            label = "ControllerBottomVideoInfo"
        ) {
            ControllerVideoInfoBottom(
                seekData = videoPlayerSeekData,
                idleIcon = videoPlayerSeekThumbData.idleIcon,
                movingIcon = videoPlayerSeekThumbData.movingIcon,
                isPlayingLambda = isPlayingLambda,
                isShowDanmakuLambda = isShowDanmakuLambda,
                isLikedLambda = isLikedLambda,
                focusRequester = focusRequester,
                onClickPlay = onClickPlay,
                onClickLike = onClickLike,
                onLongClickLike = onLongClickClickLike,
                onClickDanmaku = onClickDanmaku,
                onClickSetting = onClickSetting,
                onClickBack = onClickBack,
                onFocusBack = onHideInfo,
                )
        }
    }
}

/**
 * 视频上方标题栏
 */
@Composable
fun ControllerVideoInfoTop(
    modifier: Modifier = Modifier,
    title: String,
    clock: Triple<Int, Int, Int>
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(0.4f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Clock(
            modifier = Modifier,
            hour = clock.first,
            minute = clock.second,
            second = clock.third
        )
    }
}

@Composable
fun ControllerVideoInfoBottom(
    modifier: Modifier = Modifier,
    seekData: VideoPlayerSeekData,
    idleIcon: String,
    movingIcon: String,
    isPlayingLambda: () -> Boolean,
    isShowDanmakuLambda: () -> Boolean,
    isLikedLambda: () -> Boolean,
    focusRequester: FocusRequester?,
    onClickPlay: () -> Unit,
    onClickLike: () -> Unit,
    onLongClickLike: () -> Unit,
    onClickDanmaku: () -> Unit,
    onClickSetting: () -> Unit,
    onClickBack: () -> Unit,
    onFocusBack: () -> Unit,
) {
    Column(
        modifier = modifier
//            .clip(
//                MaterialTheme.shapes.large
//                    .copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
//            )
            .background(Color.Black.copy(0.4f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        VideoSeekBar(
            modifier = Modifier
                .fillMaxWidth(),
            duration = seekData.duration,
            position = seekData.position,
            bufferedPercentage = seekData.bufferedPercentage,
            moveState = SeekMoveState.Idle,
            idleIcon = idleIcon,
            movingIcon = movingIcon
        )
        VideoBottomController(
            modifier = Modifier
                .fillMaxWidth(),
            seekData = seekData,
            isPlayingLambda = isPlayingLambda,
            isShowDanmakuLambda = isShowDanmakuLambda,
            isLikedLambda = isLikedLambda,
            focusRequester = focusRequester,
            onClickPlay = onClickPlay,
            onClickLike = onClickLike,
            onLongClickLike = onLongClickLike,
            onClickDanmaku = onClickDanmaku,
            onClickSetting = onClickSetting,
            onClickBack = onClickBack,
            onFocusBack = onFocusBack,
        )
    }
}

@Composable
private fun Clock(
    modifier: Modifier = Modifier,
    hour: Int,
    minute: Int,
    second: Int
) {
    Text(
        modifier = modifier,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontSize = 32.sp)) {
                append("$hour".padStart(2, '0'))
                append(":")
                append("$minute".padStart(2, '0'))
            }
            withStyle(SpanStyle(fontSize = 18.sp)) {
                append(":")
                append("$second".padStart(2, '0'))
            }
        }
    )
}

@Preview
@Composable
private fun ClockPreview() {
    val clock = Triple(12, 30, 30)
    MaterialTheme {
        Clock(
            hour = clock.first,
            minute = clock.second,
            second = clock.third
        )
    }
}

@Preview(device = "id:tv_1080p")
@Composable
private fun ControllerVideoInfoPreview() {
    var show by remember { mutableStateOf(true) }

    CompositionLocalProvider(
        LocalVideoPlayerSeekData provides VideoPlayerSeekData(
            duration = 100,
            position = 33,
            bufferedPercentage = 66
        ),
        LocalVideoPlayerVideoInfoData provides VideoPlayerVideoInfoData(
            title = "【A320】民航史上最佳逆袭！A320的前世今生！民航史上最佳逆袭！A320的前世今生！",
            partTitle = "2023车队车手介绍分析预测"
        ),
        LocalVideoPlayerClockData provides VideoPlayerClockData(
            hour = 12,
            minute = 30,
            second = 30
        ),
        LocalVideoPlayerSeekThumbData provides VideoPlayerSeekThumbData(
            idleIcon = "",
            movingIcon = ""
        )
    ) {
        MaterialTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = { show = !show }) {
                    Text(text = "Switch")
                }
            }
            ControllerVideoInfo(
                modifier = Modifier.fillMaxSize(),
                show = show,
                onHideInfo = {},
                isPlayingLambda = { true },
                isShowDanmakuLambda = { true },
                isLikedLambda = { false }
            )
        }
    }
}