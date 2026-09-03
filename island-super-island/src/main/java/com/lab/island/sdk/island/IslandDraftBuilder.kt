package com.lab.island.sdk.island

class IslandDraftBuilder {
    private var scene = IslandScene.GENERAL
    private var expandedTemplate = ExpandedTemplate.TEXT_TWO_APP_ICON
    private var largeIslandTemplate = LargeIslandTemplate.LEFT_TEXT
    private var smallIslandTemplate = SmallIslandTemplate.ICON
    private var title = ""
    private var subtitle = ""
    private var extraText = ""
    private var source = ""
    private var leadingText = ""
    private var trailingText = ""
    private var digitText = "65"
    private var progressLabel = "Current progress"
    private var actionLabels = listOf("Open")
    private var actionStyle = ActionStyle.ROUND
    private var progress = 65
    private var progressPoints = 2
    private var primaryImageUri: String? = null
    private var secondaryImageUri: String? = null
    private var accentColor = 0xFF1976D2.toInt()
    private var secondaryAccentColor = 0xFF8A78FF.toInt()
    private var duration = IslandDuration.THIRTY_MINUTES
    private var islandProperty = IslandProperty.INFORMATION
    private var autoExpand = true
    private var useAccentBackground = false
    private var narrowFont = false
    private var mediaPlaying = false
    private var mediaShareUrl = ""
    private var targetPackageName: String? = null

    fun scene(value: IslandScene) = apply { scene = value }
    fun expandedTemplate(value: ExpandedTemplate) = apply { expandedTemplate = value }
    fun largeIslandTemplate(value: LargeIslandTemplate) = apply { largeIslandTemplate = value }
    fun smallIslandTemplate(value: SmallIslandTemplate) = apply { smallIslandTemplate = value }
    fun title(value: String) = apply { title = value }
    fun subtitle(value: String) = apply { subtitle = value }
    fun extraText(value: String) = apply { extraText = value }
    fun source(value: String) = apply { source = value }
    fun leadingText(value: String) = apply { leadingText = value }
    fun trailingText(value: String) = apply { trailingText = value }
    fun digitText(value: String) = apply { digitText = value }
    fun progressLabel(value: String) = apply { progressLabel = value }
    fun actionLabels(value: List<String>) = apply { actionLabels = value.toList() }
    fun actionStyle(value: ActionStyle) = apply { actionStyle = value }
    fun progress(value: Int) = apply { progress = value }
    fun progressPoints(value: Int) = apply { progressPoints = value }
    fun primaryImageUri(value: String?) = apply { primaryImageUri = value }
    fun secondaryImageUri(value: String?) = apply { secondaryImageUri = value }
    fun accentColor(value: Int) = apply { accentColor = value }
    fun secondaryAccentColor(value: Int) = apply { secondaryAccentColor = value }
    fun duration(value: IslandDuration) = apply { duration = value }
    fun islandProperty(value: IslandProperty) = apply { islandProperty = value }
    fun autoExpand(value: Boolean) = apply { autoExpand = value }
    fun useAccentBackground(value: Boolean) = apply { useAccentBackground = value }
    fun narrowFont(value: Boolean) = apply { narrowFont = value }
    fun mediaPlaying(value: Boolean) = apply { mediaPlaying = value }
    fun mediaShareUrl(value: String) = apply { mediaShareUrl = value }
    fun targetPackageName(value: String?) = apply { targetPackageName = value }

    fun build(): IslandDraft = IslandDraft(
        scene = scene,
        expandedTemplate = expandedTemplate,
        largeIslandTemplate = largeIslandTemplate,
        smallIslandTemplate = smallIslandTemplate,
        title = title,
        subtitle = subtitle,
        extraText = extraText,
        source = source,
        leadingText = leadingText,
        trailingText = trailingText,
        digitText = digitText,
        progressLabel = progressLabel,
        actionLabels = actionLabels,
        actionStyle = actionStyle,
        progress = progress,
        progressPoints = progressPoints,
        primaryImageUri = primaryImageUri,
        secondaryImageUri = secondaryImageUri,
        accentColor = accentColor,
        secondaryAccentColor = secondaryAccentColor,
        duration = duration,
        islandProperty = islandProperty,
        autoExpand = autoExpand,
        useAccentBackground = useAccentBackground,
        narrowFont = narrowFont,
        mediaPlaying = mediaPlaying,
        mediaShareUrl = mediaShareUrl,
        targetPackageName = targetPackageName
    )
}
