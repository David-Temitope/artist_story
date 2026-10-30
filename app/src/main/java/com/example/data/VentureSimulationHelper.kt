package com.example.data

import java.util.UUID
import kotlin.math.roundToInt
import kotlin.random.Random

data class VentureTierConfig(
    val tierLevel: Int,
    val tierName: String,
    val setupCost: Double,
    val upgradeCost: Double,
    val baseWeeklyGross: Double,
    val baseWeeklyExpenses: Double,
    val description: String,
    val perks: List<String>
)

data class VentureUpgradeInfo(
    val title: String,
    val description: String,
    val cost: Double,
    val currentWeeklyGross: Double,
    val nextWeeklyGross: Double,
    val currentWeeklyExpenses: Double,
    val nextWeeklyExpenses: Double,
    val currentNetProfit: Double,
    val nextNetProfit: Double,
    val isMaxed: Boolean
)

data class WeShareValuationResult(
    val totalValuation: Double,
    val sharePrice: Double,
    val totalSharesIssued: Long,
    val targetSharesToSell: Long,
    val estimatedCapitalRaised: Double,
    val peRatio: Double
)

data class VentureWeeklySimulationResult(
    val updatedVentures: List<BusinessVenture>,
    val updatedStocks: List<Stock>,
    val generatedMails: List<MailMessage>,
    val totalPlayerProfitCash: Double,
    val totalIpoCashEarned: Double,
    val logs: List<String>
)

object VentureSimulationHelper {

    data class RestaurantTierDetail(
        val tierLevel: Int,
        val name: String,
        val upgradeCost: Double,
        val setupCost: Double,
        val minDailyCapacity: Int,
        val maxDailyCapacity: Int,
        val minChefs: Int,
        val minWaiters: Int,
        val minCleaners: Int,
        val minManagers: Int,
        val minDailyRevenue: Double,
        val maxDailyRevenue: Double,
        val propertyValue: Double,
        val buildingMaintenanceMonthlyPercent: Double,
        val description: String,
        val perks: List<String>
    )

    const val WAITER_WEEKLY_SALARY = 300.0
    const val CHEF_WEEKLY_SALARY = 600.0
    const val SENIOR_CHEF_WEEKLY_SALARY = 850.0
    const val CLEANER_WEEKLY_SALARY = 250.0
    const val MANAGER_WEEKLY_SALARY = 750.0

    const val TAXI_STARTING_COST = 5000.0
    const val TAXI_CAR_COST = 4500.0
    const val TAXI_MAX_CARS = 25
    const val TAXI_DRIVER_SALARY = 400.0
    const val TAXI_MECHANIC_SALARY = 550.0
    const val TAXI_DISPATCHER_SALARY = 450.0
    const val TAXI_BASE_WEEKLY_GROSS = 955.0
    const val TAXI_BASE_WEEKLY_FUEL = 160.0
    const val TAXI_BASE_WEEKLY_MAINT = 120.0
    const val TAXI_SERVICE_BASE_COST = 500.0

    data class TaxiDepotTier(
        val level: Int,
        val name: String,
        val maxCars: Int,
        val upgradeCost: Double,
        val description: String
    )

    val TAXI_DEPOT_TIERS = listOf(
        TaxiDepotTier(1, "Starter Parking Lot", 5, 0.0, "Basic city staging lot holding up to 5 taxi cabs."),
        TaxiDepotTier(2, "Commercial Depot", 10, 8000.0, "Paved fleet yard with maintenance bays holding up to 10 vehicles."),
        TaxiDepotTier(3, "Regional Transit Terminal", 18, 18000.0, "Multi-lane transit depot holding up to 18 vehicles."),
        TaxiDepotTier(4, "Metropolitan Transport Hub", 25, 35000.0, "Flagship corporate transport hub accommodating the maximum city limit of 25 vehicles.")
    )

    // --- Farming Data Structures & Catalogs ---
    data class FarmLandOption(
        val tierKey: String,
        val name: String,
        val acres: Double,
        val monthlyRent: Double,
        val weeklyRent: Double,
        val purchasePrice: Double,
        val description: String
    )

    val FARM_LAND_OPTIONS = listOf(
        FarmLandOption("SMALL_PLOT", "Small Plot", 10.0, 500.0, 125.0, 8000.0, "10-acre parcel ideal for crop batches and startup agricultural operations."),
        FarmLandOption("MEDIUM_FARM", "Medium Farm", 35.0, 1500.0, 375.0, 25000.0, "35-acre fertile tract suitable for orchards and diversified fields."),
        FarmLandOption("LARGE_FARM", "Large Farm", 100.0, 4000.0, 1000.0, 70000.0, "100-acre expansive estate capable of hosting large pastures and grain fields."),
        FarmLandOption("INDUSTRIAL_FARMLAND", "Industrial Farmland", 250.0, 10000.0, 2500.0, 200000.0, "250+ acre mega-estate for commercial agro-industrial production and livestock.")
    )

    data class CropTypeInfo(
        val name: String,
        val seedCostPerAcre: Double,
        val growthWeeks: Int,
        val yieldPotentialText: String,
        val expectedYieldUnits: Double,
        val yieldUnitName: String,
        val marketPricePerUnit: Double,
        val description: String
    )

    val CROPS_CATALOG = listOf(
        CropTypeInfo("Maize", 150.0, 12, "Medium", 100.0, "bushels", 7.50, "Staple grain with steady market demand and corn flour processing potential."),
        CropTypeInfo("Rice", 250.0, 16, "High", 120.0, "bags", 11.00, "High-demand cereal crop thriving with proper irrigation and farmhand care."),
        CropTypeInfo("Tomatoes", 300.0, 10, "High", 90.0, "crates", 13.50, "Fast-growing cash crop, highly profitable for direct sale or tomato paste."),
        CropTypeInfo("Wheat", 200.0, 16, "Medium", 110.0, "bushels", 9.00, "Reliable grain, excellent for cold-weather storage and bakery flour milling."),
        CropTypeInfo("Potatoes", 250.0, 13, "High", 100.0, "sacks", 12.50, "Resilient root vegetable with strong supermarket and wholesale demand.")
    )

    data class LivestockTypeInfo(
        val type: String,
        val purchaseCost: Double,
        val matureSalePrice: Double,
        val weeksToMaturity: Int,
        val weeklyFeedCost: Double,
        val weeklyVetCost: Double,
        val mainProducts: String,
        val weeklyProduceValue: Double,
        val description: String
    )

    val LIVESTOCK_CATALOG = listOf(
        LivestockTypeInfo("Chicken", 50.0, 5000.0, 4, 2.00, 1.00, "Eggs & Meat", 40.00, "Bought as chick. Takes 4 weeks to mature. Produces eggs ($40/wk) + chicks or sells for $5k."),
        LivestockTypeInfo("Goat", 200.0, 8000.0, 6, 8.00, 3.00, "Milk & Meat", 150.00, "Bought as kid. Takes 6 weeks to mature. Produces milk ($150/wk) + kids or sells for $8k."),
        LivestockTypeInfo("Sheep", 350.0, 10000.0, 10, 10.00, 3.50, "Wool & Meat", 180.00, "Bought as kid. Takes 10 weeks to mature. Produces wool ($180/wk) + lambs or sells for $10k."),
        LivestockTypeInfo("Cow", 1200.0, 15000.0, 12, 35.00, 10.00, "Milk & Calves", 350.00, "Bought as kid. Takes 12 weeks to mature. Produces rich milk ($350/wk) + calves or sells for $15k."),
        LivestockTypeInfo("Pig", 300.0, 12000.0, 14, 15.00, 4.00, "Reproduction & Meat", 0.00, "Bought as kid. Takes 14 weeks to mature. Reproduces quickly or sells mature for $12k.")
    )

    data class MachineCatalogItem(
        val id: String,
        val name: String,
        val category: String, // "TOOL", "MACHINE", "VEHICLE", "STORAGE", "PROCESSING"
        val purchasePrice: Double,
        val weeklyRent: Double,
        val weeklyMaintenance: Double,
        val gameplayEffect: String,
        val unlockTier: Int = 1
    )

    val MACHINERY_CATALOG = listOf(
        MachineCatalogItem("tools_basic", "Basic Farming Tools", "TOOL", 500.0, 30.0, 8.0, "Reduces manual labor requirements (+25% farmhand efficiency)", 1),
        MachineCatalogItem("mach_tractor", "Commercial Tractor", "MACHINE", 8000.0, 350.0, 95.0, "Reduces planting/prep time; saves labor and unlocks +20% land efficiency", 1),
        MachineCatalogItem("mach_seeder", "Precision Seeder", "MACHINE", 2500.0, 100.0, 30.0, "Reduces seed waste and increases planting efficiency by 20%", 1),
        MachineCatalogItem("mach_irrigation", "Automated Irrigation System", "MACHINE", 5000.0, 200.0, 60.0, "Reduces drought-related crop losses to 0%", 1),
        MachineCatalogItem("mach_sprayer", "Crop Sprayer", "TOOL", 1000.0, 50.0, 15.0, "Reduces pest and blight crop losses by 80%", 1),
        MachineCatalogItem("mach_harvester", "Combine Harvester", "MACHINE", 12000.0, 500.0, 140.0, "Makes harvesting faster and prevents harvest weather losses", 2),
        MachineCatalogItem("veh_farm_truck", "Heavy Farm Truck", "VEHICLE", 12000.0, 400.0, 130.0, "Allows larger bulk shipments to reach buyers (+10% market price)", 2),
        MachineCatalogItem("veh_livestock_truck", "Livestock Transport Truck", "VEHICLE", 18000.0, 600.0, 180.0, "Enables safe animal transport and livestock trading", 3),
        MachineCatalogItem("stor_cold_storage", "Refrigerated Cold Storage", "STORAGE", 15000.0, 500.0, 150.0, "Prevents stored produce and dairy from spoiling over time", 2),
        MachineCatalogItem("proc_industrial", "Agri-Industrial Processing Unit", "PROCESSING", 15000.0, 600.0, 175.0, "Converts raw produce & milk into 2.5x higher-value processed goods", 4),
        MachineCatalogItem("mach_tomato_paste", "Tomato Canning Unit", "PROCESSING", 10000.0, 400.0, 120.0, "Converts 20 tomatoes into canned tomato paste (+20% value)", 4),
        MachineCatalogItem("mach_cereal_mill", "Grain Cereal Mill", "PROCESSING", 12000.0, 450.0, 130.0, "Converts 10 maize into breakfast cereal (+20% value)", 4),
        MachineCatalogItem("mach_chip_fryer", "Potato Chip Processing Plant", "PROCESSING", 11000.0, 420.0, 125.0, "Converts potatoes into crisp chips (+20% value)", 4),
        MachineCatalogItem("mach_flour_mill", "Industrial Flour Mill", "PROCESSING", 10000.0, 400.0, 115.0, "Converts wheat into fine baking flour (+20% value)", 4),
        MachineCatalogItem("mach_yogurt_vat", "Dairy Pasteurizer & Yogurt Vat", "PROCESSING", 14000.0, 500.0, 140.0, "Converts fresh milk into cultured yogurt (+20% value)", 4),
        MachineCatalogItem("mach_textile_loom", "Textile Weaving & Knitting Loom", "PROCESSING", 15000.0, 550.0, 150.0, "Converts wool into warm sweaters (+20% value)", 4)
    )

    data class FarmProcessingRecipe(
        val id: String,
        val outputName: String,
        val outputUnit: String,
        val outputQuantityPerBatch: Double,
        val rawIngredientName: String,
        val rawQuantityPerBatch: Double,
        val rawUnit: String,
        val requiredMachineId: String,
        val requiredMachineName: String,
        val processedPricePerUnit: Double,
        val description: String
    )

    val FARM_PROCESSING_RECIPES = listOf(
        FarmProcessingRecipe(
            id = "rec_tomato_paste",
            outputName = "Tomato Paste",
            outputUnit = "cans",
            outputQuantityPerBatch = 1.0,
            rawIngredientName = "Tomatoes",
            rawQuantityPerBatch = 20.0,
            rawUnit = "crates",
            requiredMachineId = "mach_tomato_paste",
            requiredMachineName = "Tomato Canning Unit",
            processedPricePerUnit = 360.0,
            description = "Turns 20 crates of tomatoes into 1 can of tomato paste (+20% value over raw tomatoes)."
        ),
        FarmProcessingRecipe(
            id = "rec_cereal",
            outputName = "Breakfast Cereal",
            outputUnit = "boxes",
            outputQuantityPerBatch = 1.0,
            rawIngredientName = "Maize",
            rawQuantityPerBatch = 10.0,
            rawUnit = "bushels",
            requiredMachineId = "mach_cereal_mill",
            requiredMachineName = "Grain Cereal Mill",
            processedPricePerUnit = 90.0,
            description = "Turns 10 bushels of maize into 1 box of cereal (+20% value over raw maize)."
        ),
        FarmProcessingRecipe(
            id = "rec_potato_chips",
            outputName = "Potato Chips",
            outputUnit = "cartons",
            outputQuantityPerBatch = 1.0,
            rawIngredientName = "Potatoes",
            rawQuantityPerBatch = 10.0,
            rawUnit = "sacks",
            requiredMachineId = "mach_chip_fryer",
            requiredMachineName = "Potato Chip Processing Plant",
            processedPricePerUnit = 150.0,
            description = "Turns 10 sacks of potatoes into 1 carton of chips (+20% value over raw potatoes)."
        ),
        FarmProcessingRecipe(
            id = "rec_flour",
            outputName = "Baking Flour",
            outputUnit = "sacks",
            outputQuantityPerBatch = 1.0,
            rawIngredientName = "Wheat",
            rawQuantityPerBatch = 10.0,
            rawUnit = "bushels",
            requiredMachineId = "mach_flour_mill",
            requiredMachineName = "Industrial Flour Mill",
            processedPricePerUnit = 108.0,
            description = "Turns 10 bushels of wheat into 1 sack of flour (+20% value over raw wheat)."
        ),
        FarmProcessingRecipe(
            id = "rec_yogurt",
            outputName = "Cultured Yogurt",
            outputUnit = "tubs",
            outputQuantityPerBatch = 1.0,
            rawIngredientName = "Milk",
            rawQuantityPerBatch = 10.0,
            rawUnit = "gallons",
            requiredMachineId = "mach_yogurt_vat",
            requiredMachineName = "Dairy Pasteurizer & Yogurt Vat",
            processedPricePerUnit = 420.0,
            description = "Turns 10 gallons of milk into 1 tub of yogurt (+20% value over raw milk)."
        ),
        FarmProcessingRecipe(
            id = "rec_sweaters",
            outputName = "Wool Sweaters",
            outputUnit = "pieces",
            outputQuantityPerBatch = 1.0,
            rawIngredientName = "Wool",
            rawQuantityPerBatch = 10.0,
            rawUnit = "bundles",
            requiredMachineId = "mach_textile_loom",
            requiredMachineName = "Textile Weaving & Knitting Loom",
            processedPricePerUnit = 216.0,
            description = "Turns 10 bundles of wool into 1 hand-loomed sweater (+20% value over raw wool)."
        )
    )

    const val FARMHAND_WEEKLY_SALARY = 350.0
    const val TRACTOR_OPERATOR_WEEKLY_SALARY = 500.0
    const val LIVESTOCK_WORKER_WEEKLY_SALARY = 400.0
    const val DRIVER_WEEKLY_SALARY = 450.0
    const val AGRI_TECH_WEEKLY_SALARY = 700.0
    const val VETERINARIAN_WEEKLY_SALARY = 900.0
    const val FARM_MANAGER_WEEKLY_SALARY = 1000.0

    fun getDefaultFarmMachinery(): List<FarmMachine> {
        return MACHINERY_CATALOG.map { item ->
            val isOwnedInitial = item.id == "tools_basic"
            FarmMachine(
                id = item.id,
                name = item.name,
                category = item.category,
                purchaseCost = item.purchasePrice,
                weeklyRentalCost = item.weeklyRent,
                isOwned = isOwnedInitial,
                isRented = false,
                condition = 1.0f,
                bookValue = if (isOwnedInitial) item.purchasePrice else 0.0,
                weeklyMaintenance = item.weeklyMaintenance,
                gameplayEffect = item.gameplayEffect
            )
        }
    }

    fun getDefaultFarmLivestock(): List<FarmLivestockGroup> {
        return LIVESTOCK_CATALOG.map { item ->
            FarmLivestockGroup(
                type = item.type,
                count = 0,
                matureCount = 0,
                babyCount = 0,
                babyWeeksOld = emptyList(),
                purchaseCostPerUnit = item.purchaseCost,
                matureSalePrice = item.matureSalePrice,
                weeksToMaturity = item.weeksToMaturity,
                weeklyFeedPerUnit = item.weeklyFeedCost,
                weeklyVetPerUnit = item.weeklyVetCost,
                mainProducts = item.mainProducts,
                weeklyProduceRevenuePerUnit = item.weeklyProduceValue
            )
        }
    }

    val RESTAURANT_TIER_DETAILS = listOf(
        RestaurantTierDetail(
            tierLevel = 1,
            name = "Roadside Stand",
            upgradeCost = 0.0,
            setupCost = 5000.0,
            minDailyCapacity = 5,
            maxDailyCapacity = 10,
            minChefs = 1,
            minWaiters = 1,
            minCleaners = 0,
            minManagers = 0,
            minDailyRevenue = 150.0,
            maxDailyRevenue = 300.0,
            propertyValue = 5000.0,
            buildingMaintenanceMonthlyPercent = 0.015,
            description = "A humble sidewalk food stand serving hot specialty street dishes with fresh local ingredients.",
            perks = listOf("Low overhead", "Fast customer turnaround")
        ),
        RestaurantTierDetail(
            tierLevel = 2,
            name = "Food Truck",
            upgradeCost = 12000.0,
            setupCost = 17000.0,
            minDailyCapacity = 15,
            maxDailyCapacity = 30,
            minChefs = 1,
            minWaiters = 1,
            minCleaners = 0,
            minManagers = 0,
            minDailyRevenue = 350.0,
            maxDailyRevenue = 700.0,
            propertyValue = 17000.0,
            buildingMaintenanceMonthlyPercent = 0.015,
            description = "Custom branded mobile kitchen parked at high-traffic nightlife events and festivals.",
            perks = listOf("Mobile prime spots", "Higher customer capacity")
        ),
        RestaurantTierDetail(
            tierLevel = 3,
            name = "Corner Shop",
            upgradeCost = 35000.0,
            setupCost = 52000.0,
            minDailyCapacity = 30,
            maxDailyCapacity = 50,
            minChefs = 2,
            minWaiters = 2,
            minCleaners = 1,
            minManagers = 0,
            minDailyRevenue = 700.0,
            maxDailyRevenue = 1500.0,
            propertyValue = 52000.0,
            buildingMaintenanceMonthlyPercent = 0.0125,
            description = "A cozy sit-down bistro with permanent indoor seating, curated menu, and loyal regulars.",
            perks = listOf("Permanent prime location", "Loyal returning customers")
        ),
        RestaurantTierDetail(
            tierLevel = 4,
            name = "Dining Lounge",
            upgradeCost = 85000.0,
            setupCost = 137000.0,
            minDailyCapacity = 60,
            maxDailyCapacity = 100,
            minChefs = 3,
            minWaiters = 5,
            minCleaners = 1,
            minManagers = 1,
            minDailyRevenue = 1500.0,
            maxDailyRevenue = 3500.0,
            propertyValue = 137000.0,
            buildingMaintenanceMonthlyPercent = 0.0125,
            description = "Trendy high-energy urban dining club featuring premium menu, VIP booths, and table reservations.",
            perks = listOf("VIP reservations", "High ticket average")
        ),
        RestaurantTierDetail(
            tierLevel = 5,
            name = "Banquet Hall",
            upgradeCost = 220000.0,
            setupCost = 357000.0,
            minDailyCapacity = 150,
            maxDailyCapacity = 300,
            minChefs = 8,
            minWaiters = 15,
            minCleaners = 3,
            minManagers = 2,
            minDailyRevenue = 4000.0,
            maxDailyRevenue = 10000.0,
            propertyValue = 357000.0,
            buildingMaintenanceMonthlyPercent = 0.010,
            description = "Palatial event complex hosting weddings, gala receptions, and massive private banquets.",
            perks = listOf("Massive corporate and wedding buyouts", "Elite culinary reputation")
        )
    )

    fun getDefaultRestaurantEquipment(): List<RestaurantEquipment> = listOf(
        RestaurantEquipment("eq_grill", "Grill", 800.0, 30.0, 1.0f, false, 1),
        RestaurantEquipment("eq_deep_fryer", "Deep Fryer", 600.0, 25.0, 1.0f, false, 1),
        RestaurantEquipment("eq_refrigerator", "Refrigerator", 1200.0, 40.0, 1.0f, false, 1),
        RestaurantEquipment("eq_freezer", "Freezer", 1000.0, 35.0, 1.0f, false, 2),
        RestaurantEquipment("eq_cooker", "Commercial Cooker", 2000.0, 60.0, 1.0f, false, 2),
        RestaurantEquipment("eq_oven", "Oven", 2500.0, 70.0, 1.0f, false, 3),
        RestaurantEquipment("eq_pos", "POS System", 800.0, 20.0, 1.0f, false, 3),
        RestaurantEquipment("eq_dishwasher", "Industrial Dishwasher", 3000.0, 80.0, 1.0f, false, 3),
        RestaurantEquipment("eq_furniture", "Dining Furniture", 5000.0, 120.0, 1.0f, false, 4),
        RestaurantEquipment("eq_ventilation", "Commercial Ventilation", 4000.0, 90.0, 1.0f, false, 4),
        RestaurantEquipment("eq_generator", "Generator", 4000.0, 100.0, 1.0f, false, 5)
    )

    // Config for fixed tiered businesses:
    // Restaurant, Farming, Crypto Token
    val RESTAURANT_TIERS = listOf(
        VentureTierConfig(
            tierLevel = 1,
            tierName = "Roadside Stand",
            setupCost = 5000.0,
            upgradeCost = 0.0,
            baseWeeklyGross = 1400.0,
            baseWeeklyExpenses = 1337.0, // 1 chef ($600), 1 waiter ($300), 30% ingredients ($420), upkeep ($17)
            description = "A humble sidewalk food stand serving hot specialty street dishes with fresh local ingredients.",
            perks = listOf("Low overhead", "Fast customer turnaround")
        ),
        VentureTierConfig(
            tierLevel = 2,
            tierName = "Food Truck",
            setupCost = 17000.0,
            upgradeCost = 12000.0,
            baseWeeklyGross = 3675.0,
            baseWeeklyExpenses = 2060.0,
            description = "Custom branded mobile kitchen parked at high-traffic nightlife events and festivals.",
            perks = listOf("Mobile prime spots", "High weekend rush volume")
        ),
        VentureTierConfig(
            tierLevel = 3,
            tierName = "Corner Shop",
            setupCost = 52000.0,
            upgradeCost = 35000.0,
            baseWeeklyGross = 7700.0,
            baseWeeklyExpenses = 4810.0,
            description = "A cozy sit-down bistro with indoor dining, curated wine pairings, and loyal regulars.",
            perks = listOf("Stable weekday dinner reservations", "Alcohol beverage margins")
        ),
        VentureTierConfig(
            tierLevel = 4,
            tierName = "Dining Lounge",
            setupCost = 137000.0,
            upgradeCost = 85000.0,
            baseWeeklyGross = 17500.0,
            baseWeeklyExpenses = 11145.0,
            description = "Trendy high-energy urban dining club featuring live acoustic sets and VIP booth bookings.",
            perks = listOf("Celebrity patrons", "VIP table minimums")
        ),
        VentureTierConfig(
            tierLevel = 5,
            tierName = "Banquet Hall",
            setupCost = 357000.0,
            upgradeCost = 220000.0,
            baseWeeklyGross = 49000.0,
            baseWeeklyExpenses = 31224.0,
            description = "Palatial event complex hosting weddings, corporate buyouts, and elite gala banquets.",
            perks = listOf("Massive corporate booking fees", "World-class industry reputation")
        )
    )

    val FARMING_TIERS = listOf(
        VentureTierConfig(
            tierLevel = 1,
            tierName = "Crop Plantation",
            setupCost = 15000.0,
            upgradeCost = 0.0,
            baseWeeklyGross = 2200.0,
            baseWeeklyExpenses = 1100.0, // seeds, basic tools upkeep, 1 farmhand
            description = "A modest acreage growing staple grain, corn, and seasonal root vegetables.",
            perks = listOf("Raw staple harvest sales", "Small plot farming")
        ),
        VentureTierConfig(
            tierLevel = 2,
            tierName = "Crops + Fruit Orchards",
            setupCost = 30000.0,
            upgradeCost = 15000.0,
            baseWeeklyGross = 5500.0,
            baseWeeklyExpenses = 2800.0, // fruit grove maintenance, pickers, crates, cold transport
            description = "Expanded groves producing apples, citrus, and organic berries sold to regional grocers.",
            perks = listOf("Fruit harvest sales", "Supermarket bulk contracts")
        ),
        VentureTierConfig(
            tierLevel = 3,
            tierName = "Crops + Fruits + Livestock",
            setupCost = 80000.0,
            upgradeCost = 50000.0,
            baseWeeklyGross = 14000.0,
            baseWeeklyExpenses = 7500.0, // animal feed, veterinary care, stable hands, fence upkeep
            description = "Integrated pastoral farm with free-range poultry, dairy cattle, and heritage livestock.",
            perks = listOf("Livestock rearing", "Fresh dairy, eggs, wool & meat")
        ),
        VentureTierConfig(
            tierLevel = 4,
            tierName = "Agri-Industrial + Dairy",
            setupCost = 230000.0,
            upgradeCost = 150000.0,
            baseWeeklyGross = 32000.0,
            baseWeeklyExpenses = 17000.0, // pasteurization, cheese aging, bottling plant, logistics
            description = "Automated processing plant bottling artisanal milk, aging specialty cheeses, and packaging farm-fresh organic goods for nationwide retail.",
            perks = listOf("High-value processed food lines", "National wholesale distribution")
        )
    )

    val CRYPTO_TOKEN_TIERS = listOf(
        VentureTierConfig(
            tierLevel = 1,
            tierName = "Basic Crypto Startup",
            setupCost = 1000000.0,
            upgradeCost = 0.0,
            baseWeeklyGross = 1020.0, // Projected $800–$1,200
            baseWeeklyExpenses = 740.0, // 1 Dev ($500) + Mkt ($150) + Web ($40) + Sec ($50)
            description = "A high-capital decentralized token venture requiring smart contract audits, decentralized hosting, initial liquidity pool funding, and core developers.",
            perks = listOf("Token creation & founder allocation", "DEX swap fees (0.3%)", "Exchange listing pathway")
        ),
        VentureTierConfig(
            tierLevel = 2,
            tierName = "Utility Protocol & Staking Hub",
            setupCost = 65000.0,
            upgradeCost = 35000.0,
            baseWeeklyGross = 4800.0,
            baseWeeklyExpenses = 2400.0,
            description = "Audited smart contracts with token staking, decentralized governance, and utility gating.",
            perks = listOf("Staking yield revenue", "Audited multi-sig security")
        ),
        VentureTierConfig(
            tierLevel = 3,
            tierName = "DeFi Liquidity AMM Ecosystem",
            setupCost = 140000.0,
            upgradeCost = 75000.0,
            baseWeeklyGross = 13500.0,
            baseWeeklyExpenses = 6200.0,
            description = "High-throughput AMM exchange with automated liquidity vaults, cross-chain bridge, and deep institutional capital.",
            perks = listOf("Institutional liquidity routing", "Massive swap volume cuts")
        ),
        VentureTierConfig(
            tierLevel = 4,
            tierName = "Decentralized AppChain & Layer-2",
            setupCost = 320000.0,
            upgradeCost = 180000.0,
            baseWeeklyGross = 34000.0,
            baseWeeklyExpenses = 15000.0,
            description = "Independent Layer-2 appchain powering gaming micro-transactions, web3 creator paywalls, and cross-border streaming settlements.",
            perks = listOf("Sequencer gas royalties", "Global ecosystem developer grants")
        )
    )

    // --- 5 Core Crypto Business Elements Catalogs ---
    data class CryptoSecurityTierDetail(
        val tier: Int,
        val name: String,
        val upgradeCost: Double,
        val weeklyCost: Double,
        val vulnerabilityPercent: Double,
        val description: String
    )

    data class CryptoWebsiteAppTierDetail(
        val tier: Int,
        val name: String,
        val upgradeCost: Double,
        val weeklyCost: Double,
        val userCapacity: Int,
        val description: String
    )

    data class CryptoMarketingTierDetail(
        val tier: Int,
        val name: String,
        val weeklyCost: Double,
        val weeklyUserGrowthMin: Int,
        val weeklyUserGrowthMax: Int,
        val volumeMultiplier: Double,
        val description: String
    )

    data class CryptoServiceConfig(
        val id: String,
        val name: String,
        val devBudgetCost: Double,
        val minDevsRequired: Int,
        val userBoostPercent: Double,
        val priceSurgePercent: Double,
        val description: String
    )

    data class CryptoListingStatus(
        val isEligible: Boolean,
        val hasDev: Boolean,
        val hasSecurity: Boolean,
        val hasWebsite: Boolean,
        val hasUsers: Boolean,
        val hasLiquidity: Boolean,
        val userCount: Int,
        val liquidityAmount: Double
    )

    const val CRYPTO_DEV_SALARY = 500.0
    const val CRYPTO_DEV_HIRE_COST = 1500.0

    val CRYPTO_SECURITY_TIERS = listOf(
        CryptoSecurityTierDetail(1, "Basic Security", 0.0, 50.0, 10.0, "Basic open-source testing. Vulnerable to reentrancy bugs and flash-loan exploits (10% risk/wk)."),
        CryptoSecurityTierDetail(2, "Multi-Sig & Smart Contract Audit", 6000.0, 150.0, 3.5, "Independent security firm audit report and 3-of-5 multi-sig treasury protection (3.5% risk/wk)."),
        CryptoSecurityTierDetail(3, "Formal Verification & Monitoring", 16000.0, 350.0, 1.0, "Mathematical bytecode proof verification and 24/7 mempool front-running shields (1.0% risk/wk)."),
        CryptoSecurityTierDetail(4, "Bug Bounty Fortress & ZK Defense", 38000.0, 800.0, 0.1, "ImmuneFi $500k community bounty pool and cryptographic zero-knowledge multi-party keys (<0.1% risk/wk).")
    )

    val CRYPTO_WEBSITE_APP_TIERS = listOf(
        CryptoWebsiteAppTierDetail(1, "Basic Website", 0.0, 40.0, 800, "Clean landing page with whitepaper, token contract address, and project roadmap. Cap: 800 users."),
        CryptoWebsiteAppTierDetail(2, "Web3 dApp Portal", 8000.0, 120.0, 4000, "Interactive Web3 wallet connect portal for staking, on-chain voting, and live DEX stats. Cap: 4,000 users."),
        CryptoWebsiteAppTierDetail(3, "Mobile iOS & Android App", 24000.0, 320.0, 20000, "Native mobile wallet app with biometric login, push price alerts, and one-tap swaps. Cap: 20,000 users."),
        CryptoWebsiteAppTierDetail(4, "Global Institutional Portal & SDK", 60000.0, 850.0, 100000, "Enterprise API endpoints, high-speed liquidity bridge, and developer ecosystem SDK. Cap: 100,000 users.")
    )

    val CRYPTO_MARKETING_TIERS = listOf(
        CryptoMarketingTierDetail(1, "Grassroots Socials", 150.0, 25, 55, 1.0, "Organic Discord, X (Twitter), and Telegram community management (+25-55 users/wk)."),
        CryptoMarketingTierDetail(2, "Crypto Influencers & KOLs", 600.0, 120, 250, 1.25, "Sponsored YouTube breakdowns, Twitter Spaces, and top Web3 key opinion leaders (+120-250 users/wk, +25% vol)."),
        CryptoMarketingTierDetail(3, "Tier-1 Crypto Media & Viral PR", 1800.0, 400, 900, 1.60, "Coindesk, CoinTelegraph features, trending banners, and viral hackathons (+400-900 users/wk, +60% vol)."),
        CryptoMarketingTierDetail(4, "Global Mainstream Sponsorship", 4800.0, 1500, 3200, 2.40, "Global sports team jerseys, esports arenas, and major stadium billboard takeovers (+1,500-3,200 users/wk, +140% vol).")
    )

    val CRYPTO_AVAILABLE_SERVICES = listOf(
        CryptoServiceConfig("srv_staking", "Token Staking & Yield Vault", 3500.0, 2, 0.25, 0.20, "Enables token holders to lock tokens for yields, reducing circulating supply and surging utility demand."),
        CryptoServiceConfig("srv_dex_agg", "DEX Swap Aggregator", 5000.0, 2, 0.30, 0.22, "Multi-chain router for fast low-fee swaps. Generates 0.3% protocol fee on external swap routes."),
        CryptoServiceConfig("srv_paywall", "Creator Micro-Streaming Paywall", 7500.0, 3, 0.40, 0.28, "Web3 audio streaming gate allowing fans to stream unreleased music by burning micro-fractions of tokens."),
        CryptoServiceConfig("srv_bridge", "Cross-Chain Liquidity Bridge", 12000.0, 4, 0.50, 0.35, "Bi-directional bridge connecting Ethereum, Solana, and Nova Protocol, opening institutional volume.")
    )

    fun checkCryptoListingEligibility(venture: BusinessVenture): CryptoListingStatus {
        val hasDev = venture.cryptoDevelopersCount >= 1
        val hasSecurity = venture.cryptoSecurityTier >= 2
        val hasWebsite = venture.cryptoWebsiteAppTier >= 1
        val hasUsers = venture.cryptoActiveUsers >= 500
        val hasLiquidity = venture.cryptoLiquidityPoolUsd >= 15000.0

        val isEligible = hasDev && hasSecurity && hasWebsite && hasUsers && hasLiquidity
        return CryptoListingStatus(
            isEligible = isEligible,
            hasDev = hasDev,
            hasSecurity = hasSecurity,
            hasWebsite = hasWebsite,
            hasUsers = hasUsers,
            hasLiquidity = hasLiquidity,
            userCount = venture.cryptoActiveUsers,
            liquidityAmount = venture.cryptoLiquidityPoolUsd
        )
    }

    // --- Hotel Realism Catalogs & Staffing ---
    data class HotelTierDetail(
        val tierLevel: Int,
        val tierName: String,
        val setupCost: Double,
        val upgradeCost: Double,
        val startingRooms: Int,
        val maxRooms: Int,
        val baseNightlyRate: Double, // ADR
        val propertyValue: Double,
        val buildingMaintenanceMonthlyPercent: Double,
        val description: String
    )

    val HOTEL_TIER_DETAILS = listOf(
        HotelTierDetail(1, "Boutique Hotel", 500000.0, 0.0, 2, 20, 45.0, 500000.0, 0.010, "Boutique hospitality property with 2 initial suites, expandable up to 20 rooms with individual custom furnishings, dedicated staffing, and luxury amenities.")
    )

    const val HOTEL_ROOM_COST = 6000.0 // Unlock an additional room
    const val HOTEL_HOUSEKEEPER_SALARY = 380.0
    const val HOTEL_FRONT_DESK_SALARY = 450.0
    const val HOTEL_CHEF_SALARY = 650.0
    const val HOTEL_WAITER_SALARY = 350.0
    const val HOTEL_SECURITY_SALARY = 420.0
    const val HOTEL_MAINT_TECH_SALARY = 480.0
    const val HOTEL_CONCIERGE_SALARY = 550.0
    const val HOTEL_MANAGER_SALARY = 800.0

    data class FurnitureCatalogItem(
        val category: String,
        val name: String,
        val cost: Double,
        val qualityTier: String,
        val description: String
    )

    val HOTEL_FURNITURE_CATALOG: List<FurnitureCatalogItem> = listOf(
        // 1. Bed
        FurnitureCatalogItem("Bed", "Standard Twin Bed", 180.0, "Basic", "Sturdy metal frame with basic wooden slats."),
        FurnitureCatalogItem("Bed", "Comfort Queen Bed", 450.0, "Comfort", "Upholstered headboard with solid wood frame."),
        FurnitureCatalogItem("Bed", "Premium King Bed", 900.0, "Premium", "Carved hardwood king frame with designer plush headboard."),
        FurnitureCatalogItem("Bed", "Luxury Canopy King Bed", 1800.0, "Luxury", "Hand-crafted solid oak canopy frame with silk accents."),

        // 2. Mattress
        FurnitureCatalogItem("Mattress", "Standard Foam Mattress", 120.0, "Basic", "High-density polyurethane foam core."),
        FurnitureCatalogItem("Mattress", "Orthopedic Pocket Spring", 300.0, "Comfort", "Individually wrapped pocket coils with gel cooling layer."),
        FurnitureCatalogItem("Mattress", "Memory Foam Deluxe", 650.0, "Premium", "Deep plush memory foam pillow-top with pressure relief."),
        FurnitureCatalogItem("Mattress", "Ultra-Plush Organic Hybrid", 1400.0, "Luxury", "Multi-layered natural latex and cashmere breathable hybrid."),

        // 3. Wardrobe
        FurnitureCatalogItem("Wardrobe", "Open Rail & Shelf Unit", 70.0, "Basic", "Minimalist open clothing rack with 3 wire shelves."),
        FurnitureCatalogItem("Wardrobe", "2-Door Modern Wardrobe", 180.0, "Comfort", "Laminated modern closet with hangers and drawers."),
        FurnitureCatalogItem("Wardrobe", "Hardwood Mirrored Wardrobe", 400.0, "Premium", "Full-length mirrored doors and soft-close solid drawers."),
        FurnitureCatalogItem("Wardrobe", "Custom Built-in Walk-in Closet", 900.0, "Luxury", "Integrated walnut cabinetry with internal LED illumination."),

        // 4. Desk
        FurnitureCatalogItem("Desk", "Compact Writing Desk", 80.0, "Basic", "Simple laminate study desk for laptops."),
        FurnitureCatalogItem("Desk", "Executive Workstation Desk", 200.0, "Comfort", "Ergonomic desk with USB ports and cable management."),
        FurnitureCatalogItem("Desk", "Solid Oak Bureau Desk", 450.0, "Premium", "Crafted solid oak with leather writing blotter."),
        FurnitureCatalogItem("Desk", "Bespoke Marble Executive Desk", 1000.0, "Luxury", "Calacatta marble top with brass detailing and hideaway power."),

        // 5. Chair
        FurnitureCatalogItem("Chair", "Molded Side Chair", 40.0, "Basic", "Durable lightweight side chair."),
        FurnitureCatalogItem("Chair", "Padded Swivel Desk Chair", 110.0, "Comfort", "Cushioned desk chair with lumbar adjustment."),
        FurnitureCatalogItem("Chair", "Ergonomic Leather Chair", 280.0, "Premium", "Supple leather executive chair with reclining tension."),
        FurnitureCatalogItem("Chair", "Designer Velvet Lounge Armchair", 650.0, "Luxury", "Mid-century designer armchair upholstered in jewel-tone velvet."),

        // 6. TV
        FurnitureCatalogItem("TV", "32\" HD Smart TV", 130.0, "Basic", "Compact high-definition screen with streaming apps."),
        FurnitureCatalogItem("TV", "43\" 4K UHD Smart TV", 260.0, "Comfort", "Vibrant 4K screen with digital cable & Chromecast."),
        FurnitureCatalogItem("TV", "55\" OLED Cinema Display", 600.0, "Premium", "Razor-thin OLED display with cinematic soundbar."),
        FurnitureCatalogItem("TV", "70\" Ultra-Thin Suite Theater", 1300.0, "Luxury", "Flagship curved 4K screen with surround-sound array."),

        // 7. Mini fridge
        FurnitureCatalogItem("Mini fridge", "Compact Beverage Cooler", 90.0, "Basic", "Energy-saving counter-top cooler."),
        FurnitureCatalogItem("Mini fridge", "Glass-Front Refreshment Fridge", 220.0, "Comfort", "Illuminated glass door silent cooler with soda rack."),
        FurnitureCatalogItem("Mini fridge", "Silent Compressor Mini Bar", 450.0, "Premium", "Silent absorption cooler stocked with gourmet treats."),
        FurnitureCatalogItem("Mini fridge", "Dual-Zone Wine & Gourmet Bar", 1000.0, "Luxury", "Climate-controlled dual temperature sommelier cooler."),

        // 8. Air conditioner
        FurnitureCatalogItem("Air conditioner", "Compact Window AC", 160.0, "Basic", "Standard rotary climate cooling unit."),
        FurnitureCatalogItem("Air conditioner", "Quiet Split-Inverter AC", 420.0, "Comfort", "Whisper-quiet wall-mounted inverter heat & cool unit."),
        FurnitureCatalogItem("Air conditioner", "Multi-Zone Climate Smart AC", 900.0, "Premium", "Touchscreen programmable smart thermostat & purifier."),
        FurnitureCatalogItem("Air conditioner", "Whisper-Silent PureAir HVAC", 1800.0, "Luxury", "Hospital-grade HEPA filtration with concealed ceiling diffuser."),

        // 9. Curtains
        FurnitureCatalogItem("Curtains", "Standard Pull Blinds", 40.0, "Basic", "Simple synthetic Venetian blinds."),
        FurnitureCatalogItem("Curtains", "Blackout Fabric Drapes", 110.0, "Comfort", "Thermal insulated light-blocking hotel drapes."),
        FurnitureCatalogItem("Curtains", "Triple-Weave Acoustic Blackout", 240.0, "Premium", "Sound-dampening heavy velvet triple-weave curtains."),
        FurnitureCatalogItem("Curtains", "Motorized Smart Silk Blackout", 550.0, "Luxury", "Remote-controlled motorized silk and linen drapery."),

        // 10. Lighting
        FurnitureCatalogItem("Lighting", "Ceiling Fixture & Desk Lamp", 40.0, "Basic", "Standard incandescent and compact fluorescent set."),
        FurnitureCatalogItem("Lighting", "Warm LED Ambient Sconces", 100.0, "Comfort", "Warm architectural sconces with dimming bedside lamps."),
        FurnitureCatalogItem("Lighting", "Dimmable Multi-Scene Array", 220.0, "Premium", "Modern recessed LED array with smart mood scenes."),
        FurnitureCatalogItem("Lighting", "Artisan Crystal & Brass Chandelier", 600.0, "Luxury", "Hand-blown glass chandelier with bedside master touch control."),

        // 11. Bathroom fixtures
        FurnitureCatalogItem("Bathroom fixtures", "Standard Porcelain Shower & Sink", 200.0, "Basic", "Standard chrome faucet, shower stall, and pedestal sink."),
        FurnitureCatalogItem("Bathroom fixtures", "Modern Vanity & Rainfall Shower", 500.0, "Comfort", "Stone vanity with walk-in glass rainfall shower."),
        FurnitureCatalogItem("Bathroom fixtures", "Granite Vanity & Soaking Tub", 1200.0, "Premium", "Polished granite double vanity with deep soaking ceramic tub."),
        FurnitureCatalogItem("Bathroom fixtures", "Italian Marble & Jacuzzi Spa Suite", 2600.0, "Luxury", "Imported Carrara marble, heated towel rails, and hydrotherapy jacuzzi tub."),

        // 12. Decoration
        FurnitureCatalogItem("Decoration", "Framed City Posters & Mirror", 50.0, "Basic", "Simple framed photographic prints and wall mirror."),
        FurnitureCatalogItem("Decoration", "Curated Canvas Art & Accent Rug", 140.0, "Comfort", "Original local canvas paintings with geometric wool rug."),
        FurnitureCatalogItem("Decoration", "Gallery Sculpture & Silk Wallpaper", 350.0, "Premium", "Sculptural lighting, designer mirror, and woven wallpaper."),
        FurnitureCatalogItem("Decoration", "Museum-Grade Art & Hand-Tufted Rugs", 900.0, "Luxury", "Original signed artist works and Persian hand-tufted carpets."),

        // 13. Wi-Fi equipment
        FurnitureCatalogItem("Wi-Fi equipment", "Standard 2.4GHz Router", 40.0, "Basic", "Standard wireless router with basic coverage."),
        FurnitureCatalogItem("Wi-Fi equipment", "Dual-Band Fast Mesh Node", 100.0, "Comfort", "Seamless dual-band node providing reliable 150 Mbps."),
        FurnitureCatalogItem("Wi-Fi equipment", "Wi-Fi 6 Dedicated In-Room AP", 220.0, "Premium", "Dedicated low-latency Wi-Fi 6 access point for 4K streaming."),
        FurnitureCatalogItem("Wi-Fi equipment", "Wi-Fi 7 Multi-Gigabit Fiber Node", 550.0, "Luxury", "Next-gen ultra-secure multi-gigabit private wireless hub.")
    )

    fun getDefaultHotelRooms(): List<HotelRoom> {
        val basicFurnishings = HOTEL_FURNITURE_CATALOG.filter { it.qualityTier == "Basic" }.map {
            HotelFurnitureItem(it.category, it.name, it.cost, it.qualityTier)
        }
        return (1..20).map { roomNum ->
            val isUnlocked = roomNum <= 2
            HotelRoom(
                roomNumber = roomNum,
                isUnlocked = isUnlocked,
                condition = 1.0f,
                isOutOfOrder = false,
                furnishings = if (isUnlocked) basicFurnishings else emptyList(),
                weeklyOccupancyRate = if (isUnlocked) 0.65f else 0.0f,
                lodgesThisWeek = if (isUnlocked) 5 else 0,
                weeklyRevenue = if (isUnlocked) 182.0 * 0.65 else 0.0
            )
        }
    }

    fun getDefaultHotelAmenities(): List<HotelAmenity> = listOf(
        HotelAmenity("amen_restaurant", "Restaurant", 15000.0, 200.0, false, 0.06f, 15.0, 1800.0, 1, "Food revenue with hired Chef & Waiter + 6% hotel appeal."),
        HotelAmenity("amen_pool", "Swimming Pool", 20000.0, 250.0, false, 0.08f, 20.0, 1000.0, 1, "Direct leisure revenue ($1,000/wk) + 8% potential occupancy boost."),
        HotelAmenity("amen_cinema", "Cinema", 25000.0, 300.0, false, 0.05f, 15.0, 1500.0, 1, "Direct ticket revenue ($1,500/wk) + 5% hotel appeal."),
        HotelAmenity("amen_gym", "Gym", 15000.0, 180.0, false, 0.06f, 10.0, 600.0, 1, "Direct fitness revenue ($600/wk) + 6% occupancy boost."),
        HotelAmenity("amen_event_hall", "Event Hall", 40000.0, 450.0, false, 0.05f, 30.0, 3500.0, 1, "Direct event & wedding revenue ($3,500/wk) + 5% prestige appeal."),
        HotelAmenity("amen_spa", "Spa", 30000.0, 350.0, false, 0.07f, 25.0, 2200.0, 1, "Direct wellness revenue ($2,200/wk) + premium high-paying customer pull."),
        HotelAmenity("amen_conference", "Conference Room", 25000.0, 280.0, false, 0.06f, 20.0, 2000.0, 1, "Direct corporate bookings ($2,000/wk) + business traveler occupancy."),
        HotelAmenity("amen_parking", "Parking", 10000.0, 100.0, false, 0.04f, 10.0, 800.0, 1, "Direct parking fee revenue ($800/wk) + guest capacity & satisfaction.")
    )

    // --- Crypto Staff & Protocol Salaries ---
    const val CRYPTO_MOD_SALARY = 250.0
    const val CRYPTO_AUDITOR_SALARY = 1200.0
    const val CRYPTO_MARKETER_SALARY = 650.0

    fun calculateNetAssetValue(venture: BusinessVenture): Double {
        var nav = venture.accumulatedProfit.coerceAtLeast(0.0) - venture.outstandingLoan
        when (venture.type) {
            "Farming" -> {
                if (venture.farmLandIsOwned) {
                    nav += venture.farmLandValue
                }
                nav += venture.farmMachinery.filter { it.isOwned }.sumOf { it.bookValue.coerceAtLeast(it.purchaseCost * 0.3) }
                nav += venture.farmInventory.sumOf { it.quantity * it.marketPricePerUnit }
                nav += venture.farmLivestock.sumOf { it.count * it.purchaseCostPerUnit * 0.85 }
                nav += when (venture.tierLevel) {
                    2 -> 15000.0
                    3 -> 45000.0
                    4 -> 120000.0
                    else -> 5000.0
                }
            }
            "Taxi Fleet" -> {
                val carVal = venture.taxiCarCount.coerceAtLeast(1) * TAXI_CAR_COST * venture.taxiCondition.coerceAtLeast(0.3f)
                val depotVal = when (venture.taxiDepotLevel) {
                    2 -> 8000.0
                    3 -> 26000.0
                    4 -> 61000.0
                    else -> 4000.0
                }
                nav += carVal + depotVal
            }
            "Restaurant" -> {
                val tierDetail = RESTAURANT_TIER_DETAILS.find { it.tierLevel == venture.tierLevel } ?: RESTAURANT_TIER_DETAILS.first()
                val propVal = tierDetail.propertyValue
                val eqVal = venture.equipmentList.filter { it.isOwned }.sumOf { it.price * it.condition.coerceAtLeast(0.3f) }
                nav += propVal + eqVal
            }
            "Hotel", "Hotel Chain" -> {
                // Base land and building: $500,000
                var hotelNav = 500000.0
                val rooms = if (venture.hotelRooms.isEmpty()) getDefaultHotelRooms() else venture.hotelRooms
                val unlocked = rooms.filter { it.isUnlocked }
                // Unlocked room structures beyond the 2 initial rooms ($6,000 each)
                val extraRooms = (unlocked.size - 2).coerceAtLeast(0)
                hotelNav += extraRooms * HOTEL_ROOM_COST
                // Furniture across all rooms (at 90% book value)
                val totalFurnitureVal = unlocked.sumOf { it.totalFurnishingInvestment }
                hotelNav += totalFurnitureVal * 0.90
                // Owned amenities (at 85% book value)
                val amenitiesVal = venture.hotelAmenities.filter { it.isOwned }.sumOf { it.purchaseCost * 0.85 }
                hotelNav += amenitiesVal
                nav += hotelNav
            }
            "Crypto Token" -> {
                val lpValue = venture.cryptoLiquidityPoolUsd
                val stakedReserve = (venture.cryptoStakedTokens * venture.cryptoTokenPrice * 0.40)
                nav += lpValue + stakedReserve
            }
            else -> {
                nav += venture.totalCapitalInvested * 0.70
            }
        }
        return nav.coerceAtLeast(5000.0)
    }

    fun getGrossEnterpriseValuation(venture: BusinessVenture): Double {
        // No multiplier: total all-time profits (withdrawn + in business account) + staff salaries and other expenses + every penny spent on the business
        val allTimeProfits = (venture.totalWithdrawn + venture.accumulatedProfit).coerceAtLeast(0.0)
        val allExpenses = venture.totalExpensesPaid.coerceAtLeast(0.0)
        val allPenniesSpent = (venture.setupCost + venture.totalCapitalInvested).coerceAtLeast(0.0)
        val totalValuation = allTimeProfits + allExpenses + allPenniesSpent
        return if (totalValuation > 0.0) totalValuation else venture.setupCost
    }

    fun getEnterpriseValuation(venture: BusinessVenture): Double {
        val grossValuation = getGrossEnterpriseValuation(venture)
        // Business loans reduce company value until repaid:
        val netValuation = grossValuation - venture.outstandingLoan
        return netValuation.coerceAtLeast(venture.setupCost * 0.5)
    }

    fun createVenture(type: String, customName: String? = null): BusinessVenture {
        val id = "biz_" + UUID.randomUUID().toString().take(6)
        return when (type) {
            "Taxi Fleet" -> {
                val gross = TAXI_BASE_WEEKLY_GROSS
                val exp = TAXI_DRIVER_SALARY + TAXI_BASE_WEEKLY_FUEL + TAXI_BASE_WEEKLY_MAINT
                BusinessVenture(
                    id = id,
                    name = customName ?: "City Fleet Taxis",
                    type = type,
                    setupCost = TAXI_STARTING_COST,
                    isOwned = true,
                    unitCount = 1,
                    tierLevel = 1,
                    tierName = "Urban Taxi Cab (1 Vehicle)",
                    weeklyGrossRevenue = gross,
                    weeklyOperatingExpenses = exp,
                    weeklyNetProfit = gross - exp,
                    totalCapitalInvested = TAXI_STARTING_COST,
                    history = listOf(gross - exp),
                    taxi = VentureTaxiState(
                        taxiCarCount = 1,
                        taxiDriverCount = 1,
                        taxiCondition = 1.0f,
                        taxiOdometerMiles = 0.0,
                        taxiWeeklyTripsCompleted = 65,
                        taxiWeeklyFaresEarned = gross,
                        taxiWeeklyFuelCost = TAXI_BASE_WEEKLY_FUEL,
                        taxiWeeklyDriverSalary = TAXI_DRIVER_SALARY,
                        taxiWeeklyMaintenanceCost = TAXI_BASE_WEEKLY_MAINT
                    )
                )
            }
            "Hotel", "Hotel Chain" -> {
                val rooms = getDefaultHotelRooms()
                val unlockedRooms = rooms.filter { it.isUnlocked }
                val initialRooms = unlockedRooms.size // 2
                val gross = unlockedRooms.sumOf { it.potentialWeeklyRate * 0.65 }
                val payroll = HOTEL_HOUSEKEEPER_SALARY + HOTEL_FRONT_DESK_SALARY
                val utilities = initialRooms * 35.0
                val supplies = gross * 0.09
                val bldgMaint = 450.0
                val exp = payroll + utilities + supplies + bldgMaint
                BusinessVenture(
                    id = id,
                    name = customName ?: "Grand Vista Boutique Hotel",
                    type = "Hotel",
                    setupCost = 500000.0,
                    isOwned = true,
                    unitCount = initialRooms,
                    tierLevel = 1,
                    tierName = "Boutique Hotel (2 / 20 Rooms)",
                    weeklyGrossRevenue = gross,
                    weeklyOperatingExpenses = exp,
                    weeklyNetProfit = gross - exp,
                    totalCapitalInvested = 500000.0,
                    history = listOf(gross - exp),
                    hotel = VentureHotelState(
                        hotelRoomCount = initialRooms,
                        hotelMaxRoomCapacity = 20,
                        hotelNightlyRate = 45.0,
                        hotelOccupancyRate = 0.65f,
                        hotelCondition = 1.0f,
                        hotelCleanliness = 0.95f,
                        hotelReputation = 0.85f,
                        hotelHousekeeperCount = 1,
                        hotelFrontDeskCount = 1,
                        hotelChefCount = 0,
                        hotelWaiterCount = 0,
                        hotelSecurityCount = 0,
                        hotelMaintenanceTechCount = 0,
                        hotelConciergeCount = 0,
                        hotelGeneralManagerCount = 0,
                        hotelAmenities = getDefaultHotelAmenities(),
                        hotelRooms = rooms,
                        hotelTotalLodgesThisWeek = 9,
                        hotelWeeklyRoomRevenue = gross,
                        hotelWeeklyAmenityRevenue = 0.0,
                        hotelWeeklyFoodRevenue = 0.0,
                        hotelWeeklyPayrollCost = payroll,
                        hotelWeeklySuppliesCost = supplies,
                        hotelWeeklyUtilityCost = utilities,
                        hotelWeeklyMaintenanceCost = bldgMaint,
                        hotelSeasonText = "Peak Spring Vacation (+10% Occupancy)"
                    )
                )
            }
            "Restaurant" -> {
                val tier = RESTAURANT_TIER_DETAILS.first()
                val foodSales = 1400.0
                val ingredients = foodSales * 0.30
                val payroll = (1 * CHEF_WEEKLY_SALARY) + (1 * WAITER_WEEKLY_SALARY)
                val bldgMaint = (tier.propertyValue * tier.buildingMaintenanceMonthlyPercent) / 4.33
                val exp = ingredients + payroll + bldgMaint
                BusinessVenture(
                    id = id,
                    name = customName ?: "The Artisan Bistro",
                    type = type,
                    setupCost = tier.setupCost,
                    isOwned = true,
                    unitCount = 1,
                    tierLevel = tier.tierLevel,
                    tierName = tier.name,
                    weeklyGrossRevenue = foodSales,
                    weeklyOperatingExpenses = exp,
                    weeklyNetProfit = foodSales - exp,
                    totalCapitalInvested = tier.setupCost,
                    history = listOf(foodSales - exp),
                    restaurant = VentureRestaurantState(
                        waiterCount = 1,
                        chefCount = 1,
                        seniorChefCount = 0,
                        cleanerCount = 0,
                        managerCount = 0,
                        weeklyCustomersServed = 50,
                        weeklyFoodSales = foodSales,
                        weeklyIngredientCost = ingredients,
                        weeklyBuildingMaintenanceCost = bldgMaint,
                        weeklyStaffPayroll = payroll,
                        weeklyEquipmentMaintenanceCost = 0.0,
                        equipmentList = getDefaultRestaurantEquipment(),
                        serviceEfficiency = 1.0f
                    )
                )
            }
            "Farming" -> {
                val tier = FARMING_TIERS.first()
                val initialMachines = getDefaultFarmMachinery()
                val initialLivestock = getDefaultFarmLivestock()
                val initialBatches = listOf(
                    FarmCropBatch(
                        id = "batch_" + UUID.randomUUID().toString().take(6),
                        cropName = "Maize",
                        acres = 3.0,
                        seedCost = 450.0,
                        totalWeeks = 12,
                        currentWeek = 0,
                        isReadyToHarvest = false,
                        expectedYieldUnits = 300.0,
                        yieldUnitName = "bushels"
                    ),
                    FarmCropBatch(
                        id = "batch_" + UUID.randomUUID().toString().take(6),
                        cropName = "Tomatoes",
                        acres = 2.0,
                        seedCost = 600.0,
                        totalWeeks = 10,
                        currentWeek = 0,
                        isReadyToHarvest = false,
                        expectedYieldUnits = 180.0,
                        yieldUnitName = "crates"
                    )
                )
                val initialInventory = emptyList<FarmInventoryItem>()
                val gross = 0.0
                val exp = FARMHAND_WEEKLY_SALARY + 150.0
                BusinessVenture(
                    id = id,
                    name = customName ?: "Green Valley Agro Farm",
                    type = type,
                    setupCost = tier.setupCost,
                    isOwned = true,
                    unitCount = 1,
                    tierLevel = tier.tierLevel,
                    tierName = tier.tierName,
                    weeklyGrossRevenue = gross,
                    weeklyOperatingExpenses = exp,
                    weeklyNetProfit = gross - exp,
                    totalCapitalInvested = tier.setupCost,
                    history = listOf(gross - exp),
                    farm = VentureFarmState(
                        farmLandTier = "SMALL_PLOT",
                        farmLandIsOwned = true,
                        farmLandAcres = 10.0,
                        farmLandValue = 8000.0,
                        farmLandRentWeekly = 0.0,
                        farmCropBatches = initialBatches,
                        farmLivestock = initialLivestock,
                        farmMachinery = initialMachines,
                        farmHandCount = 1,
                        farmTractorOperatorCount = 0,
                        farmLivestockWorkerCount = 0,
                        farmDriverCount = 0,
                        farmVeterinarianCount = 0,
                        farmManagerCount = 0,
                        farmInventory = initialInventory,
                        farmWeeklyRawCropsRevenue = gross,
                        farmWeeklyFruitsRevenue = 0.0,
                        farmWeeklyLivestockRevenue = 0.0,
                        farmWeeklyProcessedRevenue = 0.0,
                        farmWeeklyFeedExpense = 0.0,
                        farmWeeklyLandRentExpense = 0.0,
                        farmWeeklyMachineUpkeepExpense = 8.0,
                        farmWeeklyPayrollExpense = FARMHAND_WEEKLY_SALARY,
                        farmWeeklySeedExpense = 100.0,
                        farmLastWeatherEvent = "Optimal Sunshine & Warm Season (+15% Yield)"
                    )
                )
            }
            "Crypto Token" -> {
                val tier = CRYPTO_TOKEN_TIERS.first()
                val ticker = if (customName != null && customName.contains("(") && customName.contains(")")) {
                    customName.substringAfter("(").substringBefore(")").trim().uppercase()
                } else if (customName != null && customName.isNotBlank()) {
                    customName.filter { it.isLetter() }.take(4).uppercase().ifBlank { "NOVA" }
                } else {
                    "NOVA"
                }
                val tokenName = customName?.substringBefore("(")?.trim()?.ifBlank { "Nova Protocol" } ?: "Nova Protocol"

                val startupVol = 40000.0
                val swapFees = startupVol * 0.003 // $120
                val activeUsers = 200
                val utilityFees = activeUsers * 4.50 // $900
                val gross = swapFees + utilityFees // $1,020 (right in $800–$1,200)

                val payroll = CRYPTO_DEV_SALARY // $500 (1 dev)
                val marketing = 150.0 // Tier 1
                val webHosting = 40.0 // Tier 1
                val security = 50.0 // Tier 1
                val exp = payroll + marketing + webHosting + security // $740

                BusinessVenture(
                    id = id,
                    name = "$tokenName ($ticker)",
                    type = type,
                    setupCost = 1000000.0,
                    isOwned = true,
                    unitCount = 1,
                    tierLevel = tier.tierLevel,
                    tierName = tier.tierName,
                    weeklyGrossRevenue = gross,
                    weeklyOperatingExpenses = exp,
                    weeklyNetProfit = gross - exp,
                    totalCapitalInvested = 1000000.0,
                    history = listOf(gross - exp),
                    crypto = VentureCryptoState(
                        cryptoTotalSupply = 20_000_000.0,
                        cryptoTokenPrice = 0.05,
                        cryptoLiquidityPoolUsd = 75_000.0,
                        cryptoWeeklyTradingVolume = startupVol,
                        cryptoStakedTokens = 1_000_000.0,
                        cryptoCommunitySentiment = 0.75f,
                        cryptoMarketCycle = "Bullish Accumulation",
                        cryptoModCount = 1,
                        cryptoSolidityDevCount = 1,
                        cryptoSecurityAuditorCount = 0,
                        cryptoMarketerCount = 0,
                        cryptoWeeklySwapFeesRevenue = swapFees,
                        cryptoWeeklyStakingFeesRevenue = 0.0,
                        cryptoWeeklyDeFiYieldRevenue = 0.0,
                        cryptoWeeklyServerInfraCost = webHosting,
                        cryptoWeeklyPayrollCost = payroll,
                        cryptoWeeklySecurityCost = security,

                        // 5 Major Business Elements & Market Listing
                        cryptoTokenTicker = ticker,
                        cryptoTokenName = tokenName,
                        cryptoCirculatingSupply = 5_000_000.0,
                        cryptoActiveUsers = activeUsers,
                        cryptoDevelopersCount = 1,
                        cryptoSecurityTier = 1,
                        cryptoWebsiteAppTier = 1,
                        cryptoMarketingTier = 1,
                        cryptoIsMarketListed = false,
                        cryptoMarketListingNotice = "Requires 1+ Devs, Security Tier 2+, 500+ Users, and $15,000+ Liquidity to list on Cryptocurrency Market.",
                        cryptoFounderTokensOwned = 2_000_000.0,
                        cryptoWeeklyProtocolFeeRevenue = utilityFees,
                        cryptoEducationalLesson = "Operating as an early startup. Grow active users and upgrade security to meet Cryptocurrency Market listing requirements."
                    )
                )
            }
            else -> {
                val gross = 1800.0
                val exp = 1100.0
                BusinessVenture(
                    id = id,
                    name = customName ?: "Enterprise Venture",
                    type = type,
                    setupCost = 10000.0,
                    isOwned = true,
                    unitCount = 1,
                    tierLevel = 1,
                    tierName = "Standard Enterprise",
                    weeklyGrossRevenue = gross,
                    weeklyOperatingExpenses = exp,
                    weeklyNetProfit = gross - exp,
                    totalCapitalInvested = 10000.0,
                    history = listOf(gross - exp)
                )
            }
        }
    }

    fun getUpgradeInfo(venture: BusinessVenture): VentureUpgradeInfo? {
        return when (venture.type) {
            "Taxi Fleet" -> {
                val currentLevel = venture.taxiDepotLevel
                if (currentLevel >= 4) {
                    val nextCapacity = venture.taxiDepotCapacity + 10
                    VentureUpgradeInfo(
                        title = "Expand Garage & Fleet Capacity (+10 Cars)",
                        description = "Construct an additional vehicle dispatch deck and commercial parking lot. Expands total fleet capacity from ${venture.taxiDepotCapacity} to $nextCapacity cars, allowing you to acquire more taxis.",
                        cost = 50000.0,
                        currentWeeklyGross = venture.weeklyGrossRevenue,
                        nextWeeklyGross = venture.weeklyGrossRevenue,
                        currentWeeklyExpenses = venture.weeklyOperatingExpenses,
                        nextWeeklyExpenses = venture.weeklyOperatingExpenses,
                        currentNetProfit = venture.weeklyNetProfit,
                        nextNetProfit = venture.weeklyNetProfit,
                        isMaxed = false
                    )
                } else {
                    val currentTier = TAXI_DEPOT_TIERS.find { it.level == currentLevel.coerceIn(1, 4) } ?: TAXI_DEPOT_TIERS.first()
                    val nextTier = TAXI_DEPOT_TIERS.find { it.level == currentLevel + 1 }
                    VentureUpgradeInfo(
                        title = "Expand Facility: ${nextTier!!.name}",
                        description = "Acquire larger commercial staging space (${nextTier.description}). Expands fleet parking capacity from ${currentTier.maxCars} up to ${nextTier.maxCars} vehicles.",
                        cost = nextTier.upgradeCost,
                        currentWeeklyGross = venture.weeklyGrossRevenue,
                        nextWeeklyGross = venture.weeklyGrossRevenue,
                        currentWeeklyExpenses = venture.weeklyOperatingExpenses,
                        nextWeeklyExpenses = venture.weeklyOperatingExpenses,
                        currentNetProfit = venture.weeklyNetProfit,
                        nextNetProfit = venture.weeklyNetProfit,
                        isMaxed = false
                    )
                }
            }
            "Hotel", "Hotel Chain" -> {
                if (venture.tierLevel >= 4) {
                    val nextCapacity = venture.hotelMaxRoomCapacity + 10
                    VentureUpgradeInfo(
                        title = "Construct Penthouse Sky Suites (+10 Rooms)",
                        description = "Add 10 luxury penthouse suites to the property. Expands max room capacity from ${venture.hotelMaxRoomCapacity} to $nextCapacity rooms.",
                        cost = 100000.0,
                        currentWeeklyGross = venture.weeklyGrossRevenue,
                        nextWeeklyGross = venture.weeklyGrossRevenue * 1.25,
                        currentWeeklyExpenses = venture.weeklyOperatingExpenses,
                        nextWeeklyExpenses = venture.weeklyOperatingExpenses * 1.15,
                        currentNetProfit = venture.weeklyNetProfit,
                        nextNetProfit = (venture.weeklyGrossRevenue * 1.25) - (venture.weeklyOperatingExpenses * 1.15),
                        isMaxed = false
                    )
                } else {
                    val currentTier = HOTEL_TIER_DETAILS.find { it.tierLevel == venture.tierLevel } ?: HOTEL_TIER_DETAILS.first()
                    val nextTier = HOTEL_TIER_DETAILS.find { it.tierLevel == venture.tierLevel + 1 }
                    val curGross = venture.weeklyGrossRevenue
                    val nextGross = if (nextTier != null) nextTier.startingRooms * 7.0 * nextTier.baseNightlyRate * 0.70 else curGross
                    val curExp = venture.weeklyOperatingExpenses
                    val nextExp = if (nextTier != null) curExp * 1.5 else curExp
                    VentureUpgradeInfo(
                        title = "Upgrade to ${nextTier!!.tierName}",
                        description = "${nextTier.description} (Max Room Capacity: ${nextTier.maxRooms} rooms. Base ADR: $${nextTier.baseNightlyRate.toInt()}/night. Property Value: $${String.format("%,.0f", nextTier.propertyValue)}).",
                        cost = nextTier.upgradeCost,
                        currentWeeklyGross = curGross,
                        nextWeeklyGross = nextGross,
                        currentWeeklyExpenses = curExp,
                        nextWeeklyExpenses = nextExp,
                        currentNetProfit = curGross - curExp,
                        nextNetProfit = nextGross - nextExp,
                        isMaxed = false
                    )
                }
            }
            "Restaurant" -> {
                if (venture.tierLevel >= 4) {
                    val currentSeats = if (venture.unitCount > 0) venture.unitCount else 100
                    val nextSeats = currentSeats + 50
                    VentureUpgradeInfo(
                        title = "Expand Dining Room & VIP Suites (+50 Capacity)",
                        description = "Construct private VIP dining rooms and outdoor terrace dining. Expands customer seating space from $currentSeats to $nextSeats diners.",
                        cost = 65000.0,
                        currentWeeklyGross = venture.weeklyGrossRevenue,
                        nextWeeklyGross = venture.weeklyGrossRevenue * 1.25,
                        currentWeeklyExpenses = venture.weeklyOperatingExpenses,
                        nextWeeklyExpenses = venture.weeklyOperatingExpenses * 1.15,
                        currentNetProfit = venture.weeklyNetProfit,
                        nextNetProfit = (venture.weeklyGrossRevenue * 1.25) - (venture.weeklyOperatingExpenses * 1.15),
                        isMaxed = false
                    )
                } else {
                    val currentTier = RESTAURANT_TIER_DETAILS.find { it.tierLevel == venture.tierLevel } ?: RESTAURANT_TIER_DETAILS.first()
                    val nextTier = RESTAURANT_TIER_DETAILS.find { it.tierLevel == venture.tierLevel + 1 }
                    val curGross = venture.weeklyGrossRevenue
                    val nextGross = if (nextTier != null) (nextTier.minDailyRevenue + nextTier.maxDailyRevenue) / 2.0 * 7.0 else curGross
                    val curExp = venture.weeklyOperatingExpenses
                    val nextExp = if (nextTier != null) nextGross * 0.30 + (nextTier.minChefs * CHEF_WEEKLY_SALARY + nextTier.minWaiters * WAITER_WEEKLY_SALARY) + ((nextTier.propertyValue * nextTier.buildingMaintenanceMonthlyPercent) / 4.33) else curExp
                    VentureUpgradeInfo(
                        title = "Upgrade to ${nextTier!!.name}",
                        description = "${nextTier.description} (Capacity: ${nextTier.minDailyCapacity}-${nextTier.maxDailyCapacity}/day. Expected Staff: ${nextTier.minChefs} chefs + ${nextTier.minWaiters} waiters).",
                        cost = nextTier.upgradeCost,
                        currentWeeklyGross = curGross,
                        nextWeeklyGross = nextGross,
                        currentWeeklyExpenses = curExp,
                        nextWeeklyExpenses = nextExp,
                        currentNetProfit = curGross - curExp,
                        nextNetProfit = nextGross - nextExp,
                        isMaxed = false
                    )
                }
            }
            "Farming" -> {
                if (venture.tierLevel >= 4) {
                    VentureUpgradeInfo(
                        title = "Expand Processing Plant & Grain Silos",
                        description = "Install automated continuous grain elevators and bulk packaging conveyor systems. Boosts weekly processed goods capacity.",
                        cost = 80000.0,
                        currentWeeklyGross = venture.weeklyGrossRevenue,
                        nextWeeklyGross = venture.weeklyGrossRevenue * 1.20,
                        currentWeeklyExpenses = venture.weeklyOperatingExpenses,
                        nextWeeklyExpenses = venture.weeklyOperatingExpenses * 1.10,
                        currentNetProfit = venture.weeklyNetProfit,
                        nextNetProfit = (venture.weeklyGrossRevenue * 1.20) - (venture.weeklyOperatingExpenses * 1.10),
                        isMaxed = false
                    )
                } else {
                    val currentTier = FARMING_TIERS.find { it.tierLevel == venture.tierLevel } ?: FARMING_TIERS.first()
                    val nextTier = FARMING_TIERS.find { it.tierLevel == venture.tierLevel + 1 }
                    VentureUpgradeInfo(
                        title = "Expand to ${nextTier!!.tierName}",
                        description = nextTier.description,
                        cost = nextTier.upgradeCost,
                        currentWeeklyGross = currentTier.baseWeeklyGross,
                        nextWeeklyGross = nextTier.baseWeeklyGross,
                        currentWeeklyExpenses = currentTier.baseWeeklyExpenses,
                        nextWeeklyExpenses = nextTier.baseWeeklyExpenses,
                        currentNetProfit = currentTier.baseWeeklyGross - currentTier.baseWeeklyExpenses,
                        nextNetProfit = nextTier.baseWeeklyGross - nextTier.baseWeeklyExpenses,
                        isMaxed = false
                    )
                }
            }
            "Crypto Token" -> {
                val currentTier = CRYPTO_TOKEN_TIERS.find { it.tierLevel == venture.tierLevel } ?: CRYPTO_TOKEN_TIERS.first()
                val nextTier = CRYPTO_TOKEN_TIERS.find { it.tierLevel == venture.tierLevel + 1 }
                val isMaxed = nextTier == null
                VentureUpgradeInfo(
                    title = if (isMaxed) "Network Sovereignty (${currentTier.tierName})" else "Upgrade to ${nextTier!!.tierName}",
                    description = if (isMaxed) "Decentralized L2 network powering hundreds of thousands of daily creative web3 transactions." else nextTier!!.description,
                    cost = nextTier?.upgradeCost ?: 0.0,
                    currentWeeklyGross = currentTier.baseWeeklyGross,
                    nextWeeklyGross = nextTier?.baseWeeklyGross ?: currentTier.baseWeeklyGross,
                    currentWeeklyExpenses = currentTier.baseWeeklyExpenses,
                    nextWeeklyExpenses = nextTier?.baseWeeklyExpenses ?: currentTier.baseWeeklyExpenses,
                    currentNetProfit = currentTier.baseWeeklyGross - currentTier.baseWeeklyExpenses,
                    nextNetProfit = (nextTier?.baseWeeklyGross ?: currentTier.baseWeeklyGross) - (nextTier?.baseWeeklyExpenses ?: currentTier.baseWeeklyExpenses),
                    isMaxed = isMaxed
                )
            }
            else -> null
        }
    }

    fun performUpgrade(venture: BusinessVenture): BusinessVenture {
        return when (venture.type) {
            "Taxi Fleet" -> {
                if (venture.taxiDepotLevel >= 4) {
                    val newCap = venture.taxiDepotCapacity + 10
                    venture.copy(
                        tierName = "Expanded Dispatch Terminal (${venture.taxiCarCount}/$newCap Cars)",
                        totalCapitalInvested = venture.totalCapitalInvested + 50000.0
                    ).copyTaxi(
                        taxiDepotLevel = venture.taxiDepotLevel + 1,
                        taxiDepotCapacity = newCap
                    )
                } else {
                    val nextLevel = (venture.taxiDepotLevel + 1).coerceAtMost(4)
                    val nextDepot = TAXI_DEPOT_TIERS.find { it.level == nextLevel } ?: return venture
                    venture.copy(
                        tierName = "Taxi Fleet (${venture.taxiCarCount}/${nextDepot.maxCars} Cars - ${nextDepot.name})",
                        totalCapitalInvested = venture.totalCapitalInvested + nextDepot.upgradeCost
                    ).copyTaxi(
                        taxiDepotLevel = nextDepot.level,
                        taxiDepotCapacity = nextDepot.maxCars
                    )
                }
            }
            "Hotel", "Hotel Chain" -> {
                if (venture.tierLevel >= 4) {
                    val newRooms = venture.hotelMaxRoomCapacity + 10
                    venture.copy(
                        totalCapitalInvested = venture.totalCapitalInvested + 100000.0
                    ).copyHotel(
                        hotelMaxRoomCapacity = newRooms
                    )
                } else {
                    val nextTier = HOTEL_TIER_DETAILS.find { it.tierLevel == venture.tierLevel + 1 } ?: return venture
                    val updatedAmenities = if (venture.hotelAmenities.isEmpty()) getDefaultHotelAmenities() else venture.hotelAmenities
                    val newRooms = venture.hotelRoomCount.coerceAtLeast(nextTier.startingRooms)
                    venture.copy(
                        tierLevel = nextTier.tierLevel,
                        tierName = "${nextTier.tierName} ($newRooms/${nextTier.maxRooms} Rooms)",
                        unitCount = newRooms,
                        totalCapitalInvested = venture.totalCapitalInvested + nextTier.upgradeCost
                    ).copyHotel(
                        hotelMaxRoomCapacity = nextTier.maxRooms,
                        hotelRoomCount = newRooms,
                        hotelNightlyRate = nextTier.baseNightlyRate,
                        hotelAmenities = updatedAmenities
                    )
                }
            }
            "Restaurant" -> {
                if (venture.tierLevel >= 4) {
                    val currentSeats = if (venture.unitCount > 0) venture.unitCount else 100
                    venture.copy(
                        unitCount = currentSeats + 50,
                        totalCapitalInvested = venture.totalCapitalInvested + 65000.0
                    )
                } else {
                    val nextTier = RESTAURANT_TIER_DETAILS.find { it.tierLevel == venture.tierLevel + 1 } ?: return venture
                    val updatedEquip = if (venture.equipmentList.isEmpty()) getDefaultRestaurantEquipment() else venture.equipmentList
                    venture.copy(
                        tierLevel = nextTier.tierLevel,
                        tierName = nextTier.name,
                        totalCapitalInvested = venture.totalCapitalInvested + nextTier.upgradeCost
                    ).copyRestaurant(
                        equipmentList = updatedEquip
                    )
                }
            }
            "Farming" -> {
                if (venture.tierLevel >= 4) {
                    venture.copy(
                        totalCapitalInvested = venture.totalCapitalInvested + 80000.0
                    )
                } else {
                    val nextTier = FARMING_TIERS.find { it.tierLevel == venture.tierLevel + 1 } ?: return venture
                    val updatedLivestock = if (nextTier.tierLevel >= 3 && venture.farmLivestock.isEmpty()) getDefaultFarmLivestock() else venture.farmLivestock
                    venture.copy(
                        tierLevel = nextTier.tierLevel,
                        tierName = nextTier.tierName,
                        weeklyGrossRevenue = nextTier.baseWeeklyGross,
                        weeklyOperatingExpenses = nextTier.baseWeeklyExpenses,
                        weeklyNetProfit = nextTier.baseWeeklyGross - nextTier.baseWeeklyExpenses,
                        totalCapitalInvested = venture.totalCapitalInvested + nextTier.upgradeCost
                    ).copyFarm(
                        farmLivestock = updatedLivestock
                    )
                }
            }
            "Crypto Token" -> {
                val nextTier = CRYPTO_TOKEN_TIERS.find { it.tierLevel == venture.tierLevel + 1 } ?: return venture
                venture.copy(
                    tierLevel = nextTier.tierLevel,
                    tierName = nextTier.tierName,
                    weeklyGrossRevenue = nextTier.baseWeeklyGross,
                    weeklyOperatingExpenses = nextTier.baseWeeklyExpenses,
                    weeklyNetProfit = nextTier.baseWeeklyGross - nextTier.baseWeeklyExpenses,
                    totalCapitalInvested = venture.totalCapitalInvested + nextTier.upgradeCost
                )
            }
            else -> venture
        }
    }

    fun getMarketingCampaignCost(
        ventureType: String,
        campaignKey: String,
        economyCycle: String,
        politicalTaxModifier: Float
    ): Double {
        val baseCost = when (campaignKey) {
            "LOCAL_ADS" -> 150.0
            "DIGITAL_ADS" -> 450.0
            "PR_BLITZ" -> 1200.0
            else -> 0.0
        }
        if (baseCost <= 0.0) return 0.0

        // 1. Business type risk & trust hurdle:
        // Riskier businesses pay higher marketing prices because they need to prove to customers they can deliver
        val riskMultiplier = when (ventureType) {
            "Taxi Fleet" -> 0.85
            "Farming" -> 0.95
            "Restaurant" -> 1.15
            "Hotel", "Hotel Chain" -> 1.50
            "Crypto Token" -> 2.80
            else -> 1.00
        }

        // 2. State Economic Cycle:
        val cycleMultiplier = when (economyCycle) {
            "Boom" -> 1.25
            "Stable Growth" -> 1.00
            "Stagnation", "Inflation Peak" -> 1.10
            "Recession" -> 0.80
            "Depression" -> 0.65
            else -> 1.00
        }

        // 3. Political / Tax Policy Modifier:
        val politicalMultiplier = (1.0 + (politicalTaxModifier.toDouble() * 1.5)).coerceIn(0.70, 1.45)

        val finalCost = (baseCost * riskMultiplier * cycleMultiplier * politicalMultiplier).roundToInt().toDouble()
        return finalCost.coerceAtLeast(25.0)
    }

    /**
     * Smart SCC Regulatory Valuation for public share listing.
     * Evaluates enterprise profit, margins, expenses discipline, existing investor equity, and assets.
     * Standardizes company capital into 100,000 share units (10% = 10,000 shares, 30% = 30,000 shares).
     */
    fun calculateSmartSccValuation(venture: BusinessVenture, requestedEquityPercent: Double = 0.20): WeShareValuationResult {
        val baseValuation = getEnterpriseValuation(venture)
        val gross = venture.weeklyGrossRevenue.coerceAtLeast(1.0)
        val profitMargin = (venture.weeklyNetProfit / gross).coerceIn(-0.5, 0.8)
        val marginMultiplier = when {
            profitMargin >= 0.35 -> 1.28
            profitMargin >= 0.25 -> 1.15
            profitMargin >= 0.15 -> 1.00
            profitMargin >= 0.05 -> 0.82
            else -> 0.65
        }

        val expenseRatio = venture.weeklyOperatingExpenses / gross
        val expenseMultiplier = when {
            expenseRatio <= 0.60 -> 1.10
            expenseRatio <= 0.75 -> 1.00
            expenseRatio <= 0.88 -> 0.90
            else -> 0.78
        }

        // Institutional investor presence validates corporate governance
        val investorMultiplier = if (venture.totalInvestorEquityPercent > 0.0) 1.08 else 1.00

        // Customer retention stability
        val loyaltyMultiplier = when {
            venture.customerLoyalty >= 0.70f -> 1.15
            venture.customerLoyalty >= 0.50f -> 1.05
            venture.customerLoyalty >= 0.40f -> 1.00
            else -> 0.85
        }

        val sccAuditedValuation = (baseValuation * marginMultiplier * expenseMultiplier * investorMultiplier * loyaltyMultiplier).coerceAtLeast(50000.0)
        val totalSharesIssued = 10000L
        val rawSharePrice = sccAuditedValuation / totalSharesIssued.toDouble()
        val sharePrice = ((rawSharePrice * 100.0).roundToInt() / 100.0).coerceAtLeast(1.0)

        val actualPercentRatio = if (requestedEquityPercent > 1.0) requestedEquityPercent / 100.0 else requestedEquityPercent.coerceIn(0.10, 0.50)
        val targetShares = (totalSharesIssued * actualPercentRatio).toLong()

        val industryMultiple = when (venture.type) {
            "Restaurant" -> if (venture.tierLevel == 1) 1.5 else 2.5
            "Taxi Fleet" -> 2.0
            "Farming" -> 3.0
            "Hotel", "Hotel Chain" -> 4.0
            "Crypto Token" -> 5.0
            else -> 2.5
        }

        return WeShareValuationResult(
            totalValuation = sharePrice * totalSharesIssued,
            sharePrice = sharePrice,
            totalSharesIssued = totalSharesIssued,
            targetSharesToSell = targetShares,
            estimatedCapitalRaised = targetShares * sharePrice,
            peRatio = industryMultiple * marginMultiplier
        )
    }

    fun calculateWeShareValuation(venture: BusinessVenture): WeShareValuationResult {
        return calculateSmartSccValuation(venture, 0.20)
    }

    fun checkWeShareEligibility(
        venture: BusinessVenture,
        playerIntegrity: Int,
        playerTrust: Int
    ): Pair<Boolean, String> {
        if (venture.type == "Crypto Token") {
            return Pair(false, "WeShare policy strictly prohibits cryptocurrency tokens. Cryptographic assets must be audited and listed exclusively on the Genesis Protocol Exchange.")
        }
        if (venture.isListedOnWeShare) {
            return Pair(false, "Enterprise is already publicly listed on WeShare!")
        }
        if (!venture.hasLegalLicense) {
            return Pair(false, "Legal & Regulatory Compliance: Enterprise operates without an official State Commercial Operating License. You must acquire a legal license in the Ventures portal before public IPO filing.")
        }
        if (venture.screeningStatus == "UNDER_REVIEW") {
            return Pair(false, "Filing is currently under regulatory audit. Review completes in ${venture.screeningWeeksRemaining} week(s).")
        }
        if (venture.weeksActive < 30) {
            return Pair(false, "Operational Seasoning: Enterprise has operated for ${venture.weeksActive} weeks. WeShare policy requires at least 30-40 weeks of continuous audited operational history.")
        }
        if (venture.outstandingLoan > 0.0) {
            return Pair(false, "Outstanding Liabilities: Enterprise carries a business loan balance of $${String.format("%,.0f", venture.outstandingLoan)}. Settle all company debt before IPO filing.")
        }
        val currentGross = venture.weeklyGrossRevenue.coerceAtLeast(1.0)
        val margin = venture.weeklyNetProfit / currentGross
        if (margin < 0.15) {
            return Pair(false, "Profit Margin: Current net profit margin is ${(margin * 100).toInt()}%. WeShare requires a sustained net margin of at least 15%.")
        }
        if (venture.customerLoyalty < 0.40f) {
            return Pair(false, "Customer Retention: Returning customer index is ${(venture.customerLoyalty * 100).toInt()}%. WeShare requires at least 40% loyal returning customer base.")
        }
        if (venture.weeklyNetProfit <= 0.0) {
            return Pair(false, "Profitability: Enterprise must be currently generating net profit. Current weekly net: $${String.format("%,.0f", venture.weeklyNetProfit)}/wk.")
        }
        if (playerIntegrity < 45) {
            return Pair(false, "Governance & Integrity: WeShare compliance requires founder integrity of at least 45% (Current: $playerIntegrity%).")
        }
        if (playerTrust < 35) {
            return Pair(false, "Public Standing: WeShare compliance requires public trust of at least 35% (Current: $playerTrust%).")
        }
        return Pair(true, "Eligible for WeShare IPO screening audit ($20,000 regulatory filing fee).")
    }

    fun checkGenesisEligibility(
        venture: BusinessVenture,
        playerIntegrity: Int
    ): Pair<Boolean, String> {
        if (venture.type != "Crypto Token") {
            return Pair(false, "Genesis Protocol Exchange is strictly reserved for cryptographic tokens and decentralized networks.")
        }
        if (venture.isListedOnWeShare) {
            return Pair(false, "Protocol is already actively trading on Genesis!")
        }
        if (!venture.hasLegalLicense) {
            return Pair(false, "Regulatory Exemption: Protocol has not acquired an official Digital Asset Commercial Regulatory Filing ($${String.format("%,.0f", venture.legalLicenseCost)}). Acquire legal license before Genesis public listing.")
        }
        if (venture.screeningStatus == "UNDER_REVIEW") {
            return Pair(false, "Smart contract security audit underway. Verification completes in ${venture.screeningWeeksRemaining} week(s).")
        }
        if (venture.weeksActive < 40) {
            return Pair(false, "Network Maturity: Token protocol has run on testnet/mainnet for ${venture.weeksActive} weeks. Genesis requires at least 40 weeks on-chain operational stability.")
        }
        if (venture.weeklyNetProfit <= 0.0) {
            return Pair(false, "Protocol Cashflow: On-chain gas and staking protocol must be generating net revenue. Current net: $${String.format("%,.0f", venture.weeklyNetProfit)}/wk.")
        }
        if (venture.customerLoyalty < 0.50f) {
            return Pair(false, "On-Chain Staker Retention: Daily active wallet retention is ${(venture.customerLoyalty * 100).toInt()}%. Genesis requires >= 50% validator and token lockup retention.")
        }
        if (playerIntegrity < 50) {
            return Pair(false, "Smart Contract Governance: Genesis Foundation requires founder integrity of at least 50% to prevent rug-pull exploits (Current: $playerIntegrity%).")
        }
        return Pair(true, "Eligible for Genesis Security Audit & Public Token Listing ($35,000 audit fee).")
    }

    fun calculateValuationForEquity(venture: BusinessVenture, equityPercent: Double): WeShareValuationResult {
        return calculateSmartSccValuation(venture, equityPercent)
    }

    /**
     * Calculates public share buyback metrics at CURRENT share price.
     * Returns Triple(sharesToBuy, costAtCurrentPrice, equityPercentReclaimed)
     */
    fun calculateBuybackQuote(venture: BusinessVenture, buybackFractionOfPublic: Double): Triple<Long, Double, Double> {
        if (!venture.isListedOnWeShare || venture.weSharePublicSharesSold <= 0L) {
            return Triple(0L, 0.0, 0.0)
        }
        val currentPrice = venture.weShareSharePrice.coerceAtLeast(1.0)
        val sharesToBuy = (venture.weSharePublicSharesSold * buybackFractionOfPublic.coerceIn(0.05, 1.0)).toLong().coerceAtLeast(1L)
        val totalCost = sharesToBuy * currentPrice
        val equityReclaimed = (sharesToBuy.toDouble() / venture.weShareTotalSharesIssued.toDouble()) * venture.weShareListedEquityPercent
        return Triple(sharesToBuy, totalCost, equityReclaimed)
    }

    fun processWeeklyVentures(
        state: GameStateEntity,
        economyCycle: String,
        reportBuilder: StringBuilder
    ): VentureWeeklySimulationResult {
        var totalPlayerCash = 0.0
        var totalEnterpriseProfitsEarned = 0.0
        var totalIpoCash = 0.0
        val generatedMails = mutableListOf<MailMessage>()
        val logs = mutableListOf<String>()
        val updatedStocks = state.stocks.toMutableList()

        val cycleMultiplier = when (economyCycle) {
            "Boom" -> 1.15
            "Stable Growth" -> 1.00
            "Stagnation" -> 0.92
            "Recession" -> 0.82
            else -> 1.00
        }

        val updatedVentures = state.businessVentures.map { venture ->
            if (!venture.isOwned) return@map venture

            // 1. Calculate this week's gross and expenses
            val weeklyVariance = 1.0 + (Random.nextDouble(-0.04, 0.04))

            var actualGross = 0.0
            var actualExpenses = 0.0

            var updatedTaxiCarCount = venture.taxiCarCount
            var updatedTaxiTrips = venture.taxiWeeklyTripsCompleted
            var updatedTaxiFares = venture.taxiWeeklyFaresEarned
            var updatedTaxiFuel = venture.taxiWeeklyFuelCost
            var updatedDriverSalaries = venture.taxiWeeklyDriverSalary
            var updatedMechanicSalaries = venture.taxiWeeklyMechanicSalary
            var updatedDispatcherSalaries = venture.taxiWeeklyDispatcherSalary
            var updatedTaxiSalaries = venture.taxiWeeklyDriverSalary + venture.taxiWeeklyMechanicSalary + venture.taxiWeeklyDispatcherSalary
            var updatedTaxiMaint = venture.taxiWeeklyMaintenanceCost
            var updatedTaxiCondition = venture.taxiCondition
            var updatedTaxiMiles = venture.taxiOdometerMiles

            var updatedCustomersServed = venture.weeklyCustomersServed
            var updatedFoodSales = venture.weeklyFoodSales
            var updatedIngredientCost = venture.weeklyIngredientCost
            var updatedBuildingMaint = venture.weeklyBuildingMaintenanceCost
            var updatedPayroll = venture.weeklyStaffPayroll
            var updatedEquipMaint = venture.weeklyEquipmentMaintenanceCost
            var updatedEfficiency = venture.serviceEfficiency
            var updatedEquipmentList = venture.equipmentList

            // Farming state tracking
            var updatedLandTier = venture.farmLandTier
            var updatedLandOwned = venture.farmLandIsOwned
            var updatedLandAcres = venture.farmLandAcres
            var updatedLandValue = venture.farmLandValue
            var updatedLandRent = venture.farmLandRentWeekly
            var updatedBatches = venture.farmCropBatches
            var updatedLivestock = venture.farmLivestock
            var updatedMachinery = venture.farmMachinery
            var currentInventory = venture.farmInventory
            var weeklyRawCropsRevenue = venture.farmWeeklyRawCropsRevenue
            var weeklyFruitsRevenue = venture.farmWeeklyFruitsRevenue
            var weeklyLivestockRevenue = venture.farmWeeklyLivestockRevenue
            var weeklyProcessedRevenue = venture.farmWeeklyProcessedRevenue
            var feedExpense = venture.farmWeeklyFeedExpense
            var landRentExpense = venture.farmWeeklyLandRentExpense
            var machineUpkeepExpense = venture.farmWeeklyMachineUpkeepExpense
            var farmPayrollExpense = venture.farmWeeklyPayrollExpense
            var seedExpense = venture.farmWeeklySeedExpense
            var weatherEvent = venture.farmLastWeatherEvent

            // Hotel state tracking
            var hotelRoomRev = venture.hotelWeeklyRoomRevenue
            var hotelAmenityRev = venture.hotelWeeklyAmenityRevenue
            var hotelFoodRev = venture.hotelWeeklyFoodRevenue
            var hotelPayroll = venture.hotelWeeklyPayrollCost
            var hotelSupplies = venture.hotelWeeklySuppliesCost
            var hotelUtilities = venture.hotelWeeklyUtilityCost
            var hotelBldgMaint = venture.hotelWeeklyMaintenanceCost
            var hotelAmenityMaint = 0.0
            var updatedHotelCondition = venture.hotelCondition
            var updatedHotelCleanliness = venture.hotelCleanliness
            var updatedHotelReputation = venture.hotelReputation
            var updatedHotelOccupancy = venture.hotelOccupancyRate
            var updatedHotelRooms = if (venture.hotelRooms.isEmpty()) getDefaultHotelRooms() else venture.hotelRooms
            var updatedHotelLodges = venture.hotelTotalLodgesThisWeek
            var updatedHotelIncident = venture.hotelLastIncident
            var hotelSeasonStr = venture.hotelSeasonText

            // Crypto state tracking
            var cryptoSwapFees = venture.cryptoWeeklySwapFeesRevenue
            var cryptoStakingRev = venture.cryptoWeeklyStakingFeesRevenue
            var cryptoDeFiRev = venture.cryptoWeeklyDeFiYieldRevenue
            var cryptoPayroll = venture.cryptoWeeklyPayrollCost
            var cryptoInfra = venture.cryptoWeeklyServerInfraCost
            var cryptoSecurityExp = venture.cryptoWeeklySecurityCost
            var updatedCycle = venture.cryptoMarketCycle
            var updatedSentiment = venture.cryptoCommunitySentiment
            var updatedPrice = venture.cryptoTokenPrice
            var updatedVolume = venture.cryptoWeeklyTradingVolume
            var updatedLp = venture.cryptoLiquidityPoolUsd
            var updatedActiveUsers = venture.cryptoActiveUsers
            var updatedProtocolFees = venture.cryptoWeeklyProtocolFeeRevenue
            var updatedListingNotice = venture.cryptoMarketListingNotice
            var updatedLesson = venture.cryptoEducationalLesson
            var updatedServiceBoostWeeks = venture.cryptoNewServiceLaunchBoostWeeks
            var updatedHackLoss = 0.0

            var updatedTaxiParkingTax = venture.taxiWeeklyParkingSpaceTax
            var updatedTaxiParkingMaint = venture.taxiWeeklyParkingSpaceMaint
            var updatedFarmCleanliness = venture.farmCleanliness

            var updatedRecurringCustomers = venture.recurringCustomers.coerceAtLeast(10)
            var updatedMarketingWeeks = venture.marketingWeeksActive
            var marketingCost = 0.0
            var newMarketingCustomers = 0

            // Marketing Campaign Processing
            when (venture.marketingCampaign) {
                "LOCAL_ADS" -> {
                    marketingCost = getMarketingCampaignCost(venture.type, "LOCAL_ADS", economyCycle, state.politicalTaxModifier)
                    newMarketingCustomers = Random.nextInt(8, 16)
                    updatedMarketingWeeks += 1
                }
                "DIGITAL_ADS" -> {
                    marketingCost = getMarketingCampaignCost(venture.type, "DIGITAL_ADS", economyCycle, state.politicalTaxModifier)
                    newMarketingCustomers = Random.nextInt(22, 42)
                    updatedMarketingWeeks += 1
                }
                "PR_BLITZ" -> {
                    marketingCost = getMarketingCampaignCost(venture.type, "PR_BLITZ", economyCycle, state.politicalTaxModifier)
                    newMarketingCustomers = Random.nextInt(60, 115)
                    updatedMarketingWeeks += 1
                }
            }
            actualExpenses += marketingCost
            updatedRecurringCustomers += newMarketingCustomers

            // Word-of-Mouth Organic Customer Acquisition (after 25+ marketing weeks)
            if (updatedMarketingWeeks >= 25) {
                val womNew = Random.nextInt(2, 6)
                updatedRecurringCustomers += womNew
                if (Random.nextDouble() < 0.20) {
                    logs.add("🗣️ ${venture.name}: Loyal customer base drove +$womNew new recurring clients via word-of-mouth!")
                }
            }

            if (venture.type == "Taxi Fleet") {
                val carCount = venture.taxiCarCount.coerceAtLeast(1)
                val driverCount = venture.taxiDriverCount.coerceAtLeast(0)
                val mechanicCount = venture.taxiMechanicCount
                val dispatcherCount = venture.taxiDispatcherCount

                val activeCars = kotlin.math.min(carCount, driverCount)

                if (carCount > driverCount) {
                    val idle = carCount - driverCount
                    logs.add("ℹ️ ${venture.name}: $idle vehicle(s) sat idle this week due to unassigned drivers.")
                } else if (driverCount > carCount) {
                    val surplus = driverCount - carCount
                    logs.add("ℹ️ ${venture.name}: $surplus driver(s) were on standby payroll without vehicles.")
                }

                // 1. Staff Payroll Deductions (Drivers, Fleet Mechanics, Radio Dispatchers)
                val driverSalaries = driverCount * TAXI_DRIVER_SALARY
                val mechanicSalaries = mechanicCount * TAXI_MECHANIC_SALARY
                val dispatcherSalaries = dispatcherCount * TAXI_DISPATCHER_SALARY
                val totalTaxiStaffSalaries = driverSalaries + mechanicSalaries + dispatcherSalaries

                // 2. Radio Dispatchers: 1 per 4 cars recommended
                // Without dispatchers or without enough dispatchers, drivers can't get to pick/drop locations faster,
                // meaning they don't have enough time to cover more trips.
                val recDispatchers = ((carCount + 3) / 4).coerceAtLeast(1)
                val dispatchEfficiency: Double = when {
                    dispatcherCount == 0 -> {
                        logs.add("⚠️ ${venture.name}: No Radio Dispatcher! Drivers lacked coordinates and wasted time searching for pickups; completed trips collapsed to 45%.")
                        0.45
                    }
                    dispatcherCount < recDispatchers -> {
                        val ratio = dispatcherCount.toDouble() / recDispatchers.toDouble()
                        val eff = 0.45 + (ratio * 0.55)
                        logs.add("⚠️ ${venture.name}: Dispatcher Shortage ($dispatcherCount of $recDispatchers recommended)! Slower pickup dispatch reduced weekly trips to ${(eff * 100).toInt()}%.")
                        eff
                    }
                    else -> {
                        val bonus = ((dispatcherCount - recDispatchers) * 0.04).coerceAtMost(0.15)
                        1.0 + bonus
                    }
                }

                // 3. Fleet Mechanics & Car Lifespan:
                // Without mechanics cars last up to 4 weeks (25% wear per week).
                // With 1 mechanic per 4 cars, cars last up to 10 weeks (10% wear per week).
                val recMechanics = ((carCount + 3) / 4).coerceAtLeast(1)
                var routineUpkeep = carCount * (120.0 * weeklyVariance)
                val weeklyWearRate: Float

                if (mechanicCount >= recMechanics) {
                    weeklyWearRate = 0.10f // Exactly 10 weeks lifespan!
                    routineUpkeep *= 0.70 // 30% parts discount through in-house labor
                } else if (mechanicCount == 0) {
                    weeklyWearRate = 0.25f // Exactly 4 weeks lifespan!
                    routineUpkeep += (carCount * 300.0) // Emergency commercial shop & towing fees
                    logs.add("⚠️ ${venture.name}: No Fleet Mechanics! Rapid mechanical degradation (4-week car lifespan) and heavy commercial emergency repair fees incurred.")
                } else {
                    val ratio = mechanicCount.toDouble() / recMechanics.toDouble()
                    weeklyWearRate = (0.25f - (ratio.toFloat() * 0.15f)).coerceIn(0.10f, 0.25f)
                    routineUpkeep += (carCount * 140.0)
                    logs.add("⚠️ ${venture.name}: Mechanic Shortage ($mechanicCount of $recMechanics recommended, 1 per 4 cars needed)! Fleet maintenance backlog degrading lifespan.")
                }

                val fuelPricePerGal = when (economyCycle) {
                    "Boom" -> 4.55
                    "Stable Growth" -> 4.00
                    "Stagnation" -> 3.70
                    "Recession" -> 3.35
                    else -> 4.00
                }

                // Mechanical Condition Impact: Low condition prevents cars from covering distances they are capable of, reducing income
                val conditionFactor = venture.taxiCondition.toDouble().coerceIn(0.15, 1.0)
                if (venture.taxiCondition < 0.35f) {
                    logs.add("⚠️ ${venture.name}: Fleet mechanical condition is critical (${(venture.taxiCondition * 100).toInt()}%). Severe engine wear and breakdowns cut distance capability and slashed weekly revenue! Deep servicing recommended ($${String.format("%,.0f", carCount * TAXI_SERVICE_BASE_COST)}).")
                }

                var totalFares = 0.0
                var totalTrips = 0
                var totalMiles = 0.0
                var totalFuel = 0.0

                if (activeCars > 0) {
                    // Recurring customer rides (e.g. 50 recurring customers book ~115 rides/week)
                    val recurringRidesWeekly = (updatedRecurringCustomers * 2.3).roundToInt()
                    val recurringRidesPerCar = (recurringRidesWeekly / activeCars)

                    for (c in 1..activeCars) {
                        // Low condition slashes trip completion and distance capable
                        val baseTrips = (Random.nextInt(40, 58) * cycleMultiplier * dispatchEfficiency * conditionFactor).toInt().coerceAtLeast(2)
                        val trips = (baseTrips + (recurringRidesPerCar * dispatchEfficiency * conditionFactor).toInt()).coerceAtLeast(2)
                        val maxCapableDistance = (9.2 * conditionFactor).coerceAtLeast(1.8)
                        val minCapableDistance = (4.5 * conditionFactor).coerceAtLeast(1.0)
                        val avgDistance = Random.nextDouble(minCapableDistance, maxCapableDistance)
                        val carMiles = trips * avgDistance
                        val carFares = (trips * 3.50 * conditionFactor) + (carMiles * 1.42 * conditionFactor * weeklyVariance)
                        val gallonsUsed = carMiles / 22.0
                        val carFuelCost = gallonsUsed * fuelPricePerGal

                        totalTrips += trips
                        totalMiles += carMiles
                        totalFares += carFares
                        totalFuel += carFuelCost
                    }
                }

                // Customer Drop Mechanics: Low amount of cars & drivers to complete trips
                val weeklyTripCapacity = activeCars * 65
                val weeklyTripDemand = (updatedRecurringCustomers * 2.3).roundToInt()
                if (activeCars == 0 && updatedRecurringCustomers > 5) {
                    val lostCustomers = (updatedRecurringCustomers * 0.35).toInt().coerceAtLeast(2).coerceAtMost(updatedRecurringCustomers - 5)
                    updatedRecurringCustomers = (updatedRecurringCustomers - lostCustomers).coerceAtLeast(5)
                    logs.add("⚠️ ${venture.name}: Grounded fleet! With zero active cabs running, 100% of ride requests failed. Lost $lostCustomers recurring riders.")
                } else if (weeklyTripDemand > weeklyTripCapacity && updatedRecurringCustomers > 5) {
                    val unservedTrips = weeklyTripDemand - weeklyTripCapacity
                    val lostCustomers = ((unservedTrips / 2.3) * 0.18).toInt().coerceAtLeast(1).coerceAtMost(updatedRecurringCustomers - 5)
                    updatedRecurringCustomers = (updatedRecurringCustomers - lostCustomers).coerceAtLeast(5)
                    logs.add("⚠️ ${venture.name}: Fleet bottleneck! Passenger demand exceeded active cabs. Lost $lostCustomers recurring riders due to delays.")
                }

                // Parking Lot Space: Monthly tax ($150/car = $37.50/wk) & maintenance ($100/car = $25/wk)
                val parkingSpaceTax = carCount * 37.50
                val parkingSpaceMaint = carCount * 25.00
                updatedTaxiParkingTax = parkingSpaceTax
                updatedTaxiParkingMaint = parkingSpaceMaint

                actualGross = totalFares
                actualExpenses += totalTaxiStaffSalaries + totalFuel + routineUpkeep + parkingSpaceTax + parkingSpaceMaint

                // Condition calculation & Mechanic continuous repairs
                var newCondition = venture.taxiCondition - weeklyWearRate
                if (mechanicCount >= recMechanics && newCondition < 0.95f) {
                    newCondition += 0.03f // Fleet mechanics actively repair wear each week!
                }
                newCondition = newCondition.coerceIn(0.0f, 1.0f)

                // Damage / Breakdown / Forced Replacement
                if (newCondition <= 0.10f) {
                    if (carCount > 1) {
                        updatedTaxiCarCount = carCount - 1
                        newCondition = 0.45f
                        logs.add("💥 ${venture.name}: Vehicle Totaled! A taxi suffered catastrophic breakdown after 5 weeks of running without sufficient mechanic maintenance. Fleet reduced to $updatedTaxiCarCount cars — purchase a replacement cab!")
                    } else {
                        newCondition = 0.05f
                        logs.add("💥 ${venture.name}: Fleet Immobilized! Your taxi is completely damaged from lack of mechanic maintenance. Service or replace the vehicle to resume trips!")
                    }
                } else {
                    updatedTaxiCarCount = carCount
                }

                updatedTaxiCondition = newCondition
                updatedTaxiMiles = venture.taxiOdometerMiles + totalMiles
                updatedTaxiTrips = totalTrips
                updatedTaxiFares = totalFares
                updatedTaxiFuel = totalFuel
                updatedDriverSalaries = driverSalaries
                updatedMechanicSalaries = mechanicSalaries
                updatedDispatcherSalaries = dispatcherSalaries
                updatedTaxiSalaries = totalTaxiStaffSalaries
                updatedTaxiMaint = routineUpkeep

            } else if (venture.type == "Restaurant") {
                val tierDetail = RESTAURANT_TIER_DETAILS.find { it.tierLevel == venture.tierLevel } ?: RESTAURANT_TIER_DETAILS.first()

                val wCount = venture.waiterCount
                val cCount = venture.chefCount
                val scCount = venture.seniorChefCount
                val clCount = venture.cleanerCount
                val mCount = venture.managerCount

                // 1. Senior Chef Leadership: Every 4 chefs needs 1 senior chef, else less food than expected will be served
                val recSeniorChefs = if (cCount >= 4) (cCount / 4) else 0
                val seniorChefRatio = if (recSeniorChefs > 0) (scCount.toDouble() / recSeniorChefs.toDouble()).coerceAtMost(1.0) else 1.0
                val chefLeadershipFactor = if (recSeniorChefs > 0 && scCount < recSeniorChefs) (0.65 + seniorChefRatio * 0.35) else 1.0
                if (recSeniorChefs > 0 && scCount < recSeniorChefs) {
                    logs.add("⚠️ ${venture.name}: Kitchen Leadership Deficit ($scCount of $recSeniorChefs Senior Chefs)! Headless kitchen coordination reduced food output.")
                }

                // 2. Manager requirement: Every 10 staff needs 1 manager to coordinate floor and kitchen
                val totalRestStaff = wCount + cCount + scCount + clCount
                val recManagers = if (totalRestStaff >= 10) ((totalRestStaff + 9) / 10) else 0
                val managerModifier = if (recManagers > 0 && mCount < recManagers) {
                    val mRatio = mCount.toDouble() / recManagers.toDouble()
                    logs.add("⚠️ ${venture.name}: Management Shortage ($mCount of $recManagers Managers)! Floor disorganization slowed table turnover.")
                    0.75 + (mRatio * 0.25)
                } else {
                    1.0
                }

                // 3. Equipment Simulation
                val currentEq = if (venture.equipmentList.isEmpty()) getDefaultRestaurantEquipment() else venture.equipmentList
                var totalEqMaint = 0.0
                var eqEfficiency = 1.0
                val simulatedEq = currentEq.map { eq ->
                    if (eq.isOwned) {
                        totalEqMaint += eq.weeklyMaintenance
                        val wear = Random.nextDouble(0.02, 0.04).toFloat()
                        val newCond = (eq.condition - wear).coerceIn(0.10f, 1.0f)
                        if (newCond < 0.35f) {
                            eqEfficiency *= 0.92
                            if (Random.nextDouble() < 0.25) {
                                logs.add("⚠️ ${venture.name}: ${eq.name} malfunctioned during rush hour! Kitchen service slowed down.")
                            }
                        }
                        eq.copy(condition = newCond)
                    } else {
                        eq
                    }
                }
                updatedEquipmentList = simulatedEq
                updatedEquipMaint = totalEqMaint

                updatedEfficiency = (managerModifier * eqEfficiency).toFloat()

                // 4. Chef Cooking vs Waiter Serving Capacity
                // More chefs = more food cooked. Waiters needed to serve.
                // Seating Overcrowding: If tier is Food Truck (tier 1) and recurring customers > 50, customers have nowhere to sit!
                val maxSeatedDiners = tierDetail.maxDailyCapacity * 2
                if (venture.tierLevel == 1 && updatedRecurringCustomers >= 60) {
                    val excess = (updatedRecurringCustomers - 40).coerceAtLeast(0)
                    val walkouts = (excess * 0.22).roundToInt().coerceAtLeast(4)
                    updatedRecurringCustomers = (updatedRecurringCustomers - walkouts).coerceAtLeast(15)
                    logs.add("⚠️ ${venture.name}: Seating Capacity Crisis! Over $updatedRecurringCustomers recurring patrons arrived, but your Food Truck has zero seating! $walkouts customers left in frustration without eating. Upgrade to a Diner or Bistro to provide seating and retain diners!")
                } else if (updatedRecurringCustomers > maxSeatedDiners) {
                    val excess = updatedRecurringCustomers - maxSeatedDiners
                    val walkouts = (excess * 0.15).roundToInt().coerceAtLeast(2)
                    updatedRecurringCustomers = (updatedRecurringCustomers - walkouts).coerceAtLeast(15)
                    logs.add("⚠️ ${venture.name}: Floor Space Overwhelmed! ${updatedRecurringCustomers + walkouts} customers arrived, exceeding dining room capacity ($maxSeatedDiners seats). $walkouts patrons walked away due to excessive wait times. Upgrade premises tier to expand seating!")
                }

                val baseChefOutputPerWeek = 245.0 // ~35 meals/day
                val baseSeniorChefOutputPerWeek = 350.0 // ~50 meals/day
                val mealsCookedCapacity = ((cCount * baseChefOutputPerWeek + scCount * baseSeniorChefOutputPerWeek) * chefLeadershipFactor * eqEfficiency).toInt()

                val baseWaiterServeCapacityPerWeek = 280.0 // ~40 patrons/day
                val mealsServedCapacity = (wCount * baseWaiterServeCapacityPerWeek * managerModifier).toInt()

                val weeklyDinerDemand = (updatedRecurringCustomers * 2.8 * cycleMultiplier * weeklyVariance).toInt().coerceAtLeast(10)

                val mealsCooked = kotlin.math.min(weeklyDinerDemand, mealsCookedCapacity)
                val mealsServed = kotlin.math.min(mealsCooked, mealsServedCapacity)
                val unservedMeals = (mealsCooked - mealsServed).coerceAtLeast(0)

                if (unservedMeals > 0 && mealsCooked > 0) {
                    logs.add("⚠️ ${venture.name}: Waiter Shortage! Kitchen cooked $mealsCooked meals, but only $wCount waiter(s) were on duty. $unservedMeals prepared meals spoiled unserved! Ingredients were deducted without revenue.")
                } else if (mealsCookedCapacity < weeklyDinerDemand && wCount > 0) {
                    logs.add("⚠️ ${venture.name}: Kitchen Bottleneck! Chef capacity ($mealsCookedCapacity meals) could not satisfy customer demand ($weeklyDinerDemand meals). Hire more kitchen staff!")
                }

                updatedCustomersServed = mealsServed

                val minSpend = (tierDetail.minDailyRevenue / tierDetail.maxDailyCapacity.toDouble()).coerceIn(18.0, 50.0)
                val maxSpend = (tierDetail.maxDailyRevenue / tierDetail.minDailyCapacity.toDouble()).coerceIn(25.0, 75.0)
                val avgSpend = Random.nextDouble(minSpend, maxSpend)

                val foodSales = (mealsServed * avgSpend * weeklyVariance).coerceAtLeast(0.0)
                updatedFoodSales = foodSales

                // 30% ingredient cost rule applies to ALL food cooked
                val ingredientCostPerMeal = avgSpend * 0.30
                val ingredientCost = (mealsCooked * ingredientCostPerMeal).coerceAtLeast(0.0)
                updatedIngredientCost = ingredientCost

                // 5. Cleaners: 1 cleaner needed per 20 recurring customers, else recurring customers reduce
                val recCleaners = ((updatedRecurringCustomers + 19) / 20).coerceAtLeast(1)
                if (clCount < recCleaners && updatedRecurringCustomers > 5) {
                    val cleanerDeficit = recCleaners - clCount
                    val lostDiners = (cleanerDeficit * Random.nextInt(2, 5)).coerceAtMost(updatedRecurringCustomers - 5)
                    updatedRecurringCustomers = (updatedRecurringCustomers - lostDiners).coerceAtLeast(5)
                    logs.add("⚠️ ${venture.name}: Hygiene Alert! With only $clCount cleaner(s) for $updatedRecurringCustomers recurring customers, cleanliness dropped. Lost $lostDiners recurring patrons.")
                }

                // Monthly building maintenance prorated weekly
                val buildingMaint = (tierDetail.propertyValue * tierDetail.buildingMaintenanceMonthlyPercent) / 4.33
                updatedBuildingMaint = buildingMaint

                // Staff payroll
                val staffPayroll = (wCount * WAITER_WEEKLY_SALARY) +
                        (cCount * CHEF_WEEKLY_SALARY) +
                        (scCount * SENIOR_CHEF_WEEKLY_SALARY) +
                        (clCount * CLEANER_WEEKLY_SALARY) +
                        (mCount * MANAGER_WEEKLY_SALARY)
                updatedPayroll = staffPayroll

                actualGross = foodSales
                actualExpenses = ingredientCost + staffPayroll + buildingMaint + totalEqMaint

            } else if (venture.type == "Farming") {
                // 1. Weather Event
                val weatherRoll = Random.nextDouble()
                val isDrought = weatherRoll < 0.15
                val isPest = weatherRoll in 0.15..0.25
                val isFlood = weatherRoll in 0.25..0.35

                val hasIrrigation = venture.farmMachinery.any { it.id == "mach_irrigation" && (it.isOwned || it.isRented) }
                val hasSprayer = venture.farmMachinery.any { it.id == "mach_sprayer" && (it.isOwned || it.isRented) }
                val hasHarvester = venture.farmMachinery.any { it.id == "mach_harvester" && (it.isOwned || it.isRented) }
                val hasColdStorage = venture.farmMachinery.any { it.id == "stor_cold_storage" && (it.isOwned || it.isRented) }
                val hasFarmTruck = venture.farmMachinery.any { it.id == "veh_farm_truck" && (it.isOwned || it.isRented) }
                val hasProcessor = venture.farmMachinery.any { it.id == "proc_industrial" && (it.isOwned || it.isRented) }
                val hasTractor = venture.farmMachinery.any { it.id == "mach_tractor" && (it.isOwned || it.isRented) }
                val hasSeeder = venture.farmMachinery.any { it.id == "mach_seeder" && (it.isOwned || it.isRented) }

                val weatherYieldMod: Double
                if (isDrought) {
                    if (hasIrrigation) {
                        weatherEvent = "Drought conditions hit, but automated irrigation protected fields!"
                        weatherYieldMod = 1.0
                    } else {
                        weatherEvent = "Severe drought scorched fields! (-30% crop yield)"
                        weatherYieldMod = 0.70
                        logs.add("☀️ ${venture.name}: Drought reduced crop yields by 30%. Consider acquiring an Irrigation System.")
                    }
                } else if (isPest) {
                    if (hasSprayer) {
                        weatherEvent = "Sprayers successfully eliminated pest & blight threats!"
                        weatherYieldMod = 0.95
                    } else {
                        weatherEvent = "Pest swarm infested crops! (-25% crop yield)"
                        weatherYieldMod = 0.75
                        logs.add("🦗 ${venture.name}: Pests destroyed 25% of un-sprayed crops. Consider acquiring a Crop Sprayer.")
                    }
                } else if (isFlood) {
                    if (hasHarvester) {
                        weatherEvent = "Heavy rains delayed operations, but combine harvesters powered through."
                        weatherYieldMod = 0.95
                    } else {
                        weatherEvent = "Torrential rains delayed field harvest! (-15% yield)"
                        weatherYieldMod = 0.85
                    }
                } else {
                    weatherEvent = "Optimal Sunshine & Warm Season (+15% Yield)"
                    weatherYieldMod = 1.15
                }

                // 2. Land Expense & Appreciation
                if (venture.farmLandIsOwned) {
                    landRentExpense = 0.0
                    updatedLandValue = venture.farmLandValue * 1.0003 // ~1.5% annual appreciation
                } else {
                    landRentExpense = venture.farmLandRentWeekly
                }

                // 3. Machinery Maintenance, Rent & Depreciation
                var machineRentCost = 0.0
                var machineMaintCost = 0.0
                val simulatedMachinery = venture.farmMachinery.map { m ->
                    if (m.isRented) {
                        machineRentCost += m.weeklyRentalCost
                        m
                    } else if (m.isOwned) {
                        machineMaintCost += m.weeklyMaintenance
                        val wear = Random.nextDouble(0.01, 0.025).toFloat()
                        val newCond = (m.condition - wear).coerceIn(0.10f, 1.0f)
                        val newBookVal = (m.bookValue * 0.995).coerceAtLeast(m.purchaseCost * 0.30)
                        if (newCond < 0.35f && Random.nextDouble() < 0.20) {
                            logs.add("⚠️ ${venture.name}: ${m.name} mechanical condition is low (${(newCond * 100).toInt()}%). Service required.")
                        }
                        m.copy(condition = newCond, bookValue = newBookVal)
                    } else {
                        m
                    }
                }
                updatedMachinery = simulatedMachinery
                machineUpkeepExpense = machineRentCost + machineMaintCost

                // 4. Staff Payroll (Agricultural technician removed)
                val payroll = (venture.farmHandCount * FARMHAND_WEEKLY_SALARY) +
                        (venture.farmTractorOperatorCount * TRACTOR_OPERATOR_WEEKLY_SALARY) +
                        (venture.farmLivestockWorkerCount * LIVESTOCK_WORKER_WEEKLY_SALARY) +
                        (venture.farmDriverCount * DRIVER_WEEKLY_SALARY) +
                        (venture.farmVeterinarianCount * VETERINARIAN_WEEKLY_SALARY) +
                        (venture.farmManagerCount * FARM_MANAGER_WEEKLY_SALARY)
                farmPayrollExpense = payroll

                // --- 4a. Farmhand & Cleanliness Engine ---
                // --- 4a. Farmhand & Cleanliness Engine ---
                // Farmhands actively clean fields, pastures, and pens.
                val totalLivestockCount = venture.farmLivestock.sumOf { it.count }
                val farmhandCleaningPower = venture.farmHandCount * 0.12f
                val dirtAccumulation = 0.03f + (venture.farmCropBatches.size * 0.01f) + (totalLivestockCount * 0.002f)
                val netCleanlinessDelta = farmhandCleaningPower - dirtAccumulation
                updatedFarmCleanliness = (updatedFarmCleanliness + netCleanlinessDelta).coerceIn(0.0f, 1.0f)

                val isDirtHazardous = updatedFarmCleanliness < 0.40f
                val cropMortalityRate = if (isDirtHazardous) {
                    ((0.40f - updatedFarmCleanliness) * 0.5f).coerceIn(0.05f, 0.35f)
                } else 0.0f

                if (isDirtHazardous) {
                    logs.add("⚠️ ${venture.name}: Pasture & field cleanliness critical (${(updatedFarmCleanliness * 100).toInt()}% clean)! Needs more farmhands (${venture.farmHandCount} active). ${(cropMortalityRate * 100).toInt()}% of crops and livestock will suffer mortality.")
                }

                // --- 4b. Tractor Operators Engine ---
                // Every machine needs 1 operator. Without them machines will be effectless and money won't be earned from processed goods.
                val activeMachinesCount = venture.farmMachinery.count { it.isOwned || it.isRented }
                val tractorOperatorCount = venture.farmTractorOperatorCount
                val hasTractorOperator = tractorOperatorCount > 0
                val operatorMachineRatio = if (activeMachinesCount > 0) {
                    (tractorOperatorCount.toDouble() / activeMachinesCount.toDouble()).coerceIn(0.0, 1.0)
                } else 1.0

                val tractorBonus = if (hasTractor && hasTractorOperator) 1.0 + (0.15 * operatorMachineRatio) else 1.0
                val seederBonus = if (hasSeeder && hasTractorOperator) 1.0 + (0.10 * operatorMachineRatio) else 1.0
                val harvestBonus = if (hasHarvester && hasTractorOperator) 1.0 + (0.15 * operatorMachineRatio) else 1.0
                val truckBonus = if (hasFarmTruck && venture.farmDriverCount > 0) 1.10 else 1.0
                val cropEfficiency = weatherYieldMod * tractorBonus * seederBonus * harvestBonus

                if (activeMachinesCount > 0 && !hasTractorOperator) {
                    logs.add("⚠️ ${venture.name}: Machinery unoperated! With 0 Tractor Operators, all heavy machines are effectless.")
                } else if (activeMachinesCount > 0 && tractorOperatorCount < activeMachinesCount) {
                    logs.add("ℹ️ ${venture.name}: Partial machinery operation ($tractorOperatorCount of $activeMachinesCount machines staffed by Tractor Operators).")
                }

                // 5. Crop Batches Progress, Cleanliness Mortality & Harvest
                val nextBatches = mutableListOf<FarmCropBatch>()
                val invList = venture.farmInventory.toMutableList()
                var cropsHarvestedUnits = 0.0

                for (b in venture.farmCropBatches) {
                    val nWeek = b.currentWeek + 1
                    var batchYield = b.expectedYieldUnits * cropEfficiency
                    if (isDirtHazardous) {
                        batchYield *= (1.0f - cropMortalityRate)
                    }

                    if (nWeek >= b.totalWeeks) {
                        val cropInfo = CROPS_CATALOG.find { it.name == b.cropName } ?: CROPS_CATALOG.first()
                        val unitPrice = cropInfo.marketPricePerUnit * truckBonus * cycleMultiplier
                        cropsHarvestedUnits += batchYield

                        val existIdx = invList.indexOfFirst { it.name == b.cropName }
                        if (existIdx >= 0) {
                            val item = invList[existIdx]
                            invList[existIdx] = item.copy(
                                quantity = item.quantity + batchYield,
                                marketPricePerUnit = unitPrice
                            )
                        } else {
                            invList.add(
                                FarmInventoryItem(
                                    id = "inv_" + UUID.randomUUID().toString().take(6),
                                    name = b.cropName,
                                    category = "CROP",
                                    quantity = batchYield,
                                    unit = b.yieldUnitName,
                                    marketPricePerUnit = unitPrice
                                )
                            )
                        }
                        logs.add("🌾 ${venture.name}: Harvested ${batchYield.toInt()} ${b.yieldUnitName} of ${b.cropName} into storage!")
                    } else {
                        nextBatches.add(b.copy(currentWeek = nWeek, expectedYieldUnits = batchYield))
                    }
                }

                // --- 5a. Driver Freight Logistics Engine ---
                // "farm drivers shouldn't auto sell harvests, let player do their thing as they please"
                // Drivers provide bulk distribution access and market freight bonuses (+15% revenue when player sells)
                var weeklyRawCropsRevenue = 0.0
                val accumulatedCropUnits = invList.sumOf { it.quantity }
                if (venture.farmDriverCount > 0 && accumulatedCropUnits >= 20.0) {
                    logs.add("🚚 ${venture.name}: Farm Drivers standing by with trucks! Ready to haul harvests to market with +15% bulk freight bonus when you choose to sell.")
                } else if (venture.farmDriverCount == 0 && accumulatedCropUnits >= 50.0) {
                    logs.add("📦 ${venture.name}: Silo storage has ${accumulatedCropUnits.toInt()} crop units. Hire Drivers to unlock premium freight delivery on sales!")
                }

                var autoSeedExpense = 0.0
                if (venture.farmManagerCount > 0 && nextBatches.isEmpty()) {
                    val freeAcres = (venture.farmLandAcres * 0.6).coerceAtLeast(2.0)
                    val defCrop = CROPS_CATALOG.first()
                    val cCost = defCrop.seedCostPerAcre * freeAcres
                    autoSeedExpense = cCost
                    nextBatches.add(
                        FarmCropBatch(
                            id = "batch_" + UUID.randomUUID().toString().take(6),
                            cropName = defCrop.name,
                            acres = freeAcres,
                            seedCost = cCost,
                            totalWeeks = defCrop.growthWeeks,
                            currentWeek = 0,
                            isReadyToHarvest = false,
                            expectedYieldUnits = defCrop.expectedYieldUnits * freeAcres,
                            yieldUnitName = defCrop.yieldUnitName
                        )
                    )
                }
                updatedBatches = nextBatches
                seedExpense = autoSeedExpense

                // 6. Fruits (Tier 2+)
                var fruitsRev = 0.0
                if (venture.tierLevel >= 2) {
                    fruitsRev = Random.nextDouble(1400.0, 2600.0) * cycleMultiplier * weatherYieldMod * truckBonus
                }
                weeklyFruitsRevenue = fruitsRev

                // --- 7. Livestock Engine (Livestock Worker & Veterinarian) ---
                // Every livestock is bought as a kid and matures over weeks:
                // Chick: 4 wks, Goat: 6 wks, Sheep: 10 wks, Cow: 12 wks, Pig: 14 wks.
                // Product development (egg, milk, wool) and reproduction is for matured ones only.
                // Reproduction requires even number of mature animals (2 mature -> 1 baby, 4 mature -> 2 babies double, 6 -> 3 triple).
                // Veterinarian and livestock worker are needed to make sure babies don't die.
                var livestockRev = 0.0
                var fCost = 0.0
                val isMonthlyBreedingWeek = (state.currentWeek % 4 == 0)
                val hasVeterinarian = venture.farmVeterinarianCount > 0
                val hasLivestockWorker = venture.farmLivestockWorkerCount > 0

                val simulatedLivestock = venture.farmLivestock.map { grp ->
                    val totalAnimals = grp.count
                    if (totalAnimals > 0) {
                        fCost += (totalAnimals * grp.weeklyFeedPerUnit)
                        var currentMature = grp.matureCount
                        var currentBabyList = grp.babyWeeksOld.toMutableList()

                        // 1. Baby Aging & Maturation
                        val nextBabyList = mutableListOf<Int>()
                        for (weeksOld in currentBabyList) {
                            val newAge = weeksOld + 1
                            if (newAge >= grp.weeksToMaturity) {
                                currentMature += 1
                                logs.add("🎉 ${venture.name}: A young ${grp.type} has matured! Ready for produce, breeding, or sale.")
                            } else {
                                nextBabyList.add(newAge)
                            }
                        }

                        // 2. Baby Survival & Mortality (Vet & Worker required)
                        if (nextBabyList.isNotEmpty()) {
                            if (!hasVeterinarian && !hasLivestockWorker) {
                                val deadCount = kotlin.math.ceil(nextBabyList.size * 0.40).toInt().coerceAtLeast(1)
                                repeat(deadCount.coerceAtMost(nextBabyList.size)) { if (nextBabyList.isNotEmpty()) nextBabyList.removeAt(0) }
                                logs.add("⚠️ ${venture.name}: Neglected newborns! $deadCount young ${grp.type}(s) died due to lack of Veterinarian and Livestock Worker care.")
                            } else if (!hasVeterinarian && Random.nextFloat() < 0.25f) {
                                val deadCount = 1
                                if (nextBabyList.isNotEmpty()) nextBabyList.removeAt(0)
                                logs.add("⚠️ ${venture.name}: Sickness! $deadCount newborn ${grp.type} died without a Veterinarian to administer treatments.")
                            } else if (!hasLivestockWorker && Random.nextFloat() < 0.25f) {
                                val deadCount = 1
                                if (nextBabyList.isNotEmpty()) nextBabyList.removeAt(0)
                                logs.add("⚠️ ${venture.name}: Untended newborn! $deadCount young ${grp.type} died due to lack of Livestock Workers.")
                            }
                        }

                        // 3. Cleanliness mortality
                        if (isDirtHazardous && currentMature > 0) {
                            val deadAnimals = (currentMature * cropMortalityRate).toInt().coerceAtLeast(1)
                            currentMature = (currentMature - deadAnimals).coerceAtLeast(0)
                            logs.add("⚠️ ${venture.name}: Unsanitary pastures! $deadAnimals mature ${grp.type}(s) died from accumulated waste.")
                        }

                        // 4. Produce Collection (MATURED ONES ONLY!)
                        if (hasLivestockWorker && currentMature > 0) {
                            when {
                                grp.type.contains("Cow", ignoreCase = true) -> {
                                    // Cow brings high weekly revenue from milk + stores milk in silo
                                    val milkRevenue = currentMature * 350.0
                                    livestockRev += milkRevenue * cycleMultiplier
                                    val milkUnits = currentMature * 10.0
                                    val existMilk = invList.indexOfFirst { it.name.equals("Milk", ignoreCase = true) }
                                    if (existMilk >= 0) {
                                        invList[existMilk] = invList[existMilk].copy(quantity = invList[existMilk].quantity + milkUnits)
                                    } else {
                                        invList.add(FarmInventoryItem(id = "inv_milk", name = "Milk", category = "LIVESTOCK_PRODUCT", quantity = milkUnits, unit = "gallons", marketPricePerUnit = 35.0))
                                    }
                                }
                                grp.type.contains("Chicken", ignoreCase = true) -> {
                                    // Chicken produces eggs
                                    val eggRevenue = currentMature * 40.0
                                    livestockRev += eggRevenue * cycleMultiplier
                                    val eggUnits = currentMature * 5.0
                                    val existEgg = invList.indexOfFirst { it.name.equals("Eggs", ignoreCase = true) }
                                    if (existEgg >= 0) {
                                        invList[existEgg] = invList[existEgg].copy(quantity = invList[existEgg].quantity + eggUnits)
                                    } else {
                                        invList.add(FarmInventoryItem(id = "inv_eggs", name = "Eggs", category = "LIVESTOCK_PRODUCT", quantity = eggUnits, unit = "cartons", marketPricePerUnit = 8.0))
                                    }
                                }
                                grp.type.contains("Goat", ignoreCase = true) -> {
                                    // Goat produces milk
                                    val milkRevenue = currentMature * 150.0
                                    livestockRev += milkRevenue * cycleMultiplier
                                }
                                grp.type.contains("Sheep", ignoreCase = true) -> {
                                    // Sheep produces wool + stores wool in silo
                                    val woolRevenue = currentMature * 180.0
                                    livestockRev += woolRevenue * cycleMultiplier
                                    val woolUnits = currentMature * 10.0
                                    val existWool = invList.indexOfFirst { it.name.equals("Wool", ignoreCase = true) }
                                    if (existWool >= 0) {
                                        invList[existWool] = invList[existWool].copy(quantity = invList[existWool].quantity + woolUnits)
                                    } else {
                                        invList.add(FarmInventoryItem(id = "inv_wool", name = "Wool", category = "LIVESTOCK_PRODUCT", quantity = woolUnits, unit = "bundles", marketPricePerUnit = 18.0))
                                    }
                                }
                                grp.type.contains("Pig", ignoreCase = true) -> {
                                    // Pigs are raised for butcher sale or reproduction (no weekly milk/wool)
                                }
                                else -> {
                                    livestockRev += currentMature * grp.weeklyProduceRevenuePerUnit * cycleMultiplier
                                }
                            }
                        }

                        // 5. Reproduction (Even number of mature animals required: 2 -> 1, 4 -> 2 double, 6 -> 3 triple)
                        if (isMonthlyBreedingWeek && currentMature >= 2 && hasLivestockWorker) {
                            val pairs = currentMature / 2
                            val potentialBabies = pairs
                            val actualBabies = if (hasVeterinarian) {
                                potentialBabies
                            } else {
                                (potentialBabies / 2).coerceAtLeast(1)
                            }
                            repeat(actualBabies) { nextBabyList.add(0) }
                            if (hasVeterinarian) {
                                logs.add("🐣 ${venture.name}: Monthly breeding! +$actualBabies newborn ${grp.type}(s) born healthy from $pairs mating pairs ($currentMature mature).")
                            } else {
                                logs.add("⚠️ ${venture.name}: Breeding reduced! Lack of Veterinarian reduced newborn survival to +$actualBabies ${grp.type} ($pairs mating pairs).")
                            }
                        } else if (!hasLivestockWorker && currentMature >= 2) {
                            logs.add("⚠️ ${venture.name}: Livestock untended! 0 Livestock Workers meant no produce was gathered and mating was unsupervised.")
                        }

                        val updatedBabyCount = nextBabyList.size
                        val updatedTotalCount = currentMature + updatedBabyCount
                        grp.copy(
                            count = updatedTotalCount,
                            matureCount = currentMature,
                            babyCount = updatedBabyCount,
                            babyWeeksOld = nextBabyList
                        )
                    } else {
                        grp
                    }
                }
                updatedLivestock = simulatedLivestock
                weeklyLivestockRevenue = livestockRev
                feedExpense = fCost

                // --- 8. Agri-Industrial Processing Unit (Tier 4) ---
                // Without Tractor Operators, money won't be earned from agri-industrial processed goods; every machine needs 1 operator.
                var processedRev = 0.0
                if (venture.tierLevel >= 4 && hasProcessor) {
                    if (venture.farmTractorOperatorCount > 0) {
                        processedRev = Random.nextDouble(4500.0, 9000.0) * cycleMultiplier * truckBonus * operatorMachineRatio
                    } else {
                        logs.add("⚠️ ${venture.name}: Processing halted! Agri-Industrial Processing Unit requires a Tractor Operator to run.")
                    }
                }
                weeklyProcessedRevenue = processedRev

                // 9. Cold Storage Spoilage Prevention
                if (!hasColdStorage) {
                    for (i in invList.indices) {
                        val itm = invList[i]
                        if (itm.category == "CROP" && itm.quantity > 50) {
                            val spoiled = itm.quantity * 0.05
                            invList[i] = itm.copy(quantity = itm.quantity - spoiled)
                        }
                    }
                }
                currentInventory = invList

                var totalFarmGross = weeklyRawCropsRevenue + weeklyFruitsRevenue + weeklyLivestockRevenue + weeklyProcessedRevenue

                // --- 10. Farm Manager Engine ---
                // Farm manager is needed for every 10 staff else expected sales per week will not be met
                val totalFarmStaff = venture.farmHandCount + venture.farmTractorOperatorCount + venture.farmLivestockWorkerCount + venture.farmDriverCount + venture.farmVeterinarianCount
                val requiredManagers = if (totalFarmStaff > 0) kotlin.math.ceil(totalFarmStaff / 10.0).toInt() else 0
                if (requiredManagers > 0 && venture.farmManagerCount < requiredManagers) {
                    val managerCoverage = (venture.farmManagerCount.toDouble() / requiredManagers.toDouble()).coerceIn(0.40, 1.0)
                    totalFarmGross *= managerCoverage
                    val lostPercent = ((1.0 - managerCoverage) * 100).toInt()
                    logs.add("⚠️ ${venture.name}: Manager deficit! Operation has $totalFarmStaff field staff but only ${venture.farmManagerCount}/$requiredManagers Farm Managers. Wholesale sales dropped by $lostPercent%.")
                }

                actualGross = totalFarmGross
                actualExpenses = landRentExpense + machineUpkeepExpense + farmPayrollExpense + feedExpense + seedExpense

            } else if (venture.type == "Hotel" || venture.type == "Hotel Chain") {
                // --- Hotel Realism Simulation ---
                val existingRooms = if (venture.hotelRooms.isEmpty()) getDefaultHotelRooms() else venture.hotelRooms
                val unlockedRooms = existingRooms.filter { it.isUnlocked }
                val effectiveRoomCount = unlockedRooms.size.coerceIn(2, 20)

                // Seasonality & Summer Vacation Period
                val isSummerVacation = state.currentWeek in 22..35
                val hotelSeasons = listOf(
                    if (isSummerVacation) "Peak Summer Vacation Period (High Tourist Inflow & Maximum Lodges)"
                    else "Standard Travel Season (Regular Business & Leisure)",
                    "Autumn Weekend Getaways (+5% Occupancy)",
                    "Winter Low Season (-15% Occupancy)",
                    "Spring Cultural Festival (+10% Occupancy)"
                )
                hotelSeasonStr = if (isSummerVacation) "Peak Summer Vacation Period (High Tourist Inflow & Maximum Lodges)"
                                 else hotelSeasons[((state.currentWeek / 13) + kotlin.math.abs(venture.id.hashCode())) % hotelSeasons.size]
                val seasonOccDelta = when {
                    isSummerVacation -> 0.25f
                    hotelSeasonStr.contains("+10%") -> 0.10f
                    hotelSeasonStr.contains("+5%") -> 0.05f
                    hotelSeasonStr.contains("-15%") -> -0.15f
                    else -> 0.0f
                }

                // Global Occupancy Rate
                val activeAmenities = venture.hotelAmenities.filter { it.isOwned }
                val amenityOccBonus = activeAmenities.sumOf { it.occupancyBonus.toDouble() }.toFloat()
                var baseOccupancy = 0.50f + (updatedHotelReputation * 0.15f) + (updatedHotelCleanliness * 0.10f) + amenityOccBonus + seasonOccDelta
                if (venture.hotelGeneralManagerCount > 0) {
                    baseOccupancy += 0.06f
                }
                val globalOccupancy = baseOccupancy.coerceIn(0.20f, 0.98f)
                updatedHotelOccupancy = globalOccupancy

                // Booking / Lodging Demand based on recurring customers & summer surge
                // Maximum hotel capacity: up to 7 customer bookings/lodges per room per week
                val maxPossibleLodges = effectiveRoomCount * 7
                // Base demand driven by recurring customers (e.g. 5 customers per room = 10 lodges for 2 rooms)
                val summerDemandMult = if (isSummerVacation) 1.50f else 1.0f
                // If player has the full 20 rooms, standard demand gives a healthy baseline, but only during summer vacation do they get maximum lodges/money
                val totalDemandLodges = ((updatedRecurringCustomers * summerDemandMult * (0.80f + globalOccupancy * 0.40f)).toInt()).coerceAtLeast(effectiveRoomCount * 2)

                // Capacity vs Demand constraint:
                // When there is a lot of recurring customers and less room for them to occupy, they start decreasing!
                val totalLodgesHosted: Int
                if (totalDemandLodges > maxPossibleLodges) {
                    val turnedAwayGuests = totalDemandLodges - maxPossibleLodges
                    val lostCustomers = (turnedAwayGuests * 0.18f).toInt().coerceIn(1, (updatedRecurringCustomers * 0.30f).toInt().coerceAtLeast(1))
                    updatedRecurringCustomers = (updatedRecurringCustomers - lostCustomers).coerceAtLeast(4)
                    logs.add("⚠️ Hotel Capacity Shortage: $turnedAwayGuests prospective guests were turned away because all $effectiveRoomCount rooms were fully booked! Lost $lostCustomers recurring customers. Unlock and furnish more rooms to expand capacity.")
                    totalLodgesHosted = maxPossibleLodges
                } else {
                    totalLodgesHosted = totalDemandLodges
                    // Ample room capacity and good cleanliness gently attracts new recurring customers
                    if (updatedHotelCleanliness >= 0.80f && updatedHotelCondition >= 0.70f && Random.nextFloat() < 0.40f) {
                        updatedRecurringCustomers = (updatedRecurringCustomers + 1).coerceAtMost(250)
                    }
                }

                // Staff requirements scale with number of lodges and amenities
                // "the more the lodge, the more staffs needed and amenities"
                val requiredHousekeepers = kotlin.math.ceil(totalLodgesHosted / 12.0).toInt().coerceAtLeast(kotlin.math.ceil(effectiveRoomCount / 5.0).toInt()).coerceAtLeast(1)
                val housekeeperDeficit = (requiredHousekeepers - venture.hotelHousekeeperCount).coerceAtLeast(0)

                val requiredFrontDesk = kotlin.math.ceil(totalLodgesHosted / 18.0).toInt().coerceAtLeast(1)
                val frontDeskDeficit = (requiredFrontDesk - venture.hotelFrontDeskCount).coerceAtLeast(0)

                val requiredMaintenance = kotlin.math.ceil((effectiveRoomCount + activeAmenities.size) / 6.0).toInt().coerceAtLeast(1)
                val maintenanceDeficit = (requiredMaintenance - venture.hotelMaintenanceTechCount).coerceAtLeast(0)

                // Cleanliness & condition updates
                if (housekeeperDeficit > 0) {
                    updatedHotelCleanliness = (updatedHotelCleanliness - (housekeeperDeficit * 0.08f)).coerceAtLeast(0.20f)
                    if (updatedRecurringCustomers > 6) {
                        val lostGuests = (updatedRecurringCustomers * 0.08).toInt().coerceAtLeast(1)
                        updatedRecurringCustomers = (updatedRecurringCustomers - lostGuests).coerceAtLeast(4)
                        logs.add("⚠️ ${venture.name}: Staffing bottleneck! Housekeeper shortage for $totalLodgesHosted lodges caused dirty rooms. Lost $lostGuests recurring guests.")
                    }
                } else {
                    updatedHotelCleanliness = (updatedHotelCleanliness + 0.04f).coerceAtMost(1.0f)
                }

                if (maintenanceDeficit > 0) {
                    updatedHotelCondition = (updatedHotelCondition - (0.015f + maintenanceDeficit * 0.015f)).coerceAtLeast(0.25f)
                } else {
                    updatedHotelCondition = (updatedHotelCondition + 0.02f).coerceAtMost(1.0f)
                }

                // Security effect: reduces theft and vandalism incidents
                val theftRisk = if (effectiveRoomCount > 6 && venture.hotelSecurityCount == 0) 0.12f else 0.02f
                if (Random.nextFloat() < theftRisk) {
                    updatedHotelCondition = (updatedHotelCondition - 0.05f).coerceAtLeast(0.25f)
                    updatedHotelIncident = "Petty theft and property damage occurred (No Security patrol)."
                    logs.add("⚠️ Security Incident at ${venture.name}: Minor vandalism and theft occurred due to lack of security guard presence.")
                } else {
                    updatedHotelIncident = ""
                }

                // Amenity needs:
                if (totalLodgesHosted > 25 && !activeAmenities.any { it.id == "amen_restaurant" }) {
                    logs.add("🍽️ Guest Feedback: Heavy lodging traffic (${totalLodgesHosted} weekly bookings) created strong demand for dining, but no Restaurant amenity is present.")
                }
                if (totalLodgesHosted > 45 && activeAmenities.size < 3) {
                    logs.add("⭐ Guest Feedback: High-tier guests expect leisure amenities (Pool, Gym, Spa). Hotel prestige growth dampened.")
                    updatedHotelReputation = (updatedHotelReputation - 0.02f).coerceAtLeast(0.30f)
                }

                // Reputation Calculation
                val serviceScore = (1.0f - (frontDeskDeficit * 0.09f) - (housekeeperDeficit * 0.09f)).coerceIn(0.35f, 1.0f)
                val targetRep = (updatedHotelCleanliness * 0.35f + updatedHotelCondition * 0.35f + serviceScore * 0.30f).coerceIn(0.25f, 1.0f)
                updatedHotelReputation = (updatedHotelReputation * 0.85f + targetRep * 0.15f).coerceIn(0.25f, 1.0f)

                // Individual Room Lodging Simulation
                // Distribute totalLodgesHosted across operational rooms
                val baseLodgesPerRoom = if (effectiveRoomCount > 0) totalLodgesHosted / effectiveRoomCount else 0
                val remainderLodges = if (effectiveRoomCount > 0) totalLodgesHosted % effectiveRoomCount else 0
                var assignedIndex = 0

                var totalLodgesCalculated = 0
                var totalRoomRevenue = 0.0
                val simulatedRooms = existingRooms.map { room ->
                    if (!room.isUnlocked) {
                        room.copy(weeklyOccupancyRate = 0f, lodgesThisWeek = 0, weeklyRevenue = 0.0)
                    } else {
                        var roomOutOfOrder = room.isOutOfOrder
                        var reason = room.outOfOrderReason
                        var roomCond = room.condition

                        // Breakdown check if maintenance deficit
                        if (!roomOutOfOrder && maintenanceDeficit > 0 && Random.nextFloat() < (0.04f * maintenanceDeficit)) {
                            roomOutOfOrder = true
                            reason = "HVAC / Plumbing Breakdown (Needs Maintenance Repair)"
                            logs.add("🔧 ${venture.name} Room ${room.roomNumber} has suffered a breakdown and is out of order.")
                        } else if (roomOutOfOrder && venture.hotelMaintenanceTechCount > 0 && Random.nextFloat() < 0.65f) {
                            roomOutOfOrder = false
                            reason = ""
                            roomCond = 0.95f
                            logs.add("✅ Maintenance staff repaired ${venture.name} Room ${room.roomNumber}. It is back in service.")
                        }

                        if (roomOutOfOrder) {
                            room.copy(
                                isOutOfOrder = true,
                                outOfOrderReason = reason,
                                weeklyOccupancyRate = 0f,
                                lodgesThisWeek = 0,
                                weeklyRevenue = 0.0
                            )
                        } else {
                            val extraLodge = if (assignedIndex < remainderLodges) 1 else 0
                            assignedIndex++
                            val lodges = (baseLodgesPerRoom + extraLodge).coerceIn(1, 7)
                            // Remittance = Number of customer bookings/lodges * rate per room
                            // E.g., 5 customers booking a $520 luxury room = $2,600! 2 rooms = $5,200!
                            val roomWeeklyRev = lodges * room.potentialWeeklyRate
                            totalLodgesCalculated += lodges
                            totalRoomRevenue += roomWeeklyRev

                            room.copy(
                                condition = (roomCond * 0.992f).coerceAtLeast(0.35f),
                                isOutOfOrder = false,
                                outOfOrderReason = "",
                                weeklyOccupancyRate = (lodges / 7.0f).coerceIn(0f, 1f),
                                lodgesThisWeek = lodges,
                                weeklyRevenue = roomWeeklyRev
                            )
                        }
                    }
                }
                updatedHotelRooms = simulatedRooms
                updatedHotelLodges = totalLodgesCalculated
                hotelRoomRev = totalRoomRevenue

                // 6. Food & Amenity Revenue
                // Restaurant: Requires Chef
                val hasRestaurant = activeAmenities.any { it.id == "amen_restaurant" }
                if (hasRestaurant) {
                    if (venture.hotelChefCount > 0) {
                        val waiterMult = 1.0 + (venture.hotelWaiterCount.coerceAtMost(3) * 0.35)
                        hotelFoodRev = 1800.0 * waiterMult * globalOccupancy
                    } else {
                        hotelFoodRev = 0.0
                        logs.add("🍽️ Restaurant at ${venture.name} was idle: No Head Chef hired to prepare meals.")
                    }
                } else {
                    hotelFoodRev = 0.0
                }

                var directAmenityRev = 0.0
                activeAmenities.forEach { amenity ->
                    if (amenity.id != "amen_restaurant") {
                        directAmenityRev += amenity.weeklyRevenueBonus * (0.75 + 0.35 * globalOccupancy)
                    }
                }
                hotelAmenityRev = hotelFoodRev + directAmenityRev

                // 7. Expenses
                hotelPayroll = (venture.hotelHousekeeperCount * HOTEL_HOUSEKEEPER_SALARY) +
                               (venture.hotelFrontDeskCount * HOTEL_FRONT_DESK_SALARY) +
                               (venture.hotelChefCount * HOTEL_CHEF_SALARY) +
                               (venture.hotelWaiterCount * HOTEL_WAITER_SALARY) +
                               (venture.hotelSecurityCount * HOTEL_SECURITY_SALARY) +
                               (venture.hotelMaintenanceTechCount * HOTEL_MAINT_TECH_SALARY) +
                               (venture.hotelGeneralManagerCount * HOTEL_MANAGER_SALARY)

                val roomMaintenance = effectiveRoomCount * 18.0
                val amenityUpkeep = activeAmenities.sumOf { it.weeklyUpkeep }
                val buildingMaintenance = 400.0 + (effectiveRoomCount * 25.0)
                hotelSupplies = (hotelRoomRev + hotelFoodRev) * 0.09
                hotelUtilities = effectiveRoomCount * 35.0
                hotelBldgMaint = buildingMaintenance + roomMaintenance
                hotelAmenityMaint = amenityUpkeep

                actualGross = hotelRoomRev + hotelAmenityRev
                actualExpenses = hotelPayroll + hotelSupplies + hotelUtilities + hotelBldgMaint + hotelAmenityMaint

            } else if (venture.type == "Crypto Token") {
                // --- Crypto Token Realism: 5 Core Business Elements ---
                val cryptoCycles = listOf(
                    "Bullish Accumulation",
                    "Crypto Bull Run Mania",
                    "Crab Consolidation",
                    "Bear Market Winter"
                )
                updatedCycle = cryptoCycles[((state.currentWeek / 6) + kotlin.math.abs(venture.id.hashCode())) % cryptoCycles.size]
                val cycleVolMult = when (updatedCycle) {
                    "Crypto Bull Run Mania" -> 1.75
                    "Bullish Accumulation" -> 1.25
                    "Crab Consolidation" -> 0.85
                    "Bear Market Winter" -> 0.45
                    else -> 1.0
                }

                // 1. Developers: capacity, upkeep & service launch boost
                val devCount = venture.cryptoDevelopersCount.coerceAtLeast(0)
                val devCapacity = (devCount * 2500).coerceAtLeast(400)
                val devPayroll = devCount * CRYPTO_DEV_SALARY

                // 2. Security: tier lookup, upkeep & exploit check
                val secTier = CRYPTO_SECURITY_TIERS.find { it.tier == venture.cryptoSecurityTier } ?: CRYPTO_SECURITY_TIERS.first()
                val secExpense = secTier.weeklyCost
                var securityExploitOccurred = false
                if (Random.nextDouble(0.0, 100.0) < secTier.vulnerabilityPercent) {
                    securityExploitOccurred = true
                    val drainPercent = Random.nextDouble(0.15, 0.35)
                    val drainedAmount = updatedLp * drainPercent
                    updatedLp = (updatedLp - drainedAmount).coerceAtLeast(1000.0)
                    updatedPrice = (updatedPrice * (1.0 - drainPercent * 0.85)).coerceAtLeast(0.001)
                    updatedHackLoss = drainedAmount
                    logs.add("🚨 ${venture.name}: Smart contract flash-loan exploit! Drained $${String.format("%,.0f", drainedAmount)} from liquidity pool. Upgrade security to prevent exploits!")
                    updatedLesson = "Why did my coin fall? Smart contract exploit! Upgraded audits and multi-sig security prevent devastating liquidity drains."
                }

                // 3. Website / App: tier lookup, upkeep & user cap
                val webTier = CRYPTO_WEBSITE_APP_TIERS.find { it.tier == venture.cryptoWebsiteAppTier } ?: CRYPTO_WEBSITE_APP_TIERS.first()
                val webExpense = webTier.weeklyCost
                val effectiveUserCap = kotlin.math.min(webTier.userCapacity, devCapacity)

                // 4. Marketing: tier lookup, budget & user growth
                val mktTier = CRYPTO_MARKETING_TIERS.find { it.tier == venture.cryptoMarketingTier } ?: CRYPTO_MARKETING_TIERS.first()
                val mktExpense = mktTier.weeklyCost
                val newUsersOrganic = Random.nextInt(mktTier.weeklyUserGrowthMin, mktTier.weeklyUserGrowthMax + 1)
                var currentUsers = (venture.cryptoActiveUsers + newUsersOrganic).coerceAtMost(effectiveUserCap)

                // If user capacity exceeded, unpatched bugs/overloaded servers cause churn
                if (venture.cryptoActiveUsers > effectiveUserCap) {
                    val churn = (venture.cryptoActiveUsers * 0.08).toInt().coerceAtLeast(10)
                    currentUsers = (currentUsers - churn).coerceAtLeast(50)
                    logs.add("⚠️ ${venture.name}: Server overloaded! Active users exceed website & dev capacity. User churn occurred.")
                    updatedLesson = "Why did users churn? Website and developer capacity was bottlenecked. Upgrade app infrastructure and hire developers."
                }

                // 5. Liquidity & Trading: slippage factor
                val lpDepth = updatedLp
                val slippagePenalty = if (lpDepth < 10000.0) {
                    0.55 // High slippage limits DEX trading
                } else if (lpDepth < 25000.0) {
                    0.85
                } else {
                    1.15 // Deep liquidity attracts institutional flow
                }

                if (lpDepth < 10000.0 && !securityExploitOccurred) {
                    updatedLesson = "Why can't people trade much? Liquidity is too low. Shallow pools cause high slippage, suppressing trading volume."
                }

                // Service launch boost effect
                var serviceSurgeMult = 1.0
                var remainingServiceWeeks = venture.cryptoNewServiceLaunchBoostWeeks
                if (remainingServiceWeeks > 0) {
                    serviceSurgeMult = 1.35
                    remainingServiceWeeks -= 1
                    updatedPrice *= 1.04
                    if (updatedLesson.isBlank() || updatedLesson.startsWith("Operating")) {
                        updatedLesson = "Why did the token jump after our new service launched? More people now have a utility reason to hold and use the token."
                    }
                }
                updatedServiceBoostWeeks = remainingServiceWeeks

                // Trading volume calculation
                val baseTradingVol = (currentUsers * 85.0 * cycleVolMult * mktTier.volumeMultiplier * slippagePenalty * serviceSurgeMult).coerceAtLeast(8000.0)
                updatedVolume = (baseTradingVol * Random.nextDouble(0.92, 1.08) * 100.0).roundToInt() / 100.0

                // 0.30% swap fee on DEX trading
                cryptoSwapFees = updatedVolume * 0.003

                // Protocol utility fees ($4.50 / active user / week)
                val protocolUtilityFees = currentUsers * 4.50

                // Staking & DeFi yield if higher tier
                if (venture.tierLevel >= 2) {
                    val stakingYieldRate = 0.0006
                    cryptoStakingRev = venture.cryptoStakedTokens * stakingYieldRate * updatedPrice
                }
                if (venture.tierLevel >= 3) {
                    cryptoDeFiRev = (updatedLp * 0.012) * cycleVolMult
                }

                // Market price drift from supply & demand
                val circulating = venture.cryptoCirculatingSupply.coerceAtLeast(100000.0)
                val demandPressure = (currentUsers.toDouble() / circulating) * 10000.0 * cycleVolMult * serviceSurgeMult
                val naturalDrift = when (updatedCycle) {
                    "Crypto Bull Run Mania" -> Random.nextDouble(1.02, 1.07)
                    "Bullish Accumulation" -> Random.nextDouble(1.002, 1.03)
                    "Crab Consolidation" -> Random.nextDouble(0.985, 1.015)
                    "Bear Market Winter" -> Random.nextDouble(0.93, 0.985)
                    else -> 1.0
                }
                if (!securityExploitOccurred) {
                    updatedPrice = ((updatedPrice * naturalDrift * (1.0 + (demandPressure - 1.0).coerceIn(-0.04, 0.05))) * 10000.0).roundToInt() / 10000.0
                    updatedPrice = updatedPrice.coerceAtLeast(0.001)
                }

                // Update sentiment
                val sentimentBonus = (if (devCount >= 1) 0.02f else -0.05f) + (if (mktTier.tier >= 2) 0.02f else 0.0f)
                updatedSentiment = (updatedSentiment + sentimentBonus).coerceIn(0.20f, 0.98f)

                // Check exchange listing eligibility
                val listingStatus = checkCryptoListingEligibility(venture.copyCrypto(
                    cryptoDevelopersCount = devCount,
                    cryptoSecurityTier = secTier.tier,
                    cryptoWebsiteAppTier = webTier.tier,
                    cryptoActiveUsers = currentUsers,
                    cryptoLiquidityPoolUsd = updatedLp
                ))
                val isListed = venture.cryptoIsMarketListed
                val listingNotice = if (isListed) {
                    "🟢 Listed on Cryptocurrency Market as ${venture.cryptoTokenTicker}. Tradable against USD & BTC."
                } else if (listingStatus.isEligible) {
                    "✨ ELIGIBLE FOR EXCHANGE LISTING! Security audited, liquidity verified, and community active."
                } else {
                    "Requires 1+ Devs (${if (listingStatus.hasDev) "✓" else "✗"}), Security Tier 2+ (${if (listingStatus.hasSecurity) "✓" else "✗"}), 500+ Users (${listingStatus.userCount}/500), and $15,000+ Liquidity ($${String.format("%,.0f", listingStatus.liquidityAmount)}/$15k)."
                }

                cryptoPayroll = devPayroll
                cryptoInfra = webExpense
                cryptoSecurityExp = secExpense
                val cryptoMarketingCost = mktExpense

                actualGross = cryptoSwapFees + protocolUtilityFees + cryptoStakingRev + cryptoDeFiRev
                actualExpenses = cryptoPayroll + cryptoInfra + cryptoSecurityExp + cryptoMarketingCost

                // Update crypto-specific mutable tracking variables
                cryptoSwapFees = (cryptoSwapFees * 100.0).roundToInt() / 100.0
                updatedProtocolFees = (protocolUtilityFees * 100.0).roundToInt() / 100.0
                updatedActiveUsers = currentUsers
                updatedListingNotice = listingNotice
                if (updatedLesson.isBlank()) {
                    updatedLesson = "Operating smoothly. Keep upgrading security, infrastructure, and liquidity to qualify for exchange listing."
                }

            } else {
                val baseGross = venture.weeklyGrossRevenue.coerceAtLeast(1200.0)
                actualGross = baseGross * cycleMultiplier * weeklyVariance
                val baseExpenses = venture.weeklyOperatingExpenses.coerceAtLeast(800.0)
                actualExpenses = baseExpenses
            }

            val weeklyNet = actualGross - actualExpenses

            // 2. Share profit or handle loss
            var playerShareOfNet = weeklyNet
            var investorCut = 0.0
            var publicDividendCut = 0.0

            if (weeklyNet > 0) {
                val totalInvEq = venture.totalInvestorEquityPercent
                if (totalInvEq > 0.0) {
                    investorCut = weeklyNet * totalInvEq
                }
                if (venture.isListedOnWeShare && venture.weShareTotalSharesIssued > 0) {
                    val publicSoldRatio = (venture.weSharePublicSharesSold.toDouble() / venture.weShareTotalSharesIssued.toDouble()).coerceIn(0.0, 1.0)
                    publicDividendCut = weeklyNet * (venture.weShareListedEquityPercent * publicSoldRatio)
                }
                playerShareOfNet = weeklyNet * venture.founderEquityPercent
                totalEnterpriseProfitsEarned += playerShareOfNet

                // If player lost control over the business, weekly profits based on equity owned are deposited directly into personal cash
                if (!venture.isPlayerInControl) {
                    totalPlayerCash += playerShareOfNet
                    logs.add("💼 PASSIVE DIVIDEND: Received $${String.format("%,.0f", playerShareOfNet)} weekly dividends from non-controlling ${(venture.founderEquityPercent * 100).toInt()}% stake in ${venture.name}.")
                }
                // Note: If player is in control, profit is retained inside the business account (venture.accumulatedProfit) for player to withdraw or reinvest.
            } else {
                // Operating loss: decreases accumulatedProfit into the red (business owes debt).
                // Do NOT automatically seize player's personal cash!
                // The company account goes into debt; the player can choose to pay the debt with personal funds or shut down.
                val loss = -weeklyNet
                logs.add("⚠️ ${venture.name} ran an operating loss of -$${String.format("%,.0f", loss)} this week. Company account balance is in the red (owing debt).")
            }

            // 3. Operational Seasoning & Customer Retention
            val updatedWeeksActive = venture.weeksActive + 1
            val updatedCustomerLoyalty = if (weeklyNet > 0) {
                (venture.customerLoyalty + 0.005f).coerceAtMost(0.95f)
            } else {
                (venture.customerLoyalty - 0.02f).coerceAtLeast(0.15f)
            }

            // 4. Regulatory Screening Progression (2-week audit)
            var updatedScreeningStatus = venture.screeningStatus
            var updatedScreeningWeeks = venture.screeningWeeksRemaining
            var updatedApprovedPrice = venture.approvedSharePrice
            var updatedApprovedTotalShares = venture.approvedTotalShares
            var updatedApprovedListedShares = venture.approvedListedSharesUnits
            var updatedScreeningFeedback = venture.screeningFeedback

            if (venture.screeningStatus == "UNDER_REVIEW") {
                val newWeeksLeft = (venture.screeningWeeksRemaining - 1).coerceAtLeast(0)
                updatedScreeningWeeks = newWeeksLeft
                if (newWeeksLeft <= 0) {
                    // 2-week audit period concluded: Evaluate all-time and recent operational health
                    val isCrypto = venture.type == "Crypto Token"
                    val minAge = if (isCrypto) 35 else 25
                    val minMargin = 0.15
                    val minLoyalty = if (isCrypto) 0.50f else 0.40f
                    val currentMargin = if (actualGross > 0) weeklyNet / actualGross else 0.0
                    val hasCleanDebt = venture.outstandingLoan <= 0.0
                    val isHealthy = hasCleanDebt && weeklyNet >= 0 && (currentMargin >= 0.08 || weeklyNet >= 300.0)

                    if (isHealthy) {
                        updatedScreeningStatus = "APPROVED"
                        val valuation = calculateValuationForEquity(venture, venture.screeningRequestedEquityPercent)
                        updatedApprovedPrice = valuation.sharePrice
                        updatedApprovedTotalShares = valuation.totalSharesIssued
                        updatedApprovedListedShares = valuation.targetSharesToSell
                        updatedScreeningFeedback = "Regulatory screening passed with honors. Enterprise fundamentals, customer retention, and solvency verified."

                        val boardName = if (isCrypto) "Genesis Protocol Listing Council" else "WeShare Listing Qualifications Board"
                        val listingExchange = if (isCrypto) "Genesis Protocol Exchange" else "WeShare Public Stock Exchange"
                        val auditNotice = MailMessage(
                            id = "audit_approval_" + UUID.randomUUID().toString().take(6),
                            sender = boardName,
                            subject = "✅ IPO Screening Approved: ${venture.name}",
                            body = "Dear Founder,\n\nThe regulatory audit committee has concluded the rigorous 2-week operational screening for ${venture.name}.\n\nAUDIT DETERMINATION: APPROVED FOR PUBLIC LISTING\n• Exchange: $listingExchange\n• Target Equity Tranche: ${(venture.screeningRequestedEquityPercent * 100).toInt()}%\n• Approved Share Unit Price: $${String.format("%.2f", valuation.sharePrice)} / share\n• Total Share Units Authorized: ${String.format("%,d", valuation.totalSharesIssued)} units\n• Public Offering Float Units: ${String.format("%,d", valuation.targetSharesToSell)} units\n• Estimated Gross Capital: $${String.format("%,.0f", valuation.estimatedCapitalRaised)}\n• Regulatory Lockup: 60 calendar days\n\nYou retain 100% discretion over market timing. You can formally launch the listing at your convenience inside your Ventures app.\n\nRespectfully,\nAudit Committee, $boardName",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 12,
                            expiresYear = state.currentYear
                        )
                        generatedMails.add(auditNotice)
                        logs.add("✅ WeShare Audit Passed: ${venture.name} has been approved for public listing at $${String.format("%.2f", valuation.sharePrice)}/share.")
                    } else {
                        updatedScreeningStatus = "REJECTED"
                        val reason = when {
                            updatedWeeksActive < minAge -> "Insufficient operational history (${updatedWeeksActive}/$minAge weeks required)."
                            hasCleanDebt.not() -> "Unresolved corporate debt balance ($${String.format("%,.0f", venture.outstandingLoan)})."
                            currentMargin < minMargin -> "Sub-standard profit margin (${(currentMargin * 100).toInt()}% vs 15% threshold)."
                            updatedCustomerLoyalty < minLoyalty -> "Low customer retention index (${(updatedCustomerLoyalty * 100).toInt()}% vs ${(minLoyalty * 100).toInt()}% threshold)."
                            else -> "Inconsistent weekly cashflow."
                        }
                        updatedScreeningFeedback = "Audit Declined: $reason"
                        val boardName = if (isCrypto) "Genesis Protocol Listing Council" else "WeShare Listing Qualifications Board"
                        val declineNotice = MailMessage(
                            id = "audit_decline_" + UUID.randomUUID().toString().take(6),
                            sender = boardName,
                            subject = "❌ IPO Screening Audit Declined: ${venture.name}",
                            body = "Dear Founder,\n\nThe regulatory audit committee has completed its review of ${venture.name}.\n\nSTATUS: NOT APPROVED AT THIS TIME\nReason: $reason\n\nTo protect public retail investors, listings require sustained financial maturity. We encourage you to strengthen operations and reapply once benchmarks are met.\n\nRespectfully,\nAudit Committee, $boardName",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 8,
                            expiresYear = state.currentYear
                        )
                        generatedMails.add(declineNotice)
                        logs.add("❌ Audit Declined: ${venture.name} did not meet listing standards: $reason")
                    }
                }
            }

            // 5. WeShare IPO Dynamics: Price Fluctuations & Market Demand
            var newSharesSold = venture.weSharePublicSharesSold
            var currentSharePrice = venture.weShareSharePrice.coerceAtLeast(2.50)

            if (venture.isListedOnWeShare) {
                // Unit share price moves dynamically based on business performance
                val performanceDrift = if (weeklyNet > 0) {
                    val marginBoost = if (actualGross > 0 && (weeklyNet / actualGross) >= 0.25) 1.015 else 1.005
                    Random.nextDouble(1.008, 1.028) * marginBoost
                } else {
                    Random.nextDouble(0.93, 0.985)
                }
                currentSharePrice = ((currentSharePrice * performanceDrift) * 100.0).roundToInt() / 100.0
                currentSharePrice = currentSharePrice.coerceAtLeast(1.0)

                // Share absorption by public retail investors
                if (venture.weSharePublicSharesSold < venture.weShareTotalSharesIssued) {
                    val remainingShares = venture.weShareTotalSharesIssued - venture.weSharePublicSharesSold
                    val demandRate = if (weeklyNet > 0) {
                        when {
                            weeklyNet > 5000.0 -> Random.nextDouble(0.20, 0.38)
                            weeklyNet > 1000.0 -> Random.nextDouble(0.12, 0.24)
                            else -> Random.nextDouble(0.06, 0.15)
                        }
                    } else {
                        Random.nextDouble(0.0, 0.02) // Shares stall when business does poorly
                    }

                    val sharesPurchasedThisWeek = (remainingShares * demandRate).toLong().coerceIn(0L, remainingShares)
                    if (sharesPurchasedThisWeek > 0L) {
                        val ipoProceeds = sharesPurchasedThisWeek * currentSharePrice
                        totalIpoCash += ipoProceeds
                        newSharesSold += sharesPurchasedThisWeek
                        logs.add("📈 Market Absorption: Public investors purchased ${String.format("%,d", sharesPurchasedThisWeek)} shares of ${venture.name} at $${String.format("%.2f", currentSharePrice)}, raising $${String.format("%,.0f", ipoProceeds)}.")
                    }
                }

                // Sync linked Stock on the WeShare exchange
                val stockIndex = updatedStocks.indexOfFirst { it.id == venture.weShareStockId || it.playerVentureId == venture.id }
                if (stockIndex >= 0) {
                    val existingStock = updatedStocks[stockIndex]
                    val isComplete = newSharesSold >= venture.weShareTotalSharesIssued
                    updatedStocks[stockIndex] = existingStock.copy(
                        currentPrice = currentSharePrice,
                        ipoSharesSold = newSharesSold,
                        ipoCompleted = isComplete,
                        history = (existingStock.history + currentSharePrice).takeLast(20)
                    )
                }
            }

            // 6. SCC Commercial Licensing Compliance & Regulator Checks
            var updatedHasLicense = venture.hasLegalLicense
            var updatedSccWarning = venture.sccWarningReceived
            var updatedSccDeadline = venture.sccDeadlineWeeks
            var updatedShutDown = venture.isShutDownByRegulators

            val sccThreshold = when (venture.type) {
                "Taxi Fleet" -> 250
                "Farming" -> 150
                "Hotel", "Hotel Chain" -> 100
                "Crypto Token" -> 100
                "Restaurant" -> 120
                else -> 200
            }

            if (!updatedHasLicense && !updatedShutDown) {
                if (updatedRecurringCustomers >= sccThreshold) {
                    if (!updatedSccWarning) {
                        updatedSccWarning = true
                        updatedSccDeadline = 4
                        generatedMails.add(
                            MailMessage(
                                id = "mail_scc_" + UUID.randomUUID().toString().take(6),
                                sender = "State Commerce & Securities Commission (SCC)",
                                subject = "🚨 URGENT: SCC Formal Notice to Cease & Desist — Unlicensed Operations (${venture.name})",
                                body = "OFFICIAL REGULATORY NOTICE\n\nTo the Managing Director of ${venture.name}:\n\nAn administrative audit by the State Commerce & Securities Commission (SCC) has confirmed that ${venture.name} has exceeded state statutory consumer thresholds (${String.format("%,d", updatedRecurringCustomers)} active customers vs threshold of ${String.format("%,d", sccThreshold)}) while operating WITHOUT an official State Operating & Commercial Enterprise License.\n\nYou have FOUR (4) WEEKS from this notice to file and pay for your commercial license ($${String.format("%,.0f", venture.legalLicenseCost)}) in the Ventures Portal.\n\nFAILURE TO ACQUIRE THE LICENSE BEFORE THE DEADLINE WILL RESULT IN IMMEDIATE ADMINISTRATIVE SHUTDOWN, FORFEITURE OF ENTERPRISE ASSETS, AND COMPLETE DISSOLUTION UNDER STATE STATUTE §48-B.\n\nState Commerce & Securities Commission\nEnforcement & Licensing Division",
                                type = "SCC_LICENSE_WARNING",
                                ventureId = venture.id,
                                expiresWeek = state.currentWeek + 4,
                                expiresYear = state.currentYear
                            )
                        )
                        logs.add("🚨 SCC NOTICE: ${venture.name} served with formal Cease & Desist! Must acquire Operating License within 4 weeks to avoid shutdown.")
                    } else {
                        updatedSccDeadline -= 1
                        if (updatedSccDeadline <= 0) {
                            updatedShutDown = true
                            actualGross = 0.0
                            actualExpenses = 0.0
                            generatedMails.add(
                                MailMessage(
                                    id = "mail_scc_closure_" + UUID.randomUUID().toString().take(6),
                                    sender = "State Commerce Commission - Enforcement Division",
                                    subject = "⛔ FORCED REGULATORY CLOSURE: ${venture.name} Shuttered by State Order",
                                    body = "FINAL ORDER OF FORFEITURE & DISSOLUTION\n\nNotice is hereby served that ${venture.name} has been padlocked and dissolved by court order following the unheeded 4-week compliance grace period for unlicensed operations. All operating rights and assets have been confiscated.",
                                    type = "SCC_CLOSURE_NOTICE",
                                    ventureId = venture.id,
                                    expiresWeek = state.currentWeek + 4,
                                    expiresYear = state.currentYear
                                )
                            )
                            logs.add("⛔ REGULATORY SHUTDOWN: ${venture.name} has been permanently dissolved by the SCC for operating without an official license!")
                        }
                    }
                }
            }

            if (updatedShutDown) {
                actualGross = 0.0
                actualExpenses = 0.0
            }

            // 7. Shareholder Lawsuit Trigger (Breach of Fiduciary Duty if investor money pocketed)
            var updatedLawsuitActive = venture.investorLawsuitActive
            var updatedLawsuitDamages = venture.investorLawsuitDamages

            if (venture.investorEquityPercent > 0.0 && venture.pocketedInvestorCapital > 12000.0 && !updatedLawsuitActive) {
                if (weeklyNet < 0.0 || venture.investedCapital < 2000.0) {
                    updatedLawsuitActive = true
                    updatedLawsuitDamages = (venture.pocketedInvestorCapital * 1.5).roundToInt().toDouble()
                    generatedMails.add(
                        MailMessage(
                            id = "mail_lawsuit_" + UUID.randomUUID().toString().take(6),
                            sender = "Chambers & Vance LLP (Counsel for Minority Shareholders)",
                            subject = "⚖️ LEGAL SUMMONS: Derivative Lawsuit for Misappropriation of Investor Capital (${venture.name})",
                            body = "NOTICE OF CIVIL ACTION\n\nTo the Managing Director of ${venture.name}:\n\nOur clients, holding ${(venture.investorEquityPercent * 100).toInt()}% equity in ${venture.name}, have completed a forensic audit of company balance sheets following operational distress. The audit reveals that $${String.format("%,.0f", venture.pocketedInvestorCapital)} of designated corporate growth capital was improperly withdrawn for personal use outside the business entity.\n\nA derivative lawsuit has been filed for breach of fiduciary duty. Total damages and legal fees assessed: $${String.format("%,.0f", updatedLawsuitDamages)}.\n\nSettle the claim immediately in the Ventures Corporate Portal to avoid asset freeze and criminal referral.\n\nChambers & Vance LLP\nSecurities Litigation Group",
                            type = "INVESTOR_LAWSUIT_NOTICE",
                            ventureId = venture.id,
                            expiresWeek = state.currentWeek + 6,
                            expiresYear = state.currentYear
                        )
                    )
                    logs.add("⚖️ SHAREHOLDER LAWSUIT: Minority investors sued ${venture.name} for $${String.format("%,.0f", updatedLawsuitDamages)} over pocketed growth capital!")
                }
            }

            // 8. Grounded Inbound Investor Deals: Valuation & Share-Price Driven
            var newOffer = venture.pendingInvestorOffer
            if (newOffer != null && (state.currentYear > newOffer.expirationYear || (state.currentYear == newOffer.expirationYear && state.currentWeek > newOffer.expirationWeek))) {
                newOffer = null
            }

            val founderEquity = venture.founderEquityPercent
            val currentEnterpriseValuation = getEnterpriseValuation(venture)
            val isProvenBusiness = updatedWeeksActive >= 12

            // Offers appear if founder holds meaningful equity (> 5%), business is profitable, not in debt, not shut down, and no active offer
            val isCompanyInDebt = venture.accumulatedProfit < 0.0 || venture.outstandingLoan > 0.0
            if (!updatedShutDown && !isCompanyInDebt && newOffer == null && founderEquity > 0.05 && weeklyNet >= 250.0) {
                val offerChance = when {
                    isProvenBusiness && weeklyNet >= 1500.0 -> 0.28f
                    isProvenBusiness -> 0.20f
                    weeklyNet >= 2500.0 -> 0.16f
                    else -> 0.08f
                }

                if (Random.nextFloat() < offerChance) {
                    val distinctInvestors = listOf(
                        Pair("Victor Sterling", "Aetherius Capital"),
                        Pair("Julian Vance", "Titan Crest Capital"),
                        Pair("Evelyn Reed", "Blue Horizon Capital"),
                        Pair("Sterling Cross", "Apex Global Syndicate"),
                        Pair("Dominic Blackwood", "Blackwood Heritage Fund"),
                        Pair("Helena Rostova", "Boreal Private Equity"),
                        Pair("Harrison Caldwell", "Caldwell Merchant Bank"),
                        Pair("Amara Okafor", "Equinox Venture Partners"),
                        Pair("Kavita Rao", "Indus Crest Capital")
                    )
                    val (invName, firm) = distinctInvestors.random()

                    // Proven businesses attract substantial full buyout offers (~45% of offers)
                    val isBuyout = if (isProvenBusiness && updatedWeeksActive >= 16) {
                        Random.nextFloat() < 0.45f
                    } else {
                        Random.nextFloat() < 0.20f
                    }

                    // Smart valuation pricing based on operational performance:
                    // Underperforming businesses get lowball/distressed pricing; stellar businesses get premium pricing.
                    val currentMargin = if (actualGross > 0) weeklyNet / actualGross else 0.0
                    val isUnderperforming = currentMargin < 0.15 || weeklyNet < 400.0 || updatedCustomerLoyalty < 0.45f || venture.outstandingLoan > 0
                    val isStellar = currentMargin >= 0.28 && weeklyNet >= 2500.0 && updatedCustomerLoyalty >= 0.60f && venture.outstandingLoan <= 0

                    val valuationMultiplePercent = when {
                        isUnderperforming -> Random.nextInt(60, 78).toFloat() // Bad buying price for struggling venture
                        isStellar -> Random.nextInt(115, 135).toFloat() // Premium acquisition price for high-performing venture
                        else -> Random.nextInt(95, 108).toFloat() // Fair market price
                    }

                    val performanceConditionLabel = when {
                        isUnderperforming -> "Distressed Turnaround Valuation Discount (low margins / debt overhang)"
                        isStellar -> "Elite Performance Acquisition Premium (stellar margins & client loyalty)"
                        else -> "Fair Market Operational Value"
                    }

                    if (isBuyout) {
                        // Investor buys out 100% of FOUNDER'S REMAINING STAKE
                        val fairBuyout = (currentEnterpriseValuation * founderEquity).roundToInt().toDouble()
                        val buyoutPrice = (fairBuyout * (valuationMultiplePercent / 100.0)).roundToInt().toDouble()

                        val pitchMsg = "$firm tenders an all-cash buyout of $${String.format("%,.0f", buyoutPrice)} for 100% of your remaining ${(founderEquity * 100).toInt()}% founder equity in ${venture.name} ($performanceConditionLabel). Managerial control will transfer upon closing."

                        newOffer = VentureInvestorOffer(
                            id = "offer_" + UUID.randomUUID().toString().take(6),
                            investorName = invName,
                            investorFirm = firm,
                            offerType = "BUYOUT",
                            cashAmount = buyoutPrice,
                            equityPercent = founderEquity,
                            expirationWeek = state.currentWeek + 4,
                            expirationYear = state.currentYear,
                            pitchMessage = pitchMsg,
                            fairValue = fairBuyout,
                            isTrickyOffer = isUnderperforming,
                            valuationMultiplePercent = valuationMultiplePercent.toDouble(),
                            companyValuationAtOffer = currentEnterpriseValuation
                        )
                        generatedMails.add(
                            MailMessage(
                                id = "mail_" + UUID.randomUUID().toString().take(6),
                                sender = "$invName ($firm)",
                                subject = "💼 100% Acquisition Offer: Full Buyout of ${venture.name}",
                                body = "Dear Founder,\n\nOur investment committee at $firm has tracked ${venture.name}'s operational cashflow over the past $updatedWeeksActive weeks. We tender a formal all-cash acquisition offer of $${String.format("%,.0f", buyoutPrice)} to buy out 100% of your remaining ${(founderEquity * 100).toInt()}% founder equity.\n\nPricing Assessment: $performanceConditionLabel (${valuationMultiplePercent.toInt()}% of base valuation).\n\nUpon closing, liquid funds will wire to your personal account (subject to applicable State Capital Gains Tax) and managerial control will transfer.\n\nOffer expires in 4 weeks.\n\nSincerely,\n$invName\nManaging Director, $firm",
                                type = "VENTURE_BUYOUT_OFFER",
                                cashOffer = buyoutPrice,
                                ventureId = venture.id,
                                ventureOfferType = "BUYOUT",
                                ventureEquityPercent = founderEquity,
                                expiresWeek = state.currentWeek + 4,
                                expiresYear = state.currentYear
                            )
                        )
                    } else {
                        // Equity investment: varies dynamically based on founder's stake (e.g. 5% to 40%)
                        val maxStakePossible = (founderEquity * 0.75).coerceAtMost(0.40)
                        val possiblePcts = listOf(0.05, 0.08, 0.10, 0.12, 0.15, 0.18, 0.20, 0.25, 0.28, 0.30, 0.35, 0.40)
                            .filter { it <= maxStakePossible && it <= founderEquity }
                        val equityStake = possiblePcts.randomOrNull() ?: ((founderEquity * 0.50 * 100).roundToInt() / 100.0).coerceAtLeast(0.05)
                        val fairEquityValue = (currentEnterpriseValuation * equityStake).roundToInt().toDouble()

                        val cashInjection = (fairEquityValue * (valuationMultiplePercent / 100.0)).roundToInt().toDouble()

                        val pitchMsg = "$firm proposes to inject $${String.format("%,.0f", cashInjection)} in corporate capital for a ${(equityStake * 100).toInt()}% equity partnership in ${venture.name} ($performanceConditionLabel) to finance operations and growth."

                        newOffer = VentureInvestorOffer(
                            id = "offer_" + UUID.randomUUID().toString().take(6),
                            investorName = invName,
                            investorFirm = firm,
                            offerType = "EQUITY",
                            cashAmount = cashInjection,
                            equityPercent = equityStake,
                            expirationWeek = state.currentWeek + 4,
                            expirationYear = state.currentYear,
                            pitchMessage = pitchMsg,
                            fairValue = fairEquityValue,
                            isTrickyOffer = isUnderperforming,
                            valuationMultiplePercent = valuationMultiplePercent.toDouble(),
                            companyValuationAtOffer = currentEnterpriseValuation
                        )
                        generatedMails.add(
                            MailMessage(
                                id = "mail_" + UUID.randomUUID().toString().take(6),
                                sender = "$invName ($firm)",
                                subject = "🤝 Strategic Equity Investment: ${(equityStake * 100).toInt()}% Stake in ${venture.name}",
                                body = "Dear Founder,\n\n$firm wishes to inject $${String.format("%,.0f", cashInjection)} in growth capital into ${venture.name} in exchange for a ${(equityStake * 100).toInt()}% equity partnership.\n\nPricing Assessment: $performanceConditionLabel (${valuationMultiplePercent.toInt()}% of base equity valuation).\n\nFunds will disburse to your account (subject to applicable State tax) and enterprise growth reserves.\n\nOffer expires in 4 weeks.\n\nSincerely,\n$invName\nSenior Partner, $firm",
                                type = "VENTURE_EQUITY_OFFER",
                                cashOffer = cashInjection,
                                ventureId = venture.id,
                                ventureOfferType = "EQUITY",
                                ventureEquityPercent = equityStake,
                                expiresWeek = state.currentWeek + 4,
                                expiresYear = state.currentYear
                            )
                        )
                    }
                }
            }

            val mappedVenture = venture.copy(
                weeksActive = updatedWeeksActive,
                customerLoyalty = updatedCustomerLoyalty,
                weeklyGrossRevenue = actualGross,
                weeklyOperatingExpenses = actualExpenses,
                weeklyNetProfit = weeklyNet,
                totalGrossEarned = venture.totalGrossEarned + actualGross,
                totalExpensesPaid = venture.totalExpensesPaid + actualExpenses,
                accumulatedProfit = venture.accumulatedProfit + weeklyNet,
                history = (venture.history + weeklyNet).takeLast(20),
                pendingInvestorOffer = newOffer
            ).copyListing(
                screeningStatus = updatedScreeningStatus,
                screeningWeeksRemaining = updatedScreeningWeeks,
                approvedSharePrice = updatedApprovedPrice,
                approvedTotalShares = updatedApprovedTotalShares,
                approvedListedSharesUnits = updatedApprovedListedShares,
                screeningFeedback = updatedScreeningFeedback,
                weShareSharePrice = currentSharePrice,
                weSharePublicSharesSold = newSharesSold
            ).copyGovernance(
                recurringCustomers = updatedRecurringCustomers,
                marketingWeeksActive = updatedMarketingWeeks,
                hasLegalLicense = updatedHasLicense,
                legalLicenseCost = if (venture.legalLicenseCost > 0.0) venture.legalLicenseCost else 15000.0,
                sccWarningReceived = updatedSccWarning,
                sccDeadlineWeeks = updatedSccDeadline,
                isShutDownByRegulators = updatedShutDown,
                investorLawsuitActive = updatedLawsuitActive,
                investorLawsuitDamages = updatedLawsuitDamages
            ).copyTaxi(
                taxiCarCount = if (venture.type == "Taxi Fleet") updatedTaxiCarCount.coerceAtLeast(1) else venture.taxiCarCount,
                taxiDriverCount = venture.taxiDriverCount,
                taxiMechanicCount = venture.taxiMechanicCount,
                taxiDispatcherCount = venture.taxiDispatcherCount,
                taxiCondition = updatedTaxiCondition,
                taxiOdometerMiles = updatedTaxiMiles,
                taxiWeeklyTripsCompleted = updatedTaxiTrips,
                taxiWeeklyFaresEarned = updatedTaxiFares,
                taxiWeeklyFuelCost = updatedTaxiFuel,
                taxiWeeklyDriverSalary = updatedDriverSalaries,
                taxiWeeklyMechanicSalary = updatedMechanicSalaries,
                taxiWeeklyDispatcherSalary = updatedDispatcherSalaries,
                taxiWeeklyMaintenanceCost = updatedTaxiMaint,
                taxiWeeklyParkingSpaceTax = updatedTaxiParkingTax,
                taxiWeeklyParkingSpaceMaint = updatedTaxiParkingMaint
            ).copyRestaurant(
                waiterCount = venture.waiterCount,
                chefCount = venture.chefCount,
                seniorChefCount = venture.seniorChefCount,
                cleanerCount = venture.cleanerCount,
                managerCount = venture.managerCount,
                weeklyCustomersServed = updatedCustomersServed,
                weeklyFoodSales = updatedFoodSales,
                weeklyIngredientCost = updatedIngredientCost,
                weeklyBuildingMaintenanceCost = updatedBuildingMaint,
                weeklyStaffPayroll = updatedPayroll,
                weeklyEquipmentMaintenanceCost = updatedEquipMaint,
                equipmentList = updatedEquipmentList,
                serviceEfficiency = updatedEfficiency
            ).copyFarm(
                farmLandTier = updatedLandTier,
                farmLandIsOwned = updatedLandOwned,
                farmLandAcres = updatedLandAcres,
                farmLandValue = updatedLandValue,
                farmLandRentWeekly = updatedLandRent,
                farmCropBatches = updatedBatches,
                farmLivestock = updatedLivestock,
                farmMachinery = updatedMachinery,
                farmInventory = currentInventory,
                farmWeeklyRawCropsRevenue = weeklyRawCropsRevenue,
                farmWeeklyFruitsRevenue = weeklyFruitsRevenue,
                farmWeeklyLivestockRevenue = weeklyLivestockRevenue,
                farmWeeklyProcessedRevenue = weeklyProcessedRevenue,
                farmWeeklyFeedExpense = feedExpense,
                farmWeeklyLandRentExpense = landRentExpense,
                farmWeeklyMachineUpkeepExpense = machineUpkeepExpense,
                farmWeeklyPayrollExpense = farmPayrollExpense,
                farmWeeklySeedExpense = seedExpense,
                farmCleanliness = updatedFarmCleanliness,
                farmLastWeatherEvent = weatherEvent
            ).copyHotel(
                hotelRoomCount = if (venture.type == "Hotel" || venture.type == "Hotel Chain") updatedHotelRooms.count { it.isUnlocked } else venture.hotelRoomCount,
                hotelMaxRoomCapacity = if (venture.type == "Hotel" || venture.type == "Hotel Chain") 20 else venture.hotelMaxRoomCapacity,
                hotelNightlyRate = if (venture.type == "Hotel" || venture.type == "Hotel Chain") venture.hotelNightlyRate else venture.hotelNightlyRate,
                hotelOccupancyRate = if (venture.type == "Hotel" || venture.type == "Hotel Chain") updatedHotelOccupancy else venture.hotelOccupancyRate,
                hotelCondition = if (venture.type == "Hotel" || venture.type == "Hotel Chain") updatedHotelCondition else venture.hotelCondition,
                hotelCleanliness = if (venture.type == "Hotel" || venture.type == "Hotel Chain") updatedHotelCleanliness else venture.hotelCleanliness,
                hotelReputation = if (venture.type == "Hotel" || venture.type == "Hotel Chain") updatedHotelReputation else venture.hotelReputation,
                hotelHousekeeperCount = venture.hotelHousekeeperCount,
                hotelFrontDeskCount = venture.hotelFrontDeskCount,
                hotelChefCount = venture.hotelChefCount,
                hotelWaiterCount = venture.hotelWaiterCount,
                hotelSecurityCount = venture.hotelSecurityCount,
                hotelMaintenanceTechCount = venture.hotelMaintenanceTechCount,
                hotelConciergeCount = venture.hotelConciergeCount,
                hotelGeneralManagerCount = venture.hotelGeneralManagerCount,
                hotelAmenities = venture.hotelAmenities,
                hotelRooms = if (venture.type == "Hotel" || venture.type == "Hotel Chain") updatedHotelRooms else venture.hotelRooms,
                hotelTotalLodgesThisWeek = if (venture.type == "Hotel" || venture.type == "Hotel Chain") updatedHotelLodges else venture.hotelTotalLodgesThisWeek,
                hotelWeeklyRoomRevenue = if (venture.type == "Hotel" || venture.type == "Hotel Chain") hotelRoomRev else venture.hotelWeeklyRoomRevenue,
                hotelWeeklyAmenityRevenue = if (venture.type == "Hotel" || venture.type == "Hotel Chain") hotelAmenityRev else venture.hotelWeeklyAmenityRevenue,
                hotelWeeklyFoodRevenue = if (venture.type == "Hotel" || venture.type == "Hotel Chain") hotelFoodRev else venture.hotelWeeklyFoodRevenue,
                hotelWeeklyPayrollCost = if (venture.type == "Hotel" || venture.type == "Hotel Chain") hotelPayroll else venture.hotelWeeklyPayrollCost,
                hotelWeeklySuppliesCost = if (venture.type == "Hotel" || venture.type == "Hotel Chain") hotelSupplies else venture.hotelWeeklySuppliesCost,
                hotelWeeklyUtilityCost = if (venture.type == "Hotel" || venture.type == "Hotel Chain") hotelUtilities else venture.hotelWeeklyUtilityCost,
                hotelWeeklyMaintenanceCost = if (venture.type == "Hotel" || venture.type == "Hotel Chain") (hotelBldgMaint + hotelAmenityMaint) else venture.hotelWeeklyMaintenanceCost,
                hotelSeasonText = if (venture.type == "Hotel" || venture.type == "Hotel Chain") hotelSeasonStr else venture.hotelSeasonText,
                hotelLastIncident = if (venture.type == "Hotel" || venture.type == "Hotel Chain") updatedHotelIncident else venture.hotelLastIncident
            ).copyCrypto(
                cryptoTotalSupply = venture.cryptoTotalSupply,
                cryptoTokenPrice = if (venture.type == "Crypto Token") updatedPrice else venture.cryptoTokenPrice,
                cryptoLiquidityPoolUsd = if (venture.type == "Crypto Token") updatedLp else venture.cryptoLiquidityPoolUsd,
                cryptoWeeklyTradingVolume = if (venture.type == "Crypto Token") updatedVolume else venture.cryptoWeeklyTradingVolume,
                cryptoStakedTokens = venture.cryptoStakedTokens,
                cryptoCommunitySentiment = if (venture.type == "Crypto Token") updatedSentiment else venture.cryptoCommunitySentiment,
                cryptoMarketCycle = if (venture.type == "Crypto Token") updatedCycle else venture.cryptoMarketCycle,
                cryptoModCount = venture.cryptoModCount,
                cryptoSolidityDevCount = venture.cryptoSolidityDevCount,
                cryptoSecurityAuditorCount = venture.cryptoSecurityAuditorCount,
                cryptoMarketerCount = venture.cryptoMarketerCount,
                cryptoWeeklySwapFeesRevenue = if (venture.type == "Crypto Token") cryptoSwapFees else venture.cryptoWeeklySwapFeesRevenue,
                cryptoWeeklyStakingFeesRevenue = if (venture.type == "Crypto Token") cryptoStakingRev else venture.cryptoWeeklyStakingFeesRevenue,
                cryptoWeeklyDeFiYieldRevenue = if (venture.type == "Crypto Token") cryptoDeFiRev else venture.cryptoWeeklyDeFiYieldRevenue,
                cryptoWeeklyServerInfraCost = if (venture.type == "Crypto Token") cryptoInfra else venture.cryptoWeeklyServerInfraCost,
                cryptoWeeklyPayrollCost = if (venture.type == "Crypto Token") cryptoPayroll else venture.cryptoWeeklyPayrollCost,
                cryptoWeeklySecurityCost = if (venture.type == "Crypto Token") cryptoSecurityExp else venture.cryptoWeeklySecurityCost,
                cryptoActiveUsers = if (venture.type == "Crypto Token") updatedActiveUsers else venture.cryptoActiveUsers,
                cryptoDevelopersCount = venture.cryptoDevelopersCount,
                cryptoSecurityTier = venture.cryptoSecurityTier,
                cryptoWebsiteAppTier = venture.cryptoWebsiteAppTier,
                cryptoMarketingTier = venture.cryptoMarketingTier,
                cryptoIsMarketListed = venture.cryptoIsMarketListed,
                cryptoMarketListingNotice = if (venture.type == "Crypto Token") updatedListingNotice else venture.cryptoMarketListingNotice,
                cryptoFounderTokensOwned = venture.cryptoFounderTokensOwned,
                cryptoWeeklyProtocolFeeRevenue = if (venture.type == "Crypto Token") updatedProtocolFees else venture.cryptoWeeklyProtocolFeeRevenue,
                cryptoEducationalLesson = if (venture.type == "Crypto Token") updatedLesson else venture.cryptoEducationalLesson,
                cryptoNewServiceLaunchBoostWeeks = if (venture.type == "Crypto Token") updatedServiceBoostWeeks else venture.cryptoNewServiceLaunchBoostWeeks,
                cryptoLastHackLoss = if (venture.type == "Crypto Token") updatedHackLoss else venture.cryptoLastHackLoss
            )

            // 8. Majority Shareholder Autonomous Management & Manager Operational Reports
            val finalVenture = if (!mappedVenture.isPlayerInControl && updatedWeeksActive % 2 == 0) {
                val (ownerVenture, ownerAction) = VentureDiagnosticsHelper.simulateMajorityOwnerDecision(mappedVenture)
                if (ownerAction.isNotBlank()) {
                    logs.add("👔 ${venture.name} (Majority Shareholder Action): $ownerAction")
                }
                ownerVenture
            } else {
                mappedVenture
            }

            if (venture.isPlayerInControl && (updatedWeeksActive % 3 == 0 || Random.nextFloat() < 0.25f)) {
                val mgrMail = VentureDiagnosticsHelper.generateManagerWeeklyEmail(finalVenture, state.currentWeek, state.currentYear)
                if (mgrMail != null) {
                    generatedMails.add(mgrMail)
                }
            }

            finalVenture
        }

        if (totalEnterpriseProfitsEarned > 0) {
            reportBuilder.append("🏢 Business Ventures: Your enterprises generated $${String.format("%,.2f", totalEnterpriseProfitsEarned)} net profit (retained in business accounts; withdraw or reinvest via Ventures Portal).\n")
        }
        if (totalPlayerCash < 0) {
            reportBuilder.append("⚠️ Business Ventures: Sustained -$${String.format("%,.2f", -totalPlayerCash)} in unbuffered operational deficits across your ventures.\n")
        }

        if (totalIpoCash > 0) {
            reportBuilder.append("📈 WeShare Capital Raised: Deposited $${String.format("%,.2f", totalIpoCash)} from public IPO share sales.\n")
        }

        return VentureWeeklySimulationResult(
            updatedVentures = updatedVentures.filter { !it.isShutDownByRegulators },
            updatedStocks = updatedStocks,
            generatedMails = generatedMails,
            totalPlayerProfitCash = totalPlayerCash,
            totalIpoCashEarned = totalIpoCash,
            logs = logs
        )
    }
}
