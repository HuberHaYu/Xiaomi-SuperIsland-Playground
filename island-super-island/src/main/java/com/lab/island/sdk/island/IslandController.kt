package com.lab.island.sdk.island

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.lab.island.sdk.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/** Owns this package's single notification, persistence, and conflated publish worker. */
class IslandController private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val notificationManager = appContext.getSystemService(NotificationManager::class.java)
    private val publisher = XiaomiSuperIslandPublisher(
        appContext,
        notificationManager,
        onMediaPlaybackChanged = ::setMediaPlayback,
        onMediaSeekChanged = ::seekMediaPlayback
    )
    private val preferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val worker = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "IslandPublisher").apply { isDaemon = true }
    }
    private val storedEntries = loadActiveIslands()
    private val storedDraft = loadActiveDraft()
    private val _activeIslands = MutableStateFlow(
        storedEntries.firstOrNull { it.notificationId == NOTIFICATION_ID }
            ?.let(::listOf)
            .orEmpty()
    )
    private val activeGeneration = AtomicLong(if (_activeIslands.value.isEmpty()) 0L else 1L)
    private val activeDraft = AtomicReference(storedDraft)
    private val pendingUpdate = AtomicReference<PendingUpdate?>(null)
    private val updateDrainScheduled = AtomicBoolean(false)

    val activeIslands: StateFlow<List<ActiveIsland>> = _activeIslands.asStateFlow()

    init {
        // Migrate builds that used an ID range. Only this package's own remembered notifications
        // are touched, and the new implementation always keeps at most one fixed ID.
        storedEntries
            .asSequence()
            .filter { it.notificationId != NOTIFICATION_ID }
            .forEach { notificationManager.cancel(it.notificationId) }
        saveActiveIslands(_activeIslands.value)
        if (_activeIslands.value.isEmpty()) {
            activeDraft.set(null)
            clearActiveDraft()
        } else {
            clearLegacyRearmMetadata()
        }
    }

    fun capability(): DeviceCapability = publisher.detectCapability()

    /** Creates the one Super Island, or replaces that same notification if it already exists. */
    fun publish(draft: IslandDraft, callback: (PublishOutcome) -> Unit) {
        val safeDraft = sanitize(draft) ?: run {
            callback(PublishOutcome(PublishKind.ERROR, appContext.getString(R.string.error_enter_title)))
            return
        }
        val generation = activeGeneration.incrementAndGet()
        pendingUpdate.set(null)

        worker.execute {
            val firstFrame = _activeIslands.value.isEmpty()
            val outcome = runCatching {
                createChannel()
                check(generation == activeGeneration.get()) {
                    appContext.getString(R.string.error_send_cancelled)
                }
                val notification = publisher.buildNotification(
                    CHANNEL_ID,
                    NOTIFICATION_ID,
                    safeDraft,
                    firstFrame = firstFrame
                )
                val capability = publisher.detectCapability()
                val kind = if (
                    capability == DeviceCapability.READY && safeDraft.scene != IslandScene.MUSIC
                ) {
                    check(
                        publisher.postWithValidation(NOTIFICATION_ID, notification) {
                            generation == activeGeneration.get()
                        }
                    ) { appContext.getString(R.string.error_xmsf_validation) }
                    PublishKind.SUPER_ISLAND
                } else {
                    check(generation == activeGeneration.get()) {
                        appContext.getString(R.string.error_send_cancelled)
                    }
                    // MediaStyle is an official system path and does not need the Focus/XMSF
                    // validation transaction. Keeping it out also avoids touching global XMSF
                    // state while another app is publishing its own media notification.
                    notificationManager.notify(NOTIFICATION_ID, notification)
                    if (capability == DeviceCapability.READY) {
                        PublishKind.SUPER_ISLAND
                    } else {
                        PublishKind.REGULAR_NOTIFICATION
                    }
                }
                check(generation == activeGeneration.get()) {
                    appContext.getString(R.string.error_send_cancelled)
                }
                setActiveDraft(safeDraft)

                when (kind) {
                    PublishKind.SUPER_ISLAND -> PublishOutcome(
                        kind,
                        appContext.getString(
                            if (firstFrame) R.string.message_island_sent
                            else R.string.message_island_updated
                        )
                    )
                    PublishKind.REGULAR_NOTIFICATION -> PublishOutcome(
                        kind,
                        if (capability == DeviceCapability.OTHER_ANDROID) {
                            appContext.getString(R.string.message_regular_notification_non_xiaomi)
                        } else {
                            appContext.getString(R.string.message_regular_notification_fallback)
                        }
                    )
                    PublishKind.ERROR -> error("unreachable")
                }
            }.getOrElse { error ->
                publisher.restoreGate()
                PublishOutcome(
                    PublishKind.ERROR,
                    error.message?.takeIf { it.isNotBlank() }
                        ?: appContext.getString(R.string.error_send_failed)
                )
            }
            appContext.mainExecutor.execute { callback(outcome) }
        }
    }

    /**
     * Conflates rapid editor changes. At most one update is validating while the newest draft
     * replaces any queued intermediate state, which prevents a slider or text field from building
     * up a long HWUI-visible update backlog.
     */
    fun updateActive(
        draft: IslandDraft,
        onError: ((PublishOutcome) -> Unit)? = null
    ) {
        if (_activeIslands.value.isEmpty()) return
        val safeDraft = sanitize(draft) ?: return
        pendingUpdate.set(
            PendingUpdate(
                draft = safeDraft,
                generation = activeGeneration.get(),
                onError = onError
            )
        )
        scheduleUpdateDrain()
    }

    fun cancel(notificationId: Int) {
        activeGeneration.incrementAndGet()
        pendingUpdate.set(null)
        activeDraft.set(null)
        clearActiveDraft()
        publisher.deactivateMediaSession()
        notificationManager.cancel(notificationId)
        if (notificationId != NOTIFICATION_ID) notificationManager.cancel(NOTIFICATION_ID)
        _activeIslands.value = emptyList()
        saveActiveIslands(emptyList())
    }

    /** Removes the entry when the notification timed out while the app was closed. */
    fun refreshActiveIslands() {
        if (_activeIslands.value.isEmpty()) return
        val isPosted = runCatching {
            notificationManager.activeNotifications.any { it.id == NOTIFICATION_ID }
        }.getOrNull() ?: return
        if (!isPosted) {
            activeGeneration.incrementAndGet()
            pendingUpdate.set(null)
            activeDraft.set(null)
            clearActiveDraft()
            publisher.deactivateMediaSession()
            _activeIslands.value = emptyList()
            saveActiveIslands(emptyList())
        }
    }

    private fun scheduleUpdateDrain() {
        if (!updateDrainScheduled.compareAndSet(false, true)) return
        worker.execute(::drainPendingUpdates)
    }

    private fun drainPendingUpdates() {
        try {
            while (true) {
                val update = pendingUpdate.getAndSet(null) ?: break
                if (!isCurrent(update)) continue
                runCatching {
                    createChannel()
                    val notification = publisher.buildNotification(
                        CHANNEL_ID,
                        NOTIFICATION_ID,
                        update.draft,
                        firstFrame = false
                    )
                    val capability = publisher.detectCapability()
                    if (
                        capability == DeviceCapability.READY &&
                        update.draft.scene != IslandScene.MUSIC
                    ) {
                        check(
                            publisher.postWithValidation(NOTIFICATION_ID, notification) {
                                isCurrent(update)
                            }
                        ) { appContext.getString(R.string.error_live_update_failed) }
                    } else {
                        check(isCurrent(update)) {
                            appContext.getString(R.string.error_update_cancelled)
                        }
                        notificationManager.notify(NOTIFICATION_ID, notification)
                    }
                    if (isCurrent(update)) setActiveDraft(update.draft)
                }.onFailure { error ->
                    publisher.restoreGate()
                    if (isCurrent(update)) {
                        Log.w(TAG, "Unable to apply live Super Island update", error)
                        update.onError?.let { callback ->
                            val outcome = PublishOutcome(
                                PublishKind.ERROR,
                                error.message?.takeIf { it.isNotBlank() }
                                    ?: appContext.getString(R.string.error_live_update_failed)
                            )
                            appContext.mainExecutor.execute { callback(outcome) }
                        }
                    }
                }
            }
        } finally {
            updateDrainScheduled.set(false)
            if (pendingUpdate.get() != null) scheduleUpdateDrain()
        }
    }

    private fun isCurrent(update: PendingUpdate): Boolean =
        update.generation == activeGeneration.get() && _activeIslands.value.isNotEmpty()

    /** Handles the single play/pause action exposed by the system media island. */
    fun toggleMediaPlayback() {
        val draft = activeDraft.get() ?: return
        if (draft.scene != IslandScene.MUSIC) return
        setMediaPlayback(!draft.mediaPlaying)
    }

    private fun setMediaPlayback(playing: Boolean) {
        val draft = activeDraft.get() ?: return
        if (draft.scene != IslandScene.MUSIC || draft.mediaPlaying == playing) return
        updateActive(draft.copy(mediaPlaying = playing))
    }

    private fun seekMediaPlayback(progress: Int) {
        val draft = activeDraft.get() ?: return
        if (draft.scene != IslandScene.MUSIC) return
        updateActive(draft.copy(progress = progress.coerceIn(0, 100)))
    }

    private fun setActiveDraft(draft: IslandDraft) {
        activeDraft.set(draft)
        val entry = ActiveIsland(
            notificationId = NOTIFICATION_ID,
            title = draft.title,
            subtitle = draft.subtitle,
            accentColor = draft.accentColor,
            createdAtMillis = _activeIslands.value.firstOrNull()?.createdAtMillis
                ?: System.currentTimeMillis(),
            scene = draft.scene,
            mediaPlaying = draft.mediaPlaying,
            targetPackageName = draft.targetPackageName
        )
        _activeIslands.value = listOf(entry)
        saveActiveIslands(listOf(entry))
        saveActiveDraft(draft)
    }

    private fun saveActiveDraft(draft: IslandDraft) {
        preferences.edit {
            putString(KEY_ACTIVE_DRAFT, IslandDraftJson.encode(draft))
            clearLegacyRearmMetadata(this)
        }
    }

    private fun clearActiveDraft() {
        preferences.edit {
            remove(KEY_ACTIVE_DRAFT)
            clearLegacyRearmMetadata(this)
        }
    }

    private fun clearLegacyRearmMetadata() {
        preferences.edit { clearLegacyRearmMetadata(this) }
    }

    private fun clearLegacyRearmMetadata(
        editor: android.content.SharedPreferences.Editor
    ) {
        editor.remove(LEGACY_KEY_REARM_TOKEN)
        editor.remove(LEGACY_KEY_REARM_EXPIRES_AT)
        editor.remove(LEGACY_KEY_REARM_POSTED_AT)
        editor.remove(LEGACY_KEY_REARM_ARMED)
        editor.remove(LEGACY_KEY_REARM_PENDING)
    }

    private fun loadActiveDraft(): IslandDraft? =
        IslandDraftJson.decode(preferences.getString(KEY_ACTIVE_DRAFT, null))

    private fun sanitize(draft: IslandDraft): IslandDraft? {
        val value = draft.copy(
            title = draft.title.trim().take(MAX_TITLE_LENGTH),
            subtitle = draft.subtitle.trim().take(MAX_SUBTITLE_LENGTH),
            extraText = draft.extraText.trim().take(MAX_EXTRA_LENGTH),
            source = draft.source.trim().take(MAX_SOURCE_LENGTH),
            leadingText = draft.leadingText.trim().take(MAX_ISLAND_TEXT_LENGTH),
            trailingText = draft.trailingText.trim().take(MAX_ISLAND_TEXT_LENGTH),
            digitText = draft.digitText.trim().take(MAX_DIGIT_LENGTH),
            progressLabel = draft.progressLabel.trim().take(MAX_PROGRESS_LABEL_LENGTH),
            actionLabels = draft.actionLabels
                .asSequence()
                .map { it.trim().take(MAX_ACTION_LENGTH) }
                .filter { it.isNotBlank() }
                .take(MAX_ACTIONS)
                .toList(),
            mediaShareUrl = draft.mediaShareUrl.trim().take(MAX_MEDIA_SHARE_URL_LENGTH),
            targetPackageName = draft.targetPackageName
                ?.trim()
                ?.take(MAX_PACKAGE_NAME_LENGTH)
                ?.takeIf { it.isNotBlank() },
            progress = draft.progress.coerceIn(0, 100),
            progressPoints = draft.progressPoints.coerceIn(0, 4)
        )
        return value.takeIf { it.title.isNotBlank() }
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            appContext.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = appContext.getString(R.string.notification_channel_description)
            setSound(null, null)
            enableVibration(false)
            setShowBadge(false)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun loadActiveIslands(): List<ActiveIsland> = runCatching {
        val array = JSONArray(preferences.getString(KEY_ACTIVE_ISLANDS, "[]"))
        buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(
                    ActiveIsland(
                        notificationId = item.getInt("id"),
                        title = item.getString("title"),
                        subtitle = item.optString("subtitle"),
                        accentColor = item.getInt("accent"),
                        createdAtMillis = item.getLong("createdAt"),
                        scene = runCatching {
                            IslandScene.valueOf(item.optString("scene"))
                        }.getOrDefault(IslandScene.GENERAL),
                        mediaPlaying = item.optBoolean("mediaPlaying", false),
                        targetPackageName = item.optString("targetPackage")
                            .takeIf { it.isNotBlank() }
                    )
                )
            }
        }
    }.getOrDefault(emptyList())

    private fun saveActiveIslands(value: List<ActiveIsland>) {
        val array = JSONArray()
        value.take(1).forEach { island ->
            array.put(
                JSONObject()
                    .put("id", island.notificationId)
                    .put("title", island.title)
                    .put("subtitle", island.subtitle)
                    .put("accent", island.accentColor)
                    .put("createdAt", island.createdAtMillis)
                    .put("scene", island.scene.name)
                    .put("mediaPlaying", island.mediaPlaying)
                    .put("targetPackage", island.targetPackageName.orEmpty())
            )
        }
        preferences.edit { putString(KEY_ACTIVE_ISLANDS, array.toString()) }
    }

    private data class PendingUpdate(
        val draft: IslandDraft,
        val generation: Long,
        val onError: ((PublishOutcome) -> Unit)?
    )

    companion object {
        private const val TAG = "IslandController"
        private const val PREFERENCES_NAME = "island_state_v1"
        private const val KEY_ACTIVE_ISLANDS = "active_islands"
        private const val KEY_ACTIVE_DRAFT = "active_draft"
        private const val LEGACY_KEY_REARM_TOKEN = "rearm_token"
        private const val LEGACY_KEY_REARM_EXPIRES_AT = "rearm_expires_at"
        private const val LEGACY_KEY_REARM_POSTED_AT = "rearm_posted_at"
        private const val LEGACY_KEY_REARM_ARMED = "rearm_armed"
        private const val LEGACY_KEY_REARM_PENDING = "rearm_pending"
        private const val CHANNEL_ID = "com.lab.island.custom_islands.v1"
        private const val NOTIFICATION_ID = 51_000
        const val MAX_TITLE_LENGTH = 40
        const val MAX_SUBTITLE_LENGTH = 80
        const val MAX_EXTRA_LENGTH = 40
        const val MAX_SOURCE_LENGTH = 24
        const val MAX_ISLAND_TEXT_LENGTH = 12
        const val MAX_DIGIT_LENGTH = 8
        const val MAX_PROGRESS_LABEL_LENGTH = 16
        const val MAX_ACTION_LENGTH = 10
        const val MAX_ACTIONS = 3
        const val MAX_MEDIA_SHARE_URL_LENGTH = 512
        const val MAX_PACKAGE_NAME_LENGTH = 255

        private val instance = AtomicReference<IslandController?>()

        @JvmStatic
        fun get(context: Context): IslandController {
            instance.get()?.let { return it }
            val created = IslandController(context)
            return if (instance.compareAndSet(null, created)) created else instance.get()!!
        }
    }
}
