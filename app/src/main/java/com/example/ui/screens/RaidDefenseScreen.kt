package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoorSliding
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CartelGameState
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OnError
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSecondary
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SecondaryGoldContainer
import com.example.ui.theme.SirenRed
import com.example.ui.theme.SurfaceBright
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import java.util.Locale

@Composable
fun RaidDefenseScreen(
  state: CartelGameState,
  onStashSpot: (String) -> Unit,
  onBack: () -> Unit,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  val totalSpots = 5
  val stashedCount = state.stashedSpots.size
  val isWon = state.raidWon
  val isFinished = state.raidFinished
  val timerSeconds = state.raidTimerSeconds.coerceAtLeast(0.0)

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("raid_defense_screen")
  ) {
    // Top Bar with Back Arrow
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding(),
      color = SurfaceContainerLowest.copy(alpha = 0.95f),
      shadowElevation = 8.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("raid_back_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = PrimaryNeon
          )
        }
        AsyncImage(
          model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBAyKogrEuUlTFpMAWkqNuOXvrKtgoWb07EHMOHBtH4Lnwax0ZvwiGSR0SGxS4ijM8YnYglYK4CKVHW53lQDvHuJd7z8NXpaMkmMAv70LApxOoYcgxlTvNP-OVC-uLfjt-9nK8o1UBWr0fr07_9h8WXTVs0OUYJ3kQCruNgPLLVw-2xCtRYxO-V59xGgsNMObHfp0MF6W-XeXxtVR7-5borLwAbA-zIPGFgGjFk2aGuQCTx8J4r1HW8KQ",
          contentDescription = "Logo",
          modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "RAID DEFENSE",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          ),
          color = MaterialTheme.colorScheme.primary
        )
      }
    }

    // Police Siren Striped Tape Header
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = SirenRed,
      shadowElevation = 4.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CRITICAL BUST IN PROGRESS",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
            color = Color.White
          )
        }
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = Color.White
        ) {
          Text(
            text = "CODE 9 PAWS",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            color = SirenRed,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Headline & Subtitle
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = ErrorContainer.copy(alpha = 0.8f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.LocalPolice,
              contentDescription = null,
              tint = ErrorRed,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "SIRENS SCREECHING OUTSIDE",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = ErrorRed,
              fontSize = 10.sp
            )
          }
        }

        Text(
          text = "🚨 ANIMAL CONTROL IS CIRCLING! 🚨",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
          ),
          color = SirenRed,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Hide the illegal stash before the canine squad kicks the alley door down!",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Center,
          fontSize = 13.sp
        )
      }

      // Countdown Timer Hub
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("raid_countdown_hub"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHighest),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = SecondaryGold,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = String.format(Locale.US, "00:%04.1f", timerSeconds),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = if (timerSeconds < 3.0) SirenRed else SecondaryGold
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "LEFT",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 10.sp
              )
            }

            Surface(
              shape = RoundedCornerShape(4.dp),
              color = SurfaceContainerLowest
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Visibility,
                  contentDescription = null,
                  tint = PrimaryNeon,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "SIAMESE LOOKOUT (+5S)",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = PrimaryNeon,
                  fontSize = 9.sp
                )
              }
            }
          }

          // Progress bar
          val progressFraction = (timerSeconds / 12.4).toFloat().coerceIn(0f, 1f)
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(RoundedCornerShape(5.dp))
              .background(SurfaceContainerLowest)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(progressFraction)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(
                  Brush.horizontalGradient(
                    colors = listOf(SecondaryGold, SirenRed)
                  )
                )
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row {
              Text(
                text = "STASHED: ",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
              Text(
                text = "$stashedCount/$totalSpots",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = PrimaryNeon,
                fontSize = 11.sp
              )
            }
            Text(
              text = if (stashedCount == totalSpots) "PERIMETER SECURED" else "SUSPICION HIGH",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = if (stashedCount == totalSpots) PrimaryNeon else SirenRed,
              fontSize = 11.sp
            )
          }
        }
      }

      // Interactive Hiding Grid
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Spot 1: Litter Box
          StashSpotCard(
            name = "LITTER BOX",
            icon = Icons.Default.Inventory2,
            isStashed = state.stashedSpots.contains("Litter Box"),
            onStash = { onStashSpot("Litter Box") },
            modifier = Modifier.weight(1f)
          )
          // Spot 2: Couch Cushions
          StashSpotCard(
            name = "COUCH CUSHIONS",
            icon = Icons.Default.Weekend,
            isStashed = state.stashedSpots.contains("Couch Cushions"),
            onStash = { onStashSpot("Couch Cushions") },
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Spot 3: Dark Closet
          StashSpotCard(
            name = "DARK CLOSET",
            icon = Icons.Default.DoorSliding,
            isStashed = state.stashedSpots.contains("Dark Closet"),
            onStash = { onStashSpot("Dark Closet") },
            modifier = Modifier.weight(1f)
          )
          // Spot 4: Under Bed
          StashSpotCard(
            name = "UNDER BED",
            icon = Icons.Default.Bed,
            isStashed = state.stashedSpots.contains("Under Bed"),
            onStash = { onStashSpot("Under Bed") },
            modifier = Modifier.weight(1f)
          )
        }

        // Spot 5: Backyard Dumpster (Span 2)
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clickable(enabled = !state.stashedSpots.contains("Backyard Dumpster") && !isFinished) {
              onStashSpot("Backyard Dumpster")
            }
            .testTag("stash_spot_backyard_dumpster"),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (state.stashedSpots.contains("Backyard Dumpster")) SurfaceContainerLow else SurfaceContainer
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxSize()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "BACKYARD DUMPSTER",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
              )
              Text(
                text = "Stinky alley bins. Feds hate rummaging here!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }

            if (state.stashedSpots.contains("Backyard Dumpster")) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = PrimaryNeon.copy(alpha = 0.2f)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = PrimaryNeon,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "STASHED! (Safe ✅)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = PrimaryNeon,
                    fontSize = 10.sp
                  )
                }
              }
            } else {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = SurfaceBright
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "TAP TO HIDE!",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 10.sp
                  )
                }
              }
            }
          }
        }
      }

      // Consequence & Muscle Breakdown Tracker
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Gavel,
              contentDescription = null,
              tint = ErrorRed,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "RAID PENALTY BREAKDOWN",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = ErrorRed,
              fontSize = 10.sp
            )
          }

          Text(
            text = "Fail Penalty: Lose 15% Catnip (-192,738 Nip)",
            style = MaterialTheme.typography.bodySmall.copy(
              textDecoration = TextDecoration.LineThrough
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )

          Surface(
            shape = RoundedCornerShape(4.dp),
            color = SurfaceContainerHighest
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = SecondaryGold,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "REDUCED TO 7% BY MAINE COON MUSCLE!",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = SecondaryGold,
                fontSize = 9.sp
              )
            }
          }
        }
      }

      // Tactical Scenario Outcome Card
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = "TACTICAL SCENARIO OUTCOME",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 10.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Success state card
          Card(
            modifier = Modifier
              .weight(1f)
              .testTag("preview_success_card"),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isWon) SurfaceContainerHigh else SurfaceContainerHighest.copy(alpha = 0.7f)
            ),
            border = if (isWon) androidx.compose.foundation.BorderStroke(2.dp, PrimaryNeon) else null
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.MilitaryTech,
                  contentDescription = null,
                  tint = PrimaryNeon,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "SUCCESS STATE",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = PrimaryNeon,
                  fontSize = 9.sp
                )
              }
              Text(
                text = "Stash Secured!",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
              )
              Text(
                text = "+15% Street Cred XP",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = PrimaryNeon,
                fontSize = 10.sp
              )
            }
          }

          // Busted state card
          Card(
            modifier = Modifier
              .weight(1f)
              .testTag("preview_busted_card"),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isFinished && !isWon) SurfaceContainerHigh else SurfaceContainerHighest.copy(alpha = 0.5f)
            ),
            border = if (isFinished && !isWon) androidx.compose.foundation.BorderStroke(2.dp, SirenRed) else null
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = null,
                  tint = SirenRed,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "BUSTED STATE",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = SirenRed,
                  fontSize = 9.sp
                )
              }
              Text(
                text = "Door Kicked In!",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
              )
              Text(
                text = "-89,944 Nip Seized",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = SirenRed,
                fontSize = 10.sp
              )
            }
          }
        }
      }

      // Finish / Victory / Retry banner
      if (isFinished) {
        Spacer(modifier = Modifier.height(6.dp))
        Button(
          onClick = {
            if (isWon) onBack() else onRetry()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("raid_finish_action_btn"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isWon) PrimaryNeon else SecondaryGold,
            contentColor = if (isWon) OnPrimaryContainer else OnSecondary
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
          Text(
            text = if (isWon) "VICTORY! RETURN TO EMPIRE" else "RETRY DEFENSE DRILL",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            fontSize = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun StashSpotCard(
  name: String,
  icon: ImageVector,
  isStashed: Boolean,
  onStash: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .height(130.dp)
      .clickable(enabled = !isStashed) { onStash() }
      .testTag("stash_spot_${name.lowercase().replace(" ", "_")}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isStashed) SurfaceContainerLow else SurfaceContainer
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)
    ) {
      // Big background watermarked icon
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isStashed) PrimaryNeon.copy(alpha = 0.12f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
        modifier = Modifier
          .size(72.dp)
          .align(Alignment.BottomEnd)
      )

      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Text(
            text = name,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
            color = if (isStashed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp
          )
          if (isStashed) {
            Icon(
              imageVector = Icons.Default.Verified,
              contentDescription = "Safe",
              tint = PrimaryNeon,
              modifier = Modifier.size(16.dp)
            )
          } else {
            Icon(
              imageVector = Icons.Default.PriorityHigh,
              contentDescription = "Urgent",
              tint = SecondaryGold,
              modifier = Modifier.size(15.dp)
            )
          }
        }

        if (isStashed) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = PrimaryNeon.copy(alpha = 0.2f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = PrimaryNeon,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "STASHED! (Safe ✅)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = PrimaryNeon,
                fontSize = 9.sp
              )
            }
          }
        } else {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = SecondaryGoldContainer
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.TouchApp,
                contentDescription = null,
                tint = OnSecondary,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = "TAP TO HIDE!",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = OnSecondary,
                fontSize = 9.sp
              )
            }
          }
        }
      }
    }
  }
}
