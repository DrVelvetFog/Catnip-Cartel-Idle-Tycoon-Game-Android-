package com.example.data

data class Operative(
  val id: String,
  val name: String,
  val description: String,
  val baseCost: Long,
  val baseProduction: Double,
  val owned: Int,
  val level: Int,
  val stars: Int,
  val imageUrl: String,
  val perkText: String? = null,
  val isLocked: Boolean = false,
  val unlockRequirementText: String? = null,
  val unlockCost: Long = 0L
)

data class District(
  val id: String,
  val name: String,
  val description: String,
  val multiplier: Double,
  val multiplierLabel: String,
  val nipPerSec: Double,
  val status: DistrictStatus,
  val unlockCost: Long = 0L,
  val previewImageUrl: String? = null,
  val takeoverEstimate: String? = null,
  val perkLabel: String? = null
)

enum class DistrictStatus {
  DOMINATED,
  ACTIVE,
  READY_TO_EXPAND,
  LOCKED,
  LEGENDARY_LOCKED
}

data class ProductFormula(
  val id: String,
  val name: String,
  val description: String,
  val grade: String,
  val level: Int,
  val maxLevel: Int,
  val tapPowerMultiplier: Int,
  val baseBonus: Int,
  val upgradeCost: Long,
  val imageUrl: String,
  val isLocked: Boolean = false,
  val isLegendary: Boolean = false
)

data class LabTech(
  val id: String,
  val name: String,
  val bonusText: String,
  val cost: Long,
  val iconName: String,
  val isInstalled: Boolean,
  val isWarning: Boolean = false
)

data class FloatingParticle(
  val id: Long,
  val text: String,
  val x: Float,
  val y: Float
)
