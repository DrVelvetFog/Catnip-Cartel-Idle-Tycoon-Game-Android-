package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.math.sqrt
import kotlin.random.Random

data class CartelGameState(
  val nipBalance: Double = 0.0,
  val bossLevel: Int = 1,
  val bossTitle: String = "STREET ROOKIE",
  val streetCredXp: Long = 0L,
  val totalLifetimeNip: Double = 0.0,
  val zoomiesCharges: Int = 3,
  val isZoomiesActive: Boolean = false,
  val zoomiesRemainingSeconds: Int = 0,
  val isBoomboxBoostActive: Boolean = false,
  val boomboxRemainingSeconds: Int = 0,
  val isOverdriveActive: Boolean = false,
  val overdriveRemainingSeconds: Int = 0,
  val buyMultiplier: Int = 1, // 1, 10, 100, 9999 (MAX)
  val operatives: List<Operative> = emptyList(),
  val districts: List<District> = emptyList(),
  val productFormulas: List<ProductFormula> = emptyList(),
  val labTechs: List<LabTech> = emptyList(),
  val isRaidActive: Boolean = false,
  val raidTimerSeconds: Double = 12.4, // includes +5s Siamese bonus when acquired
  val stashedSpots: Set<String> = emptySet(),
  val raidFinished: Boolean = false,
  val raidWon: Boolean = false,
  val toastMessage: String? = null
)

class GameRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("catnip_cartel_save", Context.MODE_PRIVATE)

  private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())

  private val _state: MutableStateFlow<CartelGameState>

  init {
    // Migration: If previous version was demo state (version < 2), wipe and initialize fresh game
    val currentSaveVersion = prefs.getInt("save_version", 0)
    if (currentSaveVersion < 2) {
      prefs.edit().clear().putInt("save_version", 2).apply()
    }
    _state = MutableStateFlow(loadInitialState())
    startIdleEngine()
  }

  val state: StateFlow<CartelGameState> get() = _state.asStateFlow()

  fun resetGame() {
    prefs.edit().clear().putInt("save_version", 2).apply()
    _state.value = loadInitialState()
    _state.update { it.copy(toastMessage = "✨ FRESH GAME STARTED! WELCOME TO THE STREETS!") }
  }

  private fun getBossTitle(level: Int): String {
    return when {
      level >= 50 -> "UNDERWORLD KINGPIN"
      level >= 40 -> "OG BOSS"
      level >= 25 -> "SYNDICATE CAPO"
      level >= 15 -> "ALLEY ENFORCER"
      level >= 5 -> "CORNER RUNNER"
      else -> "STREET ROOKIE"
    }
  }

  private fun calculateBossLevel(lifetimeNip: Double): Int {
    return (1 + sqrt(lifetimeNip / 30.0)).toInt().coerceIn(1, 100)
  }

  private fun loadInitialState(): CartelGameState {
    val savedNip = prefs.getFloat("nip_balance", 0.0f).toDouble()
    val savedLifetime = prefs.getFloat("lifetime_nip", 0.0f).toDouble()
    val bossLvl = calculateBossLevel(savedLifetime)

    val defaultOperatives = listOf(
      Operative(
        id = "alley_kitten",
        name = "Alley Kitten",
        description = "Scrappy furball running street corners.",
        baseCost = 15L,
        baseProduction = 1.0,
        owned = prefs.getInt("op_alley_kitten", 0),
        level = prefs.getInt("op_alley_kitten", 0),
        stars = 1,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBD9DP6JgmjXmYuCfl3WRm7HaQ-1-f8DqWY-Eu42Zdr_Ky20GZ7YVqPVsUQBFExaWQVpSBfI1YmpuufpxunA3c0kCIROzcClUi0tj1vVZkB-PBTkhTDvy3Lgi2c0H74nAtuFE1F1zMERk9kGBwLOmFdxtK7hS5gud1emU3pQxm75KSdfH6o7JIX7uQX4xf4iqJmC4L9uWRfwQUKqZ48APCc5mVbq9xHGXGe_XXt3tGWV_cGYpX-ptOy6w"
      ),
      Operative(
        id = "siamese_lookout",
        name = "Siamese Lookout",
        description = "Eyes on rooftops. Extends Raid warning +5s!",
        baseCost = 100L,
        baseProduction = 5.0,
        owned = prefs.getInt("op_siamese_lookout", 0),
        level = prefs.getInt("op_siamese_lookout", 0),
        stars = 2,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA6A28W6W-CSu4FKO4dKF4X9kz0BtdBsHsC9OYNKHBJjcdBeM1ZdaIw8sGG2i8DYt9mIcGOke7hcQy9PKVlUvdU8AhNF4c_G-1IRljS2o4dKfP3uCbSfnCfS7XEnDpu098nnHojhET4ZK2Z_E1DE1-5E8iNvcJ_JD99UesUKH8-qfjJFEA44EtQET6nw6YeA7_ucjZQztsOub2n5O3vF3mZsUBDuUAFUeAncfv9oKaoOACMAUCGHDwR-g",
        perkText = "Eyes on rooftops. Extends Raid warning +5s!"
      ),
      Operative(
        id = "tabby_runner",
        name = "Tabby Runner",
        description = "Fast paws, customized gold sneakers.",
        baseCost = 600L,
        baseProduction = 20.0,
        owned = prefs.getInt("op_tabby_runner", 0),
        level = prefs.getInt("op_tabby_runner", 0),
        stars = 3,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDhM7Reyk2Wgr8Ztcggi9GdrhSm-mrTbApg3McmLsZ3RrCti69GQL3P5sq6Aft4pUMu_L2JADmtvZHlIrqiBziKdOHq1-s5gueDPtQYZRSlT5oaXp_QL6QoRODlflisZFymvyO3bfCkrV0uZUGf3vvmi6lG7WddWd9rRSLMaJMXDFBkg_z0F6KFFbnDACEzLhO1GfbzUqCsKd7SYcTk6DKHbkIMUwO1ny0VJLU2HDjmwH63zxXHuMGp2A"
      ),
      Operative(
        id = "persian_accountant",
        name = "Persian Accountant",
        description = "Catnip launderer. +10% syndicate earnings boost!",
        baseCost = 4000L,
        baseProduction = 75.0,
        owned = prefs.getInt("op_persian_accountant", 0),
        level = prefs.getInt("op_persian_accountant", 0),
        stars = 4,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAVHkIqJJl03wo7liL6RVnNogQpPbNjWqHNK3KjtvQQMv1NrSEPyrpc2JMQX-mQVoLqUOkvt9G9UQ058SlmIrM12rum7BstDTYdyfwaTTngkoHbE4jlvKkBV430cN_i8WXsXlE_YtAW0G-GBHSRZiTdhVErjYnoicA9LiF42M8vifOo94eJES1GtTCqP1PLLUpWSVJ_n7kn9aB1xbvYsybubtG7-zyeVfCUz9ikvrG2O46HuPEh2g06fQ",
        perkText = "Catnip launderer. +10% syndicate earnings boost!"
      ),
      Operative(
        id = "maine_coon_muscle",
        name = "Maine Coon Muscle",
        description = "Unit with brass claws. Halves raid losses to 7%!",
        baseCost = 25000L,
        baseProduction = 250.0,
        owned = prefs.getInt("op_maine_coon_muscle", 0),
        level = prefs.getInt("op_maine_coon_muscle", 0),
        stars = 4,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDUb5WpCC1SKUEhos88vb85z4z0xtIu3Qiga4hBmwvUPa0om4orfJeSMUMHmkjzKWMnNnLOttZNd8lObTNDzCPg6MkD2XxK2WyvKBkXQB3JxdHriP2yx3wbD_m2a22gKTsnYDRESOWfYJ-ImFVDKtY6qfbNjEu4fXzVL0STpmRON25EW17OUW0rnvdIA8Q3eRWcjMpDW1BSOlLB1af8c5nSTVnj6X3Xp7vPqgMZIf6AZc6YiCIEEUwbzA",
        perkText = "Unit with brass claws. Halves raid losses to 7%!"
      ),
      Operative(
        id = "the_plug",
        name = "The Plug",
        description = "Mysterious kingpin supplier behind East Side operation.",
        baseCost = 150000L,
        baseProduction = 1000.0,
        owned = prefs.getInt("op_the_plug", 0),
        level = prefs.getInt("op_the_plug", 0),
        stars = 5,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCmbGrgvr3J8N0N2cRveBbMUfyxfAHRu8ua2paohCKdWZmdFBv8PHeAc4GKfk3R0SAGMmCMz7veoMTvKCxQ0pANnzImVXUYEjB-2lx5Eqg6N1I7aPn33V4kLpyeR3Dzti7vdaHzsisC-bjTVKiYp0STsN_dbgFBg0Wg6b-LwKk6bdOyni6vGkSQwETb-mwb1E-MmTy2LIBG9LcasoyLAUBWEq8ReKW0k1lSyxCcf-33V4m6H9w53UzB0w",
        isLocked = prefs.getBoolean("op_the_plug_locked", true),
        unlockRequirementText = "REQ: SYNDICATE LVL 50",
        unlockCost = 150000L
      )
    )

    val defaultDistricts = listOf(
      District(
        id = "the_alley",
        name = "THE ALLEY",
        description = "Cracked asphalt nursery. The first dumpster distribution node is running smooth under street lamp shadows.",
        multiplier = 1.0,
        multiplierLabel = "x1.0 BASE",
        nipPerSec = 0.0,
        status = DistrictStatus.DOMINATED,
        perkLabel = "SECURE HARVEST"
      ),
      District(
        id = "the_porch",
        name = "THE PORCH",
        description = "Lawn Patrol secure. Stray calico guards pacing the perimeter, keeping neighbourhood hounds strictly in check.",
        multiplier = 1.5,
        multiplierLabel = "x1.5 REV",
        nipPerSec = 5.0,
        status = if (prefs.getBoolean("the_porch_claimed", false)) DistrictStatus.DOMINATED else DistrictStatus.READY_TO_EXPAND,
        unlockCost = 250L,
        perkLabel = "FRONT LINE SECURED"
      ),
      District(
        id = "the_rooftops",
        name = "THE ROOFTOPS",
        description = "Pigeon surveillance running. Sky-high solar drying lines and water tower drops operating at full clip.",
        multiplier = 2.5,
        multiplierLabel = "x2.5 ACTIVE",
        nipPerSec = 25.0,
        status = if (prefs.getBoolean("the_rooftops_claimed", false)) DistrictStatus.ACTIVE else DistrictStatus.LOCKED,
        unlockCost = 2500L,
        perkLabel = "PIGEON NET"
      ),
      District(
        id = "the_suburbs",
        name = "THE SUBURBS",
        description = "High-end suburban cul-de-sacs awaiting product. Pampered persian cats eager to trade gold collar studs for premium grade leaf.",
        multiplier = 5.0,
        multiplierLabel = "x5.0 TARGET",
        nipPerSec = 150.0,
        status = if (prefs.getBoolean("the_suburbs_claimed", false)) DistrictStatus.ACTIVE else DistrictStatus.LOCKED,
        unlockCost = 25000L,
        previewImageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAuJOPSEB7SkhuFPvSYDIaVLisfPcRwvtEdXZLezAWXfEigdyhXnx7sOBGbZt4KrNTQeHXZy5sUcB0-Qdg4TgD2HtUv129NlBjtWLFAJg8oxIJ8iQ_NWbcuDZXHHYxgRGQLpZGLJhZ_Xo4VzsTdRfPAmhNIAt3rXth_Wc4SE_UyVRPWwKkI4pMkLSxouMv9tX9NzjayvoGkhUrGm2PLUitQPdQAVMBuRA30G0gosb5OI3IlGzgp6wNqPQ",
        takeoverEstimate = "12M"
      ),
      District(
        id = "downtown",
        name = "DOWNTOWN",
        description = "High-rise corporate towers and diamond-encrusted flea collars. Heavy animal control security units patrolling alley grids.",
        multiplier = 10.0,
        multiplierLabel = "x10.0",
        nipPerSec = 750.0,
        status = if (prefs.getBoolean("downtown_claimed", false)) DistrictStatus.ACTIVE else DistrictStatus.LOCKED,
        unlockCost = 150000L
      ),
      District(
        id = "the_whole_city",
        name = "THE WHOLE CITY",
        description = "Absolute undisputed god-cat monopoly. Complete aerial and sewer catnip hegemony over millions of sleeping humans.",
        multiplier = 25.0,
        multiplierLabel = "x25.0",
        nipPerSec = 3000.0,
        status = if (prefs.getBoolean("the_whole_city_claimed", false)) DistrictStatus.ACTIVE else DistrictStatus.LEGENDARY_LOCKED,
        unlockCost = 1000000L
      )
    )

    val defaultFormulas = listOf(
      ProductFormula(
        id = "garden_batch",
        name = "Garden Batch",
        description = "Fresh backyard clippings, sun-dried on sizzling alley asphalt.",
        grade = "GRADE C",
        level = prefs.getInt("form_garden_lvl", 1),
        maxLevel = 10,
        tapPowerMultiplier = 1,
        baseBonus = 1,
        upgradeCost = 50L,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBO4vwYMeyDRk6vtv_uW4D2HuWXoZ-K902g_CszYTPL11VcwkLbg-UK_Vmz2bV5afioQpHKgnPzioRi0bF9pOkO9U9nSeRCd_Vksda6zmGsJzqaQcpDAh9RgJ2qinIj_d06hVD1Q3kyufVJeO4NEcw086e7LqeHDvzwlh8pHMH2Igv7jI6bv03CY_YmKeNy6jFnwLxOqHufvsgpFXsLCDiinpbg9zgVlfHQgUXxk9wdeL1X-bA8tr_jTA"
      ),
      ProductFormula(
        id = "sticky_nip",
        name = "Sticky 'Nip",
        description = "Potent, resinous street-grade bud with intense spearmint terpene punch.",
        grade = "GRADE A",
        level = prefs.getInt("form_sticky_lvl", 0),
        maxLevel = 10,
        tapPowerMultiplier = 3,
        baseBonus = 10,
        upgradeCost = 1000L,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBeNTHll755iViwPl5Vwz5e7cSJJquPapfOimKF82Vpt5bmzZAluP-hxU0-9B71-R1Pcss9Mpi-w5B5oVZuEsge0XKbeBefQ_kU6Ufo3v8MvsAh8ryLBRtaow3QNgxN7-cyCY60myaR_f7TrCeth9Z0g7K-mLNWSV_Fn7cTbbvcQ3YJRSSDX7HVZrM2FBRC_gj8_jQdgIOvoRnHoJHhLVjab3G_xxRXM_uYdesFmjOaN32R8EN8rYKC9g",
        isLocked = prefs.getBoolean("form_sticky_locked", true)
      ),
      ProductFormula(
        id = "golden_catnip",
        name = "Golden Catnip",
        description = "Legendary hydroponic gold-leaf reserve cured exclusively for feline cartel kingpins.",
        grade = "LEGENDARY",
        level = prefs.getInt("form_golden_lvl", 0),
        maxLevel = 10,
        tapPowerMultiplier = 10,
        baseBonus = 50,
        upgradeCost = 50000L,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDSPEZpZwPmMQRfrOF1X25OwfJD86L7F7F2sPDHHooW3LwkXRY1h5TLqSGjB2Pku8xcNWHCtlczXJcL8QFVjYUqu1WztxSw-pZA9MUPfemHEHRf0WIFF2brVrCqyO38wV9dqOXGXiHdN5_d9D03BajFhaPYaINR79VToUIkPHySUuDpWfIQsPRqYbBkrsK6Q_9kqsZCPniEV_XBTguIeucz9Zrz713uNhuGuVCQ76gVjMHpERFjoaWcvg",
        isLocked = prefs.getBoolean("form_golden_locked", true),
        isLegendary = true
      )
    )

    val defaultTechs = listOf(
      LabTech(
        id = "grow_lamps",
        name = "Hydroponic Grow Lamps",
        bonusText = "+15% Idle Harvest speed",
        cost = 5000L,
        iconName = "lightbulb",
        isInstalled = prefs.getBoolean("tech_grow_lamps", false)
      ),
      LabTech(
        id = "scent_baggies",
        name = "Scent-Masking Baggies",
        bonusText = "-20% Raid frequency",
        cost = 15000L,
        iconName = "local_police",
        isInstalled = prefs.getBoolean("tech_scent_baggies", false),
        isWarning = true
      ),
      LabTech(
        id = "golden_grinder",
        name = "Golden Grinder",
        bonusText = "+50% Critical Tap chance",
        cost = 50000L,
        iconName = "star",
        isInstalled = prefs.getBoolean("tech_golden_grinder", false)
      )
    )

    return CartelGameState(
      nipBalance = savedNip,
      bossLevel = bossLvl,
      bossTitle = getBossTitle(bossLvl),
      totalLifetimeNip = savedLifetime,
      streetCredXp = prefs.getLong("street_cred_xp", 0L),
      operatives = defaultOperatives,
      districts = defaultDistricts,
      productFormulas = defaultFormulas,
      labTechs = defaultTechs
    )
  }

  fun calculatePassiveRate(currentState: CartelGameState): Double {
    var baseRate = 0.0
    currentState.operatives.forEach { op ->
      baseRate += op.owned * op.baseProduction
    }
    currentState.districts.forEach { dist ->
      if (dist.status == DistrictStatus.DOMINATED || dist.status == DistrictStatus.ACTIVE) {
        baseRate += dist.nipPerSec
      }
    }

    var multiplier = 1.0
    if (currentState.isBoomboxBoostActive) multiplier *= 2.0
    if (currentState.isZoomiesActive) multiplier *= 3.0
    if (currentState.isOverdriveActive) multiplier *= 5.0

    // Persian accountant bonus +10% per accountant owned
    val accountants = currentState.operatives.find { it.id == "persian_accountant" }?.owned ?: 0
    multiplier *= (1.0 + (accountants * 0.10))

    // Hydroponic grow lamps bonus
    if (currentState.labTechs.find { it.id == "grow_lamps" }?.isInstalled == true) {
      multiplier *= 1.15
    }

    return baseRate * multiplier
  }

  fun calculateTapPower(currentState: CartelGameState): Long {
    var power = 1L // Base tap power
    currentState.productFormulas.forEach { formula ->
      if (!formula.isLocked && formula.level > 0) {
        power += (formula.level * formula.baseBonus)
      }
    }

    var mult = 1
    if (currentState.isZoomiesActive) mult *= 3
    if (currentState.isOverdriveActive) mult *= 5
    if (currentState.isBoomboxBoostActive) mult *= 2

    return power * mult
  }

  fun onAvatarTapped(): Long {
    val current = _state.value
    val tapGain = calculateTapPower(current)
    val isCrit = current.labTechs.find { it.id == "golden_grinder" }?.isInstalled == true && Random.nextFloat() < 0.5f
    val finalGain = if (isCrit) tapGain * 2 else tapGain

    val newLifetime = current.totalLifetimeNip + finalGain
    val newBossLevel = calculateBossLevel(newLifetime)

    _state.update {
      it.copy(
        nipBalance = it.nipBalance + finalGain,
        totalLifetimeNip = newLifetime,
        bossLevel = newBossLevel,
        bossTitle = getBossTitle(newBossLevel)
      )
    }
    return finalGain
  }

  fun triggerZoomies() {
    val current = _state.value
    if (current.zoomiesCharges > 0 && !current.isZoomiesActive) {
      _state.update {
        it.copy(
          zoomiesCharges = it.zoomiesCharges - 1,
          isZoomiesActive = true,
          zoomiesRemainingSeconds = 20,
          toastMessage = "🔥 3X MIDNIGHT ZOOMIES ACTIVATED!"
        )
      }
    }
  }

  fun triggerOverdrive() {
    val current = _state.value
    if (!current.isOverdriveActive) {
      _state.update {
        it.copy(
          isOverdriveActive = true,
          overdriveRemainingSeconds = 30,
          toastMessage = "⚡ 30s OVERDRIVE BEAT FRENZY!"
        )
      }
    }
  }

  fun claimAdDrop() {
    _state.update {
      val bonus = (calculateTapPower(it) * 50).toDouble().coerceAtLeast(100.0)
      it.copy(
        nipBalance = it.nipBalance + bonus,
        totalLifetimeNip = it.totalLifetimeNip + bonus,
        toastMessage = "🎁 AIRDROP CRATE: +${bonus.toLong()} NIP DELIVERED!"
      )
    }
  }

  fun activateBoomboxBoost() {
    _state.update {
      it.copy(
        isBoomboxBoostActive = true,
        boomboxRemainingSeconds = 1800, // 30 minutes boost
        toastMessage = "📻 2X BOOMBOX BASSLINE BOOST ACTIVATED!"
      )
    }
  }

  fun setBuyMultiplier(mult: Int) {
    _state.update { it.copy(buyMultiplier = mult) }
  }

  fun hireOperative(opId: String, amount: Int) {
    val current = _state.value
    val op = current.operatives.find { it.id == opId } ?: return
    val totalCost = op.baseCost * amount
    if (current.nipBalance >= totalCost) {
      val newOwned = op.owned + amount
      val updatedOps = current.operatives.map {
        if (it.id == opId) {
          it.copy(
            owned = newOwned,
            level = newOwned,
            baseCost = (it.baseCost * 1.15).roundToLong()
          )
        } else it
      }
      _state.update {
        it.copy(
          nipBalance = it.nipBalance - totalCost,
          operatives = updatedOps,
          toastMessage = "RECRUITED +$amount ${op.name}!"
        )
      }
      prefs.edit().putInt("op_$opId", newOwned).apply()
    } else {
      _state.update { it.copy(toastMessage = "NOT ENOUGH NIP TO HIRE!") }
    }
  }

  fun unlockContract(opId: String) {
    val current = _state.value
    val op = current.operatives.find { it.id == opId } ?: return
    if (current.nipBalance >= op.unlockCost) {
      val updatedOps = current.operatives.map {
        if (it.id == opId) {
          it.copy(isLocked = false, owned = 1, level = 1)
        } else it
      }
      _state.update {
        it.copy(
          nipBalance = it.nipBalance - op.unlockCost,
          operatives = updatedOps,
          toastMessage = "CONTRACT SIGNED: ${op.name} IS ONLINE!"
        )
      }
      prefs.edit().putBoolean("op_${opId}_locked", false).putInt("op_$opId", 1).apply()
    } else {
      _state.update { it.copy(toastMessage = "REQUIRES 150,000 NIP TO UNLOCK!") }
    }
  }

  fun claimDistrict(districtId: String) {
    val current = _state.value
    val district = current.districts.find { it.id == districtId } ?: return
    if (current.nipBalance >= district.unlockCost) {
      val updatedDistricts = current.districts.map {
        if (it.id == districtId) {
          it.copy(status = DistrictStatus.DOMINATED)
        } else it
      }
      _state.update {
        it.copy(
          nipBalance = it.nipBalance - district.unlockCost,
          districts = updatedDistricts,
          toastMessage = "TURF EXPANDED: ${district.name} DOMINATED!"
        )
      }
      prefs.edit().putBoolean("${districtId}_claimed", true).apply()
    } else {
      _state.update { it.copy(toastMessage = "NOT ENOUGH NIP TO CLAIM TURF!") }
    }
  }

  fun upgradeFormula(formulaId: String) {
    val current = _state.value
    val form = current.productFormulas.find { it.id == formulaId } ?: return
    if (current.nipBalance >= form.upgradeCost && form.level < form.maxLevel) {
      val nextLvl = form.level + 1
      val updatedFormulas = current.productFormulas.map {
        if (it.id == formulaId) {
          it.copy(
            level = nextLvl,
            isLocked = false,
            upgradeCost = (it.upgradeCost * 1.4).roundToLong()
          )
        } else it
      }
      _state.update {
        it.copy(
          nipBalance = it.nipBalance - form.upgradeCost,
          productFormulas = updatedFormulas,
          toastMessage = "${form.name} upgraded to LVL $nextLvl!"
        )
      }
      prefs.edit().putInt("form_${formulaId}_lvl", nextLvl).putBoolean("form_${formulaId}_locked", false).apply()
    } else {
      _state.update { it.copy(toastMessage = "NOT ENOUGH NIP!") }
    }
  }

  fun installTech(techId: String) {
    val current = _state.value
    val tech = current.labTechs.find { it.id == techId } ?: return
    if (!tech.isInstalled && current.nipBalance >= tech.cost) {
      val updated = current.labTechs.map {
        if (it.id == techId) it.copy(isInstalled = true) else it
      }
      _state.update {
        it.copy(
          nipBalance = it.nipBalance - tech.cost,
          labTechs = updated,
          toastMessage = "${tech.name} INSTALLED & OPERATIONAL!"
        )
      }
      prefs.edit().putBoolean("tech_$techId", true).apply()
    }
  }

  fun triggerRaid() {
    val hasSiamese = _state.value.operatives.find { it.id == "siamese_lookout" }?.owned ?: 0 > 0
    val duration = if (hasSiamese) 12.4 else 7.4
    _state.update {
      it.copy(
        isRaidActive = true,
        raidTimerSeconds = duration,
        stashedSpots = emptySet(),
        raidFinished = false,
        raidWon = false
      )
    }
  }

  fun stashSpot(spotName: String) {
    val current = _state.value
    if (!current.isRaidActive || current.raidFinished) return

    val newStashed = current.stashedSpots + spotName
    val allSpots = setOf("Litter Box", "Couch Cushions", "Dark Closet", "Under Bed", "Backyard Dumpster")
    val won = newStashed.containsAll(allSpots)

    _state.update {
      val bonusNip = if (won) (it.nipBalance * 0.1).coerceAtLeast(500.0) else 0.0
      it.copy(
        stashedSpots = newStashed,
        raidFinished = won,
        raidWon = won,
        nipBalance = it.nipBalance + bonusNip,
        streetCredXp = if (won) it.streetCredXp + 500L else it.streetCredXp,
        toastMessage = if (won) "🏆 STASH SECURED! +XP & NIP REWARD!" else "HIDDEN IN $spotName!"
      )
    }
  }

  fun dismissRaid() {
    _state.update {
      it.copy(
        isRaidActive = false,
        raidFinished = false
      )
    }
  }

  fun clearToast() {
    _state.update { it.copy(toastMessage = null) }
  }

  private fun startIdleEngine() {
    coroutineScope.launch {
      var tickCount = 0
      var saveTickCount = 0
      while (isActive) {
        delay(100) // 10 ticks per second for smooth idle counter
        tickCount++
        saveTickCount++
        val isSecondTick = tickCount >= 10
        if (isSecondTick) tickCount = 0

        _state.update { current ->
          val passiveRate = calculatePassiveRate(current)
          val increment = passiveRate / 10.0

          // countdown timers (decrement once per full second)
          var boomboxTime = current.boomboxRemainingSeconds
          var boomboxActive = current.isBoomboxBoostActive
          var zoomiesTime = current.zoomiesRemainingSeconds
          var zoomiesActive = current.isZoomiesActive
          var overdriveTime = current.overdriveRemainingSeconds
          var overdriveActive = current.isOverdriveActive

          if (isSecondTick) {
            if (boomboxActive) {
              boomboxTime -= 1
              if (boomboxTime <= 0) {
                boomboxActive = false
                boomboxTime = 0
              }
            }

            if (zoomiesActive) {
              zoomiesTime -= 1
              if (zoomiesTime <= 0) {
                zoomiesActive = false
                zoomiesTime = 0
              }
            }

            if (overdriveActive) {
              overdriveTime -= 1
              if (overdriveTime <= 0) {
                overdriveActive = false
                overdriveTime = 0
              }
            }
          }

          // Handle raid timer tick
          var raidTimer = current.raidTimerSeconds
          var raidFinished = current.raidFinished
          var raidWon = current.raidWon
          var balance = current.nipBalance + increment

          if (current.isRaidActive && !raidFinished) {
            raidTimer -= 0.1
            if (raidTimer <= 0.0) {
              raidTimer = 0.0
              raidFinished = true
              raidWon = false
              // Maine Coon muscle halves penalty from 15% to 7%
              val hasMaineCoon = current.operatives.find { it.id == "maine_coon_muscle" }?.owned ?: 0 > 0
              val penaltyFraction = if (hasMaineCoon) 0.07 else 0.15
              val penalty = balance * penaltyFraction
              balance -= penalty
            }
          }

          val newLifetime = current.totalLifetimeNip + increment
          val newBossLvl = calculateBossLevel(newLifetime)

          current.copy(
            nipBalance = balance,
            totalLifetimeNip = newLifetime,
            bossLevel = newBossLvl,
            bossTitle = getBossTitle(newBossLvl),
            isBoomboxBoostActive = boomboxActive,
            boomboxRemainingSeconds = boomboxTime,
            zoomiesRemainingSeconds = zoomiesTime,
            isZoomiesActive = zoomiesActive,
            overdriveRemainingSeconds = overdriveTime,
            isOverdriveActive = overdriveActive,
            raidTimerSeconds = raidTimer,
            raidFinished = raidFinished,
            raidWon = raidWon
          )
        }

        // Save progress periodically (every 5 seconds)
        if (saveTickCount >= 50) {
          saveTickCount = 0
          val cur = _state.value
          prefs.edit()
            .putFloat("nip_balance", cur.nipBalance.toFloat())
            .putFloat("lifetime_nip", cur.totalLifetimeNip.toFloat())
            .putLong("street_cred_xp", cur.streetCredXp)
            .apply()
        }
      }
    }
  }
}
