package com.sifip.cibademo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Bi-palette : par défaut le thème utilise la palette banque (violet).
 * L'écran de consentement (SIFIP) surcharge localement la couleur primary
 * via `MaterialTheme(colorScheme = SifipColorScheme)` ou par usage direct
 * des constantes SifipBlue / SifipGold.
 */

private val BankColorScheme = lightColorScheme(
    primary = BankPurple,
    onPrimary = Color.White,
    primaryContainer = BankPurpleLight,
    onPrimaryContainer = BankPurpleDeep,
    secondary = SifipBlue,
    onSecondary = Color.White,
    secondaryContainer = BankPurpleTint,
    onSecondaryContainer = BankPurpleDeep,
    tertiary = SifipGold,
    onTertiary = Color.White,
    background = AppBackground,
    onBackground = AppText,
    surface = Color.White,
    onSurface = AppText,
    surfaceVariant = BankPurpleTint,
    onSurfaceVariant = BankPurpleDeep,
    outline = AppBorder,
    error = StatusError,
    onError = Color.White,
)

@Composable
fun AppTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = BankColorScheme,
        typography = AppTypography,
        content = content,
    )
}
