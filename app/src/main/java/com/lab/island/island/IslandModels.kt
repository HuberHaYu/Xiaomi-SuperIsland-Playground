package com.lab.island.island

import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.lab.island.R

/** The primary component occupying the upper part of an expanded Focus V3 template. */
enum class ExpandedPrimaryComponent {
    TEXT_ONE,
    TEXT_TWO,
    CHAT,
    HIGHLIGHT,
    ICON_TEXT,
    COVER
}

/** Optional lower component from Xiaomi's expanded-state template library. */
enum class ExpandedAccessory {
    NONE,
    COUNTDOWN_PICTURE,
    PROGRESS_ONE,
    PROGRESS_TWO,
    MULTI_PROGRESS,
    ACTIONS,
    HINT_TWO,
    HINT_THREE,
    TEXT_BUTTONS,
    HIGHLIGHT_ACTION
}

/**
 * Every expanded-state combination in Xiaomi's 2026-01-29 Super Island template library.
 * Template 14 has two official variants, hence 22 selectable combinations for 21 numbers.
 */
enum class ExpandedTemplate(
    val number: String,
    @param:StringRes val labelRes: Int,
    val primary: ExpandedPrimaryComponent,
    val recognitionPictureType: Int?,
    val accessory: ExpandedAccessory,
    val os3Only: Boolean = false
) {
    TEXT_ONE_LARGE_PICTURE(
        "1", R.string.template_expanded_1, ExpandedPrimaryComponent.TEXT_ONE, 3,
        ExpandedAccessory.NONE
    ),
    TEXT_TWO_APP_ICON(
        "2", R.string.template_expanded_2, ExpandedPrimaryComponent.TEXT_TWO, 1,
        ExpandedAccessory.NONE
    ),
    CHAT_MIDDLE_PICTURE(
        "3", R.string.template_expanded_3, ExpandedPrimaryComponent.CHAT, 2,
        ExpandedAccessory.NONE
    ),
    TEXT_TWO_ROUTE_PROGRESS(
        "4", R.string.template_expanded_4, ExpandedPrimaryComponent.TEXT_TWO, 1,
        ExpandedAccessory.PROGRESS_ONE
    ),
    TEXT_ONE_PROGRESS(
        "5", R.string.template_expanded_5, ExpandedPrimaryComponent.TEXT_ONE, 1,
        ExpandedAccessory.PROGRESS_TWO
    ),
    TEXT_TWO_PROGRESS(
        "6", R.string.template_expanded_6, ExpandedPrimaryComponent.TEXT_TWO, 1,
        ExpandedAccessory.PROGRESS_TWO
    ),
    CHAT_PROGRESS(
        "7", R.string.template_expanded_7, ExpandedPrimaryComponent.CHAT, 1,
        ExpandedAccessory.PROGRESS_TWO
    ),
    CHAT_HINT_THREE(
        "8", R.string.template_expanded_8, ExpandedPrimaryComponent.CHAT, 1,
        ExpandedAccessory.HINT_THREE
    ),
    TEXT_TWO_HINT_TWO(
        "9", R.string.template_expanded_9, ExpandedPrimaryComponent.TEXT_TWO, 1,
        ExpandedAccessory.HINT_TWO
    ),
    TEXT_TWO_HINT_THREE(
        "10", R.string.template_expanded_10, ExpandedPrimaryComponent.TEXT_TWO, 1,
        ExpandedAccessory.HINT_THREE
    ),
    HIGHLIGHT_HINT_TWO(
        "11", R.string.template_expanded_11, ExpandedPrimaryComponent.HIGHLIGHT, 1,
        ExpandedAccessory.HINT_TWO
    ),
    CHAT_ACTIONS(
        "12", R.string.template_expanded_12, ExpandedPrimaryComponent.CHAT, null,
        ExpandedAccessory.ACTIONS
    ),
    HIGHLIGHT_ACTIONS(
        "13", R.string.template_expanded_13, ExpandedPrimaryComponent.HIGHLIGHT, null,
        ExpandedAccessory.ACTIONS
    ),
    ICON_TEXT(
        "14-1", R.string.template_expanded_14_1, ExpandedPrimaryComponent.ICON_TEXT, null,
        ExpandedAccessory.NONE, os3Only = true
    ),
    ICON_TEXT_COUNTDOWN(
        "14-2", R.string.template_expanded_14_2, ExpandedPrimaryComponent.ICON_TEXT, 5,
        ExpandedAccessory.COUNTDOWN_PICTURE, os3Only = true
    ),
    ICON_TEXT_ACTIONS(
        "15", R.string.template_expanded_15, ExpandedPrimaryComponent.ICON_TEXT, null,
        ExpandedAccessory.ACTIONS, os3Only = true
    ),
    ICON_TEXT_HIGHLIGHT_ACTION(
        "16", R.string.template_expanded_16, ExpandedPrimaryComponent.ICON_TEXT, 1,
        ExpandedAccessory.HIGHLIGHT_ACTION, os3Only = true
    ),
    ICON_TEXT_TEXT_BUTTONS(
        "17", R.string.template_expanded_17, ExpandedPrimaryComponent.ICON_TEXT, 1,
        ExpandedAccessory.TEXT_BUTTONS, os3Only = true
    ),
    COVER_HIGHLIGHT_ACTION(
        "18", R.string.template_expanded_18, ExpandedPrimaryComponent.COVER, 1,
        ExpandedAccessory.HIGHLIGHT_ACTION, os3Only = true
    ),
    TEXT_TWO_MULTI_PROGRESS(
        "19", R.string.template_expanded_19, ExpandedPrimaryComponent.TEXT_TWO, 1,
        ExpandedAccessory.MULTI_PROGRESS, os3Only = true
    ),
    CHAT_PROGRESS_ONLY(
        "20", R.string.template_expanded_20, ExpandedPrimaryComponent.CHAT, null,
        ExpandedAccessory.PROGRESS_TWO, os3Only = true
    ),
    ICON_TEXT_MULTI_PROGRESS(
        "21", R.string.template_expanded_21, ExpandedPrimaryComponent.ICON_TEXT, 1,
        ExpandedAccessory.MULTI_PROGRESS, os3Only = true
    );

    val usesProgress: Boolean
        get() = accessory in setOf(
            ExpandedAccessory.PROGRESS_ONE,
            ExpandedAccessory.PROGRESS_TWO,
            ExpandedAccessory.MULTI_PROGRESS
        )

    val usesActions: Boolean
        get() = accessory in setOf(
            ExpandedAccessory.ACTIONS,
            ExpandedAccessory.HINT_TWO,
            ExpandedAccessory.HINT_THREE,
            ExpandedAccessory.TEXT_BUTTONS,
            ExpandedAccessory.HIGHLIGHT_ACTION
        )

    val usesExtraText: Boolean
        get() = primary in setOf(
            ExpandedPrimaryComponent.TEXT_ONE,
            ExpandedPrimaryComponent.TEXT_TWO,
            ExpandedPrimaryComponent.HIGHLIGHT,
            ExpandedPrimaryComponent.COVER
        ) || accessory in setOf(
            ExpandedAccessory.HINT_TWO,
            ExpandedAccessory.HINT_THREE,
            ExpandedAccessory.HIGHLIGHT_ACTION,
            ExpandedAccessory.MULTI_PROGRESS
        )
}

/** The nine official large-island A/B area combinations. */
enum class LargeIslandTemplate(val number: Int, @param:StringRes val labelRes: Int) {
    LEFT_ONLY(1, R.string.template_large_1),
    LEFT_TEXT(2, R.string.template_large_2),
    LEFT_IMAGE_TEXT_TWO(3, R.string.template_large_3),
    LEFT_IMAGE_TEXT_THREE(4, R.string.template_large_4),
    LEFT_PROGRESS_TEXT(5, R.string.template_large_5),
    LEFT_SAME_WIDTH_DIGIT(6, R.string.template_large_6),
    LEFT_FIXED_WIDTH_DIGIT(7, R.string.template_large_7),
    LEFT_LARGE_PICTURE(8, R.string.template_large_8),
    FIXED_IMAGE_TEXT_PAIR(9, R.string.template_large_9)
}

/** The three official small-island component choices. */
enum class SmallIslandTemplate(@param:StringRes val labelRes: Int) {
    ICON(R.string.template_small_icon),
    PROGRESS_ICON(R.string.template_small_progress_icon),
    ICON_TEXT(R.string.template_small_icon_text)
}

enum class IslandProperty(val protocolValue: Int, @param:StringRes val labelRes: Int) {
    INFORMATION(1, R.string.island_property_information),
    ACTION(2, R.string.island_property_action)
}

enum class ActionStyle(@param:StringRes val labelRes: Int) {
    ROUND(R.string.action_style_round),
    PROGRESS(R.string.action_style_progress),
    TEXT(R.string.action_style_text)
}

/** Selects the system presentation path used by the notification. */
enum class IslandScene(
    @param:StringRes val labelRes: Int,
    @param:StringRes val descriptionRes: Int
) {
    GENERAL(R.string.scene_general, R.string.scene_general_description),
    NAVIGATION(R.string.scene_navigation, R.string.scene_navigation_description),
    MUSIC(R.string.scene_music, R.string.scene_music_description)
}

data class IslandDraft(
    val scene: IslandScene,
    val expandedTemplate: ExpandedTemplate,
    val largeIslandTemplate: LargeIslandTemplate,
    val smallIslandTemplate: SmallIslandTemplate,
    val title: String,
    val subtitle: String,
    val extraText: String,
    val source: String,
    val leadingText: String,
    val trailingText: String,
    val digitText: String,
    val progressLabel: String,
    val actionLabels: List<String>,
    val actionStyle: ActionStyle,
    val progress: Int,
    val progressPoints: Int,
    val primaryImageUri: String?,
    val secondaryImageUri: String?,
    @param:ColorInt val accentColor: Int,
    @param:ColorInt val secondaryAccentColor: Int,
    val duration: IslandDuration,
    val islandProperty: IslandProperty,
    val autoExpand: Boolean,
    val useAccentBackground: Boolean,
    val narrowFont: Boolean,
    val mediaPlaying: Boolean,
    val mediaShareUrl: String,
    /** Package opened when the notification, expanded island, or an island action is tapped. */
    val targetPackageName: String?
)

enum class IslandDuration(@param:StringRes val labelRes: Int, val timeoutMillis: Long) {
    FIVE_MINUTES(R.string.duration_five_minutes, 5 * 60 * 1_000L),
    THIRTY_MINUTES(R.string.duration_thirty_minutes, 30 * 60 * 1_000L),
    TWO_HOURS(R.string.duration_two_hours, 2 * 60 * 60 * 1_000L),
    TWELVE_HOURS(R.string.duration_twelve_hours, 12 * 60 * 60 * 1_000L),
    UNTIL_CANCELLED(R.string.duration_until_cancelled, 0L)
}

data class ActiveIsland(
    val notificationId: Int,
    val title: String,
    val subtitle: String,
    @param:ColorInt val accentColor: Int,
    val createdAtMillis: Long,
    val scene: IslandScene = IslandScene.GENERAL,
    val mediaPlaying: Boolean = false,
    val targetPackageName: String? = null
)

enum class DeviceCapability {
    READY,
    XIAOMI_FALLBACK,
    OTHER_ANDROID
}

enum class PublishKind {
    SUPER_ISLAND,
    REGULAR_NOTIFICATION,
    ERROR
}

data class PublishOutcome(
    val kind: PublishKind,
    val message: String
)
