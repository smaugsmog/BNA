package io.github.fate_grand_automata.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val md_theme_light_primary = Color(0xFFFFEB02)
val md_theme_light_onPrimary = Color(0xFF3D3500)
val md_theme_light_primaryContainer = Color(0xFFFFF8C4)
val md_theme_light_onPrimaryContainer = Color(0xFF3D3500)
val md_theme_light_secondary = Color(0xFF6D5C00)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFE082)
val md_theme_light_onSecondaryContainer = Color(0xFF3D3500)
val md_theme_light_tertiary = Color(0xFF90F7F9)
val md_theme_light_onTertiary = Color(0xFF003D40)
val md_theme_light_tertiaryContainer = Color(0xFFB6FCFD)
val md_theme_light_onTertiaryContainer = Color(0xFF003033)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFAFAF5)
val md_theme_light_onBackground = Color(0xFF1C1C1C)
val md_theme_light_surface = Color(0xFFFAFAF5)
val md_theme_light_onSurface = Color(0xFF1C1C1C)
val md_theme_light_surfaceVariant = Color(0xFFE8E4D9)
val md_theme_light_onSurfaceVariant = Color(0xFF4A4A4A)
val md_theme_light_outline = Color(0xFF8C8C8C)
val md_theme_light_inverseOnSurface = Color(0xFFF0F0E8)
val md_theme_light_inverseSurface = Color(0xFF383838)
val md_theme_light_inversePrimary = Color(0xFFD4C200)
val md_theme_light_shadow = Color(0xFF000000)
val md_theme_light_surfaceTint = Color(0xFFFFEB02)
val md_theme_light_outlineVariant = Color(0xFFC8C4B8)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFFFEB02)
val md_theme_dark_onPrimary = Color(0xFF3D3500)
val md_theme_dark_primaryContainer = Color(0xFF4A4200)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFF8C4)
val md_theme_dark_secondary = Color(0xFFD4C200)
val md_theme_dark_onSecondary = Color(0xFF3D3500)
val md_theme_dark_secondaryContainer = Color(0xFF5C4E00)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFE082)
val md_theme_dark_tertiary = Color(0xFF90F7F9)
val md_theme_dark_onTertiary = Color(0xFF003D40)
val md_theme_dark_tertiaryContainer = Color(0xFF005F63)
val md_theme_dark_onTertiaryContainer = Color(0xFFB6FCFD)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF1A1A1A)
val md_theme_dark_onBackground = Color(0xFFE0DED6)
val md_theme_dark_surface = Color(0xFF1A1A1A)
val md_theme_dark_onSurface = Color(0xFFE0DED6)
val md_theme_dark_surfaceVariant = Color(0xFF3A3A3A)
val md_theme_dark_onSurfaceVariant = Color(0xFFC8C4B8)
val md_theme_dark_outline = Color(0xFF8C8C8C)
val md_theme_dark_inverseOnSurface = Color(0xFF1C1C1C)
val md_theme_dark_inverseSurface = Color(0xFFE0DED6)
val md_theme_dark_inversePrimary = Color(0xFFFFF8C4)
val md_theme_dark_shadow = Color(0xFF000000)
val md_theme_dark_surfaceTint = Color(0xFFFFEB02)
val md_theme_dark_outlineVariant = Color(0xFF4A4A4A)
val md_theme_dark_scrim = Color(0xFF000000)

// Set of Material typography styles to start with
val typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun FGAListItemColors() = ListItemDefaults.colors(
    containerColor = MaterialTheme.colorScheme.surfaceVariant
)

@Composable
fun FGATheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    background: Color = Color.Unspecified,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography
    ) {
        Surface(
            color = if (background == Color.Unspecified) MaterialTheme.colorScheme.background else background
        ) {
            PreventRtl {
                content()
            }
        }
    }
}

@Composable
fun FgaScreen(
    content: @Composable BoxScope.() -> Unit
) {
    FGATheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            content()
        }
    }
}