package com.esselunga.navigator.data

data class Category(
    val id: String,
    val displayName: String,
    val section: StoreSection,
    val corsia: Int

)
data class Product(
    val id: String,
    val name: String,
    val image: String,
    val price: Double,
    val keywords: List<String>,
    val categoryId: String,
    val suggestedPerDay: Double = 0.5
)

enum class StoreSection(val label: String) {
    FRESHPRODUCTS("Fresh Products"),
    BAKERY("Bakery"),
    PASTA_RICE("Pasta, Rice & Grains"),
    CONDIMENTS("Condiments & Preserves"),
    DAIRY("Dairy & Eggs"),
    DELI("Deli & Cheese"),
    MEAT("Meat & Fish"),
    FROZEN("Frozen Foods"),
    BREAKFAST("Breakfast & Snacks"),
    DRINKS("Drinks"),
    PERSONAL_CARE("Personal Care"),
    CLEANING("Household Cleaning"),
    PET("Pet & Baby"),
}
// fruta, verdura, crane, pesce e sushi, formaggi, latte, yogurt e uova, pane, pasta riso e sughi, gelati
val CATEGORIES = listOf(
    Category("frutta","Frutta", StoreSection.FRESHPRODUCTS, 1  ),
    Category("verdura","Verdura", StoreSection.FRESHPRODUCTS, 1  ),
    Category("carne","Carne", StoreSection.MEAT, 1  ),
    Category("pesce_sushi","Pesce e Sushi", StoreSection.MEAT, 1  ),
    Category("formaggi","Formaggi", StoreSection.DAIRY, 1  ),
    Category("latte","Latte", StoreSection.DAIRY, 1  ),
    Category("yougurt_uova","Yogurt e Uova", StoreSection.DAIRY, 1  ),
    Category("pane","Pane", StoreSection.BAKERY, 1  ),
    Category("pasta_riso_sughi","Pasta, riso e sughi", StoreSection.PASTA_RICE, 1  ),
    Category("gelati","Surgelati e gelati", StoreSection.FROZEN, 1  ),
    )

val ALL_CATEGORIES: List<Product> = listOf(
    // PRODUCE
    Product("frutta_fresca", "Frutta fresca", "Fresh Fruit", corsia = 1, section = StoreSection.PRODUCE, keywords = listOf("mela", "banana", "arancia", "apple", "fruit"), defaultPrice = 2.50, suggestedPerDay = 1.0),
    Product("verdura_fresca", "Verdura fresca", "Fresh Vegetables", corsia = 1, section = StoreSection.PRODUCE, keywords = listOf("insalata", "pomodoro", "zucchina", "salad", "tomato", "vegetables"), defaultPrice = 2.20, suggestedPerDay = 1.0),
    Product("frutta_secca", "Frutta secca", "Nuts & Dried Fruit", corsia = 2, section = StoreSection.PRODUCE, keywords = listOf("noci", "mandorle", "pistacchi", "nuts", "almonds"), defaultPrice = 4.50, suggestedPerDay = 0.2),
    // BAKERY
    Product("pane", "Pane", "Bread", corsia = 2, section = StoreSection.BAKERY, keywords = listOf("pane", "focaccia", "bread"), defaultPrice = 1.50, suggestedPerDay = 0.5),
    Product("pasticceria", "Pasticceria", "Pastries", corsia = 2, section = StoreSection.BAKERY, keywords = listOf("croissant", "torta", "pastry"), defaultPrice = 2.80, suggestedPerDay = 0.2),
    // PASTA / RICE
    Product("pasta_secca", "Pasta secca", "Pasta", corsia = 3, section = StoreSection.PASTA_RICE, keywords = listOf("spaghetti", "penne", "barilla", "pasta"), defaultPrice = 1.20, suggestedPerDay = 0.4),
    Product("pasta_fresca", "Pasta fresca", "Fresh Pasta", corsia = 3, section = StoreSection.PASTA_RICE, keywords = listOf("tortellini", "ravioli", "fresh pasta"), defaultPrice = 3.50, suggestedPerDay = 0.3),
    Product("riso", "Riso", "Rice", corsia = 3, section = StoreSection.PASTA_RICE, keywords = listOf("riso", "arborio", "basmati", "rice"), defaultPrice = 1.80, suggestedPerDay = 0.3),
    Product("farina", "Farina e preparati", "Flour & Baking", corsia = 3, section = StoreSection.PASTA_RICE, keywords = listOf("farina", "lievito", "flour"), defaultPrice = 1.30, suggestedPerDay = 0.1),
    // CONDIMENTS
    Product("olio", "Olio", "Olive Oil", corsia = 4, section = StoreSection.CONDIMENTS, keywords = listOf("olio", "olive oil"), defaultPrice = 5.50, suggestedPerDay = 0.1),
    Product("aceto", "Aceto", "Vinegar", corsia = 4, section = StoreSection.CONDIMENTS, keywords = listOf("aceto", "balsamic", "vinegar"), defaultPrice = 2.50, suggestedPerDay = 0.05),
    Product("sale_spezie", "Sale e spezie", "Salt & Spices", corsia = 4, section = StoreSection.CONDIMENTS, keywords = listOf("sale", "pepe", "origano", "salt", "pepper", "spices"), defaultPrice = 1.50, suggestedPerDay = 0.05),
    Product("sughi", "Sughi e salse", "Sauces", corsia = 4, section = StoreSection.CONDIMENTS, keywords = listOf("sugo", "pesto", "sauce", "tomato sauce"), defaultPrice = 2.20, suggestedPerDay = 0.3),
    Product("conserve", "Conserve", "Canned Goods", corsia = 5, section = StoreSection.CONDIMENTS, keywords = listOf("pelati", "tonno", "fagioli", "canned", "chickpeas", "ceci"), defaultPrice = 1.60, suggestedPerDay = 0.3),
    // BREAKFAST
    Product("cereali", "Cereali", "Cereal", corsia = 5, section = StoreSection.BREAKFAST, keywords = listOf("cereali", "muesli", "cereal"), defaultPrice = 3.20, suggestedPerDay = 0.3),
    Product("biscotti", "Biscotti", "Cookies & Biscuits", corsia = 5, section = StoreSection.BREAKFAST, keywords = listOf("biscotti", "mulino bianco", "cookies"), defaultPrice = 2.50, suggestedPerDay = 0.2),
    Product("merendine", "Merendine", "Snack Cakes", corsia = 6, section = StoreSection.BREAKFAST, keywords = listOf("merendine", "kinder"), defaultPrice = 2.80, suggestedPerDay = 0.2),
    Product("cioccolato", "Cioccolato e dolci", "Chocolate & Sweets", corsia = 6, section = StoreSection.BREAKFAST, keywords = listOf("cioccolato", "nutella", "chocolate"), defaultPrice = 2.90, suggestedPerDay = 0.1),
    Product("snack_salati", "Snack salati", "Savoury Snacks", corsia = 6, section = StoreSection.BREAKFAST, keywords = listOf("patatine", "crackers", "chips", "snack"), defaultPrice = 2.10, suggestedPerDay = 0.2),
    // DRINKS
    Product("acqua", "Acqua", "Water", corsia = 7, section = StoreSection.DRINKS, keywords = listOf("acqua", "water"), defaultPrice = 0.50, suggestedPerDay = 2.0),
    Product("succhi", "Succhi e bibite", "Juice & Soft Drinks", corsia = 7, section = StoreSection.DRINKS, keywords = listOf("coca cola", "fanta", "succo", "juice", "soda"), defaultPrice = 1.80, suggestedPerDay = 0.5),
    Product("caffe", "Caffè", "Coffee", corsia = 8, section = StoreSection.DRINKS, keywords = listOf("caffè", "lavazza", "coffee"), defaultPrice = 3.50, suggestedPerDay = 0.3),
    Product("te", "Tè e tisane", "Tea & Herbal", corsia = 8, section = StoreSection.DRINKS, keywords = listOf("tè", "camomilla", "tea"), defaultPrice = 2.20, suggestedPerDay = 0.2),
    Product("vino", "Vino", "Wine", corsia = 8, section = StoreSection.DRINKS, keywords = listOf("vino", "prosecco", "wine"), defaultPrice = 7.00, suggestedPerDay = 0.1),
    Product("birra", "Birra", "Beer", corsia = 9, section = StoreSection.DRINKS, keywords = listOf("birra", "moretti", "beer"), defaultPrice = 4.50, suggestedPerDay = 0.1),
    Product("superalcolici", "Superalcolici", "Spirits", corsia = 9, section = StoreSection.DRINKS, keywords = listOf("vodka", "gin", "whisky", "spirits"), defaultPrice = 18.00, suggestedPerDay = 0.0),
    // PERSONAL CARE
    Product("shampoo", "Shampoo e doccia", "Shampoo & Shower", corsia = 10, section = StoreSection.PERSONAL_CARE, keywords = listOf("shampoo", "bagnoschiuma", "shower gel"), defaultPrice = 4.50, suggestedPerDay = 0.05),
    Product("igiene_orale", "Igiene orale", "Oral Hygiene", corsia = 10, section = StoreSection.PERSONAL_CARE, keywords = listOf("dentifricio", "spazzolino", "toothpaste", "toothbrush"), defaultPrice = 3.20, suggestedPerDay = 0.02),
    Product("deodorante", "Deodorante", "Deodorant", corsia = 10, section = StoreSection.PERSONAL_CARE, keywords = listOf("deodorante", "deodorant"), defaultPrice = 3.50, suggestedPerDay = 0.02),
    Product("carta_igienica", "Carta igienica", "Toilet Paper", corsia = 11, section = StoreSection.PERSONAL_CARE, keywords = listOf("carta igienica", "fazzoletti", "toilet paper", "tissues"), defaultPrice = 3.80, suggestedPerDay = 0.1),
    Product("pannolini", "Pannolini", "Nappies", corsia = 11, section = StoreSection.PERSONAL_CARE, keywords = listOf("pannolini", "pampers", "nappies"), defaultPrice = 12.00, suggestedPerDay = 0.0),
    Product("assorbenti", "Assorbenti", "Sanitary Products", corsia = 11, section = StoreSection.PERSONAL_CARE, keywords = listOf("assorbenti", "lines"), defaultPrice = 4.20, suggestedPerDay = 0.0),
    // CLEANING
    Product("detersivo_bucato", "Detersivo bucato", "Laundry Detergent", corsia = 12, section = StoreSection.CLEANING, keywords = listOf("detersivo", "dash", "laundry"), defaultPrice = 6.50, suggestedPerDay = 0.05),
    Product("detersivo_piatti", "Detersivo piatti", "Dish Soap", corsia = 12, section = StoreSection.CLEANING, keywords = listOf("detersivo piatti", "fairy", "dish soap"), defaultPrice = 2.80, suggestedPerDay = 0.05),
    Product("detersivo_pavimenti", "Detersivo pavimenti", "Floor Cleaner", corsia = 12, section = StoreSection.CLEANING, keywords = listOf("lysoform", "swiffer", "floor cleaner"), defaultPrice = 3.50, suggestedPerDay = 0.02),
    Product("sacchetti", "Sacchetti spazzatura", "Bin Bags", corsia = 12, section = StoreSection.CLEANING, keywords = listOf("sacchetti", "domopak", "bin bags"), defaultPrice = 2.40, suggestedPerDay = 0.05),
    // PET
    Product("cibo_gatti", "Cibo per gatti", "Cat Food", corsia = 13, section = StoreSection.PET, keywords = listOf("whiskas", "sheba", "felix", "cat food"), defaultPrice = 3.20, suggestedPerDay = 0.0),
    Product("cibo_cani", "Cibo per cani", "Dog Food", corsia = 13, section = StoreSection.PET, keywords = listOf("pedigree", "cesar", "dog food"), defaultPrice = 3.50, suggestedPerDay = 0.0),
    Product("alimenti_bambini", "Alimenti per bambini", "Baby Food", corsia = 13, section = StoreSection.PET, keywords = listOf("omogeneizzato", "plasmon", "baby food"), defaultPrice = 2.20, suggestedPerDay = 0.0),
    // DAIRY
    Product("latte", "Latte", "Milk", corsia = 14, section = StoreSection.DAIRY, keywords = listOf("latte", "milk"), defaultPrice = 1.20, suggestedPerDay = 0.5),
    Product("yogurt", "Yogurt", "Yogurt", corsia = 14, section = StoreSection.DAIRY, keywords = listOf("yogurt", "yogurt greco"), defaultPrice = 0.90, suggestedPerDay = 0.5),
    Product("formaggi_freschi", "Formaggi freschi", "Fresh Cheese", corsia = 14, section = StoreSection.DAIRY, keywords = listOf("mozzarella", "ricotta", "stracchino", "cheese"), defaultPrice = 2.50, suggestedPerDay = 0.2),
    Product("formaggi_stagionati", "Formaggi stagionati", "Aged Cheese", corsia = 14, section = StoreSection.DAIRY, keywords = listOf("parmigiano", "grana padano", "parmesan"), defaultPrice = 6.50, suggestedPerDay = 0.1),
    Product("burro_panna", "Burro e panna", "Butter & Cream", corsia = 14, section = StoreSection.DAIRY, keywords = listOf("burro", "panna", "butter", "cream"), defaultPrice = 2.10, suggestedPerDay = 0.1),
    Product("uova", "Uova", "Eggs", corsia = 14, section = StoreSection.DAIRY, keywords = listOf("uova", "eggs"), defaultPrice = 2.80, suggestedPerDay = 0.3),
    // DELI
    Product("salumi", "Salumi e affettati", "Deli Meats", corsia = 15, section = StoreSection.DELI, keywords = listOf("prosciutto", "salame", "mortadella", "ham", "salami"), defaultPrice = 4.50, suggestedPerDay = 0.2),
    // MEAT
    Product("carne_bovina", "Carne bovina", "Beef", corsia = 16, section = StoreSection.MEAT, keywords = listOf("manzo", "vitello", "bistecca", "beef"), defaultPrice = 8.00, suggestedPerDay = 0.2),
    Product("carne_suina", "Carne suina", "Pork", corsia = 16, section = StoreSection.MEAT, keywords = listOf("maiale", "salsiccia", "pork", "sausage"), defaultPrice = 5.50, suggestedPerDay = 0.2),
    Product("pollo", "Pollo e pollame", "Chicken", corsia = 16, section = StoreSection.MEAT, keywords = listOf("pollo", "petto di pollo", "chicken"), defaultPrice = 5.20, suggestedPerDay = 0.3),
    Product("pesce_fresco", "Pesce fresco", "Fresh Fish", corsia = 17, section = StoreSection.MEAT, keywords = listOf("salmone", "merluzzo", "orata", "fish"), defaultPrice = 9.50, suggestedPerDay = 0.1),
    Product("pesce_surgelato", "Pesce surgelato", "Frozen Fish", corsia = 18, section = StoreSection.FROZEN, keywords = listOf("bastoncini", "gamberi", "frozen fish"), defaultPrice = 5.00, suggestedPerDay = 0.1),
    // FROZEN
    Product("surgelati_verdure", "Surgelati verdure", "Frozen Vegetables", corsia = 18, section = StoreSection.FROZEN, keywords = listOf("spinaci surgelati", "minestrone", "frozen vegetables"), defaultPrice = 2.80, suggestedPerDay = 0.2),
    Product("surgelati_pronti", "Surgelati piatti pronti", "Frozen Meals", corsia = 18, section = StoreSection.FROZEN, keywords = listOf("pizza surgelata", "lasagna", "frozen meals"), defaultPrice = 4.20, suggestedPerDay = 0.1),
    Product("gelati", "Gelati", "Ice Cream", corsia = 18, section = StoreSection.FROZEN, keywords = listOf("gelato", "magnum", "ice cream"), defaultPrice = 3.50, suggestedPerDay = 0.1),
)

fun findCategory(query: String): Product? {
    val q = query.lowercase().trim()
    return ALL_CATEGORIES.firstOrNull { it.displayName.lowercase().contains(q) }
        ?: ALL_CATEGORIES.firstOrNull { it.displayNameEn.lowercase().contains(q) }
        ?: ALL_CATEGORIES.firstOrNull { it.keywords.any { k -> k.contains(q) || q.contains(k) } }
}
