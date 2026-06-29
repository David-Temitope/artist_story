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
        Car("car_bus", "Touring Coach Bus", cost = 250000.0, costReductionPercent = 0.80f)
    )

    val HOUSES = listOf(
        House("house_studio", "Cozy Studio Apartment", cost = 20000.0, energyBonus = 20),
        House("house_townhouse", "Suburban Townhouse", cost = 80000.0, energyBonus = 50),
        House("house_loft", "Downtown Loft", cost = 200000.0, energyBonus = 100),
        House("house_mansion", "Sunset Hills Mansion", cost = 1000000.0, energyBonus = 200)
    )

    val INSTRUMENTS = listOf(
        Instrument("inst_acoustic", "Budget Acoustic Guitar", cost = 500.0, ratingBonus = 0.1f),
        Instrument("inst_midi", "Studio MIDI Keyboard", cost = 1500.0, ratingBonus = 0.3f),
        Instrument("inst_synth", "Professional Stage Synth", cost = 5000.0, ratingBonus = 0.6f),
        Instrument("inst_vintage", "Custom Vintage Electric", cost = 15000.0, ratingBonus = 1.0f)
    )

    val STOCKS = listOf(
        Stock("stock_media", "MediaGiant", currentPrice = 50.0, weeklyDividendPercent = 0.015f, connectedTo = "MediaGiant"),
        Stock("stock_apex", "ApexRecordCorp", currentPrice = 120.0, weeklyDividendPercent = 0.03f),
        Stock("stock_space", "StudioSpaceInc", currentPrice = 35.0, weeklyDividendPercent = 0.02f),
        Stock("stock_stream", "StreamingStar", currentPrice = 80.0, weeklyDividendPercent = 0.015f),
        Stock("stock_acoustic", "AcousticVibe Instruments", currentPrice = 45.0, weeklyDividendPercent = 0.01f),
        Stock("stock_label", "GlobalIndie Records", currentPrice = 65.0, weeklyDividendPercent = 0.025f),
        Stock("stock_auto", "TeslaDrive Autos", currentPrice = 210.0, weeklyDividendPercent = 0.005f),
        Stock("stock_micro", "SiliconSound Chipsets", currentPrice = 185.0, weeklyDividendPercent = 0.018f),
        Stock("stock_realestate", "SunsetHills Realty", currentPrice = 140.0, weeklyDividendPercent = 0.035f)
    )

    val CRYPTOS = listOf(
        Crypto("crypto_bit", "BitMusic (BTCM)", currentPrice = 45000.0),
        Crypto("crypto_ether", "EtherMelody (ETHM)", currentPrice = 3200.0),
        Crypto("crypto_ripple", "RippleArt (XRP)", currentPrice = 1.20),
        Crypto("crypto_sol", "SolanaSound (SOLS)", currentPrice = 150.0),
        Crypto("crypto_doge", "DogeTune (DOGET)", currentPrice = 0.15),
        Crypto("crypto_cardano", "AdaMelody (ADAM)", currentPrice = 0.65),
        Crypto("crypto_shib", "ShibHype (SHIBH)", currentPrice = 0.000025),
        Crypto("crypto_polka", "PolkaBand (DOTB)", currentPrice = 6.20)
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
        NPCEntity("npc_star_9", "Viper", popularity = 80f, influence = 78f, fans = 2200000, bestSongTitle = "Venomous Spit", bestSongRating = 4.6f, bestSongStreams = 45000000L),
        NPCEntity("npc_star_10", "Alabaster Rock", popularity = 65f, influence = 64f, fans = 950000, bestSongTitle = "Granite Heart", bestSongRating = 4.1f, bestSongStreams = 15000000L),
        NPCEntity("npc_star_11", "Chloe Bloom", popularity = 55f, influence = 59f, fans = 500000, bestSongTitle = "Wildflowers", bestSongRating = 4.3f, bestSongStreams = 8000000L),
        NPCEntity("npc_star_12", "Amadeus Neo", popularity = 45f, influence = 40f, fans = 300000, bestSongTitle = "Midnight Espresso", bestSongRating = 4.5f, bestSongStreams = 4000000L),
        NPCEntity("npc_star_13", "Elysian Sky", popularity = 77f, influence = 75f, fans = 1900000, bestSongTitle = "Stargaze Odyssey", bestSongRating = 4.6f, bestSongStreams = 38000000L),
        NPCEntity("npc_star_14", "Cyrus King", popularity = 83f, influence = 85f, fans = 3100000, bestSongTitle = "Regal Touch", bestSongRating = 4.7f, bestSongStreams = 62000000L)
    )
}
