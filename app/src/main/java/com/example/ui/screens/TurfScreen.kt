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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CartelGameState
import com.example.data.District
import com.example.data.DistrictStatus
import com.example.ui.components.formatShortNumber
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSecondary
import com.example.ui.theme.Outline
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
fun TurfScreen(
  state: CartelGameState,
  onClaimDistrict: (String) -> Unit,
  onLaunchRaidDefense: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("turf_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Command & Multiplier Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("turf_control_banner"),
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
          verticalAlignment = Alignment.Top
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = null,
                tint = PrimaryNeon,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "TACTICAL TURF OPS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = PrimaryNeon,
                letterSpacing = 1.sp
              )
            }
            Text(
              text = "TURF CONTROL",
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(top = 2.dp)
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "OVERALL HEAT",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = SecondaryGold,
              fontSize = 9.sp
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = SurfaceContainerLowest,
              modifier = Modifier.padding(top = 2.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.LocalFireDepartment,
                  contentDescription = null,
                  tint = SecondaryGold,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "LOW LEVEL",
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = SecondaryGold,
                  fontSize = 10.sp
                )
              }
            }
          }
        }

        // Active Global Multiplier Pill
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = SurfaceContainerLowest,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(PrimaryNeon),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Bolt,
                  contentDescription = null,
                  tint = OnPrimaryContainer,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "SYNTHETIC NIP STASH",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 9.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Global Multiplier: ",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                  )
                  Text(
                    text = "x4.0 ACTIVE",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                    color = PrimaryNeon,
                    fontSize = 12.sp
                  )
                }
              }
            }

            Text(
              text = "+300% BOOST",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              color = PrimaryNeon,
              fontSize = 12.sp
            )
          }
        }

        // Territory Progress Bar & Defense Score
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Districts Card
          Surface(
            modifier = Modifier.weight(1f),
            color = SurfaceContainerLowest.copy(alpha = 0.8f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "DISTRICTS",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 9.sp
                )
                Text(
                  text = "3 / 6",
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = PrimaryNeon,
                  fontSize = 11.sp
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp))
                  .background(SurfaceContainerHigh)
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(PrimaryNeon)
                )
              }
              Text(
                text = "50% Alley Domination",
                style = MaterialTheme.typography.labelSmall,
                color = Outline,
                fontSize = 9.sp,
                modifier = Modifier.padding(top = 3.dp)
              )
            }
          }

          // Defense Rating Card
          Surface(
            modifier = Modifier.weight(1f),
            color = SurfaceContainerLowest.copy(alpha = 0.8f),
            shape = RoundedCornerShape(8.dp)
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "DEFENSE RATING",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 9.sp
                )
                Text(
                  text = "88%",
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = SecondaryGold,
                  fontSize = 11.sp
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp))
                  .background(SurfaceContainerHigh)
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SecondaryGold)
                )
              }
              Text(
                text = "Razor Wire & Stray Claws",
                style = MaterialTheme.typography.labelSmall,
                color = Outline,
                fontSize = 9.sp,
                modifier = Modifier.padding(top = 3.dp)
              )
            }
          }
        }
      }
    }

    // City Sector Tactical Map Strip
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(130.dp)
        .testTag("tactical_map_strip"),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
          model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBUltJnjejEH4cQHl08kV3dURVuFKEeBB8qknb327WkfA7KZcK5bvtdx5PkSkMfxCFituVqZWDV9dsqe7hF5R2_UL21y3pLPwTg__Mq6RlIc2-X32tUkxl4tXa1jnF9xJQcTFJWVC0En_p-lcWfVxA3Z06riib595BdQF4D-aMd6TdPPVXIHuhkW__-oCHL6c-_Bd8hD-ja9GeiAcMVhKMi02vw_7bNPQ0p7VlYWMTt-csAYm2eOoaX4g",
          contentDescription = "Tactical Aerial Map",
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxSize()
            .alpha(0.75f)
        )

        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
          color = SurfaceContainerLowest.copy(alpha = 0.85f)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(PrimaryNeon)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "LIVE SURVEILLANCE FEED",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp
              )
            }
            Text(
              text = "SECTOR 9-FELINE",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = SecondaryGold,
              fontSize = 10.sp
            )
          }
        }
      }
    }

    // Districts Interactive Pipeline
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      state.districts.forEach { district ->
        DistrictCard(
          district = district,
          currentNipBalance = state.nipBalance,
          onClaim = { onClaimDistrict(district.id) }
        )
      }
    }

    // Raid Defense Posture Hub
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("raid_defense_posture_card"),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = SecondaryGold,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "RAID DEFENSE POSTURE",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
              color = MaterialTheme.colorScheme.primary,
              fontSize = 14.sp
            )
          }
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = SurfaceContainerHigh
          ) {
            Text(
              text = "OPTIMAL",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = PrimaryNeon,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }

        Text(
          text = "Militant sewer rats and hostile street tomcats test your perimeter every 60 minutes. Station enforcers to prevent passive nip siphon.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            color = SurfaceContainerLowest
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "SENTRY ENFORCERS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp
              )
              Text(
                text = "12 BRAWLERS",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
              )
            }
          }

          Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            color = SurfaceContainerLowest
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "INTERCEPT SUCCESS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp
              )
              Text(
                text = "88.4% SAFE",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = SecondaryGold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
              )
            }
          }
        }

        // Test Raid Drill Button
        Button(
          onClick = onLaunchRaidDefense,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .testTag("launch_raid_defense_btn"),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = PrimaryNeon,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "RUN DEFENSE DRILL (ANIMAL CONTROL BUST)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
              color = MaterialTheme.colorScheme.primary,
              fontSize = 12.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
fun DistrictCard(
  district: District,
  currentNipBalance: Double,
  onClaim: () -> Unit
) {
  val isDominated = district.status == DistrictStatus.DOMINATED
  val isActive = district.status == DistrictStatus.ACTIVE
  val isReady = district.status == DistrictStatus.READY_TO_EXPAND
  val isLocked = district.status == DistrictStatus.LOCKED || district.status == DistrictStatus.LEGENDARY_LOCKED

  val cardContainerColor = when {
    isReady -> SurfaceContainerHigh
    isActive -> SurfaceContainerHigh
    isDominated -> SurfaceContainer
    else -> SurfaceContainerLow.copy(alpha = 0.65f)
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("district_row_${district.id}"),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.Top
  ) {
    // Left Status Icon Box
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(SurfaceContainerLowest),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(
            if (isActive) PrimaryNeon else SurfaceContainerHigh
          ),
        contentAlignment = Alignment.Center
      ) {
        val icon = when {
          isDominated -> Icons.Default.CheckCircle
          isActive -> Icons.Default.Radar
          isReady -> Icons.Default.AddBusiness
          district.status == DistrictStatus.LEGENDARY_LOCKED -> Icons.Default.MilitaryTech
          else -> Icons.Default.Lock
        }
        val iconTint = when {
          isActive -> OnPrimaryContainer
          isDominated -> PrimaryNeon
          isReady -> SecondaryGold
          else -> Outline
        }
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // Right Content Card
    Card(
      modifier = Modifier.weight(1f),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = cardContainerColor),
      elevation = CardDefaults.cardElevation(defaultElevation = if (isReady) 6.dp else 3.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column {
            val statusLabel = when (district.status) {
              DistrictStatus.DOMINATED -> "DOMINATED"
              DistrictStatus.ACTIVE -> "ACTIVE TURF"
              DistrictStatus.READY_TO_EXPAND -> "READY TO EXPAND"
              DistrictStatus.LOCKED -> "LOCKED DISTRICT"
              DistrictStatus.LEGENDARY_LOCKED -> "LEGENDARY APEX"
            }
            val statusColor = when (district.status) {
              DistrictStatus.DOMINATED, DistrictStatus.ACTIVE -> PrimaryNeon
              DistrictStatus.READY_TO_EXPAND -> SecondaryGold
              else -> Outline
            }

            Text(
              text = statusLabel,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = statusColor,
              fontSize = 9.sp,
              letterSpacing = 1.sp
            )
            Text(
              text = district.name,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
              color = if (isReady) SecondaryGold else if (isLocked) Outline else MaterialTheme.colorScheme.primary,
              fontSize = 15.sp,
              modifier = Modifier.padding(top = 1.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = SurfaceContainerLowest
          ) {
            Text(
              text = district.multiplierLabel,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = if (isLocked) Outline else if (isReady) SecondaryGold else PrimaryNeon,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Text(
          text = district.description,
          style = MaterialTheme.typography.bodySmall,
          color = if (isLocked) Outline else MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp,
          modifier = Modifier.padding(top = 4.dp)
        )

        // Suburbs preview asset if ready to expand
        if (district.previewImageUrl != null && district.status == DistrictStatus.READY_TO_EXPAND) {
          Spacer(modifier = Modifier.height(8.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(86.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceContainerLowest)
          ) {
            AsyncImage(
              model = district.previewImageUrl,
              contentDescription = "District preview",
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .fillMaxSize()
                .alpha(0.65f)
            )
            Surface(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(6.dp),
              color = SurfaceContainerLowest.copy(alpha = 0.9f),
              shape = RoundedCornerShape(4.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = SecondaryGold,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "ESTIMATED TAKEOVER: ${district.takeoverEstimate ?: "12M"}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary,
                  fontSize = 9.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = onClaim,
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp)
              .testTag("claim_turf_${district.id}"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = SecondaryGold,
              contentColor = OnSecondary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LockOpen,
                  contentDescription = null,
                  tint = OnSecondary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "CLAIM TURF",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                  color = OnSecondary,
                  fontSize = 13.sp
                )
              }
              Text(
                text = "${formatShortNumber(district.unlockCost)} NIP",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = OnSecondary,
                fontSize = 13.sp
              )
            }
          }
        } else if (district.perkLabel != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (district.id == "the_rooftops") Icons.Default.FlightTakeoff else Icons.Default.Shield,
                contentDescription = null,
                tint = PrimaryNeon,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = district.perkLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = PrimaryNeon,
                fontSize = 10.sp
              )
            }
            Text(
              text = "${district.multiplierLabel} EARNINGS",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              fontSize = 11.sp
            )
          }
        } else if (isLocked) {
          Spacer(modifier = Modifier.height(6.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = SurfaceContainerLowest.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "REQUIREMENT",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Outline,
                fontSize = 9.sp
              )
              Text(
                text = "${formatShortNumber(district.unlockCost)} NIP",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Outline,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }
  }
}
