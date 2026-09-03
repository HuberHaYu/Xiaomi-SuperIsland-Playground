package com.lab.island.sdk.island

import org.json.JSONArray
import org.json.JSONObject

/** Lossless local representation used to restore a one-shot island after its target app exits. */
internal object IslandDraftJson {
    fun encode(draft: IslandDraft): String = JSONObject()
        .put("scene", draft.scene.name)
        .put("expandedTemplate", draft.expandedTemplate.name)
        .put("largeIslandTemplate", draft.largeIslandTemplate.name)
        .put("smallIslandTemplate", draft.smallIslandTemplate.name)
        .put("title", draft.title)
        .put("subtitle", draft.subtitle)
        .put("extraText", draft.extraText)
        .put("source", draft.source)
        .put("leadingText", draft.leadingText)
        .put("trailingText", draft.trailingText)
        .put("digitText", draft.digitText)
        .put("progressLabel", draft.progressLabel)
        .put("actionLabels", JSONArray(draft.actionLabels))
        .put("actionStyle", draft.actionStyle.name)
        .put("progress", draft.progress)
        .put("progressPoints", draft.progressPoints)
        .put("primaryImageUri", draft.primaryImageUri ?: JSONObject.NULL)
        .put("secondaryImageUri", draft.secondaryImageUri ?: JSONObject.NULL)
        .put("accentColor", draft.accentColor)
        .put("secondaryAccentColor", draft.secondaryAccentColor)
        .put("duration", draft.duration.name)
        .put("islandProperty", draft.islandProperty.name)
        .put("autoExpand", draft.autoExpand)
        .put("useAccentBackground", draft.useAccentBackground)
        .put("narrowFont", draft.narrowFont)
        .put("mediaPlaying", draft.mediaPlaying)
        .put("mediaShareUrl", draft.mediaShareUrl)
        .put("targetPackageName", draft.targetPackageName ?: JSONObject.NULL)
        .toString()

    fun decode(raw: String?): IslandDraft? {
        if (raw.isNullOrBlank()) return null
        return runCatching {
            val value = JSONObject(raw)
            val labels = value.optJSONArray("actionLabels") ?: JSONArray()
            IslandDraft(
                scene = enumValue(value, "scene", IslandScene.GENERAL),
                expandedTemplate = enumValue(
                    value,
                    "expandedTemplate",
                    ExpandedTemplate.TEXT_TWO_APP_ICON
                ),
                largeIslandTemplate = enumValue(
                    value,
                    "largeIslandTemplate",
                    LargeIslandTemplate.LEFT_TEXT
                ),
                smallIslandTemplate = enumValue(
                    value,
                    "smallIslandTemplate",
                    SmallIslandTemplate.ICON
                ),
                title = value.getString("title"),
                subtitle = value.optString("subtitle"),
                extraText = value.optString("extraText"),
                source = value.optString("source"),
                leadingText = value.optString("leadingText"),
                trailingText = value.optString("trailingText"),
                digitText = value.optString("digitText"),
                progressLabel = value.optString("progressLabel"),
                actionLabels = buildList {
                    for (index in 0 until labels.length()) add(labels.optString(index))
                },
                actionStyle = enumValue(value, "actionStyle", ActionStyle.ROUND),
                progress = value.optInt("progress", 0),
                progressPoints = value.optInt("progressPoints", 0),
                primaryImageUri = value.nullableString("primaryImageUri"),
                secondaryImageUri = value.nullableString("secondaryImageUri"),
                accentColor = value.optInt("accentColor"),
                secondaryAccentColor = value.optInt("secondaryAccentColor"),
                duration = enumValue(value, "duration", IslandDuration.THIRTY_MINUTES),
                islandProperty = enumValue(
                    value,
                    "islandProperty",
                    IslandProperty.INFORMATION
                ),
                autoExpand = value.optBoolean("autoExpand", true),
                useAccentBackground = value.optBoolean("useAccentBackground", false),
                narrowFont = value.optBoolean("narrowFont", false),
                mediaPlaying = value.optBoolean("mediaPlaying", false),
                mediaShareUrl = value.optString("mediaShareUrl"),
                targetPackageName = value.nullableString("targetPackageName")
            )
        }.getOrNull()
    }

    private inline fun <reified T : Enum<T>> enumValue(
        source: JSONObject,
        key: String,
        fallback: T
    ): T = runCatching { enumValueOf<T>(source.optString(key)) }.getOrDefault(fallback)

    private fun JSONObject.nullableString(key: String): String? =
        takeUnless { isNull(key) }?.optString(key)?.takeIf { it.isNotBlank() }
}
