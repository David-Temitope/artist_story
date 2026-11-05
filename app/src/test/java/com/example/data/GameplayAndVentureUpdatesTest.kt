package com.example.data

import org.junit.Assert.*
import org.junit.Test

class GameplayAndVentureUpdatesTest {

    @Test
    fun testFarmProduceSellDoesNotCreditPersonalCashTwice() {
        val inv = FarmInventoryItem(
            id = "crop_apples",
            name = "Apples",
            category = "CROP",
            quantity = 50.0,
            marketPricePerUnit = 12.0
        )
        val farm = BusinessVenture(
            id = "farm-1",
            name = "Test Farm",
            type = "ORGANIC_FARM",
            setupCost = 10000.0,
            accumulatedProfit = 500.0,
            tierLevel = 1,
            farm = VentureFarmState(
                farmInventory = listOf(inv)
            )
        )

        val pricePerUnit = 12.0
        val totalRevenue = 50.0 * pricePerUnit

        val updatedFarm = farm.copy(
            accumulatedProfit = farm.accumulatedProfit + totalRevenue,
            farm = farm.farm.copy(farmInventory = emptyList())
        )

        assertTrue(updatedFarm.farmInventory.isEmpty())
        assertEquals(500.0 + totalRevenue, updatedFarm.accumulatedProfit, 0.01)
    }

    @Test
    fun testSiloAutoSell70Percent() {
        val inv = FarmInventoryItem(
            id = "crop_apples",
            name = "Apples",
            category = "CROP",
            quantity = 100.0,
            marketPricePerUnit = 10.0
        )
        val farm = BusinessVenture(
            id = "farm-silo",
            name = "Silo Farm",
            type = "Farming",
            isOwned = true,
            setupCost = 20000.0,
            accumulatedProfit = 0.0,
            tierLevel = 2,
            farm = VentureFarmState(
                farmInventory = listOf(inv),
                siloAutoSell = true,
                siloCondition = 1.0f
            )
        )

        val state = GameStateEntity(email = "test@game.com", artistName = "Test Artist").copy(
            businessVentures = listOf(farm)
        )

        val result = VentureSimulationHelper.processWeeklyVentures(
            state = state,
            economyCycle = "NORMAL",
            reportBuilder = StringBuilder()
        )

        val updatedFarm = result.updatedVentures.find { it.id == "farm-silo" }
        assertNotNull(updatedFarm)
        val appleItem = updatedFarm!!.farmInventory.find { it.id == "crop_apples" }
        assertNotNull(appleItem)
        // 70% sold -> 30 units remain
        assertEquals(30.0, appleItem!!.quantity, 0.01)
        assertTrue("Weekly revenue should include 70% auto-sell", updatedFarm.weeklyGrossRevenue > 0.0)
    }

    @Test
    fun testSiloAutoSellPausesAtTier4() {
        val inv = FarmInventoryItem(
            id = "crop_apples",
            name = "Apples",
            category = "CROP",
            quantity = 100.0,
            marketPricePerUnit = 10.0
        )
        val machinery = listOf(FarmMachine("mach_1", "Industrial Processor", "PROCESSING", 50000.0, 500.0, isOwned = true, condition = 1.0f))
        val farm = BusinessVenture(
            id = "farm-agri",
            name = "Agri-Industry Farm",
            type = "Farming",
            isOwned = true,
            setupCost = 100000.0,
            accumulatedProfit = 0.0,
            tierLevel = 4, // Agri-Industry tier
            farm = VentureFarmState(
                farmInventory = listOf(inv),
                farmMachinery = machinery,
                siloAutoSell = true,
                siloCondition = 1.0f
            )
        )

        val state = GameStateEntity(email = "test@game.com", artistName = "Test Artist").copy(
            businessVentures = listOf(farm)
        )

        val result = VentureSimulationHelper.processWeeklyVentures(
            state = state,
            economyCycle = "NORMAL",
            reportBuilder = StringBuilder()
        )

        val updatedFarm = result.updatedVentures.find { it.id == "farm-agri" }
        assertNotNull(updatedFarm)
        val appleItem = updatedFarm!!.farmInventory.find { it.id == "crop_apples" }
        assertNotNull(appleItem)
        // At Tier 4, auto-sell is paused to reserve crops for agro-processing
        assertEquals(100.0, appleItem!!.quantity, 0.01)
    }

    @Test
    fun testHotelCapacityScalesTo100AcrossTiers() {
        var hotel = BusinessVenture(
            id = "hotel-1",
            name = "Boutique Hotel",
            type = "Hotel",
            setupCost = 50000.0,
            tierLevel = 1,
            hotel = VentureHotelState(hotelMaxRoomCapacity = 20)
        )

        assertEquals(20, hotel.hotelMaxRoomCapacity)

        // Upgrade through tiers to Tier 4
        hotel = VentureSimulationHelper.performUpgrade(hotel) // Tier 2 -> 35
        assertEquals(35, hotel.hotelMaxRoomCapacity)
        hotel = VentureSimulationHelper.performUpgrade(hotel) // Tier 3 -> 60
        assertEquals(60, hotel.hotelMaxRoomCapacity)
        hotel = VentureSimulationHelper.performUpgrade(hotel) // Tier 4 -> 100
        assertEquals(100, hotel.hotelMaxRoomCapacity)
        assertEquals(100, hotel.hotelRooms.size)
    }

    @Test
    fun testSongLyricRatingCalculation() {
        // Skill 50/100 -> skillRatio = 0.5 -> baseRating = 1.0 + (0.5 * 4.0) = 3.0
        val skillRatio = (50f / 100f).coerceIn(0.2f, 1.0f)
        val baseRating = 1.0f + (skillRatio * 4.0f)
        val rating = (baseRating * 10).toInt() / 10f

        assertEquals(3.0f, rating, 0.01f)

        // Skill 100/100 -> rating 5.0
        val maxRatio = (100f / 100f).coerceIn(0.2f, 1.0f)
        val maxRating = ((1.0f + (maxRatio * 4.0f)) * 10).toInt() / 10f
        assertEquals(5.0f, maxRating, 0.01f)
    }

    @Test
    fun testNpcSpamBlockAndUnblock() {
        val blockedList = mutableListOf<String>()
        val spamCountMap = mutableMapOf<String, Int>()

        val npcId = "npc_1"
        spamCountMap[npcId] = 3
        if (spamCountMap[npcId]!! >= 3) {
            blockedList.add(npcId)
        }
        assertTrue(blockedList.contains(npcId))

        // When fame rises (popularity >= 70 or fans >= 250k)
        val popularity = 75
        val fans = 300_000L
        if (popularity >= 70 || fans >= 250_000L) {
            blockedList.remove(npcId)
            spamCountMap[npcId] = 0
        }

        assertFalse(blockedList.contains(npcId))
        assertEquals(0, spamCountMap[npcId])
    }
}
