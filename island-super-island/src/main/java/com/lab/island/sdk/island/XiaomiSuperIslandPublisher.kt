package com.lab.island.sdk.island

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.Icon
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.LruCache
import androidx.core.text.htmlEncode
import androidx.core.graphics.drawable.toBitmap
import com.lab.island.sdk.R
import org.json.JSONArray
import org.json.JSONObject
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.util.Locale

/** Builds the HyperOS FocusTemplateV3 payload consumed by Xiaomi Super Island. */
internal class XiaomiSuperIslandPublisher(
    context: Context,
    private val notificationManager: NotificationManager,
    private val onMediaPlaybackChanged: (Boolean) -> Unit,
    private val onMediaSeekChanged: (Int) -> Unit
) {
    private val appContext = context.applicationContext
    private val xmsfGate = XiaomiAurogonNetworkGate(appContext)
    private val pictureCache = LruCache<String, Bitmap>(4)
    private var mediaSession: MediaSession? = null

    @Suppress("PrivateApi")
    fun detectCapability(): DeviceCapability {
        val manufacturer = Build.MANUFACTURER.orEmpty().lowercase(Locale.ROOT)
        if (!manufacturer.contains("xiaomi")) return DeviceCapability.OTHER_ANDROID

        val protocol = runCatching {
            Settings.System.getInt(appContext.contentResolver, FOCUS_PROTOCOL_SETTING, 0)
        }.getOrDefault(0)
        if (protocol < 3 || !xmsfGate.isSupported) return DeviceCapability.XIAOMI_FALLBACK

        val islandEnabled = runCatching {
            HiddenApiBypass.invoke(
                Class.forName("android.os.SystemProperties"),
                null,
                "getBoolean",
                ISLAND_FEATURE_PROPERTY,
                false
            ) as? Boolean
        }.getOrNull()
        return if (islandEnabled == false) {
            DeviceCapability.XIAOMI_FALLBACK
        } else {
            DeviceCapability.READY
        }
    }

    fun buildNotification(
        channelId: String,
        notificationId: Int,
        draft: IslandDraft,
        firstFrame: Boolean = true
    ): Notification = if (draft.scene == IslandScene.MUSIC) {
        buildMediaNotification(channelId, notificationId, draft)
    } else {
        mediaSession?.isActive = false
        buildFocusNotification(channelId, notificationId, draft, firstFrame)
    }

    private fun buildFocusNotification(
        channelId: String,
        notificationId: Int,
        draft: IslandDraft,
        firstFrame: Boolean
    ): Notification {
        val contentIntent = buildContentPendingIntent(notificationId, draft)

        val fallbackIcon = applicationIconBitmap()
            ?.let(Icon::createWithBitmap)
            ?: Icon.createWithResource(appContext, R.drawable.ic_island_notification)
        val primaryIcon = decodePicture(draft.primaryImageUri) ?: fallbackIcon
        val secondaryIcon = decodePicture(draft.secondaryImageUri) ?: primaryIcon
        val pictures = Bundle().apply {
            putParcelable(LOGO_KEY, fallbackIcon)
            putParcelable(PRIMARY_IMAGE_KEY, primaryIcon)
            putParcelable(SECONDARY_IMAGE_KEY, secondaryIcon)
        }
        val shouldFloat = firstFrame && draft.autoExpand
        val extras = Bundle().apply {
            putString(FOCUS_PARAM, buildFocusPayload(draft, firstFrame))
            putBundle(FOCUS_PICS, pictures)
            putBoolean(ISLAND_UPDATE_NO_FLOAT, !shouldFloat)
            putBoolean(ISLAND_FIRST_FLOAT, shouldFloat)
            putBoolean(MIUI_ENABLE_FLOAT, shouldFloat)
        }

        return Notification.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_island_notification)
            .setContentTitle(draft.title)
            .setContentText(draft.subtitle.ifBlank { " " })
            .setSubText(draft.source.ifBlank { null })
            .setContentIntent(contentIntent)
            .setCategory(
                if (draft.scene == IslandScene.NAVIGATION) {
                    Notification.CATEGORY_NAVIGATION
                } else {
                    Notification.CATEGORY_STATUS
                }
            )
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setAutoCancel(false)
            .setColor(draft.accentColor)
            .setColorized(false)
            .apply {
                if (draft.duration.timeoutMillis > 0) {
                    setTimeoutAfter(draft.duration.timeoutMillis)
                }
            }
            .addExtras(extras)
            .build()
    }

    /**
     * HyperOS derives its music island from the standard Android media contract. This path does
     * not attach a fake FocusTemplate payload; PlaybackState remains the source of media actions.
     */
    private fun buildMediaNotification(
        channelId: String,
        notificationId: Int,
        draft: IslandDraft
    ): Notification {
        val contentIntent = buildContentPendingIntent(notificationId, draft)
        val toggleIntent = PendingIntent.getBroadcast(
            appContext,
            notificationId + MEDIA_ACTION_REQUEST_OFFSET,
            Intent(appContext, MediaControlReceiver::class.java)
                .setAction(ACTION_TOGGLE_MEDIA)
                .setPackage(appContext.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val cover = decodeBitmap(draft.primaryImageUri)
            ?: applicationIconBitmap()
        val session = mediaSession ?: MediaSession(appContext, MEDIA_SESSION_TAG).also { created ->
            created.setCallback(
                object : MediaSession.Callback() {
                    override fun onPlay() = onMediaPlaybackChanged(true)

                    override fun onPause() = onMediaPlaybackChanged(false)

                    override fun onStop() = onMediaPlaybackChanged(false)

                    override fun onSeekTo(pos: Long) {
                        onMediaSeekChanged(
                            (pos * 100L / MEDIA_TIMELINE_MILLIS).toInt().coerceIn(0, 100)
                        )
                    }
                },
                Handler(Looper.getMainLooper())
            )
            mediaSession = created
        }
        session.setSessionActivity(contentIntent)
        session.setMetadata(
            MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, draft.title)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, draft.subtitle)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, draft.extraText)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, MEDIA_TIMELINE_MILLIS)
                .apply {
                    if (cover != null) {
                        putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, cover)
                        putBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON, cover)
                    }
                }
                .build()
        )
        val playbackActions = PlaybackState.ACTION_PLAY or
            PlaybackState.ACTION_PAUSE or
            PlaybackState.ACTION_PLAY_PAUSE or
            PlaybackState.ACTION_SEEK_TO
        session.setPlaybackState(
            PlaybackState.Builder()
                .setActions(playbackActions)
                .setState(
                    if (draft.mediaPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED,
                    draft.progress.coerceIn(0, 100) * MEDIA_TIMELINE_MILLIS / 100L,
                    if (draft.mediaPlaying) 1f else 0f
                )
                .build()
        )
        session.isActive = true

        val extras = Bundle().apply {
            if (draft.mediaShareUrl.isNotBlank()) {
                putString(MEDIA_FOCUS_PARAM, buildMediaSharePayload(draft))
            }
        }
        val toggleAction = Notification.Action.Builder(
            if (draft.mediaPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
            appContext.getString(
                if (draft.mediaPlaying) R.string.action_pause else R.string.action_play
            ),
            toggleIntent
        ).build()

        return Notification.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_island_notification)
            .setContentTitle(draft.title)
            .setContentText(draft.subtitle.ifBlank { " " })
            .setSubText(draft.extraText.ifBlank { draft.source.ifBlank { null } })
            .setContentIntent(contentIntent)
            .setLargeIcon(cover)
            .setCategory(Notification.CATEGORY_TRANSPORT)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(draft.mediaPlaying)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setAutoCancel(false)
            .setColor(draft.accentColor)
            .setColorized(false)
            .addAction(toggleAction)
            .setStyle(
                Notification.MediaStyle()
                    .setMediaSession(session.sessionToken)
                    .setShowActionsInCompactView(0)
            )
            .apply {
                if (draft.duration.timeoutMillis > 0) {
                    setTimeoutAfter(draft.duration.timeoutMillis)
                }
            }
            .addExtras(extras)
            .build()
    }

    private fun buildMediaSharePayload(draft: IslandDraft): String = JSONObject()
        .put(
            "param_v2",
            JSONObject().put(
                "param_island",
                JSONObject().put(
                    "shareData",
                    JSONObject()
                        .put("title", draft.title)
                        .put("content", draft.subtitle)
                        .put("shareContent", draft.mediaShareUrl)
                )
            )
        )
        .toString()

    /**
     * Matches the reference XMSF validation transaction. The process-wide gate is serialized by
     * [IslandController] and restoration is unconditional so another app's notifications are not
     * left behind a changed network rule.
     */
    fun postWithValidation(
        notificationId: Int,
        notification: Notification,
        stillCurrent: () -> Boolean = { true }
    ): Boolean {
        var posted = false
        var interrupted = false
        try {
            if (stillCurrent() && xmsfGate.setXmsfBlocked(true) && stillCurrent()) {
                notificationManager.notify(notificationId, notification)
                try {
                    Thread.sleep(XMSF_VALIDATION_WINDOW_MS)
                } catch (_: InterruptedException) {
                    interrupted = true
                }
                posted = stillCurrent()
                if (!posted) notificationManager.cancel(notificationId)
            }
        } catch (error: RuntimeException) {
            Log.w(TAG, "Super Island publish failed", error)
        } finally {
            interrupted = interrupted || Thread.interrupted()
            if (!xmsfGate.restoreIfNeeded()) {
                Log.e(TAG, "HyperOS did not confirm XMSF validation restoration")
                posted = false
            }
            if (interrupted) Thread.currentThread().interrupt()
        }
        return posted
    }

    fun restoreGate() {
        xmsfGate.restoreIfNeeded()
    }

    fun deactivateMediaSession() {
        mediaSession?.isActive = false
    }

    internal fun buildFocusPayload(
        draft: IslandDraft,
        firstFrame: Boolean
    ): String {
        val focusV3 = JSONObject()
            .put("protocol", 1)
            .put("business", BUSINESS_NAME)
            .put("ticker", draft.title)
            .put("tickerPic", LOGO_KEY)
            .put("aodTitle", draft.title)
            .put("aodPic", LOGO_KEY)
            // Keep the notification active after its content intent opens. HyperOS treats a
            // non-updatable Focus notification as one-shot and removes it after the click.
            .put("updatable", true)
            .put("reopen", "close")
            .put("enableFloat", firstFrame && draft.autoExpand)
            .put("islandFirstFloat", firstFrame && draft.autoExpand)
            .apply {
                val timeoutMinutes = draft.duration.timeoutMillis / 60_000L
                if (timeoutMinutes > 0) put("timeout", timeoutMinutes.coerceAtMost(720L))
            }

        putPrimaryComponent(focusV3, draft)
        draft.expandedTemplate.recognitionPictureType?.let { type ->
            focusV3.put("picInfo", buildRecognitionPicture(type, draft))
        }
        putExpandedAccessory(focusV3, draft)
        if (draft.useAccentBackground) {
            focusV3.put(
                "bgInfo",
                JSONObject().put("type", 1).put("colorBg", colorToHtml(draft.accentColor))
            )
        }
        focusV3.put("param_island", buildIslandPayload(draft))

        val payload = JSONObject()
            .put("type", FOCUS_V3_SERIAL_NAME)
            .put("param_v2", focusV3)
            .toString()
        require(payload.toByteArray(Charsets.UTF_8).size <= MAX_FOCUS_PARAMETER_BYTES) {
            appContext.getString(R.string.error_template_too_large)
        }
        return payload
    }

    private fun putPrimaryComponent(target: JSONObject, draft: IslandDraft) {
        val accent = colorToHtml(draft.accentColor)
        when (draft.expandedTemplate.primary) {
            ExpandedPrimaryComponent.TEXT_ONE,
            ExpandedPrimaryComponent.TEXT_TWO -> {
                val type = if (
                    draft.expandedTemplate.primary == ExpandedPrimaryComponent.TEXT_ONE
                ) 1 else 2
                target.put(
                    "baseInfo",
                    JSONObject()
                        .put("type", type)
                        .put("title", escapeHtml(draft.title))
                        .put("content", escapeHtml(draft.subtitle.ifBlank { " " }))
                        .putIfNotBlank("subTitle", escapeHtml(draft.extraText))
                        .put("showDivider", draft.extraText.isNotBlank())
                        .put("colorTitle", accent)
                        .put("colorTitleDark", accent)
                )
            }

            ExpandedPrimaryComponent.CHAT -> target.put(
                "chatInfo",
                JSONObject()
                    .put("picProfile", PRIMARY_IMAGE_KEY)
                    .put("picProfileDark", PRIMARY_IMAGE_KEY)
                    .put("title", escapeHtml(draft.title))
                    .put("content", escapeHtml(draft.subtitle.ifBlank { " " }))
                    .put("colorTitle", accent)
                    .put("colorTitleDark", accent)
            )

            ExpandedPrimaryComponent.HIGHLIGHT -> target.put(
                "highlightInfo",
                JSONObject()
                    .put("title", escapeHtml(draft.title))
                    .put("content", escapeHtml(draft.subtitle.ifBlank { " " }))
                    .putIfNotBlank("subContent", escapeHtml(draft.extraText))
                    .put("picFunction", PRIMARY_IMAGE_KEY)
                    .put("picFunctionDark", PRIMARY_IMAGE_KEY)
                    .put("colorTitle", accent)
                    .put("colorTitleDark", accent)
            )

            ExpandedPrimaryComponent.ICON_TEXT -> target.put(
                "iconTextInfo",
                JSONObject()
                    .put("title", escapeHtml(draft.title))
                    .put("content", escapeHtml(draft.subtitle.ifBlank { " " }))
                    .putIfNotBlank("subContent", escapeHtml(draft.extraText))
                    .put("colorTitle", accent)
                    .put("colorTitleDark", accent)
                    .put(
                        "animIconInfo",
                        JSONObject()
                            .put("type", 0)
                            .put("src", PRIMARY_IMAGE_KEY)
                            .put("srcDark", PRIMARY_IMAGE_KEY)
                    )
            )

            ExpandedPrimaryComponent.COVER -> target.put(
                "coverInfo",
                JSONObject()
                    .put("picCover", PRIMARY_IMAGE_KEY)
                    .put("title", escapeHtml(draft.title))
                    .put("content", escapeHtml(draft.subtitle.ifBlank { " " }))
                    .put("subContent", escapeHtml(draft.extraText.ifBlank { " " }))
                    .put("colorTitle", accent)
                    .put("colorTitleDark", accent)
            )
        }
    }

    private fun buildRecognitionPicture(type: Int, draft: IslandDraft): JSONObject =
        JSONObject()
            .put("type", type)
            .put("pic", SECONDARY_IMAGE_KEY)
            .put("picDark", SECONDARY_IMAGE_KEY)
            .apply {
                if (type == 5) {
                    put("title", draft.digitText.ifBlank { "30" })
                    put("colorTitle", colorToHtml(draft.accentColor))
                }
            }

    private fun putExpandedAccessory(target: JSONObject, draft: IslandDraft) {
        val accent = colorToHtml(draft.accentColor)
        val secondaryAccent = colorToHtml(draft.secondaryAccentColor)
        when (draft.expandedTemplate.accessory) {
            ExpandedAccessory.NONE,
            ExpandedAccessory.COUNTDOWN_PICTURE -> Unit

            ExpandedAccessory.PROGRESS_ONE -> target.put(
                "progressInfo",
                buildProgress(draft)
                    .put("picForward", SECONDARY_IMAGE_KEY)
                    .put("picMiddle", SECONDARY_IMAGE_KEY)
                    .put("picMiddleUnselected", PRIMARY_IMAGE_KEY)
                    .put("picEnd", SECONDARY_IMAGE_KEY)
                    .put("picEndUnselected", PRIMARY_IMAGE_KEY)
            )

            ExpandedAccessory.PROGRESS_TWO -> target.put("progressInfo", buildProgress(draft))

            ExpandedAccessory.MULTI_PROGRESS -> target.put(
                "multiProgressInfo",
                JSONObject()
                    .put(
                        "title",
                        escapeHtml(
                            draft.progressLabel.ifBlank {
                                draft.extraText.ifBlank {
                                    appContext.getString(R.string.placeholder_progress)
                                }
                            }
                        )
                    )
                    .put("progress", draft.progress)
                    .put("color", accent)
                    .put("points", draft.progressPoints)
            )

            ExpandedAccessory.ACTIONS -> {
                val labels = resolvedActionLabels(draft)
                val limit = if (draft.actionStyle == ActionStyle.TEXT) 1 else 3
                target.put(
                    "actions",
                    JSONArray().apply {
                        labels.take(limit).forEach { label ->
                            put(buildActionInfo(label, draft, includeType = true))
                        }
                    }
                )
            }

            ExpandedAccessory.HINT_TWO,
            ExpandedAccessory.HINT_THREE -> {
                val type = if (draft.expandedTemplate.accessory == ExpandedAccessory.HINT_TWO) 2 else 1
                target.put(
                    "hintInfo",
                    JSONObject()
                        .put("type", type)
                        .put("title", escapeHtml(draft.digitText.ifBlank { draft.title }))
                        .putIfNotBlank("subTitle", escapeHtml(draft.trailingText))
                        .put(
                            "content",
                            escapeHtml(
                                draft.extraText.ifBlank {
                                    draft.subtitle.ifBlank {
                                        appContext.getString(R.string.placeholder_status)
                                    }
                                }
                            )
                        )
                        .putIfNotBlank("subContent", escapeHtml(draft.leadingText))
                        .put("colorTitle", accent)
                        .put("colorTitleDark", accent)
                        .apply {
                            if (type == 1) {
                                put("picContent", SECONDARY_IMAGE_KEY)
                                put("colorContentBg", secondaryAccent)
                            }
                        }
                        .put("actionInfo", buildActionInfo(resolvedActionLabels(draft).first(), draft))
                )
            }

            ExpandedAccessory.TEXT_BUTTONS -> target.put(
                "textButton",
                JSONArray().apply {
                    resolvedActionLabels(draft).take(2).forEach { label ->
                        put(buildActionInfo(label, draft))
                    }
                }
            )

            ExpandedAccessory.HIGHLIGHT_ACTION -> target.put(
                "highlightInfoV3",
                JSONObject()
                    .put("primaryText", escapeHtml(draft.title))
                    .putIfNotBlank("secondaryText", escapeHtml(draft.subtitle))
                    .putIfNotBlank("highLightText", escapeHtml(draft.extraText))
                    .put("primaryColor", accent)
                    .put("primaryColorDark", accent)
                    .put("secondaryColor", secondaryAccent)
                    .put("secondaryColorDark", secondaryAccent)
                    .put("highLightTextColor", accent)
                    .put("highLightTextColorDark", accent)
                    .put("highLightbgColor", secondaryAccent)
                    .put("highLightbgColorDark", secondaryAccent)
                    .put("actionInfo", buildActionInfo(resolvedActionLabels(draft).first(), draft))
            )
        }
    }

    private fun buildProgress(draft: IslandDraft): JSONObject = JSONObject()
        .put("progress", draft.progress)
        .put("colorProgress", colorToHtml(draft.accentColor))
        .put("colorProgressEnd", colorToHtml(draft.secondaryAccentColor))

    private fun buildActionInfo(
        label: String,
        draft: IslandDraft,
        includeType: Boolean = false
    ): JSONObject {
        val background = draft.accentColor
        val pressed = darkenColor(background, 0.14f)
        val titleColor = if (Color.luminance(background) >= 0.62f) "#161616" else "#FFFFFF"
        val actionIntent = buildLaunchIntent(draft).toUri(Intent.URI_INTENT_SCHEME)
        return JSONObject()
            .apply {
                if (includeType) {
                    put(
                        "type",
                        when (draft.actionStyle) {
                            ActionStyle.ROUND -> 0
                            ActionStyle.PROGRESS -> 1
                            ActionStyle.TEXT -> 2
                        }
                    )
                }
            }
            .put("actionIcon", SECONDARY_IMAGE_KEY)
            .put("actionIconDark", SECONDARY_IMAGE_KEY)
            .put("actionTitle", escapeHtml(label))
            .put("actionTitleColor", titleColor)
            .put("actionTitleColorDark", titleColor)
            .put("actionBgColor", colorToHtml(background))
            .put("actionBgColorDark", colorToHtml(background))
            .put("actionBgPressColor", colorToHtml(pressed))
            .put("actionBgPressColorDark", colorToHtml(pressed))
            .put("actionIntentType", 1)
            .put("actionIntent", actionIntent)
            .put("clickWithCollapse", true)
            .apply {
                if (includeType && draft.actionStyle == ActionStyle.PROGRESS) {
                    put(
                        "progressInfo",
                        JSONObject()
                            .put("progress", draft.progress)
                            .put("colorProgress", colorToHtml(draft.secondaryAccentColor))
                            .put("isCCW", true)
                    )
                }
            }
    }

    private fun resolvedActionLabels(draft: IslandDraft): List<String> =
        draft.actionLabels.filter { it.isNotBlank() }.ifEmpty {
            listOf(appContext.getString(R.string.action_open))
        }

    /**
     * Island remains the PendingIntent creator, while the explicit component belongs to the app
     * selected by the user. SystemUI can open it without changing notification ownership or
     * requiring integration from the target package.
     */
    private fun buildContentPendingIntent(
        notificationId: Int,
        draft: IslandDraft
    ): PendingIntent = PendingIntent.getActivity(
        appContext,
        notificationId,
        buildLaunchIntent(draft, notificationId),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun buildLaunchIntent(
        draft: IslandDraft,
        notificationId: Int? = null
    ): Intent {
        val externalIntent = draft.targetPackageName
            ?.takeIf { it != appContext.packageName }
            ?.let { packageName ->
                runCatching {
                    appContext.packageManager.getLaunchIntentForPackage(packageName)
                }.getOrNull()
            }
        val ownIntent = appContext.packageManager
            .getLaunchIntentForPackage(appContext.packageName)
            ?.apply {
                action = ACTION_OPEN_ISLAND
                notificationId?.let { putExtra(EXTRA_NOTIFICATION_ID, it) }
            }
            ?: Intent(ACTION_OPEN_ISLAND).setPackage(appContext.packageName).apply {
                notificationId?.let { putExtra(EXTRA_NOTIFICATION_ID, it) }
            }
        return (externalIntent ?: ownIntent).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        }
    }

    private fun applicationIconBitmap(): Bitmap? = runCatching {
        appContext.applicationInfo
            .loadIcon(appContext.packageManager)
            .toBitmap(256, 256)
    }.getOrNull()

    private fun buildIslandPayload(draft: IslandDraft): JSONObject {
        val timeoutSeconds = (draft.duration.timeoutMillis / 1_000L).coerceAtMost(43_200L)
        return JSONObject()
            .put("islandProperty", draft.islandProperty.protocolValue)
            .put("islandOrder", false)
            .put("highlightColor", colorToHtml(draft.accentColor))
            .put("bigIslandArea", buildLargeIslandArea(draft))
            .put("smallIslandArea", buildSmallIslandArea(draft))
            .apply {
                if (timeoutSeconds > 0) put("islandTimeout", timeoutSeconds)
            }
    }

    private fun buildLargeIslandArea(draft: IslandDraft): JSONObject {
        val area = JSONObject()
        if (draft.largeIslandTemplate == LargeIslandTemplate.FIXED_IMAGE_TEXT_PAIR) {
            area.put(
                "imageTextInfoLeft",
                JSONObject()
                    .put("type", 5)
                    .put("picInfo", pictureInfo(PRIMARY_IMAGE_KEY, 4))
                    .put("textInfo", summaryText(draft, draft.digitText.ifBlank { draft.title }))
            )
            area.put(
                "imageTextInfoRight",
                JSONObject()
                    .put("type", 6)
                    .put("picInfo", pictureInfo(SECONDARY_IMAGE_KEY, 4))
                    .put("textInfo", summaryText(draft, draft.trailingText.ifBlank { draft.digitText.ifBlank { "1" } }))
            )
            return area
        }

        area.put(
            "imageTextInfoLeft",
            JSONObject()
                .put("type", 1)
                .put("picInfo", pictureInfo(PRIMARY_IMAGE_KEY))
                .put("textInfo", summaryText(draft, draft.title))
        )
        when (draft.largeIslandTemplate) {
            LargeIslandTemplate.LEFT_ONLY -> Unit
            LargeIslandTemplate.LEFT_TEXT -> area.put(
                "textInfo",
                summaryText(draft, draft.digitText.ifBlank { draft.subtitle.ifBlank { draft.title } })
            )
            LargeIslandTemplate.LEFT_IMAGE_TEXT_TWO -> area.put(
                "imageTextInfoRight",
                JSONObject()
                    .put("type", 2)
                    .put("picInfo", pictureInfo(SECONDARY_IMAGE_KEY))
                    .put("textInfo", summaryText(draft, draft.digitText.ifBlank { draft.subtitle.ifBlank { "1" } }))
            )
            LargeIslandTemplate.LEFT_IMAGE_TEXT_THREE -> area.put(
                "imageTextInfoRight",
                JSONObject()
                    .put("type", 3)
                    .put("picInfo", pictureInfo(SECONDARY_IMAGE_KEY))
                    .put("textInfo", summaryText(draft, draft.digitText.ifBlank { draft.subtitle.ifBlank { "1" } }))
            )
            LargeIslandTemplate.LEFT_PROGRESS_TEXT -> area.put(
                "progressTextInfo",
                JSONObject()
                    .put(
                        "progressInfo",
                        JSONObject()
                            .put("progress", draft.progress)
                            .put("colorReach", colorToHtml(draft.accentColor))
                            .put("colorUnReach", colorToHtml(draft.secondaryAccentColor))
                            .put("isCCW", true)
                    )
                    .put("textInfo", summaryText(draft, draft.digitText.ifBlank { "${draft.progress}%" }))
            )
            LargeIslandTemplate.LEFT_SAME_WIDTH_DIGIT -> area.put(
                "sameWidthDigitInfo",
                JSONObject()
                    .put("digit", draft.digitText.ifBlank { "00:30" })
                    .putIfNotBlank("content", draft.trailingText)
                    .put("showHighlightColor", true)
            )
            LargeIslandTemplate.LEFT_FIXED_WIDTH_DIGIT -> area.put(
                "fixedWidthDigitInfo",
                JSONObject()
                    .put("digit", draft.digitText.ifBlank { "99" })
                    .putIfNotBlank("content", draft.trailingText)
                    .put("showHighlightColor", true)
            )
            LargeIslandTemplate.LEFT_LARGE_PICTURE -> area.put(
                "picInfo",
                pictureInfo(SECONDARY_IMAGE_KEY)
            )
            LargeIslandTemplate.FIXED_IMAGE_TEXT_PAIR -> Unit
        }
        return area
    }

    private fun buildSmallIslandArea(draft: IslandDraft): JSONObject = when (
        draft.smallIslandTemplate
    ) {
        SmallIslandTemplate.ICON -> JSONObject().put(
            "picInfo",
            pictureInfo(PRIMARY_IMAGE_KEY)
        )
        SmallIslandTemplate.PROGRESS_ICON -> JSONObject().put(
            "combinePicInfo",
            JSONObject()
                .put("picInfo", pictureInfo(PRIMARY_IMAGE_KEY))
                .put(
                    "progressInfo",
                    JSONObject()
                        .put("progress", draft.progress)
                        .put("colorReach", colorToHtml(draft.accentColor))
                        .put("colorUnReach", colorToHtml(draft.secondaryAccentColor))
                        .put("isCCW", true)
                )
        )
        SmallIslandTemplate.ICON_TEXT -> JSONObject().put(
            "imageTextInfoRight",
            JSONObject()
                .put("type", 6)
                .put("picInfo", pictureInfo(PRIMARY_IMAGE_KEY, 4))
                .put("textInfo", summaryText(draft, draft.digitText.ifBlank { draft.title }))
        )
    }

    private fun summaryText(draft: IslandDraft, title: String): JSONObject = JSONObject()
        .putIfNotBlank("frontTitle", draft.leadingText)
        .put("title", title.ifBlank { " " })
        .putIfNotBlank("content", draft.trailingText)
        .put("narrowFont", draft.narrowFont)
        .put("showHighlightColor", true)

    private fun pictureInfo(key: String, type: Int = 1): JSONObject = JSONObject()
        .put("type", type)
        .put("pic", key)

    private fun decodePicture(rawUri: String?): Icon? = decodeBitmap(rawUri)?.let(Icon::createWithBitmap)

    private fun decodeBitmap(rawUri: String?): Bitmap? {
        if (rawUri.isNullOrBlank()) return null
        pictureCache.get(rawUri)?.let { return it }
        return runCatching {
            val source = ImageDecoder.createSource(appContext.contentResolver, Uri.parse(rawUri))
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val size = info.size
                val longest = maxOf(size.width, size.height)
                if (longest > MAX_PICTURE_EDGE) {
                    val scale = MAX_PICTURE_EDGE.toFloat() / longest
                    decoder.setTargetSize(
                        (size.width * scale).toInt().coerceAtLeast(1),
                        (size.height * scale).toInt().coerceAtLeast(1)
                    )
                }
            }
        }.onSuccess { bitmap ->
            pictureCache.put(rawUri, bitmap)
        }.onFailure { error ->
            Log.w(TAG, "Unable to decode selected picture", error)
        }.getOrNull()
    }

    private fun JSONObject.putIfNotBlank(key: String, value: String): JSONObject = apply {
        if (value.isNotBlank()) put(key, value)
    }

    private fun escapeHtml(value: String): String = value.htmlEncode()

    private fun darkenColor(color: Int, amount: Float): Int {
        val factor = 1f - amount.coerceIn(0f, 1f)
        return Color.rgb(
            (Color.red(color) * factor).toInt(),
            (Color.green(color) * factor).toInt(),
            (Color.blue(color) * factor).toInt()
        )
    }

    private fun colorToHtml(color: Int): String = String.format(
        Locale.US,
        "#%02X%02X%02X",
        Color.red(color),
        Color.green(color),
        Color.blue(color)
    )

    companion object {
        private const val TAG = "IslandPublisher"
        private const val FOCUS_PARAM = "miui.focus.param"
        private const val MEDIA_FOCUS_PARAM = "miui.focus.param.media"
        private const val FOCUS_PICS = "miui.focus.pics"
        private const val LOGO_KEY = "miui.focus.pic_island_logo"
        private const val BUSINESS_NAME = "custom_island"
        private const val PRIMARY_IMAGE_KEY = "miui.focus.pic_island_primary"
        private const val SECONDARY_IMAGE_KEY = "miui.focus.pic_island_secondary"
        private const val FOCUS_V3_SERIAL_NAME =
            "com.xzakota.hyper.notification.focus.FocusNotification.FocusTemplateFactory.V3"
        private const val FOCUS_PROTOCOL_SETTING = "notification_focus_protocol"
        private const val ISLAND_FEATURE_PROPERTY = "persist.sys.feature.island"
        private const val ISLAND_UPDATE_NO_FLOAT = "miui.island.updateNoFloat"
        private const val ISLAND_FIRST_FLOAT = "miui.island.firstFloat"
        private const val MIUI_ENABLE_FLOAT = "miui.enableFloat"
        private const val XMSF_VALIDATION_WINDOW_MS = 220L
        private const val MAX_FOCUS_PARAMETER_BYTES = 3_072
        private const val MAX_PICTURE_EDGE = 256
        private const val MEDIA_SESSION_TAG = "IslandMediaSession"
        private const val MEDIA_TIMELINE_MILLIS = 100_000L
        private const val MEDIA_ACTION_REQUEST_OFFSET = 100
        const val ACTION_OPEN_ISLAND = "com.lab.island.action.OPEN_ISLAND"
        const val ACTION_TOGGLE_MEDIA = "com.lab.island.action.TOGGLE_MEDIA"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
    }
}
