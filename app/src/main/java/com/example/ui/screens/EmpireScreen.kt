package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CartelGameState
import com.example.ui.components.formatNumber
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSecondary
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SecondaryGoldContainer
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import java.util.Locale

@Composable
fun EmpireScreen(
  state: CartelGameState,
  passiveRate: Double,
  tapPower: Long,
  musicEnabled: Boolean,
  onToggleMusic: (Boolean) -> Unit,
  onPrestigeLaunder: () -> Unit,
  onResetGame: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()
  var hapticEnabled by remember { mutableStateOf(true) }
  var showResetDialog by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("empire_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Boss Profile Hero Header
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("boss_profile_card"),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(74.dp)
            .clip(CircleShape)
            .background(SurfaceContainerLowest)
        ) {
          AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAUdI4DX0Fgpcf17ipgusF0Dhj2MN75Jf0x4lmZVtslCgHPv2kk_0-0im9H2Ogf3Jn09TXasw_KbPqryuGAtIfNdtAnipqZMQVgFDkiX_Tm3FSyNVGM0WLLCMG48MlDVrcid_VKPXIkg23KLHGJ6mIvDfSnoHlPJbgC4nzBoqNzk_leKZrypxljrzFh6pdB684m8Wned8htxvWzKX8cMRyLbHpdXzVZ_IGlTrCMR8Ekk_PGdfCwnoXDdA",
            contentDescription = "OG MC Whiskers",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = SecondaryGold
          ) {
            Text(
              text = "OG BOSS LVL ${state.bossLevel}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = OnSecondary,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Text(
            text = "MC WHISKERS",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 2.dp)
          )

          Text(
            text = "Undisputed Kingpin of Sector 9",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }
      }
    }

    // Syndicate Lifetime Metrics
    Text(
      text = "SYNDICATE DOSSIER",
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp
      ),
      color = SecondaryGold,
      fontSize = 11.sp
    )

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricRow(
          icon = Icons.Default.Eco,
          label = "Total Lifetime Nip Harvested",
          value = "${formatNumber(state.totalLifetimeNip)} NIP",
          valueColor = PrimaryNeon
        )
        MetricRow(
          icon = Icons.Default.MilitaryTech,
          label = "Street Cred XP",
          value = "${formatNumber(state.streetCredXp.toDouble())} XP",
          valueColor = SecondaryGold
        )
        MetricRow(
          icon = Icons.Default.Bolt,
          label = "Active Passive Syndicate Rate",
          value = String.format(Locale.US, "+%,.1f Nip/s", passiveRate),
          valueColor = PrimaryNeon
        )
        MetricRow(
          icon = Icons.Default.Groups,
          label = "Crew Operatives Recruited",
          value = "${state.operatives.sumOf { it.owned }} Cats",
          valueColor = MaterialTheme.colorScheme.primary
        )
        MetricRow(
          icon = Icons.Default.FormatPaint,
          label = "Territories Dominated",
          value = "3 / 6 Districts",
          valueColor = SecondaryGold
        )
      }
    }

    // Syndicate Laundering (Prestige)
    Text(
      text = "UNDERWORLD LAUNDERING",
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp
      ),
      color = SecondaryGold,
      fontSize = 11.sp
    )

    Card(
      modifier = Modifier.fillMaxWidth(),
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
              imageVector = Icons.Default.Paid,
              contentDescription = null,
              tint = SecondaryGold,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "LAUNDER STASH (PRESTIGE)",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
              color = MaterialTheme.colorScheme.primary
            )
          }

          Surface(
            shape = RoundedCornerShape(4.dp),
            color = PrimaryNeon.copy(alpha = 0.2f)
          ) {
            Text(
              text = "NINE LIVES: ${state.prestigeLives}/9",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = PrimaryNeon,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Text(
          text = "Nine Lives prestige: at 1,000,000 lifetime nip, reset your empire for a permanent +25% earnings per life — up to 9 lives.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )

        Button(
          onClick = onPrestigeLaunder,
          modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .testTag("launder_stash_btn"),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = SecondaryGold,
            contentColor = OnSecondary
          )
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = OnSecondary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = when {
                state.prestigeLives >= 9 -> "MAX NINE LIVES — IMMORTAL"
                state.totalLifetimeNip >= 1000000.0 -> "LAUNDER & PRESTIGE (LIFE ${state.prestigeLives + 1}/9)"
                else -> "LAUNDER & PRESTIGE (1M LIFETIME REQ.)",
              },
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
              fontSize = 12.sp
            )
          }
        }
      }
    }

    // Audio & Haptics Toggles
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
              imageVector = Icons.Default.MusicNote,
              contentDescription = null,
              tint = SecondaryGold,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Theme Music (Catnip Cartel)",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary,
              fontSize = 13.sp
            )
          }
          Switch(
            checked = musicEnabled,
            onCheckedChange = { onToggleMusic(it) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = PrimaryNeon,
              checkedTrackColor = OnPrimaryContainer
            )
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Vibration,
              contentDescription = null,
              tint = PrimaryNeon,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Tactile Alley Haptics",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary,
              fontSize = 13.sp
            )
          }
          Switch(
            checked = hapticEnabled,
            onCheckedChange = { hapticEnabled = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = PrimaryNeon,
              checkedTrackColor = OnPrimaryContainer
            )
          )
        }
      }
    }

    // Reset Game / Fresh Start Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "GAME DATA MANAGEMENT",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        )
        Text(
          text = "Want to restart your syndicate journey? Reset your game progress to Level 1 with 0 NIP.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp
        )
        OutlinedButton(
          onClick = { showResetDialog = true },
          modifier = Modifier.fillMaxWidth().testTag("reset_game_button"),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DeleteForever,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "START FRESH GAME (RESET ALL)",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    }

    if (showResetDialog) {
      AlertDialog(
        onDismissRequest = { showResetDialog = false },
        title = {
          Text(
            text = "Reset Game to Fresh Start?",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        },
        text = {
          Text(
            text = "This will wipe all catnip, operative hires, and territory progress. You will start as a Level 1 Street Rookie with 0 NIP. Are you sure?",
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        },
        confirmButton = {
          Button(
            onClick = {
              showResetDialog = false
              onResetGame()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
          ) {
            Text("Yes, Reset Game", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          TextButton(onClick = { showResetDialog = false }) {
            Text("Cancel")
          }
        },
        containerColor = SurfaceContainerHigh
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
fun MetricRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  value: String,
  valueColor: androidx.compose.ui.graphics.Color
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = valueColor,
        modifier = Modifier.size(15.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp
      )
    }
    Text(
      text = value,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Bold,
      color = valueColor,
      fontSize = 12.sp
    )
  }
}
