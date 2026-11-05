package com.example.data

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HotelDashboardFullTest {

    @Test
    fun testHotelVentureDashboardProperties() {
        val venture = BusinessVenture(
            id = "hotel_1",
            name = "The Grand Palms Hotel",
            type = "Hotel",
            setupCost = 500000.0,
            tierLevel = 1,
            unitCount = 2,
            weeklyGrossRevenue = 15000.0,
            weeklyOperatingExpenses = 4500.0,
            weeklyNetProfit = 10500.0,
            isOwned = true
        )

        // Verify getUpgradeInfo
        val upgrade = VentureSimulationHelper.getUpgradeInfo(venture)
        assertNotNull(upgrade)

        // Verify diagnostics
        val diagnostics = VentureDiagnosticsHelper.analyzeVenture(venture)
        assertNotNull(diagnostics)

        // Verify rooms
        val rooms = if (venture.hotelRooms.isEmpty()) VentureSimulationHelper.getDefaultHotelRooms() else venture.hotelRooms
        assertTrue(rooms.size >= 20)

        // Verify default amenities
        val amenities = if (venture.hotelAmenities.isEmpty()) VentureSimulationHelper.getDefaultHotelAmenities() else venture.hotelAmenities
        assertTrue(amenities.isNotEmpty())

        // Test all tiers
        for (tier in 1..4) {
            val tierVenture = venture.copy(tierLevel = tier)
            val info = VentureSimulationHelper.getUpgradeInfo(tierVenture)
            assertNotNull(info)
        }
    }
}
