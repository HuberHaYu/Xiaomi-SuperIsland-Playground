package com.lab.island.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image as ComposeImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.lab.island.R
import com.lab.island.island.ActionStyle
import com.lab.island.island.ActiveIsland
import com.lab.island.island.DeviceCapability
import com.lab.island.island.ExpandedAccessory
import com.lab.island.island.ExpandedPrimaryComponent
import com.lab.island.island.ExpandedTemplate
import com.lab.island.island.IslandController
import com.lab.island.island.IslandDraft
import com.lab.island.island.IslandDuration
import com.lab.island.island.IslandProperty
import com.lab.island.island.IslandScene
import com.lab.island.island.LargeIslandTemplate
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.lab.island.island.SmallIslandTemplate
import java.text.Collator
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LIVE_UPDATE_DEBOUNCE_MS = 120L
private val TOP_BAR_CONTENT_HEIGHT = 112.dp
private val BOTTOM_BAR_CONTENT_HEIGHT = 78.dp
private val FROSTED_GLASS_BLUR = 14.dp

private const val PLATFORM_URL_BILIBILI = "https://space.bilibili.com/194639276?spm_id_from=333.1007.0.0"
private const val PLATFORM_URL_X = "https://x.com/Huber_HaYu"
private const val PLATFORM_URL_YOUTUBE = "https://www.youtube.com/@ameikarewa2116"

private val accentColors = listOf(
    Color(0xFF4D8DFF),
    Color(0xFF24B99A),
    Color(0xFFFF8A4C),
    Color(0xFF8A78FF),
    Color(0xFFE7557C),
    Color(0xFFF0B429)
)

private data class LaunchTarget(
    val packageName: String?,
    val label: String,
    val icon: ImageBitmap?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslandApp(
    capability: DeviceCapability,
    activeIslands: List<ActiveIsland>,
    message: String?,
    onMessageShown: () -> Unit,
    onSend: (IslandDraft) -> Unit,
    onUpdate: (IslandDraft) -> Unit,
    onCancel: (Int) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val verticalScrollState = rememberScrollState()
    val aboutScrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val appPagerState = rememberPagerState(initialPage = 0) { 2 }
    val expandedPagerState = rememberPagerState(initialPage = 1) {
        ExpandedTemplate.entries.size
    }
    val largePagerState = rememberPagerState(initialPage = 1) {
        LargeIslandTemplate.entries.size
    }
    val smallPagerState = rememberPagerState { SmallIslandTemplate.entries.size }

    val defaultTitle = stringResource(R.string.default_title)
    val defaultSubtitle = stringResource(R.string.default_subtitle)
    val defaultExtraText = stringResource(R.string.default_extra_text)
    val defaultProgressLabel = stringResource(R.string.default_progress_label)
    val defaultActionOpen = stringResource(R.string.action_open)
    val defaultActionLater = stringResource(R.string.action_later)
    val defaultActionDone = stringResource(R.string.action_done)
    val defaultSource = stringResource(R.string.app_name)

    var sceneIndex by rememberSaveable { mutableIntStateOf(0) }
    var title by rememberSaveable { mutableStateOf(defaultTitle) }
    var subtitle by rememberSaveable { mutableStateOf(defaultSubtitle) }
    var extraText by rememberSaveable { mutableStateOf(defaultExtraText) }
    var source by rememberSaveable { mutableStateOf(defaultSource) }
    var leadingText by rememberSaveable { mutableStateOf("") }
    var trailingText by rememberSaveable { mutableStateOf("") }
    var digitText by rememberSaveable { mutableStateOf("65") }
    var progressLabel by rememberSaveable { mutableStateOf(defaultProgressLabel) }
    var actionOne by rememberSaveable { mutableStateOf(defaultActionOpen) }
    var actionTwo by rememberSaveable { mutableStateOf(defaultActionLater) }
    var actionThree by rememberSaveable { mutableStateOf(defaultActionDone) }
    var actionStyleIndex by rememberSaveable { mutableIntStateOf(0) }
    var accentIndex by rememberSaveable { mutableIntStateOf(0) }
    var secondaryAccentIndex by rememberSaveable { mutableIntStateOf(3) }
    var durationIndex by rememberSaveable {
        mutableIntStateOf(IslandDuration.entries.indexOf(IslandDuration.THIRTY_MINUTES))
    }
    var propertyIndex by rememberSaveable { mutableIntStateOf(0) }
    var progress by rememberSaveable { mutableFloatStateOf(65f) }
    var progressPoints by rememberSaveable { mutableFloatStateOf(2f) }
    var primaryImageUri by rememberSaveable { mutableStateOf<String?>(null) }
    var secondaryImageUri by rememberSaveable { mutableStateOf<String?>(null) }
    var autoExpand by rememberSaveable { mutableStateOf(true) }
    var useAccentBackground by rememberSaveable { mutableStateOf(false) }
    var narrowFont by rememberSaveable { mutableStateOf(false) }
    var mediaPlaying by rememberSaveable { mutableStateOf(true) }
    var mediaShareUrl by rememberSaveable { mutableStateOf("") }
    var targetPackageName by rememberSaveable {
        mutableStateOf(activeIslands.firstOrNull()?.targetPackageName)
    }
    var showLaunchTargetPicker by rememberSaveable { mutableStateOf(false) }
    var launchTargetQuery by rememberSaveable { mutableStateOf("") }
    var launchTargets by remember { mutableStateOf<List<LaunchTarget>>(emptyList()) }

    LaunchedEffect(context) {
        launchTargets = withContext(Dispatchers.IO) {
            loadLaunchTargets(context.applicationContext)
        }
    }

    LaunchedEffect(launchTargets, targetPackageName) {
        if (
            launchTargets.isNotEmpty() &&
            targetPackageName != null &&
            launchTargets.none { it.packageName == targetPackageName }
        ) {
            targetPackageName = null
        }
    }

    val primaryImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            primaryImageUri = it.toString()
        }
    }
    val secondaryImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            secondaryImageUri = it.toString()
        }
    }

    val scene = IslandScene.entries[sceneIndex]
    val expandedTemplate = ExpandedTemplate.entries[expandedPagerState.currentPage]
    val largeTemplate = LargeIslandTemplate.entries[largePagerState.currentPage]
    val smallTemplate = SmallIslandTemplate.entries[smallPagerState.currentPage]
    val accent = accentColors[accentIndex]
    val secondaryAccent = accentColors[secondaryAccentIndex]
    val activeIsland = activeIslands.firstOrNull()
    val currentDraft = IslandDraft(
        scene = scene,
        expandedTemplate = expandedTemplate,
        largeIslandTemplate = largeTemplate,
        smallIslandTemplate = smallTemplate,
        title = title,
        subtitle = subtitle,
        extraText = extraText,
        source = source,
        leadingText = leadingText,
        trailingText = trailingText,
        digitText = digitText,
        progressLabel = progressLabel,
        actionLabels = listOf(actionOne, actionTwo, actionThree),
        actionStyle = ActionStyle.entries[actionStyleIndex],
        progress = progress.toInt(),
        progressPoints = progressPoints.toInt(),
        primaryImageUri = primaryImageUri,
        secondaryImageUri = secondaryImageUri,
        accentColor = accent.toArgb(),
        secondaryAccentColor = secondaryAccent.toArgb(),
        duration = IslandDuration.entries[durationIndex],
        islandProperty = IslandProperty.entries[propertyIndex],
        autoExpand = autoExpand,
        useAccentBackground = useAccentBackground,
        narrowFont = narrowFont,
        mediaPlaying = mediaPlaying,
        mediaShareUrl = mediaShareUrl,
        targetPackageName = targetPackageName
    )
    val latestDraft = rememberUpdatedState(currentDraft)

    LaunchedEffect(message) {
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onMessageShown()
        }
    }

    LaunchedEffect(activeIsland?.notificationId) {
        if (activeIsland == null) return@LaunchedEffect
        var skipInitialValue = true
        snapshotFlow { latestDraft.value }.collectLatest { draft ->
            if (skipInitialValue) {
                skipInitialValue = false
                return@collectLatest
            }
            delay(LIVE_UPDATE_DEBOUNCE_MS)
            onUpdate(draft)
        }
    }

    LaunchedEffect(activeIsland?.mediaPlaying) {
        if (activeIsland?.scene == IslandScene.MUSIC) {
            mediaPlaying = activeIsland.mediaPlaying
        }
    }

    val backdropLayer = rememberGraphicsLayer()
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarHeight = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
    val isEditorPage = appPagerState.currentPage == 0
    val topBarHeight = TOP_BAR_CONTENT_HEIGHT + statusBarHeight
    val editorBottomBarHeight = BOTTOM_BAR_CONTENT_HEIGHT + navigationBarHeight
    val targetBottomBarHeight = if (isEditorPage) {
        editorBottomBarHeight
    } else {
        navigationBarHeight
    }
    val bottomBarHeight by animateDpAsState(
        targetValue = targetBottomBarHeight,
        animationSpec = spring(
            dampingRatio = 0.9f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bottom bar height"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = appPagerState,
            modifier = Modifier
                .fillMaxSize()
                .captureFrostedBackdrop(backdropLayer),
            beyondViewportPageCount = 1,
            key = { it },
            userScrollEnabled = true
        ) { page ->
            if (page == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(verticalScrollState)
                        .padding(
                            start = 16.dp,
                            top = topBarHeight + 8.dp,
                            end = 16.dp,
                            bottom = bottomBarHeight + 20.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
            CapabilityBanner(capability)

            SceneSelector(
                selected = scene,
                onSelected = { selected ->
                    sceneIndex = IslandScene.entries.indexOf(selected)
                    if (selected == IslandScene.NAVIGATION) {
                        propertyIndex = IslandProperty.entries.indexOf(IslandProperty.ACTION)
                        coroutineScope.launch {
                            expandedPagerState.animateScrollToPage(
                                ExpandedTemplate.entries.indexOf(
                                    ExpandedTemplate.TEXT_TWO_ROUTE_PROGRESS
                                )
                            )
                            largePagerState.animateScrollToPage(
                                LargeIslandTemplate.entries.indexOf(
                                    LargeIslandTemplate.LEFT_PROGRESS_TEXT
                                )
                            )
                            smallPagerState.animateScrollToPage(
                                SmallIslandTemplate.entries.indexOf(
                                    SmallIslandTemplate.PROGRESS_ICON
                                )
                            )
                        }
                    }
                }
            )

            LaunchTargetSelector(
                targets = launchTargets,
                selectedPackageName = targetPackageName,
                onOpenPicker = { showLaunchTargetPicker = true }
            )

            if (scene == IslandScene.MUSIC) {
                MusicIslandPreview(
                    title = title.ifBlank { stringResource(R.string.placeholder_song_title) },
                    artist = subtitle.ifBlank { stringResource(R.string.placeholder_artist) },
                    progress = progress.toInt(),
                    playing = mediaPlaying,
                    accent = accent,
                    coverSelected = primaryImageUri != null
                )
            } else {
                ExpandedTemplateCarousel(
                    pagerState = expandedPagerState,
                    title = title.ifBlank { stringResource(R.string.placeholder_island_title) },
                    subtitle = subtitle.ifBlank { stringResource(R.string.placeholder_notification_content) },
                    extraText = extraText,
                    digitText = digitText,
                    actionLabel = actionOne.ifBlank { stringResource(R.string.action_open) },
                    progress = progress.toInt(),
                    accent = accent,
                    secondaryAccent = secondaryAccent,
                    primaryImageSelected = primaryImageUri != null
                )

                SummaryTemplateCarousels(
                    largePagerState = largePagerState,
                    smallPagerState = smallPagerState,
                    title = title.ifBlank { stringResource(R.string.field_title) },
                    digitText = digitText.ifBlank { "65" },
                    leadingText = leadingText,
                    trailingText = trailingText,
                    progress = progress.toInt(),
                    accent = accent
                )
            }

            PreviewDisclaimer()

            SectionCard(
                title = when (scene) {
                    IslandScene.GENERAL -> stringResource(R.string.section_content)
                    IslandScene.NAVIGATION -> stringResource(R.string.section_navigation_content)
                    IslandScene.MUSIC -> stringResource(R.string.section_media_content)
                },
                subtitle = when (scene) {
                    IslandScene.GENERAL -> stringResource(R.string.section_content_description)
                    IslandScene.NAVIGATION -> stringResource(R.string.section_navigation_content_description)
                    IslandScene.MUSIC -> stringResource(R.string.section_media_content_description)
                }
            ) {
                    AppTextField(
                        value = title,
                        onValueChange = { title = it.take(IslandController.MAX_TITLE_LENGTH) },
                        label = when (scene) {
                            IslandScene.GENERAL -> stringResource(R.string.field_title)
                            IslandScene.NAVIGATION -> stringResource(R.string.field_navigation_instruction)
                            IslandScene.MUSIC -> stringResource(R.string.field_song_title)
                        },
                        limit = IslandController.MAX_TITLE_LENGTH,
                        imeAction = ImeAction.Next
                    )
                    AppTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it.take(IslandController.MAX_SUBTITLE_LENGTH) },
                        label = when (scene) {
                            IslandScene.GENERAL -> stringResource(R.string.field_content)
                            IslandScene.NAVIGATION -> stringResource(R.string.field_distance_eta)
                            IslandScene.MUSIC -> stringResource(R.string.field_artist)
                        },
                        limit = IslandController.MAX_SUBTITLE_LENGTH,
                        imeAction = ImeAction.Next,
                        singleLine = false
                    )
                    if (scene != IslandScene.GENERAL || expandedTemplate.usesExtraText) {
                        AppTextField(
                            value = extraText,
                            onValueChange = {
                                extraText = it.take(IslandController.MAX_EXTRA_LENGTH)
                            },
                            label = when (scene) {
                                IslandScene.GENERAL -> stringResource(R.string.field_extra_text)
                                IslandScene.NAVIGATION -> stringResource(R.string.field_route_hint)
                                IslandScene.MUSIC -> stringResource(R.string.field_album)
                            },
                            limit = IslandController.MAX_EXTRA_LENGTH,
                            imeAction = ImeAction.Next
                        )
                    }
                    AppTextField(
                        value = source,
                        onValueChange = { source = it.take(IslandController.MAX_SOURCE_LENGTH) },
                        label = stringResource(R.string.field_notification_source_optional),
                        limit = IslandController.MAX_SOURCE_LENGTH,
                        imeAction = ImeAction.Done
                    )
            }

            if (scene != IslandScene.MUSIC) {
                SectionCard(
                    title = stringResource(R.string.section_summary_text),
                    subtitle = stringResource(R.string.section_summary_text_description)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CompactTextField(
                            value = leadingText,
                            onValueChange = {
                                leadingText = it.take(IslandController.MAX_ISLAND_TEXT_LENGTH)
                            },
                            label = stringResource(R.string.field_leading_text),
                            modifier = Modifier.weight(1f)
                        )
                        CompactTextField(
                            value = trailingText,
                            onValueChange = {
                                trailingText = it.take(IslandController.MAX_ISLAND_TEXT_LENGTH)
                            },
                            label = stringResource(R.string.field_trailing_text),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    CompactTextField(
                        value = digitText,
                        onValueChange = { digitText = it.take(IslandController.MAX_DIGIT_LENGTH) },
                        label = stringResource(R.string.field_digit_text),
                        modifier = Modifier.fillMaxWidth()
                    )
                    SettingRow(
                        title = stringResource(R.string.setting_narrow_font),
                        subtitle = stringResource(R.string.setting_narrow_font_description),
                        checked = narrowFont,
                        onCheckedChange = { narrowFont = it }
                    )
                }
            }

            SectionCard(
                title = if (scene == IslandScene.MUSIC) {
                    stringResource(R.string.section_cover)
                } else {
                    stringResource(R.string.section_images)
                },
                subtitle = if (scene == IslandScene.MUSIC) {
                    stringResource(R.string.section_cover_description)
                } else {
                    stringResource(R.string.section_images_description)
                }
            ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ImagePickerButton(
                            title = stringResource(R.string.image_primary),
                            selectedUri = primaryImageUri,
                            onSelect = { primaryImagePicker.launch(arrayOf("image/*")) },
                            onClear = { primaryImageUri = null },
                            modifier = Modifier.weight(1f)
                        )
                        if (scene != IslandScene.MUSIC) {
                            ImagePickerButton(
                                title = stringResource(R.string.image_component),
                                selectedUri = secondaryImageUri,
                                onSelect = { secondaryImagePicker.launch(arrayOf("image/*")) },
                                onClear = { secondaryImageUri = null },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
            }

            if (
                scene == IslandScene.NAVIGATION ||
                scene == IslandScene.MUSIC ||
                expandedTemplate.usesProgress ||
                largeTemplate == LargeIslandTemplate.LEFT_PROGRESS_TEXT ||
                smallTemplate == SmallIslandTemplate.PROGRESS_ICON
            ) {
                SectionCard(
                    title = if (scene == IslandScene.MUSIC) {
                        stringResource(R.string.section_playback_progress)
                    } else {
                        stringResource(R.string.section_progress)
                    },
                    subtitle = if (scene == IslandScene.MUSIC) {
                        stringResource(R.string.section_playback_progress_description)
                    } else {
                        stringResource(R.string.section_progress_description)
                    }
                ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                stringResource(R.string.current_progress),
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                stringResource(R.string.value_percent, progress.toInt()),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = progress,
                            onValueChange = { progress = it },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                        if (
                            scene != IslandScene.MUSIC &&
                            expandedTemplate.accessory == ExpandedAccessory.MULTI_PROGRESS
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                CompactTextField(
                                    value = progressLabel,
                                    onValueChange = {
                                        progressLabel = it.take(
                                            IslandController.MAX_PROGRESS_LABEL_LENGTH
                                        )
                                    },
                                    label = stringResource(R.string.field_progress_description),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    stringResource(
                                        R.string.segment_count,
                                        progressPoints.toInt()
                                    ),
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Slider(
                                    value = progressPoints,
                                    onValueChange = { progressPoints = it },
                                    valueRange = 0f..4f,
                                    steps = 3,
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary,
                                        inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                                    )
                                )
                            }
                        }
                }
            }

            if (scene != IslandScene.MUSIC && expandedTemplate.usesActions) {
                ActionEditor(
                    template = expandedTemplate,
                    actionStyleIndex = actionStyleIndex,
                    onActionStyleChange = { actionStyleIndex = it },
                    actionOne = actionOne,
                    onActionOneChange = {
                        actionOne = it.take(IslandController.MAX_ACTION_LENGTH)
                    },
                    actionTwo = actionTwo,
                    onActionTwoChange = {
                        actionTwo = it.take(IslandController.MAX_ACTION_LENGTH)
                    },
                    actionThree = actionThree,
                    onActionThreeChange = {
                        actionThree = it.take(IslandController.MAX_ACTION_LENGTH)
                    }
                )
            }

            if (scene == IslandScene.MUSIC) {
                SectionCard(
                    title = stringResource(R.string.section_media_state),
                    subtitle = stringResource(R.string.section_media_state_description)
                ) {
                        SettingRow(
                            title = stringResource(R.string.setting_playing),
                            subtitle = stringResource(R.string.setting_playing_description),
                            checked = mediaPlaying,
                            onCheckedChange = { mediaPlaying = it }
                        )
                        AppTextField(
                            value = mediaShareUrl,
                            onValueChange = {
                                mediaShareUrl = it.take(
                                    IslandController.MAX_MEDIA_SHARE_URL_LENGTH
                                )
                            },
                            label = stringResource(R.string.field_share_link_optional),
                            limit = IslandController.MAX_MEDIA_SHARE_URL_LENGTH,
                            imeAction = ImeAction.Done
                        )
                        Text(
                            stringResource(R.string.media_usage_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }
            }

            SectionCard(
                title = stringResource(R.string.section_appearance_behavior),
                subtitle = stringResource(R.string.section_appearance_behavior_description)
            ) {
                    ColorPickerRow(
                        title = stringResource(R.string.setting_accent_color),
                        selectedIndex = accentIndex,
                        onSelected = { accentIndex = it }
                    )
                    ColorPickerRow(
                        title = stringResource(R.string.setting_secondary_color),
                        selectedIndex = secondaryAccentIndex,
                        onSelected = { secondaryAccentIndex = it }
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                    )
                    if (scene != IslandScene.MUSIC) {
                        SettingRow(
                            title = stringResource(R.string.setting_auto_expand),
                            subtitle = stringResource(R.string.setting_auto_expand_description),
                            checked = autoExpand,
                            onCheckedChange = { autoExpand = it }
                        )
                        SettingRow(
                            title = stringResource(R.string.setting_accent_background),
                            subtitle = stringResource(R.string.setting_accent_background_description),
                            checked = useAccentBackground,
                            onCheckedChange = { useAccentBackground = it }
                        )
                        Text(
                            stringResource(R.string.setting_island_property),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(IslandProperty.entries) { property ->
                                FilterChip(
                                    selected = IslandProperty.entries[propertyIndex] == property,
                                    onClick = {
                                        propertyIndex = IslandProperty.entries.indexOf(property)
                                    },
                                    label = { Text(stringResource(property.labelRes)) },
                                    colors = appFilterChipColors()
                                )
                            }
                        }
                    }
                    Text(
                        stringResource(R.string.setting_duration),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(IslandDuration.entries) { duration ->
                            FilterChip(
                                selected = IslandDuration.entries[durationIndex] == duration,
                                onClick = {
                                    durationIndex = IslandDuration.entries.indexOf(duration)
                                },
                                label = { Text(stringResource(duration.labelRes)) },
                                colors = appFilterChipColors()
                            )
                        }
                    }
            }

            if (activeIslands.isNotEmpty()) {
                ActiveIslandSection(
                    activeIslands = activeIslands,
                    onCancel = onCancel
                )
            }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(aboutScrollState)
                        .padding(
                            start = 16.dp,
                            top = topBarHeight + 16.dp,
                            end = 16.dp,
                            bottom = bottomBarHeight + 24.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AboutPage()
                }
            }
        }

        FrostedGlassBars(
            backdropLayer = backdropLayer,
            topBarHeight = topBarHeight,
            bottomBarHeight = bottomBarHeight,
            modifier = Modifier.fillMaxSize()
        )

        IslandTopBar(
            selectedPage = appPagerState.currentPage,
            onPageSelected = { page ->
                coroutineScope.launch { appPagerState.animateScrollToPage(page) }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(topBarHeight)
        )

        AnimatedVisibility(
            visible = isEditorPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(editorBottomBarHeight),
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight }
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight }
            ) + fadeOut()
        ) {
            IslandBottomBar(
                activeIsland = activeIsland,
                canSend = title.isNotBlank(),
                onSend = { onSend(currentDraft) },
                onCancel = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 16.dp,
                    top = 0.dp,
                    end = 16.dp,
                    bottom = bottomBarHeight + 10.dp
                )
        )
    }

    if (showLaunchTargetPicker) {
        LaunchTargetDialog(
            targets = launchTargets,
            selectedPackageName = targetPackageName,
            query = launchTargetQuery,
            onQueryChange = { launchTargetQuery = it },
            onSelected = { target ->
                targetPackageName = target.packageName
                launchTargetQuery = ""
                showLaunchTargetPicker = false
            },
            onDismiss = {
                launchTargetQuery = ""
                showLaunchTargetPicker = false
            }
        )
    }
}

@Composable
private fun IslandTopBar(
    selectedPage: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.statusBarsPadding()) {
        Row(
            modifier = Modifier
                .height(TOP_BAR_CONTENT_HEIGHT - 48.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconImage(modifier = Modifier.size(42.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    stringResource(R.string.top_bar_subtitle),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        TabRow(
            selectedTabIndex = selectedPage,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
            }
        ) {
            Tab(
                selected = selectedPage == 0,
                onClick = { onPageSelected(0) },
                text = { Text(stringResource(R.string.tab_editor)) }
            )
            Tab(
                selected = selectedPage == 1,
                onClick = { onPageSelected(1) },
                text = { Text(stringResource(R.string.tab_about)) }
            )
        }
    }
}

@Composable
private fun AppIconImage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val icon = remember(context) {
        runCatching {
            context.applicationInfo
                .loadIcon(context.packageManager)
                .toBitmap(192, 192)
                .asImageBitmap()
        }.getOrNull()
    }
    if (icon == null) {
        Icon(
            Icons.Rounded.Apps,
            contentDescription = stringResource(R.string.app_icon_description),
            modifier = modifier,
            tint = MaterialTheme.colorScheme.primary
        )
    } else {
        ComposeImage(
            bitmap = icon,
            contentDescription = stringResource(R.string.app_icon_description),
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}

private data class PlatformLink(
    val name: String,
    val iconRes: Int,
    val color: Color,
    val url: String
)

@Composable
private fun AboutPage() {
    val context = LocalContext.current
    val platforms = listOf(
        PlatformLink(
            name = stringResource(R.string.platform_bilibili),
            iconRes = R.drawable.ic_platform_bilibili,
            color = Color(0xFF00AEEC),
            url = PLATFORM_URL_BILIBILI
        ),
        PlatformLink(
            name = stringResource(R.string.platform_x),
            iconRes = R.drawable.ic_platform_x,
            color = MaterialTheme.colorScheme.onSurface,
            url = PLATFORM_URL_X
        ),
        PlatformLink(
            name = stringResource(R.string.platform_youtube),
            iconRes = R.drawable.ic_platform_youtube,
            color = Color(0xFFFF0033),
            url = PLATFORM_URL_YOUTUBE
        )
    )

    Card(
        modifier = Modifier.contentWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIconImage(modifier = Modifier.size(84.dp))
            Spacer(Modifier.width(18.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    stringResource(R.string.about_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    stringResource(R.string.about_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                )
                Text(
                    stringResource(R.string.about_author),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }

    SectionCard(
        title = stringResource(R.string.about_platforms_title),
        subtitle = stringResource(R.string.about_platforms_description)
    ) {
        platforms.forEach { platform ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(enabled = platform.url.isNotBlank()) {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, platform.url.toUri())
                        )
                    },
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    ) {
                        Icon(
                            painter = painterResource(platform.iconRes),
                            contentDescription = platform.name,
                            modifier = Modifier.padding(10.dp),
                            tint = platform.color
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            platform.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        Text(
            stringResource(R.string.about_license_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun IslandBottomBar(
    activeIsland: ActiveIsland?,
    canSend: Boolean,
    onSend: () -> Unit,
    onCancel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        if (activeIsland == null) {
            Button(
                onClick = onSend,
                enabled = canSend,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null)
                Spacer(Modifier.width(10.dp))
                Text(
                    stringResource(R.string.action_send_island),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .height(54.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.live_sync_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        stringResource(R.string.live_sync_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(
                    onClick = { onCancel(activeIsland.notificationId) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.action_withdraw))
                }
            }
        }
    }
}

private fun Modifier.captureFrostedBackdrop(layer: GraphicsLayer): Modifier = drawWithContent {
    layer.record(size = IntSize(size.width.roundToInt(), size.height.roundToInt())) {
        this@drawWithContent.drawContent()
    }
    drawLayer(layer)
}

@Composable
private fun LaunchTargetSelector(
    targets: List<LaunchTarget>,
    selectedPackageName: String?,
    onOpenPicker: () -> Unit
) {
    val selected = targets.firstOrNull { it.packageName == selectedPackageName }
    SectionCard(
        title = stringResource(R.string.section_launch_target),
        subtitle = stringResource(R.string.section_launch_target_description)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onOpenPicker),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (targets.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(42.dp).padding(9.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    LaunchTargetIcon(selected?.icon, Modifier.size(42.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        selected?.label ?: stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        selected?.packageName
                            ?: stringResource(R.string.launch_target_island_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    stringResource(R.string.action_choose_app),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun LaunchTargetDialog(
    targets: List<LaunchTarget>,
    selectedPackageName: String?,
    query: String,
    onQueryChange: (String) -> Unit,
    onSelected: (LaunchTarget) -> Unit,
    onDismiss: () -> Unit
) {
    val filteredTargets = remember(targets, query) {
        val normalized = query.trim().lowercase(Locale.ROOT)
        if (normalized.isEmpty()) {
            targets
        } else {
            targets.filter { target ->
                target.label.lowercase(Locale.ROOT).contains(normalized) ||
                    target.packageName.orEmpty().lowercase(Locale.ROOT).contains(normalized)
            }
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.launch_target_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.launch_target_search)) },
                    singleLine = true,
                    colors = appOutlinedTextFieldColors()
                )
                when {
                    targets.isEmpty() -> Box(
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    filteredTargets.isEmpty() -> Box(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.launch_target_no_results),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = filteredTargets,
                            key = { it.packageName ?: "__island__" }
                        ) { target ->
                            val selected = target.packageName == selectedPackageName
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onSelected(target) },
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    Color.Transparent
                                },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LaunchTargetIcon(target.icon, Modifier.size(40.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            target.label,
                                            style = MaterialTheme.typography.titleSmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            target.packageName ?: stringResource(
                                                R.string.launch_target_island_description
                                            ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.action_close))
            }
        }
    )
}

@Composable
private fun LaunchTargetIcon(icon: ImageBitmap?, modifier: Modifier = Modifier) {
    if (icon == null) {
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Rounded.Apps,
                contentDescription = null,
                modifier = Modifier.padding(9.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    } else {
        ComposeImage(
            bitmap = icon,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}

private fun loadLaunchTargets(context: Context): List<LaunchTarget> {
    val packageManager = context.packageManager
    val launcherQuery = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    val resolveInfos = runCatching {
        if (Build.VERSION.SDK_INT >= 33) {
            packageManager.queryIntentActivities(
                launcherQuery,
                PackageManager.ResolveInfoFlags.of(0L)
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(launcherQuery, 0)
        }
    }.getOrDefault(emptyList())
    val iconSizePx = (40f * context.resources.displayMetrics.density)
        .roundToInt()
        .coerceIn(48, 96)
    fun loadIcon(packageName: String, fallback: () -> android.graphics.drawable.Drawable) =
        runCatching {
            packageManager.getApplicationIcon(packageName)
        }.getOrElse { fallback() }
            .let { drawable ->
                runCatching {
                    drawable.toBitmap(iconSizePx, iconSizePx).asImageBitmap()
                }.getOrNull()
            }

    val ownTarget = LaunchTarget(
        packageName = null,
        label = context.getString(R.string.app_name),
        icon = loadIcon(context.packageName) { context.applicationInfo.loadIcon(packageManager) }
    )
    val collator = Collator.getInstance()
    val externalTargets = resolveInfos
        .asSequence()
        .mapNotNull { resolveInfo ->
            resolveInfo.activityInfo?.packageName?.let { packageName ->
                resolveInfo to packageName
            }
        }
        .distinctBy { (_, packageName) -> packageName }
        .filter { (_, packageName) -> packageName != context.packageName }
        .mapNotNull { (resolveInfo, packageName) ->
            runCatching {
                LaunchTarget(
                    packageName = packageName,
                    label = resolveInfo.loadLabel(packageManager)?.toString()
                        ?.takeIf { it.isNotBlank() }
                        ?: packageName,
                    icon = runCatching {
                        resolveInfo.loadIcon(packageManager)
                            .toBitmap(iconSizePx, iconSizePx)
                            .asImageBitmap()
                    }.getOrNull()
                )
            }.getOrNull()
        }
        .sortedWith { left, right -> collator.compare(left.label, right.label) }
        .toList()
    return listOf(ownTarget) + externalTargets
}

private data class FrostedGlassRecordKey(
    val viewportSize: IntSize,
    val topHeightPx: Int,
    val bottomHeightPx: Int,
    val blurPaddingPx: Int
)

private class FrostedGlassRecordCache {
    var key: FrostedGlassRecordKey? = null
    var bottomSegmentY = 0
    var bottomSampleStartY = 0
}

@Composable
private fun FrostedGlassBars(
    backdropLayer: GraphicsLayer,
    topBarHeight: Dp,
    bottomBarHeight: Dp,
    modifier: Modifier = Modifier
) {
    val blurredBarsLayer = rememberGraphicsLayer()
    val recordCache = remember { FrostedGlassRecordCache() }
    val isDark = isSystemInDarkTheme()
    val glassTint = if (isDark) Color(0xC91B1C1E) else Color(0xC9F7F7F8)

    Canvas(modifier = modifier) {
        val viewportSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
        if (viewportSize.width <= 0 || viewportSize.height <= 0) {
            return@Canvas
        }

        val topHeightPx = topBarHeight.toPx().roundToInt().coerceAtMost(viewportSize.height)
        val bottomHeightPx = bottomBarHeight.toPx().roundToInt().coerceAtMost(viewportSize.height)
        val bottomPanelTop = viewportSize.height - bottomHeightPx

        if (backdropLayer.size == viewportSize) {
            val blurRadiusPx = FROSTED_GLASS_BLUR.toPx()
            val blurPaddingPx = ceil(blurRadiusPx * 1.5f).toInt()
            val key = FrostedGlassRecordKey(
                viewportSize = viewportSize,
                topHeightPx = topHeightPx,
                bottomHeightPx = bottomHeightPx,
                blurPaddingPx = blurPaddingPx
            )

            if (recordCache.key != key) {
                val topSampleHeight =
                    (topHeightPx + blurPaddingPx).coerceAtMost(viewportSize.height)
                val bottomSampleStartY = (
                    viewportSize.height - bottomHeightPx - blurPaddingPx
                    ).coerceAtLeast(0)
                val bottomSampleHeight = viewportSize.height - bottomSampleStartY
                val segmentGap = blurPaddingPx * 2
                val bottomSegmentY = topSampleHeight + segmentGap

                blurredBarsLayer.record(
                    size = IntSize(
                        viewportSize.width,
                        bottomSegmentY + bottomSampleHeight
                    )
                ) {
                    clipRect(
                        left = 0f,
                        top = 0f,
                        right = viewportSize.width.toFloat(),
                        bottom = topSampleHeight.toFloat()
                    ) {
                        drawLayer(backdropLayer)
                    }
                    translate(top = bottomSegmentY.toFloat()) {
                        clipRect(
                            left = 0f,
                            top = 0f,
                            right = viewportSize.width.toFloat(),
                            bottom = bottomSampleHeight.toFloat()
                        ) {
                            translate(top = -bottomSampleStartY.toFloat()) {
                                drawLayer(backdropLayer)
                            }
                        }
                    }
                }
                blurredBarsLayer.renderEffect = BlurEffect(
                    radiusX = blurRadiusPx,
                    radiusY = blurRadiusPx,
                    edgeTreatment = TileMode.Clamp
                )
                recordCache.key = key
                recordCache.bottomSegmentY = bottomSegmentY
                recordCache.bottomSampleStartY = bottomSampleStartY
            }

            clipRect(bottom = topHeightPx.toFloat()) {
                drawLayer(blurredBarsLayer)
            }
            clipRect(top = bottomPanelTop.toFloat()) {
                translate(
                    top = (recordCache.bottomSampleStartY - recordCache.bottomSegmentY).toFloat()
                ) {
                    drawLayer(blurredBarsLayer)
                }
            }
        }

        drawRect(
            color = glassTint,
            size = Size(size.width, topHeightPx.toFloat())
        )
        drawRect(
            color = glassTint,
            topLeft = Offset(0f, bottomPanelTop.toFloat()),
            size = Size(size.width, bottomHeightPx.toFloat())
        )
    }
}

@Composable
private fun SceneSelector(
    selected: IslandScene,
    onSelected: (IslandScene) -> Unit
) {
    SectionCard(
        title = stringResource(R.string.section_notification_type),
        subtitle = stringResource(R.string.section_notification_type_description)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IslandScene.entries.forEach { scene ->
                FilterChip(
                    selected = selected == scene,
                    onClick = { onSelected(scene) },
                    label = { Text(stringResource(scene.labelRes)) },
                    colors = appFilterChipColors(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Text(
            stringResource(selected.descriptionRes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MusicIslandPreview(
    title: String,
    artist: String,
    progress: Int,
    playing: Boolean,
    accent: Color,
    coverSelected: Boolean
) {
    SectionCard(
        title = stringResource(R.string.section_music_island),
        subtitle = stringResource(R.string.section_music_island_description)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.Black, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PreviewPicture(
                    accent = accent,
                    selected = coverSelected,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        artist,
                        color = Color(0xFFAEB0B6),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape),
                        color = accent,
                        trackColor = Color(0xFF34353A)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(58.dp).background(Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (playing) {
                        stringResource(R.string.action_pause)
                    } else {
                        stringResource(R.string.action_play)
                    },
                    tint = accent,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun ExpandedTemplateCarousel(
    pagerState: androidx.compose.foundation.pager.PagerState,
    title: String,
    subtitle: String,
    extraText: String,
    digitText: String,
    actionLabel: String,
    progress: Int,
    accent: Color,
    secondaryAccent: Color,
    primaryImageSelected: Boolean
) {
    SectionCard(
        title = stringResource(R.string.section_expanded_templates),
        subtitle = stringResource(R.string.section_expanded_templates_description)
    ) {
        Box(Modifier.fillMaxWidth().height(126.dp)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp),
                pageSpacing = 12.dp,
                beyondViewportPageCount = 1,
                key = { ExpandedTemplate.entries[it] },
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapAnimationSpec = spring(
                        dampingRatio = 0.82f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            ) { page ->
                val templateDescription = stringResource(
                    R.string.expanded_template_description,
                    ExpandedTemplate.entries[page].number
                )
                OfficialExpandedPreview(
                    template = ExpandedTemplate.entries[page],
                    title = title,
                    subtitle = subtitle,
                    extraText = extraText,
                    digitText = digitText,
                    actionLabel = actionLabel,
                    progress = progress,
                    accent = accent,
                    secondaryAccent = secondaryAccent,
                    primaryImageSelected = primaryImageSelected,
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics {
                            contentDescription = templateDescription
                        }
                )
            }
            PagerEdgeFadeOverlay(MaterialTheme.colorScheme.surfaceContainer)
        }
        val currentTemplate = ExpandedTemplate.entries[pagerState.currentPage]
        val currentTemplateLabel = stringResource(currentTemplate.labelRes)
        val os3Suffix = if (currentTemplate.os3Only) {
            stringResource(R.string.template_os3_suffix)
        } else {
            ""
        }
        PagerCaption(
            current = pagerState.currentPage,
            total = ExpandedTemplate.entries.size,
            title = stringResource(
                R.string.template_caption,
                currentTemplate.number,
                currentTemplateLabel,
                os3Suffix
            )
        )
    }
}

@Composable
private fun SummaryTemplateCarousels(
    largePagerState: androidx.compose.foundation.pager.PagerState,
    smallPagerState: androidx.compose.foundation.pager.PagerState,
    title: String,
    digitText: String,
    leadingText: String,
    trailingText: String,
    progress: Int,
    accent: Color
) {
    SectionCard(
        title = stringResource(R.string.section_summary_templates),
        subtitle = stringResource(R.string.section_summary_templates_description)
    ) {
        Text(
            stringResource(R.string.large_island_regions),
            style = MaterialTheme.typography.labelLarge
        )
        Box(Modifier.fillMaxWidth().height(86.dp)) {
            HorizontalPager(
                state = largePagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 12.dp,
                beyondViewportPageCount = 1,
                key = { LargeIslandTemplate.entries[it] },
                flingBehavior = PagerDefaults.flingBehavior(
                    state = largePagerState,
                    snapAnimationSpec = spring(
                        dampingRatio = 0.82f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            ) { page ->
                OfficialLargeIslandPreview(
                    template = LargeIslandTemplate.entries[page],
                    title = title,
                    digitText = digitText,
                    leadingText = leadingText,
                    trailingText = trailingText,
                    progress = progress,
                    accent = accent,
                    modifier = Modifier.fillMaxSize()
                )
            }
            PagerEdgeFadeOverlay(MaterialTheme.colorScheme.surfaceContainer)
        }
        val currentLargeTemplate = LargeIslandTemplate.entries[largePagerState.currentPage]
        val currentLargeTemplateLabel = stringResource(currentLargeTemplate.labelRes)
        PagerCaption(
            current = largePagerState.currentPage,
            total = LargeIslandTemplate.entries.size,
            title = stringResource(
                R.string.large_template_caption,
                currentLargeTemplate.number,
                currentLargeTemplateLabel
            )
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
        )
        Text(
            stringResource(R.string.small_island_components),
            style = MaterialTheme.typography.labelLarge
        )
        Box(Modifier.fillMaxWidth().height(72.dp)) {
            HorizontalPager(
                state = smallPagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 40.dp),
                pageSpacing = 18.dp,
                beyondViewportPageCount = 1,
                key = { SmallIslandTemplate.entries[it] },
                flingBehavior = PagerDefaults.flingBehavior(
                    state = smallPagerState,
                    snapAnimationSpec = spring(
                        dampingRatio = 0.82f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            ) { page ->
                OfficialSmallIslandPreview(
                    template = SmallIslandTemplate.entries[page],
                    digitText = digitText,
                    progress = progress,
                    accent = accent,
                    modifier = Modifier.fillMaxSize()
                )
            }
            PagerEdgeFadeOverlay(MaterialTheme.colorScheme.surfaceContainer)
        }
        PagerCaption(
            current = smallPagerState.currentPage,
            total = SmallIslandTemplate.entries.size,
            title = stringResource(
                SmallIslandTemplate.entries[smallPagerState.currentPage].labelRes
            )
        )
    }
}

@Composable
private fun PagerCaption(current: Int, total: Int, title: String) {
    Text(
        title,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    LinearProgressIndicator(
        progress = { (current + 1f) / total },
        modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape),
        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
    )
    Text(
        stringResource(R.string.pager_position, current + 1, total),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun OfficialExpandedPreview(
    template: ExpandedTemplate,
    title: String,
    subtitle: String,
    extraText: String,
    digitText: String,
    actionLabel: String,
    progress: Int,
    accent: Color,
    secondaryAccent: Color,
    primaryImageSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF111215), RoundedCornerShape(24.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (template.primary) {
                    ExpandedPrimaryComponent.CHAT,
                    ExpandedPrimaryComponent.ICON_TEXT -> {
                        PreviewPicture(
                            accent = accent,
                            selected = primaryImageSelected,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    ExpandedPrimaryComponent.HIGHLIGHT -> {
                        PreviewPicture(
                            accent = accent,
                            selected = primaryImageSelected,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    ExpandedPrimaryComponent.COVER -> {
                        PreviewPicture(
                            accent = accent,
                            selected = primaryImageSelected,
                            modifier = Modifier.size(width = 42.dp, height = 54.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                    }
                    ExpandedPrimaryComponent.TEXT_ONE,
                    ExpandedPrimaryComponent.TEXT_TWO -> Unit
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        color = accent,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        subtitle,
                        color = Color(0xFFE8E8EA),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (
                        extraText.isNotBlank() &&
                        template.primary in setOf(
                            ExpandedPrimaryComponent.COVER,
                            ExpandedPrimaryComponent.HIGHLIGHT
                        )
                    ) {
                        Text(
                            extraText,
                            color = Color(0xFF96979C),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    }
                }
                template.recognitionPictureType?.let { type ->
                    Spacer(Modifier.width(10.dp))
                    if (type == 5) {
                        Row(
                            modifier = Modifier
                                .background(Color(0xFF24252A), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.size(16.dp).background(secondaryAccent, CircleShape))
                            Spacer(Modifier.width(5.dp))
                            Text(
                                digitText,
                                color = accent,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        PreviewPicture(
                            accent = secondaryAccent,
                            selected = false,
                            modifier = Modifier.size(if (type == 3) 48.dp else 30.dp)
                        )
                    }
                }
            }
            ExpandedAccessoryPreview(
                accessory = template.accessory,
                progress = progress,
                label = actionLabel,
                extraText = extraText,
                accent = accent,
                secondaryAccent = secondaryAccent
            )
        }
    }
}

@Composable
private fun ExpandedAccessoryPreview(
    accessory: ExpandedAccessory,
    progress: Int,
    label: String,
    extraText: String,
    accent: Color,
    secondaryAccent: Color
) {
    when (accessory) {
        ExpandedAccessory.NONE,
        ExpandedAccessory.COUNTDOWN_PICTURE -> Unit
        ExpandedAccessory.PROGRESS_ONE,
        ExpandedAccessory.PROGRESS_TWO -> LinearProgressIndicator(
            progress = { progress / 100f },
            modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
            color = accent,
            trackColor = Color(0xFF3B3C42)
        )
        ExpandedAccessory.MULTI_PROGRESS -> Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                extraText.ifBlank { stringResource(R.string.placeholder_segmented_progress) },
                color = Color(0xFFD5D5D8),
                style = MaterialTheme.typography.labelSmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(4) { index ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(5.dp)
                            .background(
                                if (index < (progress + 24) / 25) accent else Color(0xFF3B3C42),
                                CircleShape
                            )
                    )
                }
            }
        }
        ExpandedAccessory.ACTIONS,
        ExpandedAccessory.TEXT_BUTTONS -> Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            PreviewAction(label, accent)
            if (accessory == ExpandedAccessory.TEXT_BUTTONS) {
                Spacer(Modifier.width(7.dp))
                PreviewAction(stringResource(R.string.action_more), secondaryAccent)
            }
        }
        ExpandedAccessory.HINT_TWO,
        ExpandedAccessory.HINT_THREE,
        ExpandedAccessory.HIGHLIGHT_ACTION -> Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                extraText.ifBlank { stringResource(R.string.placeholder_status) },
                modifier = Modifier.weight(1f),
                color = secondaryAccent,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
            PreviewAction(label, accent)
        }
    }
}

@Composable
private fun PreviewAction(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            label,
            color = if (color.luminance() > 0.58f) Color(0xFF121212) else Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun PreviewPicture(
    accent: Color,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(accent.copy(alpha = 0.9f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            if (selected) Icons.Rounded.Image else Icons.Rounded.Apps,
            contentDescription = null,
            tint = if (accent.luminance() > 0.58f) Color(0xFF111111) else Color.White,
            modifier = Modifier.fillMaxSize(0.52f)
        )
    }
}

@Composable
private fun OfficialLargeIslandPreview(
    template: LargeIslandTemplate,
    title: String,
    digitText: String,
    leadingText: String,
    trailingText: String,
    progress: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .widthIn(max = 330.dp)
                .height(50.dp)
                .background(Color.Black, RoundedCornerShape(50))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryLeftZone(template, title, digitText, trailingText, accent)
            Spacer(Modifier.width(42.dp))
            SummaryRightZone(
                template = template,
                title = title,
                digitText = digitText,
                leadingText = leadingText,
                trailingText = trailingText,
                progress = progress,
                accent = accent,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RowScope.SummaryLeftZone(
    template: LargeIslandTemplate,
    title: String,
    digitText: String,
    trailingText: String,
    accent: Color
) {
    Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(20.dp).background(accent, RoundedCornerShape(7.dp)))
        Spacer(Modifier.width(5.dp))
        Text(
            if (template == LargeIslandTemplate.FIXED_IMAGE_TEXT_PAIR) {
                digitText
            } else {
                title
            },
            color = accent,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (template == LargeIslandTemplate.FIXED_IMAGE_TEXT_PAIR && trailingText.isNotBlank()) {
            Text(
                trailingText,
                color = Color(0xFFB2B3B7),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SummaryRightZone(
    template: LargeIslandTemplate,
    title: String,
    digitText: String,
    leadingText: String,
    trailingText: String,
    progress: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (template) {
            LargeIslandTemplate.LEFT_ONLY -> Unit
            LargeIslandTemplate.LEFT_TEXT -> SummaryText(digitText, leadingText, trailingText, accent)
            LargeIslandTemplate.LEFT_IMAGE_TEXT_TWO -> {
                SummaryText(digitText, leadingText, trailingText, accent)
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(18.dp).background(accent, RoundedCornerShape(6.dp)))
            }
            LargeIslandTemplate.LEFT_IMAGE_TEXT_THREE -> {
                Text(
                    digitText,
                    color = accent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(18.dp).background(accent, CircleShape))
            }
            LargeIslandTemplate.LEFT_PROGRESS_TEXT -> {
                Text(
                    stringResource(R.string.value_percent, progress),
                    color = accent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(5.dp))
                CircularProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.size(22.dp),
                    color = accent,
                    trackColor = Color(0xFF3B3C42),
                    strokeWidth = 3.dp
                )
            }
            LargeIslandTemplate.LEFT_SAME_WIDTH_DIGIT,
            LargeIslandTemplate.LEFT_FIXED_WIDTH_DIGIT -> Text(
                digitText,
                color = accent,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            LargeIslandTemplate.LEFT_LARGE_PICTURE -> Box(
                Modifier.size(width = 48.dp, height = 22.dp)
                    .background(accent, RoundedCornerShape(7.dp))
            )
            LargeIslandTemplate.FIXED_IMAGE_TEXT_PAIR -> {
                Text(
                    digitText,
                    color = accent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(18.dp).background(accent, RoundedCornerShape(6.dp)))
            }
        }
    }
}

@Composable
private fun SummaryText(value: String, leading: String, trailing: String, accent: Color) {
    Text(
        listOf(leading, value, trailing).filter { it.isNotBlank() }.joinToString(" "),
        color = accent,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun OfficialSmallIslandPreview(
    template: SmallIslandTemplate,
    digitText: String,
    progress: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (template) {
            SmallIslandTemplate.ICON -> Box(
                Modifier.size(38.dp).background(Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.size(18.dp).background(accent, RoundedCornerShape(6.dp)))
            }
            SmallIslandTemplate.PROGRESS_ICON -> Box(
                Modifier.size(42.dp).background(Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.size(30.dp),
                    color = accent,
                    trackColor = Color(0xFF33343A),
                    strokeWidth = 3.dp
                )
                Box(Modifier.size(14.dp).background(accent, RoundedCornerShape(5.dp)))
            }
            SmallIslandTemplate.ICON_TEXT -> Row(
                modifier = Modifier
                    .height(38.dp)
                    .background(Color.Black, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    digitText,
                    color = accent,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(5.dp))
                Box(Modifier.size(16.dp).background(accent, RoundedCornerShape(5.dp)))
            }
        }
    }
}

@Composable
private fun ActionEditor(
    template: ExpandedTemplate,
    actionStyleIndex: Int,
    onActionStyleChange: (Int) -> Unit,
    actionOne: String,
    onActionOneChange: (String) -> Unit,
    actionTwo: String,
    onActionTwoChange: (String) -> Unit,
    actionThree: String,
    onActionThreeChange: (String) -> Unit
) {
    val supportsMultiple = template.accessory in setOf(
        ExpandedAccessory.ACTIONS,
        ExpandedAccessory.TEXT_BUTTONS
    )
    val maxActions = when {
        template.accessory == ExpandedAccessory.TEXT_BUTTONS -> 2
        template.accessory == ExpandedAccessory.ACTIONS &&
            ActionStyle.entries[actionStyleIndex] != ActionStyle.TEXT -> 3
        else -> 1
    }
    SectionCard(
        title = stringResource(R.string.section_actions),
        subtitle = if (supportsMultiple) {
            stringResource(R.string.section_actions_multiple, maxActions)
        } else {
            stringResource(R.string.section_actions_single)
        }
    ) {
        if (template.accessory == ExpandedAccessory.ACTIONS) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ActionStyle.entries) { style ->
                    FilterChip(
                        selected = ActionStyle.entries[actionStyleIndex] == style,
                        onClick = { onActionStyleChange(ActionStyle.entries.indexOf(style)) },
                        label = { Text(stringResource(style.labelRes)) },
                        colors = appFilterChipColors()
                    )
                }
            }
        }
        CompactTextField(
            value = actionOne,
            onValueChange = onActionOneChange,
            label = stringResource(R.string.action_button_number, 1),
            modifier = Modifier.fillMaxWidth()
        )
        if (maxActions >= 2) {
            CompactTextField(
                value = actionTwo,
                onValueChange = onActionTwoChange,
                label = stringResource(R.string.action_button_number, 2),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (maxActions >= 3) {
            CompactTextField(
                value = actionThree,
                onValueChange = onActionThreeChange,
                label = stringResource(R.string.action_button_number, 3),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    limit: Int,
    imeAction: ImeAction,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        supportingText = {
            Text(stringResource(R.string.character_count, value.length, limit))
        },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 2,
        maxLines = if (singleLine) 1 else 3,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        colors = appOutlinedTextFieldColors()
    )
}

@Composable
private fun appOutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@Composable
private fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        singleLine = true,
        colors = appOutlinedTextFieldColors()
    )
}

@Composable
private fun ImagePickerButton(
    title: String,
    selectedUri: String?,
    onSelect: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                RoundedCornerShape(18.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (selectedUri == null) Icons.Rounded.PhotoLibrary else Icons.Rounded.Image,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleSmall)
        }
        Text(
            if (selectedUri == null) {
                stringResource(R.string.image_use_app_icon)
            } else {
                selectedImageName(
                    selectedUri,
                    fallback = stringResource(R.string.image_custom_selected)
                )
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        FilledTonalButton(
            onClick = onSelect,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text(
                if (selectedUri == null) {
                    stringResource(R.string.action_select_image)
                } else {
                    stringResource(R.string.action_change_image)
                }
            )
        }
        if (selectedUri != null) {
            TextButton(
                onClick = onClear,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.action_restore_default))
            }
        }
    }
}

private fun selectedImageName(rawUri: String, fallback: String): String = runCatching {
    Uri.parse(rawUri).lastPathSegment?.substringAfterLast('/')?.takeLast(24)
}.getOrNull().orEmpty().ifBlank { fallback }

@Composable
private fun ColorPickerRow(title: String, selectedIndex: Int, onSelected: (Int) -> Unit) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        accentColors.forEachIndexed { index, color ->
            AccentSwatch(
                color = color,
                selected = selectedIndex == index,
                onClick = { onSelected(index) }
            )
        }
    }
}

@Composable
private fun appFilterChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    labelColor = MaterialTheme.colorScheme.onSurface,
    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@Composable
private fun Modifier.contentWidth(): Modifier = fillMaxWidth().widthIn(max = 720.dp)

@Composable
private fun BoxScope.PagerEdgeFadeOverlay(edgeColor: Color) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .drawWithCache {
                val fadeBrush = Brush.horizontalGradient(
                    0f to edgeColor,
                    0.11f to Color.Transparent,
                    0.89f to Color.Transparent,
                    1f to edgeColor
                )
                onDrawBehind { drawRect(fadeBrush) }
            }
    )
}

@Composable
private fun PreviewDisclaimer() {
    Text(
        text = stringResource(R.string.preview_disclaimer),
        modifier = Modifier.contentWidth().padding(horizontal = 8.dp),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun CapabilityBanner(capability: DeviceCapability) {
    val (title, subtitle, color) = when (capability) {
        DeviceCapability.READY -> Triple(
            stringResource(R.string.capability_ready_title),
            stringResource(R.string.capability_ready_description),
            MaterialTheme.colorScheme.primaryContainer
        )
        DeviceCapability.XIAOMI_FALLBACK -> Triple(
            stringResource(R.string.capability_fallback_title),
            stringResource(R.string.capability_fallback_description),
            MaterialTheme.colorScheme.tertiaryContainer
        )
        DeviceCapability.OTHER_ANDROID -> Triple(
            stringResource(R.string.capability_unsupported_title),
            stringResource(R.string.capability_unsupported_description),
            MaterialTheme.colorScheme.secondaryContainer
        )
    }
    Surface(
        modifier = Modifier.contentWidth(),
        color = color,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (capability == DeviceCapability.READY) {
                    Icons.Rounded.NotificationsActive
                } else {
                    Icons.Rounded.Info
                },
                contentDescription = null
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.contentWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content()
        }
    }
}

@Composable
private fun AccentSwatch(color: Color, selected: Boolean, onClick: () -> Unit) {
    val background by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
        label = "accent selection"
    )
    val description = if (selected) {
        stringResource(R.string.color_selected)
    } else {
        stringResource(R.string.color_select)
    }
    Box(
        modifier = Modifier
            .size(43.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics { contentDescription = description }
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.fillMaxSize().background(color, CircleShape))
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

@Composable
private fun ActiveIslandSection(
    activeIslands: List<ActiveIsland>,
    onCancel: (Int) -> Unit
) {
    Card(
        modifier = Modifier.contentWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.active_island_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        stringResource(R.string.active_island_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            activeIslands.firstOrNull()?.let { ActiveIslandRow(it, onCancel) }
        }
    }
}

@Composable
private fun ActiveIslandRow(island: ActiveIsland, onCancel: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, top = 10.dp, end = 8.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(12.dp).background(Color(island.accentColor), CircleShape))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                island.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val sentAt = remember(island.createdAtMillis) {
                DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(island.createdAtMillis))
            }
            val metadata = if (island.subtitle.isBlank()) {
                sentAt
            } else {
                stringResource(R.string.active_island_metadata, island.subtitle, sentAt)
            }
            Text(
                metadata,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = { onCancel(island.notificationId) }) {
            Icon(
                Icons.Rounded.Close,
                contentDescription = stringResource(
                    R.string.withdraw_island_description,
                    island.title
                )
            )
        }
    }
}

private fun Color.luminance(): Float =
    0.2126f * red + 0.7152f * green + 0.0722f * blue
