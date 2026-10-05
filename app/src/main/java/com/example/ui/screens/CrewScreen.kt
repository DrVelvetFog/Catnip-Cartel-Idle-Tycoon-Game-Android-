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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
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
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToLong
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CartelGameState
import com.example.data.Economy
import com.example.data.Operative
import com.example.ui.components.formatShortNumber
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSecondaryContainer
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.SecondaryFixedDim
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SecondaryGoldContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import java.util.Locale

@Composable
fun CrewScreen(
  state: CartelGameState,
  passiveRate: Double,
  onHire: (String, Int) -> Unit,
  onUnlockContract: (String) -> Unit,
  onSelectMultiplier: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val multipliers = listOf(1, 10, 100, 9999) // 9999 = MAX

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("crew_screen_list"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Roster Overview Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("crew_roster_overview_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = SecondaryGold,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "CATNIP SYNDICATE CREW",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = SecondaryGold,
                letterSpacing = 1.sp
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${state.operatives.size} OPERATIVES",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              ),
              color = MaterialTheme.colorScheme.primary
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = SurfaceContainerLowest.copy(alpha = 0.85f)
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              horizontalAlignment = Alignment.End
            ) {
              Text(
                text = "TOTAL PASSIVE RATE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Bolt,
                  contentDescription = null,
                  tint = PrimaryNeon,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                  text = String.format(Locale.US, "+%,.1f", passiveRate),
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  fontSize = 15.sp,
                  color = PrimaryNeon
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                  text = "Nip/s",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary,
                  fontSize = 11.sp
                )
              }
            }
          }
        }
      }
    }

    // Buy Multiplier Selector
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceContainerLowest,
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "BUY MULTIPLIER:",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.8.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            multipliers.forEach { mult ->
              val isSelected = state.buyMultiplier == mult
              val label = if (mult == 9999) "MAX" else "${mult}x"
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .clickable { onSelectMultiplier(mult) }
                  .testTag("multiplier_btn_$label"),
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) PrimaryNeon else SurfaceContainer
              ) {
                Text(
                  text = label,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp,
                  color = if (isSelected) OnPrimaryContainer else if (mult == 9999) SecondaryGold else MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
              }
            }
          }
        }
      }
    }

    // Operatives List
    items(state.operatives, key = { it.id }) { op ->
      OperativeCard(
        operative = op,
        buyMultiplier = state.buyMultiplier,
        currentNipBalance = state.nipBalance,
        onHire = { amount -> onHire(op.id, amount) },
        onUnlockContract = { onUnlockContract(op.id) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun OperativeCard(
  operative: Operative,
  buyMultiplier: Int,
  currentNipBalance: Double,
  onHire: (Int) -> Unit,
  onUnlockContract: () -> Unit
) {
  // Geometric pricing: next unit costs base * 1.6^owned (matches GameRepository)
  val growth = 1.6
  val unitCost = (operative.baseCost * growth.pow(operative.owned)).roundToLong()
  fun bulkPrice(n: Int): Long =
    (unitCost * (growth.pow(n) - 1.0) / (growth - 1.0)).roundToLong().coerceAtLeast(0L)
  val multiplierCount = if (buyMultiplier == 9999) {
    // MAX: largest n with bulkPrice(n) <= balance
    if (currentNipBalance < unitCost) 0
    else floor(ln(1.0 + currentNipBalance * (growth - 1.0) / unitCost) / ln(growth)).toInt().coerceAtLeast(0)
  } else {
    buyMultiplier
  }

  val bulkCost = bulkPrice(10)
  val multCost = bulkPrice(multiplierCount)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("operative_card_${operative.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (operative.isLocked) SurfaceContainerLowest else SurfaceContainerLow
    ),
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
          // Avatar with level badge
          Box(
            modifier = Modifier
              .size(66.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceContainerHighest)
          ) {
            AsyncImage(
              model = operative.imageUrl,
              contentDescription = operative.name,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .fillMaxSize()
                .alpha(if (operative.isLocked) 0.35f else 1f)
            )
            Surface(
              modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
              color = SurfaceContainerLowest.copy(alpha = 0.9f)
            ) {
              Text(
                text = "LVL ${operative.level}",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = PrimaryNeon,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(vertical = 1.dp)
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
                text = operative.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = if (operative.isLocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              // Star rating
              Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                repeat(4) { index ->
                  Icon(
                    imageVector = if (index < operative.stars) Icons.Default.Star else Icons.Default.StarOutline,
                    contentDescription = null,
                    tint = if (index < operative.stars) SecondaryGold else SurfaceContainerHighest,
                    modifier = Modifier.size(13.dp)
                  )
                }
              }
            }

            Text(
              text = operative.description,
              style = MaterialTheme.typography.bodySmall,
              color = if (operative.perkText != null) SecondaryFixedDim else MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.padding(top = 2.dp)
            )

            val milestones = Economy.milestonesReached(operative.owned)
            val milestoneMult = 2.0.pow(milestones).roundToLong()
            val nextMilestone = Economy.nextMilestone(operative.owned)
            val prevMilestone = Economy.prevMilestone(operative.owned)
            val milestoneProgress = ((operative.owned - prevMilestone).toFloat() /
              (nextMilestone - prevMilestone).coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

            Row(
              modifier = Modifier.padding(top = 5.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = SurfaceContainerHigh
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = PrimaryNeon,
                    modifier = Modifier.size(11.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = String.format(Locale.US, "+%.1f Nip/s", operative.baseProduction * milestoneMult),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = PrimaryNeon
                  )
                }
              }

              if (milestoneMult > 1L) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = SecondaryGold.copy(alpha = 0.2f)
                ) {
                  Text(
                    text = "x$milestoneMult PRODUCTION",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    color = SecondaryGold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Text(
                text = "Owned: ${operative.owned}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }

            // Milestone Progress Row + Bar
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Next x2 at $nextMilestone",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = SecondaryGold,
                  fontSize = 10.sp
                )
                Text(
                  text = "${operative.owned} / $nextMilestone",
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 10.sp
                )
              }
              Spacer(modifier = Modifier.height(3.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(5.dp)
                  .clip(RoundedCornerShape(3.dp))
                  .background(SurfaceContainerHighest)
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth(milestoneProgress)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SecondaryGold)
                )
              }
            }
          }
        }

        // Action Buttons Row
        Spacer(modifier = Modifier.height(10.dp))
        if (!operative.isLocked) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Secondary +10 Button
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onHire(10) }
                .testTag("hire_10_${operative.id}"),
              shape = RoundedCornerShape(8.dp),
              color = SurfaceContainerHigh
            ) {
              Text(
                text = "+10 (${formatShortNumber(bulkCost)})",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
              )
            }

            // Primary HIRE +N Button
            val isGoldButton = operative.id == "persian_accountant" || operative.id == "maine_coon_muscle"
            val buttonContainerColor = if (isGoldButton) SecondaryGoldContainer else PrimaryNeon
            val buttonTextColor = if (isGoldButton) OnSecondaryContainer else OnPrimaryContainer

            Button(
              onClick = { onHire(multiplierCount) },
              modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .testTag("hire_btn_${operative.id}"),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = buttonContainerColor,
                contentColor = buttonTextColor
              ),
              elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Text(
                  text = "HIRE +$multiplierCount",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                  color = buttonTextColor,
                  fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Default.Eco,
                  contentDescription = null,
                  tint = buttonTextColor,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                  text = formatShortNumber(multCost),
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = buttonTextColor
                )
              }
            }
          }
        } else {
          // Locked Tier Action
          Button(
            onClick = onUnlockContract,
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp)
              .testTag("unlock_contract_${operative.id}"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = SurfaceContainerHigh,
              contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = SecondaryGold,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "UNLOCK CONTRACT",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = SecondaryGold,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = formatShortNumber(operative.unlockCost),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = SecondaryGold
              )
            }
          }
        }
      }

      // Locked Banner Overlay Tag
      if (operative.isLocked) {
        Surface(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(10.dp),
          shape = RoundedCornerShape(4.dp),
          color = ErrorContainer.copy(alpha = 0.85f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = ErrorRed,
              modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = operative.unlockRequirementText ?: "LOCKED",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = ErrorRed,
              fontSize = 9.sp
            )
          }
        }
      }
    }
  }
}
