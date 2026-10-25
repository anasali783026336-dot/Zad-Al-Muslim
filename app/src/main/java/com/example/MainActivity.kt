package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.QuranDataSource
import com.example.data.ZadRepository
import com.example.player.AudioDownloadManager
import com.example.player.AudioPlayerManager
import com.example.ui.components.IslamicTopAppBar
import com.example.ui.components.ZadBottomNavigation
import com.example.ui.screens.AudioPlayerScreen
import com.example.ui.screens.AzkarCategoryScreen
import com.example.ui.screens.HijriCalendarScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationSettingsScreen
import com.example.ui.screens.QuranIndexScreen
import com.example.ui.screens.QuranReaderScreen
import com.example.ui.screens.TasbihScreen
import com.example.ui.theme.ZadAlMuslimTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val app = application as ZadApplication
        val downloadManager = AudioDownloadManager(this, app.database.zadDao())
        val repository = ZadRepository(
            context = this,
            zadDao = app.database.zadDao(),
            prefs = app.userPreferences,
            downloadManager = downloadManager
        )
        val playerManager = AudioPlayerManager(this)
        MainViewModel.Factory(this, repository, playerManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleNotificationIntent(intent)

        setContent {
            ZadAlMuslimTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val navigateTo = intent?.getStringExtra("navigate_to")
        if (navigateTo == "azkar") {
            viewModel.navigateTo(Screen.Azkar("الصباح"))
        }
    }
}

@Composable
fun MainContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle system back button with BackHandler
    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.navigateBack()
    }

    val (topTitle, topSubtitle) = when (val s = currentScreen) {
        is Screen.Home -> Pair("زاد المسلم", "«زادُ قلبك، ونورُ دربك»")
        is Screen.QuranIndex -> Pair("المصحف الشريف", "فهرس السور الكريمة")
        is Screen.QuranReader -> {
            val surah = QuranDataSource.getSurahById(s.surahId)
            Pair("سُورَةُ ${surah?.nameArabic ?: ""}", "المصحف الشريف")
        }
        is Screen.Azkar -> Pair("الأذكار والأدعية", "حصن المسلم")
        is Screen.Tasbih -> Pair("المسبحة الإلكترونية", "ألا بذكر الله تطمئن القلوب")
        is Screen.CalendarView -> Pair("التقويم الهجري", "المناسبات الإسلامية والأيام البيض")
        is Screen.AudioPlayer -> Pair("التلاوات الصوتية", "نخبة من كبار القراء")
        is Screen.NotificationSettings -> Pair("تنبيهات الأذكار", "إعدادات المواعيد والتذكير")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
        topBar = {
            IslamicTopAppBar(
                title = topTitle,
                subtitle = topSubtitle,
                canNavigateBack = currentScreen !is Screen.Home,
                onNavigateBack = { viewModel.navigateBack() },
                actions = {
                    if (currentScreen !is Screen.NotificationSettings) {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.NotificationSettings) },
                            modifier = Modifier.testTag("app_bar_notifications_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "تنبيهات الأذكار"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            ZadBottomNavigation(
                currentScreen = currentScreen,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (val s = currentScreen) {
            is Screen.Home -> HomeScreen(viewModel = viewModel, modifier = screenModifier)
            is Screen.QuranIndex -> QuranIndexScreen(viewModel = viewModel, modifier = screenModifier)
            is Screen.QuranReader -> QuranReaderScreen(
                surahId = s.surahId,
                highlightAyah = s.highlightAyah,
                viewModel = viewModel,
                modifier = screenModifier
            )
            is Screen.Azkar -> AzkarCategoryScreen(
                initialCategory = s.initialCategory,
                viewModel = viewModel,
                modifier = screenModifier
            )
            is Screen.Tasbih -> TasbihScreen(viewModel = viewModel, modifier = screenModifier)
            is Screen.CalendarView -> HijriCalendarScreen(viewModel = viewModel, modifier = screenModifier)
            is Screen.AudioPlayer -> AudioPlayerScreen(viewModel = viewModel, modifier = screenModifier)
            is Screen.NotificationSettings -> NotificationSettingsScreen(viewModel = viewModel, modifier = screenModifier)
        }
    }
}
