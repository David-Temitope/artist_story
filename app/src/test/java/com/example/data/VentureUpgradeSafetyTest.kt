package com.example.data

import org.junit.Assert.assertNotNull
import org.junit.Test

class VentureUpgradeSafetyTest {

    @Test
    fun testHotelUpgradeInfoSafetyAcrossAllTiers() {
        for (tier in 1..5) {
            val venture = BusinessVenture(
                id = "test_hotel",
                name = "Test Hotel",
                type = "Hotel",
                setupCost = 500000.0,
                tierLevel = tier,
                weeklyGrossRevenue = 50000.0,
                weeklyOperatingExpenses = 20000.0,
                weeklyNetProfit = 30000.0
            )
            val upgradeInfo = VentureSimulationHelper.getUpgradeInfo(venture)
            assertNotNull("Upgrade info should never be null or crash for hotel tier $tier", upgradeInfo)
        }
    }

    @Test
    fun testAllVentureTypesUpgradeSafetyAcrossTiers() {
        val types = listOf("Hotel", "Hotel Chain", "Taxi Fleet", "Restaurant", "Farming", "Crypto Token")
        for (type in types) {
            for (tier in 1..5) {
                val venture = BusinessVenture(
                    id = "test_$type",
                    name = "Test $type",
                    type = type,
                    setupCost = 50000.0,
                    tierLevel = tier,
                    weeklyGrossRevenue = 50000.0,
                    weeklyOperatingExpenses = 20000.0,
                    weeklyNetProfit = 30000.0
                )
                val upgradeInfo = VentureSimulationHelper.getUpgradeInfo(venture)
                assertNotNull("Upgrade info should never crash for $type at tier $tier", upgradeInfo)
            }
        }
    }
}
