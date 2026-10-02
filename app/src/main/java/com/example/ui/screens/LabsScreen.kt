package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CartelGameState
import com.example.data.LabTech
import com.example.data.ProductFormula
import com.example.ui.components.formatShortNumber
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSecondary
import com.example.ui.theme.Outline
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SecondaryGoldContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun LabsScreen(
  state: CartelGameState,
  tapPower: Long,
  onUpgradeFormula: (String) -> Unit,
  onInstallTech: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("labs_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Lab Metric & Status Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("secret_batch_lab_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(SurfaceContainerLowest),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Biotech,
                  contentDescription = null,
                  tint = PrimaryNeon,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "SECRET BATCH LAB",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = PrimaryNeon
            ) {
              Text(
                text = "BATCH ACTIVE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = OnPrimaryContainer,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          // Efficiency & Purity Card
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = SurfaceContainerLowest.copy(alpha = 0.85f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "TAP YIELD EFFICIENCY",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Outline,
                  fontSize = 9.sp
                )
                Row(
                  verticalAlignment = Alignment.Bottom,
                  modifier = Modifier.padding(top = 2.dp)
                ) {
                  Text(
                    text = "$tapPower",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = PrimaryNeon
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "NIP / TAP",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "SYNDICATE PURITY",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = SecondaryGold,
                  fontSize = 9.sp
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(top = 2.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = SecondaryGold,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "98.4%",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = SecondaryGold
                  )
                }
              }
            }
          }
        }
      }
    }

    // Product Formulas Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = null,
            tint = SecondaryGold,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "PRODUCT FORMULAS",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp
          )
        }
        Text(
          text = "3 FORMULATIONS",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = Outline,
          fontSize = 10.sp
        )
      }
    }

    // Formulas Cards
    items(state.productFormulas, key = { it.id }) { formula ->
      FormulaCard(
        formula = formula,
        currentNipBalance = state.nipBalance,
        onUpgrade = { onUpgradeFormula(formula.id) }
      )
    }

    // Lab Tech Header
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.PrecisionManufacturing,
            contentDescription = null,
            tint = PrimaryNeon,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "LAB APPARATUS & TECH",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp
          )
        }
        Text(
          text = "MODS",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = Outline,
          fontSize = 10.sp
        )
      }
    }

    // Tech Items
    items(state.labTechs, key = { it.id }) { tech ->
      TechCard(
        tech = tech,
        currentNipBalance = state.nipBalance,
        onInstall = { onInstallTech(tech.id) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun FormulaCard(
  formula: ProductFormula,
  currentNipBalance: Double,
  onUpgrade: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("formula_card_${formula.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.Top
        ) {
          // Thumbnail with Grade Tag
          Box(
            modifier = Modifier
              .size(76.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceContainerLowest)
          ) {
            AsyncImage(
              model = formula.imageUrl,
              contentDescription = formula.name,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
            Surface(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp),
              shape = RoundedCornerShape(3.dp),
              color = if (formula.isLegendary) SecondaryGoldContainer else SurfaceContainerLowest.copy(alpha = 0.9f)
            ) {
              Text(
                text = formula.grade,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = if (formula.isLegendary) OnSecondary else PrimaryNeon,
                fontSize = 8.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }

          // Info Column
          Column(modifier = Modifier.weight(1f)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = formula.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = if (formula.isLegendary) SecondaryGold else MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              Surface(
                shape = RoundedCornerShape(4.dp),
                color = SurfaceContainerHigh
              ) {
                Text(
                  text = "LVL ${formula.level}/${formula.maxLevel}",
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = SecondaryGold,
                  fontSize = 10.sp,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = formula.description,
              style = MaterialTheme.typography.bodySmall,
              color = Outline,
              fontSize = 11.sp,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.padding(top = 2.dp)
            )

            Row(
              modifier = Modifier.padding(top = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (formula.isLegendary) SecondaryGold.copy(alpha = 0.2f) else PrimaryNeon.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "x${formula.tapPowerMultiplier} TAP POWER",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = if (formula.isLegendary) SecondaryGold else PrimaryNeon,
                  fontSize = 9.sp,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
              Text(
                text = "One-time purchase",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            }
          }
        }

        // Segmented Level Bar (10 steps)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(SurfaceContainerLowest)
            .padding(1.dp),
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          repeat(formula.maxLevel) { idx ->
            val filled = idx < formula.level
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .clip(RoundedCornerShape(2.dp))
                .background(
                  if (filled) {
                    if (formula.isLegendary) SecondaryGold else PrimaryNeon
                  } else SurfaceContainerHigh
                )
            )
          }
        }

        // Action Button
        Spacer(modifier = Modifier.height(10.dp))
        val isLegendaryUnlock = formula.isLegendary && formula.level == 0
        Button(
          onClick = onUpgrade,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .testTag("upgrade_formula_${formula.id}"),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isLegendaryUnlock) SecondaryGold else PrimaryNeon,
            contentColor = if (isLegendaryUnlock) OnSecondary else OnPrimaryContainer
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (isLegendaryUnlock) Icons.Default.LockOpen else Icons.Default.Upgrade,
                contentDescription = null,
                tint = if (isLegendaryUnlock) OnSecondary else OnPrimaryContainer,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isLegendaryUnlock) "UNLOCK LAB" else "UPGRADE BATCH",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = if (isLegendaryUnlock) OnSecondary else OnPrimaryContainer,
                fontSize = 13.sp
              )
            }

            Text(
              text = "${formatShortNumber(formula.upgradeCost)} NIP",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              color = if (isLegendaryUnlock) OnSecondary else OnPrimaryContainer,
              fontSize = 13.sp
            )
          }
        }
      }

      // Top-right Boss Tier Badge if Legendary
      if (formula.isLegendary) {
        Surface(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp),
          shape = RoundedCornerShape(4.dp),
          color = SecondaryGold
        ) {
          Text(
            text = "BOSS TIER",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
            color = OnSecondary,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}

@Composable
fun TechCard(
  tech: LabTech,
  currentNipBalance: Double,
  onInstall: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("tech_card_${tech.id}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainerHigh),
          contentAlignment = Alignment.Center
        ) {
          val icon = when (tech.iconName) {
            "lightbulb" -> Icons.Default.Lightbulb
            "local_police" -> Icons.Default.LocalPolice
            else -> Icons.Default.Star
          }
          val iconTint = when {
            tech.isWarning -> ErrorRed
            tech.iconName == "star" -> SecondaryGold
            else -> PrimaryNeon
          }
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = tech.name,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp
          )
          Text(
            text = tech.bonusText,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = if (tech.isWarning) ErrorRed else if (tech.iconName == "star") SecondaryGold else PrimaryNeon,
            fontSize = 11.sp
          )
        }
      }

      // Install or Online Button
      if (!tech.isInstalled) {
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onInstall() }
            .testTag("install_tech_${tech.id}"),
          shape = RoundedCornerShape(6.dp),
          color = SurfaceContainerHighest
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.End
          ) {
            Text(
              text = "INSTALL",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Outline,
              fontSize = 9.sp
            )
            Text(
              text = "${formatShortNumber(tech.cost)} NIP",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = if (tech.iconName == "star") SecondaryGold else MaterialTheme.colorScheme.primary,
              fontSize = 11.sp
            )
          }
        }
      } else {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = PrimaryNeon.copy(alpha = 0.2f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = PrimaryNeon,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "ONLINE",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = PrimaryNeon,
              fontSize = 10.sp
            )
          }
        }
      }
    }
  }
}
