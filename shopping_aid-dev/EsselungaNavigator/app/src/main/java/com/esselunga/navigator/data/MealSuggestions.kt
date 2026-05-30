package com.esselunga.navigator.data

data class MealTemplate(
    val id: String,
    val label: String,
    val emoji: String,
    val categoryIds: List<String>
)

val MEAL_TEMPLATES: List<MealTemplate> = listOf(
    MealTemplate("breakfast", "Breakfast", "🌅", listOf("latte", "cereali", "frutta_fresca", "pane", "burro_panna")),
    MealTemplate("lunch", "Lunch", "☀️", listOf("pasta_secca", "sughi", "verdura_fresca", "olio", "conserve")),
    MealTemplate("dinner", "Dinner", "🌙", listOf("pollo", "verdura_fresca", "pane", "olio", "sale_spezie")),
    MealTemplate("snacks", "Snacks", "🍎", listOf("frutta_fresca", "biscotti", "yogurt", "snack_salati"))
)
