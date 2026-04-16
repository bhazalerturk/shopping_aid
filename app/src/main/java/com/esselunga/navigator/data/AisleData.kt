package com.esselunga.navigator.data

/**
 * Product → Aisle mapping for Esselunga Porta Vittoria (Via Giovanni Cena 8, Milan 20135).
 *
 * CORSIA numbers are PLACEHOLDERS — update them after running batch_search2.py
 * to get real "Trova in negozio" results from the Esselunga app.
 *
 * Each ProductCategory has:
 *   - displayName: Italian label shown in the app
 *   - corsia: aisle number (0-based, physical order in store)
 *   - section: logical section for grouping the shopping list
 *   - keywords: search terms used in the Esselunga app (for reference)
 */
data class ProductCategory(
    val id: String,
    val displayName: String,
    val corsia: Int,          // placeholder — fill in from batch_search2.py output
    val section: StoreSection,
    val keywords: List<String>
)

enum class StoreSection(val label: String) {
    PRODUCE("Frutta e Verdura"),
    BAKERY("Pane e Pasticceria"),
    PASTA_RICE("Pasta, Riso, Farine"),
    CONDIMENTS("Condimenti e Conserve"),
    DAIRY("Latte e Latticini"),
    DELI("Salumi e Formaggi"),
    MEAT("Carne e Pesce"),
    FROZEN("Surgelati"),
    BREAKFAST("Colazione e Snack"),
    DRINKS("Bevande"),
    PERSONAL_CARE("Igiene Persona"),
    CLEANING("Pulizia Casa"),
    PET("Animali e Bambini"),
}

// ── PLACEHOLDER CORSIA VALUES ──────────────────────────────────────────────
// Run batch_search2.py and replace each corsia = X with the real number.
// The route optimizer sorts by corsia ascending, so the relative order matters.
val ALL_CATEGORIES: List<ProductCategory> = listOf(

    // PRODUCE (entrance area)
    ProductCategory("frutta_fresca",       "Frutta fresca",           corsia = 1,  section = StoreSection.PRODUCE,       keywords = listOf("mela", "banana", "arancia")),
    ProductCategory("verdura_fresca",      "Verdura fresca",          corsia = 1,  section = StoreSection.PRODUCE,       keywords = listOf("insalata", "pomodoro", "zucchina")),
    ProductCategory("frutta_secca",        "Frutta secca",            corsia = 2,  section = StoreSection.PRODUCE,       keywords = listOf("noci", "mandorle", "pistacchi")),

    // BAKERY
    ProductCategory("pane",                "Pane",                    corsia = 2,  section = StoreSection.BAKERY,        keywords = listOf("pane", "focaccia")),
    ProductCategory("pasticceria",         "Pasticceria",             corsia = 2,  section = StoreSection.BAKERY,        keywords = listOf("croissant", "torta")),

    // PASTA / RICE / FLOUR
    ProductCategory("pasta_secca",         "Pasta secca",             corsia = 3,  section = StoreSection.PASTA_RICE,   keywords = listOf("spaghetti", "penne", "barilla")),
    ProductCategory("pasta_fresca",        "Pasta fresca",            corsia = 3,  section = StoreSection.PASTA_RICE,   keywords = listOf("tortellini", "ravioli")),
    ProductCategory("riso",                "Riso",                    corsia = 3,  section = StoreSection.PASTA_RICE,   keywords = listOf("riso arborio", "riso basmati")),
    ProductCategory("farina",              "Farina e preparati",      corsia = 3,  section = StoreSection.PASTA_RICE,   keywords = listOf("farina", "lievito")),

    // CONDIMENTS
    ProductCategory("olio",                "Olio",                    corsia = 4,  section = StoreSection.CONDIMENTS,   keywords = listOf("olio extravergine", "olio oliva")),
    ProductCategory("aceto",               "Aceto",                   corsia = 4,  section = StoreSection.CONDIMENTS,   keywords = listOf("aceto balsamico")),
    ProductCategory("sale_spezie",         "Sale e spezie",           corsia = 4,  section = StoreSection.CONDIMENTS,   keywords = listOf("sale", "pepe", "origano")),
    ProductCategory("sughi",               "Sughi e salse",           corsia = 4,  section = StoreSection.CONDIMENTS,   keywords = listOf("sugo pomodoro", "pesto")),
    ProductCategory("conserve",            "Conserve",                corsia = 5,  section = StoreSection.CONDIMENTS,   keywords = listOf("pomodori pelati", "tonno", "fagioli")),

    // BREAKFAST & SNACKS
    ProductCategory("cereali",             "Cereali",                 corsia = 5,  section = StoreSection.BREAKFAST,    keywords = listOf("cereali", "muesli")),
    ProductCategory("biscotti",            "Biscotti",                corsia = 5,  section = StoreSection.BREAKFAST,    keywords = listOf("biscotti", "mulino bianco")),
    ProductCategory("merendine",           "Merendine",               corsia = 6,  section = StoreSection.BREAKFAST,    keywords = listOf("merendine", "kinder")),
    ProductCategory("cioccolato",          "Cioccolato e dolci",      corsia = 6,  section = StoreSection.BREAKFAST,    keywords = listOf("cioccolato", "nutella")),
    ProductCategory("snack_salati",        "Snack salati",            corsia = 6,  section = StoreSection.BREAKFAST,    keywords = listOf("patatine", "crackers")),

    // DRINKS
    ProductCategory("acqua",               "Acqua",                   corsia = 7,  section = StoreSection.DRINKS,       keywords = listOf("acqua naturale", "acqua frizzante")),
    ProductCategory("succhi",              "Succhi e bibite",         corsia = 7,  section = StoreSection.DRINKS,       keywords = listOf("coca cola", "fanta", "succo")),
    ProductCategory("caffe",               "Caffè",                   corsia = 8,  section = StoreSection.DRINKS,       keywords = listOf("caffè", "lavazza", "illy")),
    ProductCategory("te",                  "Tè e tisane",             corsia = 8,  section = StoreSection.DRINKS,       keywords = listOf("tè", "camomilla")),
    ProductCategory("vino",                "Vino",                    corsia = 8,  section = StoreSection.DRINKS,       keywords = listOf("vino rosso", "prosecco")),
    ProductCategory("birra",               "Birra",                   corsia = 9,  section = StoreSection.DRINKS,       keywords = listOf("birra", "moretti", "peroni")),
    ProductCategory("superalcolici",       "Superalcolici",           corsia = 9,  section = StoreSection.DRINKS,       keywords = listOf("vodka", "gin", "whisky")),

    // PERSONAL CARE
    ProductCategory("shampoo",             "Shampoo e doccia",        corsia = 10, section = StoreSection.PERSONAL_CARE, keywords = listOf("shampoo", "bagnoschiuma")),
    ProductCategory("igiene_orale",        "Igiene orale",            corsia = 10, section = StoreSection.PERSONAL_CARE, keywords = listOf("dentifricio", "spazzolino")),
    ProductCategory("deodorante",          "Deodorante",              corsia = 10, section = StoreSection.PERSONAL_CARE, keywords = listOf("deodorante", "dove")),
    ProductCategory("carta_igienica",      "Carta igienica",          corsia = 11, section = StoreSection.PERSONAL_CARE, keywords = listOf("carta igienica", "fazzoletti")),
    ProductCategory("pannolini",           "Pannolini",               corsia = 11, section = StoreSection.PERSONAL_CARE, keywords = listOf("pannolini", "pampers")),
    ProductCategory("assorbenti",          "Assorbenti",              corsia = 11, section = StoreSection.PERSONAL_CARE, keywords = listOf("assorbenti", "lines")),

    // HOUSEHOLD CLEANING
    ProductCategory("detersivo_bucato",    "Detersivo bucato",        corsia = 12, section = StoreSection.CLEANING,     keywords = listOf("detersivo", "dash")),
    ProductCategory("detersivo_piatti",    "Detersivo piatti",        corsia = 12, section = StoreSection.CLEANING,     keywords = listOf("detersivo piatti", "fairy")),
    ProductCategory("detersivo_pavimenti", "Detersivo pavimenti",     corsia = 12, section = StoreSection.CLEANING,     keywords = listOf("lysoform", "swiffer")),
    ProductCategory("sacchetti",           "Sacchetti spazzatura",    corsia = 12, section = StoreSection.CLEANING,     keywords = listOf("sacchetti", "domopak")),

    // PET & BABY
    ProductCategory("cibo_gatti",          "Cibo per gatti",          corsia = 13, section = StoreSection.PET,          keywords = listOf("whiskas", "sheba", "felix")),
    ProductCategory("cibo_cani",           "Cibo per cani",           corsia = 13, section = StoreSection.PET,          keywords = listOf("pedigree", "cesar")),
    ProductCategory("alimenti_bambini",    "Alimenti per bambini",    corsia = 13, section = StoreSection.PET,          keywords = listOf("omogeneizzato", "plasmon")),

    // DAIRY (refrigerated back wall)
    ProductCategory("latte",               "Latte",                   corsia = 14, section = StoreSection.DAIRY,        keywords = listOf("latte intero", "latte parzialmente scremato")),
    ProductCategory("yogurt",              "Yogurt",                  corsia = 14, section = StoreSection.DAIRY,        keywords = listOf("yogurt", "yogurt greco")),
    ProductCategory("formaggi_freschi",    "Formaggi freschi",        corsia = 14, section = StoreSection.DAIRY,        keywords = listOf("mozzarella", "ricotta", "stracchino")),
    ProductCategory("formaggi_stagionati", "Formaggi stagionati",     corsia = 14, section = StoreSection.DAIRY,        keywords = listOf("parmigiano", "grana padano")),
    ProductCategory("burro_panna",         "Burro e panna",           corsia = 14, section = StoreSection.DAIRY,        keywords = listOf("burro", "panna", "mascarpone")),
    ProductCategory("uova",                "Uova",                    corsia = 14, section = StoreSection.DAIRY,        keywords = listOf("uova")),

    // DELI
    ProductCategory("salumi",              "Salumi e affettati",      corsia = 15, section = StoreSection.DELI,         keywords = listOf("prosciutto", "salame", "mortadella")),

    // MEAT & FISH (counter)
    ProductCategory("carne_bovina",        "Carne bovina",            corsia = 16, section = StoreSection.MEAT,         keywords = listOf("manzo", "vitello", "bistecca")),
    ProductCategory("carne_suina",         "Carne suina",             corsia = 16, section = StoreSection.MEAT,         keywords = listOf("maiale", "salsiccia")),
    ProductCategory("pollo",               "Pollo e pollame",         corsia = 16, section = StoreSection.MEAT,         keywords = listOf("pollo", "petto di pollo")),
    ProductCategory("pesce_fresco",        "Pesce fresco",            corsia = 17, section = StoreSection.MEAT,         keywords = listOf("salmone", "merluzzo", "orata")),
    ProductCategory("pesce_surgelato",     "Pesce surgelato",         corsia = 18, section = StoreSection.FROZEN,       keywords = listOf("bastoncini pesce", "gamberi surgelati")),

    // FROZEN
    ProductCategory("surgelati_verdure",   "Surgelati verdure",       corsia = 18, section = StoreSection.FROZEN,       keywords = listOf("spinaci surgelati", "minestrone")),
    ProductCategory("surgelati_pronti",    "Surgelati piatti pronti", corsia = 18, section = StoreSection.FROZEN,       keywords = listOf("pizza surgelata", "lasagna surgelata")),
    ProductCategory("gelati",              "Gelati",                  corsia = 18, section = StoreSection.FROZEN,       keywords = listOf("gelato", "magnum")),
)

/** Find a category by display name (case-insensitive, partial match). */
fun findCategory(query: String): ProductCategory? {
    val q = query.lowercase().trim()
    return ALL_CATEGORIES.firstOrNull { it.displayName.lowercase().contains(q) }
        ?: ALL_CATEGORIES.firstOrNull { it.keywords.any { k -> k.contains(q) || q.contains(k) } }
}
