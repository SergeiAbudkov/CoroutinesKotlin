package com.example.simplemvvmsideeffects.views.changecolor

import com.example.simplemvvmsideeffects.model.colors.NamedColor

/**
 * Represents list item for the color; it may be selected or not
 */
data class NamedColorListItem(
    val namedColor: NamedColor,
    val selected: Boolean
)