package com.example.todolist.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Design-system shape scale.
 *
 *  Small      →  8 dp  – Chips, badges, small cards
 *  Medium     → 16 dp  – Cards, dialogs, bottom-sheets header
 *  Large      → 24 dp  – Modal bottom-sheets, large containers
 *  ExtraLarge → 32 dp  – FAB, pill buttons, hero cards
 */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)
