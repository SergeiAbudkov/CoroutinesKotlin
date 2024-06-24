package com.example.simplemvvmcoroutineskotlin.views.changecolor

import com.example.simplemvvmcoroutineskotlin.model.colors.NamedColor

/**
 * Represents list item for the color; it may be selected or not
 */
data class NamedColorListItem(
    val namedColor: NamedColor,
    val selected: Boolean
)