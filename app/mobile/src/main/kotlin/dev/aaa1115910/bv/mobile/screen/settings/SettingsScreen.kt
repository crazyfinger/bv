package dev.aaa1115910.bv.mobile.screen.settings

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator()

    var selectedSettings by rememberSaveable { mutableStateOf<MobileSettings?>(null) }
    val singlePart = listOf(WindowWidthSizeClass.COMPACT, WindowWidthSizeClass.MEDIUM)
        .contains(currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass)

    BackHandler(scaffoldNavigator.canNavigateBack()) {
        scope.launch { scaffoldNavigator.navigateBack() }
    }

    ListDetailPaneScaffold(
        directive = scaffoldNavigator.scaffoldDirective,
        value = scaffoldNavigator.scaffoldValue,
        listPane = {
            AnimatedPane(
                modifier = Modifier.preferredWidth(300.dp),
                enterTransition = fadeIn() + slideInHorizontally(),
                exitTransition = fadeOut() + slideOutHorizontally()
            ) {
                SettingsCategories(
                    selectedSettings = if (singlePart) null else selectedSettings
                        ?: MobileSettings.Play,
                    onSelectedSettings = {
                        selectedSettings = it
                        scope.launch {
                            scaffoldNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                        }
                    },
                    showNavBack = !scaffoldNavigator.canNavigateBack(),
                    onBack = { (context as Activity).finish() },
                    singleList = singlePart
                )
            }
        },
        detailPane = {
            AnimatedPane(
                modifier = Modifier,
                enterTransition = fadeIn() + slideInHorizontally { it / 2 },
                exitTransition = fadeOut() + slideOutHorizontally { it / 2 }
            ) {
                SettingsDetails(
                    selectedSettings = selectedSettings ?: MobileSettings.Play,
                    showNavBack = scaffoldNavigator.canNavigateBack(),
                    onBack = { scope.launch { scaffoldNavigator.navigateBack() } }
                )
            }
        }
    )
}

enum class MobileSettings(val displayName: String) {
    Play("播放设置"), About("关于"), Advance("更多设置"), Debug("调试")
}
