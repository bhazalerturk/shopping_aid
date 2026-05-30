"""
Esselunga Porta Vittoria - Product Category to Aisle (Corsia) Mapping
Store: Via Giovanni Cena 8, Milan 20135

Instructions: Fill in the CORSIA number for each category based on the Esselunga app
"Trova in negozio" search results.
"""

# Standard Italian supermarket product categories
# Format: category_name: {"corsia": None, "keywords": [...], "notes": ""}
# The keywords are what to search in the app to find the aisle

CATEGORIES = {
    # === FRUTTA E VERDURA (Produce) ===
    "Frutta fresca": {"corsia": None, "keywords": ["mela", "banana", "arancia", "fragola", "uva"]},
    "Verdura fresca": {"corsia": None, "keywords": ["insalata", "pomodoro", "zucchina", "carota", "patata"]},
    "Frutta secca": {"corsia": None, "keywords": ["noci", "mandorle", "noccioline", "pistacchi"]},

    # === PANE E PASTICCERIA (Bakery) ===
    "Pane": {"corsia": None, "keywords": ["pane", "focaccia", "grissini"]},
    "Pasticceria": {"corsia": None, "keywords": ["croissant", "torta", "biscotti artigianali"]},

    # === PASTA, RISO, FARINA ===
    "Pasta secca": {"corsia": None, "keywords": ["spaghetti", "penne", "fusilli", "barilla"]},
    "Pasta fresca": {"corsia": None, "keywords": ["tortellini", "ravioli", "gnocchi"]},
    "Riso": {"corsia": None, "keywords": ["riso arborio", "riso basmati", "riso carnaroli"]},
    "Farina e preparati": {"corsia": None, "keywords": ["farina", "lievito", "preparato torta"]},

    # === CONDIMENTI E CONSERVE ===
    "Olio": {"corsia": None, "keywords": ["olio extravergine", "olio oliva", "olio semi"]},
    "Aceto": {"corsia": None, "keywords": ["aceto balsamico", "aceto vino"]},
    "Sale e spezie": {"corsia": None, "keywords": ["sale", "pepe", "origano", "basilico secco"]},
    "Sughi e salse": {"corsia": None, "keywords": ["sugo pomodoro", "pesto", "salsa"]},
    "Conserve": {"corsia": None, "keywords": ["pomodori pelati", "tonno", "fagioli", "legumi"]},

    # === LATTE E LATTICINI (Dairy) ===
    "Latte": {"corsia": None, "keywords": ["latte intero", "latte parzialmente scremato"]},
    "Yogurt": {"corsia": None, "keywords": ["yogurt", "yogurt greco", "activia"]},
    "Formaggi freschi": {"corsia": None, "keywords": ["mozzarella", "ricotta", "stracchino", "philadelphia"]},
    "Formaggi stagionati": {"corsia": None, "keywords": ["parmigiano", "grana padano", "pecorino"]},
    "Burro e panna": {"corsia": None, "keywords": ["burro", "panna", "mascarpone"]},

    # === UOVA ===
    "Uova": {"corsia": None, "keywords": ["uova"]},

    # === SALUMI E AFFETTATI ===
    "Salumi": {"corsia": None, "keywords": ["prosciutto", "salame", "mortadella", "bresaola"]},

    # === CARNE (Meat) ===
    "Carne bovina": {"corsia": None, "keywords": ["manzo", "vitello", "bistecca"]},
    "Carne suina": {"corsia": None, "keywords": ["maiale", "costine", "salsiccia"]},
    "Pollo e pollame": {"corsia": None, "keywords": ["pollo", "tacchino", "petto di pollo"]},

    # === PESCE (Fish) ===
    "Pesce fresco": {"corsia": None, "keywords": ["salmone fresco", "merluzzo", "orata"]},
    "Pesce surgelato": {"corsia": None, "keywords": ["bastoncini pesce", "gamberi surgelati"]},

    # === SURGELATI (Frozen) ===
    "Surgelati verdure": {"corsia": None, "keywords": ["spinaci surgelati", "piselli surgelati", "minestrone"]},
    "Surgelati piatti pronti": {"corsia": None, "keywords": ["pizza surgelata", "lasagna surgelata"]},
    "Gelati": {"corsia": None, "keywords": ["gelato", "magnum", "cornetto algida"]},

    # === COLAZIONE E SNACK (Breakfast & Snacks) ===
    "Cereali": {"corsia": None, "keywords": ["cereali", "muesli", "corn flakes"]},
    "Biscotti": {"corsia": None, "keywords": ["biscotti", "mulino bianco", "oro saiwa"]},
    "Merendine": {"corsia": None, "keywords": ["merendine", "kinder", "brioche confezionate"]},
    "Cioccolato e dolci": {"corsia": None, "keywords": ["cioccolato", "nutella", "crema spalmabile"]},
    "Snack salati": {"corsia": None, "keywords": ["patatine", "chips", "crackers", "taralli"]},

    # === BEVANDE (Drinks) ===
    "Acqua": {"corsia": None, "keywords": ["acqua naturale", "acqua frizzante", "san benedetto"]},
    "Succhi e bibite": {"corsia": None, "keywords": ["succo arancia", "coca cola", "fanta", "aranciata"]},
    "Caffè": {"corsia": None, "keywords": ["caffè", "lavazza", "illy", "nespresso"]},
    "Tè e tisane": {"corsia": None, "keywords": ["tè", "tisana", "camomilla"]},
    "Vino": {"corsia": None, "keywords": ["vino rosso", "vino bianco", "prosecco"]},
    "Birra": {"corsia": None, "keywords": ["birra", "moretti", "peroni", "heineken"]},
    "Superalcolici": {"corsia": None, "keywords": ["vodka", "gin", "rum", "whisky"]},

    # === IGIENE PERSONA (Personal Care) ===
    "Shampoo e doccia": {"corsia": None, "keywords": ["shampoo", "bagnoschiuma", "docciaschiuma"]},
    "Dentifricio e igiene orale": {"corsia": None, "keywords": ["dentifricio", "spazzolino", "collutorio"]},
    "Deodorante": {"corsia": None, "keywords": ["deodorante", "dove", "nivea"]},
    "Carta igienica": {"corsia": None, "keywords": ["carta igienica", "fazzoletti", "scottex"]},
    "Pannolini": {"corsia": None, "keywords": ["pannolini", "pampers"]},
    "Assorbenti": {"corsia": None, "keywords": ["assorbenti", "lines"]},

    # === PULIZIA CASA (Household Cleaning) ===
    "Detersivo bucato": {"corsia": None, "keywords": ["detersivo", "dash", "omino bianco"]},
    "Detersivo piatti": {"corsia": None, "keywords": ["detersivo piatti", "fairy"]},
    "Detersivo pavimenti": {"corsia": None, "keywords": ["pavimenti", "swiffer", "lysoform"]},
    "Sacchetti spazzatura": {"corsia": None, "keywords": ["sacchetti", "spazzatura", "domopak"]},

    # === CIBO ANIMALI (Pet Food) ===
    "Cibo per gatti": {"corsia": None, "keywords": ["cibo gatto", "whiskas", "sheba", "felix"]},
    "Cibo per cani": {"corsia": None, "keywords": ["cibo cane", "pedigree", "cesar"]},

    # === BAMBINI (Baby) ===
    "Alimenti per bambini": {"corsia": None, "keywords": ["omogeneizzato", "plasmon", "latte artificiale"]},
}


def print_categories():
    """Print all categories for the user to fill in aisle numbers."""
    print("\n=== ESSELUNGA PORTA VITTORIA - CATEGORIE ===\n")
    for cat, info in CATEGORIES.items():
        corsia = info["corsia"] if info["corsia"] else "???"
        keywords = ", ".join(info["keywords"][:2])
        print(f"  {cat:<30} Corsia: {corsia:<5}  (cerca: {keywords})")


if __name__ == "__main__":
    print_categories()
