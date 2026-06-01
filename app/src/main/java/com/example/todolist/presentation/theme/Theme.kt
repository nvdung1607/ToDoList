package com.example.todolist.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.todolist.domain.model.ThemeMode

// ─── Color Schemes ────────────────────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary            = IndigoLight,
    onPrimary          = OnPrimaryLight,
    primaryContainer   = IndigoContainerLight,
    onPrimaryContainer = OnPrimaryDark,

    secondary            = VioletLight,
    onSecondary          = OnSecondaryLight,
    secondaryContainer   = Color(0xFFEDE9FE),
    onSecondaryContainer = Color(0xFF4C1D95),

    tertiary            = StreakAmber,
    onTertiary          = Color(0xFF000000),
    tertiaryContainer   = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF78350F),

    background   = BackgroundLight,
    onBackground = OnBackgroundLight,

    surface             = SurfaceLight,
    onSurface           = OnSurfaceLight,
    surfaceVariant      = SurfaceVariantLight,
    onSurfaceVariant    = OnSurfaceVariantLight,

    outline      = OutlineLight,
    outlineVariant = Color(0xFFCAC4D0),

    error    = OverdueRed,
    onError  = Color(0xFFFFFFFF),
    errorContainer   = Color(0xFFFFE4E6),
    onErrorContainer = Color(0xFF9F1239),

    scrim = ScrimColor,
)

private val DarkColorScheme = darkColorScheme(
    primary            = IndigoDark,
    onPrimary          = OnPrimaryDark,
    primaryContainer   = IndigoContainerDark,
    onPrimaryContainer = Color(0xFFE0E7FF),

    secondary            = VioletDark,
    onSecondary          = OnSecondaryDark,
    secondaryContainer   = Color(0xFF3B0764),
    onSecondaryContainer = Color(0xFFEDE9FE),

    tertiary            = StreakAmber,
    onTertiary          = Color(0xFF1C1400),
    tertiaryContainer   = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFEF3C7),

    background   = BackgroundDark,
    onBackground = OnBackgroundDark,

    surface             = SurfaceDark,
    onSurface           = OnSurfaceDark,
    surfaceVariant      = SurfaceVariantDark,
    onSurfaceVariant    = OnSurfaceVariantDark,

    outline      = OutlineDark,
    outlineVariant = Color(0xFF49454F),

    error    = Color(0xFFFF6B81),
    onError  = Color(0xFF690014),
    errorContainer   = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    scrim = ScrimColor,
)

// ─── Custom Colors CompositionLocal ──────────────────────────────────────────

/**
 * Holds semantic colors that don't map 1:1 to Material3 roles.
 * Access via [MaterialTheme.appColors].
 */
data class AppColors(
    val streakAmber: Color,
    val successGreen: Color,
    val overdueRed: Color,
    val rollOverYellow: Color,
)

private val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        streakAmber    = StreakAmber,
        successGreen   = SuccessGreen,
        overdueRed     = OverdueRed,
        rollOverYellow = RollOverYellow,
    )
}

// ─── MaterialTheme Extension Properties ──────────────────────────────────────

/** Quick access: `MaterialTheme.colorScheme.streakAmber` */
val ColorScheme.streakAmber: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.streakAmber

/** Quick access: `MaterialTheme.colorScheme.successGreen` */
val ColorScheme.successGreen: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.successGreen

/** Quick access: `MaterialTheme.colorScheme.overdueRed` */
val ColorScheme.overdueRed: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.overdueRed

/** Quick access: `MaterialTheme.colorScheme.rollOverYellow` */
val ColorScheme.rollOverYellow: Color
    @Composable @ReadOnlyComposable get() = LocalAppColors.current.rollOverYellow

/** Convenience: `MaterialTheme.appColors.streakAmber` etc. */
val MaterialTheme.appColors: AppColors
    @Composable @ReadOnlyComposable get() = LocalAppColors.current

// ─── Theme Composable ─────────────────────────────────────────────────────────

/**
 * Root theme composable for ToDoList app.
 *
 * @param themeMode Explicit mode (LIGHT/DARK/SYSTEM). Defaults to SYSTEM.
 * @param dynamicColor Use Material You dynamic color on Android 12+. Defaults to false
 *                     to keep the brand palette consistent.
 */
@Composable
fun ToDoListTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()

    val isDark = when (themeMode) {
        ThemeMode.LIGHT  -> false
        ThemeMode.DARK   -> true
        ThemeMode.SYSTEM -> systemDark
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else   -> LightColorScheme
    }

    // Update status bar color to match background
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    CompositionLocalProvider(
        LocalAppColors provides AppColors(
            streakAmber    = StreakAmber,
            successGreen   = SuccessGreen,
            overdueRed     = OverdueRed,
            rollOverYellow = RollOverYellow,
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = AppTypography,
            shapes      = AppShapes,
            content     = content,
        )
    }
}
