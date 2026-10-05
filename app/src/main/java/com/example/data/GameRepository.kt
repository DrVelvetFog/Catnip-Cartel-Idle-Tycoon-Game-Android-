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
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sqrt
import kotlin.random.Random

// ---------------------------------------------------------------------------
// Catnip Cartel Tycoon — game economy v2
//
// Crew:        cost = baseCost * 1.6^owned ; production per unit as listed
// Territories: ladder multipliers (highest owned applies to all earnings)
// Products:    one-time tap-power multipliers
// Zoomies:     3x earnings, 30s, one charge per 300s (max 3)
// Raids:       every 8-15 min; 10s warning (15s with Siamese Lookout);
//              win = +5% of stash, fail = -15% (-7% with Maine Coon Muscle)
// Prestige:    "Nine Lives" — lifetime >= 1M nip -> +25% per life, max 9
// Offline:     up to 8h of passive earnings while away
// ---------------------------------------------------------------------------

object Economy {
  const val COST_GROWTH = 1.6
  const val TAP_BASE = 25L
  const val ZOOMIES_MULT = 3.0
  const val ZOOMIES_DURATION_S = 30
  const val ZOOMIES_RECHARGE_S = 300
  const val ZOOMIES_MAX_CHARGES = 3
  const val RAID_MIN_DELAY_S = 480.0   // 8 min
  const val RAID_MAX_DELAY_S = 900.0   // 15 min
  const val RAID_WARN_S = 10.0
  const val RAID_WARN_SIAM_ESE_S = 15.0
  const val RAID_WIN_BONUS = 0.05      // +5% of stash
  const val RAID_FAIL_LOSS = 0.15      // -15%
  const val RAID_FAIL_LOSS_MUSCLE = 0.07 // -7% with Maine Coon Muscle
  const val PRESTIGE_LIFETIME_REQ = 1_000_000.0
  const val PRESTIGE_PER_LIFE = 0.25
  const val PRESTIGE_MAX_LIVES = 9
  const val OFFLINE_CAP_S = 8 * 3600L
  const val SAVE_VERSION = 2

  /** Total price for buying [count] more units when [owned] are held. */
  fun bulkCost(baseCost: Long, owned: Int, count: Int): Long {
    if (count <= 0) return 0L
    val unit = baseCost * COST_GROWTH.pow(owned)
    val total = unit * (COST_GROWTH.pow(count) - 1.0) / (COST_GROWTH - 1.0)
    return total.roundToLong().coerceAtMost(Long.MAX_VALUE / 4)
  }

  /** Largest n such that bulkCost(baseCost, owned, n) <= balance. */
  fun maxAffordable(baseCost: Long, owned: Int, balance: Double): Int {
    val unit = baseCost * COST_GROWTH.pow(owned)
    if (unit <= 0 || balance < unit) return 0
    val n = floor(ln(1.0 + balance * (COST_GROWTH - 1.0) / unit) / ln(COST_GROWTH)).toInt()
    return n.coerceAtLeast(0)
  }
}

data class CartelGameState(
  val nipBalance: Double = 0.0,
  val bossLevel: Int = 1,
  val bossTitle: String = "STREET ROOKIE",
  val streetCredXp: Long = 0L,
  val totalLifetimeNip: Double = 0.0,
  val zoomiesCharges: Int = 1,
  val isZoomiesActive: Boolean = false,
  val zoomiesRemainingSeconds: Int = 0,
  val zoomiesRechargeSeconds: Int = Economy.ZOOMIES_RECHARGE_S,
  val isBoomboxBoostActive: Boolean = false,
  val boomboxRemainingSeconds: Int = 0,
  val boomboxCooldownSeconds: Int = 0,
  val isOverdriveActive: Boolean = false,
  val overdriveRemainingSeconds: Int = 0,
  val overdriveCooldownSeconds: Int = 0,
  val adDropCooldownSeconds: Int = 0,
  val buyMultiplier: Int = 1, // 1, 10, 100, 9999 (MAX)
  val operatives: List<Operative> = emptyList(),
  val districts: List<District> = emptyList(),
  val productFormulas: List<ProductFormula> = emptyList(),
  val labTechs: List<LabTech> = emptyList(),
  val prestigeLives: Int = 0,
  val isRaidActive: Boolean = false,
  val raidTimerSeconds: Double = 0.0,
  val nextRaidInSeconds: Double = Economy.RAID_MIN_DELAY_S,
  val stashedSpots: Set<String> = emptySet(),
  val raidFinished: Boolean = false,
  val raidWon: Boolean = false,
  val musicEnabled: Boolean = true,
  val toastMessage: String? = null
)

class GameRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("catnip_cartel_save", Context.MODE_PRIVATE)

  private val _state = MutableStateFlow(loadInitialState())
  val state: StateFlow<CartelGameState> = _state.asStateFlow()

  private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
  private var tickCount = 0

  init {
    grantOfflineEarnings()
    startIdleEngine()
  }

  // ------------------------------ state -----------------------------------

  private fun defaultOperatives(): List<Operative> = listOf(
    Operative(
      id = "alley_kitten", name = "Alley Kitten",
      description = "Scrappy furball running street corners.",
      baseCost = 50L, baseProduction = 1.0, owned = 0, level = 0, stars = 1,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBD9DP6JgmjXmYuCfl3WRm7HaQ-1-f8DqWY-Eu42Zdr_Ky20GZ7YVqPVsUQBFExaWQVpSBfI1YmpuufpxunA3c0kCIROzcClUi0tj1vVZkB-PBTkhTDvy3Lgi2c0H74nAtuFE1F1zMERk9kGBwLOmFdxtK7hS5gud1emU3pQxm75KSdfH6o7JIX7uQX4xf4iqJmC4L9uWRfwQUKqZ48APCc5mVbq9xHGXGe_XXt3tGWV_cGYpX-ptOy6w"
    ),
    Operative(
      id = "siamese_lookout", name = "Siamese Lookout",
      description = "Eyes on rooftops. Raid warning becomes 15s!",
      baseCost = 300L, baseProduction = 6.0, owned = 0, level = 0, stars = 2,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA6A28W6W-CSu4FKO4dKF4X9kz0BtdBsHsC9OYNKHBJjcdBeM1ZdaIw8sGG2i8DYt9mIcGOke7hcQy9PKVlUvdU8AhNF4c_G-1IRljS2o4dKfP3uCbSfnCfS7XEnDpu098nnHojhET4ZK2Z_E1DE1-5E8iNvcJ_JD99UesUKH8-qfjJFEA44EtQET6nw6YeA7_ucjZQztsOub2n5O3vF3mZsUBDuUAFUeAncfv9oKaoOACMAUCGHDwR-g",
      perkText = "Raid warning becomes 15s!"
    ),
    Operative(
      id = "tabby_runner", name = "Tabby Runner",
      description = "Fast paws, customized gold sneakers.",
      baseCost = 1500L, baseProduction = 20.0, owned = 0, level = 0, stars = 3,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDhM7Reyk2Wgr8Ztcggi9GdrhSm-mrTbApg3McmLsZ3RrCti69GQL3P5sq6Aft4pUMu_L2JADmtvZHlIrqiBziKdOHq1-s5gueDPtQYZRSlT5oaXp_QL6QoRODlflisZFymvyO3bfCkrV0uZUGf3vvmi6lG7WddWd9rRSLMaJMXDFBkg_z0F6KFFbnDACEzLhO1GfbzUqCsKd7SYcTk6DKHbkIMUwO1ny0VJLU2HDjmwH63zxXHuMGp2A"
    ),
    Operative(
      id = "persian_accountant", name = "Persian Accountant",
      description = "Catnip launderer. +10% all earnings!",
      baseCost = 8000L, baseProduction = 60.0, owned = 0, level = 0, stars = 4,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAVHkIqJJl03wo7liL6RVnNogQpPbNjWqHNK3KjtvQQMv1NrSEPyrpc2JMQX-mQVoLqUOkvt9G9UQ058SlmIrM12rum7BstDTYdyfwaTTngkoHbE4jlvKkBV430cN_i8WXsXlE_YtAW0G-GBHSRZiTdhVErjYnoicA9LiF42M8vifOo94eJES1GtTCqP1PLLUpWSVJ_n7kn9aB1xbvYsybubtG7-zyeVfCUz9ikvrG2O46HuPEh2g06fQ",
      perkText = "+10% all earnings!"
    ),
    Operative(
      id = "maine_coon_muscle", name = "Maine Coon Muscle",
      description = "Unit with brass claws. Failed raids only take 7%!",
      baseCost = 40000L, baseProduction = 200.0, owned = 0, level = 0, stars = 4,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDUb5WpCC1SKUEhos88vb85z4z0xtIu3Qiga4hBmwvUPa0om4orfJeSMUMHmkjzKWMnNnLOttZNd8lObTNDzCPg6MkD2XxK2WyvKBkXQB3JxdHriP2yx3wbD_m2a22gKTsnYDRESOWfYJ-ImFVDKtY6qfbNjEu4fXzVL0STpmRON25EW17OUW0rnvdIA8Q3eRWcjMpDW1BSOlLB1af8c5nSTVnj6X3Xp7vPqgMZIf6AZc6YiCIEEUwbzA",
      perkText = "Failed raids only take 7%!"
    ),
    Operative(
      id = "the_plug", name = "The Plug",
      description = "Mysterious kingpin supplier. Unlocks after Downtown.",
      baseCost = 250000L, baseProduction = 800.0, owned = 0, level = 0, stars = 5,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCmbGrgvr3J8N0N2cRveBbMUfyxfAHRu8ua2paohCKdWZmdFBv8PHeAc4GKfk3R0SAGMmCMz7veoMTvKCxQ0pANnzImVXUYEjB-2lx5Eqg6N1I7aPn33V4kLpyeR3Dzti7vdaHzsisC-bjTVKiYp0STsN_dbgFBg0Wg6b-LwKk6bdOyni6vGkSQwETb-mwb1E-MmTy2LIBG9LcasoyLAUBWEq8ReKW0k1lSyxCcf-33V4m6H9w53UzB0w",
      isLocked = true,
      unlockRequirementText = "REQ: CLAIM DOWNTOWN",
      unlockCost = 250000L
    )
  )

  private fun defaultDistricts(): List<District> = listOf(
    District(
      id = "the_alley", name = "THE ALLEY",
      description = "Cracked asphalt nursery. The first dumpster distribution node runs smooth under street lamp shadows.",
      multiplier = 1.0, multiplierLabel = "x1 BASE", nipPerSec = 0.0,
      status = DistrictStatus.DOMINATED, perkLabel = "SECURE HARVEST"
    ),
    District(
      id = "the_porch", name = "THE PORCH",
      description = "Lawn patrol territory. Stray calico guards pacing the perimeter.",
      multiplier = 2.0, multiplierLabel = "x2 EARNINGS", nipPerSec = 0.0,
      status = DistrictStatus.READY_TO_EXPAND, unlockCost = 5000L,
      perkLabel = "FRONT LINE SECURED"
    ),
    District(
      id = "the_rooftops", name = "THE ROOFTOPS",
      description = "Sky-high solar drying lines and water tower drops.",
      multiplier = 4.0, multiplierLabel = "x4 EARNINGS", nipPerSec = 0.0,
      status = DistrictStatus.LOCKED, unlockCost = 25000L,
      perkLabel = "PIGEON NET"
    ),
    District(
      id = "the_suburbs", name = "THE SUBURBS",
      description = "High-end cul-de-sacs. Pampered persians trade gold collar studs for premium leaf.",
      multiplier = 8.0, multiplierLabel = "x8 EARNINGS", nipPerSec = 0.0,
      status = DistrictStatus.LOCKED, unlockCost = 150000L
    ),
    District(
      id = "downtown", name = "DOWNTOWN",
      description = "High-rise towers and diamond flea collars. Heavy animal control patrols.",
      multiplier = 16.0, multiplierLabel = "x16 EARNINGS", nipPerSec = 0.0,
      status = DistrictStatus.LOCKED, unlockCost = 1000000L
    ),
    District(
      id = "the_whole_city", name = "THE WHOLE CITY",
      description = "Absolute god-cat monopoly. Complete aerial and sewer catnip hegemony.",
      multiplier = 32.0, multiplierLabel = "x32 EARNINGS", nipPerSec = 0.0,
      status = DistrictStatus.LEGENDARY_LOCKED, unlockCost = 10000000L
    )
  )

  private fun defaultFormulas(): List<ProductFormula> = listOf(
    ProductFormula(
      id = "garden_batch", name = "Garden Batch",
      description = "Fresh backyard clippings, sun-dried on sizzling alley asphalt. One-time tap upgrade.",
      grade = "GRADE C", level = 0, maxLevel = 1,
      tapPowerMultiplier = 2, baseBonus = 0, upgradeCost = 1000L,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBO4vwYMeyDRk6vtv_uW4D2HuWXoZ-K902g_CszYTPL11VcwkLbg-UK_Vmz2bV5afioQpHKgnPzioRi0bF9pOkO9U9nSeRCd_Vksda6zmGsJzqaQcpDAh9RgJ2qinIj_d06hVD1Q3kyufVJeO4NEcw086e7LqeHDvzwlh8pHMH2Igv7jI6bv03CY_YmKeNy6jFnwLxOqHufvsgpFXsLCDiinpbg9zgVlfHQgUXxk9wdeL1X-bA8tr_jTA"
    ),
    ProductFormula(
      id = "sticky_nip", name = "Sticky 'Nip",
      description = "Potent, resinous street-grade bud. One-time tap upgrade.",
      grade = "GRADE A", level = 0, maxLevel = 1,
      tapPowerMultiplier = 5, baseBonus = 0, upgradeCost = 10000L,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBeNTHll755iViwPl5Vwz5e7cSJJquPapfOimKF82Vpt5bmzZAluP-hxU0-9B71-R1Pcss9Mpi-w5B5oVZuEsge0XKbeBefQ_kU6Ufo3v8MvsAh8ryLBRtaow3QNgxN7-cyCY60myaR_f7TrCeth9Z0g7K-mLNWSV_Fn7cTbbvcQ3YJRSSDX7HVZrM2FBRC_gj8_jQdgIOvoRnHoJHhLVjab3G_xxRXM_uYdesFmjOaN32R8EN8rYKC9g"
    ),
    ProductFormula(
      id = "golden_catnip", name = "Golden Catnip",
      description = "Legendary hydroponic gold-leaf reserve. One-time tap upgrade.",
      grade = "LEGENDARY", level = 0, maxLevel = 1,
      tapPowerMultiplier = 15, baseBonus = 0, upgradeCost = 100000L,
      imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDSPEZpZwPmMQRfrOF1X25OwfJD86L7F7F2sPDHHooW3LwkXRY1h5TLqSGjB2Pku8xcNWHCtlczXJcL8QFVjYUqu1WztxSw-pZA9MUPfemHEHRf0WIFF2brVrCqyO38wV9dqOXGXiHdN5_d9D03BajFhaPYaINR79VToUIkPHySUuDpWfIQsPRqYbBkrsK6Q_9kqsZCPniEV_XBTguIeucz9Zrz713uNhuGuVCQ76gVjMHpERFjoaWcvg",
      isLegendary = true
    )
  )

  private fun defaultTechs(): List<LabTech> = listOf(
    LabTech(
      id = "grow_lamps", name = "Hydroponic Grow Lamps",
      bonusText = "+15% Idle Harvest speed", cost = 8000L,
      iconName = "lightbulb", isInstalled = false
    ),
    LabTech(
      id = "scent_baggies", name = "Scent-Masking Baggies",
      bonusText = "-20% Raid frequency", cost = 22000L,
      iconName = "local_police", isInstalled = false, isWarning = true
    ),
    LabTech(
      id = "golden_grinder", name = "Golden Grinder",
      bonusText = "+50% Critical Tap chance", cost = 65000L,
      iconName = "star", isInstalled = false
    )
  )

  // ------------------------------ persistence -------------------------------

  private fun putDouble(editor: SharedPreferences.Editor, key: String, v: Double) {
    editor.putLong(key, v.toRawBits())
  }

  private fun getDouble(key: String, def: Double): Double {
    if (!prefs.contains(key)) return def
    return try {
      Double.fromBits(prefs.getLong(key, 0L))
    } catch (_: ClassCastException) {
      try {
        prefs.getFloat(key, def.toFloat()).toDouble()
      } catch (_: ClassCastException) {
        try {
          prefs.getInt(key, def.toInt()).toDouble()
        } catch (_: ClassCastException) {
          def
        }
      }
    }
  }

  private fun getLongSafe(key: String, def: Long): Long {
    if (!prefs.contains(key)) return def
    return try {
      prefs.getLong(key, def)
    } catch (_: ClassCastException) {
      try {
        prefs.getInt(key, def.toInt()).toLong()
      } catch (_: ClassCastException) {
        try {
          prefs.getFloat(key, def.toFloat()).toLong()
        } catch (_: ClassCastException) {
          def
        }
      }
    }
  }

  private fun getIntSafe(key: String, def: Int): Int {
    if (!prefs.contains(key)) return def
    return try {
      prefs.getInt(key, def)
    } catch (_: ClassCastException) {
      try {
        prefs.getLong(key, def.toLong()).toInt()
      } catch (_: ClassCastException) {
        try {
          prefs.getFloat(key, def.toFloat()).toInt()
        } catch (_: ClassCastException) {
          def
        }
      }
    }
  }

  private fun loadInitialState(): CartelGameState = try {
    if (getIntSafe("save_version", 0) < Economy.SAVE_VERSION) {
      // Fresh start: ignore any legacy demo-state keys.
      CartelGameState(
        operatives = defaultOperatives(),
        districts = defaultDistricts(),
        productFormulas = defaultFormulas(),
        labTechs = defaultTechs(),
        nextRaidInSeconds = Random.nextDouble(Economy.RAID_MIN_DELAY_S, Economy.RAID_MAX_DELAY_S)
      )
    } else {
      val ops = defaultOperatives().map { op ->
        val owned = getIntSafe("op_owned_${op.id}", 0)
        val locked = if (op.id == "the_plug") prefs.getBoolean("op_locked_the_plug", true) else false
        op.copy(owned = owned, level = owned, isLocked = locked)
      }
      val rawDists = defaultDistricts()
      val dists = rawDists.mapIndexed { idx, d ->
        if (d.id == "the_alley") d
        else {
          val claimed = prefs.getBoolean("dist_claimed_${d.id}", false)
          if (claimed) {
            d.copy(status = DistrictStatus.ACTIVE)
          } else {
            val prev = rawDists.getOrNull(idx - 1)
            val prevClaimed = prev != null && (prev.id == "the_alley" || prefs.getBoolean("dist_claimed_${prev.id}", false))
            if (prevClaimed) d.copy(status = DistrictStatus.READY_TO_EXPAND) else d
          }
        }
      }
      val forms = defaultFormulas().map { f ->
        f.copy(level = getIntSafe("form_lvl_${f.id}", 0))
      }
      val techs = defaultTechs().map { t ->
        t.copy(isInstalled = prefs.getBoolean("tech_${t.id}", false))
      }
      val lifetime = getDouble("lifetime_nip", 0.0)
      val bossLvl = calculateBossLevel(lifetime)
      CartelGameState(
        nipBalance = getDouble("nip_balance", 0.0),
        bossLevel = bossLvl,
        bossTitle = getBossTitle(bossLvl),
        streetCredXp = getLongSafe("street_cred_xp", 0L),
        totalLifetimeNip = lifetime,
        zoomiesCharges = getIntSafe("zoomies_charges", 1),
        prestigeLives = getIntSafe("prestige_lives", 0),
        musicEnabled = prefs.getBoolean("music_enabled", true),
        operatives = ops,
        districts = dists,
        productFormulas = forms,
        labTechs = techs,
        nextRaidInSeconds = Random.nextDouble(Economy.RAID_MIN_DELAY_S, Economy.RAID_MAX_DELAY_S)
      )
    }
  } catch (_: ClassCastException) {
    CartelGameState(
      operatives = defaultOperatives(),
      districts = defaultDistricts(),
      productFormulas = defaultFormulas(),
      labTechs = defaultTechs(),
      nextRaidInSeconds = Random.nextDouble(Economy.RAID_MIN_DELAY_S, Economy.RAID_MAX_DELAY_S)
    )
  }

  private fun saveState(s: CartelGameState) {
    val e = prefs.edit()
    e.putInt("save_version", Economy.SAVE_VERSION)
    putDouble(e, "nip_balance", s.nipBalance)
    putDouble(e, "lifetime_nip", s.totalLifetimeNip)
    e.putInt("boss_level", s.bossLevel)
    e.putLong("street_cred_xp", s.streetCredXp)
    e.putInt("zoomies_charges", s.zoomiesCharges)
    e.putInt("prestige_lives", s.prestigeLives)
    e.putBoolean("music_enabled", s.musicEnabled)
    e.putLong("last_seen", System.currentTimeMillis() / 1000L)
    s.operatives.forEach { op ->
      e.putInt("op_owned_${op.id}", op.owned)
      if (op.id == "the_plug") e.putBoolean("op_locked_the_plug", op.isLocked)
    }
    s.districts.forEach { d ->
      if (d.id != "the_alley") {
        e.putBoolean("dist_claimed_${d.id}", d.status == DistrictStatus.ACTIVE)
      }
    }
    s.productFormulas.forEach { f -> e.putInt("form_lvl_${f.id}", f.level) }
    s.labTechs.forEach { t -> e.putBoolean("tech_${t.id}", t.isInstalled) }
    e.apply()
  }

  private fun grantOfflineEarnings() {
    val lastSeen = getLongSafe("last_seen", 0L)
    if (lastSeen <= 0L) return
    val now = System.currentTimeMillis() / 1000L
    val elapsed = (now - lastSeen).coerceIn(0L, Economy.OFFLINE_CAP_S)
    if (elapsed < 60L) return // ignore trivial gaps
    val s = _state.value
    val rate = calculatePassiveRate(s)
    if (rate <= 0.0) return
    val gained = rate * elapsed
    val hrs = elapsed / 3600
    val mins = (elapsed % 3600) / 60
    _state.update {
      it.copy(
        nipBalance = it.nipBalance + gained,
        totalLifetimeNip = it.totalLifetimeNip + gained,
        toastMessage = "🌙 WELCOME BACK BOSS! +${formatShort(gained)} NIP (${hrs}h ${mins}m offline)"
      )
    }
    saveState(_state.value)
  }

  // ------------------------------ economy -----------------------------------

  private fun prestigeMult(s: CartelGameState): Double =
    (1.0 + Economy.PRESTIGE_PER_LIFE).pow(s.prestigeLives)

  private fun territoryMult(s: CartelGameState): Double =
    s.districts
      .filter { it.status == DistrictStatus.DOMINATED || it.status == DistrictStatus.ACTIVE }
      .maxOfOrNull { it.multiplier } ?: 1.0

  private fun tapProductMult(s: CartelGameState): Long {
    var m = 1L
    s.productFormulas.forEach { f -> if (f.level > 0) m *= f.tapPowerMultiplier }
    return m
  }

  fun calculatePassiveRate(currentState: CartelGameState): Double {
    var baseRate = 0.0
    currentState.operatives.forEach { op -> baseRate += op.owned * op.baseProduction }

    var multiplier = territoryMult(currentState) * prestigeMult(currentState)
    if (currentState.isBoomboxBoostActive) multiplier *= 2.0
    if (currentState.isZoomiesActive) multiplier *= Economy.ZOOMIES_MULT
    if (currentState.isOverdriveActive) multiplier *= 5.0

    // Persian Accountant: +10% all earnings while at least one is owned
    if ((currentState.operatives.find { it.id == "persian_accountant" }?.owned ?: 0) > 0) {
      multiplier *= 1.10
    }
    // Hydroponic grow lamps: +15% idle
    if (currentState.labTechs.find { it.id == "grow_lamps" }?.isInstalled == true) {
      multiplier *= 1.15
    }
    return baseRate * multiplier
  }

  fun calculateTapPower(currentState: CartelGameState): Long {
    var power = Economy.TAP_BASE * tapProductMult(currentState)
    var mult = prestigeMult(currentState)
    if (currentState.isZoomiesActive) mult *= Economy.ZOOMIES_MULT
    if (currentState.isOverdriveActive) mult *= 5.0
    if ((currentState.operatives.find { it.id == "persian_accountant" }?.owned ?: 0) > 0) mult *= 1.10
    power = (power * mult).roundToLong()
    return power
  }

  fun onAvatarTapped(): Long {
    val current = _state.value
    val tapGain = calculateTapPower(current)
    val grinder = current.labTechs.find { it.id == "golden_grinder" }?.isInstalled == true
    val finalGain = if (grinder && Random.nextFloat() < 0.5f) tapGain * 2 else tapGain
    _state.update {
      it.copy(
        nipBalance = it.nipBalance + finalGain,
        totalLifetimeNip = it.totalLifetimeNip + finalGain
      )
    }
    return finalGain
  }

  // ------------------------------ zoomies -----------------------------------

  fun triggerZoomies() {
    val current = _state.value
    if (current.zoomiesCharges > 0 && !current.isZoomiesActive) {
      _state.update {
        it.copy(
          zoomiesCharges = it.zoomiesCharges - 1,
          isZoomiesActive = true,
          zoomiesRemainingSeconds = Economy.ZOOMIES_DURATION_S,
          zoomiesRechargeSeconds = Economy.ZOOMIES_RECHARGE_S,
          toastMessage = "🔥 3X MIDNIGHT ZOOMIES ACTIVATED! (30s)"
        )
      }
    }
  }

  fun triggerOverdrive() {
    val current = _state.value
    if (!current.isOverdriveActive && current.overdriveCooldownSeconds == 0) {
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
    val current = _state.value
    if (current.adDropCooldownSeconds != 0) return
    _state.update {
      it.copy(
        nipBalance = it.nipBalance + 50000.0,
        totalLifetimeNip = it.totalLifetimeNip + 50000.0,
        adDropCooldownSeconds = 300,
        toastMessage = "🎁 AIRDROP CRATE: +50,000 NIP DELIVERED!"
      )
    }
    saveState(_state.value)
  }

  fun setBuyMultiplier(mult: Int) {
    _state.update { it.copy(buyMultiplier = mult) }
  }

  fun setMusicEnabled(enabled: Boolean) {
    _state.update { it.copy(musicEnabled = enabled) }
    saveState(_state.value)
  }

  // ------------------------------ meta --------------------------------------

  /** Wipe the save and start a brand-new run. */
  fun resetGame() {
    prefs.edit().clear().putInt("save_version", Economy.SAVE_VERSION).apply()
    _state.value = CartelGameState(
      operatives = defaultOperatives(),
      districts = defaultDistricts(),
      productFormulas = defaultFormulas(),
      labTechs = defaultTechs(),
      nextRaidInSeconds = Random.nextDouble(Economy.RAID_MIN_DELAY_S, Economy.RAID_MAX_DELAY_S),
      toastMessage = "✨ FRESH GAME STARTED! WELCOME TO THE STREETS!"
    )
  }

  fun activateBoomboxBoost() {
    val current = _state.value
    if (!current.isBoomboxBoostActive && current.boomboxCooldownSeconds == 0) {
      _state.update {
        it.copy(
          isBoomboxBoostActive = true,
          boomboxRemainingSeconds = 1800, // 30 minutes
          toastMessage = "📻 2X BOOMBOX BASSLINE BOOST ACTIVATED!"
        )
      }
    }
  }

  private fun getBossTitle(level: Int): String = when {
    level >= 50 -> "UNDERWORLD KINGPIN"
    level >= 40 -> "OG BOSS"
    level >= 25 -> "SYNDICATE CAPO"
    level >= 15 -> "ALLEY ENFORCER"
    level >= 5 -> "CORNER RUNNER"
    else -> "STREET ROOKIE"
  }

  private fun calculateBossLevel(lifetimeNip: Double): Int =
    (1 + sqrt(lifetimeNip / 30.0)).toInt().coerceIn(1, 100)

  // ------------------------------ crew --------------------------------------

  fun hireOperative(opId: String, amount: Int) {
    val current = _state.value
    val op = current.operatives.find { it.id == opId } ?: return
    if (op.isLocked) {
      _state.update { it.copy(toastMessage = "🔒 ${op.name} IS STILL LOCKED!") }
      return
    }
    val count = if (amount >= 9999) Economy.maxAffordable(op.baseCost, op.owned, current.nipBalance)
                else amount
    if (count <= 0) {
      _state.update { it.copy(toastMessage = "NOT ENOUGH NIP TO HIRE!") }
      return
    }
    val totalCost = Economy.bulkCost(op.baseCost, op.owned, count).toDouble()
    if (current.nipBalance < totalCost) {
      _state.update { it.copy(toastMessage = "NOT ENOUGH NIP TO HIRE!") }
      return
    }
    val updatedOps = current.operatives.map {
      if (it.id == opId) it.copy(owned = it.owned + count, level = it.level + count) else it
    }
    _state.update {
      it.copy(
        nipBalance = it.nipBalance - totalCost,
        operatives = updatedOps,
        toastMessage = "RECRUITED +$count ${op.name}!"
      )
    }
    saveState(_state.value)
  }

  fun unlockContract(opId: String) {
    val current = _state.value
    val op = current.operatives.find { it.id == opId } ?: return
    if (!op.isLocked) return
    // The Plug unlocks after Downtown is claimed
    if (opId == "the_plug") {
      val downtown = current.districts.find { it.id == "downtown" }
      val claimed = downtown?.status == DistrictStatus.ACTIVE || downtown?.status == DistrictStatus.DOMINATED
      if (!claimed) {
        _state.update { it.copy(toastMessage = "🔒 CLAIM DOWNTOWN FIRST!") }
        return
      }
    }
    if (current.nipBalance >= op.unlockCost) {
      val updatedOps = current.operatives.map {
        if (it.id == opId) it.copy(isLocked = false, owned = 1, level = 1) else it
      }
      _state.update {
        it.copy(
          nipBalance = it.nipBalance - op.unlockCost,
          operatives = updatedOps,
          toastMessage = "CONTRACT SIGNED: ${op.name} IS ONLINE!"
        )
      }
      saveState(_state.value)
    } else {
      _state.update { it.copy(toastMessage = "REQUIRES ${formatShort(op.unlockCost.toDouble())} NIP TO UNLOCK!") }
    }
  }

  // ------------------------------ turf --------------------------------------

  fun claimDistrict(districtId: String) {
    val current = _state.value
    val district = current.districts.find { it.id == districtId } ?: return
    if (district.status == DistrictStatus.DOMINATED || district.status == DistrictStatus.ACTIVE) return
    if (current.nipBalance >= district.unlockCost) {
      val claimedIndex = current.districts.indexOfFirst { it.id == districtId }
      val updatedDistricts = current.districts.mapIndexed { idx, d ->
        when {
          d.id == districtId -> d.copy(status = DistrictStatus.ACTIVE)
          idx == claimedIndex + 1 &&
            (d.status == DistrictStatus.LOCKED || d.status == DistrictStatus.LEGENDARY_LOCKED) ->
            d.copy(status = DistrictStatus.READY_TO_EXPAND)
          else -> d
        }
      }
      _state.update {
        it.copy(
          nipBalance = it.nipBalance - district.unlockCost,
          districts = updatedDistricts,
          toastMessage = "TURF EXPANDED: ${district.name} DOMINATED! EARNINGS x${district.multiplier.toInt()}"
        )
      }
      // Claiming Downtown unlocks The Plug's contract
      saveState(_state.value)
    } else {
      _state.update { it.copy(toastMessage = "NOT ENOUGH NIP TO CLAIM TURF!") }
    }
  }

  // ------------------------------ labs --------------------------------------

  fun upgradeFormula(formulaId: String) {
    val current = _state.value
    val form = current.productFormulas.find { it.id == formulaId } ?: return
    if (form.level >= form.maxLevel) return
    if (current.nipBalance >= form.upgradeCost) {
      val updatedFormulas = current.productFormulas.map {
        if (it.id == formulaId) it.copy(level = it.level + 1, isLocked = false) else it
      }
      _state.update {
        it.copy(
          nipBalance = it.nipBalance - form.upgradeCost,
          productFormulas = updatedFormulas,
          toastMessage = "${form.name} INSTALLED! TAP POWER x${form.tapPowerMultiplier}"
        )
      }
      saveState(_state.value)
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
      saveState(_state.value)
    }
  }

  // ------------------------------ prestige ----------------------------------

  fun canPrestige(s: CartelGameState = _state.value): Boolean =
    s.totalLifetimeNip >= Economy.PRESTIGE_LIFETIME_REQ &&
      s.prestigeLives < Economy.PRESTIGE_MAX_LIVES

  /** Nine Lives prestige: reset the run for a permanent +25% per life. */
  fun doPrestige() {
    val current = _state.value
    if (!canPrestige(current)) {
      val need = Economy.PRESTIGE_LIFETIME_REQ - current.totalLifetimeNip
      val msg = if (current.prestigeLives >= Economy.PRESTIGE_MAX_LIVES)
        "🐱 MAX NINE LIVES REACHED — YOU ARE IMMORTAL!"
      else
        "🐱 NEED ${formatShort(need.coerceAtLeast(0.0))} MORE LIFETIME NIP FOR NINE LIVES!"
      _state.update { it.copy(toastMessage = msg) }
      return
    }
    val newLives = current.prestigeLives + 1
    _state.update {
      it.copy(
        nipBalance = 0.0,
        totalLifetimeNip = 0.0,
        zoomiesCharges = 1,
        isZoomiesActive = false,
        zoomiesRemainingSeconds = 0,
        zoomiesRechargeSeconds = Economy.ZOOMIES_RECHARGE_S,
        isOverdriveActive = false,
        overdriveRemainingSeconds = 0,
        overdriveCooldownSeconds = 0,
        isBoomboxBoostActive = false,
        boomboxRemainingSeconds = 0,
        boomboxCooldownSeconds = 0,
        adDropCooldownSeconds = 0,
        operatives = defaultOperatives(),
        districts = defaultDistricts(),
        productFormulas = defaultFormulas(),
        labTechs = defaultTechs(),
        prestigeLives = newLives,
        isRaidActive = false,
        raidFinished = false,
        nextRaidInSeconds = Random.nextDouble(Economy.RAID_MIN_DELAY_S, Economy.RAID_MAX_DELAY_S),
        toastMessage = "🐱 NINE LIVES: LIFE $newLives/9 — PERMANENT +${(newLives * 25)}% EARNINGS!"
      )
    }
    saveState(_state.value)
  }

  // ------------------------------ raids -------------------------------------

  fun triggerRaid() {
    val current = _state.value
    if (current.isRaidActive) return
    val hasSiamese = (current.operatives.find { it.id == "siamese_lookout" }?.owned ?: 0) > 0
    _state.update {
      it.copy(
        isRaidActive = true,
        raidTimerSeconds = if (hasSiamese) Economy.RAID_WARN_SIAM_ESE_S else Economy.RAID_WARN_S,
        stashedSpots = emptySet(),
        raidFinished = false,
        raidWon = false,
        toastMessage = "🚨 ANIMAL CONTROL RAID! STASH YOUR NIP!"
      )
    }
  }

  fun stashSpot(spotName: String) {
    val current = _state.value
    if (!current.isRaidActive || current.raidFinished) return
    val newStashed = current.stashedSpots + spotName
    val allSpots = setOf("Litter Box", "Couch Cushions", "Dark Closet", "Under Bed", "Backyard Dumpster")
    val won = newStashed.containsAll(allSpots)
    var bonus = 0.0
    var xp = 0L
    if (won) {
      bonus = current.nipBalance * Economy.RAID_WIN_BONUS
      xp = 2500L
    }
    _state.update {
      it.copy(
        stashedSpots = newStashed,
        raidFinished = won,
        raidWon = won,
        isRaidActive = !won,
        nipBalance = it.nipBalance + bonus,
        totalLifetimeNip = it.totalLifetimeNip + bonus,
        streetCredXp = it.streetCredXp + xp,
        toastMessage = if (won) "🏆 STASH SECURED! +5% NIP & 2500 XP!" else "HIDDEN IN $spotName!"
      )
    }
    if (won) saveState(_state.value)
  }

  private fun resolveRaidTimeout(current: CartelGameState): CartelGameState {
    val hasMuscle = (current.operatives.find { it.id == "maine_coon_muscle" }?.owned ?: 0) > 0
    val lossRate = if (hasMuscle) Economy.RAID_FAIL_LOSS_MUSCLE else Economy.RAID_FAIL_LOSS
    val lost = current.nipBalance * lossRate
    return current.copy(
      nipBalance = current.nipBalance - lost,
      isRaidActive = false,
      raidFinished = true,
      raidWon = false,
      nextRaidInSeconds = Random.nextDouble(Economy.RAID_MIN_DELAY_S, Economy.RAID_MAX_DELAY_S),
      toastMessage = "💸 RAID FAILED! LOST ${formatShort(lost)} NIP (${(lossRate * 100).toInt()}%)"
    )
  }

  fun dismissRaid() {
    _state.update { it.copy(isRaidActive = false, raidFinished = false) }
  }

  fun clearToast() {
    _state.update { it.copy(toastMessage = null) }
  }

  // ------------------------------ idle engine -------------------------------

  private fun startIdleEngine() {
    coroutineScope.launch {
      while (isActive) {
        delay(100) // 10 ticks per second for smooth idle counter
        tickCount++
        val wholeSecond = tickCount % 10 == 0
        _state.update { current ->
          val passiveRate = calculatePassiveRate(current)
          val increment = passiveRate / 10.0
          var next = current.copy(
            nipBalance = current.nipBalance + increment,
            totalLifetimeNip = current.totalLifetimeNip + increment
          )

          if (wholeSecond) {
            // --- per-second timers ---
            if (next.isZoomiesActive) {
              val left = next.zoomiesRemainingSeconds - 1
              next = next.copy(
                isZoomiesActive = left > 0,
                zoomiesRemainingSeconds = left.coerceAtLeast(0)
              )
            } else if (next.zoomiesCharges < Economy.ZOOMIES_MAX_CHARGES) {
              val recharge = next.zoomiesRechargeSeconds - 1
              if (recharge <= 0) {
                next = next.copy(
                  zoomiesCharges = next.zoomiesCharges + 1,
                  zoomiesRechargeSeconds = Economy.ZOOMIES_RECHARGE_S,
                  toastMessage = "🔥 MIDNIGHT ZOOMIES RECHARGED!"
                )
              } else {
                next = next.copy(zoomiesRechargeSeconds = recharge)
              }
            }

            if (next.isOverdriveActive) {
              val left = next.overdriveRemainingSeconds - 1
              next = next.copy(
                isOverdriveActive = left > 0,
                overdriveRemainingSeconds = left.coerceAtLeast(0),
                overdriveCooldownSeconds = if (left <= 0) 120 else next.overdriveCooldownSeconds
              )
            } else if (next.overdriveCooldownSeconds > 0) {
              next = next.copy(
                overdriveCooldownSeconds = (next.overdriveCooldownSeconds - 1).coerceAtLeast(0)
              )
            }
            if (next.isBoomboxBoostActive) {
              val left = next.boomboxRemainingSeconds - 1
              next = next.copy(
                isBoomboxBoostActive = left > 0,
                boomboxRemainingSeconds = left.coerceAtLeast(0),
                boomboxCooldownSeconds = if (left <= 0) 300 else next.boomboxCooldownSeconds
              )
            } else if (next.boomboxCooldownSeconds > 0) {
              next = next.copy(
                boomboxCooldownSeconds = (next.boomboxCooldownSeconds - 1).coerceAtLeast(0)
              )
            }
            if (next.adDropCooldownSeconds > 0) {
              next = next.copy(
                adDropCooldownSeconds = (next.adDropCooldownSeconds - 1).coerceAtLeast(0)
              )
            }

            // --- raid scheduler (every 8-15 min, -20% frequency with scent baggies) ---
            if (!next.isRaidActive) {
              var delay = next.nextRaidInSeconds - 1.0
              if (next.labTechs.find { it.id == "scent_baggies" }?.isInstalled == true) {
                delay -= 0.25 // -20% frequency ~= stretches the countdown
              }
              if (delay <= 0) {
                val hasSiamese = (next.operatives.find { it.id == "siamese_lookout" }?.owned ?: 0) > 0
                next = next.copy(
                  isRaidActive = true,
                  raidTimerSeconds = if (hasSiamese) Economy.RAID_WARN_SIAM_ESE_S else Economy.RAID_WARN_S,
                  stashedSpots = emptySet(),
                  raidFinished = false,
                  raidWon = false,
                  nextRaidInSeconds = Random.nextDouble(Economy.RAID_MIN_DELAY_S, Economy.RAID_MAX_DELAY_S),
                  toastMessage = "🚨 ANIMAL CONTROL RAID! STASH YOUR NIP!"
                )
              } else {
                next = next.copy(nextRaidInSeconds = delay)
              }
            }

            // --- boss progression from lifetime earnings ---
            val newLvl = calculateBossLevel(next.totalLifetimeNip)
            if (newLvl != next.bossLevel) {
              next = next.copy(bossLevel = newLvl, bossTitle = getBossTitle(newLvl))
            }
          }

          // --- active raid countdown (fine-grained) ---
          if (next.isRaidActive && !next.raidFinished) {
            val timer = next.raidTimerSeconds - 0.1
            next = if (timer <= 0.0) {
              resolveRaidTimeout(next.copy(raidTimerSeconds = 0.0))
            } else {
              next.copy(raidTimerSeconds = timer)
            }
          }

          next
        }
        if (tickCount % 50 == 0) saveState(_state.value) // autosave every 5s
      }
    }
  }

  companion object {
    fun formatShort(v: Double): String {
      val abs = kotlin.math.abs(v)
      return when {
        abs >= 1e12 -> String.format("%.2fT", v / 1e12)
        abs >= 1e9 -> String.format("%.2fB", v / 1e9)
        abs >= 1e6 -> String.format("%.2fM", v / 1e6)
        abs >= 1e3 -> String.format("%.2fK", v / 1e3)
        else -> String.format("%.0f", v)
      }
    }
  }
}
