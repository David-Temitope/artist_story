package com.example.data

object GameData {
    val SIDE_JOBS = listOf(
        SideJob("job_flyer", "Flyer Distributing", weeklyPay = 100.0, energyCost = 10, timeCost = 1),
        SideJob("job_coffee", "Coffee Shop Barista", weeklyPay = 300.0, energyCost = 20, timeCost = 2),
        SideJob("job_music", "Music Store Assistant", weeklyPay = 500.0, energyCost = 25, timeCost = 3),
        SideJob("job_beat", "Freelance Beatmaker", weeklyPay = 800.0, energyCost = 30, timeCost = 4),
        SideJob("job_ghost", "Ghostwriter", weeklyPay = 1200.0, energyCost = 40, timeCost = 5),
        SideJob("job_dj", "Club DJ Set", weeklyPay = 2000.0, energyCost = 50, timeCost = 6),
        SideJob("job_tutor", "Private Music Tutor", weeklyPay = 1500.0, energyCost = 35, timeCost = 4),
        SideJob("job_voice", "Voiceover Artist", weeklyPay = 1000.0, energyCost = 20, timeCost = 3)
    )

    val GENRES = listOf(
        Genre("genre_pop", "Pop", productionCostMultiplier = 1.0f, listenerAppeal = 1.2f),
        Genre("genre_hiphop", "Hip Hop", productionCostMultiplier = 0.8f, listenerAppeal = 1.3f),
        Genre("genre_rock", "Rock", productionCostMultiplier = 0.9f, listenerAppeal = 1.0f),
        Genre("genre_rnb", "R&B", productionCostMultiplier = 1.0f, listenerAppeal = 1.1f),
        Genre("genre_electronic", "Electronic", productionCostMultiplier = 1.1f, listenerAppeal = 1.0f),
        Genre("genre_country", "Country", productionCostMultiplier = 0.8f, listenerAppeal = 0.9f),
        Genre("genre_jazz", "Jazz", productionCostMultiplier = 1.2f, listenerAppeal = 0.8f),
        Genre("genre_indie", "Indie", productionCostMultiplier = 0.7f, listenerAppeal = 0.9f)
    )

    val PRODUCERS = listOf(
        Producer("prod_beg_1", "Beginner Ben", "Beginner", cost = 500.0, ratingBonus = 0.2f),
        Producer("prod_exp_1", "Experienced Emma", "Experienced", cost = 1500.0, ratingBonus = 0.5f),
        Producer("prod_elite_1", "Elite Eric", "Elite", cost = 3000.0, ratingBonus = 1.0f),
        Producer("prod_phoenix", "The Legendary Phoenix", "Legendary", cost = 5000.0, ratingBonus = 1.5f)
    )

    val CARS = listOf(
        Car("car_sedan", "Beat-up Sedan", cost = 5000.0, costReductionPercent = 0.15f),
        Car("car_suv", "Midrange SUV", cost = 15000.0, costReductionPercent = 0.30f),
        Car("car_sports", "Luxury Sports Coupe", cost = 60000.0, costReductionPercent = 0.50f),
        Car("car_bus", "Touring Coach Bus", cost = 250000.0, costReductionPercent = 0.80f),
        Car("porsche_gt3", "Porsche 911 GT3 RS", cost = 295000.0, costReductionPercent = 0.85f),
        Car("maybach_s680", "Mercedes-Maybach S680", cost = 340000.0, costReductionPercent = 0.88f),
        Car("aston_dbs", "Aston Martin DBS Superleggera", cost = 385000.0, costReductionPercent = 0.88f),
        Car("mclaren_750s", "McLaren 750S Spider", cost = 460000.0, costReductionPercent = 0.90f),
        Car("rolls_phantom", "Rolls-Royce Phantom Series II", cost = 480000.0, costReductionPercent = 0.90f),
        Car("ferrari_sf90", "Ferrari SF90 Stradale Hybrid", cost = 620000.0, costReductionPercent = 0.92f),
        Car("lambo_revuelto", "Lamborghini Revuelto V12", cost = 780000.0, costReductionPercent = 0.95f),
        Car("bugatti_chiron", "Bugatti Chiron Super Sport", cost = 3800000.0, costReductionPercent = 0.98f)
    )

    val HOUSES = listOf(
        House("house_studio", "Cozy Studio Apartment", cost = 20000.0, energyBonus = 5),
        House("house_townhouse", "Suburban Townhouse", cost = 80000.0, energyBonus = 12),
        House("house_loft", "Downtown Loft", cost = 200000.0, energyBonus = 25),
        House("house_mansion", "Sunset Hills Mansion", cost = 1000000.0, energyBonus = 50),
        House("executive_loft", "Downtown Executive High-Rise Loft", cost = 850000.0, energyBonus = 30),
        House("palm_estate", "Suburban Palm Estate & Grounds", cost = 1800000.0, energyBonus = 45),
        House("alpine_retreat", "Alpine Mountain Modern Sanctuary", cost = 2900000.0, energyBonus = 55),
        House("skyline_penthouse", "Skyline View Penthouse & Studio", cost = 4250000.0, energyBonus = 60),
        House("coastal_villa", "Coastal Villa - The Glass Pavilion", cost = 5100000.0, energyBonus = 70),
        House("miami_estate", "Miami Waterfront Modern Estate", cost = 7800000.0, energyBonus = 78),
        House("sunset_ultra_mansion", "Sunset Hills Ultra-Mansion", cost = 12500000.0, energyBonus = 85),
        House("beverly_manor", "Beverly Hills Classical Manor", cost = 22000000.0, energyBonus = 100)
    )

    val INSTRUMENTS = listOf(
        Instrument("synth_prophet", "Prophet Rev2 8-Voice", cost = 2200.0, ratingBonus = 0.35f),
        Instrument("synth_moog", "Moog Modular System Studio 55", cost = 14500.0, ratingBonus = 0.70f),
        Instrument("synth_jupiter", "Roland Jupiter-X Synthesizer", cost = 2800.0, ratingBonus = 0.40f),
        Instrument("synth_kronos", "Korg Kronos 88-Key Flagship", cost = 3900.0, ratingBonus = 0.45f),
        Instrument("strat_60s", "Fender Custom 1960 Stratocaster", cost = 4500.0, ratingBonus = 0.45f),
        Instrument("gibson_lespaul", "Gibson Les Paul 1959 Reissue", cost = 6800.0, ratingBonus = 0.55f),
        Instrument("martin_d28", "Martin D-28 Authentic Acoustic", cost = 3600.0, ratingBonus = 0.40f),
        Instrument("ssl_console", "SSL 4000 G-Series Console Unit", cost = 18500.0, ratingBonus = 0.80f),
        Instrument("neve_1073", "Neve 1073 Dual Preamp Strip", cost = 3800.0, ratingBonus = 0.45f),
        Instrument("la2a_comp", "Teletronix LA-2A Leveling Amplifier", cost = 4200.0, ratingBonus = 0.50f),
        Instrument("mic_u87", "Neumann U87 Ai Multi-Pattern", cost = 3600.0, ratingBonus = 0.45f),
        Instrument("mic_c800g", "Sony C-800G Tube Studio Condenser", cost = 11000.0, ratingBonus = 0.75f)
    )
    val STOCKS = listOf(
        Stock(
            id = "stock_media",
            name = "MediaGiant",
            currentPrice = 50.0,
            weeklyDividendPercent = 0.015f,
            connectedTo = "MediaGiant",
            sector = "Broadcasting & Media",
            quarterlyEarnings = 4200000.0,
            PE_Ratio = 16.5,
            dividendYieldPercent = 2.2f,
            marketCap = 125000000.0,
            financialHealth = "Stable",
            initialPrice = 50.0,
            allTimeHigh = 50.0
        ),
        Stock(
            id = "stock_apex",
            name = "ApexRecordCorp",
            currentPrice = 120.0,
            weeklyDividendPercent = 0.03f,
            sector = "Music Publishing & Distribution",
            quarterlyEarnings = 8900000.0,
            PE_Ratio = 13.8,
            dividendYieldPercent = 3.4f,
            marketCap = 480000000.0,
            financialHealth = "Strong",
            initialPrice = 120.0,
            allTimeHigh = 120.0
        ),
        Stock(
            id = "stock_space",
            name = "StudioSpaceInc",
            currentPrice = 35.0,
            weeklyDividendPercent = 0.02f,
            sector = "Creative Real Estate",
            quarterlyEarnings = 1800000.0,
            PE_Ratio = 12.2,
            dividendYieldPercent = 2.6f,
            marketCap = 85000000.0,
            financialHealth = "Stable",
            initialPrice = 35.0,
            allTimeHigh = 35.0
        ),
        Stock(
            id = "stock_stream",
            name = "StreamingStar",
            currentPrice = 80.0,
            weeklyDividendPercent = 0.015f,
            sector = "Digital Streaming Tech",
            quarterlyEarnings = 6200000.0,
            PE_Ratio = 27.5,
            dividendYieldPercent = 1.8f,
            marketCap = 340000000.0,
            financialHealth = "Strong",
            initialPrice = 80.0,
            allTimeHigh = 80.0
        ),
        Stock(
            id = "stock_acoustic",
            name = "AcousticVibe Instruments",
            currentPrice = 45.0,
            weeklyDividendPercent = 0.01f,
            sector = "Audio Equipment & Guitars",
            quarterlyEarnings = 1500000.0,
            PE_Ratio = 11.5,
            dividendYieldPercent = 1.6f,
            marketCap = 62000000.0,
            financialHealth = "Stable",
            initialPrice = 45.0,
            allTimeHigh = 45.0
        ),
        Stock(
            id = "stock_label",
            name = "GlobalIndie Records",
            currentPrice = 65.0,
            weeklyDividendPercent = 0.025f,
            sector = "Independent Labels",
            quarterlyEarnings = 3100000.0,
            PE_Ratio = 14.0,
            dividendYieldPercent = 2.8f,
            marketCap = 110000000.0,
            financialHealth = "Stable",
            initialPrice = 65.0,
            allTimeHigh = 65.0
        ),
        Stock(
            id = "stock_auto",
            name = "TeslaDrive Autos",
            currentPrice = 210.0,
            weeklyDividendPercent = 0.005f,
            sector = "EV & Clean Tech",
            quarterlyEarnings = 14500000.0,
            PE_Ratio = 36.5,
            dividendYieldPercent = 0.8f,
            marketCap = 920000000.0,
            financialHealth = "Strong",
            initialPrice = 210.0,
            allTimeHigh = 210.0
        ),
        Stock(
            id = "stock_micro",
            name = "SiliconSound Chipsets",
            currentPrice = 185.0,
            weeklyDividendPercent = 0.018f,
            sector = "Audio DSP & Semiconductors",
            quarterlyEarnings = 11200000.0,
            PE_Ratio = 23.0,
            dividendYieldPercent = 2.1f,
            marketCap = 750000000.0,
            financialHealth = "Strong",
            initialPrice = 185.0,
            allTimeHigh = 185.0
        ),
        Stock(
            id = "stock_realestate",
            name = "SunsetHills Realty",
            currentPrice = 140.0,
            weeklyDividendPercent = 0.035f,
            sector = "Commercial Real Estate & REIT",
            quarterlyEarnings = 9500000.0,
            PE_Ratio = 10.8,
            dividendYieldPercent = 4.2f,
            marketCap = 610000000.0,
            financialHealth = "Strong",
            initialPrice = 140.0,
            allTimeHigh = 140.0
        )
    )

    val CRYPTOS = listOf(
        Crypto(
            id = "crypto_bit",
            name = "BitMusic (BTCM)",
            currentPrice = 45000.0,
            supportLevel = 35000.0,
            resistanceLevel = 68000.0,
            developerActivity = "High",
            marketCap = 880000000000.0,
            volume24h = 28500000000.0,
            initialPrice = 45000.0,
            allTimeHigh = 45000.0,
            cycleFloor = 38000.0
        ),
        Crypto(
            id = "crypto_ether",
            name = "EtherMelody (ETHM)",
            currentPrice = 3200.0,
            supportLevel = 2400.0,
            resistanceLevel = 4800.0,
            developerActivity = "High",
            marketCap = 385000000000.0,
            volume24h = 14200000000.0,
            initialPrice = 3200.0,
            allTimeHigh = 3200.0,
            cycleFloor = 2700.0
        ),
        Crypto(
            id = "crypto_sol",
            name = "SolanaSound (SOLS)",
            currentPrice = 150.0,
            supportLevel = 105.0,
            resistanceLevel = 260.0,
            developerActivity = "High",
            marketCap = 68000000000.0,
            volume24h = 4200000000.0,
            initialPrice = 150.0,
            allTimeHigh = 150.0,
            cycleFloor = 125.0
        ),
        Crypto(
            id = "crypto_ripple",
            name = "RippleArt (XRP)",
            currentPrice = 1.20,
            supportLevel = 0.85,
            resistanceLevel = 2.20,
            developerActivity = "Medium",
            marketCap = 65000000000.0,
            volume24h = 2100000000.0,
            initialPrice = 1.20,
            allTimeHigh = 1.20,
            cycleFloor = 1.00
        ),
        Crypto(
            id = "crypto_cardano",
            name = "AdaMelody (ADAM)",
            currentPrice = 0.65,
            supportLevel = 0.45,
            resistanceLevel = 1.20,
            developerActivity = "Medium",
            marketCap = 22000000000.0,
            volume24h = 850000000.0,
            initialPrice = 0.65,
            allTimeHigh = 0.65,
            cycleFloor = 0.55
        ),
        Crypto(
            id = "crypto_polka",
            name = "PolkaBand (DOTB)",
            currentPrice = 6.20,
            supportLevel = 4.40,
            resistanceLevel = 12.00,
            developerActivity = "Medium",
            marketCap = 8500000000.0,
            volume24h = 400000000.0,
            initialPrice = 6.20,
            allTimeHigh = 6.20,
            cycleFloor = 5.00
        ),
        Crypto(
            id = "crypto_doge",
            name = "DogeTune (DOGET)",
            currentPrice = 0.15,
            supportLevel = 0.09,
            resistanceLevel = 0.40,
            developerActivity = "Low",
            marketCap = 21000000000.0,
            volume24h = 1800000000.0,
            initialPrice = 0.15,
            allTimeHigh = 0.15,
            cycleFloor = 0.11
        ),
        Crypto(
            id = "crypto_shib",
            name = "ShibHype (SHIBH)",
            currentPrice = 0.000025,
            supportLevel = 0.000015,
            resistanceLevel = 0.000080,
            developerActivity = "Low",
            marketCap = 14500000000.0,
            volume24h = 920000000.0,
            initialPrice = 0.000025,
            allTimeHigh = 0.000025,
            cycleFloor = 0.000019
        )
    )

    val WARDROBE_ITEMS = listOf(
        // HAIR STYLES
        WardrobeItem("hair_slick", "Sleek Side-Part", "HAIR", 0.0, "Classic look, clean and professional."),
        WardrobeItem("hair_afro", "Retro Afro Buzz", "HAIR", 150.0, "Voluminous textured hair, bold presence."),
        WardrobeItem("hair_braids", "Regal Braids", "HAIR", 350.0, "Elegant long braids, frames the face beautifully."),
        WardrobeItem("hair_spiky", "Neon Green Spikes", "HAIR", 500.0, "Cyber punk spiky hairstyle in neon dye."),
        WardrobeItem("hair_waves", "Ocean Waves", "HAIR", 800.0, "Smooth 360-degree waves, perfectly laid."),
        WardrobeItem("hair_mohawk", "Glitch Punk Mohawk", "HAIR", 1200.0, "Fierce orange mohawk with shaved sides."),
        WardrobeItem("hair_bob", "Sleek Silver Bob", "HAIR", 1500.0, "Futuristic silver colored bob haircut."),
        WardrobeItem("hair_dreads", "Gold-Tipped Dreads", "HAIR", 2500.0, "Thick locked hair cords with metallic gold tips."),

        // CLOTHING
        WardrobeItem("cloth_casual_tee", "Basic Cotton Tee", "CLOTHES", 0.0, "Simple and comfortable white crewneck."),
        WardrobeItem("cloth_retro_hoodie", "Neon Retro Hoodie", "CLOTHES", 200.0, "Oversized hoodie with vibrant nostalgic accents."),
        WardrobeItem("cloth_street_jacket", "Denim Street Jacket", "CLOTHES", 600.0, "Distressed retro denim with high-fidelity embroidery."),
        WardrobeItem("cloth_bomber", "Cyber Bomber Vest", "CLOTHES", 1200.0, "Heavy-duty techwear bomber jacket."),
        WardrobeItem("cloth_leather", "Matte Leather Jacket", "CLOTHES", 3500.0, "Bespoke heavy black motorcycle leather jacket."),
        WardrobeItem("cloth_suit", "Bespoke Royal Suit", "CLOTHES", 8000.0, "Immaculately tailored silk suit with gold accents."),
        WardrobeItem("cloth_gold_blazer", "24K Gold Blazer", "CLOTHES", 25000.0, "Woven with genuine gold thread, glows under stage lights."),
        WardrobeItem("cloth_space_suit", "Astro Space Suit", "CLOTHES", 75000.0, "Cosmic metallic spacesuit with integrated neon piping."),

        // JEWELRY
        WardrobeItem("jewel_none", "No Accessories", "JEWELRY", 0.0, "Keep it simple and unadorned."),
        WardrobeItem("jewel_silver_chain", "Minimalist Silver Chain", "JEWELRY", 250.0, "Fine sterling silver link necklace."),
        WardrobeItem("jewel_pearls", "Natural Pearl Loop", "JEWELRY", 1200.0, "Hand-selected ocean pearls, a timeless look."),
        WardrobeItem("jewel_gold_medallion", "Golden Record Medallion", "JEWELRY", 4500.0, "Large 18K gold medallion celebrating industry success."),
        WardrobeItem("jewel_grillz", "Diamond Iced Grillz", "JEWELRY", 18000.0, "Fully iced out dental grills crafted from VVS diamonds."),
        WardrobeItem("jewel_crown", "Royal Diamond Crown", "JEWELRY", 150000.0, "The absolute pinnacle of luxury. Sparkling diamonds and gold.")
    )

    val CO_FOUNDER_CANDIDATES = listOf(
        CoFounder("cf_marcus", "Marcus Vance", "Pragmatic", 0f, 80f, 0.0, "Prefers balanced growth and stable revenues."),
        CoFounder("cf_elena", "Elena Rostova", "Creative", 0f, 75f, 0.0, "Focuses purely on high ratings, design, and aesthetics."),
        CoFounder("cf_devon", "Devon Cole", "Aggressive", 0f, 70f, 0.0, "Wants rapid marketing expansions and massive streaming loops."),
        CoFounder("cf_sarah", "Sarah Sterling", "Conservative", 0f, 85f, 0.0, "Prefers high bank balances and minimizing financial risks."),
        CoFounder("cf_harvey", "Harvey Specter Jr.", "Greedy", 0f, 65f, 0.0, "Wants high CEO salary dividends and massive corporate valuations.")
    )

    val DEFAULT_NPCS = listOf(
        NPCEntity("npc_star_1", "Aria Nova", popularity = 85f, influence = 80f, fans = 2500000, bestSongTitle = "Nebula Dream", bestSongRating = 4.8f, bestSongStreams = 50000000L),
        NPCEntity("npc_star_2", "DJ Pulse", popularity = 75f, influence = 82f, fans = 1800000, bestSongTitle = "Neon Beats", bestSongRating = 4.5f, bestSongStreams = 30000000L),
        NPCEntity("npc_star_3", "Shadow Lyricist", popularity = 60f, influence = 55f, fans = 800000, bestSongTitle = "Subterranean Flow", bestSongRating = 4.2f, bestSongStreams = 12000000L),
        NPCEntity("npc_star_4", "Jax Riff", popularity = 68f, influence = 62f, fans = 1100000, bestSongTitle = "Overdrive", bestSongRating = 4.3f, bestSongStreams = 18000000L),
        NPCEntity("npc_star_5", "Luna Soft", popularity = 50f, influence = 58f, fans = 450000, bestSongTitle = "Quiet Oceans", bestSongRating = 4.0f, bestSongStreams = 6000000L),
        NPCEntity("npc_star_6", "Busta Rhyme", popularity = 92f, influence = 90f, fans = 7500000, bestSongTitle = "Mic Dropper", bestSongRating = 4.9f, bestSongStreams = 120000000L),
        NPCEntity("npc_star_7", "Justin Blaze", popularity = 89f, influence = 87f, fans = 4200000, bestSongTitle = "Flickering Sparks", bestSongRating = 4.7f, bestSongStreams = 85000000L),
        NPCEntity("npc_star_8", "Seraphina", popularity = 72f, influence = 70f, fans = 1500000, bestSongTitle = "Velvet Nights", bestSongRating = 4.4f, bestSongStreams = 28000000L),
        NPCEntity("npc_star_9", "Vince", popularity = 80f, influence = 78f, fans = 2200000, bestSongTitle = "Venomous Spit", bestSongRating = 4.6f, bestSongStreams = 45000000L),
        NPCEntity("npc_star_10", "Alabaster Rock", popularity = 65f, influence = 64f, fans = 950000, bestSongTitle = "Granite Heart", bestSongRating = 4.1f, bestSongStreams = 15000000L),
        NPCEntity("npc_star_11", "Chloe Bloom", popularity = 55f, influence = 59f, fans = 500000, bestSongTitle = "Wildflowers", bestSongRating = 4.3f, bestSongStreams = 8000000L),
        NPCEntity("npc_star_12", "Amadeus Neo", popularity = 45f, influence = 40f, fans = 300000, bestSongTitle = "Midnight Espresso", bestSongRating = 4.5f, bestSongStreams = 4000000L),
        NPCEntity("npc_star_13", "Elysian Sky", popularity = 77f, influence = 75f, fans = 1900000, bestSongTitle = "Stargaze Odyssey", bestSongRating = 4.6f, bestSongStreams = 38000000L),
        NPCEntity("npc_star_14", "Cyrus King", popularity = 83f, influence = 85f, fans = 3100000, bestSongTitle = "Regal Touch", bestSongRating = 4.7f, bestSongStreams = 62000000L),
        // Newly added artists/rivals to expand the roster!
        NPCEntity("npc_star_15", "Kanye East", popularity = 88f, influence = 92f, fans = 3800000, bestSongTitle = "Yeezy Season", bestSongRating = 4.7f, bestSongStreams = 72000000L),
        NPCEntity("npc_star_16", "Travis Scottie", popularity = 86f, influence = 89f, fans = 3300000, bestSongTitle = "Rodeo Dust", bestSongRating = 4.6f, bestSongStreams = 64000000L),
        NPCEntity("npc_star_17", "Billie Eyelash", popularity = 84f, influence = 88f, fans = 2900000, bestSongTitle = "Bad Guy Vibes", bestSongRating = 4.8f, bestSongStreams = 58000000L),
        NPCEntity("npc_star_18", "Drake Drake", popularity = 93f, influence = 94f, fans = 8200000, bestSongTitle = "Hotline Call", bestSongRating = 4.5f, bestSongStreams = 135000000L),
        NPCEntity("npc_star_19", "Lady Gogo", popularity = 82f, influence = 85f, fans = 2400000, bestSongTitle = "Poker Smile", bestSongRating = 4.7f, bestSongStreams = 48000000L),
        NPCEntity("npc_star_20", "Post Malone-y", popularity = 87f, influence = 86f, fans = 3500000, bestSongTitle = "Sunflower Fields", bestSongRating = 4.6f, bestSongStreams = 69000000L),
        NPCEntity("npc_star_21", "Dua Lip-sync", popularity = 85f, influence = 84f, fans = 2700000, bestSongTitle = "Levitating Beats", bestSongRating = 4.4f, bestSongStreams = 52000000L),
        NPCEntity("npc_star_22", "Eminem-y", popularity = 91f, influence = 93f, fans = 6800000, bestSongTitle = "Rap Engine", bestSongRating = 4.9f, bestSongStreams = 110000000L),
        NPCEntity("npc_star_23", "Bob Marley-esque", popularity = 70f, influence = 72f, fans = 1200000, bestSongTitle = "Redemption Jam", bestSongRating = 4.9f, bestSongStreams = 22000000L),
        NPCEntity("npc_star_24", "Kid Laroi-esque", popularity = 66f, influence = 65f, fans = 850000, bestSongTitle = "Stay High", bestSongRating = 4.3f, bestSongStreams = 14000000L),
        // 8 Up-and-coming unsigned rookies
        NPCEntity("npc_rookie_ryan", "Ryan Sparks", popularity = 15f, influence = 12f, fans = 5000L, bestSongTitle = "First Sparks", bestSongRating = 3.6f, bestSongStreams = 25000L, genrePreference = "Pop"),
        NPCEntity("npc_rookie_mia", "Mia Flow", popularity = 22f, influence = 18f, fans = 12000L, bestSongTitle = "Concrete Jungle", bestSongRating = 3.8f, bestSongStreams = 45000L, genrePreference = "Hip Hop"),
        NPCEntity("npc_rookie_ian", "Ian Woods", popularity = 18f, influence = 15f, fans = 8000L, bestSongTitle = "Backwoods Acoustic", bestSongRating = 3.9f, bestSongStreams = 32000L, genrePreference = "Indie"),
        NPCEntity("npc_rookie_chloe", "Chloe Retro", popularity = 25f, influence = 20f, fans = 15000L, bestSongTitle = "Pixelated Love", bestSongRating = 4.1f, bestSongStreams = 60000L, genrePreference = "Electronic"),
        NPCEntity("npc_rookie_sam", "Sam Rivers", popularity = 20f, influence = 16f, fans = 9500L, bestSongTitle = "Rainy Cafe", bestSongRating = 4.0f, bestSongStreams = 40000L, genrePreference = "R&B"),
        NPCEntity("npc_rookie_matt", "Matt Thorne", popularity = 12f, influence = 10f, fans = 3500L, bestSongTitle = "Shattered Amp", bestSongRating = 3.5f, bestSongStreams = 18000L, genrePreference = "Rock"),
        NPCEntity("npc_rookie_abby", "Abby Lane", popularity = 16f, influence = 14f, fans = 6000L, bestSongTitle = "Dusty Boots", bestSongRating = 3.7f, bestSongStreams = 28000L, genrePreference = "Country"),
        NPCEntity("npc_rookie_nova", "Nova Vibe", popularity = 10f, influence = 8f, fans = 2500L, bestSongTitle = "Midnight Saxophone", bestSongRating = 3.4f, bestSongStreams = 10000L, genrePreference = "Jazz")
    )

        val LUXURY_ITEMS = listOf(
        LuxuryItem(
            id = "lux_rolex",
            name = "Rolex Oyster Perpetual Gold",
            description = "+20 Public Image • +15 Prestige",
            cost = 35000.0,
            perks = "+20 Public Image, +15 Prestige",
            unlockRequirement = ""
        ),
        LuxuryItem(
            id = "lux_cuban",
            name = "18k Solid Diamond Cuban Chain",
            description = "+18 Public Image • +12 Prestige",
            cost = 28000.0,
            perks = "+18 Public Image, +12 Prestige",
            unlockRequirement = ""
        ),
        LuxuryItem(
            id = "lux_tailor",
            name = "Italian Bespoke Tailored Wardrobe",
            description = "+15 Public Image • +10 Prestige",
            cost = 12000.0,
            perks = "+15 Public Image, +10 Prestige",
            unlockRequirement = ""
        ),
        LuxuryItem(
            id = "luxury_vocal_coach",
            name = "Premium Vocal Coach & Artist Retreat",
            description = "Fly out to an exclusive mountain cabin with a world-class vocal specialist to train your breath control and tone.",
            cost = 30000.0,
            perks = "+15% Vocal Skill, -40% Stress",
            unlockRequirement = "Unlocks at $10,000 cash"
        ),
        LuxuryItem(
            id = "luxury_writing_camp",
            name = "Grammy-winning Songwriting Camp",
            description = "Attend a private studio retreat in Malibu collaborating with award-winning songwriters and composers.",
            cost = 75000.0,
            perks = "+20% Writing Skill",
            unlockRequirement = "Unlocks at $25,000 cash"
        ),
        LuxuryItem(
            id = "luxury_pr_defense",
            name = "VIP Public Relations Defense Campaign",
            description = "Hire a high-end corporate PR team to sanitize your internet presence, counter negative tabloids, and clean your image.",
            cost = 120000.0,
            perks = "+45% Public Image, -30% Negative Sentiment",
            unlockRequirement = "Unlocks at $50,000 cash"
        ),
        LuxuryItem(
            id = "luxury_media_syndicate",
            name = "Media Network Syndicate Shares",
            description = "Buy a minority stake in prominent radio stations and streaming playlist conglomerates to secure favorable exposure.",
            cost = 500000.0,
            perks = "+25% Industry Respect, permanently boosts monthly listener growth",
            unlockRequirement = "Unlocks at $150,000 cash"
        ),
        LuxuryItem(
            id = "luxury_wellness_bio",
            name = "Private Wellness Clinic & Biohacking Chamber",
            description = "Install an advanced cryotherapy and oxygen therapy unit in your home. Complete health recovery system.",
            cost = 1200000.0,
            perks = "Permanently boost Max Energy by +25, clear all Burnout instantly",
            unlockRequirement = "Unlocks at $500,000 cash"
        ),
        LuxuryItem(
            id = "luxury_superyacht",
            name = "Bespoke Superyacht & Private Jetty",
            description = "A massive 150-foot customized luxury vessel anchored in Manhattan. The absolute peak of high-society elite prestige.",
            cost = 4500000.0,
            perks = "+25% Popularity, -25% Humility (Hubris boost)",
            unlockRequirement = "Unlocks at $1,500,000 cash"
        ),
        LuxuryItem(
            id = "luxury_festival_owner",
            name = "Global Music Festival Co-Ownership",
            description = "Purchase a 20% co-owner stake in the world's largest recurring electronic and hip-hop music festival.",
            cost = 12000000.0,
            perks = "+$85,000 weekly salary, +40 Industry Respect",
            unlockRequirement = "Unlocks at $4,000,000 cash"
        ),
        LuxuryItem(
            id = "luxury_security_detail",
            name = "Elite Personal Security Detail & Cyber Defense",
            description = "Recruit former intelligence officers and cyber specialists to handle protective security and proactively squash negative PR crises.",
            cost = 800000.0,
            perks = "-80% negative PR scandal impact permanently",
            unlockRequirement = "Unlocks at $250,000 cash"
        ),
        LuxuryItem(
            id = "luxury_web3_metaverse",
            name = "Web3 & NFT Virtual Reality Studio",
            description = "Launch your own decentralized virtual reality concert studio and issue fan-utility tokens.",
            cost = 2500000.0,
            perks = "+20% Influence, +$18,000 weekly metaverse dividend payout",
            unlockRequirement = "Unlocks at $750,000 cash"
        ),
        LuxuryItem(
            id = "luxury_catalog_trust",
            name = "Music Catalog Preservation Trust",
            description = "Establish a high-profile legal trust of master-rights attorneys to double your publishing royalties from older music releases.",
            cost = 8000000.0,
            perks = "Double publishing mechanical collections permanently",
            unlockRequirement = "Unlocks at $3,000,000 cash"
        ),
        LuxuryItem(
            id = "luxury_charity_foundation",
            name = "Charitable Arts & Education Foundation",
            description = "Endow a philanthropic global foundation to support music education and creative spaces in underprivileged communities.",
            cost = 15000000.0,
            perks = "+60 Industry Respect, +50 Public Image, permanent charity-believer conversion",
            unlockRequirement = "Unlocks at $5,000,000 cash"
        ),
        LuxuryItem(
            id = "luxury_private_jet",
            name = "Gulfstream G800 Elite Private Jet",
            description = "Acquire a long-range ultra-luxury private jet to travel globally without stress. The ultimate symbol of music industry royalty.",
            cost = 35000000.0,
            perks = "+50 Popularity, weekly stress decays 30% faster permanently",
            unlockRequirement = "Unlocks at $10,000,000 cash"
        ),
        LuxuryItem(
            id = "luxury_netflix_biopic",
            name = "Sundance & Netflix Cinematic Biopic",
            description = "Produce and star in a cinematic documentary detailing your struggle, rise, and creative genius in the music industry.",
            cost = 25000000.0,
            perks = "+1,000,000 fans instantly, 1.5x streaming hype for all future songs",
            unlockRequirement = "Unlocks at $8,000,000 cash"
        )
    )

    val AD_AGENCIES = listOf(
        AdAgency("ag1", "Pulse Media", "Social Media", 500000L, 0.08f, 3000.0, 85f),
        AdAgency("ag2", "RadioWave Pro", "Radio", 1200000L, 0.04f, 5000.0, 90f),
        AdAgency("ag3", "StreamBoost", "Streaming", 800000L, 0.11f, 4000.0, 75f),
        AdAgency("ag4", "UrbanBoards", "Billboards", 2000000L, 0.03f, 8000.0, 95f),
        AdAgency("ag5", "TeleAds", "TV", 5000000L, 0.02f, 15000.0, 88f),
        AdAgency("ag6", "GrassrootsConnect", "Street", 150000L, 0.15f, 1000.0, 70f)
    )

    val BASE_STUDIO_SESSION_COST = 2000.0
    val BASE_CAR_PRICES = mapOf("car_sedan" to 5000.0, "car_suv" to 15000.0, "car_sports" to 60000.0, "car_bus" to 250000.0)
    val BASE_HOUSE_PRICES = mapOf("house_studio" to 20000.0, "house_townhouse" to 80000.0, "house_loft" to 200000.0, "house_mansion" to 1000000.0)
    val BASE_INSTRUMENT_PRICES = mapOf("inst_acoustic" to 500.0, "inst_midi" to 1500.0, "inst_synth" to 5000.0, "inst_vintage" to 15000.0)

    val ROLES_LIST = listOf(
        "Crypto Analyst",
        "Accountant",
        "Financial Advisor",
        "Personal Assistant",
        "Social Media Manager",
        "PR Manager",
        "Talent Agent",
        "Business Manager",
        "Lawyer",
        "Music Producer",
        "Tour Manager",
        "Security",
        "Vocal Coach / Sound Engineer"
    )

    fun generateRealisticCandidateRoster(): List<Employee> = listOf(
        // 1. CRYPTO ANALYSTS ($1,100 - $45,000/wk) - 80%+ Rating commands executive salaries, Stubborn Trait & 70/30 Predictions
        Employee(id = "cry_appr", name = "Toby Sparks", role = "Crypto Analyst", weeklySalary = 1100.0, experience = 30, reliability = 60f, honesty = 75f, greed = 55f, intelligence = 55f, leadership = 30f, rating = 2.4f, investorStyle = "Daily Trader", traitDescription = "Crypto Apprentice · Scours online communities for micro-cap tokens"),
        Employee(id = "cry_jr", name = "Leo 'Hodl' Vance", role = "Crypto Analyst", weeklySalary = 2400.0, experience = 45, reliability = 68f, honesty = 80f, greed = 45f, intelligence = 65f, leadership = 38f, rating = 3.0f, investorStyle = "Daily Trader", traitDescription = "Junior On-Chain Analyst · Monitors token volume and short-term volatility"),
        Employee(id = "cry_mid", name = "Zooko Wilcox", role = "Crypto Analyst", weeklySalary = 5200.0, experience = 68, reliability = 80f, honesty = 85f, greed = 30f, intelligence = 78f, leadership = 52f, rating = 3.9f, investorStyle = "Balanced", traitDescription = "Mid-Level Token Analyst · Balanced liquidity and protocol risk"),
        Employee(id = "cry_sr", name = "Maya Lin-Diem", role = "Crypto Analyst", weeklySalary = 18500.0, experience = 84, reliability = 88f, honesty = 90f, greed = 25f, intelligence = 88f, leadership = 68f, rating = 4.4f, investorStyle = "Balanced", traitDescription = "Senior DeFi Strategist · ★ Stubborn Market Vision (80%+) · 70/30 Directional Predictions"),
        Employee(id = "cry_lead", name = "Vitalik Nakamoto", role = "Crypto Analyst", weeklySalary = 29000.0, experience = 92, reliability = 95f, honesty = 95f, greed = 20f, intelligence = 94f, leadership = 80f, rating = 4.8f, investorStyle = "Long-Term", traitDescription = "Chief Blockchain Economist · ★ Stubborn Trait (80%+) · Dislocated Value Opportunist"),
        Employee(id = "cry_elite", name = "Satoshi Finney", role = "Crypto Analyst", weeklySalary = 45000.0, experience = 99, reliability = 99f, honesty = 96f, greed = 12f, intelligence = 99f, leadership = 95f, rating = 5.0f, investorStyle = "Long-Term", traitDescription = "Master Quantitative Crypto Architect · ★ Elite Stubborn Trait (80%+) · Deep Value Allocator"),

        // 2. ACCOUNTANTS ($1,200 - $48,000/wk) - 80%+ Rating commands executive salaries, Stubborn Trait & 70/30 Predictions
        Employee(id = "acct_appr", name = "Gary Higgins", role = "Accountant", weeklySalary = 1200.0, experience = 32, reliability = 62f, honesty = 88f, greed = 20f, intelligence = 58f, leadership = 30f, rating = 2.5f, investorStyle = "Daily Trader", traitDescription = "Junior Bookkeeper · Reconciles gig receipts and daily expense tallies"),
        Employee(id = "acct_jr", name = "Arthur Pendelton", role = "Accountant", weeklySalary = 2500.0, experience = 48, reliability = 70f, honesty = 90f, greed = 22f, intelligence = 66f, leadership = 38f, rating = 3.1f, investorStyle = "Daily Trader", traitDescription = "Staff Accountant · General ledger management and invoice processing"),
        Employee(id = "acct_mid", name = "Franklin Sterns", role = "Accountant", weeklySalary = 5400.0, experience = 70, reliability = 82f, honesty = 88f, greed = 28f, intelligence = 80f, leadership = 55f, rating = 3.9f, investorStyle = "Balanced", traitDescription = "Certified Public Accountant · Quarterly tax estimates and operational audits"),
        Employee(id = "acct_sr", name = "Amelia Sterling", role = "Accountant", weeklySalary = 19500.0, experience = 85, reliability = 90f, honesty = 94f, greed = 18f, intelligence = 88f, leadership = 70f, rating = 4.5f, investorStyle = "Balanced", traitDescription = "Senior Corporate Comptroller · ★ Stubborn Market Vision (80%+) · 70/30 Equity Forecasts"),
        Employee(id = "acct_lead", name = "Diane Fox-Caldwell", role = "Accountant", weeklySalary = 31000.0, experience = 93, reliability = 96f, honesty = 96f, greed = 15f, intelligence = 95f, leadership = 82f, rating = 4.8f, investorStyle = "Long-Term", traitDescription = "Head of Entertainment Tax · ★ Stubborn Trait (80%+) · Opportunistic Equity Deployment"),
        Employee(id = "acct_elite", name = "Raymond T. Vance", role = "Accountant", weeklySalary = 48000.0, experience = 99, reliability = 99f, honesty = 98f, greed = 10f, intelligence = 98f, leadership = 92f, rating = 5.0f, investorStyle = "Long-Term", traitDescription = "Elite Forensic Tax Partner · ★ Elite Stubborn Trait (80%+) · Deep Margin of Safety Allocator"),

        // 3. FINANCIAL ADVISORS ($1,400 - $24,000/wk)
        Employee(id = "fin_appr", name = "Penny Albright", role = "Financial Advisor", weeklySalary = 1400.0, experience = 30, reliability = 64f, honesty = 84f, greed = 35f, intelligence = 58f, leadership = 32f, rating = 2.5f, traitDescription = "Financial Planning Intern · High-yield savings and index fund basics"),
        Employee(id = "fin_jr", name = "Clara Oswald", role = "Financial Advisor", weeklySalary = 2800.0, experience = 50, reliability = 72f, honesty = 85f, greed = 30f, intelligence = 68f, leadership = 40f, rating = 3.2f, traitDescription = "Associate Wealth Planner · Conservative portfolio risk profiling"),
        Employee(id = "fin_mid", name = "Marcus Brody", role = "Financial Advisor", weeklySalary = 5800.0, experience = 72, reliability = 82f, honesty = 82f, greed = 38f, intelligence = 82f, leadership = 58f, rating = 4.0f, traitDescription = "Chartered Financial Analyst · Growth equity and commercial bond balancing"),
        Employee(id = "fin_sr", name = "Julian Vance-Moreau", role = "Financial Advisor", weeklySalary = 9800.0, experience = 85, reliability = 90f, honesty = 88f, greed = 30f, intelligence = 89f, leadership = 70f, rating = 4.5f, traitDescription = "Senior Wealth Strategist · Private equity and music catalog syndication"),
        Employee(id = "fin_lead", name = "Devon Vance", role = "Financial Advisor", weeklySalary = 15000.0, experience = 92, reliability = 95f, honesty = 92f, greed = 25f, intelligence = 94f, leadership = 80f, rating = 4.8f, traitDescription = "Executive Family Office Director · Generational trusts and real estate yields"),
        Employee(id = "fin_elite", name = "Sir Roderick Sterling", role = "Financial Advisor", weeklySalary = 24000.0, experience = 99, reliability = 99f, honesty = 95f, greed = 15f, intelligence = 99f, leadership = 94f, rating = 5.0f, traitDescription = "Titan Private Wealth Partner · Wall Street insider with sovereign fund networks"),

        // 4. PERSONAL ASSISTANTS ($850 - $11,500/wk)
        Employee(id = "pa_appr", name = "Toby Higgins", role = "Personal Assistant", weeklySalary = 850.0, experience = 28, reliability = 60f, honesty = 85f, greed = 20f, intelligence = 55f, leadership = 25f, rating = 2.3f, traitDescription = "Intern Assistant · Runs errands, coffee runs, and basic schedule notes"),
        Employee(id = "pa_jr", name = "Wendy Darling", role = "Personal Assistant", weeklySalary = 1600.0, experience = 46, reliability = 72f, honesty = 88f, greed = 20f, intelligence = 65f, leadership = 38f, rating = 3.0f, traitDescription = "Personal Concierge · Organizes daily itineraries and studio calendars"),
        Employee(id = "pa_mid", name = "Reggie 'Smooth' Moore", role = "Personal Assistant", weeklySalary = 3400.0, experience = 70, reliability = 84f, honesty = 90f, greed = 20f, intelligence = 78f, leadership = 55f, rating = 4.0f, traitDescription = "Executive Road Assistant · Backstage catering and hotel coordination"),
        Employee(id = "pa_sr", name = "Simon Croft", role = "Personal Assistant", weeklySalary = 5800.0, experience = 84, reliability = 92f, honesty = 94f, greed = 15f, intelligence = 86f, leadership = 68f, rating = 4.5f, traitDescription = "Senior Celebrity Chief of Staff · Keeps artist stress low and operations calm"),
        Employee(id = "pa_lead", name = "Gwendolyn Croft", role = "Personal Assistant", weeklySalary = 8200.0, experience = 92, reliability = 96f, honesty = 96f, greed = 12f, intelligence = 92f, leadership = 78f, rating = 4.8f, traitDescription = "Master Celebrity Gatekeeper · Eliminates distractions and guards mental health"),
        Employee(id = "pa_elite", name = "Archibald Thorne", role = "Personal Assistant", weeklySalary = 11500.0, experience = 99, reliability = 99f, honesty = 99f, greed = 8f, intelligence = 96f, leadership = 90f, rating = 5.0f, traitDescription = "Legendary Royal Butler & Aide · Flawless lifestyle and wellness synchronization"),

        // 5. SOCIAL MEDIA MANAGERS ($950 - $14,500/wk)
        Employee(id = "smm_appr", name = "Jaxson Byte", role = "Social Media Manager", weeklySalary = 950.0, experience = 30, reliability = 60f, honesty = 78f, greed = 35f, intelligence = 58f, leadership = 28f, rating = 2.4f, traitDescription = "TikTok Intern · Reposts snippets and monitors trending music tags"),
        Employee(id = "smm_jr", name = "Zoe Trends", role = "Social Media Manager", weeklySalary = 1900.0, experience = 48, reliability = 70f, honesty = 82f, greed = 30f, intelligence = 68f, leadership = 38f, rating = 3.1f, traitDescription = "Content Creator · Crafts catchy short-form video captions and memes"),
        Employee(id = "smm_mid", name = "Kai Rivera", role = "Social Media Manager", weeklySalary = 4200.0, experience = 70, reliability = 82f, honesty = 85f, greed = 28f, intelligence = 80f, leadership = 55f, rating = 4.0f, traitDescription = "Digital Growth Strategist · Optimizes algorithm reach and fan engagement"),
        Employee(id = "smm_sr", name = "Sora Takahashi", role = "Social Media Manager", weeklySalary = 7200.0, experience = 85, reliability = 90f, honesty = 90f, greed = 22f, intelligence = 88f, leadership = 68f, rating = 4.5f, traitDescription = "Senior Viral Architect · Consistent trending topics and organic fan conversion"),
        Employee(id = "smm_lead", name = "Harper Vance", role = "Social Media Manager", weeklySalary = 10500.0, experience = 92, reliability = 95f, honesty = 92f, greed = 18f, intelligence = 94f, leadership = 80f, rating = 4.8f, traitDescription = "Global Campaign Director · Multi-platform influencer synergy and brand deals"),
        Employee(id = "smm_elite", name = "Aria St. Cloud", role = "Social Media Manager", weeklySalary = 14500.0, experience = 99, reliability = 99f, honesty = 94f, greed = 15f, intelligence = 98f, leadership = 92f, rating = 5.0f, traitDescription = "Elite Pop Culture Alchemist · Guarantees viral dominance and brand prestige"),

        // 6. PR MANAGERS ($1,500 - $22,500/wk)
        Employee(id = "pr_appr", name = "Liam Press", role = "PR Manager", weeklySalary = 1500.0, experience = 32, reliability = 64f, honesty = 80f, greed = 30f, intelligence = 60f, leadership = 32f, rating = 2.5f, traitDescription = "Junior Publicist Assistant · Drafts standard press releases and media kits"),
        Employee(id = "pr_jr", name = "Fiona Gallagher", role = "PR Manager", weeklySalary = 2900.0, experience = 50, reliability = 72f, honesty = 82f, greed = 28f, intelligence = 70f, leadership = 40f, rating = 3.2f, traitDescription = "Music Publicist · Pitches independent music blogs and regional radio"),
        Employee(id = "pr_mid", name = "Elena Rostova", role = "PR Manager", weeklySalary = 5900.0, experience = 72, reliability = 84f, honesty = 86f, greed = 30f, intelligence = 82f, leadership = 60f, rating = 4.0f, traitDescription = "Crisis & Media Publicist · Swift narrative shielding and magazine interviews"),
        Employee(id = "pr_sr", name = "Julian Ross", role = "PR Manager", weeklySalary = 9800.0, experience = 85, reliability = 90f, honesty = 90f, greed = 24f, intelligence = 89f, leadership = 72f, rating = 4.5f, traitDescription = "Senior Entertainment Publicist · High-tier press features and talk show bookings"),
        Employee(id = "pr_lead", name = "Samantha Hayes", role = "PR Manager", weeklySalary = 15200.0, experience = 93, reliability = 96f, honesty = 92f, greed = 18f, intelligence = 95f, leadership = 82f, rating = 4.8f, traitDescription = "Strategic Media Executive · Flawless damage control and pristine public image"),
        Employee(id = "pr_elite", name = "Dominic Blackwood", role = "PR Manager", weeklySalary = 22500.0, experience = 99, reliability = 99f, honesty = 95f, greed = 12f, intelligence = 99f, leadership = 94f, rating = 5.0f, traitDescription = "Legendary Spin Master · Commands national headlines and elite red carpet prestige"),

        // 7. TALENT AGENTS ($1,600 - $26,000/wk)
        Employee(id = "agt_appr", name = "Brett Cooper", role = "Talent Agent", weeklySalary = 1600.0, experience = 32, reliability = 62f, honesty = 78f, greed = 45f, intelligence = 60f, leadership = 35f, rating = 2.5f, traitDescription = "Agency Mailroom Graduate · Books local showcases and college events"),
        Employee(id = "agt_jr", name = "Gavin Belson", role = "Talent Agent", weeklySalary = 3200.0, experience = 50, reliability = 70f, honesty = 76f, greed = 42f, intelligence = 70f, leadership = 45f, rating = 3.2f, traitDescription = "Junior Booking Agent · Club dates and regional festival side-stages"),
        Employee(id = "agt_mid", name = "Rachel Zane", role = "Talent Agent", weeklySalary = 6500.0, experience = 72, reliability = 84f, honesty = 82f, greed = 35f, intelligence = 82f, leadership = 62f, rating = 4.0f, traitDescription = "Commercial Talent Agent · Corporate events and mid-tier brand deals"),
        Employee(id = "agt_sr", name = "Ari Goldfield", role = "Talent Agent", weeklySalary = 11200.0, experience = 86, reliability = 90f, honesty = 80f, greed = 32f, intelligence = 90f, leadership = 76f, rating = 4.5f, traitDescription = "Senior Industry Agent · Lucrative brand endorsements and high-guarantee tours"),
        Employee(id = "agt_lead", name = "Harvey Spector", role = "Talent Agent", weeklySalary = 17500.0, experience = 93, reliability = 95f, honesty = 85f, greed = 25f, intelligence = 95f, leadership = 85f, rating = 4.8f, traitDescription = "Power Agency Partner · Major movie placements and global arena tour packages"),
        Employee(id = "agt_elite", name = "Victoria Sterling-Fox", role = "Talent Agent", weeklySalary = 26000.0, experience = 99, reliability = 98f, honesty = 90f, greed = 18f, intelligence = 98f, leadership = 95f, rating = 5.0f, traitDescription = "Legendary Hollywood Mogul · Direct access to Fortune 500 CEOs and A-list royalty"),

        // 8. BUSINESS MANAGERS ($1,800 - $28,500/wk)
        Employee(id = "biz_appr", name = "Miles Dunphy", role = "Business Manager", weeklySalary = 1800.0, experience = 34, reliability = 64f, honesty = 85f, greed = 30f, intelligence = 62f, leadership = 36f, rating = 2.6f, traitDescription = "Business Operations Trainee · Monitors vendor contracts and basic budgets"),
        Employee(id = "biz_jr", name = "Carlton Banks", role = "Business Manager", weeklySalary = 3500.0, experience = 52, reliability = 74f, honesty = 88f, greed = 28f, intelligence = 72f, leadership = 48f, rating = 3.3f, traitDescription = "Associate Business Manager · Enforces cost discipline and production quotes"),
        Employee(id = "biz_mid", name = "Claire Underwood", role = "Business Manager", weeklySalary = 7200.0, experience = 74, reliability = 86f, honesty = 84f, greed = 32f, intelligence = 84f, leadership = 68f, rating = 4.1f, traitDescription = "Corporate Business Strategist · Negotiates discounts across vendor services"),
        Employee(id = "biz_sr", name = "Gordon Gekko", role = "Business Manager", weeklySalary = 12500.0, experience = 87, reliability = 90f, honesty = 82f, greed = 30f, intelligence = 91f, leadership = 80f, rating = 4.6f, traitDescription = "Senior Commercial Operator · Relentless cost slashing and contract renegotiation"),
        Employee(id = "biz_lead", name = "Alexander Mercer", role = "Business Manager", weeklySalary = 19000.0, experience = 94, reliability = 96f, honesty = 90f, greed = 22f, intelligence = 96f, leadership = 88f, rating = 4.8f, traitDescription = "Executive Business Director · Slashes enterprise overhead and doubles net profit"),
        Employee(id = "biz_elite", name = "Logan Sterling-Roy", role = "Business Manager", weeklySalary = 28500.0, experience = 99, reliability = 99f, honesty = 92f, greed = 15f, intelligence = 99f, leadership = 96f, rating = 5.0f, traitDescription = "Empire Business Titan · Scales catalogs into global multi-million conglomerates"),

        // 9. LAWYERS ($2,000 - $32,000/wk)
        Employee(id = "law_appr", name = "Todd Alquist", role = "Lawyer", weeklySalary = 2000.0, experience = 34, reliability = 66f, honesty = 88f, greed = 32f, intelligence = 65f, leadership = 35f, rating = 2.6f, traitDescription = "Junior Associate · Reviews standard non-disclosure agreements and release forms"),
        Employee(id = "law_jr", name = "Kim Wexler", role = "Lawyer", weeklySalary = 3800.0, experience = 52, reliability = 78f, honesty = 92f, greed = 22f, intelligence = 75f, leadership = 48f, rating = 3.3f, traitDescription = "Music & Copyright Attorney · Protects publishing splits and detects unfair clauses"),
        Employee(id = "law_mid", name = "Saul Goodman", role = "Lawyer", weeklySalary = 7500.0, experience = 75, reliability = 85f, honesty = 78f, greed = 45f, intelligence = 86f, leadership = 66f, rating = 4.2f, traitDescription = "Tenacious Defense Attorney · Loophole master; resolves copyright and licensing threats"),
        Employee(id = "law_sr", name = "Chuck McGill", role = "Lawyer", weeklySalary = 13500.0, experience = 88, reliability = 95f, honesty = 96f, greed = 18f, intelligence = 93f, leadership = 78f, rating = 4.6f, traitDescription = "Senior Entertainment Partner · Unmatched legal precedent and contract defenses"),
        Employee(id = "law_lead", name = "Jessica Pearson", role = "Lawyer", weeklySalary = 21000.0, experience = 95, reliability = 97f, honesty = 94f, greed = 16f, intelligence = 97f, leadership = 88f, rating = 4.9f, traitDescription = "Managing Law Partner · Neutralizes multi-million dollar lawsuits with sovereign poise"),
        Employee(id = "law_elite", name = "Sir Humphrey Appleby", role = "Lawyer", weeklySalary = 32000.0, experience = 99, reliability = 99f, honesty = 96f, greed = 12f, intelligence = 99f, leadership = 96f, rating = 5.0f, traitDescription = "Supreme Entertainment Counsel · Crown royalty legal immunity; writes the industry standards"),

        // 10. MUSIC PRODUCERS ($1,500 - $25,000/wk)
        Employee(id = "prd_appr", name = "Benny Beatmaker", role = "Music Producer", weeklySalary = 1500.0, experience = 32, reliability = 64f, honesty = 86f, greed = 35f, intelligence = 60f, leadership = 32f, rating = 2.5f, traitDescription = "Home Studio Beatmaker · Fresh drum patterns and sample chops"),
        Employee(id = "prd_jr", name = "Marcus Wave", role = "Music Producer", weeklySalary = 3100.0, experience = 50, reliability = 74f, honesty = 88f, greed = 28f, intelligence = 70f, leadership = 42f, rating = 3.2f, traitDescription = "Session Producer · Solid mix balance, vocal comping, and bass synthesis"),
        Employee(id = "prd_mid", name = "Metro Vance", role = "Music Producer", weeklySalary = 6600.0, experience = 73, reliability = 86f, honesty = 88f, greed = 32f, intelligence = 84f, leadership = 62f, rating = 4.1f, traitDescription = "Chart-Topping Beat Architect · Signature hard-hitting 808s and radio hook ear"),
        Employee(id = "prd_sr", name = "Max 'Melody' Martin", role = "Music Producer", weeklySalary = 11800.0, experience = 87, reliability = 92f, honesty = 92f, greed = 24f, intelligence = 92f, leadership = 75f, rating = 4.5f, traitDescription = "Master Pop Architect · Guarantees irresistible songwriting flow and earworm hooks"),
        Employee(id = "prd_lead", name = "Pharrell Sound", role = "Music Producer", weeklySalary = 17500.0, experience = 94, reliability = 96f, honesty = 95f, greed = 18f, intelligence = 96f, leadership = 84f, rating = 4.8f, traitDescription = "Grammy-Winning Executive Producer · Innovative sonic textures that redefine genres"),
        Employee(id = "prd_elite", name = "Quincy Jones Jr.", role = "Music Producer", weeklySalary = 25000.0, experience = 99, reliability = 99f, honesty = 98f, greed = 10f, intelligence = 99f, leadership = 95f, rating = 5.0f, traitDescription = "Legendary Maestro Producer · Orchestral acoustic perfection and historic diamond albums"),

        // 11. TOUR MANAGERS ($1,400 - $22,000/wk)
        Employee(id = "tour_appr", name = "Mick Shifter", role = "Tour Manager", weeklySalary = 1400.0, experience = 30, reliability = 62f, honesty = 82f, greed = 30f, intelligence = 58f, leadership = 35f, rating = 2.4f, traitDescription = "Assistant Roadie · Loads gear, manages cable checks, and drives tour vans"),
        Employee(id = "tour_jr", name = "Dusty Rhodes", role = "Tour Manager", weeklySalary = 2800.0, experience = 50, reliability = 72f, honesty = 84f, greed = 26f, intelligence = 68f, leadership = 46f, rating = 3.2f, traitDescription = "Road Manager · Coordinates hotel check-ins and venue soundcheck schedules"),
        Employee(id = "tour_mid", name = "Axel Stone", role = "Tour Manager", weeklySalary = 6000.0, experience = 73, reliability = 86f, honesty = 88f, greed = 28f, intelligence = 82f, leadership = 68f, rating = 4.1f, traitDescription = "Production Tour Director · Prevents stage delays and optimizes route logistics"),
        Employee(id = "tour_sr", name = "Ronnie Van Zandt", role = "Tour Manager", weeklySalary = 10200.0, experience = 86, reliability = 92f, honesty = 90f, greed = 22f, intelligence = 89f, leadership = 78f, rating = 4.5f, traitDescription = "Senior Arena Tour Boss · Keeps massive crews on-time and eliminates travel burnout"),
        Employee(id = "tour_lead", name = "Bruce 'Road' Miller", role = "Tour Manager", weeklySalary = 15800.0, experience = 93, reliability = 96f, honesty = 94f, greed = 16f, intelligence = 94f, leadership = 86f, rating = 4.8f, traitDescription = "Global Stadium Tour Director · Flawless pyrotechnics, international freight, and sellouts"),
        Employee(id = "tour_elite", name = "Gunnar 'Valkyrie' Thorne", role = "Tour Manager", weeklySalary = 22000.0, experience = 99, reliability = 99f, honesty = 97f, greed = 12f, intelligence = 98f, leadership = 95f, rating = 5.0f, traitDescription = "Legendary World Tour Commander · Military precision; manages stadium tours with zero hitches"),

        // 12. SECURITY ($1,200 - $18,500/wk)
        Employee(id = "sec_appr", name = "Tiny Kowalski", role = "Security", weeklySalary = 1200.0, experience = 30, reliability = 64f, honesty = 84f, greed = 25f, intelligence = 55f, leadership = 32f, rating = 2.4f, traitDescription = "Club Bouncer · Basic backstage door watch and guest list enforcement"),
        Employee(id = "sec_jr", name = "Buster Briggs", role = "Security", weeklySalary = 2400.0, experience = 50, reliability = 74f, honesty = 88f, greed = 22f, intelligence = 65f, leadership = 42f, rating = 3.1f, traitDescription = "Close Protection Officer · Escorts through crowded backstage corridors"),
        Employee(id = "sec_mid", name = "Victor 'Tank' Stone", role = "Security", weeklySalary = 5200.0, experience = 72, reliability = 86f, honesty = 90f, greed = 24f, intelligence = 78f, leadership = 62f, rating = 4.0f, traitDescription = "Veteran Bodyguard · Neutralizes stalkers and erratic fan rushes instantly"),
        Employee(id = "sec_sr", name = "Marcus 'Ironclad' Kane", role = "Security", weeklySalary = 8800.0, experience = 85, reliability = 92f, honesty = 94f, greed = 18f, intelligence = 86f, leadership = 74f, rating = 4.5f, traitDescription = "Senior Executive Bodyguard · Advanced tactical convoy and route hazard sweeps"),
        Employee(id = "sec_lead", name = "Darius Vance", role = "Security", weeklySalary = 13200.0, experience = 92, reliability = 96f, honesty = 96f, greed = 14f, intelligence = 92f, leadership = 84f, rating = 4.8f, traitDescription = "Director of Dignitary Protection · Armored convoy protocols and perimeter counter-surveillance"),
        Employee(id = "sec_elite", name = "Sergeant Major Nikolai Steele", role = "Security", weeklySalary = 18500.0, experience = 99, reliability = 99f, honesty = 98f, greed = 10f, intelligence = 97f, leadership = 95f, rating = 5.0f, traitDescription = "Ex-Tier 1 Special Forces Commander · Total physical and operational invulnerability"),

        // 13. VOCAL COACH & SOUND ENGINEERS ($1,100 - $18,500/wk)
        Employee(id = "voc_appr", name = "Penny Scales", role = "Vocal Coach / Sound Engineer", weeklySalary = 1100.0, experience = 30, reliability = 64f, honesty = 86f, greed = 25f, intelligence = 58f, leadership = 30f, rating = 2.4f, traitDescription = "Vocal Intern · Guides basic breathing and dynamic scales"),
        Employee(id = "voc_jr", name = "Chloe Harmonics", role = "Vocal Coach / Sound Engineer", weeklySalary = 2200.0, experience = 48, reliability = 74f, honesty = 88f, greed = 24f, intelligence = 68f, leadership = 40f, rating = 3.1f, traitDescription = "Vocal Warmup Coach · Prevents vocal strain and builds diaphragm stamina"),
        Employee(id = "voc_mid", name = "Darren Pitch", role = "Vocal Coach / Sound Engineer", weeklySalary = 4800.0, experience = 72, reliability = 86f, honesty = 86f, greed = 28f, intelligence = 80f, leadership = 58f, rating = 4.0f, traitDescription = "Studio Sound Engineer · Pristine pitch correction, microphone acoustics, and vocal clarity"),
        Employee(id = "voc_sr", name = "Dr. Julian Aria", role = "Vocal Coach / Sound Engineer", weeklySalary = 8800.0, experience = 86, reliability = 92f, honesty = 92f, greed = 22f, intelligence = 89f, leadership = 72f, rating = 4.5f, traitDescription = "Master Vocal Producer · Expands dynamic vocal range, belting power, and acoustic projection"),
        Employee(id = "voc_lead", name = "Madame Genevieve Dupont", role = "Vocal Coach / Sound Engineer", weeklySalary = 13500.0, experience = 93, reliability = 96f, honesty = 95f, greed = 16f, intelligence = 95f, leadership = 82f, rating = 4.8f, traitDescription = "Conservatory Vocal Director · Eliminates vocal fatigue and polishes signature timbres"),
        Employee(id = "voc_elite", name = "Lucia Bellini", role = "Vocal Coach / Sound Engineer", weeklySalary = 18500.0, experience = 99, reliability = 99f, honesty = 98f, greed = 10f, intelligence = 98f, leadership = 92f, rating = 5.0f, traitDescription = "Legendary Operatic Vocal Maestro · Unlocks 5-octave perfection and world-class vocal mastery")
    )

    val INITIAL_CAST_POSTS = listOf(
        CastPost(
            id = "init_cast_1",
            authorName = "Ava Maxx",
            authorHandle = "@avamaxx",
            content = "Every collab I'm doing this year has been intentional. No throwaway features. Everything means something. 💽✨",
            likes = 14200,
            reposts = 3410,
            commentsCount = 890,
            week = 1,
            year = 1,
            isByPlayer = false
        ),
        CastPost(
            id = "init_cast_2",
            authorName = "Soundwave Daily",
            authorHandle = "@soundwavedaily",
            content = "The streaming discourse around this year's new releases is heating up. Which independent artist is primed for the next global breakthrough? 🎧📈",
            likes = 8900,
            reposts = 1200,
            commentsCount = 450,
            week = 1,
            year = 1,
            isByPlayer = false
        ),
        CastPost(
            id = "init_cast_3",
            authorName = "Chart Stats",
            authorHandle = "@chartstats",
            content = "📈 Midweek DSP Insights: Independent releases are claiming 34% more editorial playlist spots this quarter. A massive shift toward artist master ownership.",
            likes = 19500,
            reposts = 4800,
            commentsCount = 670,
            week = 1,
            year = 1,
            isByPlayer = false
        ),
        CastPost(
            id = "init_cast_4",
            authorName = "Pop Zone",
            authorHandle = "@popzone",
            content = "Exclusive: Behind-the-scenes recording sessions reveal a massive wave of live instrumentation and analog synth production returning to the top charts. 🎙️⚡",
            likes = 11200,
            reposts = 2100,
            commentsCount = 530,
            week = 1,
            year = 1,
            isByPlayer = false
        ),
        CastPost(
            id = "init_cast_5",
            authorName = "SMZ Celebrity Wire",
            authorHandle = "@smz",
            content = "SPOTTED: Major record label executives and A&R scouts swarming local underground venues looking for raw talent. Who is catching their radar? 👀",
            likes = 24600,
            reposts = 6300,
            commentsCount = 1420,
            week = 1,
            year = 1,
            isByPlayer = false
        ),
        CastPost(
            id = "init_cast_6",
            authorName = "Aria Nova",
            authorHandle = "@arianova",
            content = "Rehearsals for the upcoming world tour start tomorrow! Building something truly immersive for the fans. See you on the road! 🏟️✨",
            likes = 31000,
            reposts = 8900,
            commentsCount = 2100,
            week = 1,
            year = 1,
            isByPlayer = false
        ),
        CastPost(
            id = "init_cast_7",
            authorName = "DJ Pulse",
            authorHandle = "@djpulse",
            content = "Locked in the studio for 14 hours straight. When the low-end frequencies hit right, sleep doesn't matter. New club anthem soon! 🎧🔥",
            likes = 17800,
            reposts = 3900,
            commentsCount = 810,
            week = 1,
            year = 1,
            isByPlayer = false
        )
    )
}
