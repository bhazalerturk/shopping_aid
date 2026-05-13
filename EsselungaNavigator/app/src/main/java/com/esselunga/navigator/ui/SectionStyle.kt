package com.esselunga.navigator.ui

import androidx.compose.ui.graphics.Color
import com.esselunga.navigator.data.StoreSection

fun sectionColor(section: StoreSection?): Color = when (section) {
    StoreSection.PRODUCE       -> Color(0xFF43A047)
    StoreSection.BAKERY        -> Color(0xFFFF8F00)
    StoreSection.PASTA_RICE    -> Color(0xFFFBC02D)
    StoreSection.CONDIMENTS    -> Color(0xFF8E24AA)
    StoreSection.DAIRY         -> Color(0xFF0288D1)
    StoreSection.DELI          -> Color(0xFFD81B60)
    StoreSection.MEAT          -> Color(0xFFE53935)
    StoreSection.FROZEN        -> Color(0xFF00ACC1)
    StoreSection.BREAKFAST     -> Color(0xFFFF7043)
    StoreSection.DRINKS        -> Color(0xFF1E88E5)
    StoreSection.PERSONAL_CARE -> Color(0xFF7B1FA2)
    StoreSection.CLEANING      -> Color(0xFF546E7A)
    StoreSection.PET           -> Color(0xFF6D4C41)
    null                       -> Color(0xFFF57C00)
}

fun sectionEmoji(section: StoreSection?): String = when (section) {
    StoreSection.PRODUCE       -> "🥦"
    StoreSection.BAKERY        -> "🍞"
    StoreSection.PASTA_RICE    -> "🍝"
    StoreSection.CONDIMENTS    -> "🫙"
    StoreSection.DAIRY         -> "🥛"
    StoreSection.DELI          -> "🥩"
    StoreSection.MEAT          -> "🍗"
    StoreSection.FROZEN        -> "🧊"
    StoreSection.BREAKFAST     -> "🥣"
    StoreSection.DRINKS        -> "🥤"
    StoreSection.PERSONAL_CARE -> "🧴"
    StoreSection.CLEANING      -> "🧹"
    StoreSection.PET           -> "🐾"
    null                       -> "❓"
}
