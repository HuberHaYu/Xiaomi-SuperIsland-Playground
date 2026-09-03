package com.lab.island.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueDark,
    onPrimary = OnSkyBlueDark,
    primaryContainer = SkyBlueContainerDark,
    onPrimaryContainer = OnSkyBlueContainerDark,
    inversePrimary = SkyInversePrimaryDark,
    secondary = SkySecondaryDark,
    onSecondary = OnSkySecondaryDark,
    secondaryContainer = SkySecondaryContainerDark,
    onSecondaryContainer = OnSkySecondaryContainerDark,
    tertiary = SkyTertiaryDark,
    onTertiary = OnSkyTertiaryDark,
    tertiaryContainer = SkyTertiaryContainerDark,
    onTertiaryContainer = OnSkyTertiaryContainerDark,
    background = SkyBackgroundDark,
    surface = SkySurfaceDark,
    surfaceVariant = SkySurfaceVariantDark,
    onSurfaceVariant = OnSkySurfaceVariantDark,
    outline = SkyOutlineDark,
    outlineVariant = SkyOutlineVariantDark,
    surfaceDim = SkySurfaceDimDark,
    surfaceBright = SkySurfaceBrightDark,
    surfaceContainerLowest = SkySurfaceContainerLowestDark,
    surfaceContainerLow = SkySurfaceContainerLowDark,
    surfaceContainer = SkySurfaceContainerDark,
    surfaceContainerHigh = SkySurfaceContainerHighDark,
    surfaceContainerHighest = SkySurfaceContainerHighestDark
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlueLight,
    onPrimary = OnSkyBlueLight,
    primaryContainer = SkyBlueContainerLight,
    onPrimaryContainer = OnSkyBlueContainerLight,
    inversePrimary = SkyInversePrimaryLight,
    secondary = SkySecondaryLight,
    onSecondary = OnSkySecondaryLight,
    secondaryContainer = SkySecondaryContainerLight,
    onSecondaryContainer = OnSkySecondaryContainerLight,
    tertiary = SkyTertiaryLight,
    onTertiary = OnSkyTertiaryLight,
    tertiaryContainer = SkyTertiaryContainerLight,
    onTertiaryContainer = OnSkyTertiaryContainerLight,
    background = SkyBackgroundLight,
    surface = SkySurfaceLight,
    surfaceVariant = SkySurfaceVariantLight,
    onSurfaceVariant = OnSkySurfaceVariantLight,
    outline = SkyOutlineLight,
    outlineVariant = SkyOutlineVariantLight,
    surfaceDim = SkySurfaceDimLight,
    surfaceBright = SkySurfaceBrightLight,
    surfaceContainerLowest = SkySurfaceContainerLowestLight,
    surfaceContainerLow = SkySurfaceContainerLowLight,
    surfaceContainer = SkySurfaceContainerLight,
    surfaceContainerHigh = SkySurfaceContainerHighLight,
    surfaceContainerHighest = SkySurfaceContainerHighestLight
)

@Composable
fun IslandTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
